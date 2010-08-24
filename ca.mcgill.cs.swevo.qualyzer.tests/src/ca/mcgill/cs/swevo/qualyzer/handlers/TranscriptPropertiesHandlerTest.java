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
/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.handlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.TestUtil;
import ca.mcgill.cs.swevo.qualyzer.dialogs.TranscriptPropertiesDialog;
import ca.mcgill.cs.swevo.qualyzer.editors.IDialogTester;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;

/**
 * @author Jonathan Faubert
 *
 */
public class TranscriptPropertiesHandlerTest
{

	private static final String PROJECT = "Project";
	private static final String INV = "Inv";
	private static final String PART = "Part";
	private static final String TRANSCRIPT = "Transcript";
	private Project fProject;
	private Transcript fTranscript;

	@Before
	public void setUp()
	{
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
		fProject = TestUtil.createProject(PROJECT, INV, PART, TRANSCRIPT);
		fTranscript = fProject.getTranscripts().get(0);
	}
	
	@After
	public void tearDown()
	{
		Facade.getInstance().deleteProject(fProject);
	}
	
	@Test
	public void testTranscriptProperties()
	{
		TestUtil.setProjectExplorerSelection(fTranscript);
		
		TranscriptPropertiesHandler handler = new TranscriptPropertiesHandler();
		handler.setTesting(true);
		handler.setTester(new IDialogTester()
		{
			
			@Override
			public void execute(Dialog dialog)
			{
				TranscriptPropertiesDialog prop = (TranscriptPropertiesDialog) dialog;
				
				prop.getDateWidget().setDate(1981, 0, 1);
				
				prop.okPressed();
			}
		});
		
		try
		{
			handler.execute(null);
		}
		catch (ExecutionException e)
		{
			fail();
		}
		
		fProject = PersistenceManager.getInstance().getProject(PROJECT);
		fTranscript = fProject.getTranscripts().get(0);
		
		assertEquals(fTranscript.getDate(), "1/1/1981");
	}
}
