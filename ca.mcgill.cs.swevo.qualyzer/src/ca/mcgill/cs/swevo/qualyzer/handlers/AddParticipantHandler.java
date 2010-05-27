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
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.navigator.CommonNavigator;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.editors.ParticipantFormEditor;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.ParticipantEditorInput;
import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.providers.ProjectWrapper;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperParticipant;
import ca.mcgill.cs.swevo.qualyzer.wizards.AddParticipantWizard;

/**
 * Launches a wizard whenever the New Participant Command is clicked.
 * @author Jonathan Faubert (jonfaub@gmail.com)
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

			Project project = getProject(element);
			
			AddParticipantWizard wizard = new AddParticipantWizard(project);
			WizardDialog dialog = new WizardDialog(HandlerUtil.getActiveShell(event), wizard);
			
			if(dialog.open() == Window.OK)
			{
				view.getCommonViewer().refresh(new WrapperParticipant(project));
				openEditor(wizard.getParticipant(), page);
				//TODO open the editor by calling the command
			}
		}

		return null;
	}

	/**
	 * @param participant
	 * @param page
	 */
	private void openEditor(Participant participant, IWorkbenchPage page)
	{
		ParticipantEditorInput input = new ParticipantEditorInput(participant);
		try
		{
			page.openEditor(input, ParticipantFormEditor.ID);
		}
		catch (PartInitException e)
		{
			e.printStackTrace();
		}
	}
	
//	/**
//	 * @param participant
//	 * @param page
//	 * @throws ExecutionException 
//	 */
//	private void openEditor() throws ExecutionException
//	{
//		IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(
//				IHandlerService.class);
//		try
//		{
//			handlerService.executeCommand("ca.mcgill.cs.swevo.qualyzer.commands.editParticipant", null);
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

	/**
	 * @param element The object selected by the user.
	 * @return The project the object belongs to.
	 */
	public static Project getProject(Object element)
	{
		Project project = null;
		if(element instanceof IProject)
		{
			project = PersistenceManager.getInstance().getProject(((IProject) element).getName());
		}
		else if(element instanceof ProjectWrapper)
		{
			project = ((ProjectWrapper) element).getProject();
		}
		else 
		{
			project = checkBaseTypes(element);
		}
		return project;
	}
	
	private static Project checkBaseTypes(Object element)
	{
		Project project = null;
		
		if(element instanceof Code)
		{
			project = ((Code) element).getProject();
		}
		else if(element instanceof Participant)
		{
			project = ((Participant) element).getProject();
		}
		else if(element instanceof Investigator)
		{
			project = ((Investigator) element).getProject();
		}
		else if(element instanceof Transcript)
		{
			project = ((Transcript) element).getProject();
		}
		else if(element instanceof Memo)
		{
			project = ((Memo) element).getProject();
		}
		
		return project;
	}
	
	

}
