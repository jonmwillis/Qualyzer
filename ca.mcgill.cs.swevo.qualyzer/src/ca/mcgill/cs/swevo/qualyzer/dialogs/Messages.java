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

package ca.mcgill.cs.swevo.qualyzer.dialogs;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public final class Messages
{
	private static final String BUNDLE_NAME = "ca.mcgill.cs.swevo.qualyzer.dialogs.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private Messages()
	{
	}

	/**
	 * Get the string with the given key.
	 * @param key
	 * @return
	 */
	public static String getString(String key)
	{
		try
		{
			return RESOURCE_BUNDLE.getString(key);
		}
		catch (MissingResourceException e)
		{
			return '!' + key + '!';
		}
	}
}
