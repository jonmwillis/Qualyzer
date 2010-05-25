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

import org.hibernate.annotations.CollectionId;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

/**
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 * 
 */
@Entity
@GenericGenerator(name = "uuid-gen", strategy = "uuid")
public class Fragment
{

	private List<Annotation> fAnnotations = new ArrayList<Annotation>();

	private List<CodeEntry> fCodeEntries = new ArrayList<CodeEntry>();

	private Long fPersistenceId;

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@CollectionId(columns = @Column(name = "COL_ID"), type = @Type(type = "string"), generator = "uuid-gen")
	public List<Annotation> getAnnotations()
	{
		return fAnnotations;
	}

	public void setAnnotations(List<Annotation> annotations)
	{
		this.fAnnotations = annotations;
	}

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@CollectionId(columns = @Column(name = "COL_ID"), type = @Type(type = "string"), generator = "uuid-gen")
	public List<CodeEntry> getCodeEntries()
	{
		return fCodeEntries;
	}

	public void setCodeEntries(List<CodeEntry> codeEntries)
	{
		this.fCodeEntries = codeEntries;
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
