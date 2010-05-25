/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     McGill University - initial API and implementation
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.providers;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import ca.mcgill.cs.swevo.qualyzer.model.Investigator;

/**
 * Label Provider for Investigators.
 * @author Jonathan Faubert
 *
 */
public class InvestigatorLabelProvider extends LabelProvider implements ILabelProvider
{

	@Override
	public Image getImage(Object element)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getText(Object element)
	{
		if(element instanceof Investigator)
		{
			return ((Investigator) element).getFullName();
		}
		return null;
	}
}
