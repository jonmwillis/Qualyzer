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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.navigator.CommonNavigator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.dialogs.TranscriptDeleteDialog;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;

/**
 * Hander for the delete transcript command.
 *
 */
public class DeleteTranscriptHandler extends AbstractHandler
{
	private static final String TRANSCRIPT = File.separator + "transcripts" + File.separator; //$NON-NLS-1$

	private final Logger fLogger = LoggerFactory.getLogger(DeleteTranscriptHandler.class);
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		ISelection selection = page.getSelection();
		Shell shell = HandlerUtil.getActiveShell(event).getShell();
		
		if(selection != null && selection instanceof IStructuredSelection)
		{
			List<Transcript> toDelete = new ArrayList<Transcript>();
			List<Project> projects = new ArrayList<Project>();
			
			for(Object element : ((IStructuredSelection) selection).toArray())
			{
				if(element instanceof Transcript)
				{
					Transcript transcript = (Transcript) element;
					
					if(!projects.contains(transcript.getProject()))
					{
						projects.add(transcript.getProject());
					}
					
					toDelete.add(transcript);	
				}
			}
			
			if(projects.size() > 1)
			{
				String warningMessage = Messages.getString(
						"handlers.DeleteTranscriptHandler.multipleProjects"); //$NON-NLS-1$
				fLogger.warn(warningMessage);
				MessageDialog.openError(shell, Messages.getString(
						"handlers.DeleteTranscriptHandler.deleteFailed"), warningMessage); //$NON-NLS-1$
			}
			else
			{
				proceedWithDeletion(page, shell, toDelete);
			}
		}
		return null;
	}
	
	/**
	 * @param page
	 * @param shell
	 * @param toDelete
	 */
	private void proceedWithDeletion(IWorkbenchPage page, Shell shell, List<Transcript> toDelete)
	{	
		TranscriptDeleteDialog dialog = new TranscriptDeleteDialog(shell);
		dialog.create();
		
		int check = dialog.open();
			
		if(check == Window.OK)
		{	
			for(Transcript transcript : toDelete)
			{
				delete(transcript, dialog.getDeleteAudio(), dialog.getDeleteCodes(), 
						dialog.getDeleteParticipants(), shell);
									
				CommonNavigator view;
				view = (CommonNavigator) page.findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
				view.getCommonViewer().refresh();
			}
		}
		
	}

	/**
	 * @param transcript
	 * @param deleteAudio
	 * @param deleteCodes
	 * @param deleteParticipants
	 */
	private void delete(Transcript transcript, boolean deleteAudio, boolean deleteCodes, boolean deleteParticipants,
			Shell shell)
	{
		Project project = transcript.getProject();
		IProject wProject = ResourcesPlugin.getWorkspace().getRoot().getProject(project.getName());
		ArrayList<Participant> participants = null;
		
		if(deleteParticipants)
		{
			participants = deleteParticipants(transcript, project);
		}
		
		if(deleteCodes)
		{
			//TODO delete codes
		}
		
		if(deleteAudio && transcript.getAudioFile() != null)
		{
			File file = new File(wProject.getLocation() + transcript.getAudioFile().getRelativePath());
			if(!file.delete())
			{
				String warningMessage = Messages.getString(
						"handlers.DeleteTranscriptHandler.audioDeleteFailed"); //$NON-NLS-1$
				fLogger.warn(warningMessage);
				MessageDialog.openWarning(shell, Messages.getString(
						"handlers.DeleteTranscriptHandler.fileAccess"), warningMessage); //$NON-NLS-1$
			}
		}
		
		File file = new File(wProject.getLocation() + TRANSCRIPT + transcript.getFileName());
		if(!file.delete())
		{
			String warningMessage = Messages.getString(
					"handlers.DeleteTranscriptHandler.transcriptDeleteFailed"); //$NON-NLS-1$
			fLogger.warn(warningMessage);
			MessageDialog.openWarning(shell, Messages.getString(
					"handlers.DeleteTranscriptHandler.fileAccess"), warningMessage); //$NON-NLS-1$
		}
		
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		ResourcesUtil.closeEditor(page, transcript.getFileName());
		Facade.getInstance().deleteTranscript(transcript);
		//TODO delete annotations?
		if(participants != null)
		{
			for(Participant p : participants)
			{
				Facade.getInstance().deleteParticipant(p);
			}
		}
	}

	/**
	 * @param transcript
	 * @param project
	 * @param manager 
	 */
	private ArrayList<Participant> deleteParticipants(Transcript transcript, Project project)
	{
		ArrayList<Participant> toDelete = new ArrayList<Participant>();
				
		Transcript lTranscript = Facade.getInstance().forceTranscriptLoad(transcript);
		for(Participant participant : lTranscript.getParticipants())
		{
			boolean found = false;
			for(Transcript otherTranscript : project.getTranscripts())
			{
				if(!otherTranscript.equals(transcript))
				{
					Transcript lOtherTranscript = Facade.getInstance().forceTranscriptLoad(otherTranscript);
					
					for(Participant otherParticipant : lOtherTranscript.getParticipants())
					{
						if(otherParticipant.equals(participant))
						{
							found = true;
							break;
						}
					}
				}
			}
			if(!found)
			{
				toDelete.add(participant);
			}
		}
				
		return toDelete;
	}

}