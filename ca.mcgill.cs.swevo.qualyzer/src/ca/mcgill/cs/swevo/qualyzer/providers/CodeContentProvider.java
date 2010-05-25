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

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ca.mcgill.cs.swevo.qualyzer.model.Code;

/**
 * Content Provider for Codes.
 * @author Jonathan Faubert
 *
 */
public class CodeContentProvider implements ITreeContentProvider
{
	private static final Object[] NO_CHILDREN = new Object[0];
	

	@Override
	public Object[] getChildren(Object parentElement)
	{
		Object[] children = null;
		
		//TODO finish this method
		
		return children != null ? children : NO_CHILDREN;
	}

	@Override
	public Object getParent(Object element)
	{
		if(element instanceof Code)
		{
			return ((Code) element).getProject();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element)
	{
		//TODO ??
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement)
	{
		return getChildren(inputElement);
	}

	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		// TODO Auto-generated method stub

	}

}
