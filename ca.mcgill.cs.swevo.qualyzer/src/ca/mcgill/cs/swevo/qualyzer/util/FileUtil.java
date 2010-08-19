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
/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.QualyzerException;
import ca.mcgill.cs.swevo.qualyzer.model.HibernateDBManager;

/**
 *
 */
public final class FileUtil
{
	public static final String ACTIVE_INV = "activeInv"; //$NON-NLS-1$
	private static final String DELIMITER = ";"; //$NON-NLS-1$
	private static final String EQUAL = "="; //$NON-NLS-1$

	private static final String MEMOS = "memos"; //$NON-NLS-1$
	private static final String TRANSCRIPTS = "transcripts"; //$NON-NLS-1$
	private static final String AUDIO = "audio"; //$NON-NLS-1$
	private static Logger gLogger = LoggerFactory.getLogger(FileUtil.class);

	private FileUtil(){}
	
	/**
	 * Get the value of the specified property in the project's .project file.
	 * @param project
	 * @param property
	 * @return
	 * @throws CoreException
	 */
	public static String getProjectProperty(IProject project, String property) throws CoreException
	{	
		String comment = project.getDescription().getComment();
		String[] pairs = comment.split(DELIMITER);
		for(String pair : pairs)
		{
			String[] keyValue = pair.split(EQUAL);
			if(keyValue.length == 2 && keyValue[0].equals(property))
			{
				return keyValue[1];
			}
		}
		
		return ""; //$NON-NLS-1$
	}
	
	/**
	 * Set the specified property with the given value in the project's .project file.
	 * @param project
	 * @param property
	 * @param value
	 * @throws CoreException
	 */
	public static void setProjectProperty(IProject project, String property, String value) throws CoreException
	{
		IProjectDescription description = project.getDescription();
		String comment = description.getComment();
		String newValue = property + EQUAL + value;
		
		String[] pairs = comment.split(DELIMITER);
		boolean found = false;
		for(int i = 0; i < pairs.length; i++)
		{
			String[] keyValue = pairs[i].split(EQUAL);
			
			if(keyValue.length == 2 && keyValue[0].equals(property))
			{
				pairs[i] = newValue;
				found = true;
				break;
			}
		}
		
		comment = ""; //$NON-NLS-1$
		for(String info : pairs)
		{
			comment += info + DELIMITER;
		}
		
		if(!found)
		{
			comment += newValue;
		}
		
		description.setComment(comment);
		
		project.setDescription(description, new NullProgressMonitor());
	}

	/**
	 * Copies input to the location specified by output.
	 * @param input The File to be copied.
	 * @param output The File representing the location to copy to.
	 * @throws IOException
	 */
	public static void copyFile(File input, File output) throws IOException
	{
		if(output.exists())
		{
			output.delete();
		}
		
		FileChannel in = null;
		FileChannel out = null;
		
		try
		{
			in = new FileInputStream(input).getChannel();
			out = new FileOutputStream(output).getChannel();
			in.transferTo(0, in.size(), out);
		}
		catch(IOException e)
		{
			gLogger.error("Could not copy file.", e); //$NON-NLS-1$
		}
		finally
		{
			if(in != null)
			{
				in.close();
			}
			if(out != null)
			{
				out.close();
			}
		}
	}
	
	/**
	 * Create the IProject and file system of the given name. 
	 * @param name
	 * @return
	 */
	public static IProject makeProjectFileSystem(String name)
	{
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject wProject = root.getProject(name);
		
		try
		{
			wProject.create(new NullProgressMonitor());
			wProject.open(new NullProgressMonitor());
			
			if(!makeSubFolders(wProject))
			{
				cleanUpFolders(wProject);
				throw new QualyzerException(Messages.getString("util.FileUtil.fileSystemFailed")); //$NON-NLS-1$
			}
		}
		catch(CoreException e)
		{
			throw new QualyzerException(Messages.getString("util.FileUtil.projectProblem"), e); //$NON-NLS-1$
		}
		
		return wProject;
	}
	
