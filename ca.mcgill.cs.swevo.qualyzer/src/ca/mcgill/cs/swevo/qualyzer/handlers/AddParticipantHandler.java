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
import org.eclipse.jface.window.Window;
import org.eclipse.ui.handlers.HandlerUtil;

import ca.mcgill.cs.swevo.qualyzer.dialogs.AddParticipantDialog;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;

/**
 * Launches a dialog whenever the New Participant Command is clicked.
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class AddParticipantHandler extends AbstractHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		AddParticipantDialog dialog = new AddParticipantDialog(HandlerUtil.getActiveWorkbenchWindow(event).getShell());
		dialog.create();
		if(dialog.open() == Window.OK)
		{
			Participant participant = new Participant();
			participant.setParticipantId(dialog.getParticipantId());
			participant.setFullName(dialog.getFullname());
			
			//TODO add the participant to the relevant project
			//TODO open the participant editor
		}
		return null;
	}

}
