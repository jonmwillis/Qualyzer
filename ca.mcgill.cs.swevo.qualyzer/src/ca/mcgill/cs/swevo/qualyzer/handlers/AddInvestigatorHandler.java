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
package ca.mcgill.cs.swevo.qualyzer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.navigator.CommonNavigator;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.dialogs.AddInvestigatorDialog;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 * 
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class AddInvestigatorHandler extends AbstractHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage page = window.getActivePage();
		CommonNavigator view = (CommonNavigator) page.findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
		ISelection selection = page.getSelection();
		
		AddInvestigatorDialog dialog = new AddInvestigatorDialog(window.getShell());
		dialog.create();
		
		if(dialog.open() == Window.OK)
		{
			Investigator investigator = new Investigator();
			investigator.setFullName(dialog.getFullname());
			investigator.setInstitution(dialog.getInstitution());
			investigator.setNickName(dialog.getNickname());
			
			if(selection != null && selection instanceof IStructuredSelection)
			{
				Object element = ((IStructuredSelection) selection).getFirstElement();
				Project project = AddParticipantHandler.getProject(element);
				project.getInvestigators().add(investigator);
				investigator.setProject(project);
				
				view.getCommonViewer().refresh();
			}
		}
		//TODO add to db and open editor
		return null;
	}

}
