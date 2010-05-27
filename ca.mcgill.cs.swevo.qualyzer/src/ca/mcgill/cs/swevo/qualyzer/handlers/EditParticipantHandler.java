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
package ca.mcgill.cs.swevo.qualyzer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import ca.mcgill.cs.swevo.qualyzer.editors.ParticipantFormEditor;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.ParticipantEditorInput;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;

/**
 * 
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class EditParticipantHandler extends AbstractHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{	
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		ISelection selection = page.getSelection();
		
		if(selection != null && selection instanceof IStructuredSelection)
		{
			IStructuredSelection strucSelection = (IStructuredSelection) selection;
			Object obj = strucSelection.getFirstElement();
			
			if(obj instanceof Participant)
			{
				ParticipantEditorInput input = new ParticipantEditorInput((Participant) obj);
				try
				{
					page.openEditor(input, ParticipantFormEditor.ID);
				}
				catch (PartInitException e)
				{
					e.printStackTrace();
				}
			}
		}
		return null;
	}

}
