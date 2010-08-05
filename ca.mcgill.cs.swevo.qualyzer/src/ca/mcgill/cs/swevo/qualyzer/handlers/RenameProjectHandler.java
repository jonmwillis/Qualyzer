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
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.QualyzerException;
import ca.mcgill.cs.swevo.qualyzer.dialogs.RenameProjectDialog;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;
import ca.mcgill.cs.swevo.qualyzer.util.FileUtil;

/**
 * 
 *
 */
public class RenameProjectHandler extends AbstractHandler
{

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
				RenameProjectDialog dialog = new RenameProjectDialog(shell, project);
				dialog.create();
				if(dialog.open() == Window.OK)
				{
					try
					{
						String oldName = project.getName();
						Facade.getInstance().renameProject(project, dialog.getNewName());
						
						CommonNavigator view = (CommonNavigator) page.findView(
								QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
						view.getCommonViewer().refresh();
						
						FileUtil.renameProject(oldName, dialog.getNewName());
						
						IProject wProject = ResourcesPlugin.getWorkspace().getRoot().getProject(dialog.getNewName());
						PersistenceManager.getInstance().refreshManager(wProject);
						
						
					}
					catch(QualyzerException e)
					{
						MessageDialog.openError(shell, Messages.getString(
								"handlers.RenameProjectHandler.renameError"), e.getMessage()); //$NON-NLS-1$
					}
				}
			}
		}
		return null;
	}

}
