/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Barthelemy Dagenais (bart@cs.mcgill.ca)
 *     Jonathan Faubert
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.model;

import java.util.List;

/**
 */
public interface IAnnotatedDocument
{
	
	/**
	 * @return
	 */
	String getName();
	
	/**
	 * @return
	 */
	List<Participant> getParticipants();
	
	/**
	 * @return
	 */
	List<Fragment> getFragments();
	
	/**
	 * @return
	 */
	Project getProject();
	
	/**
	 * Get the file name.
	 * @return
	 */
	String getFileName();
	
	/**
	 * 
	 * @return
	 */
	String getDate();
	
	/**
	 * Set the fragments.
	 * @param fragments
	 */
	void setFragments(List<Fragment> fragments);
	
	/**
	 * Set the project.
	 * @param project
	 */
	void setProject(Project project);

	/**
	 * @param name
	 */
	void setName(String name);
	
	/**
	 * 
	 * @param date
	 */
	void setDate(String date);
}
