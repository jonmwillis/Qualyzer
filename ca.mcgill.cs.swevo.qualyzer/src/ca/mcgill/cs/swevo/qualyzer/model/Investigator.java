/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Barthelemy Dagenais (bart@cs.mcgill.ca)
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 */
@Entity
public class Investigator
{
	private String fNickName;
	private String fFullName;
	private String fInstitution;
	private Project fProject;
	private Long fPersistenceId;

	/**
	 * @return
	 */
	public String getNickName()
	{
		return fNickName;
	}

	/**
	 * @param nickName
	 */
	public void setNickName(String nickName)
	{
		this.fNickName = nickName;
	}

	/**
	 * @return
	 */
	public String getFullName()
	{
		return fFullName;
	}

	/**
	 * @param fullName
	 */
	public void setFullName(String fullName)
	{
		this.fFullName = fullName;
	}

	/**
	 * @return
	 */
	public String getInstitution()
	{
		return fInstitution;
	}

	/**
	 * @param institution
	 */
	public void setInstitution(String institution)
	{
		this.fInstitution = institution;
	}

	/**
	 * @return
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getPersistenceId()
	{
		return fPersistenceId;
	}

	/**
	 * @param persistenceId
	 */
	public void setPersistenceId(Long persistenceId)
	{
		this.fPersistenceId = persistenceId;
	}
	
	/**
	 * @return
	 */
	@ManyToOne
	@JoinColumn(name = "project_persistenceid", nullable = false, insertable = false, updatable = false)
	public Project getProject()
	{
		return fProject;
	}

	/**
	 * @param project
	 */
	public void setProject(Project project)
	{
		this.fProject = project;
	}
	
	@Override
	public int hashCode()
	{
		//TODO
		return super.hashCode();
	}
	
	@Override
	public boolean equals(Object object)
	{
		if(object == null)
		{
			return false;
		}
		else if(object == this)
		{
			return true;
		}
		else if(object.getClass().equals(getClass()))
		{
			return fNickName.equals(((Investigator) object).getNickName());
		}
		return false;
	}
}
