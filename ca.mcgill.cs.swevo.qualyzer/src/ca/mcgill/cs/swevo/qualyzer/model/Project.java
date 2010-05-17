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

/**
 * A qualitative project.
 * 
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 *
 */
public class Project
{
	private String fName;

	/**
	 * @return the name
	 */
	public String getName()
	{
		return fName;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.fName = name;
	}
	
	
	
}
