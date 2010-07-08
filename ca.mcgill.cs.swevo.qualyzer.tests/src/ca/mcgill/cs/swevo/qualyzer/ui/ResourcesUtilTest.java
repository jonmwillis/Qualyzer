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
package ca.mcgill.cs.swevo.qualyzer.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.model.validation.ValidationUtils;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperCode;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperInvestigator;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperMemo;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperParticipant;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperTranscript;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class ResourcesUtilTest
{

	/**
	 * Verify Ids.
	 */
	@Test
	public void verifyIDTest()
	{
		assertTrue(ValidationUtils.verifyID("aA1_-"));
		assertTrue(ValidationUtils.verifyID("aasdl_sSDFA-3425"));
		
		assertFalse(ValidationUtils.verifyID(""));
		assertFalse(ValidationUtils.verifyID(" "));
		assertFalse(ValidationUtils.verifyID("!"));
		assertFalse(ValidationUtils.verifyID("^"));
		assertFalse(ValidationUtils.verifyID("ffffsadfsadfieurASDFSDF84375987 "));
	}
	
	/**
	 * Test the getProject() method.
	 */
	@Test
	public void getProjectTest()
	{
		Project project = new Project();
		
		Investigator i = new Investigator();
		i.setProject(project);
		
		Participant p = new Participant();
		p.setProject(project);
		
		Memo m = new Memo();
		m.setProject(project);
		
		Transcript t = new Transcript();
		t.setProject(project);
		
		Code c = new Code();
		c.setProject(project);
		
		WrapperInvestigator wI = new WrapperInvestigator(project);
		WrapperParticipant wP = new WrapperParticipant(project);
		WrapperMemo wM = new WrapperMemo(project);
		WrapperCode wC = new WrapperCode(project);
		WrapperTranscript wT = new WrapperTranscript(project);
		
		assertEquals(ResourcesUtil.getProject(i), project);
		assertEquals(ResourcesUtil.getProject(p), project);
		assertEquals(ResourcesUtil.getProject(m), project);
		assertEquals(ResourcesUtil.getProject(t), project);
		assertEquals(ResourcesUtil.getProject(c), project);
		assertEquals(ResourcesUtil.getProject(wI), project);
		assertEquals(ResourcesUtil.getProject(wP), project);
		assertEquals(ResourcesUtil.getProject(wM), project);
		assertEquals(ResourcesUtil.getProject(wT), project);
		assertEquals(ResourcesUtil.getProject(wC), project);

	}
	
	/**
	 * test open investigator editor.
	 */
	@Test
	public void openEditorTest()
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		Investigator i = new Investigator();
		
		i.setFullName("Jonathan Faubert");
		i.setNickName("jon");
		i.setInstitution("Bob Riley University");
		
		ResourcesUtil.openEditor(page, i);
		
		boolean found = false;
		for(IEditorReference editor : page.getEditorReferences())
		{
			if(editor.getName().equals("editor.investigator."+i.getNickName()))
			{
				found = true;
				break;
			}
		}
		
		assertTrue(found);
	}
	
	/**
	 * test open participant editor.
	 */
	@Test
	public void openEditorTest2()
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		Participant p = new Participant();
		Project proj = new Project();
		proj.getParticipants().add(p);
		
		p.setFullName("Jonathan Faubert");
		p.setParticipantId("jon");
		
		ResourcesUtil.openEditor(page, p);
		
		boolean found = false;
		for(IEditorReference editor : page.getEditorReferences())
		{
			if(editor.getName().equals("editor.participant."+p.getParticipantId()))
			{
				found = true;
				break;
			}
		}
		
		assertTrue(found);
	}
}
