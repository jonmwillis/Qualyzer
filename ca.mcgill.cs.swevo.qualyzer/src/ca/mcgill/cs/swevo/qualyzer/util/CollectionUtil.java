/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Name - Initial Contribution
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.util;

import java.util.Collections;
import java.util.List;

/**
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 * 
 */
public final class CollectionUtil
{

	private CollectionUtil()
	{

	}

	public static <T extends Comparable<T>> void insertSorted(List<T> sortedList, T element)
	{
		int index = Collections.binarySearch(sortedList, element);
		sortedList.add(-index - 1, element);
	}

}
