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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.navigator.CommonNavigator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.dialogs.MemoDeleteDialog;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;

/**
 * 
 *
 */
public class DeleteMemoHandler extends AbstractHandler
{
	private static final String MEMO = File.separator + "memos" + File.separator; //$NON-NLS-1$
	private final Logger fLogger = LoggerFactory.getLogger(DeleteMemoHandler.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		ISelection selection = page.getSelection();
		Shell shell = HandlerUtil.getActiveShell(event).getShell();
		
		if(selection != null && selection instanceof IStructuredSelection)
		{
			List<Memo> toDelete = new ArrayList<Memo>();
			List<Project> projects = new ArrayList<Project>();
			
			for(Object element : ((IStructuredSelection) selection).toArray())
			{
				if(element instanceof Memo)
				{
					Memo memo = (Memo) element;
					
					if(!projects.contains(memo.getProject()))
					{
						projects.add(memo.getProject());
					}
					
					toDelete.add(memo);	
				}
			}
			
			if(projects.size() > 1)
			{
				String warningMessage = Messages.getString("handlers.DeleteMemoHandler.tooManyProjects"); //$NON-NLS-1$
				fLogger.warn(warningMessage);
				MessageDialog.openError(shell, Messages.getString(
						"handlers.DeleteMemoHandler.unableToDelete"), warningMessage); //$NON-NLS-1$
			}
			else
			{
				proceedWithDeletion(page, shell, toDelete);
			}
		}
		return null;
	}

	/**
	 * @param page
	 * @param shell
	 * @param toDelete
	 */
	private void proceedWithDeletion(IWorkbenchPage page, Shell shell, List<Memo> toDelete)
	{
		MemoDeleteDialog dialog = new MemoDeleteDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		dialog.create();
		int check = dialog.open();
			
		if(check == Window.OK)
		{	
			for(Memo memo : toDelete)
			{
				delete(memo, shell);
									
				CommonNavigator view;
				view = (CommonNavigator) page.findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
				view.getCommonViewer().refresh();
			}
		}
		
	}

	/**
	 * @param memo
	 * @param shell
	 */
	private void delete(Memo memo, Shell shell)
	{
		Project project = memo.getProject();
		IProject wProject = ResourcesPlugin.getWorkspace().getRoot().getProject(project.getName());
		
		File file = new File(wProject.getLocation() + MEMO + memo.getFileName());
		if(!file.delete())
		{
			String warningMessage = Messages.getString("handlers.DeleteMemoHandler.deleteFailed"); //$NON-NLS-1$
			fLogger.warn(warningMessage);
			MessageDialog.openWarning(shell, Messages.getString(
					"handlers.DeleteMemoHandler.fileError"), warningMessage); //$NON-NLS-1$
		}
		
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		ResourcesUtil.closeEditor(page, memo.getFileName());
		Facade.getInstance().deleteMemo(memo);
	}

}
