/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     -Barthelemy Dagenais (bart@cs.mcgill.ca)
 *     -Jonathan Faubert
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.handlers;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.navigator.CommonNavigator;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.dialogs.RenameDialog;
import ca.mcgill.cs.swevo.qualyzer.model.AudioFile;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;

/**
 * Qualyzer handler for rename (F2).
 *  
 *
 */
public class RenameHandler extends AbstractHandler
{
	/**
	 * 
	 */
	private static final String EXT = ".rtf"; //$NON-NLS-1$
	private static final String TRANSCRIPT = File.separator+"transcripts"+File.separator; //$NON-NLS-1$
	private static final String AUDIO = File.separator+"audio"+File.separator; //$NON-NLS-1$

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		ISelection selection = page.getSelection();
		CommonNavigator view = (CommonNavigator) page.findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
		
		if(selection != null && selection instanceof IStructuredSelection)
		{
			Object element = ((IStructuredSelection) selection).getFirstElement();
			Project project = ResourcesUtil.getProject(element);

			RenameDialog dialog = new RenameDialog(HandlerUtil.getActiveShell(event).getShell(), project);

			dialog.create();
			dialog.setCurrentName(((Transcript) element).getName());
			
			if(trancriptNoLongerExists((Transcript) element))
			{
				MessageDialog.openError(HandlerUtil.getActiveShell(event), 
						Messages.getString("handlers.RenameHandler.fileError"),  //$NON-NLS-1$
						Messages.getString("handlers.RenameHandler.transcriptRenamed")); //$NON-NLS-1$
				return null;
			}
			
			if(dialog.open() == Window.OK)
			{
				if(element instanceof Transcript)
				{
					rename((Transcript) element, dialog.getName(), dialog.getChangeAudio());
					
					Facade.getInstance().saveTranscript((Transcript) element);	
				}
				view.getCommonViewer().refresh();
			}
		}
		return null;
	}

	/**
	 * @param element
	 * @return
	 */
	private boolean trancriptNoLongerExists(Transcript element)
	{
		String projectName = element.getProject().getName();
		String projectPath = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).getLocation().toString();
		
		File file = new File(projectPath+TRANSCRIPT+element.getFileName());
		
		return !file.exists();
	}

	/**
	 * @param transcript
	 * @param name
	 * @param changeAudio
	 */
	private void rename(Transcript transcript, String name, boolean changeAudio)
	{	
		boolean closed = false;
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorReference[] editors = activePage.getEditorReferences();
		for(IEditorReference editor : editors)
		{
			if(editor.getName().equals(transcript.getFileName()))
			{
				activePage.closeEditor(editor.getEditor(true), true);
				closed = true;
			}
		}
		
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(transcript.getProject().getName());
		
		String projectPath = project.getLocation().toString();
		File origFile = new File(projectPath + TRANSCRIPT + transcript.getFileName());
		File newFile = new File(projectPath + TRANSCRIPT + name + EXT);
		
		origFile.renameTo(newFile);
		
		if(changeAudio)
		{
			AudioFile audio = transcript.getAudioFile();
			if(audio != null)
			{
				origFile = new File(projectPath + audio.getRelativePath());
				
				if(!origFile.exists())
				{
					origFile = getNewAudioFile(projectPath);
				}
				
				String audioExt = audio.getRelativePath().substring(audio.getRelativePath().lastIndexOf('.'));
				newFile = new File(projectPath + AUDIO + name + audioExt);
				
				origFile.renameTo(newFile);
				
				audio.setRelativePath(AUDIO + name + audioExt);
			}
		}
		
		transcript.setName(name);
		transcript.setFileName(name+EXT);
		
		if(closed)
		{
			ResourcesUtil.openEditor(activePage, transcript);
		}
	}

	/**
	 * @param projectPath
	 * @return
	 */
	private File getNewAudioFile(String projectPath)
	{
		File origFile;
		MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
				Messages.getString("handlers.RenameHandler.fileError"), //$NON-NLS-1$
				Messages.getString("handlers.RenameHandler.audioFileGone")); //$NON-NLS-1$ 
		
		FileDialog dialog = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		dialog.setFilterPath(projectPath+AUDIO);
		dialog.setFilterExtensions(new String[]{"*.mp3;*.wav"}); //$NON-NLS-1$
		dialog.setFilterNames(new String[]{Messages.getString(
				"handlers.ImportAudioFileHandler.audioExt")}); //$NON-NLS-1$
		
		String fileName = dialog.open();
		origFile = new File(fileName);
		return origFile;
	}

}
