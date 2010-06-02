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

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.part.FileEditorInput;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.editors.TranscriptEditor;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperTranscript;
import ca.mcgill.cs.swevo.qualyzer.wizards.NewTranscriptWizard;

/**
 * 
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class NewTranscriptHandler extends AbstractHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		ISelection selection = page.getSelection();
		
		if(selection != null && selection instanceof IStructuredSelection)
		{
			Object element = ((IStructuredSelection) selection).getFirstElement();
			Project project = AddParticipantHandler.getProject(element);

			NewTranscriptWizard wizard = new NewTranscriptWizard(project);
			WizardDialog dialog = new WizardDialog(HandlerUtil.getActiveShell(event), wizard);
			
			if(dialog.open() == WizardDialog.OK)
			{
				CommonNavigator view = (CommonNavigator) page.findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
				view.getCommonViewer().refresh(new WrapperTranscript(wizard.getTranscript().getProject()));
				openEditor(page, wizard.getTranscript());
			}
		}

		return null;
	}

	/**
	 * @param page
	 * @param transcript
	 */
	private void openEditor(IWorkbenchPage page, Transcript transcript)
	{
		IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(transcript.getProject().getName());
		IFile file = proj.getFile("transcripts" + File.separator + transcript.getFileName());
		FileEditorInput editorInput = new FileEditorInput(file);
		try
		{
			page.openEditor(editorInput, TranscriptEditor.ID);
		}
		catch (PartInitException e)
		{
			e.printStackTrace();
		}
	}

}
