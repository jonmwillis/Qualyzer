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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.util.HibernateUtil;

/**
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 * 
 */
public class PersistenceManagerTest
{

	public static final String TEST_PROJECT_NAME = "TEST_QUAL_STUDY";

	private QualyzerActivator fActivator;

	private PersistenceManager fManager;

	private IProject fProject;

	@Before
	public void setUp()
	{
		fManager = PersistenceManager.getInstance();
		fProject = ResourcesPlugin.getWorkspace().getRoot().getProject(TEST_PROJECT_NAME);
		if (!fProject.exists())
		{
			try
			{
				fProject.create(new NullProgressMonitor());
				fProject.open(new NullProgressMonitor());
			}
			catch (CoreException e)
			{
				e.printStackTrace();
			}
		}
		fActivator = QualyzerActivator.getDefault();
	}

	@After
	public void tearDown()
	{
		try
		{
			fProject.delete(true, new NullProgressMonitor());
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void testDBInit()
	{
		fManager.initDB(fProject);
		IPath path = fManager.getDBFilePath(fProject);
		assertTrue(path.toFile().exists());
	}

	@Test
	public void testGetProject()
	{
		fManager.initDB(fProject);
		Project projectDB = new Project();
		projectDB.setName(TEST_PROJECT_NAME);
		HibernateUtil.quietSave(fActivator.getHibernateDBManagers().get(TEST_PROJECT_NAME), projectDB);
		projectDB = fManager.getProject(TEST_PROJECT_NAME);
		assertNotNull(projectDB);
	}

	/**
	 * 
	 * Tests whether an ordered list is always ordered after an addition and a save.
	 * 
	 * Warning: in this method, there are many calls to HibernateUtil. This is an anti-pattern as multiple sessions are
	 * created/closed in a single method. All the calls (saves, refreshes) should be inlined in one session. This is
	 * acceptable for testing purpose though.
	 */
	@Test
	public void testHibernateOrderBy()
	{
		fManager.initDB(fProject);
		HibernateDBManager dbManager = fActivator.getHibernateDBManagers().get(TEST_PROJECT_NAME);
		Project projectDB = new Project();
		projectDB.setName(TEST_PROJECT_NAME);
		HibernateUtil.quietSave(dbManager, projectDB);

		Code code1 = new Code();
		code1.setCodeName("b");
		projectDB.getCodes().add(code1);
		HibernateUtil.quietSave(dbManager, projectDB);

		Code code2 = new Code();
		code2.setCodeName("a");
		projectDB.getCodes().add(code2);
		HibernateUtil.quietSave(dbManager, projectDB);

		// XXX Should be in a try/finally block like in HibernateUtil
		// TODO Implement refresh in the HibernateUtil (maybe?)
		Session session = dbManager.openSession();
		// Refresh to trigger the ordering.
		session.refresh(projectDB);
		HibernateUtil.quietClose(session);
		Code tempCode = projectDB.getCodes().get(0);
		assertEquals("a", tempCode.getCodeName());
	}

	// TODO Add testHibernateCascade
	// TODO Add testHibernateFetchStrategy
}
