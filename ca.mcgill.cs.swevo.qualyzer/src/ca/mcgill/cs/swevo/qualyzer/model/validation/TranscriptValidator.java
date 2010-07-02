/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Robillard
 *******************************************************************************/

package ca.mcgill.cs.swevo.qualyzer.model.validation;

import java.io.File;

import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;

/**
 * Validates the business rules when a new transcript is created:
 * - The transcript name is not already in use
 * - The transcript name is not empty
 * - The transcript name is in alphanumerical+ format.
 * - At least one participant is associated with the transcript
 * - The name of the audio file is not empty but does not refer to an existing file
 */
public class TranscriptValidator extends AbstractValidator
{
	private final String fName;
	private final Project fProject;
	private final int fNumberOfParticipants;
	private final String fAudioFileName;
	
	/**
	 * Constructs a new ParticipantValidator.
	 * @param pName The name chosen for the new transcript.
	 * @param pProject The Project in which the transcript is to be created.
	 */
	public TranscriptValidator(String pName, Project pProject, int pNumberOfParticipants, String pAudioFileName)
	{
		fName = pName;
		fProject = pProject;
		fNumberOfParticipants = pNumberOfParticipants;
		fAudioFileName = pAudioFileName;
	}
	
	@Override
	public boolean isValid() 
	{
		boolean lReturn = true;
		
		if(fName.length() == 0)
		{
			fMessage = Messages.getString("model.validation.TranscriptValidator.enterName"); //$NON-NLS-1$
			lReturn = false;
		}
		else if(!ResourcesUtil.verifyID(fName))
		{
			fMessage = Messages.getString("model.validation.TranscriptValidator.invalidName"); //$NON-NLS-1$
			lReturn = false;
		}
		else if(transcriptExists())
		{
			fMessage = Messages.getString("model.validation.TranscriptValidator.nameInUse"); //$NON-NLS-1$
			lReturn = false;
		}
		else if(fNumberOfParticipants <= 0)
		{
			fMessage = Messages.getString("model.validation.TranscriptValidator.selectOne"); //$NON-NLS-1$
			lReturn = false;
		}
		else
		{
			File file = new File(fAudioFileName);
			if((fAudioFileName.length() != 0) && !file.exists())
			{
				fMessage = Messages.getString("model.validation.TranscriptValidator.enterAudioName"); //$NON-NLS-1$
				lReturn = false;
			}
		}
	
		return lReturn;
	}
	
	/**
	 * @return true if the transcript name refers to a transcript in the project.
	 */
	protected boolean transcriptExists()
	{
		for(Transcript transcript : fProject.getTranscripts())
		{
			if(transcript.getName().equals(fName))
			{
				return true;
			}
		}
		return false;
	}
}
