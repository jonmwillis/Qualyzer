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
package ca.mcgill.cs.swevo.qualyzer.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;

/**
 * 
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 * 
 */
public class ProjectExplorerActionProvider extends CommonActionProvider
{
	private IAction doubleClickAction;

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

		doubleClickAction = new Action()
		{
			@Override
			public void run()
			{
				try
				{
					handlerService.executeCommand("ca.mcgill.cs.swevo.qualyzer.commands.openInterview", null);
				}
				// CSOFF:
				catch (Exception e)
				{
					e.printStackTrace();
				}
				//CSON:
			}
		};

	}

	@Override
	public void fillActionBars(IActionBars actionBars)
	{
		super.fillActionBars(actionBars);
		// forward doubleClick to doubleClickAction
		actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN, doubleClickAction);
	}

}
