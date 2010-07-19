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
package ca.mcgill.cs.swevo.qualyzer.editors;

/**
 * Contains the various RTF Tags that the parser needs.
 */
public final class RTFTags
{
	public static final String HEADER = "{\\rtf1\\ansi\\deff0\n";
	public static final String FOOTER = "\n}\n\0";
	
	public static final String BOLD_START = "b";
	public static final String BOLD_END = "b0";
	public static final String ITALIC_START = "i";
	public static final String ITALIC_END = "i0";
	public static final String UNDERLINE_START = "ul";
	public static final String UNDERLINE_END = "ulnone";
	public static final String NEW_LINE = "par";
	public static final String TAB = "tab";
	public static final String BACKSLASH = "\\";
	public static final String PAR_DEFAULT = "pard";
	public static final String PLAIN = "plain";
	
	public static final String BOLD_START_TAG = "\\b";
	public static final String BOLD_END_TAG = "\\b0";
	public static final String ITALIC_START_TAG = "\\i";
	public static final String ITALIC_END_TAG = "\\i0";
	public static final String UNDERLINE_START_TAG = "\\ul";
	public static final String UNDERLINE_END_TAG = "\\ulnone";
	public static final String NEW_LINE_TAG = "\\par \n";
	public static final String TAB_TAG = "\\tab ";
	
	public static final String FONT_TABLE = "fonttbl";
	public static final String STYLESHEET = "stylesheet";
	public static final String INFO = "info";
	public static final String IGNORE = "*";
	public static final String COLOR_TABLE = "colortbl;";
	public static final String COLOR_TABLE2 = "colortbl";
	
	public static final String[] IGNORE_GROUPS = {FONT_TABLE, STYLESHEET, INFO, IGNORE, COLOR_TABLE, COLOR_TABLE2};
	
	private RTFTags()
	{
		
	}
	
	
}
