/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Robillard
 *******************************************************************************/

package ca.mcgill.cs.swevo.qualyzer.model.validation;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;


/**
 * Validates the business rules when a new project is created.
 */
public class ProjectValidator extends AbstractValidator
{
	private final String fName;
	private final String fInvestigator;
	private final IWorkspaceRoot fRoot;
	/**
	 * Constructs a new ProjectValidator.
	 * @param pName The name chosen for the new project.
	 * @param pInvestigator The nickname chosen for the default investigator
	 * @param pRoot The root of the workspace.
	 */
	public ProjectValidator(String pName, String pInvestigator, IWorkspaceRoot pRoot)
	{
		fName = pName;
		fInvestigator = pInvestigator;
		fRoot = pRoot;
	}
	
	@Override
	public boolean isValid() 
	{
		boolean lReturn = true;
		if(fName.length() == 0)
		{
			fMessage = Messages.getString("model.validation.ProjectValidator.emptyProjectName"); //$NON-NLS-1$
			lReturn = false;
		}
		else if(!ValidationUtils.verifyID(fName))
		{
			fMessage = Messages.getString("model.validation.ProjectValidator.invalidProjectName"); //$NON-NLS-1$
			lReturn = false;
		}
		else
		{
			IProject wProject = fRoot.getProject(fName.replace(' ', '_'));
			
			if(projectExists(wProject))
			{
				fMessage = Messages.getString("model.validation.ProjectValidator.alreadyExists"); //$NON-NLS-1$
				lReturn = false;
			}
			else if(fInvestigator.length() == 0)
			{
				fMessage = Messages.getString("model.validation.ProjectValidator.enterNickname"); //$NON-NLS-1$
				lReturn = false;
			}
			else if(!ValidationUtils.verifyID(fInvestigator))
			{
				fMessage = 
					Messages.getString("model.validation.ProjectValidator.invalidInvestigatorName"); //$NON-NLS-1$
				lReturn = false;
			}
		}
		return lReturn;
	}

	/**
	 * @param wProject
	 * @return
	 */
	private boolean projectExists(IProject wProject)
	{
		if(wProject.exists())
		{
			return true;
		}
		else
		{
			for(IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects())
			{
				if(project.getName().equalsIgnoreCase(wProject.getName()))
				{
					return true;
				}
			}
				
			return false;
		}
	}
}
