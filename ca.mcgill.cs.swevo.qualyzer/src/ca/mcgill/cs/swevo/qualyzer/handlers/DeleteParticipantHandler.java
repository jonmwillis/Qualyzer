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

import java.util.ArrayList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.navigator.CommonNavigator;
import org.hibernate.Session;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.model.HibernateDBManager;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperParticipant;
import ca.mcgill.cs.swevo.qualyzer.util.HibernateUtil;

/**
 * The handler for the delete participant command.
 *
 */
public class DeleteParticipantHandler extends AbstractHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		ISelection selection = page.getSelection();
		
		if(selection != null && selection instanceof IStructuredSelection)
		{
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if(element instanceof Participant)
			{
				Participant participant = (Participant) element;
				Project project = participant.getProject();
				
				HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers()
					.get(project.getName());
				ArrayList<Object> conflicts = checkForConflicts(participant, project, manager.openSession());
				
				Shell shell = HandlerUtil.getActiveShell(event).getShell();
				if(conflicts.size() > 0)
				{
					String errorMsg = printErrors(conflicts);
					MessageDialog.openError(shell, "Cannot Delete Participant", errorMsg);
				}
				else
				{
					boolean check = MessageDialog.openConfirm(shell, "Delete this Participant", 
							"Are you sure you want to delete this participant?");
					
					if(check)
					{
						for(IEditorReference editor : page.getEditorReferences())
						{
							if(editor.getName().equals(participant.getParticipantId()))
							{
								page.closeEditor(editor.getEditor(true), true);
							}
						}
						project.getParticipants().remove(participant);
						participant.setProject(null);
						
						HibernateUtil.quietSave(manager, project);
			
						CommonNavigator view;
						view = (CommonNavigator) page.findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
						view.getCommonViewer().refresh(new WrapperParticipant(project));
					}
				}
			}
		}
		return null;
	}

	/**
	 * @param conflicts
	 */
	private String printErrors(ArrayList<Object> conflicts)
	{
		String output = "Cannot delete participant due to the following conflicts:";
		for(Object obj : conflicts)
		{
			if(obj instanceof Memo)
			{
				output += "\nMemo: " + ((Memo) obj).getName();
			}
			else if(obj instanceof Transcript)
			{
				output += "\nTranscript: " + ((Transcript) obj).getName();
			}
		}
		
		return output;
	}

	/**
	 * @param participant
	 * @param project
	 * @param session
	 * @return
	 */
	private ArrayList<Object> checkForConflicts(Participant participant, Project project, Session session)
	{
		ArrayList<Object> conflicts = new ArrayList<Object>();
		for(Memo memo : project.getMemos())
		{
			Object lMemo = session.get(Memo.class, memo.getPersistenceId());
			for(Participant part : ((Memo) lMemo).getParticipants())
			{
				if(part.equals(participant))
				{
					conflicts.add(memo);
					break;
				}
			}
		}
		
		for(Transcript transcript : project.getTranscripts())
		{
			Object lTranscript = session.get(Transcript.class, transcript.getPersistenceId());
			for(Participant part : ((Transcript) lTranscript).getParticipants())
			{
				if(part.equals(participant))
				{
					conflicts.add(transcript);
					break;
				}
			}
		}
		
		return conflicts;
	}

}