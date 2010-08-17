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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.QualyzerException;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.wizards.pages.NewProjectPage;

/**
 * The wizard that controls the creation of a new project.
 */
public class NewProjectWizard extends Wizard
{
	private static final int WORK = 5;
	private NewProjectPage fOne;
	private IProject fProject;
	
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
		fOne.save();
		
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
		dialog.setOpenOnRun(true);
		dialog.create();
		dialog.getShell().setText(Messages.getString("wizards.NewProjectWizard.projectCreationStatus")); //$NON-NLS-1$
		try
		{
			dialog.run(true, false, new IRunnableWithProgress()
			{
				
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
				{
					QualyzerActivator.getDefault().setCreatingProject(true);
					monitor.beginTask(Messages.getString("wizards.NewProjectWizard.creatingNewProject"), //$NON-NLS-1$
							WORK); 
					monitor.worked(1);
					monitor.worked(1);
					Project project = Facade.getInstance().
					createProject(fOne.getProjectName(), fOne.getInvestigatorNickname(), 
							fOne.getInvestigatorFullname(), fOne.getInstitution());
					monitor.worked(2);
					
					fProject = ResourcesPlugin.getWorkspace().getRoot().getProject(project.getFolderName());
					monitor.worked(1);
					monitor.done();
					QualyzerActivator.getDefault().setCreatingProject(false);
				}
			});
		}
		catch (InvocationTargetException e)
		{
			
		}
		catch (InterruptedException e)
		{
			
		}
		catch(QualyzerException e) 
		{
			MessageDialog.openError(getShell(), Messages.getString(
					"wizard.NewProjectWizard.projectError"), e.getMessage()); //$NON-NLS-1$
			return false;
		}

		return fProject != null && fProject.exists();
	}
	
	/**
	 * Gets the IProject created by the wizard. Used to properly expand the node.
	 * @return
	 */
	public IProject getProjectReference()
	{
		return fProject;
	}

}
