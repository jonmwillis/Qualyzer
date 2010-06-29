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

import ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public interface ProjectListener
{

	/**
	 * Notification that a project has changed.
	 * @param cType
	 * @param project
	 * @param facade
	 */
	void projectChanged(ChangeType cType, Project[] projects, Facade facade);
}
