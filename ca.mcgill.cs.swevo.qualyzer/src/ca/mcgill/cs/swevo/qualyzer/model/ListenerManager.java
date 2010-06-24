/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jonathan Faubert
 *******************************************************************************/
/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *	Keeps track of all the listeners to various change events in the model.
 *
 */
public class ListenerManager
{
	/**
	 * Defines the various reasons listeners can be notified.
	 */
	public enum ChangeType
	{ADD, DELETE, MODIFY}
	
	private HashMap<Project, ArrayList<ProjectListener>> fProjectListeners;
	private HashMap<Project, ArrayList<CodeListener>> fCodeListeners;
	private HashMap<Project, ArrayList<InvestigatorListener>> fInvestigatorListeners;
	private HashMap<Project, ArrayList<ParticipantListener>> fParticipantListeners;
	private HashMap<Project, ArrayList<TranscriptListener>> fTranscriptListeners;
	
	/**
	 * Constructor.
	 */
	public ListenerManager()
	{
		fProjectListeners = new HashMap<Project, ArrayList<ProjectListener>>();
	}
	
	/**
	 * Notify the registered ProjectListeners that the given Project has changed.
	 * @param cType
	 * @param project
	 * @param facade
	 */
	public void notifyProjectListeners(ChangeType cType, Project project, Facade facade)
	{
		for(ProjectListener listener : fProjectListeners.get(project))
		{
			listener.projectChanged(cType, project, facade);
		}
	}
	
	/**
	 * Notify all the CodeListeners that a Code has changed.
	 * @param cType
	 * @param project
	 * @param facade
	 */
	public void notifyCodeListeners(ChangeType cType, Code code, Facade facade)
	{
		for(CodeListener listener : fCodeListeners.get(code.getProject()))
		{
			listener.codeChanged(cType, code, facade);
		}
	}
	
	/**
	 * Notify the InvestigatorListeners that an Investigator has changed.
	 * @param cType
	 * @param investigator
	 * @param facade
	 */
	public void notifyInvestigatorListeners(ChangeType cType, Investigator investigator, Facade facade)
	{
		for(InvestigatorListener listener : fInvestigatorListeners.get(investigator.getProject()))
		{
			listener.investigatorChanged(cType, investigator, facade);
		}
	}
	
	/**
	 * Notify the ParticipantListeners that a Participant has changed.
	 * @param cType
	 * @param participant
	 * @param facade
	 */
	public void notifyParticipantListeners(ChangeType cType, Participant participant, Facade facade)
	{
		for(ParticipantListener listener : fParticipantListeners.get(participant.getProject()))
		{
			listener.participantChanged(cType, participant, facade);
		}
	}
	
	/**
	 * Notify the TranscriptListeners that a Transcript has changed.
	 * @param cType
	 * @param transcript
	 * @param facade
	 */
	public void notifyTranscriptListeners(ChangeType cType, Transcript transcript, Facade facade)
	{
		for(TranscriptListener listener : fTranscriptListeners.get(transcript.getProject()))
		{
			listener.transcriptChanged(cType, transcript, facade);
		}
	}
	
	/**
	 * Register a CodeListener with the specified Project.
	 * @param project
	 * @param listener
	 */
	public void registerCodeListener(Project project, CodeListener listener)
	{
		ArrayList<CodeListener> listenerList = fCodeListeners.get(project);
		
		if(listenerList == null)
		{
			listenerList = new ArrayList<CodeListener>();
		}
		
		listenerList.add(listener);
		
		fCodeListeners.put(project, listenerList);
	}
	
	/**
	 * Register an InvestigatorListener with the given Project.
	 * @param project
	 * @param listener
	 */
	public void registerInvestigatorListener(Project project, InvestigatorListener listener)
	{
		ArrayList<InvestigatorListener> listenerList = fInvestigatorListeners.get(project);
		
		if(listenerList == null)
		{
			listenerList = new ArrayList<InvestigatorListener>();
		}
		
		listenerList.add(listener);
		fInvestigatorListeners.put(project, listenerList);
	}
	
	/**
	 * Register a ParticipantListener with the given Project.
	 * @param project
	 * @param listener
	 */
	public void registerParticipantListener(Project project, ParticipantListener listener)
	{
		ArrayList<ParticipantListener> listenerList = fParticipantListeners.get(project);
	
		if(listenerList == null)
		{
			listenerList = new ArrayList<ParticipantListener>();
		}
		
		listenerList.add(listener);
		fParticipantListeners.put(project, listenerList);
	}
	
	/**
	 * Register a ProjectListener with a particular project.
	 * @param project
	 * @param listener
	 */
	public void registerProjectListener(Project project, ProjectListener listener)
	{
		ArrayList<ProjectListener> listenerList = fProjectListeners.get(project);
		
		if(listenerList == null)
		{
			listenerList = new ArrayList<ProjectListener>();
		}
		
		listenerList.add(listener);
		fProjectListeners.put(project, listenerList);
	}
	
	/**
	 * Register a TranscriptListener with the given Project.
	 * @param project
	 * @param listener
	 */
	public void registerTranscriptListener(Project project, TranscriptListener listener)
	{
		ArrayList<TranscriptListener> listenerList = fTranscriptListeners.get(project);
		
		if(listenerList == null)
		{
			listenerList = new ArrayList<TranscriptListener>();
		}
		
		listenerList.add(listener);
		fTranscriptListeners.put(project, listenerList);
	}
	
	/**
	 * Unregister a CodeListener from the given Project.
	 * @param project
	 * @param listener
	 */
	public void unregisterCodeListener(Project project, CodeListener listener)
	{
		ArrayList<CodeListener> listenerList = fCodeListeners.get(project);
		
		if(listenerList != null)
		{
			listenerList.remove(listener);
		}
	}
	
	/**
	 * Unregister an InvestigatorListener from the given Project.
	 * @param project
	 * @param listener
	 */
	public void unregisterInvestigatorListener(Project project, InvestigatorListener listener)
	{
		ArrayList<InvestigatorListener> listenerList = fInvestigatorListeners.get(project);
		
		if(listenerList != null)
		{
			listenerList.remove(listener);
		}
	}
	
	/**
	 * Unregister a ParticipantListener from the given Project.
	 * @param project
	 * @param listener
	 */
	public void unregisterParticipantListener(Project project, ParticipantListener listener)
	{
		ArrayList<ParticipantListener> listenerList = fParticipantListeners.get(project);
		
		if(listenerList != null)
		{
			listenerList.remove(listener);
		}
	}
	
	/**
	 * Unregister a ProjectListener from the given Project.
	 * @param project
	 * @param listener
	 */
	public void unregisterProjectListener(Project project, ProjectListener listener)
	{
		ArrayList<ProjectListener> listenerList = fProjectListeners.get(project);
		
		if(listenerList != null)
		{
			listenerList.remove(listener);
		}
	}
	
	/**
	 * Unregister a TranscriptListener from the given Project.
	 * @param project
	 * @param listener
	 */
	public void unregisterTranscriptListener(Project project, TranscriptListener listener)
	{
		ArrayList<TranscriptListener> listenerList = fTranscriptListeners.get(project);
		
		if(listenerList != null)
		{
			listenerList.remove(listener);
		}
	}
}
