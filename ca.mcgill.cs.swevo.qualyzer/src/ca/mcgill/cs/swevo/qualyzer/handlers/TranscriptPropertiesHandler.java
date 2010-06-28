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
import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import ca.mcgill.cs.swevo.qualyzer.dialogs.TranscriptPropertiesDialog;
import ca.mcgill.cs.swevo.qualyzer.model.AudioFile;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;
import ca.mcgill.cs.swevo.qualyzer.util.FileUtil;

/**
 * 
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class TranscriptPropertiesHandler extends AbstractHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
		
		if(selection != null && selection instanceof IStructuredSelection)
		{
			Object element = ((IStructuredSelection) selection).getFirstElement();
			TranscriptPropertiesDialog dialog;
			dialog = new TranscriptPropertiesDialog(HandlerUtil.getActiveShell(event).getShell(), (Transcript) element);
			
			dialog.create();
			if(dialog.open() == Window.OK)
			{
				Transcript transcript = (Transcript) element;
				String projectName = transcript.getProject().getName();
				String newDate = dialog.getDate();
				String audioFile = dialog.getAudioFile();
				
				String oldAudio = ""; //$NON-NLS-1$
				if(transcript.getAudioFile() != null)
				{
					String projectPath = ResourcesPlugin.getWorkspace().getRoot()
						.getProject(projectName).getLocation() + ""; //$NON-NLS-1$
					
					oldAudio = projectPath + transcript.getAudioFile().getRelativePath();
				}
				
				copyNewAudioFile(transcript, audioFile, oldAudio);
								
				transcript.setDate(newDate);
				transcript.setParticipants(dialog.getParticipants());
								
				Facade.getInstance().saveTranscript(transcript);
				
				ResourcesUtil.refreshParticipants(transcript.getProject());
			}
		}
		
		return null;
	}

	/**
	 * @param transcript
	 * @param audioFile
	 * @param oldAudio
	 */
	private void copyNewAudioFile(Transcript transcript, String audioFile, String oldAudio)
	{
		if(!oldAudio.equals(audioFile))
		{
			if(!audioFile.isEmpty())
			{
				AudioFile aFile = new AudioFile();
				String relativePath = File.separator+"audio"+File.separator; //$NON-NLS-1$
				relativePath += transcript.getName() + audioFile.substring(audioFile.lastIndexOf('.'));
				aFile.setRelativePath(relativePath);
				transcript.setAudioFile(aFile);
						
				String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation()+File.separator;
				File input = new File(audioFile);
				String dest = workspacePath + File.separator + transcript.getProject().getName();
				dest = dest + relativePath;
				File output = new File(dest);
				try
				{
					FileUtil.copyFile(input, output);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				transcript.setAudioFile(null);
			}
		}
	}

}
