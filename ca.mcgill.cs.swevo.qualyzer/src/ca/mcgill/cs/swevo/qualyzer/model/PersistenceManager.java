/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Barthelemy Dagenais (bart@cs.mcgill.ca)
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.model;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.QualyzerException;
import ca.mcgill.cs.swevo.qualyzer.util.HibernateUtil;

/**
 */
public final class PersistenceManager
{
	public static final String DB_FOLDER = ".db"; //$NON-NLS-1$
	public static final String QUALYZER_DB_NAME = "qualyzer_db"; //$NON-NLS-1$
	public static final String QUALYZER_DB_FILE_NAME = "qualyzer_db.data"; //$NON-NLS-1$
	public static final String DB_CONNECTION_STRING = "jdbc:hsqldb:file:%s"; //$NON-NLS-1$
	public static final String DB_INIT_STRING = ";hsqldb.default_table_type=cached"; //$NON-NLS-1$
	public static final String DB_USERNAME = "sa"; //$NON-NLS-1$
	public static final String DB_DIALECT = "org.hibernate.dialect.HSQLDialect"; //$NON-NLS-1$
	public static final String DB_DRIVER = "org.hsqldb.jdbcDriver"; //$NON-NLS-1$

	private static final PersistenceManager INSTANCE = new PersistenceManager();

	private final QualyzerActivator fActivator;

	private PersistenceManager()
	{
		fActivator = QualyzerActivator.getDefault();
	}

	/**
	 * @return
	 */
	public static PersistenceManager getInstance()
	{
		return INSTANCE;
	}

	/**
	 * @param project
	 * @return
	 */
	public IPath getDBPath(IProject project)
	{
		return project.getFolder(DB_FOLDER).getFile(QUALYZER_DB_NAME).getRawLocation();
	}

	/**
	 * @param project
	 * @return
	 */
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
		String connectionString = DB_CONNECTION_STRING.replace("%s", dbPath) + DB_INIT_STRING; //$NON-NLS-1$

		HibernateDBManager dbManager;
		dbManager = new HibernateDBManager(connectionString, DB_USERNAME, "", DB_DRIVER, DB_DIALECT); //$NON-NLS-1$

		// Add DB Manager
		fActivator.getHibernateDBManagers().put(project.getName(), dbManager);

