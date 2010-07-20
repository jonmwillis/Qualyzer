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
/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;

import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.model.TranscriptListener;
import ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;

/**
 *
 */
public class TranscriptEditor extends RTFEditor implements TranscriptListener
{
	public static final String ID = "ca.mcgill.cs.swevo.qualyzer.editors.transcriptEditor"; //$NON-NLS-1$
	
	
	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.editors.RTFEditor#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		// TODO Auto-generated method stub
		super.createPartControl(parent);
		
		Facade.getInstance().getListenerManager().registerTranscriptListener(getDocument().getProject(), this);
	}
	
	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.model.TranscriptListener#transcriptChanged(
	 * ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType, ca.mcgill.cs.swevo.qualyzer.model.Transcript[],
	 *  ca.mcgill.cs.swevo.qualyzer.model.Facade)
	 */
	@Override
	public void transcriptChanged(ChangeType cType, Transcript[] transcripts, Facade facade)
	{
		if(cType == ChangeType.DELETE)
		{
			for(Transcript transcript : transcripts)
			{
				if(transcript.equals(getDocument()))
				{
					IWorkbenchPage page = getSite().getPage();
					ResourcesUtil.closeEditor(page, getEditorInput().getName());
					break;
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.editors.RTFEditor#dispose()
	 */
	@Override
	public void dispose()
	{
		Facade.getInstance().getListenerManager().unregisterTranscriptListener(getDocument().getProject(), this);
		super.dispose();
	}

}