	/**
	 * Rolls-back the creation of the project's sub-folders.
	 * @param wProject
	 */
	private static void cleanUpFolders(IProject wProject)
	{
		try
		{
			wProject.getFolder(AUDIO).delete(true, new NullProgressMonitor());
		}
		catch (CoreException e)
		{
			
		}
		
		try
		{
			wProject.getFolder(TRANSCRIPTS).delete(true, new NullProgressMonitor());
		}
		catch (CoreException e)
		{
			
		}
		
		try
		{
			wProject.getFolder(MEMOS).delete(true, new NullProgressMonitor());
		}
		catch (CoreException e)
		{
			
		}
	}
	
	/**
	 * Create the sub-folders for the given project.
	 * @param wProject
	 * @return
	 */
	private static boolean makeSubFolders(IProject wProject)
	{
		try
		{
			wProject.getFolder(AUDIO).create(true, true, new NullProgressMonitor());
			wProject.getFolder(TRANSCRIPTS).create(true, true, new NullProgressMonitor());
			wProject.getFolder(MEMOS).create(true, true, new NullProgressMonitor());
		}
		catch (CoreException e)
		{
			gLogger.error("Failed to create sub-Folders", e); //$NON-NLS-1$
			return false;
		}
		
		return true;
	}
	
	/**
	 * Verify that a project contains the correct sub folders and create them if necessary.
	 * For use when importing Projects.
	 * @param project
	 */
	public static void refreshSubFolders(IProject project)
	{
		try
		{
			IFolder folder = project.getFolder(AUDIO);
			if(!folder.exists())
			{
				folder.create(true, true, new NullProgressMonitor());
			}
			
			folder = project.getFolder(TRANSCRIPTS);
			if(!folder.exists())
			{
				folder.create(true, true, new NullProgressMonitor());
			}
			
			folder = project.getFolder(MEMOS);
			if(!folder.exists())
			{
				folder.create(true, true, new NullProgressMonitor());
			}
		}
		catch(CoreException e)
		{
			gLogger.error("Unable to create folders", e); //$NON-NLS-1$
			throw new QualyzerException(Messages.getString(
					"util.FileUtil.folderCreationError") + project.getName(), e); //$NON-NLS-1$
		}
		
	}
	
	/**
	 * Creates a new empty transcript file or copies an existing one and copies the audio file.
	 * @param transcript
	 * @param audioFilePath
	 * @param existingTranscript
	 */
	public static void setupTranscriptFiles(String transcriptName, String projectName, String audioFilePath,
			String existingTranscript)
	{
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject wProject = root.getProject(projectName);
		
		String workspacePath = wProject.getLocation().toString();
		hookupAudioFile(audioFilePath, workspacePath, transcriptName);
		
		String transcriptFileName = transcriptName.replace(' ', '_') + ".rtf"; //$NON-NLS-1$
		createTranscriptFile(existingTranscript, projectName, transcriptFileName); 
	}
	
	private static void hookupAudioFile(String audioFilePath, String workspacePath, String transcriptName)
	{
		if(!audioFilePath.isEmpty())
		{
			//if the audio file is not in the workspace then copy it there.
			int i = audioFilePath.lastIndexOf('.');
			
			String relativePath = transcriptName.replace(' ', '_')+audioFilePath.substring(i);
			
			if(audioFilePath.indexOf(workspacePath) == -1 || namesAreDifferent(transcriptName, audioFilePath))
			{
				if(!copyAudioFile(audioFilePath, relativePath, workspacePath))
				{
					throw new QualyzerException(Messages.getString("util.FileUtil.audioCopyFailed")); //$NON-NLS-1$
				}

			}
		}
	}
	
	private static boolean namesAreDifferent(String name, String audioPath)
	{
		int i = audioPath.lastIndexOf(File.separatorChar) + 1;
		int j = audioPath.lastIndexOf('.');
		return !name.equals(audioPath.substring(i, j));
	}
	
	private static boolean copyAudioFile(String audioPath, String relativePath, String workspacePath)
	{
		File file = new File(audioPath);
		File fileCpy = new File(workspacePath+File.separator+AUDIO+File.separator+relativePath);
		
		if(!file.exists())
		{
			throw new QualyzerException(Messages.getString("util.FileUtil.audioMissing")); //$NON-NLS-1$
		}
		
		try
		{
			FileUtil.copyFile(file, fileCpy);
			return true;
		}
		catch (IOException e)
		{
			gLogger.error("Audio File copy failed", e); //$NON-NLS-1$
			return false;
		}
	}
	
