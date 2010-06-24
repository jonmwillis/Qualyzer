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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;

import ca.mcgill.cs.swevo.qualyzer.QualyzerException;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
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
		Project project = null;
		try
		{
			project = Facade.getInstance().createProject(fOne.getProjectName(), 
				fOne.getInvestigatorNickname(), fOne.getInvestigatorFullname(), fOne.getInstitution());
			
			fProject = ResourcesPlugin.getWorkspace().getRoot().getProject(project.getName());
		}
		catch(QualyzerException e) 
		{
			MessageDialog.openError(getShell(), "Project Error", e.getMessage());
			return false;
		}

		return project != null;
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
