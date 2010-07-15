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
package ca.mcgill.cs.swevo.qualyzer.wizards.pages;

import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 *
 */
public class NewMemoPage extends TranscriptWizardPage
{
	/**
	 * Constructor.
	 * @param project
	 */
	public NewMemoPage(Project project)
	{
		super(project, "New Memo");
		setTitle("New Memo Wizard");
		setDescription("Enter the following information to create a new Memo.");
	}
}
