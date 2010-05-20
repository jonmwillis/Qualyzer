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

import org.hibernate.annotations.Type;

/**
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 * 
 */
@Entity
public class Participant
{

	private String fParticipantId;

	private String fFullName;

	private String fNotes;

	private Long fPersistenceId;

	public String getParticipantId()
	{
		return fParticipantId;
	}

	public void setParticipantId(String participantId)
	{
		this.fParticipantId = participantId;
	}

	public String getFullName()
	{
		return fFullName;
	}

	public void setFullName(String fullName)
	{
		this.fFullName = fullName;
	}

	@Type(type = "text")
	public String getNotes()
	{
		return fNotes;
	}

	public void setNotes(String notes)
	{
		this.fNotes = notes;
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

}
