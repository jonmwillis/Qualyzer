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

import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 * Validates the business rules when a new code is created:
 * - Name non-empty
 * - Name not already in use (except if it's oldName)
 * - Name in alphanumerical+ format.
 */
public class CodeValidator extends AbstractValidator
{
	private final String fName;
	private final Project fProject;
	private final String fOldName;
	
	/**
	 * Constructs a new CodeValidator.
	 * @param pName The name chosen for the new code.
	 * @param pOldName The current name of the code (null if this is a new code).
	 * @param pProject The Project in which the code is to be created.
	 */
	public CodeValidator(String pName, String pOldName, Project pProject)
	{
		fName = pName;
		fOldName = pOldName;
		fProject = pProject;
	}
	
	/**
	 * Constructs a new CodeValidator with a null old name.
	 * @param pName The name chosen for the new code.
	 * @param pProject The Project in which the code is to be created.
	 */
	public CodeValidator(String pName, Project pProject)
	{
		this(pName, null, pProject);
	}
	
	@Override
	public boolean isValid() 
	{
		boolean lReturn = true;
		
		if(fName.length() == 0)
		{
			lReturn = false;
			fMessage = Messages.getString("model.validation.CodeValidator.empty");  //$NON-NLS-1$
		}
		else if(!ValidationUtils.verifyID(fName))
		{
			lReturn = false;
			fMessage = Messages.getString(
					"model.validation.CodeValidator.invalid");  //$NON-NLS-1$
		}
		else if(nameInUse())
		{
			if((fOldName == null) || (!fName.equals(fOldName)))
			{
				lReturn = false;
				fMessage = Messages.getString("model.validation.CodeValidator.taken");  //$NON-NLS-1$
			}
		}
		
		return lReturn;
	}
	
	private boolean nameInUse()
	{
		for(Code code : fProject.getCodes())
		{
			if(code.getCodeName().equals(fName))
			{
				return true;
			}
		}
		
		return false;
	}
}
