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
/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import ca.mcgill.cs.swevo.qualyzer.QualyzerException;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public final class FileUtil
{
	private FileUtil(){}

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
			e.printStackTrace();
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
				throw new QualyzerException("Unable to create the required file system.");
			}
		}
		catch(CoreException e)
		{
			e.printStackTrace();
			throw new QualyzerException("There was a problem creating the project", e);
		}
		
		return wProject;
	}
	
	private static void cleanUpFolders(IProject wProject)
	{
		String path = wProject.getLocation().toOSString();
		File dir = new File(path+File.separator+"audio"); //$NON-NLS-1$
		if(!dir.exists())
		{
			dir.delete();
		}
		dir = new File(path+File.separator+"transcripts");
		if(!dir.exists())
		{
			dir.delete();
		}
		dir = new File(path+File.separator+"memos"); //$NON-NLS-1$
		if(!dir.exists())
		{
			dir.delete();
		}
	}
	
	private static boolean makeSubFolders(IProject wProject)
	{
		String path = wProject.getLocation().toOSString();
		File dir = new File(path+File.separator+"audio"); //$NON-NLS-1$
		if(!dir.mkdir())
		{
			return false;
		}
		dir = new File(path+File.separator+"transcripts"); 
		if(!dir.mkdir())
		{
			return false;
		}
		dir = new File(path+File.separator+"memos"); //$NON-NLS-1$
		return dir.mkdir();
	}
}
