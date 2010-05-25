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

	public Project getProject()
	{
		return fProject;
	}
	
	public abstract String getResource();
}
