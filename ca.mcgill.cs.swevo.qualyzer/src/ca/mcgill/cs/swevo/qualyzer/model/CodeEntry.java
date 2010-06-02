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

/**
  */
@Entity
public class CodeEntry
{
	private Code fCode;
	
	private Investigator fInvestigator;
	
	private Long fPersistenceId;

	/**
	 * @return
	 */
	@OneToOne
	public Code getCode()
	{
		return fCode;
	}

	/**
	 * @param code
	 */
	public void setCode(Code code)
	{
		this.fCode = code;
	}

	/**
	 * @return
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
