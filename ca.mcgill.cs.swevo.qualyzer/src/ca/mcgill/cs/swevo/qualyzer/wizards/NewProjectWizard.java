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

/**
 * The wizard that controls the creation of a new project.
 * @author Jonathan Faubert
 *
 */
public class NewProjectWizard extends Wizard 
{

	private NewProjectPageOne fOne;
	private NewProjectPageTwo fTwo;
	
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
		// TODO Popup tips dialog if user hasn't disabled it
		Project project = createProject();
		
		try
		{
			//build workspace data
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IProject wProject = root.getProject(fOne.getProjectName());
			wProject.create(new NullProgressMonitor());
			wProject.open(new NullProgressMonitor());
			
			PersistenceManager.getInstance().initDB(wProject);
			HibernateDBManager manager;
			manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(wProject.getName());
			HibernateUtil.quietSave(manager, project);
			
			if(!makeSubFolders(wProject))
			{
				//Undo changes.
			}
		}
		catch(CoreException e)
		{
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	/**
	 * @param wProject
	 */
	private boolean makeSubFolders(IProject wProject)
	{
		//If any of these fail then undo the changes.
		String path = wProject.getLocation().toOSString();
		File dir = new File(path+File.separator+"audio");
		dir.mkdir();
		dir = new File(path+File.separator+"transcripts");
		dir.mkdir();
		dir = new File(path+File.separator+"memos");
		dir.mkdir();
		return true;
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
