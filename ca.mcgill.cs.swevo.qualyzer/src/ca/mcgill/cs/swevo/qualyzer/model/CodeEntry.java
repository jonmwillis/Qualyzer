/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     McGill University - initial API and implementation
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 *
 */
@Entity
public class CodeEntry
{
	private Code fCode;
	
	private Investigator fInvestigator;
	
	private Long fPersistenceId;

	@OneToOne
	public Code getCode()
	{
		return fCode;
	}

	public void setCode(Code code)
	{
		this.fCode = code;
	}

	@OneToOne
	public Investigator getInvestigator()
	{
		return fInvestigator;
	}

	public void setInvestigator(Investigator investigator)
	{
		this.fInvestigator = investigator;
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
