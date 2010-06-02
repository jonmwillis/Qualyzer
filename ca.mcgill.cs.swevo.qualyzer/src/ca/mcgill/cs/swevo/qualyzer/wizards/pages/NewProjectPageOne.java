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
package ca.mcgill.cs.swevo.qualyzer.wizards.pages;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * The first page of the New Project Wizard.
 * @author Jonathan Faubert
 *
 */
public class NewProjectPageOne extends WizardPage
{

	private Composite fContainer;
	private Text fProjectName;
	
	/**
	 * Constructor.
	 */
	public NewProjectPageOne()
	{
		super("New Project - 1/2");
		setTitle("New Project");
		setDescription("Please enter a name for the project.");
	}
	
	@Override
	public void createControl(Composite parent) 
	{
		fContainer = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		fContainer.setLayout(layout);
		Label label = new Label(fContainer, SWT.NULL);
		label.setText("Project Name");
		fProjectName = new Text(fContainer, SWT.BORDER);
		fProjectName.setText("");
		
		//Checks if there is anything in the textbox
		//if not then cannot proceed
		fProjectName.addKeyListener(createKeyListener());
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		fProjectName.setLayoutData(gd);
		// Required to avoid an error in the system
		setControl(fContainer);
		setPageComplete(false);
	}

	/**
	 * @return
	 */
	private KeyListener createKeyListener()
	{
		return new KeyListener(){
			@Override
			public void keyPressed(KeyEvent e){}
			
			@Override
			public void keyReleased(KeyEvent e) 
			{
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				
				if(!fProjectName.getText().isEmpty())
				{
					IProject wProject = root.getProject(fProjectName.getText());
					
					if(wProject.exists())
					{
						setError("This project already exists! Please choose a different name.");
					}	
					else
					{
						setError(null);
					}
				}
				else
				{
					setError("Please enter a name for the project");
				}
			}
		};
	}
	
	private void setError(String message)
	{
		setErrorMessage(message);
		if(message == null)
		{
			setPageComplete(true);
		}
		else
		{
			setPageComplete(false);
		}
	}
	
	/**
	 * Get the project name field.
	 * @return
	 */
	public String getProjectName()
	{
		return fProjectName.getText();
	}
}
