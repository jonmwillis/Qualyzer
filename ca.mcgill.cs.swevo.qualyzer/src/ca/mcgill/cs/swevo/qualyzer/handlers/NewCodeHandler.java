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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.dialogs.NewCodeDialog;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperCode;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;

/**
 * Handler for the new code command.
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class NewCodeHandler extends AbstractHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		ISelection selection = activePage.getSelection();
		
		if(selection != null && selection instanceof IStructuredSelection)
		{
			IStructuredSelection strucSelection = (IStructuredSelection) selection;
			Object element = strucSelection.getFirstElement();
			
			Project project = ResourcesUtil.getProject(element);
			
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().getActivePart().getSite().getShell();
			
			NewCodeDialog dialog = new NewCodeDialog(shell, project);
			dialog.create();
			if(dialog.open() == Window.OK)
			{
				Facade.getInstance().createCode(dialog.getName(), dialog.getDescription(), project);
				CommonNavigator viewer = (CommonNavigator) activePage.findView(
						QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
				viewer.getCommonViewer().refresh();
				
				ResourcesUtil.openEditor(activePage, new WrapperCode(project));
			}
		}
		
		return null;
	}

}
