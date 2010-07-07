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
package ca.mcgill.cs.swevo.qualyzer.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import ca.mcgill.cs.swevo.qualyzer.QualyzerException;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.ProjectCreationProgressListener;
import ca.mcgill.cs.swevo.qualyzer.wizards.pages.NewProjectPage;

/**
 * The wizard that controls the creation of a new project.
 */
public class NewProjectWizard extends Wizard implements ProjectCreationProgressListener
{

	/**
	 * 
	 */
	private static final int PROGRESS_HEIGHT = 50;
	/**
	 * 
	 */
	private static final int PROGRESS_WIDTH = 260;
	/**
	 * 
	 */
	private static final int PROGRESS_MAX = 5;
	private NewProjectPage fOne;
	private IProject fProject;
	private ProgressBar fProgressBar;
	
	/**
	 * Constructor.
	 */
	public NewProjectWizard()
	{
		super();
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public void addPages()
	{
		fOne = new NewProjectPage();
		
		addPage(fOne);
	}
	
	@Override
	public boolean performFinish()
	{	
		Project project = null;
		
		Shell shell = new Shell(getShell(), SWT.TITLE | SWT.BORDER);
		shell.setLayout(new GridLayout(1, true));
		shell.setText(Messages.getString("wizards.NewProjectWizard.projectCreation")); //$NON-NLS-1$
		shell.setBounds(0, 0, PROGRESS_WIDTH, PROGRESS_HEIGHT);
		fProgressBar = new ProgressBar(shell, SWT.HORIZONTAL);
		fProgressBar.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		fProgressBar.setMinimum(0);
		fProgressBar.setMaximum(PROGRESS_MAX);
		fProgressBar.setSelection(0);
		shell.open();

		try
		{
			project = Facade.getInstance().createProject(fOne.getProjectName(), 
				fOne.getInvestigatorNickname(), fOne.getInvestigatorFullname(), fOne.getInstitution(), this);
			
			fProject = ResourcesPlugin.getWorkspace().getRoot().getProject(project.getName());
		}
		catch(QualyzerException e) 
		{
			MessageDialog.openError(getShell(), Messages.getString(
					"wizard.NewProjectWizard.projectError"), e.getMessage()); //$NON-NLS-1$
			return false;
		}

		return project != null;
	}
	
	/**
	 * Gets the IProject created by the wizard. Used to properly expand the node.
	 * @return
	 */
	public IProject getProjectReference()
	{
		return fProject;
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.model.ProjectCreationProgressListener#statusUpdate()
	 */
	@Override
	public void statusUpdate()
	{
		fProgressBar.setSelection(fProgressBar.getSelection() + 1);
	}
}
