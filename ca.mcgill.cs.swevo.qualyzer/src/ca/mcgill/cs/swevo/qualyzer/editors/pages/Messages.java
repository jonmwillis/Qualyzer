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
package ca.mcgill.cs.swevo.qualyzer.editors.pages;

import org.eclipse.osgi.util.NLS;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "ca.mcgill.cs.swevo.qualyzer.editors.pages.messages"; //$NON-NLS-1$
	public static String editors_pages_InvestigatorEditorPage_CodedInterviews;
	public static String editors_pages_InvestigatorEditorPage_ConductedInterviews;
	public static String editors_pages_InvestigatorEditorPage_FulllName;
	public static String editors_pages_InvestigatorEditorPage_Instituion;
	public static String editors_pages_InvestigatorEditorPage_Investigator;
	public static String editors_pages_InvestigatorEditorPage_Memos;
	public static String editors_pages_InvestigatorEditorPage_Nickname;
	public static String editors_pages_ParticipantEditorPage_id;
	public static String editors_pages_ParticipantEditorPage_Name;
	public static String editors_pages_ParticipantEditorPage_participant;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
