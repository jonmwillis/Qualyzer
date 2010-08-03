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
package ca.mcgill.cs.swevo.qualyzer.dialogs;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 *
 */
public class ProjectPropertiesDialog extends TitleAreaDialog
{
	private Project fProject;
	private Combo fInvestigator;
	
	/**
	 * Constructor.
	 * @param shell
	 */
	public ProjectPropertiesDialog(Shell shell, Project project)
	{
		super(shell);
		fProject = project;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	@Override
	public void create()
	{
		super.create();
		setTitle("Project Properties");
		setMessage("Properties for Project: " + fProject.getName());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(2, false));
		
		Label label = new Label(composite, SWT.NULL);
		label.setText("Project Path: ");
		
		String path = ResourcesPlugin.getWorkspace().getRoot().getProject(fProject.getName()).getLocation().toString();
		label = new Label(composite, SWT.BORDER);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		label.setText(path);
		
		label = new Label(composite, SWT.NULL);
		label.setText("Active Investigator: ");
		
		fInvestigator = new Combo(composite, SWT.READ_ONLY);
		for(Investigator investigator : fProject.getInvestigators())
		{
			fInvestigator.add(investigator.getNickName());
		}
		fInvestigator.select(0); //TODO change to currently selected one.
		fInvestigator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		return parent;
	}
}
