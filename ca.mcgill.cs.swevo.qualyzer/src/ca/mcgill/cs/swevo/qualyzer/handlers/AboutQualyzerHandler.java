/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     McGill University - initial API and implementation
 *     Jonathan Faubert
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Handler for the About Qualyzer Command.
 *
 */
public class AboutQualyzerHandler extends AbstractHandler
{
	private static final String DESCRIPTION = 
		Messages.getString("handlers.AboutQualyzerHandler.description1") + //$NON-NLS-1$
			Messages.getString("handlers.AboutQualyzerHandler.description2"); //$NON-NLS-1$
	
	private static final String WEB = Messages.getString("handlers.AboutQualyzerHandler.website"); //$NON-NLS-1$
	
	private static final String COPYRIGHT = 
		Messages.getString("handlers.AboutQualyzerHandler.copyright1") + //$NON-NLS-1$
			Messages.getString("handlers.AboutQualyzerHandler.copyright2") + //$NON-NLS-1$
			Messages.getString("handlers.AboutQualyzerHandler.copyright3") + //$NON-NLS-1$
			Messages.getString("handlers.AboutQualyzerHandler.copyright4") + //$NON-NLS-1$
			Messages.getString("handlers.AboutQualyzerHandler.copyright5") + //$NON-NLS-1$
			Messages.getString("handlers.AboutQualyzerHandler.copyright6") + //$NON-NLS-1$
			Messages.getString("handlers.AboutQualyzerHandler.copyright7") + //$NON-NLS-1$
			Messages.getString("handlers.AboutQualyzerHandler.copyright8"); //$NON-NLS-1$
	
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		Shell shell = HandlerUtil.getActiveShell(event).getShell();
		
		MessageDialog.openInformation(shell, 
				Messages.getString("handlers.AboutQualyzerHandler.about"), DESCRIPTION+WEB+COPYRIGHT); //$NON-NLS-1$
		return null;
	}

}
