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

import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 *
 */
public class RenameMemoValidator extends AbstractValidator
{
	private Project fProject;
	private String fNewName;
	private String fOldName;

	/**
	 * Constructor.
	 * @param newName
	 * @param oldName
	 * @param project
	 */
	public RenameMemoValidator(String newName, String oldName, Project project)
	{
		fNewName = newName;
		fOldName = oldName;
		fProject = project;
	}
	
	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.model.validation.IValidator#isValid()
	 */
	@Override
	public boolean isValid()
	{
		boolean valid = true;
		
		if(fNewName.isEmpty())
		{
			valid = false;
			fMessage = "The name cannot be empty.";
		}
		else if(!ValidationUtils.verifyID(fNewName))
		{
			valid = false;
			fMessage = "The name is invalid. It can contain letters, numbers, - and _.";
		}
		else if(memoExists())
		{
			valid = false;
			fMessage = "That name is already taken.";
		}
		
		return valid;
	}
	
	private boolean memoExists()
	{
		for(Memo memo : fProject.getMemos())
		{
			if(!fOldName.equals(fNewName) && memo.getName().equals(fNewName))
			{
				return true;
			}
		}
		return false;
	}

}
