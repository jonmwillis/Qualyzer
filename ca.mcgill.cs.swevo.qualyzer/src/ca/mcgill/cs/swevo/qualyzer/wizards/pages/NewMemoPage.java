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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

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
	
	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.wizards.pages.TranscriptWizardPage#createControl(
	 * org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent)
	{
		super.createControl(parent);
		
		Composite composite = (Composite) parent.getChildren()[0];
		
		((Label) composite.getChildren()[0]).setText("Memo name");
		composite.getChildren()[composite.getChildren().length - 1].dispose();
	}
	
	
}
