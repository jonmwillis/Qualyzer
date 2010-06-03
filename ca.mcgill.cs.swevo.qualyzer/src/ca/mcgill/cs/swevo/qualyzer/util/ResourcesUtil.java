/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jonathan Faubert (jonfaub@gmail.com)
 *******************************************************************************/
/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.util;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;

import ca.mcgill.cs.swevo.qualyzer.editors.InvestigatorFormEditor;
import ca.mcgill.cs.swevo.qualyzer.editors.ParticipantFormEditor;
import ca.mcgill.cs.swevo.qualyzer.editors.TranscriptEditor;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.InvestigatorEditorInput;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.ParticipantEditorInput;
import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.providers.ProjectWrapper;

/**
 * Contains methods for the opening of editors and retrieving the Project
 * given any of it's sub-elements.
 *
 */
public final class ResourcesUtil
{

	private ResourcesUtil(){}
	
	/**
	 * Open the Investigator Editor.
	 * @param page
	 * @param investigator
	 */
	public static void openEditor(IWorkbenchPage page, Investigator investigator)
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
	
	/**
	 * Open the Participant Editor.
	 * @param page
	 * @param participant
	 */
	public static void openEditor(IWorkbenchPage page, Participant participant)
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
	
	/**
	 * Open the Transcript editor.
	 * @param page
	 * @param transcript
	 */
	public static void openEditor(IWorkbenchPage page, Transcript transcript)
	{
		IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(transcript.getProject().getName());
		IFile file = proj.getFile("transcripts" + File.separator + transcript.getFileName()); //$NON-NLS-1$
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
