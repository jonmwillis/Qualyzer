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
import org.eclipse.ui.handlers.HandlerUtil;

import ca.mcgill.cs.swevo.qualyzer.editors.InvestigatorFormEditor;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.InvestigatorEditorInput;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;

/**
 * 
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class EditInvestigatorHandler extends AbstractHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchPage page = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();
		ISelection selection = page.getSelection();
		
		if(selection != null && selection instanceof IStructuredSelection)
		{
			IStructuredSelection structSelection = (IStructuredSelection) selection;
			Object element = structSelection.getFirstElement();
			if(element instanceof Investigator)
			{
				InvestigatorEditorInput input = new InvestigatorEditorInput((Investigator) element);
				try
				{
					page.openEditor(input, InvestigatorFormEditor.ID);
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
