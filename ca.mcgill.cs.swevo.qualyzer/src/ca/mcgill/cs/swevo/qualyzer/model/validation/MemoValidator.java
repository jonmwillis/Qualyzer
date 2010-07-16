/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jonathan Faubert
 *******************************************************************************/
/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.model.validation;

import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 *
 */
public class MemoValidator extends AbstractValidator
{
	private Project fProject;
	private String fName;
	private Investigator fAuthor;
	
	/**
	 * 
	 * @param pName
	 * @param investigator
	 * @param project
	 */
	public MemoValidator(String pName, Investigator investigator, Project project)
	{
		fName = pName;
		fAuthor = investigator;
		fProject = project;
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.model.validation.IValidator#isValid()
	 */
	@Override
	public boolean isValid()
	{
		boolean valid = true;
		
		if(fName.isEmpty())
		{
			valid = false;
			fMessage = Messages.getString("model.validation.MemoValidator.emptyName"); //$NON-NLS-1$
		}
		else if(!ValidationUtils.verifyID(fName))
		{
			valid = false;
			fMessage = Messages.getString("model.validation.MemoValidator.invalidName"); //$NON-NLS-1$
		}
		else if(nameIsTaken())
		{
			valid = false;
			fMessage = Messages.getString("model.validation.MemoValidator.nameTaken"); //$NON-NLS-1$
		}
		else if(fAuthor == null)
		{
			valid = false;
			fMessage = Messages.getString("model.validation.MemoValidator.chooseAuthor"); //$NON-NLS-1$
		}
		
		return valid;
	}

	/**
	 * @return
	 */
	private boolean nameIsTaken()
	{
		for(Memo memo : fProject.getMemos())
		{
			if(memo.getName().equals(fName))
			{
				return true;
			}
		}
		return false;
	}

}
