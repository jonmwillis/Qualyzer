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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

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
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		
		if(selection != null && selection instanceof IStructuredSelection)
		{			
			for(Object element : ((IStructuredSelection) selection).toArray())
			{
				if(element instanceof IProject)
				{	
					boolean confirm = MessageDialog.openConfirm(shell, 
							Messages.getString("handler.DeleteProjectHandler.deleteProject"),  //$NON-NLS-1$
							Messages.getString("handler.DeleteProjectHandler.confirm")); //$NON-NLS-1$
					
					if(confirm)
					{
						Project project = PersistenceManager.getInstance().getProject(((IProject) element).getName());
						Facade.getInstance().deleteProject(project);
					}
				}
			}
		}
		
		return null;
	}

}
