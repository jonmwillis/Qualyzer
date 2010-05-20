/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Name - Initial Contribution
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;

/**
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 * 
 */
@Entity
public class Code implements Comparable<Code>
{

	private String fCodeName;

	private String fDescription;

	private Project fProject;

	private Long fPersistenceId;

	/**
	 * @return the codeName
	 */
	public String getCodeName()
	{
		return fCodeName;
	}

	/**
	 * @param codeName
	 *            the codeName to set
	 */
	public void setCodeName(String codeName)
	{
		this.fCodeName = codeName;
	}

	@Type(type = "text")
	public String getDescription()
	{
		return fDescription;
	}

	public void setDescription(String description)
	{
		this.fDescription = description;
	}

	/**
	 * @param persistenceId
	 *            the persistenceId to set
	 */
	public void setPersistenceId(Long persistenceId)
	{
		this.fPersistenceId = persistenceId;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getPersistenceId()
	{
		return fPersistenceId;
	}

	@ManyToOne
	@JoinColumn(name = "project_persistenceid", nullable = false, insertable = false, updatable = false)
	public Project getProject()
	{
		return fProject;
	}

	public void setProject(Project project)
	{
		this.fProject = project;
	}

	@Override
	public int compareTo(Code code)
	{
		return this.getCodeName().compareTo(code.getCodeName());
	}

}
