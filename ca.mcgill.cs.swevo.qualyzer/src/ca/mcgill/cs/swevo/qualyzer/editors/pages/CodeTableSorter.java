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
/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.editors.pages;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import ca.mcgill.cs.swevo.qualyzer.editors.inputs.CodeTableInput.CodeTableRow;

/**
 * @author Jonathan Faubert
 *
 */
public class CodeTableSorter extends ViewerSorter
{
	private int fColIndex;
	private boolean fDescending;
	
	/**
	 * 
	 */
	public CodeTableSorter()
	{
		fColIndex = 0;
		fDescending = false;
	}
	
	/**
	 * Change the column to sort by.
	 * @param col
	 */
	public void setColumn(int col)
	{
		if(col == fColIndex)
		{
			fDescending = !fDescending;
		}
		else
		{
			fColIndex = col;
			fDescending = false;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerComparator#compare(
	 * org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2)
	{
		int val = super.compare(viewer, e1, e2);
		
		if(fDescending && fColIndex == 0)
		{
			val = -val;
		}
		
		return val;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerComparator#category(java.lang.Object)
	 */
	@Override
	public int category(Object element)
	{
		if(fColIndex == 1)
		{
			CodeTableRow row = (CodeTableRow) element;
			return fDescending ? -row.getFrequency() : row.getFrequency();
		}
		
		return 0;
	}
}
