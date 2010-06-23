/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     McGill University - initial API and implementation
 *******************************************************************************/
/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.model;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.QualyzerException;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;
import ca.mcgill.cs.swevo.qualyzer.util.FileUtil;
import ca.mcgill.cs.swevo.qualyzer.util.HibernateUtil;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public final class ModelFacade
{
	/**
	 * 
	 */
	private static final String TRANSCRIPTS = "transcripts";

	private static final String AUDIO_PATH = null;

	private static ModelFacade gFacade = null;
	
	private ListenerManager fManager;

	private ModelFacade()
	{
		fManager = new ListenerManager();
	}
	
	/**
	 * Get the Facade.
	 * @return
	 */
	public static ModelFacade getInstance()
	{
		if(gFacade == null)
		{
			gFacade = new ModelFacade();
		}
		
		return gFacade;
	}
	
	/**
	 * Create a new Project with the given name.
	 * @param name
	 * @return
	 */
	public Project createProject(String name, String nickname, String fullName, String institution)
		throws QualyzerException
	{
		if(!validateProject(name, nickname, fullName, institution))
		{
			throw new QualyzerException(); //TODO
		}
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject wProject = root.getProject(name);
		Project project;
		
		try
		{
			wProject.create(new NullProgressMonitor());
			wProject.open(new NullProgressMonitor());
			
			if(!makeSubFolders(wProject))
			{
				cleanUpFolders(wProject);
				throw new QualyzerException("Unable to create the required file system.");
			}
			
			project = new Project();
			project.setName(name);
			
			createInvestigator(nickname, fullName, institution, project, false);
			
			PersistenceManager.getInstance().initDB(wProject);
			HibernateDBManager manager;
			manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(name);
			HibernateUtil.quietSave(manager, project);
			root.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		}
		catch(CoreException e)
		{
			e.printStackTrace();
			throw new QualyzerException("There was a problem creating the project", e);
		}
		
		return project;		
	}
	
	/**
	 * Checks that a project's fields are all valid.
	 * @param name
	 * @param nickname
	 * @param fullName
	 * @param institution
	 * @return
	 */
	public boolean validateProject(String name, String nickname, String fullName, String institution)
	{
		return ResourcesUtil.verifyID(name) && ResourcesUtil.verifyID(nickname);
	}

	/**
	 * Create an investigator from the given information.
	 * @param nickname
	 * @param fullName
	 * @param institution
	 * @param project
	 * @return
	 */
	public Investigator createInvestigator(String nickname, String fullName, String institution, 
			Project project, boolean save) throws QualyzerException
	{	
		Investigator investigator = new Investigator();
		investigator.setNickName(nickname);
		investigator.setFullName(fullName);
		investigator.setInstitution(institution);
		investigator.setProject(project);
		
		project.getInvestigators().add(investigator);
		
		if(save)
		{
			HibernateDBManager manager;
			manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(project.getName());
			HibernateUtil.quietSave(manager, project);
		}
		return investigator;
	}

	/**
	 * @param wProject
	 */
	private void cleanUpFolders(IProject wProject)
	{
		String path = wProject.getLocation().toOSString();
		File dir = new File(path+File.separator+"audio"); //$NON-NLS-1$
		if(!dir.exists())
		{
			dir.delete();
		}
		dir = new File(path+File.separator+TRANSCRIPTS);
		if(!dir.exists())
		{
			dir.delete();
		}
		dir = new File(path+File.separator+"memos"); //$NON-NLS-1$
		if(!dir.exists())
		{
			dir.delete();
		}
		
	}

	/**
	 * @param wProject
	 * @return
	 */
	private boolean makeSubFolders(IProject wProject)
	{
		String path = wProject.getLocation().toOSString();
		File dir = new File(path+File.separator+"audio"); //$NON-NLS-1$
		if(!dir.mkdir())
		{
			return false;
		}
		dir = new File(path+File.separator+TRANSCRIPTS); 
		if(!dir.mkdir())
		{
			return false;
		}
		dir = new File(path+File.separator+"memos"); //$NON-NLS-1$
		return dir.mkdir();
	}

	/**
	 * @param participantId
	 * @param fullname
	 * @param fProject
	 * @return
	 */
	public Participant createParticipant(String participantId, String fullName, Project project) 
		throws QualyzerException
	{
		if(!validateParticipant(participantId, fullName))
		{
			throw new QualyzerException(); //TODO
		}
		
		Participant participant = new Participant();
		 
		participant.setParticipantId(participantId);
		participant.setFullName(fullName);
		participant.setProject(project);
		project.getParticipants().add(participant);
		 
		HibernateDBManager manager;
		manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(project.getName());
		HibernateUtil.quietSave(manager, project);
	
		return participant;
	}
	
	/**
	 * Verifies that the participant info is valid.
	 * @param participantId
	 * @param fullName
	 * @return
	 */
	public boolean validateParticipant(String participantId, String fullName)
	{
		return ResourcesUtil.verifyID(participantId);
	}
	
	/**
	 * Create a transcript.
	 * @param name
	 * @param Date
	 * @param participants
	 * @param project
	 * @return
	 */
	public Transcript createTranscript(String name, String date, String audioFilePath, String existingTranscript,
			List<Participant> participants, Project project) throws QualyzerException
	{
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject wProject = root.getProject(project.getName());
		
		String workspacePath = wProject.getLocation().toString();
		
		Transcript transcript = new Transcript();
		transcript.setName(name);
		transcript.setFileName(name+".txt"); //$NON-NLS-1$
		transcript.setDate(date);
		transcript.setParticipants(participants);
		
		hookupAudioFile(audioFilePath, workspacePath, transcript);
		
		createTranscriptFile(existingTranscript, wProject, transcript);
		
		project.getTranscripts().add(transcript);
		transcript.setProject(project);

		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(project.getName());
		HibernateUtil.quietSave(manager, project);
		
		return transcript;
	}

	/**
	 * @param existingTranscript
	 * @param wProject
	 * @param transcript
	 */
	private void createTranscriptFile(String existingTranscript, IProject wProject, Transcript transcript)
	{
		String path = wProject.getLocation()+File.separator+TRANSCRIPTS+File.separator+transcript.getFileName();
		File file = new File(path);
		
		if(existingTranscript.isEmpty())
		{
			try
			{
				if(!file.createNewFile())
				{
					throw new QualyzerException(
							"Unable to create the transcript file. One probably already exists with that name.");
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
				throw new QualyzerException("There was an error creating the transcript file", e);
			}
		}
		else
		{
			File fileOrig = new File(existingTranscript);
			if(file.exists())
			{
				throw new QualyzerException("A transcript already exists with the requested name");
			}
			
			if(!fileOrig.exists())
			{
				throw new QualyzerException(
						"The requested transcript cannot be found. It may have been deleted or renamed.");
			}
			
			try
			{
				FileUtil.copyFile(fileOrig, file);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				throw new QualyzerException("There was a problem copying the transcript file", e);
			}
		}
	}

	/**
	 * @param audioFilePath
	 * @param workspacePath
	 * @param transcript
	 */
	private void hookupAudioFile(String audioFilePath, String workspacePath, Transcript transcript)
	{
		if(!audioFilePath.isEmpty())
		{
			//if the audio file is not in the workspace then copy it there.
			AudioFile audioFile = new AudioFile();
			int i = audioFilePath.lastIndexOf('.');
			
			String relativePath = transcript.getName()+audioFilePath.substring(i);
			
			if(audioFilePath.indexOf(workspacePath) == -1 || namesAreDifferent(transcript.getName(), audioFilePath))
			{
				if(!copyAudioFile(audioFilePath, relativePath, workspacePath))
				{
					throw new QualyzerException("Failed to copy the audio file");
				}

			}
			audioFile.setRelativePath(AUDIO_PATH+relativePath);
			transcript.setAudioFile(audioFile);
		}
	}
	
	private boolean namesAreDifferent(String name, String audioPath)
	{
		int i = audioPath.lastIndexOf(File.separatorChar) + 1;
		int j = audioPath.lastIndexOf('.');
		return !name.equals(audioPath.substring(i, j));
	}
	
	private boolean copyAudioFile(String audioPath, String relativePath, String workspacePath)
	{
		File file = new File(audioPath);
		File fileCpy = new File(workspacePath+AUDIO_PATH+relativePath);
		
		if(!file.exists())
		{
			throw new QualyzerException("The audio file you wish to copy cannot be found.");
		}
		
		try
		{
			FileUtil.copyFile(file, fileCpy);
			return true;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Get the Listener Manager.
	 * @return
	 */
	public ListenerManager getListenerManager()
	{
		return fManager;
	}
}
