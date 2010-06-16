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

import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;

/**
 * The first page of the New Project Wizard.
 * @author Jonathan Faubert
 *
 */
public class NewProjectPageOne extends WizardPage
{

	private Composite fContainer;
	private Text fProjectName;
	private Text fNickname;
	private Text fFullname;
	private Text fInstitution;
	
	/**
	 * Constructor.
	 */
	public NewProjectPageOne()
	{
		super(Messages.getString("wizards.pages.NewProjectPageOne.newProject")); //$NON-NLS-1$
		setTitle(Messages.getString("wizards.pages.NewProjectPageOne.newProject")); //$NON-NLS-1$
		setDescription(Messages.getString("wizards.pages.NewProjectPageOne.enterName")); //$NON-NLS-1$
	}
	
	@Override
	public void createControl(Composite parent) 
	{
		fContainer = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		fContainer.setLayout(layout);
		
		Label label = new Label(fContainer, SWT.NULL);
		label.setText(Messages.getString("wizards.pages.NewProjectPageOne.projectName")); //$NON-NLS-1$
		fProjectName = new Text(fContainer, SWT.BORDER);
		fProjectName.setText(""); //$NON-NLS-1$
		fProjectName.addKeyListener(createKeyListener());
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		fProjectName.setLayoutData(gd);
	
		Composite composite = createComposite();
		
		createSectionHeader(composite);
		
		label = new Label(composite, SWT.NULL);
		label.setText(Messages.getString("wizards.pages.NewProjectPageOne.nickname")); //$NON-NLS-1$

		fNickname = new Text(composite, SWT.BORDER | SWT.SINGLE);
		fNickname.setText(System.getProperty("user.name")); //$NON-NLS-1$
		
		fNickname.addKeyListener(createKeyListener());
		
		label = new Label(composite, SWT.NULL);
		label.setText(Messages.getString("wizards.pages.NewProjectPageOne.fullName")); //$NON-NLS-1$
		
		fFullname = new Text(composite, SWT.BORDER | SWT.SINGLE);
		fFullname.setText(""); //$NON-NLS-1$
		
		label = new Label(composite, SWT.NULL);
		label.setText(Messages.getString("wizards.pages.NewProjectPageOne.insitution")); //$NON-NLS-1$
		
		fInstitution = new Text(composite, SWT.BORDER | SWT.SINGLE);
		fInstitution.setText(""); //$NON-NLS-1$
		
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fNickname.setLayoutData(gd);
		fFullname.setLayoutData(gd);
		fInstitution.setLayoutData(gd);
		
		// Required to avoid an error in the system
		setControl(fContainer);
		setPageComplete(false);
	}

	/**
	 * @param composite
	 */
	private void createSectionHeader(Composite composite)
	{
		Label label;
		GridData gd;
		label = new Label(composite, SWT.WRAP);
		label.setText(Messages.getString("wizards.pages.NewProjectPageOne.info")); //$NON-NLS-1$
		gd = new GridData(SWT.FILL, SWT.NULL, false, false);
		gd.horizontalSpan = 2;
		label.setLayoutData(gd);
	}

	/**
	 * @return
	 */
	private Composite createComposite()
	{
		GridLayout layout;
		GridData gd;
		Composite composite = new Composite(fContainer, SWT.BORDER);
		layout = new GridLayout();
		layout.numColumns = 2;
		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		gd.horizontalSpan = 2;
		composite.setLayout(layout);
		composite.setLayoutData(gd);
		return composite;
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
				
				if(fProjectName.getText().isEmpty())
				{
					setError(Messages.getString("wizards.pages.NewProjectPageOne.enterName")); //$NON-NLS-1$
				}
				else if(!ResourcesUtil.verifyID(fProjectName.getText()))
				{
					setError(Messages.getString("wizards.pages.NewProjectPageOne.invalidName")); //$NON-NLS-1$
				}
				else
				{
					IProject wProject = root.getProject(fProjectName.getText());
					
					if(wProject.exists())
					{
						setError(Messages.getString("wizards.pages.NewProjectPageOne.alreadyExists")); //$NON-NLS-1$
					}
					else if(fNickname.getText().isEmpty())
					{
						setError(Messages.getString("wizards.pages.NewProjectPageOne.enterNickname")); //$NON-NLS-1$
					}
					else
					{
						setError(null);
					}
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
	
	/**
	 * Get the Nickname field.
	 * @return
	 */
	public String getInvestigatorNickname()
	{
		return fNickname.getText();
	}
	
	/**
	 * Get the fullname field.
	 * @return
	 */
	public String getInvestigatorFullname()
	{
		return fFullname.getText();
	}
	
	/**
	 * Get the Institution field.
	 * @return
	 */
	public String getInstitution()
	{
		return fInstitution.getText();
	}
}
