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
 * Validates the business rules when a new investigator is created.
 */
public class InvestigatorValidator extends BasicNameValidator
{
	private static final int MAX_LENGTH = 255;
	
	private String fFullName;
	private String fInstitution;
	
	/**
	 * Constructs a new InvestigatorValidator.
	 * @param pName The ID chosen for the new investigator.
	 * @param pOldName The current ID of the investigator (if applicable). Null if there are none.
	 * @param pFullName The full name of the investigator.
	 * @param pInstitution The institution of the investigator.
	 * @param pProject The Project in which the investigator is to be created.
	 */
	public InvestigatorValidator(String pName, String pOldName, String pFullName, 
			String pInstitution, Project pProject)
	{
		super(Messages.getString("model.validation.InvestigatorValidator.label"), //$NON-NLS-1$
				pName, pOldName, pProject); 
		fFullName = pFullName;
		fInstitution = pInstitution;
	}
	
	/**
	 * Constructs a new InvestigatorValidator with a null old name.
	 * @param pName The ID chosen for the new investigator.
	 * @param pFullName The full name of the investigator.
	 * @param pInstitution The institution of the investigator.
	 * @param pProject The Project in which the investigator is to be created.
	 */
	public InvestigatorValidator(String pName, String pFullName, String pInstitution, Project pProject)
	{
		this(pName, null, pFullName, pInstitution, pProject);
	}
	
	@Override
	public boolean isValid()
	{
		boolean valid = super.isValid();
		
		if(valid)
		{
			if(fFullName.length() > MAX_LENGTH)
			{
				fMessage = Messages.getString("model.validation.InvestigatorValidator.fullNameTooLong"); 
				valid = false;
			}
			else if(fInstitution.length() > MAX_LENGTH)
			{
				fMessage = Messages.getString("model.validation.InvestigatorValidator.institutionTooLong"); 
				valid = false;
			}
		}
		return valid;
	}
	
	@Override
	protected boolean nameInUse()
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
