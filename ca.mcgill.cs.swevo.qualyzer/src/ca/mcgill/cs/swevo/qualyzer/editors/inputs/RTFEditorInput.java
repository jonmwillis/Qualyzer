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

package ca.mcgill.cs.swevo.qualyzer.editors.inputs;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.part.FileEditorInput;

import ca.mcgill.cs.swevo.qualyzer.model.Transcript;

/**
 *	Input for the RTFEditor.
 */
public class RTFEditorInput extends FileEditorInput
{
	private static final int NUM1 = 39753;
	private static final int NUM2 = 50071;
	
	private Transcript fTranscript;
	
	/**
	 * @param file
	 */
	public RTFEditorInput(IFile file, Transcript transcript)
	{
		super(file);
		fTranscript = transcript;
	}
	
	/**
	 * Get the transcript that serves as input.
	 * @return
	 */
	public Transcript getTranscript()
	{
		return fTranscript;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.FileEditorInput#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null)
		{
			return false;
		}
		else if(obj.getClass().equals(getClass()))
		{
			RTFEditorInput rhs = (RTFEditorInput) obj;
			return new EqualsBuilder().appendSuper(super.equals(obj)).append(fTranscript, rhs.fTranscript).isEquals();
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.FileEditorInput#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(NUM1, NUM2).appendSuper(super.hashCode()).append(fTranscript).toHashCode();
	}
	
}
