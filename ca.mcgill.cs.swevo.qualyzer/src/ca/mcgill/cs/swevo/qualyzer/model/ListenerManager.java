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
package ca.mcgill.cs.swevo.qualyzer.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class ListenerManager
{
	private HashMap<Project, ArrayList<ProjectListener>> fProjectListeners;

	/**
	 * Defines the various reasons listeners can be notified.
	 */
	public enum ChangeType
	{ADD, DELETE, MODIFY}
	
	/**
	 * Constructor.
	 */
	public ListenerManager()
	{
		fProjectListeners = new HashMap<Project, ArrayList<ProjectListener>>();
	}
	
	/**
	 * Register a ProjectListener with a particular project.
	 * @param project
	 * @param listener
	 */
	public void registerProjectListener(Project project, ProjectListener listener)
	{
		ArrayList<ProjectListener> listenerList = fProjectListeners.get(project);
		
		if(listenerList == null)
		{
			listenerList = new ArrayList<ProjectListener>();
		}
		
		listenerList.add(listener);
		fProjectListeners.put(project, listenerList);
	}
	
	/**
	 * Notify the registered project listeners that the given project has changed.
	 * @param cType
	 * @param project
	 * @param facade
	 */
	public void notifiyProjectListeners(ChangeType cType, Project project, ModelFacade facade)
	{
		for(ProjectListener listener : fProjectListeners.get(project))
		{
			listener.projectChanged(cType, project, facade);
		}
	}
}
