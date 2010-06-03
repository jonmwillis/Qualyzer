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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.navigator.CommonNavigator;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.dialogs.RenameDialog;
import ca.mcgill.cs.swevo.qualyzer.model.AudioFile;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperTranscript;

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
	private static final String EXT = ".txt"; //$NON-NLS-1$
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
			Project project = AddParticipantHandler.getProject(element);

			RenameDialog dialog = new RenameDialog(HandlerUtil.getActiveShell(event).getShell(), project);

			dialog.create();
			
			if(dialog.open() == Window.OK)
			{
				if(element instanceof Transcript)
				{
					rename((Transcript) element, dialog.getName(), dialog.getChangeAudio());
				}
				view.getCommonViewer().refresh(new WrapperTranscript(project));
			}
		}
		return null;
	}

	/**
	 * @param element
	 * @param name
	 * @param changeAudio
	 */
	private void rename(Transcript element, String name, boolean changeAudio)
	{	
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(element.getProject().getName());
		
		String projectPath = project.getLocation().toString();
		File origFile = new File(projectPath + TRANSCRIPT + element.getFileName());
		File newFile = new File(projectPath + TRANSCRIPT + name + EXT);
		
		origFile.renameTo(newFile);
		
		if(changeAudio)
		{
			AudioFile audio = element.getAudioFile();
			if(audio != null)
			{
				origFile = new File(projectPath + audio.getRelativePath());
				String audioExt = audio.getRelativePath().substring(audio.getRelativePath().lastIndexOf('.'));
				newFile = new File(projectPath + AUDIO + name + audioExt);
				
				origFile.renameTo(newFile);
				
				audio.setRelativePath(AUDIO + name + audioExt);
			}
		}
		
		element.setName(name);
		element.setFileName(name+EXT);
	}

}
