/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Barthelemy Dagenais (bart@cs.mcgill.ca)v
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.editors;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
  */
public class ColorManager
{
	public static final RGB DEFAULT = new RGB(0, 0, 0);
	public static final RGB PROC_INSTR = new RGB(128, 128, 128);
	public static final RGB STRING = new RGB(0, 128, 0);
	public static final RGB TAG = new RGB(0, 0, 128);
	public static final RGB XML_COMMENT = new RGB(128, 0, 0);
	
	protected Map<RGB, Color> fColorTable = new HashMap<RGB, Color>();
	
	/**
	 * 
	 */
	public void dispose()
	{
		for(Color c : fColorTable.values())
		{
			c.dispose();
		}
	}

	/**
	 * @param rgb
	 * @return
	 */
	public Color getColor(RGB rgb)
	{
		Color color = fColorTable.get(rgb);
		if(color == null)
		{
			color = new Color(Display.getCurrent(), rgb);
			fColorTable.put(rgb, color);
		}
		return color;
	}
}
