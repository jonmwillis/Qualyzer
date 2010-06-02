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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.model.AudioFile;
import ca.mcgill.cs.swevo.qualyzer.model.HibernateDBManager;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.util.FileUtil;
import ca.mcgill.cs.swevo.qualyzer.util.HibernateUtil;

/**
 * 
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class ImportAudioFileHandler extends AbstractHandler
{

	/**
	 * 
	 */
	private static final String AUDIO = File.separator+"audio"+File.separator;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
		if(selection != null)
		{
			IStructuredSelection strucSelection = (IStructuredSelection) selection;
			Object element = strucSelection.getFirstElement();
			if(element instanceof Transcript)
			{
				String projectName = ((Transcript) element).getProject().getName();
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
				String path =  project.getLocation() + AUDIO;
				
				String fileName = openFileDialog(event);
				if(fileName != null)
				{
					String ext = fileName.substring(fileName.lastIndexOf('.'));
					copyFile(element, path, fileName, ext);
					
					AudioFile audioFile = new AudioFile();
					audioFile.setRelativePath(AUDIO + ((Transcript) element).getName() + ext);
					((Transcript) element).setAudioFile(audioFile);
					
					HibernateDBManager manager;
					manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(projectName);
					HibernateUtil.quietSave(manager, (Transcript) element);
				}
			}
		}
		return null;
	}

	/**
	 * @param event
	 * @return
	 */
	private String openFileDialog(ExecutionEvent event)
	{
		FileDialog dialog = new FileDialog(HandlerUtil.getActiveShell(event));
		dialog.setFilterExtensions(new String[]{"*.mp3;*.wav"});
		dialog.setFilterNames(new String[]{"Audio (.mp3, .wav)"});
		
		String fileName = dialog.open();
		return fileName;
	}

	/**
	 * @param element
	 * @param path
	 * @param fileName
	 * @param ext
	 */
	private void copyFile(Object element, String path, String fileName, String ext)
	{
		File file = new File(fileName);
		File fileCpy = new File(path+((Transcript) element).getName()+ext);
		if(!fileCpy.exists())
		{
			try
			{
				FileUtil.copyFile(file, fileCpy);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

}
