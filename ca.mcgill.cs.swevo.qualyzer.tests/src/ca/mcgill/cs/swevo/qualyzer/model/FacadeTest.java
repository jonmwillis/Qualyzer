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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType;

/**
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 * 
 */
public class FacadeTest
{

	private static final String TEST_PROJECT_NAME = "TestProject";

	private static final String TEST_INVESTIGATOR_NAME = "Bob";

	private ListenerManager fListenerManager;

	private Project fProject;

	private Facade fFacade;

	private DebugListener fListener;

	/**
	 * 
	 */
	@Before
	public void setUp()
	{
		fListener = new DebugListener();
		fFacade = Facade.getInstance();
		fProject = fFacade.createProject(TEST_PROJECT_NAME, TEST_INVESTIGATOR_NAME, TEST_INVESTIGATOR_NAME, "");
		fListenerManager = fFacade.getListenerManager();
		fListenerManager.registerCodeListener(fProject, fListener);
		fListenerManager.registerInvestigatorListener(fProject, fListener);
		fListenerManager.registerParticipantListener(fProject, fListener);
		fListenerManager.registerProjectListener(fProject, fListener);
		fListenerManager.registerTranscriptListener(fProject, fListener);
	}

	/**
	 * 
	 */
	@After
	public void tearDown()
	{
		fListenerManager.unregisterCodeListener(fProject, fListener);
		fListenerManager.unregisterInvestigatorListener(fProject, fListener);
		fListenerManager.unregisterParticipantListener(fProject, fListener);
		fListenerManager.unregisterProjectListener(fProject, fListener);
		fListenerManager.unregisterTranscriptListener(fProject, fListener);
		fFacade.deleteProject(fProject);
	}

	/**
	 * Verifies db state and listeners.
	 */
	@Test
	public void testCreateParticipant()
	{
		String pId = "p1";
		String pName = "Toto";
		Participant participant = fFacade.createParticipant(pId, pName, fProject);
		ListenerEvent event = fListener.getEvents().get(0);

		// Test DB
		assertNotNull(participant);
		assertEquals(1, PersistenceManager.getInstance().getProject(TEST_PROJECT_NAME).getParticipants().size());
		// Test event
		assertEquals(ChangeType.ADD, event.getChangeType());
		assertArrayEquals(new Participant[]{participant}, (Object[]) event.getObject());
		
	}

	/**
	 * Verifies db state and listeners.
	 */
	@Test
	public void testCreateInvestigator()
	{
		Investigator investigator = fFacade.createInvestigator("TestInvestigator", "TestInvestigator FullName",
				"McGill", fProject, true);
		ListenerEvent event = fListener.getEvents().get(0);

		// Test DB
		assertNotNull(investigator);
		assertEquals(2, PersistenceManager.getInstance().getProject(TEST_PROJECT_NAME).getInvestigators().size());
		// Test event
		assertEquals(ChangeType.ADD, event.getChangeType());
		assertArrayEquals(new Investigator[]{investigator}, (Object[]) event.getObject());
		
	}

	/**
	 * Verifies db state and listeners.
	 */
	@Test
	public void testCreateTranscript()
	{
		String pId = "p1";
		String pName = "Toto";
		String transcriptName = "t1";
		Participant participant = fFacade.createParticipant(pId, pName, fProject);
		List<Participant> participants = new ArrayList<Participant>();
		participants.add(participant);

		Transcript transcript = fFacade.createTranscript(transcriptName, "6/26/2010", "", participants, fProject);
		ListenerEvent event = fListener.getEvents().get(1);

		// Test DB
		assertNotNull(transcript);
		assertEquals(1, PersistenceManager.getInstance().getProject(TEST_PROJECT_NAME).getTranscripts().size());
		// Test event
		assertEquals(ChangeType.ADD, event.getChangeType());
		assertArrayEquals(new Transcript[]{transcript}, (Object[]) event.getObject());
	}

}
