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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.CollectionId;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

/**
 */
@Entity
@GenericGenerator(name = "uuid-gen", strategy = "uuid")
public class Memo implements Comparable<Memo>, IAnnotatedDocument
{
	private Investigator fAuthor;
	private List<Fragment> fFragments = new ArrayList<Fragment>();
	private List<Participant> fParticipants = new ArrayList<Participant>();
	private String fName;
	private String fFileName;
	private Project fProject;
	private Long fPersistenceId;

	/**
	 * @return
	 */
	@ManyToOne
	public Investigator getAuthor()
	{
		return fAuthor;
	}

	/**
	 * @param author
	 */
	public void setAuthor(Investigator author)
	{
		this.fAuthor = author;
	}

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@CollectionId(columns = @Column(name = "COL_ID"), type = @Type(type = "string"), generator = "uuid-gen")
	@Override
	public List<Fragment> getFragments()
	{
		return fFragments;
	}

	/**
	 * @param fragments
	 */
	public void setFragments(List<Fragment> fragments)
	{
		this.fFragments = fragments;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@CollectionId(columns = @Column(name = "COL_ID"), type = @Type(type = "string"), generator = "uuid-gen")
	@Override
	public List<Participant> getParticipants()
	{
		return fParticipants;
	}

	/**
	 * @param participants
	 */
	public void setParticipants(List<Participant> participants)
	{
		this.fParticipants = participants;
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
	public String getName()
	{
		return fName;
	}

	/**
	 * @param name
	 */
	public void setName(String name)
	{
		this.fName = name;
	}

	/**
	 * @return
	 */
	public String getFileName()
	{
		return fFileName;
	}

	/**
	 * @param fileName
	 */
	public void setFileName(String fileName)
	{
		this.fFileName = fileName;
	}

	@ManyToOne
	@JoinColumn(name = "project_persistenceid", nullable = false, insertable = false, updatable = false)
	@Override
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
	public int compareTo(Memo memo)
	{
		return this.getName().compareTo(memo.getName());
	}
	
	

}
