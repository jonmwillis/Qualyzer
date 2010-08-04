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
package ca.mcgill.cs.swevo.qualyzer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.mcgill.cs.swevo.qualyzer.QualyzerException;
import ca.mcgill.cs.swevo.qualyzer.dialogs.ProjectPropertiesDialog;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;

/**
 * 
 *
 */
public class ProjectPropertiesHandler extends AbstractHandler
{
	private static Logger gLogger = LoggerFactory.getLogger(ProjectPropertiesHandler.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		ISelection selection = page.getSelection();
		
		if(selection != null && selection instanceof IStructuredSelection)
		{
			IStructuredSelection struct = (IStructuredSelection) selection;
			Object element = struct.getFirstElement();
			if(element instanceof IProject)
			{
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				Project project = ResourcesUtil.getProject(element);
				ProjectPropertiesDialog dialog = new ProjectPropertiesDialog(shell, project);
				dialog.create();
				
				try
				{
					if(dialog.open() == Window.OK)
					{
						IProject wProject = (IProject) element;
						try
						{
							IProjectDescription desc = wProject.getDescription();
							desc.setComment(dialog.getInvestigator());
							wProject.setDescription(desc, new NullProgressMonitor());
						}
						catch (CoreException e)
						{
							gLogger.error("Unable to set Active Investigator", e); //$NON-NLS-1$
							MessageDialog.openError(shell, Messages.getString(
									"handlers.ProjectPropertiesHandler.fileAccessError"), //$NON-NLS-1$
									Messages.getString("handlers.ProjectPropertiesHandler.errorMessage")); //$NON-NLS-1$
						}
					}
				}
				catch(QualyzerException e)
				{
					MessageDialog.openError(shell, Messages.getString(
							"handlers.ProjectPropertiesHandler.fileAccessError"), e.getMessage()); //$NON-NLS-1$
				}
			}
		}
		
		return null;
	}

}
