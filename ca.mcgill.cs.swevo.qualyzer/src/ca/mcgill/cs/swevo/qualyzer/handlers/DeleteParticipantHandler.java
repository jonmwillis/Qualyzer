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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.navigator.CommonNavigator;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
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
					
					conflicts.addAll(checkForConflicts(participant, project));
					toDelete.add(participant);
				}
			}
			
			if(projects.size() > 1)
			{
				MessageDialog.openError(shell, 
						Messages.getString("handlers.DeleteParticipantHandler.deleteFailed"), //$NON-NLS-1$
						Messages.getString("handlers.DeleteParticipantHandler.multipleProjects")); //$NON-NLS-1$
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
		String message = ""; //$NON-NLS-1$
		if(toDelete.size() == 1)
		{
			message = Messages.getString("handlers.DeleteParticipantHandler.confirm"); //$NON-NLS-1$
		}
		else
		{
			message = Messages.getString("handlers.DeleteParticipantHandler.confirmMany"); //$NON-NLS-1$
		}
		
		boolean check = MessageDialog.openConfirm(shell, Messages.getString(
				"handlers.DeleteParticipantHandler.deleteParticipant"),  //$NON-NLS-1$
				message); //$NON-NLS-1$
		
		if(check)
		{
			CommonNavigator view;
			view = (CommonNavigator) page.findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
			for(Participant participant : toDelete)
			{	
				Facade.getInstance().deleteParticipant(participant);
			}
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
			output += "\n" + conflict;  //$NON-NLS-1$
		}
		
		return output;
	}

	/**
	 * Determines if the participant is associated with either a memo or a transcript.
	 * Such an association constitutes a conflict.
	 * @param participant
	 * @param project
	 * @param session
	 * @return A list of strings describing the conflict.
	 */
	private ArrayList<String> checkForConflicts(Participant participant, Project project)
	{
		ArrayList<String> conflicts = new ArrayList<String>();
		for(Memo memo : project.getMemos())
		{
			Memo lMemo = Facade.getInstance().forceMemoLoad(memo);
			for(Participant part : ((Memo) lMemo).getParticipants())
			{
				if(part.equals(participant))
				{
					conflicts.add(Messages.getString("handlers.DeleteParticipantHandler.participant") +   //$NON-NLS-1$
							participant.getParticipantId() + " " +  //$NON-NLS-1$
							Messages.getString("handlers.DeleteParticipantHandler.memo") +   //$NON-NLS-1$
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
					conflicts.add(Messages.getString("handlers.DeleteParticipantHandler.participant") +   //$NON-NLS-1$
							participant.getParticipantId() + " " + //$NON-NLS-1$
							Messages.getString("handlers.DeleteParticipantHandler.transcript") +   //$NON-NLS-1$
							transcript.getName());
					break;
				}
			}
		}

		return conflicts;
	}

}
