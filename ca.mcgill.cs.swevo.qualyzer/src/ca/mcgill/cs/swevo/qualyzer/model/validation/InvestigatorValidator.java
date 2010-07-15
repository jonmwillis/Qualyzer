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

import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 * Validates the business rules when a new investigator is creates:
 * - Nick Name non-empty
 * - Nick Name not already in use
 * - Nick Name in alphanumerical+ format.
 */
public class InvestigatorValidator extends AbstractValidator
{
	private final String fName;
	private final String fOldName;
	private final Project fProject;
	
	/**
	 * Constructs a new InvestigatorValidator.
	 * @param pName The ID chosen for the new investigator.
	 * @param pOldName The current ID of the investigator (if applicable). Null if there are none.
	 * @param pProject The Project in which the investigator is to be created.
	 */
	public InvestigatorValidator(String pName, String pOldName, Project pProject)
	{
		fName = pName;
		fOldName = pOldName;
		fProject = pProject;
	}
	
	/**
	 * Constructs a new InvestigatorValidator with a null old name.
	 * @param pName The ID chosen for the new investigator.
	 * @param pProject The Project in which the investigator is to be created.
	 */
	public InvestigatorValidator(String pName, Project pProject)
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
			fMessage = Messages.getString("model.validation.InvestigatorValidator.emptyInvestigatorID");  //$NON-NLS-1$
		}
		else if(!ValidationUtils.verifyID(fName))
		{
			lReturn = false;
			fMessage = Messages.getString(
					"model.validation.InvestigatorValidator.invalidInvestigatorName");  //$NON-NLS-1$
		}
		else if(idInUse())
		{
			if((fOldName == null) || (!fName.equals(fOldName)))
			{
				lReturn = false;
				fMessage = Messages.getString("model.validation.InvestigatorValidator.nicknameTaken");  //$NON-NLS-1$
			}
		}
		
		return lReturn;
	}
	
	private boolean idInUse()
	{
		for(Investigator investigator : fProject.getInvestigators())
		{
			if(investigator.getNickName().equals(fName))
			{
				return true;
			}
		}
		return false;
	}
}
