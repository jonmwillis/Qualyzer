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

import org.eclipse.core.resources.ResourcesPlugin;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.wizards.pages.Messages;

public class ProjectValidatorTest
{
	private static final String TEST_PROJECT_NAME = "TestProject";

	private static final String TEST_INVESTIGATOR_NAME = "Bob";

	/**
	 * Verifies that the name is not empty.
	 */
	@Test
	public void testEmptyProjectName()
	{
		ProjectValidator lValidator = new ProjectValidator("", "Martin", ResourcesPlugin.getWorkspace().getRoot());
		assertFalse(lValidator.isValid());
		assertEquals(Messages.getString("model.validation.ProjectValidator.emptyProjectName"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the name follows alphanumerical+
	 */
	@Test
	public void testProjectNameFormat()
	{
		ProjectValidator lValidator = new ProjectValidator("Bing! Bang!", "Martin", ResourcesPlugin.getWorkspace().getRoot());
		assertFalse(lValidator.isValid());
		assertEquals(Messages.getString("model.validation.ProjectValidator.invalidProjectName"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the project does not already exist
	 */
	@Test
	public void testProjectUniqueName()
	{
		Project project = Facade.getInstance().createProject(TEST_PROJECT_NAME, TEST_INVESTIGATOR_NAME, TEST_INVESTIGATOR_NAME, "");
		ProjectValidator lValidator = new ProjectValidator(TEST_PROJECT_NAME, "Martin", ResourcesPlugin.getWorkspace().getRoot());
		assertFalse(lValidator.isValid());
		assertEquals(Messages.getString("model.validation.ProjectValidator.alreadyExists"),lValidator.getErrorMessage());
		Facade.getInstance().deleteProject(project);
	}
	
	/**
	 * Verifies that the investigator nickname is not empty
	 */
	@Test
	public void testEmptyInvestigatorName()
	{
		ProjectValidator lValidator = new ProjectValidator(TEST_PROJECT_NAME, "", ResourcesPlugin.getWorkspace().getRoot());
		assertFalse(lValidator.isValid());
		assertEquals(Messages.getString("model.validation.ProjectValidator.enterNickname"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that the investigator nickname is in the right format.
	 */
	@Test
	public void testInvestigatorNameFormat()
	{
		ProjectValidator lValidator = new ProjectValidator(TEST_PROJECT_NAME, "Bing! Bang!", ResourcesPlugin.getWorkspace().getRoot());
		assertFalse(lValidator.isValid());
		assertEquals(Messages.getString("model.validation.ProjectValidator.invalidInvestigatorName"),lValidator.getErrorMessage());
	}
	
	/**
	 * Verifies that multiple errors cascade in order
	 */
	@Test
	public void testMultipleErrors()
	{
		ProjectValidator lValidator = new ProjectValidator("", "Bing! Bang!", ResourcesPlugin.getWorkspace().getRoot());
		assertFalse(lValidator.isValid());
		assertEquals(Messages.getString("model.validation.ProjectValidator.emptyProjectName"),lValidator.getErrorMessage());
	}
	
	/**
	 * Tests that a valid project is indeed validated
	 */
	@Test
	public void testValidProject()
	{
		ProjectValidator lValidator = new ProjectValidator("Test", "Martin", ResourcesPlugin.getWorkspace().getRoot());
		assertTrue(lValidator.isValid());
		assertEquals(null,lValidator.getErrorMessage());
	}
}
