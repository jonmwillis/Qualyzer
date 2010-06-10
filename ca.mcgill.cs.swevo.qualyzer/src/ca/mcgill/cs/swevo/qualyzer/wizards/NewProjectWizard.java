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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.model.HibernateDBManager;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.util.HibernateUtil;
import ca.mcgill.cs.swevo.qualyzer.wizards.pages.NewProjectPageOne;

/**
 * The wizard that controls the creation of a new project.
 * @author Jonathan Faubert
 *
 */
public class NewProjectWizard extends Wizard 
{

	private NewProjectPageOne fOne;
	private IProject fProject;
	
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
		
		addPage(fOne);
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
				MessageDialog.openError(getShell(), 
						Messages.getString("wizards.NewProjectWizard.failure"),  //$NON-NLS-1$
						Messages.getString("wizards.NewProjectWizard.errorMessage")); //$NON-NLS-1$
			}
			
			PersistenceManager.getInstance().initDB(wProject);
			HibernateDBManager manager;
			manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(wProject.getName());
			HibernateUtil.quietSave(manager, project);
			root.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			
			fProject = wProject;
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
	 * Makes the sub-folders required by the project.
	 * @param wProject
	 * @return false if any fail to be created
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

	/**
	 * @return the project built by the wizard
	 */
	private Project createProject()
	{
		Project project = new Project();
		project.setName(fOne.getProjectName());
		Investigator investigator = new Investigator();
		investigator.setNickName(fOne.getInvestigatorNickname());
		investigator.setFullName(fOne.getInvestigatorFullname());
		investigator.setInstitution(fOne.getInstitution());
		project.getInvestigators().add(investigator);
		investigator.setProject(project);
		return project;
	}
	
	/**
	 * Gets the IProject created by the wizard. Used to properly expand the node.
	 * @return
	 */
	public IProject getProjectReference()
	{
		return fProject;
	}

}
