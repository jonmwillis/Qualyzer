/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jonathan Faubert
 *     Martin Robillard
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.handlers;

import java.util.ArrayList;
import java.util.List;

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
import ca.mcgill.cs.swevo.qualyzer.editors.ParticipantFormEditor;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.HibernateDBManager;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;

/**
 * The handler for the delete participant command.
 * Multiple participants can be deleted at once, but the operation is atomic.
 * Either all the selected participants are deleted, or none.
 */
public class DeleteParticipantHandler extends AbstractHandler
{
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		ISelection selection = page.getSelection();
		Shell shell = HandlerUtil.getActiveShell(event).getShell();
		
		if(selection != null && selection instanceof IStructuredSelection)
		{
			List<String> conflicts = new ArrayList<String>();
			List<Participant> toDelete = new ArrayList<Participant>();
			List<Project> projects = new ArrayList<Project>();
			for(Object element : ((IStructuredSelection) selection).toArray())
			{				
				if(element instanceof Participant)
				{
					Participant participant = (Participant) element;
					Project project = participant.getProject();
					
					if(!projects.contains(project))
					{
						projects.add(project);
					}
					
					HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers()
						.get(project.getName());
					
					conflicts.addAll(checkForConflicts(participant, project, manager));
					toDelete.add(participant);
				}
			}
			
			if(projects.size() > 1)
			{
				MessageDialog.openError(shell, "Unable to delete",
						"Cannot delete participants across multiple projects.");
			}
			else if(conflicts.size() > 0)
			{
				String errorMsg = printErrors(conflicts);
				MessageDialog.openError(shell, Messages.getString(
						"handlers.DeleteParticipantHandler.cannotDelete"), errorMsg); //$NON-NLS-1$
			}
			else
			{
				proceedWithDeletion(page, shell, toDelete);
			}
		}
		return null;
	}

	private void proceedWithDeletion(IWorkbenchPage page, Shell shell, List<Participant> toDelete) 
	{
		String message = "";
		if(toDelete.size() == 1)
		{
			message = Messages.getString("handlers.DeleteParticipantHandler.confirm");
		}
		else
		{
			message = Messages.getString("handlers.DeleteParticipantHandler.confirmMany");
		}
		
		boolean check = MessageDialog.openConfirm(shell, Messages.getString(
				"handlers.DeleteParticipantHandler.deleteParticipant"),  //$NON-NLS-1$
				message); //$NON-NLS-1$
		
		if(check)
		{
			for(Participant participant : toDelete)
			{	
				IEditorReference[] editors = page.getEditorReferences();
				for(IEditorReference editor : editors)
				{
					if(editor.getEditor(true) instanceof ParticipantFormEditor && 
							editor.getName().equals(participant.getParticipantId()))
					{
						page.closeEditor(editor.getEditor(true), true);
					}
				}
				
				Facade.getInstance().deleteParticipant(participant);	
			}
			CommonNavigator view;
			view = (CommonNavigator) page.findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
			view.getCommonViewer().refresh();
		}
	}

	/**
	 * Formats the list of conflicts into a message string.
	 */
	private String printErrors(List<String> conflicts)
	{
		String output = Messages.getString("handlers.DeleteParticipantHandler.conflicts"); //$NON-NLS-1$
		for(Object conflict : conflicts)
		{
			output += "\n" + conflict; 
		}
		
		return output;
	}

	/**
	 * Determines if the participant is associated with either a memo or a transcript.
	 * Such an association constituted a conflict.
	 * @param participant
	 * @param project
	 * @param session
	 * @return A list of strings describing the conflict.
	 */
	private ArrayList<String> checkForConflicts(Participant participant, Project project, HibernateDBManager manager)
	{
		Session session = manager.openSession();
		ArrayList<String> conflicts = new ArrayList<String>();
		try
		{
			for(Memo memo : project.getMemos())
			{
				Object lMemo = session.get(Memo.class, memo.getPersistenceId());
				for(Participant part : ((Memo) lMemo).getParticipants())
				{
					if(part.equals(participant))
					{
						conflicts.add(Messages.getString("handlers.DeleteParticipantHandler.participant") +  
								participant.getParticipantId() + " " + 
								Messages.getString("handlers.DeleteParticipantHandler.memo") +  
								memo.getName());
						break;
					}
				}
			}
		
			for(Transcript transcript : project.getTranscripts())
			{
				Transcript lTranscript = Facade.getInstance().forceTranscriptLoad(transcript);
				for(Participant part : lTranscript.getParticipants())
				{
					if(part.equals(participant))
					{
						conflicts.add(Messages.getString("handlers.DeleteParticipantHandler.participant") +  
								participant.getParticipantId() + " " +
								Messages.getString("handlers.DeleteParticipantHandler.transcript") +  
								transcript.getName());
						break;
					}
				}
			}
		}
		finally
		{
			session.close();
		}
		return conflicts;
	}

}
