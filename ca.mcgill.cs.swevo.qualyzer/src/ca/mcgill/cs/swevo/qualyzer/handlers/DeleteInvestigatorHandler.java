/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jonathan Faubert
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.handlers;

import java.util.ArrayList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.navigator.CommonNavigator;
import org.hibernate.Session;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.model.HibernateDBManager;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperInvestigator;
import ca.mcgill.cs.swevo.qualyzer.util.HibernateUtil;

/**
 * Handler for the Delete Investigator Command.
 *
 */
public class DeleteInvestigatorHandler extends AbstractHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		ISelection selection = page.getSelection();
		
		if(selection != null && selection instanceof IStructuredSelection)
		{
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if(element instanceof Investigator)
			{
				Investigator investigator = (Investigator) element;
				Project project = investigator.getProject();
				
				HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers()
					.get(project.getName());
				ArrayList<String> conflicts = checkForConflicts(investigator, project, manager.openSession());
				
				Shell shell = HandlerUtil.getActiveShell(event).getShell();
				if(conflicts.size() > 0)
				{
					String errorMsg = printErrors(conflicts);
					MessageDialog.openError(shell, Messages._handlers_DeleteInvestigatorHandler_cannotDelete, errorMsg);
				}
				else
				{
					boolean check = MessageDialog.openConfirm(shell, 
							Messages._handlers_DeleteInvestigatorHandler_deleteInvestigator, 
							Messages._handlers_DeleteInvestigatorHandler_confirm);
					
					if(check)
					{
						for(IEditorReference editor : page.getEditorReferences())
						{
							if(editor.getName().equals(investigator.getNickName()))
							{
								page.closeEditor(editor.getEditor(true), true);
							}
						}
						project.getInvestigators().remove(investigator);
						investigator.setProject(null);
						HibernateUtil.quietSave(manager, project);
						
						CommonNavigator view;
						view = (CommonNavigator) page.findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
						view.getCommonViewer().refresh(new WrapperInvestigator(project));
					}
				}
			}
		}
		return null;
	}

	/**
	 * @param conflicts
	 * @return
	 */
	private String printErrors(ArrayList<String> conflicts)
	{
		String output = Messages._handlers_DeleteInvestigatorHandler_conflicts;
		for(String str : conflicts)
		{
			output += "\n"+str; //$NON-NLS-1$
		}
		
		return output;
	}

	/**
	 * @param investigator
	 * @param project
	 * @param openSession
	 * @return
	 */
	private ArrayList<String> checkForConflicts(Investigator investigator, Project project, Session openSession)
	{
		ArrayList<String> conflicts = new ArrayList<String>();
		
		for(Memo memo : project.getMemos())
		{
			if(memo.getAuthor().equals(investigator))
			{
				conflicts.add(Messages._handlers_DeleteInvestigatorHandler_memo + memo.getName());
			}
		}
		
		//TODO Check codes?
		
		//TODO Check annotations?
		
		openSession.close();
		return conflicts;
	}

}
