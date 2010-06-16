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

import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
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
		assertTrue(ResourcesUtil.verifyID("aA1_-"));
		assertTrue(ResourcesUtil.verifyID("aasdl_sSDFA-3425"));
		
		assertFalse(ResourcesUtil.verifyID(""));
		assertFalse(ResourcesUtil.verifyID(" "));
		assertFalse(ResourcesUtil.verifyID("!"));
		assertFalse(ResourcesUtil.verifyID("^"));
		assertFalse(ResourcesUtil.verifyID("ffffsadfsadfieurASDFSDF84375987 "));
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
}
