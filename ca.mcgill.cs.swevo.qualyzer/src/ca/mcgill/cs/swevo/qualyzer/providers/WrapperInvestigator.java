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
/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.providers;

import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 * A ProjectWrapper for Investigators.
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class WrapperInvestigator extends ProjectWrapper
{

	private static final String RESOURCE = "investigators";

	/**
	 * @param project
	 */
	public WrapperInvestigator(Project project)
	{
		super(project);
	}
	
	public String getResource()
	{
		return RESOURCE;
	}
	
	@Override
	public int hashCode()
	{
		return super.hashCode();
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
		if(obj instanceof WrapperInvestigator)
		{
			return super.equals(obj);
		}
		
		return false;
			
	}

}
