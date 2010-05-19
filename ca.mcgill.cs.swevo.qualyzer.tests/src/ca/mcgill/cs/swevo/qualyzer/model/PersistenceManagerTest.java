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
package ca.mcgill.cs.swevo.qualyzer.model;

import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 * 
 */
public class PersistenceManagerTest
{

	public static final String TEST_PROJECT_NAME = "TEST_QUAL_STUDY";

	private PersistenceManager manager;

	private IProject project;

	@Before
	public void setUp()
	{
		manager = PersistenceManager.getInstance();
		project = ResourcesPlugin.getWorkspace().getRoot().getProject(TEST_PROJECT_NAME);
		if (!project.exists())
		{
			try
			{
				project.create(new NullProgressMonitor());
				project.open(new NullProgressMonitor());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@After
	public void tearDown()
	{
		try {
			project.delete(true, new NullProgressMonitor());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDBInit() {
		manager.initDB(project);
		IPath path = manager.getDBFilePath(project);
		assertTrue(path.toFile().exists());
	}
}