		// Init DB
		SchemaExport export = new SchemaExport(dbManager.getConfiguration());
		export.execute(false, true, false, false);
		dbManager.getSessionFactory().close();
	}
	
	/**
	 * Makes sure that project has a HibernateDBManager registered.
	 * Should be called when Qualyzer launches so that the workspace can be properly propagated.
	 * @param project The project whose HibernateDBManager needs to be refreshed.
	 */
	public void refreshManager(IProject project)
	{
		String dbPath = getDBPath(project).toOSString();
		String connectionString = DB_CONNECTION_STRING.replace("%s", dbPath) + DB_INIT_STRING; //$NON-NLS-1$

		HibernateDBManager dbManager;
		dbManager = new HibernateDBManager(connectionString, DB_USERNAME, "", DB_DRIVER, DB_DIALECT); //$NON-NLS-1$

		// Add DB Manager
		fActivator.getHibernateDBManagers().put(project.getName(), dbManager);
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
				String message = "Could not create .db folder in project " + project.getName(); //$NON-NLS-1$
				throw new QualyzerException(message, ce);
			}
		}
		return dbFolder;
	}

	/**
	 * Gets the Project object represented by the given name.
	 * @param name The name of the expected Project
	 * @return The Project represented by name.
	 */
	public Project getProject(String name)
	{
		HibernateDBManager dbManager = fActivator.getHibernateDBManagers().get(name);

		Session session = dbManager.openSession();
		Project project = null;
		try
		{
			project = (Project) session.createQuery("from Project").uniqueResult(); //$NON-NLS-1$
		}
		finally
		{
			HibernateUtil.quietClose(session);
		}
		return project;
	}

	/**
	 * 
	 * Does something.
	 * 
	 * @param project
	 * @return
	 */
	public void initializeDocument(IAnnotatedDocument document)
	{
		HibernateDBManager dbManager = fActivator.getHibernateDBManagers().get(document.getProject().getName());

		Session session = dbManager.openSession();
		try
		{
			// Reattach
			session.buildLockRequest(LockOptions.NONE).lock(document);
			Hibernate.initialize(document.getParticipants());
			Hibernate.initialize(document.getFragments());
		}
		finally
		{
			HibernateUtil.quietClose(session);
		}
	}
	
	/**
	 * Try to delete a participant.
	 * @param participant
	 * @param manager
	 */
	public void deleteParticipant(Participant participant, HibernateDBManager manager)
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorReference[] editors = page.getEditorReferences();
		for(IEditorReference editor : editors)
		{
			if(editor.getName().equals(participant.getParticipantId()))
			{
				page.closeEditor(editor.getEditor(true), true);
			}
		}
		
		Object project = null;
		Session session = null;
		Transaction t = null;
		try
		{
			session = manager.openSession();
			t = session.beginTransaction();
			
			project = session.get(Project.class, participant.getProject().getPersistenceId());
			Object part = session.get(Participant.class, participant.getPersistenceId());
			
			((Project) project).getParticipants().remove(part);
			
			session.delete(part);
			session.flush();
			t.commit();
		}
		catch(HibernateException e)
		{
			System.out.println("Exception while deleting participant"); //$NON-NLS-1$
			e.printStackTrace();
			HibernateUtil.quietRollback(t);
		}
		finally
		{
			HibernateUtil.quietClose(session);
			HibernateUtil.quietSave(manager, project);
		}
	}
	
	/**
	 * Try to delete an investigator.
	 * @param participant
	 * @param manager
	 */
	public void deleteInvestigator(Investigator investigator, HibernateDBManager manager)
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorReference[] editors = page.getEditorReferences();
		for(IEditorReference editor : editors)
		{
			if(editor.getName().equals(investigator.getNickName()))
			{
				page.closeEditor(editor.getEditor(true), true);
			}
		}
		
		Object project = null;
		Session session = null;
		Transaction t = null;
		try
		{
			session = manager.openSession();
			t = session.beginTransaction();
			
			project = session.get(Project.class, investigator.getProject().getPersistenceId());
			Object inv = session.get(Investigator.class, investigator.getPersistenceId());
			
			((Project) project).getInvestigators().remove(inv);
			
			session.delete(inv);
			session.flush();
			t.commit();
		}
		catch(HibernateException e)
		{
			System.out.println("Exception while deleting investigator"); //$NON-NLS-1$
			e.printStackTrace();
			HibernateUtil.quietRollback(t);
		}
		finally
		{
			HibernateUtil.quietClose(session);
			HibernateUtil.quietSave(manager, project);
		}
	}
	
	/**
	 * Try to delete a transcript.
	 * @param participant
	 * @param manager
	 */
	public void deleteTranscript(Transcript transcript, HibernateDBManager manager)
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorReference[] editors = page.getEditorReferences();
		for(IEditorReference editor : editors)
		{
			if(editor.getName().equals(transcript.getFileName()))
			{
				page.closeEditor(editor.getEditor(true), true);
			}
		}
		
		Object project = null;
		Session session = null;
		Transaction t = null;
		try
		{
			session = manager.openSession();
			t = session.beginTransaction();
			
			project = session.get(Project.class, transcript.getProject().getPersistenceId());
			Object trans = session.get(Transcript.class, transcript.getPersistenceId());
			
			((Project) project).getTranscripts().remove(trans);
			
			session.delete(trans);
			session.flush();
			t.commit();
		}
		catch(HibernateException e)
		{
			System.out.println("Exception while deleting transcript"); //$NON-NLS-1$
			e.printStackTrace();
			HibernateUtil.quietRollback(t);
		}
		finally
		{
			HibernateUtil.quietClose(session);
			HibernateUtil.quietSave(manager, project);
		}
	}

}
