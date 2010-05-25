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

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.wizard.Wizard;

import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

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
		// TODO populate the project explorer with the new project and all necessary folders
		
		//Build model data
		Project project = new Project();
		project.setName(fOne.getProjectName());
		Investigator investigator = new Investigator();
		investigator.setFullName(fTwo.getInvestigatorNickname());
		investigator.setProject(project);
		
		
//		try
//		{
//		//build workspace data
//		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
//		
//		IProject wProject = root.getProject(fOne.getProjectName());
//		
//		if(wProject.exists())
//		{
//			//TODO Error message
//			System.out.println("This project already exists");
//			return false;
//		}
//		
//		wProject.create(new NullProgressMonitor());
//		wProject.open(new NullProgressMonitor());
//		
//		IFolder transcripts, investigators, memos, participants, codes;
//		
//		transcripts = wProject.getFolder("transcripts");
//		transcripts.create(true, true, new NullProgressMonitor());
//		
//		codes = wProject.getFolder("codes");
//		codes.create(true, true, new NullProgressMonitor());
//		
//		memos = wProject.getFolder("memos");
//		memos.create(true, true, new NullProgressMonitor());
//		
//		investigators = wProject.getFolder("investigators");
//		investigators.create(true, true, new NullProgressMonitor());
//		
//		participants = wProject.getFolder("participants");
//		participants.create(true, true, new NullProgressMonitor());
//		}
//		catch(CoreException e)
//		{
//			e.printStackTrace();
//			return false;
//		}
		
		return true;
	}

}
