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
package ca.mcgill.cs.swevo.qualyzer.wizards;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.wizard.Wizard;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.model.HibernateDBManager;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.util.HibernateUtil;
import ca.mcgill.cs.swevo.qualyzer.wizards.pages.NewProjectPageOne;
import ca.mcgill.cs.swevo.qualyzer.wizards.pages.NewProjectPageTwo;

/**
 * The wizard that controls the creation of a new project.
 * @author Jonathan Faubert
 *
 */
public class NewProjectWizard extends Wizard 
{

	private NewProjectPageOne fOne;
	private NewProjectPageTwo fTwo;
	
	/**
	 * Constructor.
	 */
	public NewProjectWizard()
	{
		super();
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public void addPages()
	{
		fOne = new NewProjectPageOne();
		fTwo = new NewProjectPageTwo();
		
		addPage(fOne);
		addPage(fTwo);
	}
	
	@Override
	public boolean performFinish()
	{
		Project project = createProject();
		
		try
		{
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IProject wProject = root.getProject(fOne.getProjectName());
			wProject.create(new NullProgressMonitor());
			wProject.open(new NullProgressMonitor());
			
			if(!makeSubFolders(wProject))
			{
				cleanUpFolders(wProject);
				//TODO display error message and quit.
			}
			
			PersistenceManager.getInstance().initDB(wProject);
			HibernateDBManager manager;
			manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(wProject.getName());
			HibernateUtil.quietSave(manager, project);
			root.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		}
		catch(CoreException e)
		{
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	/**
	 * Deletes any created subfolders in the event of a failure.
	 */
	private void cleanUpFolders(IProject wProject)
	{
		String path = wProject.getLocation().toOSString();
		File dir = new File(path+File.separator+"audio");
		if(!dir.exists())
		{
			dir.delete();
		}
		dir = new File(path+File.separator+"transcripts");
		if(!dir.exists())
		{
			dir.delete();
		}
		dir = new File(path+File.separator+"memos");
		if(!dir.exists())
		{
			dir.delete();
		}
	}

	/**
	 * Makes the sub-folders required by the project.
	 * @param wProject
	 * @return false if any fail to be created
	 */
	private boolean makeSubFolders(IProject wProject)
	{
		String path = wProject.getLocation().toOSString();
		File dir = new File(path+File.separator+"audio");
		if(!dir.mkdir())
		{
			return false;
		}
		dir = new File(path+File.separator+"transcripts");
		if(!dir.mkdir())
		{
			return false;
		}
		dir = new File(path+File.separator+"memos");
		return dir.mkdir();
	}

	/**
	 * @return the project built by the wizard
	 */
	private Project createProject()
	{
		Project project = new Project();
		project.setName(fOne.getProjectName());
		Investigator investigator = new Investigator();
		investigator.setNickName(fTwo.getInvestigatorNickname());
		investigator.setFullName(fTwo.getInvestigatorFullname());
		investigator.setInstitution(fTwo.getInstitution());
		project.getInvestigators().add(investigator);
		investigator.setProject(project);
		return project;
	}

}
