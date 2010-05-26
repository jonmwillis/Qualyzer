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
package ca.mcgill.cs.swevo.qualyzer.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import ca.mcgill.cs.swevo.qualyzer.editors.inputs.ParticipantEditorInput;
import ca.mcgill.cs.swevo.qualyzer.editors.pages.ParticipantEditorPage;

/**
 * 
 * @author Jonathan Faubert
 *
 */
public class ParticipantFormEditor extends FormEditor
{
	public static final String ID = "ca.mcgill.cs.swevo.qualyzer.editors.ParticipantFormEditor";

	@Override
	protected void addPages()
	{
		IEditorInput input = getEditorInput();
		if(input instanceof ParticipantEditorInput)
		{
			ParticipantEditorInput partInput = (ParticipantEditorInput) input;
			try
			{
				addPage(new ParticipantEditorPage(this, partInput.getParticipant()));
				this.setPartName(partInput.getParticipant().getParticipantId());
			}
			catch(PartInitException e)
			{
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void doSave(IProgressMonitor monitor)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doSaveAs()
	{		
	}

	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}
}
