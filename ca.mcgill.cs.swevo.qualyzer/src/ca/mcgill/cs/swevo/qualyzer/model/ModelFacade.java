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
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.QualyzerException;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;
import ca.mcgill.cs.swevo.qualyzer.util.HibernateUtil;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public final class ModelFacade
{
	private static ModelFacade gFacade = null;
	
	private HashMap<Project, ArrayList> fListeners;

	private ModelFacade()
	{
		fListeners = new HashMap<Project, ArrayList>();
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
	public Project createProject(String name, String nickname, String fullName, String institution)
		throws QualyzerException
	{
		if(!validateProject(name, nickname, fullName, institution))
		{
			throw new QualyzerException(); //TODO
		}
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject wProject = root.getProject(name);
		Project project;
		
		try
		{
			wProject.create(new NullProgressMonitor());
			wProject.open(new NullProgressMonitor());
			
			if(!makeSubFolders(wProject))
			{
				cleanUpFolders(wProject);
				throw new QualyzerException(); //TODO
			}
			
			project = new Project();
			project.setName(name);
			
			createInvestigator(nickname, fullName, institution, project, false);
			
			PersistenceManager.getInstance().initDB(wProject);
			HibernateDBManager manager;
			manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(name);
			HibernateUtil.quietSave(manager, project);
			root.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		}
		catch(CoreException e)
		{
			e.printStackTrace();
			throw new QualyzerException(); //TODO
		}
		
		return project;		
	}
	
	/**
	 * Checks that a project's fields are all valid.
	 * @param name
	 * @param nickname
	 * @param fullName
	 * @param institution
	 * @return
	 */
	public boolean validateProject(String name, String nickname, String fullName, String institution)
	{
		return ResourcesUtil.verifyID(name) && ResourcesUtil.verifyID(nickname);
	}

	/**
	 * Create an investigator from the given information.
	 * @param nickname
	 * @param fullName
	 * @param institution
	 * @param project
	 * @return
	 */
	public Investigator createInvestigator(String nickname, String fullName, String institution, 
			Project project, boolean save) throws QualyzerException
	{
		if(!validateInvestigator(nickname, fullName, institution))
		{
			throw new QualyzerException();
		}
		
		Investigator investigator = new Investigator();
		investigator.setNickName(nickname);
		investigator.setFullName(fullName);
		investigator.setInstitution(institution);
		investigator.setProject(project);
		
		project.getInvestigators().add(investigator);
		
		if(save)
		{
			HibernateDBManager manager;
			manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(project.getName());
			HibernateUtil.quietSave(manager, project);
		}
		return investigator;
	}

	/**
	 * @param nickname
	 * @param fullName
	 * @param institution
	 * @return
	 */
	public boolean validateInvestigator(String nickname, String fullName, String institution)
	{
		return ResourcesUtil.verifyID(nickname);
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
