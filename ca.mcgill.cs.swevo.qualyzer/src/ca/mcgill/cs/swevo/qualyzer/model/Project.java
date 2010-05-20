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
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.hibernate.annotations.CollectionId;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;

/**
 * A qualitative project.
 * 
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 * 
 */
@Entity
@GenericGenerator(name = "uuid-gen", strategy = "uuid")
public class Project
{
	private String fName;

	private Long fPersistenceId;

	private List<Investigator> fInvestigators = new ArrayList<Investigator>();

	private List<Participant> fParticipants = new ArrayList<Participant>();

	private List<Transcript> fTranscripts = new ArrayList<Transcript>();

	private List<Memo> fMemos = new ArrayList<Memo>();

	private List<Code> fCodes = new ArrayList<Code>();

	/**
	 * @return the name
	 */
	public String getName()
	{
		return fName;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this.fName = name;
	}

	/**
	 * @return the investigators
	 */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@OrderBy("nickName")
	@CollectionId(columns = @Column(name = "COL_ID"), type = @Type(type = "string"), generator = "uuid-gen")
	public List<Investigator> getInvestigators()
	{
		return fInvestigators;
	}

	/**
	 * @param investigators
	 *            the investigators to set
	 */
	public void setInvestigators(List<Investigator> investigators)
	{
		this.fInvestigators = investigators;
	}

	/**
	 * @return the participants
	 */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@OrderBy("participantId")
	@CollectionId(columns = @Column(name = "COL_ID"), type = @Type(type = "string"), generator = "uuid-gen")
	public List<Participant> getParticipants()
	{
		return fParticipants;
	}

	/**
	 * @param participants
	 *            the participants to set
	 */
	public void setParticipants(List<Participant> participants)
	{
		this.fParticipants = participants;
	}

	/**
	 * @return the transcripts
	 */
	@OneToMany(cascade = { CascadeType.ALL })
	@LazyCollection(LazyCollectionOption.EXTRA)
	@CollectionId(columns = @Column(name = "COL_ID"), type = @Type(type = "string"), generator = "uuid-gen")
	@OrderBy("name")
	public List<Transcript> getTranscripts()
	{
		return fTranscripts;
	}

	/**
	 * @param transcripts
	 *            the transcripts to set
	 */
	public void setTranscripts(List<Transcript> transcripts)
	{
		this.fTranscripts = transcripts;
	}

	/**
	 * @return the memos
	 */
	@OneToMany(cascade = { CascadeType.ALL })
	@LazyCollection(LazyCollectionOption.EXTRA)
	@CollectionId(columns = @Column(name = "COL_ID"), type = @Type(type = "string"), generator = "uuid-gen")
	@OrderBy("name")
	public List<Memo> getMemos()
	{
		return fMemos;
	}

	/**
	 * @param memos
	 *            the memos to set
	 */
	public void setMemos(List<Memo> memos)
	{
		this.fMemos = memos;
	}

	/**
	 * @return the codes
	 */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@OrderBy("codeName")
	@CollectionId(columns = @Column(name = "COL_ID"), type = @Type(type = "string"), generator = "uuid-gen")
	public List<Code> getCodes()
	{
		return fCodes;
	}

	/**
	 * @param codes
	 *            the codes to set
	 */
	public void setCodes(List<Code> codes)
	{
		this.fCodes = codes;
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

}