	private static void createTranscriptFile(String existingTranscript, String projectName, String fileName)
	{
		IProject wProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		String path = wProject.getLocation()+File.separator+TRANSCRIPTS+File.separator+fileName;
		File file = new File(path);
		
		if(existingTranscript.isEmpty())
		{
			try
			{
				if(!file.createNewFile())
				{
					throw new QualyzerException(Messages.getString(
							"util.FileUtil.transcriptCreateFailed")); //$NON-NLS-1$
				}
				else
				{
					FileWriter writer = new FileWriter(file);
					writer.write("{\\rtf1\\ansi\\deff0\n\n\n}\n\0"); //$NON-NLS-1$
					writer.close();
				}
			}
			catch (IOException e)
			{
				gLogger.error("Failed to create new File", e); //$NON-NLS-1$
				throw new QualyzerException(Messages.getString("util.FileUtil.errorCreateTranscript"), e); //$NON-NLS-1$
			}
		}
		else
		{
			File fileOrig = new File(existingTranscript);
			if(file.exists())
			{
				throw new QualyzerException(Messages.getString("util.FileUtil.transcriptAlreadyExists")); //$NON-NLS-1$
			}
			
			if(!fileOrig.exists())
			{
				throw new QualyzerException(Messages.getString("util.FileUtil.transcriptMissing")); //$NON-NLS-1$
			}
			
			try
			{
				FileUtil.copyFile(fileOrig, file);
			}
			catch (IOException e)
			{
				throw new QualyzerException(Messages.getString("util.FileUtil.TranscriptCopyError"), e); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Create a new empty memo file or copy an existing one.
	 * @param memoName
	 * @param name
	 */
	public static void setupMemoFiles(String memoName, String projectName, String fileName)
	{
		IProject wProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		String workspacePath = wProject.getLocation().toString();
		String memoFileName = memoName.replace(' ', '_') + ".rtf"; //$NON-NLS-1$
		String path = workspacePath+File.separator+MEMOS+File.separator+memoFileName;
		File file = new File(path);
		if(fileName == null || fileName.isEmpty())
		{
			try
			{
				if(!file.createNewFile())
				{
					throw new QualyzerException(Messages.getString("util.FileUtil.memoFailed")); //$NON-NLS-1$
				}
				else
				{
					FileWriter writer = new FileWriter(file);
					writer.write("{\\rtf1\\ansi\\deff0\n\n\n}\n\0"); //$NON-NLS-1$
					writer.close();
				}
			}
			catch (IOException e)
			{
				gLogger.error("Failed to create new File", e);  //$NON-NLS-1$
				throw new QualyzerException(Messages.getString("util.FileUtil.memoCreationFailed"), e);  //$NON-NLS-1$
			}
		}
		else
		{
			File fileOrig = new File(fileName);
			if(file.exists())
			{
				throw new QualyzerException(Messages.getString("util.FileUtil.memoAlreadyExists"));  //$NON-NLS-1$
			}
			
			if(!fileOrig.exists())
			{
				throw new QualyzerException(Messages.getString("util.FileUtil.cannotFindMemo"));  //$NON-NLS-1$
			}
			
			try
			{
				FileUtil.copyFile(fileOrig, file);
			}
			catch (IOException e)
			{
				throw new QualyzerException(Messages.getString("util.FileUtil.memoCopyFailed"), e);  //$NON-NLS-1$
			}
		}
	}
	
	/**
	 * Handles the renaming of a project on the Resource Side.
	 * @param oldName
	 * @param newName
	 */
	public static void renameProject(String oldName, String newName)
	{
		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers().remove(oldName);
		manager.close();
		
		IProject wProject = ResourcesPlugin.getWorkspace().getRoot().getProject(oldName);
		
		try
		{
			IProjectDescription description = wProject.getDescription();
			description.setName(newName.replace(' ', '_'));
			wProject.move(description, true, new NullProgressMonitor());
		}
		catch(CoreException e)
		{
			gLogger.error("Unable to rename project", e); //$NON-NLS-1$
			throw new QualyzerException(Messages.getString("util.FileUtil.renameFailed"), e); //$NON-NLS-1$
		}
	}
}
