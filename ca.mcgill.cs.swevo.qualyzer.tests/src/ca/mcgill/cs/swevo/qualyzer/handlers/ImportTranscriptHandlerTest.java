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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.TestUtil;
import ca.mcgill.cs.swevo.qualyzer.dialogs.QualyzerWizardDialog;
import ca.mcgill.cs.swevo.qualyzer.editors.IDialogTester;
import ca.mcgill.cs.swevo.qualyzer.editors.TranscriptEditor;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.wizards.pages.ImportTranscriptPage;

/**
 * @author Jonathan Faubert
 *
 */
public class ImportTranscriptHandlerTest
{
	private Project fProject;
	private IProject wProject;
	private IWorkbenchPage fPage;
	
	private static final String PROJECT = "Project";
	private static final String INV = "Inv";
	private static final String PART = "Part";
	private static final String DOC = "ImportDocument.rtf";
	private static final String DOC_NAME = "ImportDocument";

	@Before
	public void setUp()
	{
		fPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		fPage.closeAllEditors(false);
		
		fProject = Facade.getInstance().createProject(PROJECT, INV, "", "");
		Facade.getInstance().createParticipant(PART, "", fProject);
		
		wProject = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT);
	}
	
	@After
	public void tearDown()
	{
		Facade.getInstance().deleteProject(fProject);
	}
	
	@Test
	public void importTranscriptTest()
	{
		TestUtil.setProjectExplorerSelection(wProject);
		
		ImportTranscriptHandler handler = new ImportTranscriptHandler();
		handler.setTesting(true);
		handler.setTester(new IDialogTester()
		{
			
			@Override
			public void execute(Dialog dialog)
			{
				QualyzerWizardDialog wizard = (QualyzerWizardDialog) dialog;
				
				ImportTranscriptPage page = (ImportTranscriptPage) wizard.getCurrentPage();
				page.getFileText().setText(DOC);
				page.getNameText().setText(DOC_NAME);
				page.getTable().select(0);
				
				wizard.finishPressed();
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
		assertEquals(fProject.getTranscripts().size(), 1);
		
		Transcript transcript = fProject.getTranscripts().get(0);
		assertEquals(transcript.getName(), DOC_NAME);
		assertEquals(transcript.getFileName(), DOC);
		
		Transcript lTranscript = Facade.getInstance().forceTranscriptLoad(transcript);
		assertEquals(lTranscript.getParticipants().size(), 1);
		assertEquals(lTranscript.getParticipants().get(0), fProject.getParticipants().get(0));
		
		try
		{
			wProject.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		}
		catch (CoreException e)
		{
			fail();
		}
		
		IFile file = wProject.getFile("/transcripts/"+DOC);
		assertTrue(file.exists());
		
		assertEquals(fPage.getEditorReferences().length, 1);
		IEditorPart editor = fPage.getActiveEditor();
		assertTrue(editor instanceof TranscriptEditor);
	}
}
