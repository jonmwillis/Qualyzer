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

public class CodeValidatorTest
{
	private static final String TEST_PROJECT_NAME = "TestProject";

	private static final String TEST_INVESTIGATOR_NAME = "Bob";
	
	private static final String TEST_CODE = "Code1";

	private Facade fFacade;
	
	private Project fProject;
	
	/**
	 * 
	 */
	@Before
	public void setUp()
	{
		fFacade = Facade.getInstance();
		fProject = fFacade.createProject(TEST_PROJECT_NAME, TEST_INVESTIGATOR_NAME, TEST_INVESTIGATOR_NAME, "");
		fFacade.createCode(TEST_CODE, "", fProject);
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
	public void testEmpty()
	{
		CodeValidator lValidator = new CodeValidator("", fProject);
		assertFalse(lValidator.isValid());
		assertEquals(Messages.getString("model.validation.CodeValidator.empty"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the name follows alphanumerical+
	 */
	@Test
	public void testFormat()
	{
		CodeValidator lValidator = new CodeValidator("Moon Indigo", fProject);
		assertFalse(lValidator.isValid());
		assertEquals(Messages.getString("model.validation.CodeValidator.invalid"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the name does not already exist
	 */
	@Test
	public void testUniqueName()
	{
		CodeValidator lValidator = new CodeValidator(TEST_CODE, fProject);
		assertFalse(lValidator.isValid());
		assertEquals(Messages.getString("model.validation.CodeValidator.taken"),lValidator.getErrorMessage());
	}
	
	/**
	 * Tests that a valid code is indeed validated
	 */
	@Test
	public void testValid()
	{
		CodeValidator lValidator = new CodeValidator("NewCode", fProject);
		assertTrue(lValidator.isValid());
		assertEquals(null,lValidator.getErrorMessage());
	}
}