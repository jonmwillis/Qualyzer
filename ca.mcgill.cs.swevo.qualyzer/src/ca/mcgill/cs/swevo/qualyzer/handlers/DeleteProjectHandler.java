/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     McGill University - initial API and implementation
 *     Jonathan Faubert
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.handlers;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.model.HibernateDBManager;

/**
 * Handler for the Delete Project Command.
 *
 */
public class DeleteProjectHandler extends AbstractHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
		
		if(selection != null && selection instanceof IStructuredSelection)
		{
			IStructuredSelection structSelection = (IStructuredSelection) selection;
			
			Object element = structSelection.getFirstElement();
			
			if(element instanceof IProject)
			{
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				
				boolean confirm = MessageDialog.openConfirm(shell, 
						Messages.getString("handler.DeleteProjectHandler.deleteProject"),  //$NON-NLS-1$
						Messages.getString("handler.DeleteProjectHandler.confirm")); //$NON-NLS-1$
				
				if(confirm)
				{
					try
					{
						IProject project = (IProject) element;
						
						if(!project.isOpen())
						{
							project.open(new NullProgressMonitor());
						}
						
						HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers()
							.get(project.getName());
						QualyzerActivator.getDefault().getHibernateDBManagers().remove(project.getName());
						manager.shutdownDBServer();
						manager.close();
						project.delete(true, true, new NullProgressMonitor());
					}
					catch(CoreException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		
		return null;
	}

}
