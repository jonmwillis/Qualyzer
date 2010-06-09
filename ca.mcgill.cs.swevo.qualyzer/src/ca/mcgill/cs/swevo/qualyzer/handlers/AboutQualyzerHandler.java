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
	private static final String COPYRIGHT = "Copyright (c) 2010 McGill University" +
			"\nAll rights reserved. This program and the accompanying materials" +
			"\nare made available under the terms of the Eclipse Public License v1.0" +
			"\nwhich accompanies this distribution, and is available at" +
			"\nhttp://www.eclipse.org/legal/epl-v10.html" +
			"\n\nContributors:" +
			"\n\tMcGill University - initial API and implementation";
	
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		Shell shell = HandlerUtil.getActiveShell(event).getShell();
		
		MessageDialog.openInformation(shell, "About Qualyzer", COPYRIGHT);
		return null;
	}

}
