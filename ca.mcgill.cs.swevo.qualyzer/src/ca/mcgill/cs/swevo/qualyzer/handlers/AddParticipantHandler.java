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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.navigator.CommonNavigator;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.dialogs.AddParticipantDialog;
import ca.mcgill.cs.swevo.qualyzer.editors.ParticipantFormEditor;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.ParticipantEditorInput;
import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.HibernateDBManager;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.providers.ProjectWrapper;
import ca.mcgill.cs.swevo.qualyzer.util.HibernateUtil;

/**
 * Launches a dialog whenever the New Participant Command is clicked.
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
		
		AddParticipantDialog dialog = new AddParticipantDialog(HandlerUtil.getActiveWorkbenchWindow(event).getShell());
		dialog.create();
		if(dialog.open() == Window.OK)
		{
			Participant participant = new Participant();
			participant.setParticipantId(dialog.getParticipantId());
			participant.setFullName(dialog.getFullname());
			
			if (selection != null && selection instanceof IStructuredSelection)
			{
				IStructuredSelection strucSelection = (IStructuredSelection) selection;
				Object element = strucSelection.getFirstElement();

				Project project = getProject(element);
				
				project.getParticipants().add(participant);
				HibernateDBManager manager;
				manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(project.getName());
				HibernateUtil.quietSave(manager, project);
				view.getCommonViewer().refresh();
				openEditor(participant, page);
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

	/**
	 * @param element The object selected by the user.
	 * @return TODO
	 */
	private Project getProject(Object element)
	{
		Project project = null;
		
		if(element instanceof IProject)
		{
			String projectName = ((IProject) element).getName();
			project = PersistenceManager.getInstance().getProject(projectName);
		}
		else if(element instanceof ProjectWrapper)
		{
			project = ((ProjectWrapper) element).getProject();
		}
		else if(element instanceof Code)
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
		else
		{
			System.out.println("Error, selected:" +element.getClass().getName());
		}
		return project;
	}

}
