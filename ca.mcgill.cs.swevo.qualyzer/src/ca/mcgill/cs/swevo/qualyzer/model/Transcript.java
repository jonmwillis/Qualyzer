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
import javax.persistence.OneToOne;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.CollectionId;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

/**
 */
@Entity
@GenericGenerator(name = "uuid-gen", strategy = "uuid")
public class Transcript implements Comparable<Transcript>, IAnnotatedDocument
{
	private static final int NUM1 = 3;
	private static final int NUM2 = 67;

	private AudioFile fAudioFile;
	private List<Participant> fParticipants = new ArrayList<Participant>();
	private List<Fragment> fFragments = new ArrayList<Fragment>();
	private String fName;
	private String fFileName;
	private String fDate;
	private Project fProject;
	private Long fPersistenceId;

	/**
	 * @return
	 */
	@OneToOne(cascade = { CascadeType.ALL })
	public AudioFile getAudioFile()
	{
		return fAudioFile;
	}

	/**
	 * @param audioFile
	 */
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

	/**
	 * @param participants
	 */
	public void setParticipants(List<Participant> participants)
	{
		this.fParticipants = participants;
	}

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "transcript")
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
	public int compareTo(Transcript transcript)
	{
		return this.getName().compareTo(transcript.getName());
	}

	/**
	 * 
	 * @param Date
	 */
	public void setDate(String date)
	{
		this.fDate = date;
	}

	/**
	 * 
	 * @return
	 */
	public String getDate()
	{
		return fDate;
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(NUM1, NUM2).append(fName).append(fFileName).append(fProject).toHashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		if (obj.getClass() != getClass())
		{
			return false;
		}

		Transcript transcript = (Transcript) obj;

		return new EqualsBuilder().append(fName, transcript.fName).append(fFileName, transcript.fFileName).append(
				fProject, transcript.fProject).isEquals();
	}

}
