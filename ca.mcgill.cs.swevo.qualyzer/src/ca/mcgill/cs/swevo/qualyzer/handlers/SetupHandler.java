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
package ca.mcgill.cs.swevo.qualyzer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.HibernateDBManager;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.util.HibernateUtil;

/**
 * Test command used to setup a basic project.
 * 
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 * 
 */
public class SetupHandler extends AbstractHandler
{

	/**
	 * <p>
	 * Does something.
	 * </p>
	 * 
	 * @param event
	 * @return
	 * @throws ExecutionException
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	//CSOFF:
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		try
		{
			PersistenceManager manager = PersistenceManager.getInstance();
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

			IProject project = root.getProject("Navigator Test");
			
			project.create(new NullProgressMonitor());
			project.open(new NullProgressMonitor());
			
			manager.initDB(project);
			HibernateDBManager dbManager = QualyzerActivator.getDefault().getHibernateDBManagers().get("Navigator Test");

			Project projectDB = new Project();
			projectDB.setName("Navigator Test");
			Investigator inv = new Investigator();
			inv.setFullName("Jonathan Faubert");
			projectDB.getInvestigators().add(inv);
			
			Code code = new Code();
			code.setCodeName("code");
			projectDB.getCodes().add(code);
			
			Memo memo = new Memo();
			memo.setName("memo");
			projectDB.getMemos().add(memo);
			
			Participant part = new Participant();
			part.setFullName("Tester Participant");
			projectDB.getParticipants().add(part);
			
			Transcript trans = new Transcript();
			trans.setName("transcript");
			projectDB.getTranscripts().add(trans);
			
			HibernateUtil.quietSave(dbManager, projectDB);
			
			root.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	//CSON:

}
