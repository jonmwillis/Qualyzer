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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 * Acts as a folder containing parts of a project.
 * Holds a reference back to the project.
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public abstract class ProjectWrapper
{
	private static final int NUM1 = 35367;
	private static final int NUM2 = 21853;
	
	private Project fProject;
	
	/**
	 * Constructor.
	 * @param project
	 */
	public ProjectWrapper(Project project)
	{
		setProject(project);
	}

	/**
	 * Sets the project.
	 * @param project
	 */
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
		return new HashCodeBuilder(NUM1, NUM2).append(fProject).toHashCode();
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
			ProjectWrapper wrapper = (ProjectWrapper) obj;
			return new EqualsBuilder().append(fProject, wrapper.fProject).isEquals();
		}
		return false;
	}
}
