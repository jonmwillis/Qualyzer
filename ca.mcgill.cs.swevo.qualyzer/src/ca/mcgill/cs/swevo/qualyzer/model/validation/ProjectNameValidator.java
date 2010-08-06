/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jonathan Faubert
 *******************************************************************************/
/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.model.validation;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 *
 */
public class ProjectNameValidator extends BasicNameValidator
{

	/**
	 * @param pName
	 * @param pOldName
	 * @param pProject
	 */
	public ProjectNameValidator(String pName, String pOldName, Project pProject)
	{
		super(Messages.getString("model.validation.ProjectNameValidator.projectName"), //$NON-NLS-1$
				pName, pOldName, pProject); 
	}
	
	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.model.validation.BasicNameValidator#nameInUse()
	 */
	@Override
	protected boolean nameInUse()
	{
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(fName);
		
		return project.exists();
	}
	
}
