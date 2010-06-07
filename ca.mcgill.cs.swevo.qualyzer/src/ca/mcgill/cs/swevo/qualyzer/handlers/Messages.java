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
package ca.mcgill.cs.swevo.qualyzer.handlers;

import org.eclipse.osgi.util.NLS;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "ca.mcgill.cs.swevo.qualyzer.handlers.messages"; //$NON-NLS-1$
	public static String _handlers_DeleteInvestigatorHandler_cannotDelete;
	public static String _handlers_DeleteInvestigatorHandler_confirm;
	public static String _handlers_DeleteInvestigatorHandler_conflicts;
	public static String _handlers_DeleteInvestigatorHandler_deleteInvestigator;
	public static String _handlers_DeleteInvestigatorHandler_memo;
	public static String handlers_DeleteParticipantHandler_cannotDelete;
	public static String handlers_DeleteParticipantHandler_confirm;
	public static String handlers_DeleteParticipantHandler_conflicts;
	public static String handlers_DeleteParticipantHandler_deleteParticipant;
	public static String handlers_DeleteParticipantHandler_memo;
	public static String handlers_DeleteParticipantHandler_transcript;
	public static String handlers_ImportAudioFileHandler_audio;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
