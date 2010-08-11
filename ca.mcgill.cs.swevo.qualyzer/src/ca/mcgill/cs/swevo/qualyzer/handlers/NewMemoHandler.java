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
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;
import ca.mcgill.cs.swevo.qualyzer.wizards.NewMemoWizard;

/**
 * Opens the new memo wizard and then opens the memo editor.
 *
 */
public class NewMemoHandler extends AbstractHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		ISelection selection = page.getSelection();
		
		if(selection != null && selection instanceof IStructuredSelection)
		{
			Object element = ((IStructuredSelection) selection).getFirstElement();
			Project project = ResourcesUtil.getProject(element);
			
			NewMemoWizard wizard = new NewMemoWizard(project);
			WizardDialog dialog = new WizardDialog(shell, wizard);
			
			if(dialog.open() == Window.OK)
			{
				CommonNavigator view = (CommonNavigator) page.findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
				view.getCommonViewer().refresh();
				
				ResourcesUtil.openEditor(page, wizard.getMemo());
			}
			
			
		}
		return null;
	}

}
