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
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.navigator.CommonNavigator;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.editors.InvestigatorFormEditor;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.InvestigatorEditorInput;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperInvestigator;
import ca.mcgill.cs.swevo.qualyzer.wizards.AddInvestigatorWizard;

/**
 * Launched a wizard whenever the new Investigator Command is clicked.
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
		
		if(selection != null && selection instanceof IStructuredSelection)
		{
			Object element = ((IStructuredSelection) selection).getFirstElement();
			Project project = AddParticipantHandler.getProject(element);
		
			AddInvestigatorWizard wizard = new AddInvestigatorWizard(project);
			WizardDialog dialog = new WizardDialog(HandlerUtil.getActiveShell(event), wizard);
		
			if(dialog.open() == Window.OK)
			{
				view.getCommonViewer().refresh(new WrapperInvestigator(project)); //change to wizard.getInvestigator() once equality is finished
				openEditor(wizard.getInvestigator(), page);
				//TODO Open the editor by calling the command
			}
		}

		return null;
	}

	/**
	 * @param investigator
	 * @param page
	 */
	private void openEditor(Investigator investigator, IWorkbenchPage page)
	{
		InvestigatorEditorInput input = new InvestigatorEditorInput(investigator);
		try
		{
			page.openEditor(input, InvestigatorFormEditor.ID);
		}
		catch (PartInitException e)
		{
			e.printStackTrace();
		}		
	}
	
//	/**
//	 * @param investigator
//	 * @param page
//	 * @throws ExecutionException 
//	 */
//	private void openEditor() throws ExecutionException
//	{
//		IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(
//				IHandlerService.class);
//		try
//		{
//			handlerService.executeCommand("ca.mcgill.cs.swevo.qualyzer.commands.editInvestigator", null);
//		}
//		catch (NotDefinedException e)
//		{
//			e.printStackTrace();
//		}
//		catch (NotEnabledException e)
//		{
//			e.printStackTrace();
//		}
//		catch (NotHandledException e)
//		{
//			e.printStackTrace();
//		}	
//	}

}
