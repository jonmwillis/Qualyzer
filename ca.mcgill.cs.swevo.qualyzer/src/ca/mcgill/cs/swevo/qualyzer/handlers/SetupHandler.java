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

import java.io.ByteArrayInputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * Test command used to setup a basic project.
 * 
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 * 
 */
public class SetupHandler extends AbstractHandler
{

	/**
	 * <p>
	 * Does something.
	 * </p>
	 * 
	 * @param event
	 * @return
	 * @throws ExecutionException
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		try
		{
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

			IProject project = root.getProject("My First Project");
			project.create(new NullProgressMonitor());
			project.open(new NullProgressMonitor());

			IFolder interviewFolder = project.getFolder("interviews");
			interviewFolder.create(true, true, new NullProgressMonitor());

			IFile interview1 = interviewFolder.getFile("interview1.txt");
			interview1.create(new ByteArrayInputStream("This is an interview\nWith Participant 1".getBytes()), true,
					new NullProgressMonitor());
			root.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
		return null;
	}

}
