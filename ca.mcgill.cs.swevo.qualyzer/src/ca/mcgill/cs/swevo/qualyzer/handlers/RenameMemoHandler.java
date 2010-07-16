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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.dialogs.RenameMemoDialog;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;

/**
 * 
 *
 */
public class RenameMemoHandler extends AbstractHandler
{

	
	private static final String MEMO = File.separator + "memos" + File.separator; //$NON-NLS-1$
	private static final String EXT = ".rtf"; //$NON-NLS-1$

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		
		ISelection selection = page.getSelection();
		
		if(selection != null && selection instanceof IStructuredSelection)
		{
			Object element = ((IStructuredSelection) selection).getFirstElement();
			Project project = ResourcesUtil.getProject(element);
			if(element instanceof Memo)
			{
				String currentName = ((Memo) element).getName();
				RenameMemoDialog dialog = new RenameMemoDialog(shell, project);
				dialog.setOldName(currentName);
				dialog.create();
				if(dialog.open() == Window.OK)
				{
					rename((Memo) element, dialog.getName());
					
					Facade.getInstance().saveMemo((Memo) element);
					
					((CommonNavigator) page.findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID))
						.getCommonViewer().refresh();
				}
			}
		}
		return null;
	}
	
	private void rename(Memo memo, String name)
	{
		boolean closed = false;
		
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorReference[] editors = activePage.getEditorReferences();
		for(IEditorReference editor : editors)
		{
			if(editor.getName().equals(memo.getFileName()))
			{
				activePage.closeEditor(editor.getEditor(true), true);
				closed = true;
			}
		}
		
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(memo.getProject().getName());
		
		String projectPath = project.getLocation().toString();
		File origFile = new File(projectPath + MEMO + memo.getFileName());
		File newFile = new File(projectPath + MEMO + name + EXT);
		
		origFile.renameTo(newFile);
		
		memo.setName(name);
		memo.setFileName(name+EXT);
		
		if(closed)
		{
			//TODO open the file.
		}
	}

}
