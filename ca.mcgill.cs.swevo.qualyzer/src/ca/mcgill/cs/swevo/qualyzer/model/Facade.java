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
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.QualyzerException;
import ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType;
import ca.mcgill.cs.swevo.qualyzer.util.FileUtil;
import ca.mcgill.cs.swevo.qualyzer.util.HibernateUtil;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public final class Facade
{
	private static Facade gFacade = null;
	
	private ListenerManager fListenerManager;

	private final Logger fLogger = LoggerFactory.getLogger(Facade.class);
	
	private Facade()
	{
		fListenerManager = new ListenerManager();
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
		
		fListenerManager.notifyProjectListeners(ChangeType.ADD, project, this);
		
		return project;		
	}

	/**
	 * Create a new code.
	 * @param codeName
	 * @param codeDescription
	 * @param project
	 * @return
	 */
	public Code createCode(String codeName, String codeDescription, Project project)
	{
		Code code = new Code();
		code.setCodeName(codeName);
		code.setDescription(codeDescription);
		code.setProject(project);
		project.getCodes().add(code);
		
		HibernateDBManager manager;
		manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(project.getName());
		HibernateUtil.quietSave(manager, project);
		
		fListenerManager.notifyCodeListeners(ChangeType.ADD, code, this);
		
		return code;
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
		
		fListenerManager.notifyInvestigatorListeners(ChangeType.ADD, investigator, this);
		
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
	
		fListenerManager.notifyParticipantListeners(ChangeType.ADD, participant, this);
		
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
		
		String fileExt = ""; //$NON-NLS-1$
		if(!audioFilePath.isEmpty())
		{
			fileExt = audioFilePath.substring(audioFilePath.lastIndexOf('.'));
		}
	
		transcript.setAudioFile(createAudioFile(name, fileExt));
		
		project.getTranscripts().add(transcript);
		transcript.setProject(project);
		
		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(project.getName());
		HibernateUtil.quietSave(manager, project);
		
		fListenerManager.notifyTranscriptListeners(ChangeType.ADD, transcript, this);
		
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
		audioFile.setRelativePath(File.separator+"audio"+File.separator+transcriptName+fileExt); //$NON-NLS-1$
		
		return audioFile;
	}
	
	/**
	 * Try to delete a project.
	 * @param project
	 */
	public void deleteProject(Project project)
	{
		IProject wProject = ResourcesPlugin.getWorkspace().getRoot().getProject(project.getName());
		
		try
		{	
			HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers()
				.get(project.getName());
			
			QualyzerActivator.getDefault().getHibernateDBManagers().remove(project.getName());
			manager.shutdownDBServer();
			manager.close();
			
			wProject.delete(true, true, new NullProgressMonitor());
		}
		catch(CoreException e)
		{
			String errorMessage = Messages.getString("model.Facade.cannotDelete"); //$NON-NLS-1$
			fLogger.error(errorMessage, e);
			throw new QualyzerException(errorMessage, e);
		}
		
		fListenerManager.notifyProjectListeners(ChangeType.DELETE, project, this);
	}
	
	/**
	 * Try to delete a participant.
	 * @param participant
	 */
	public void deleteParticipant(Participant participant)
	{
		Object project = null;
		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers()
			.get(participant.getProject().getName());
		Session session = null;
		Transaction t = null;
		
		try
		{
			session = manager.openSession();
			t = session.beginTransaction();
			
			/*The following is ALL required in order to delete the object from the database.
			 * Don't ask me why, I don't really understand it myself -JF.
			 */
			project = session.get(Project.class, participant.getProject().getPersistenceId());
			Object part = session.get(Participant.class, participant.getPersistenceId());
			
			((Project) project).getParticipants().remove(part);
			
			session.delete(part);
			session.flush();
			t.commit();
			
			fListenerManager.notifyParticipantListeners(ChangeType.DELETE, participant, this);
		}
		catch(HibernateException e)
		{
			HibernateUtil.quietRollback(t);
			String errorMessage = Messages.getString("model.Facade.participantDeleteFailed"); //$NON-NLS-1$
			fLogger.error(errorMessage, e);
			throw new QualyzerException(errorMessage, e);
		}
		finally
		{
			HibernateUtil.quietClose(session);
			HibernateUtil.quietSave(manager, project);
		}
	}
	
	/**
	 * Try to delete the investigator.
	 * @param investigator
	 */
	public void deleteInvestigator(Investigator investigator)
	{
		Object project = null;
		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers()
			.get(investigator.getProject().getName());
		Session session = null;
		Transaction t = null;
		try
		{
			session = manager.openSession();
			t = session.beginTransaction();
			
			/*The following is ALL required in order to delete the object from the database.
			 * Don't ask me why, I don't really understand it myself -JF.
			 */
			project = session.get(Project.class, investigator.getProject().getPersistenceId());
			Object inv = session.get(Investigator.class, investigator.getPersistenceId());
			
			((Project) project).getInvestigators().remove(inv);
			
			session.delete(inv);
			session.flush();
			t.commit();
			fListenerManager.notifyInvestigatorListeners(ChangeType.DELETE, investigator, this);
		}
		catch(HibernateException e)
		{
			HibernateUtil.quietRollback(t);
			String errorMessage = Messages.getString("model.Facade.invesDeleteFailed"); //$NON-NLS-1$
			fLogger.error(errorMessage, e);
			throw new QualyzerException(errorMessage, e);
		}
		finally
		{
			HibernateUtil.quietClose(session);
			HibernateUtil.quietSave(manager, project);
		}
	}
	
	/**
	 * Try to delete the transcript.
	 * @param transcript
	 */
	public void deleteTranscript(Transcript transcript)
	{
		Object project = null;
		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers()
			.get(transcript.getProject().getName());
		Session session = null;
		Transaction t = null;
		try
		{
			session = manager.openSession();
			t = session.beginTransaction();
			
			/*The following is ALL required in order to delete the object from the database.
			 * Don't ask me why, I don't really understand it myself -JF.
			 */
			project = session.get(Project.class, transcript.getProject().getPersistenceId());
			Object trans = session.get(Transcript.class, transcript.getPersistenceId());
			
			((Project) project).getTranscripts().remove(trans);
			
			session.delete(trans);
			session.flush();
			t.commit();
			fListenerManager.notifyTranscriptListeners(ChangeType.DELETE, transcript, this);
		}
		catch(HibernateException e)
		{
			HibernateUtil.quietRollback(t);
			String errorMessage = Messages.getString("model.Facade.transDeleteFailed"); //$NON-NLS-1$
			fLogger.error(errorMessage, e);
			throw new QualyzerException(errorMessage, e);
		}
		finally
		{
			HibernateUtil.quietClose(session);
			HibernateUtil.quietSave(manager, project);
		}
	}
	
	/**
	 * Force a Transcript to load all its fields.
	 * @param transcript
	 * @return
	 */
	public Transcript forceTranscriptLoad(Transcript transcript)
	{
		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers()
			.get(transcript.getProject().getName());
		Session s = manager.openSession();
		
		Object object = s.get(Transcript.class, transcript.getPersistenceId());
		Transcript toReturn = (Transcript) object;
		
		Hibernate.initialize(toReturn.getParticipants());
		
		Hibernate.initialize(toReturn.getFragments());
		
		HibernateUtil.quietClose(s);
		
		return toReturn;
	}
	
	/**
	 * Force a Memo to load all of its fields.
	 * @param memo
	 * @return
	 */
	public Memo forceMemoLoad(Memo memo)
	{
		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers()
			.get(memo.getProject().getName());
		Session s = manager.openSession();
		
		Object object = s.get(Memo.class, memo.getPersistenceId());
		Memo toReturn = (Memo) object;
		
		Hibernate.initialize(toReturn.getParticipants());
		
		Hibernate.initialize(toReturn.getFragments());
		
		HibernateUtil.quietClose(s);
		
		return toReturn;
	}
	
	/**
	 * Get the Listener Manager.
	 * @return
	 */
	public ListenerManager getListenerManager()
	{
		return fListenerManager;
	}
	
	/**
	 * Save a code.
	 * @param code
	 */
	public void saveCode(Code code)
	{
		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers()
			.get(code.getProject().getName());
		HibernateUtil.quietSave(manager, code);
		
		fListenerManager.notifyCodeListeners(ChangeType.MODIFY, code, this);
	}
	
	/**
	 * Save an investigator.
	 * @param investigator
	 */
	public void saveInvestigator(Investigator investigator)
	{
		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers()
			.get(investigator.getProject().getName());
		HibernateUtil.quietSave(manager, investigator);
		
		fListenerManager.notifyInvestigatorListeners(ChangeType.MODIFY, investigator, this);
	}
	
	/**
	 * Save a Participant.
	 * @param participant
	 */
	public void saveParticipant(Participant participant)
	{
		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers()
			.get(participant.getProject().getName());
		HibernateUtil.quietSave(manager, participant);
		
		fListenerManager.notifyParticipantListeners(ChangeType.MODIFY, participant, this);
	}
	
	/**
	 * Save a Transcript.
	 * @param transcript
	 */
	public void saveTranscript(Transcript transcript)
	{
		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers()
			.get(transcript.getProject().getName());
		HibernateUtil.quietSave(manager, transcript);
		
		fListenerManager.notifyTranscriptListeners(ChangeType.MODIFY, transcript, this);
	}
}
