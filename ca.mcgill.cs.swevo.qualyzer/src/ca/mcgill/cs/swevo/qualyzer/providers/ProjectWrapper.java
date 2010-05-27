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
package ca.mcgill.cs.swevo.qualyzer.providers;

import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 * Acts as a folder containing parts of a project.
 * Holds a reference back to the project.
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public abstract class ProjectWrapper
{
	private Project fProject;
	
	public ProjectWrapper(Project project)
	{
		setProject(project);
	}

	public void setProject(Project project)
	{
		this.fProject = project;
	}

	/**
	 * Get the project that this wrapper belongs to.
	 * @return The project contained in the wrapper.
	 */
	public Project getProject()
	{
		return fProject;
	}
	
	/**
	 * Returns the name of the resource that this wrapper acts as a folder for.
	 * @return A resource name.
	 */
	public abstract String getResource();
	
	@Override
	public int hashCode()
	{
		return fProject.hashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null)
		{
			return false;
		}
		if(obj instanceof ProjectWrapper)
		{
			return fProject.equals(((ProjectWrapper) obj).getProject());
		}
		return false;
	}
}
