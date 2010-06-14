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


package ca.mcgill.cs.swevo.qualyzer.util;

import java.lang.reflect.Field;
import java.util.Properties;

import org.eclipse.ui.internal.navigator.extensions.NavigatorViewerDescriptor;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
@SuppressWarnings("restriction")
public final class CommonViewerUtil
{
	private CommonViewerUtil(){};
	
	/**
	 * Set the property of the CommonViewer to hide the link button and the customise actions.
	 * @param desc The NavigatorViewerDescriptor.
	 * @param prop The property you want to change.
	 * @param val The value to change it to.
	 */
	public static void setProperty(NavigatorViewerDescriptor desc, String prop, String val)
	{
		try
		{
			Field[] fields = desc.getClass().getDeclaredFields();
			
			for(Field field : fields)
			{
				if(field.getName().equals("properties")) //$NON-NLS-1$
				{
					field.setAccessible(true);
					Object object = field.get(desc);
					Properties properties = (Properties) object;
					properties.setProperty(prop, val);
					field.setAccessible(false);
				}
			}
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}
}
