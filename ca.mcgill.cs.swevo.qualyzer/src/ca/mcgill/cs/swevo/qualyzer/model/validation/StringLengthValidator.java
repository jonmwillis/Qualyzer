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

/**
 * Validates that strings are not longer than MAX_LENGTH.
 */
public class StringLengthValidator extends AbstractValidator
{
	private static final int MAX_LENGTH = 255;
	
	private String fString;
	private String fLabel;
	
	/**
	 * Creates a new validator.
	 * @param pLabel A description of the string being validated.
	 * @param pString The string to validate.
	 */
	public StringLengthValidator(String pLabel, String pString)
	{
		fLabel = pLabel;
		fString = pString;
	}

	@Override
	public boolean isValid()
	{
		boolean lReturn = true;
		if(fString.length() > MAX_LENGTH)
		{
			lReturn = false;
			fMessage = fLabel + " " + Messages.getString("model.validation.StringLengthValidator.tooLong");
		}
		return lReturn;
	}

}
