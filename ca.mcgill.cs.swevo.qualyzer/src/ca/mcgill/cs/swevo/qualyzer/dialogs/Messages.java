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
package ca.mcgill.cs.swevo.qualyzer.dialogs;

import org.eclipse.osgi.util.NLS;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "ca.mcgill.cs.swevo.qualyzer.dialogs.messages"; //$NON-NLS-1$
	public static String dialogs_RenameDialog_AlreadyUsed;
	public static String dialogs_RenameDialog_AudioToo;
	public static String dialogs_RenameDialog_NewName;
	public static String dialogs_RenameDialog_RenameTheTranscript;
	public static String dialogs_RenameDialog_RenameTranscript;
	public static String dialogs_TranscriptDeleteDialog_confirm;
	public static String dialogs_TranscriptDeleteDialog_deleteAudio;
	public static String dialogs_TranscriptDeleteDialog_deleteCodes;
	public static String dialogs_TranscriptDeleteDialog_deleteParticipants;
	public static String dialogs_TranscriptDeleteDialog_deleteTranscript;
	public static String dialogs_TranscriptDeleteDialog_warning;
	public static String dialogs_TranscriptPropertiesDialog_addWhich;
	public static String dialogs_TranscriptPropertiesDialog_audioPath;
	public static String dialogs_TranscriptPropertiesDialog_audioType;
	public static String dialogs_TranscriptPropertiesDialog_Browse;
	public static String dialogs_TranscriptPropertiesDialog_date;
	public static String dialogs_TranscriptPropertiesDialog_name;
	public static String dialogs_TranscriptPropertiesDialog_needOne;
	public static String dialogs_TranscriptPropertiesDialog_participants;
	public static String dialogs_TranscriptPropertiesDialog_path;
	public static String dialogs_TranscriptPropertiesDialog_properties;
	public static String dialogs_TranscriptPropertiesDialog_warning;
	public static String dialogs_TranscriptPropertiesDialog_warning2;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
