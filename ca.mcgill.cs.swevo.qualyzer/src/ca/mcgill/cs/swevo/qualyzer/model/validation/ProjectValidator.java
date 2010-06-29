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

import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;

/**
 * Validates the business rules when a new project is created.
 */
public class ProjectValidator implements IValidator
{
	private final String fName;
	private final String fInvestigator;
	private final IWorkspaceRoot fRoot;
	private String fMessage;
	
	/**
	 * Constructs a new ProjectValidator.
	 * @param pName The name chosen for the new project.
	 * @param pInvestigator The nickname chosen for the default investigator
	 * @param pRoot The root of the workshpace.
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
		else if(!ResourcesUtil.verifyID(fName))
		{
			fMessage = Messages.getString("model.validation.ProjectValidator.invalidProjectName"); //$NON-NLS-1$
			lReturn = false;
		}
		else
		{
			IProject wProject = fRoot.getProject(fName);
			
			if(wProject.exists())
			{
				fMessage = Messages.getString("model.validation.ProjectValidator.alreadyExists"); //$NON-NLS-1$
				lReturn = false;
			}
			else if(fInvestigator.length() == 0)
			{
				fMessage = Messages.getString("model.validation.ProjectValidator.enterNickname"); //$NON-NLS-1$
				lReturn = false;
			}
			else if(!ResourcesUtil.verifyID(fInvestigator))
			{
				fMessage = 
					Messages.getString("model.validation.ProjectValidator.invalidInvestigatorName"); //$NON-NLS-1$
				lReturn = false;
			}
		}
		return lReturn;
	}

	@Override
	public String getErrorMessage() 
	{
		return fMessage;
	}

	
}
