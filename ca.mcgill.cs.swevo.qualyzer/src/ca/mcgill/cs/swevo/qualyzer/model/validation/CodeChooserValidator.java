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

import ca.mcgill.cs.swevo.qualyzer.model.CodeEntry;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 *
 */
public class CodeChooserValidator extends CodeValidator
{
	private Fragment fFragment;

	/**
	 * Constructor.
	 * @param pName
	 * @param pProject
	 * @param pFragment
	 */
	public CodeChooserValidator(String pName, Project pProject, Fragment pFragment)
	{
		super(pName, pProject);
		fFragment = pFragment;
	}
	
	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.model.validation.CodeValidator#isValid()
	 */
	@Override
	public boolean isValid()
	{
		boolean valid = super.isValid();
		
		if(fFragment != null && codeInUse(getName()))
		{
			fMessage = "This code is already attached to this fragment.";
			return false;
		}
		
		return valid;
	}

	/**
	 * @param name
	 * @return
	 */
	private boolean codeInUse(String name)
	{
		for(CodeEntry entry : fFragment.getCodeEntries())
		{
			if(entry.getCode().getCodeName().equals(name))
			{
				return true;
			}
		}
		return false;
	}
}
