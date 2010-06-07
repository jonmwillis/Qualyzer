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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.navigator.CommonNavigator;
import org.hibernate.Session;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.dialogs.TranscriptDeleteDialog;
import ca.mcgill.cs.swevo.qualyzer.model.HibernateDBManager;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperCode;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperParticipant;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperTranscript;
import ca.mcgill.cs.swevo.qualyzer.util.HibernateUtil;

/**
 * Hander for the delete transcript command.
 *
 */
public class DeleteTranscriptHandler extends AbstractHandler
{
	private static final String TRANSCRIPT = File.separator + "transcripts" + File.separator; //$NON-NLS-1$

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		ISelection selection = page.getSelection();
		
		if(selection != null && selection instanceof IStructuredSelection)
		{
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if(element instanceof Transcript)
			{
				Transcript transcript = (Transcript) element;
				Project project = transcript.getProject();
				
				Shell shell = HandlerUtil.getActiveShell(event).getShell();
				
				TranscriptDeleteDialog dialog = new TranscriptDeleteDialog(shell);
				dialog.create();
				
				int check = dialog.open();
					
				if(check == Window.OK)
				{
					IEditorReference[] editors = page.getEditorReferences();
					for(IEditorReference editor : editors)
					{
						if(editor.getName().equals(transcript.getFileName()))
						{
							page.closeEditor(editor.getEditor(true), true);
						}
					}
					
					delete(transcript, dialog.getDeleteAudio(), dialog.getDeleteCodes(), 
							dialog.getDeleteParticipants());
										
					CommonNavigator view;
					view = (CommonNavigator) page.findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
					view.getCommonViewer().refresh(new WrapperTranscript(project));
					view.getCommonViewer().refresh(new WrapperParticipant(project));
					view.getCommonViewer().refresh(new WrapperCode(project));
				}
			}
		}
		return null;
	}

	/**
	 * @param transcript
	 * @param deleteAudio
	 * @param deleteCodes
	 * @param deleteParticipants
	 */
	private void delete(Transcript transcript, boolean deleteAudio, boolean deleteCodes, boolean deleteParticipants)
	{
		Project project = transcript.getProject();
		IProject wProject = ResourcesPlugin.getWorkspace().getRoot().getProject(project.getName());
		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(project.getName());
		
		if(deleteParticipants)
		{
			deleteParticipants(transcript, project, manager);
		}
		
		if(deleteCodes)
		{
			//TODO delete codes
		}
		
		if(deleteAudio)
		{
			File file = new File(wProject.getLocation() + transcript.getAudioFile().getRelativePath());
			file.delete();
		}
		
		File file = new File(wProject.getLocation() + TRANSCRIPT + transcript.getFileName());
		file.delete();
		
		project.getTranscripts().remove(transcript);
		transcript.setProject(null);
		transcript.setFragments(null);
		transcript.setParticipants(null);
		transcript.setAudioFile(null);
		
		HibernateUtil.quietSave(manager, transcript);
		HibernateUtil.quietSave(manager, project);
	}

	/**
	 * @param transcript
	 * @param project
	 * @param manager 
	 */
	private void deleteParticipants(Transcript transcript, Project project, HibernateDBManager manager)
	{
		Session session = manager.openSession();
		
		Object lTranscript = session.get(Transcript.class, transcript.getPersistenceId());
		for(Participant participant : ((Transcript) lTranscript).getParticipants())
		{
			boolean found = false;
			for(Transcript otherTranscript : project.getTranscripts())
			{
				if(!otherTranscript.equals(transcript))
				{
					Object lOtherTranscript = session.get(Transcript.class, otherTranscript.getPersistenceId());
					
					for(Participant otherParticipant : ((Transcript) lOtherTranscript).getParticipants())
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
				project.getParticipants().remove(participant);
				participant.setProject(null);
			}
		}
		
		session.close();
	}

}
