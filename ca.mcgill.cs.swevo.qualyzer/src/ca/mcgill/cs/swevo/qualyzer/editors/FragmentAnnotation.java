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
package ca.mcgill.cs.swevo.qualyzer.editors;

import org.eclipse.jface.text.source.Annotation;

import ca.mcgill.cs.swevo.qualyzer.model.Fragment;

/**
 *	An annotation that marks a text fragment.
 */
public class FragmentAnnotation extends Annotation
{
	private Fragment fFragment;
	
	/**
	 * Constructor.
	 */
	public FragmentAnnotation(Fragment fragment)
	{
		super(RTFConstants.FRAGMENT_TYPE, true, ""); //$NON-NLS-1$
		fFragment = fragment;
	}
	
	/**
	 * Get the text fragment associated with this annotation.
	 * @return
	 */
	public Fragment getFragment()
	{
		return fFragment;
	}
}
