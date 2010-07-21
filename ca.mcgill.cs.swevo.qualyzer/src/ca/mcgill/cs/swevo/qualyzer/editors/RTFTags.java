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
	public static final String HEADER = "{\\rtf1\\ansi\\deff0\n"; //$NON-NLS-1$
	public static final String FOOTER = "\n}\n\0"; //$NON-NLS-1$
	
	public static final String BOLD_START = "b"; //$NON-NLS-1$
	public static final String BOLD_END = "b0"; //$NON-NLS-1$
	public static final String ITALIC_START = "i"; //$NON-NLS-1$
	public static final String ITALIC_END = "i0"; //$NON-NLS-1$
	public static final String UNDERLINE_START = "ul"; //$NON-NLS-1$
	public static final String UNDERLINE_END = "ulnone"; //$NON-NLS-1$
	public static final String NEW_LINE = "par"; //$NON-NLS-1$
	public static final String TAB = "tab"; //$NON-NLS-1$
	public static final String BACKSLASH = "\\"; //$NON-NLS-1$
	public static final String LEFT_BRACE = "{"; //$NON-NLS-1$
	public static final String RIGHT_BRACE = "}"; //$NON-NLS-1$
	public static final String PAR_DEFAULT = "pard"; //$NON-NLS-1$
	public static final String PLAIN = "plain"; //$NON-NLS-1$
	
	public static final String BOLD_START_TAG = "\\b"; //$NON-NLS-1$
	public static final String BOLD_END_TAG = "\\b0"; //$NON-NLS-1$
	public static final String ITALIC_START_TAG = "\\i"; //$NON-NLS-1$
	public static final String ITALIC_END_TAG = "\\i0"; //$NON-NLS-1$
	public static final String UNDERLINE_START_TAG = "\\ul"; //$NON-NLS-1$
	public static final String UNDERLINE_END_TAG = "\\ulnone"; //$NON-NLS-1$
	public static final String NEW_LINE_TAG = "\\par \n"; //$NON-NLS-1$
	public static final String TAB_TAG = "\\tab "; //$NON-NLS-1$
	
	public static final String FONT_TABLE = "fonttbl"; //$NON-NLS-1$
	public static final String STYLESHEET = "stylesheet"; //$NON-NLS-1$
	public static final String INFO = "info"; //$NON-NLS-1$
	public static final String IGNORE = "*"; //$NON-NLS-1$
	public static final String COLOR_TABLE = "colortbl;"; //$NON-NLS-1$
	public static final String COLOR_TABLE2 = "colortbl"; //$NON-NLS-1$
	
	public static final String[] IGNORE_GROUPS = {FONT_TABLE, STYLESHEET, INFO, IGNORE, COLOR_TABLE, COLOR_TABLE2};
	
	private RTFTags()
	{
		
	}
	
	
}
