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

package ca.mcgill.cs.swevo.qualyzer.editors;

import java.util.HashMap;
import java.util.Set;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;

/**
 * The model for the Rich Text Document.
 *
 */
public class RTFDocument extends Document
{
	/**
	 * Stores Rich Text Tags as annotations.
	 */
	private HashMap<Position, Annotation> fAnnotations;
	
	/**
	 * 
	 */
	public RTFDocument()
	{
		fAnnotations = new HashMap<Position, Annotation>();
	}
	
	/**
	 * Add an annotation at the given position.
	 * @param position
	 * @param annotation
	 */
	public void addAnnotation(Position position, Annotation annotation)
	{
		fAnnotations.put(position, annotation);
	}
	
	/**
	 * Get the set of positions that have annotations.
	 * @return
	 */
	public Set<Position> getKeys()
	{
		return fAnnotations.keySet();
	}
	
	/**
	 * Get the annotation for the given position.
	 * @param position
	 * @return
	 */
	public Annotation getAnnotation(Position position)
	{
		return fAnnotations.get(position);
	}

}

