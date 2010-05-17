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
package ca.mcgill.cs.swevo.qualyzer.editors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 * 
 */
@SuppressWarnings("unchecked")
public class ColorManager
{

	protected Map fColorTable = new HashMap();

	
	public void dispose()
	{
		Iterator e = fColorTable.values().iterator();
		while (e.hasNext())
		{
			((Color) e.next()).dispose();
		}
	}

	public Color getColor(RGB rgb)
	{
		Color color = (Color) fColorTable.get(rgb);
		if (color == null)
		{
			color = new Color(Display.getCurrent(), rgb);
			fColorTable.put(rgb, color);
		}
		return color;
	}
}
