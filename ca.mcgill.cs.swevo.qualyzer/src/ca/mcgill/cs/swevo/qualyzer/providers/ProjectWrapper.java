/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.providers;

import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class ProjectWrapper
{
	private Project fProject;
	private String fResource;
	
	public ProjectWrapper(Project project, String resource)
	{
		setProject(project);
		setResource(resource);
	}

	public void setProject(Project project)
	{
		this.fProject = project;
	}

	public Project getProject()
	{
		return fProject;
	}

	public void setResource(String resource)
	{
		this.fResource = resource;
	}

	public String getResource()
	{
		return fResource;
	}
	
	
}
