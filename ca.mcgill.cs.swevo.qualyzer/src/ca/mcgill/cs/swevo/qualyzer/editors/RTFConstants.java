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
 * Stores all the ID's for the various annotations and actions.
 */
public final class RTFConstants
{
	public static final String BOLD_TYPE = "RTFEditor.annotation.bold";
	public static final String ITALIC_TYPE = "RTFEditor.annotation.italic";
	public static final String UNDERLINE_TYPE = "RTFEditor.annotation.underline";
	public static final String BOLD_ITALIC_TYPE = "RTFEditor.annotation.boldItalic";
	public static final String BOLD_UNDERLINE_TYPE = "RTFEditor.annotation.boldUnderline";
	public static final String ITALIC_UNDERLINE_TYPE = "RTFEditor.annotation.italicUnderline";
	public static final String BOLD_ITALIC_UNDERLINE_TYPE = "RTFEditor.annotation.boldItalicUnderline";
	
	public static final String FRAGMENT_TYPE = "RTFEditor.annotation.fragment";
	
	public static final String BOLD_ACTION_ID = "action.bold";
	public static final String ITALIC_ACTION_ID = "action.italic";
	public static final String UNDERLINE_ACTION_ID = "action.underline";
	
	private RTFConstants(){}
}