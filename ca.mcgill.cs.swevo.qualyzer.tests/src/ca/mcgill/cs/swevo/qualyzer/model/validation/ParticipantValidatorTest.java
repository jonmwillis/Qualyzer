/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Robillard
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.model.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.ProjectCreationProgressListener;

public class ParticipantValidatorTest
{
	private static final String TEST_PROJECT_NAME = "TestProject";

	private static final String TEST_INVESTIGATOR_NAME = "Bob";
	
	private static final String TEST_PARTICIPANT_NAME = "Jaffy";
	private static final String SAME_PARTICIPANT_NAME = "Jaffy";
	
	private static final String TEST_PARTICIPANT_ID = "P01";

	private Facade fFacade;
	
	private Project fProject;
	
	private static final ProjectCreationProgressListener fProgress = new ProjectCreationProgressListener()
	{
		
		@Override
		public void statusUpdate()
		{
			
		}
	};
	
	/**
	 * 
	 */
	@Before
	public void setUp()
	{
		fFacade = Facade.getInstance();
		fProject = fFacade.createProject(TEST_PROJECT_NAME, TEST_INVESTIGATOR_NAME, TEST_INVESTIGATOR_NAME, "", fProgress);
		fFacade.createParticipant(TEST_PARTICIPANT_ID, TEST_PARTICIPANT_NAME, fProject);
	}

	/**
	 * 
	 */
	@After
	public void tearDown()
	{
		fFacade.deleteProject(fProject);
	}
	
	/**
	 * Verifies that the name is not empty.
	 */
	@Test
	public void testEmptyParticipantName()
	{
		ParticipantValidator lValidator = new ParticipantValidator("", fProject);
		assertFalse(lValidator.isValid());
		assertEquals(Messages.getString("model.validation.ParticipantValidator.emptyParticipantID"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the ID follows alphanumerical+
	 */
	@Test
	public void testParticipantNameFormat()
	{
		ParticipantValidator lValidator = new ParticipantValidator("Bing! Crosby", fProject);
		assertFalse(lValidator.isValid());
		assertEquals(Messages.getString("model.validation.ParticipantValidator.invalidParticipantName"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the nickname does not already exist
	 */
	@Test
	public void testParticipantUniqueName()
	{
		ParticipantValidator lValidator = new ParticipantValidator(TEST_PARTICIPANT_ID, fProject);
		assertFalse(lValidator.isValid());
		assertEquals(Messages.getString("model.validation.ParticipantValidator.IDTaken"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the ID does not already exist with a existing name
	 */
	@Test
	public void testUniqueName2()
	{
		ParticipantValidator lValidator = new ParticipantValidator(TEST_PARTICIPANT_ID, TEST_PARTICIPANT_ID + "Foo", fProject);
		assertFalse(lValidator.isValid());
		assertEquals(Messages.getString("model.validation.ParticipantValidator.IDTaken"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the name does not already exist with a existing name
	 */
	@Test
	public void testUniqueName3()
	{
		ParticipantValidator lValidator = new ParticipantValidator(TEST_PARTICIPANT_NAME, SAME_PARTICIPANT_NAME, fProject);
		assertTrue(lValidator.isValid());
		assertNull(lValidator.getErrorMessage());
	}
	
	/**
	 * Tests that a valid investigator is indeed validated
	 */
	@Test
	public void testValidParticipant()
	{
		ParticipantValidator lValidator = new ParticipantValidator("NewGuy", fProject);
		assertTrue(lValidator.isValid());
		assertEquals(null,lValidator.getErrorMessage());
	}
}
