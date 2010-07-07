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
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.ProjectCreationProgressListener;

public class InvestigatorValidatorTest
{
	private static final String TEST_PROJECT_NAME = "TestProject";

	private static final String TEST_INVESTIGATOR_NAME = "Bob";

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
	public void testEmptyInvestigatorName()
	{
		InvestigatorValidator lValidator = new InvestigatorValidator("", fProject);
		assertFalse(lValidator.isValid());
		assertEquals(Messages.getString("model.validation.InvestigatorValidator.emptyInvestigatorID"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the nickname follows alphanumerical+
	 */
	@Test
	public void testInvestigatorNameFormat()
	{
		InvestigatorValidator lValidator = new InvestigatorValidator("Mickey Mouse!!", fProject);
		assertFalse(lValidator.isValid());
		assertEquals(Messages.getString("model.validation.InvestigatorValidator.invalidInvestigatorName"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the nickname does not already exist
	 */
	@Test
	public void testInvestigatorUniqueName()
	{
		InvestigatorValidator lValidator = new InvestigatorValidator(TEST_INVESTIGATOR_NAME, fProject);
		assertFalse(lValidator.isValid());
		assertEquals(Messages.getString("model.validation.InvestigatorValidator.nicknameTaken"),lValidator.getErrorMessage());
	}
	
	/**
	 * Tests that a valid investigator is indeed validated
	 */
	@Test
	public void testValidInvestigator()
	{
		InvestigatorValidator lValidator = new InvestigatorValidator("Martin", fProject);
		assertTrue(lValidator.isValid());
		assertEquals(null,lValidator.getErrorMessage());
	}
}
