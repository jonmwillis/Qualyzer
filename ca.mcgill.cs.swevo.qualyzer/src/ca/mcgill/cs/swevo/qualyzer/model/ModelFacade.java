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
package ca.mcgill.cs.swevo.qualyzer.model;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.util.HibernateUtil;
import ca.mcgill.cs.swevo.qualyzer.wizards.Messages;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public final class ModelFacade
{
	private static ModelFacade gFacade = null;

	private ModelFacade()
	{
		
	}
	
	/**
	 * Get the Facade.
	 * @return
	 */
	public static ModelFacade getInstance()
	{
		if(gFacade == null)
		{
			gFacade = new ModelFacade();
		}
		
		return gFacade;
	}
	
	/**
	 * Create a new Project with the given name.
	 * @param name
	 * @return
	 */
	public IProject createProject(String name, String nickname, String fullName, String institution)
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
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
						Messages.getString("wizards.NewProjectWizard.failure"),  //$NON-NLS-1$
						Messages.getString("wizards.NewProjectWizard.errorMessage")); //$NON-NLS-1$
			}
			
			Project project = new Project();
			project.setName(name);
			
			Investigator investigator = new Investigator();
			investigator.setNickName(nickname);
			investigator.setFullName(fullName);
			investigator.setInstitution(institution);
			investigator.setProject(project);
			
			project.getInvestigators().add(investigator);
			
			PersistenceManager.getInstance().initDB(wProject);
			HibernateDBManager manager;
			manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(wProject.getName());
			HibernateUtil.quietSave(manager, project);
			root.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		}
		catch(CoreException e)
		{
			e.printStackTrace();
			return null;
		}
		
		return wProject;		
	}

	/**
	 * @param wProject
	 */
	private void cleanUpFolders(IProject wProject)
	{
		String path = wProject.getLocation().toOSString();
		File dir = new File(path+File.separator+"audio"); //$NON-NLS-1$
		if(!dir.exists())
		{
			dir.delete();
		}
		dir = new File(path+File.separator+"transcripts"); //$NON-NLS-1$
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

	/**
	 * @param wProject
	 * @return
	 */
	private boolean makeSubFolders(IProject wProject)
	{
		String path = wProject.getLocation().toOSString();
		File dir = new File(path+File.separator+"audio"); //$NON-NLS-1$
		if(!dir.mkdir())
		{
			return false;
		}
		dir = new File(path+File.separator+"transcripts"); //$NON-NLS-1$
		if(!dir.mkdir())
		{
			return false;
		}
		dir = new File(path+File.separator+"memos"); //$NON-NLS-1$
		return dir.mkdir();
	}
	
}
