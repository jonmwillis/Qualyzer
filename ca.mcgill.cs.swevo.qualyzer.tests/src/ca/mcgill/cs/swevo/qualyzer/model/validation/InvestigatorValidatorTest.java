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

public class InvestigatorValidatorTest
{
	private static final String TEST_PROJECT_NAME = "TestProject";

	private static final String TEST_INVESTIGATOR_NAME = "Bob";
	private static final String TEST_INVESTIGATOR_FULL = "Mickey Mouse";
	private static final String TEST_INVESTIGATOR_INST = "McGill University";
	
	
	private static final String SAME_INVESTIGATOR_NAME = "Bob";
	
	private static final String LONG_NAME = "AAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBB"+
	"AAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBB"+
	"AAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBB"+
	"AAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBBAAAAAAAAAABBBBBBBBBB"+
	"AAAAAAAAAABBBBBB";

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
		InvestigatorValidator lValidator = new InvestigatorValidator("", TEST_INVESTIGATOR_FULL, TEST_INVESTIGATOR_INST, fProject);
		assertFalse(lValidator.isValid());
		assertEquals("Investigator nickname " + Messages.getString("model.validation.BasicNameValidator.empty"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the nickname follows alphanumerical+
	 */
	@Test
	public void testInvestigatorNameFormat()
	{
		InvestigatorValidator lValidator = new InvestigatorValidator("Mickey Mouse!!", TEST_INVESTIGATOR_FULL, TEST_INVESTIGATOR_INST, fProject);
		assertFalse(lValidator.isValid());
		assertEquals("Investigator nickname " + Messages.getString("model.validation.BasicNameValidator.invalid"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the nickname does not already exist
	 */
	@Test
	public void testInvestigatorUniqueName()
	{
		InvestigatorValidator lValidator = new InvestigatorValidator(TEST_INVESTIGATOR_NAME, TEST_INVESTIGATOR_FULL, TEST_INVESTIGATOR_INST, fProject);
		assertFalse(lValidator.isValid());
		assertEquals("Investigator nickname " + Messages.getString("model.validation.BasicNameValidator.taken"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the nickname does not already exist with a existing name
	 */
	@Test
	public void testInvestigatorUniqueName2()
	{
		InvestigatorValidator lValidator = new InvestigatorValidator(TEST_INVESTIGATOR_NAME, TEST_INVESTIGATOR_NAME + "Foo",TEST_INVESTIGATOR_FULL, TEST_INVESTIGATOR_INST, fProject);
		assertFalse(lValidator.isValid());
		assertEquals("Investigator nickname " + Messages.getString("model.validation.BasicNameValidator.taken"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the name is not tooLong.
	 */
	@Test
	public void testTooLong()
	{
		InvestigatorValidator lValidator = new InvestigatorValidator(LONG_NAME, TEST_INVESTIGATOR_FULL, TEST_INVESTIGATOR_INST, fProject);
		assertFalse(lValidator.isValid());
		assertEquals("Investigator nickname " + Messages.getString("model.validation.BasicNameValidator.tooLong"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the nickname does not already exist with a existing name
	 */
	@Test
	public void testInvestigatorUniqueName3()
	{
		InvestigatorValidator lValidator = new InvestigatorValidator(TEST_INVESTIGATOR_NAME, SAME_INVESTIGATOR_NAME,TEST_INVESTIGATOR_FULL, TEST_INVESTIGATOR_INST, fProject);
		assertTrue(lValidator.isValid());
		assertNull(lValidator.getErrorMessage());
	}
	
	/**
	 * Tests a too long full name.
	 */
	@Test
	public void testFullName()
	{
		InvestigatorValidator lValidator = new InvestigatorValidator(TEST_INVESTIGATOR_NAME, SAME_INVESTIGATOR_NAME,
				LONG_NAME, TEST_INVESTIGATOR_INST, fProject);
		assertFalse(lValidator.isValid());
		assertEquals(Messages.getString("model.validation.InvestigatorValidator.fullNameTooLong"),lValidator.getErrorMessage());
	}
	
	/**
	 * Tests a too long full name.
	 */
	@Test
	public void testInstitution()
	{
		InvestigatorValidator lValidator = new InvestigatorValidator(TEST_INVESTIGATOR_NAME, SAME_INVESTIGATOR_NAME,
				TEST_INVESTIGATOR_FULL, LONG_NAME, fProject);
		assertFalse(lValidator.isValid());
		assertEquals(Messages.getString("model.validation.InvestigatorValidator.institutionTooLong"),lValidator.getErrorMessage());
	}
	
	/**
	 * Tests that a valid investigator is indeed validated
	 */
	@Test
	public void testValidInvestigator()
	{
		InvestigatorValidator lValidator = new InvestigatorValidator("Martin", TEST_INVESTIGATOR_FULL, TEST_INVESTIGATOR_INST, fProject);
		assertTrue(lValidator.isValid());
		assertEquals(null,lValidator.getErrorMessage());
	}
}
