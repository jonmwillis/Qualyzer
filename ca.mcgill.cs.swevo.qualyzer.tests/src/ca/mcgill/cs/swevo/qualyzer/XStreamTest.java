/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Name - Initial Contribution
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.model.Project;

import com.thoughtworks.xstream.XStream;

/**
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 * 
 */
public class XStreamTest
{

	@Test
	public void testSimpleSerialization()
	{
		XStream xstream = new XStream();
		xstream.alias("project", Project.class);
		
		Project project = new Project();
		project.setName("test1");
		
		String xml = xstream.toXML(project);
		assertTrue(xml.length() > 1);
	}

}
