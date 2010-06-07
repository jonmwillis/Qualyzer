/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jonathan Faubert (jonfaub@gmail.com)
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.navigator.CommonNavigator;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperParticipant;
import ca.mcgill.cs.swevo.qualyzer.util.ResourcesUtil;
import ca.mcgill.cs.swevo.qualyzer.wizards.AddParticipantWizard;

/**
 * Launches a wizard whenever the New Participant Command is clicked.
 *
 */
public class AddParticipantHandler extends AbstractHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchPage page = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();
		CommonNavigator view = (CommonNavigator) page.findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
		ISelection selection = page.getSelection();
		
		if (selection != null && selection instanceof IStructuredSelection)
		{
			IStructuredSelection strucSelection = (IStructuredSelection) selection;
			Object element = strucSelection.getFirstElement();

			Project project = ResourcesUtil.getProject(element);
			
			AddParticipantWizard wizard = new AddParticipantWizard(project);
			WizardDialog dialog = new WizardDialog(HandlerUtil.getActiveShell(event), wizard);
			
			if(dialog.open() == Window.OK)
			{
				view.getCommonViewer().refresh(new WrapperParticipant(project));
				
				if(element instanceof IProject)
				{
					view.getCommonViewer().refresh(element);
				}
				
				ResourcesUtil.openEditor(page, wizard.getParticipant());
				//TODO open the editor by calling the command
			}
		}

		return null;
	}

}
