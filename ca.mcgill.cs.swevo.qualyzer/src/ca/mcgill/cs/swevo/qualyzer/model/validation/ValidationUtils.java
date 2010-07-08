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
 * Utility methods for input validation.
 */
public final class ValidationUtils 
{
	private ValidationUtils()
	{}
	
	/*
	 * Verifies that an id is valid. Package-private on purpose. To perform validation, a 
	 * validator object should be used.
	 * An id is valid if and only if it contains only alpha-numeric characters and '_' or '-'.
	 * @param id 
	 * @return 
	 */
	static boolean verifyID(String id)
	{
		for(int i = 0; i < id.length(); i++)
		{
			char c = id.charAt(i);
			if((c <= 'Z' && c >= 'A') || (c >= 'a' && c <= 'z')) //isAlpha
			{
				continue;
			}
			else if(c >= '0' && c <= '9' || c == '_' || c == '-') //is digit or _ or -
			{
				continue;
			}
			else
			{
				return false;
			}
		}
		
		return id.length() > 0;
	}

}
