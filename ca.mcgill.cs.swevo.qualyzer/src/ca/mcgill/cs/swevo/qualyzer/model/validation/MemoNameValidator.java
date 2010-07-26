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
public class MemoNameValidator extends AbstractValidator
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
	public MemoNameValidator(String newName, String oldName, Project project)
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
			fMessage = Messages.getString("model.validation.RenameMemoValidator.emptyName"); //$NON-NLS-1$
		}
		else if(!ValidationUtils.verifyID(fNewName))
		{
			valid = false;
			fMessage = Messages.getString("model.validation.RenameMemoValidator.invalidName"); //$NON-NLS-1$
		}
		else if(memoExists())
		{
			valid = false;
			fMessage = Messages.getString("model.validation.RenameMemoValidator.nameTaken"); //$NON-NLS-1$
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
