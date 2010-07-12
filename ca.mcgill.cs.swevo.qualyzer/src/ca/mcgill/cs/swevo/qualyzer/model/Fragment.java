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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.CollectionId;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

/**
 */
@Entity
@GenericGenerator(name = "uuid-gen", strategy = "uuid")
public class Fragment
{
	private int fOffset;
	private int fLength;
	private List<Annotation> fAnnotations = new ArrayList<Annotation>();
	private List<CodeEntry> fCodeEntries = new ArrayList<CodeEntry>();
	private Long fPersistenceId;
	private Transcript fTranscript;

	/**
	 * @return
	 */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@CollectionId(columns = @Column(name = "COL_ID"), type = @Type(type = "string"), generator = "uuid-gen")
	public List<Annotation> getAnnotations()
	{
		return fAnnotations;
	}

	/**
	 * @param annotations
	 */
	public void setAnnotations(List<Annotation> annotations)
	{
		this.fAnnotations = annotations;
	}

	/**
	 * @return
	 */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@CollectionId(columns = @Column(name = "COL_ID"), type = @Type(type = "string"), generator = "uuid-gen")
	public List<CodeEntry> getCodeEntries()
	{
		return fCodeEntries;
	}

	/**
	 * @param codeEntries
	 */
	public void setCodeEntries(List<CodeEntry> codeEntries)
	{
		this.fCodeEntries = codeEntries;
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
	 * 
	 * @param position
	 */
	public void setOffset(int offset)
	{
		fOffset = offset;
	}

	/**
	 * 
	 * @param length
	 */
	public void setLength(int length)
	{
		fLength = length;
	}

	/**
	 * 
	 * @return
	 */
	public int getOffset()
	{
		return fOffset;
	}

	/**
	 * 
	 * @return
	 */
	public int getLength()
	{
		return fLength;
	}

	/**
	 * 
	 */
	@ManyToOne
	@JoinColumn(name = "transcript_fk")
	public Transcript getTranscript()
	{
		return fTranscript;
	}

	/**
	 * 
	 * @param transcript
	 */
	public void setTranscript(Transcript transcript)
	{
		this.fTranscript = transcript;
	}
}
