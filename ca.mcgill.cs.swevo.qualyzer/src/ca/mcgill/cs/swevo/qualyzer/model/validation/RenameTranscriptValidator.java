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

import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;

/**
 * Validates the business rules when a transcript is renamed:
 * - New name non-empty
 * - New name not already in use (except if it refers to the current transcript)
 * - New name in alphanumerical+ format.
 */
public class RenameTranscriptValidator extends AbstractValidator
{
	private final String fName;
	private final Project fProject;
	private String fOldName;
	/**
	 * Constructs a new Validator.
	 * @param pName The name chosen for the new transcript.
	 * @param pOldName The current name of the transcript.
	 * @param pProject The Project in which the transcript exists.
	 */
	public RenameTranscriptValidator(String pName, String pOldName, Project pProject)
	{
		fName = pName;
		fOldName = pOldName;
		fProject = pProject;
	}
	
	@Override
	public boolean isValid() 
	{
		boolean lReturn = true;
		
		if(fName.length() == 0)
		{
			lReturn = false;
			fMessage = Messages.getString("model.validation.RenameTranscriptValidator.nameEmpty");  //$NON-NLS-1$
		}
		else if(!ValidationUtils.verifyID(fName))
		{
			lReturn = false;
			fMessage = Messages.getString(
					"model.validation.RenameTranscriptValidator.invalidID");  //$NON-NLS-1$
		}
		else if(transcriptExists())
		{
			lReturn = false;
			fMessage = Messages.getString("model.validation.RenameTranscriptValidator.nameInUse");  //$NON-NLS-1$
		}
		
		return lReturn;
	}
	
	private boolean transcriptExists()
	{
		for(Transcript transcript : fProject.getTranscripts())
		{
			if(!fOldName.equals(fName) && transcript.getName().equals(fName))
			{
				return true;
			}
		}
		return false;
	}
}
