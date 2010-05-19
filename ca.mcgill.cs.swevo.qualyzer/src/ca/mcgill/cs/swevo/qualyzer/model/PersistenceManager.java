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

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.QualyzerException;

/**
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 * 
 */
public final class PersistenceManager
{

	public static final String DB_FOLDER = ".db";
	public static final String QUALYZER_DB_NAME = "qualyzer_db";
	public static final String QUALYZER_DB_FILE_NAME = "qualyzer_db.data";
	public static final String DB_CONNECTION_STRING = "jdbc:hsqldb:file:%s";
	public static final String DB_INIT_STRING = ";hsqldb.default_table_type=cached";
	public static final String DB_USERNAME = "sa";
	public static final String DB_DIALECT = "org.hibernate.dialect.HSQLDialect";
	public static final String DB_DRIVER = "org.hsqldb.jdbcDriver";

	private static final PersistenceManager INSTANCE = new PersistenceManager();

	private final QualyzerActivator fActivator;

	private PersistenceManager()
	{
		fActivator = QualyzerActivator.getDefault();
	}

	public static PersistenceManager getInstance()
	{
		return INSTANCE;
	}

	public IPath getDBPath(IProject project)
	{
		return project.getFolder(DB_FOLDER).getFile(QUALYZER_DB_NAME).getRawLocation();
	}

	public IPath getDBFilePath(IProject project)
	{
		return project.getFolder(DB_FOLDER).getFile(QUALYZER_DB_FILE_NAME).getRawLocation();
	}

	/**
	 * 
	 * Creates the HSQLDB database in the .db folder of project.
	 * 
	 * @param project
	 */
	public void initDB(IProject project)
	{
		setupDBFolder(project);
		String dbPath = getDBPath(project).toOSString();
		String connectionString = DB_CONNECTION_STRING.replace("%s", dbPath) + DB_INIT_STRING;

		HibernateDBManager dbManager = new HibernateDBManager(connectionString, DB_USERNAME, "", DB_DRIVER, DB_DIALECT);

		// Add DB Manager
		fActivator.getHibernateDBManagers().put(project.getName(), dbManager);

		// Init DB
		SchemaExport export = new SchemaExport(dbManager.getConfiguration());
		export.execute(false, true, false, false);
		dbManager.getSessionFactory().close();
	}

	/**
	 * 
	 * Gets the .db folder in the project. Creates it if it does not exist.
	 * 
	 * @param project
	 * @return
	 */
	private IFolder setupDBFolder(IProject project)
	{
		IFolder dbFolder = project.getFolder(DB_FOLDER);
		if (!dbFolder.exists())
		{
			try
			{
				dbFolder.create(true, true, new NullProgressMonitor());
			}
			catch (CoreException ce)
			{
				String message = "Could not create .db folder in project " + project.getName();
				throw new QualyzerException(message, ce);
			}
		}
		return dbFolder;
	}

	public Project[] getProjects()
	{
		return null;
	}

}
