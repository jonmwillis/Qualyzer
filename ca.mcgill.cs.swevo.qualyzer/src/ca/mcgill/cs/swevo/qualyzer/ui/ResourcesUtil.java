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
package ca.mcgill.cs.swevo.qualyzer.ui;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.mcgill.cs.swevo.qualyzer.editors.CodeEditor;
import ca.mcgill.cs.swevo.qualyzer.editors.InvestigatorFormEditor;
import ca.mcgill.cs.swevo.qualyzer.editors.ParticipantFormEditor;
import ca.mcgill.cs.swevo.qualyzer.editors.RTFEditor;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.CodeEditorInput;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.InvestigatorEditorInput;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.ParticipantEditorInput;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.RTFEditorInput;
import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.providers.ProjectWrapper;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperCode;

/**
 * Contains methods for the opening of editors and retrieving the Project
 * given any of it's sub-elements.
 *
 */
public final class ResourcesUtil
{
	/**
	 * 
	 */
	private static final String ERROR_MSG = "Could not open editor"; //$NON-NLS-1$
	private static Logger gLogger = LoggerFactory.getLogger(ResourcesUtil.class);

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
			gLogger.error(ERROR_MSG, e);
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
			gLogger.error(ERROR_MSG, e);
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
		try
		{
			file.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		}
		catch (CoreException e)
		{
			gLogger.error(ERROR_MSG, e);
		}
		
		if(!file.exists())
		{
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Messages.getString("ui.ResourcesUtil.fileError"), //$NON-NLS-1$
					Messages.getString("ui.ResourcesUtil.transcriptMissing")); //$NON-NLS-1$ 
			return;
		}
		String ext = file.getFileExtension();
		
		try
		{
			if(ext.equals("rtf") || ext.equals("txt"))
			{
				Transcript lTranscript = Facade.getInstance().forceTranscriptLoad(transcript);
				RTFEditorInput editorInput = new RTFEditorInput(file, lTranscript);
				page.openEditor(editorInput, RTFEditor.ID);
			}
		}
		catch (PartInitException e)
		{
			gLogger.error(ERROR_MSG, e);
		}
	}
	
	/**
	 * Verifies that an id is valid.
	 * An id is valid if and only if it contains only alpha-numeric characters and '_' or '-'.
	 * @param id 
	 * @return 
	 */
	public static boolean verifyID(String id)
	{
		for(int i = 0; i < id.length(); i++)
		{
			char c = id.charAt(i);
			if((c <= 'Z' && c >= 'A') || (c >= 'a' && c <= 'z')) //isAlpha
			{
				continue;
			}
			else if(c >= '0' && c <= '9' || c == '_' || c == '-') //is digit or _ or -
			{
				continue;
			}
			else
			{
				return false;
			}
		}
		
		return id.length() > 0;
	}
	
	/**
	 * Closes the editor with the given name if it's open.
	 * @param page
	 * @param editorName
	 */
	public static void closeEditor(IWorkbenchPage page, String editorName)
	{
		IEditorReference[] editors = page.getEditorReferences();
		for(IEditorReference editor : editors)
		{
			if(editor.getName().equals(editorName))
			{
				page.closeEditor(editor.getEditor(true), true);
			}
		}		
	}

	/**
	 * Open a code editor.
	 * @param page
	 * @param project
	 */
	public static void openEditor(IWorkbenchPage page, WrapperCode wrapperCode)
	{
		CodeEditorInput input = new CodeEditorInput(wrapperCode.getProject());
		try
		{
			page.openEditor(input, CodeEditor.ID);
		}
		catch(PartInitException e)
		{
			gLogger.error("Failed to open code editor.", e); //$NON-NLS-1$
		}
	}
}
