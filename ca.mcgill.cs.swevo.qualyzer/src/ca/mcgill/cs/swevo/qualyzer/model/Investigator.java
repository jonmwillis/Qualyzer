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

/**
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 *
 */
@Entity
public class Investigator
{
	private String fNickName;
	
	private String fFullName;
	
	private String fInstitution;
	
	private Project fProject;
	
	private Long fPersistenceId;

	public String getNickName()
	{
		return fNickName;
	}

	public void setNickName(String nickName)
	{
		this.fNickName = nickName;
	}

	public String getFullName()
	{
		return fFullName;
	}

	public void setFullName(String fullName)
	{
		this.fFullName = fullName;
	}

	public String getInstitution()
	{
		return fInstitution;
	}

	public void setInstitution(String institution)
	{
		this.fInstitution = institution;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getPersistenceId()
	{
		return fPersistenceId;
	}

	public void setPersistenceId(Long persistenceId)
	{
		this.fPersistenceId = persistenceId;
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
	
}
