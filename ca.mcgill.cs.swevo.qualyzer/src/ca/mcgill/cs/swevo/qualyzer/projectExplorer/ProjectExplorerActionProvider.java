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
package ca.mcgill.cs.swevo.qualyzer.projectExplorer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;

import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;

/**
 * 
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 * @author Jonathan Faubert (jonfaub@gmail.com)
 */
public class ProjectExplorerActionProvider extends CommonActionProvider
{
	/**
	 * 
	 */
	private static final String OPEN_ALL_COMMAND_ID = "ca.mcgill.cs.swevo.qualyzer.commands.openAll";
	private IAction fDoubleClickAction;

	/**
	 * Initializes common actions such as Open.
	 * 
	 * @param aSite
	 * @see org.eclipse.ui.navigator.CommonActionProvider#init(org.eclipse.ui.navigator.ICommonActionExtensionSite)
	 */
	@Override
	public void init(ICommonActionExtensionSite aSite)
	{
		super.init(aSite);
		final IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(
				IHandlerService.class);
		fDoubleClickAction = new Action()
		{
			@Override
			public void run()
			{
				ISelection selection;
				selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
				if(selection != null && selection instanceof IStructuredSelection)
				{
					Object element = ((IStructuredSelection) selection).getFirstElement();
				
					String commandId = getCommandId(element);
					
					try
					{
						handlerService.executeCommand(commandId, null);
					}
					// CSOFF:
					catch (Exception e)
					{
						e.printStackTrace();
					}//CSON:
				}
			}
		};
	}
	
	/**
	 * @param element
	 * @return
	 */
	private String getCommandId(Object element)
	{
		String commandId = ""; //$NON-NLS-1$
		if(element instanceof Participant)
		{
			commandId = OPEN_ALL_COMMAND_ID; //$NON-NLS-1$
		}
		else if(element instanceof Investigator)
		{
			commandId = OPEN_ALL_COMMAND_ID; //$NON-NLS-1$
		}
		else if(element instanceof Transcript)
		{
			commandId = OPEN_ALL_COMMAND_ID; //$NON-NLS-1$
		}
		return commandId;
	}

	@Override
	public void fillActionBars(IActionBars actionBars)
	{
		super.fillActionBars(actionBars);
		// forward doubleClick to doubleClickAction
		if(fDoubleClickAction.isEnabled())
		{
			actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN, fDoubleClickAction);
		}
	}

}
