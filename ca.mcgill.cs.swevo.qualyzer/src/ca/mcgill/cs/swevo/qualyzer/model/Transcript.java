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
import javax.persistence.OneToOne;

import org.hibernate.annotations.CollectionId;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

/**
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 * 
 */
@Entity
@GenericGenerator(name = "uuid-gen", strategy = "uuid")
public class Transcript implements Comparable<Transcript>, IAnnotatedDocument
{
	private AudioFile fAudioFile;

	private List<Participant> fParticipants = new ArrayList<Participant>();

	private List<Fragment> fFragments = new ArrayList<Fragment>();

	private String fName;

	private String fFileName;
	
	private Project fProject;

	private Long fPersistenceId;

	@OneToOne(cascade = { CascadeType.ALL })
	public AudioFile getAudioFile()
	{
		return fAudioFile;
	}

	public void setAudioFile(AudioFile audioFile)
	{
		this.fAudioFile = audioFile;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@CollectionId(columns = @Column(name = "COL_ID"), type = @Type(type = "string"), generator = "uuid-gen")
	@Override
	public List<Participant> getParticipants()
	{
		return fParticipants;
	}

	public void setParticipants(List<Participant> participants)
	{
		this.fParticipants = participants;
	}

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@CollectionId(columns = @Column(name = "COL_ID"), type = @Type(type = "string"), generator = "uuid-gen")
	@Override
	public List<Fragment> getFragments()
	{
		return fFragments;
	}

	public void setFragments(List<Fragment> fragments)
	{
		this.fFragments = fragments;
	}

	public String getName()
	{
		return fName;
	}

	public void setName(String name)
	{
		this.fName = name;
	}

	public String getFileName()
	{
		return fFileName;
	}

	public void setFileName(String fileName)
	{
		this.fFileName = fileName;
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
	@Override
	public Project getProject()
	{
		return fProject;
	}

	public void setProject(Project project)
	{
		this.fProject = project;
	}

	@Override
	public int compareTo(Transcript transcript)
	{
		return this.getName().compareTo(transcript.getName());
	}
	
	

}
