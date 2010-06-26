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
package ca.mcgill.cs.swevo.qualyzer.model;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType;

/**
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 * 
 */
public class DebugListener implements CodeListener, InvestigatorListener, ParticipantListener, ProjectListener,
		TranscriptListener
{

	private List<ListenerEvent> fEvents = new ArrayList<ListenerEvent>();

	/**
	 * 
	 * @return
	 */
	public List<ListenerEvent> getEvents()
	{
		return fEvents;
	}

	@Override
	public void investigatorChanged(ChangeType cType, Investigator investigator, Facade facade)
	{
		fEvents.add(new ListenerEvent(cType, investigator));
	}

	@Override
	public void participantChanged(ChangeType cType, Participant participant, Facade facade)
	{
		fEvents.add(new ListenerEvent(cType, participant));
	}

	@Override
	public void projectChanged(ChangeType cType, Project project, Facade facade)
	{
		fEvents.add(new ListenerEvent(cType, project));
	}

	@Override
	public void codeChanged(ChangeType cType, Code code, Facade facade)
	{
		fEvents.add(new ListenerEvent(cType, code));
	}

	@Override
	public void transcriptChanged(ChangeType cType, Transcript transcript, Facade facade)
	{
		fEvents.add(new ListenerEvent(cType, transcript));
	}

}
