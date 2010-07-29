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
package ca.mcgill.cs.swevo.qualyzer;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 *
 */
public class QualyzerPreferenceInitializer extends AbstractPreferenceInitializer
{

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences node = new DefaultScope().getNode(QualyzerActivator.PLUGIN_ID);
		node.put(IQualyzerPreferenceConstants.DEFAULT_INVESTIGATOR, System.getProperty("user.name")); //$NON-NLS-1$
		node.put(IQualyzerPreferenceConstants.FRAGMENT_COLOR, "218,218,218"); //$NON-NLS-1$

	}

}
