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
import java.util.List;

import org.eclipse.core.resources.IProject;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.QualyzerException;
import ca.mcgill.cs.swevo.qualyzer.util.FileUtil;
import ca.mcgill.cs.swevo.qualyzer.util.HibernateUtil;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public final class Facade
{
	private static Facade gFacade = null;
	
	private ListenerManager fManager;

	private Facade()
	{
		fManager = new ListenerManager();
	}
	
	/**
	 * Get the Facade.
	 * @return
	 */
	public static Facade getInstance()
	{
		if(gFacade == null)
		{
			gFacade = new Facade();
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
		IProject wProject = FileUtil.makeProjectFileSystem(name);
		Project project;
		
		project = new Project();
		project.setName(name);
		
		createInvestigator(nickname, fullName, institution, project, false);
		
		PersistenceManager.getInstance().initDB(wProject);
		HibernateDBManager manager;
		manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(name);
		HibernateUtil.quietSave(manager, project);
		
		return project;		
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
	 * @param participantId
	 * @param fullname
	 * @param fProject
	 * @return
	 */
	public Participant createParticipant(String participantId, String fullName, Project project) 
		throws QualyzerException
	{	
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
	 * Create a transcript.
	 * @param name
	 * @param Date
	 * @param participants
	 * @param project
	 * @return
	 */
	public Transcript createTranscript(String name, String date, String audioFilePath,
			List<Participant> participants, Project project) throws QualyzerException
	{
		Transcript transcript = new Transcript();
		transcript.setName(name);
		transcript.setFileName(name+".txt"); //$NON-NLS-1$
		transcript.setDate(date);
		transcript.setParticipants(participants);
		
		String fileExt = "";
		if(!audioFilePath.isEmpty())
		{
			fileExt = audioFilePath.substring(audioFilePath.lastIndexOf('.'));
		}
	
		transcript.setAudioFile(createAudioFile(name, fileExt));
		
		project.getTranscripts().add(transcript);
		transcript.setProject(project);
		
		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(project.getName());
		HibernateUtil.quietSave(manager, project);
		
		return transcript;
	}
	
	/**
	 * Build the audio file for the specified transcript.
	 * @param transcriptName
	 * @param fileExt
	 * @return
	 */
	public AudioFile createAudioFile(String transcriptName, String fileExt)
	{
		if(fileExt.isEmpty())
		{
			return null;
		}
		
		AudioFile audioFile = new AudioFile();
		audioFile.setRelativePath(File.separator+"audio"+File.separator+transcriptName+fileExt);
		
		return audioFile;
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
