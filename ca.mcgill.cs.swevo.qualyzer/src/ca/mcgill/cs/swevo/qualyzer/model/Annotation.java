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
import javax.persistence.OneToOne;

import org.hibernate.annotations.Type;

/**
 * An annotation to a transcript.
 */
@Entity
public class Annotation
{
	private Investigator fInvestigator;
	
	private String fComment;
	
	private Long fPersistenceId;

	/**
	 * @return The investigator who authored the annotation.
	 */
	@OneToOne
	public Investigator getInvestigator()
	{
		return fInvestigator;
	}

	/**
	 * @param investigator
	 */
	public void setInvestigator(Investigator investigator)
	{
		this.fInvestigator = investigator;
	}

	/**
	 * @return
	 */
	@Type(type = "text")
	public String getComment()
	{
		return fComment;
	}

	/**
	 * @param comment
	 */
	public void setComment(String comment)
	{
		this.fComment = comment;
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
	
	
	
}
