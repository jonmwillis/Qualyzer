/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jonathan Faubert
 *******************************************************************************/
/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.providers;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 * A ProjectWrapper for Participants.
 *
 */
public class WrapperParticipant extends ProjectWrapper
{
	private static final int NUM1 = 51175;
	private static final int NUM2 = 10275;
	
	private static final String RESOURCE = "participants"; //$NON-NLS-1$

	/**
	 * @param project
	 */
	public WrapperParticipant(Project project)
	{
		super(project);
		fResource = RESOURCE;
	}
	
	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(NUM1, NUM2).appendSuper(super.hashCode()).toHashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null)
		{
			return false;
		}
		if(obj == this)
		{
			return true;
		}
		if(obj instanceof WrapperParticipant)
		{
			return new EqualsBuilder().appendSuper(super.equals(obj)).isEquals();
		}
		
		return false;
			
	}

}
