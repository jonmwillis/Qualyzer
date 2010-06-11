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
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

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
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("Project");
		IFile file = project.getFile("doc.rtf");
		
		FileEditorInput input = new FileEditorInput(file);
		
		try
		{
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, "ca.mcgill.cs.swevo.qualyzer.editors.colorer");
		}
		catch (PartInitException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
//		try
//		{
//			PersistenceManager manager = PersistenceManager.getInstance();
//			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
//
//			IProject project = root.getProject("Navigator Test"); //$NON-NLS-1$
//			
//			project.create(new NullProgressMonitor());
//			project.open(new NullProgressMonitor());
//			
//			manager.initDB(project);
//			HibernateDBManager dbManager = QualyzerActivator.getDefault().getHibernateDBManagers().get("Navigator Test"); //$NON-NLS-1$
//
//			Project projectDB = new Project();
//			projectDB.setName("Navigator Test"); //$NON-NLS-1$
//			Investigator inv = new Investigator();
//			inv.setFullName("Jonathan Faubert"); //$NON-NLS-1$
//			projectDB.getInvestigators().add(inv);
//			
//			Code code = new Code();
//			code.setCodeName("code"); //$NON-NLS-1$
//			projectDB.getCodes().add(code);
//			
//			Memo memo = new Memo();
//			memo.setName("memo"); //$NON-NLS-1$
//			projectDB.getMemos().add(memo);
//			
//			memo = new Memo();
//			memo.setName("memo2"); //$NON-NLS-1$
//			projectDB.getMemos().add(memo);
//			
//			Participant part = new Participant();
//			part.setFullName("Tester Participant"); //$NON-NLS-1$
//			projectDB.getParticipants().add(part);
//			
//			Transcript trans = new Transcript();
//			trans.setName("transcript"); //$NON-NLS-1$
//			projectDB.getTranscripts().add(trans);
//			
//			HibernateUtil.quietSave(dbManager, projectDB);
//			
//			root.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
//		}
//		catch (CoreException e)
//		{
//			e.printStackTrace();
//		}
		return null;
	}
	//CSON:

}
