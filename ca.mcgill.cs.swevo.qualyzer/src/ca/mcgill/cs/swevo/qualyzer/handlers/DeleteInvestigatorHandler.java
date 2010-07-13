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
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.navigator.CommonNavigator;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.model.Annotation;
import ca.mcgill.cs.swevo.qualyzer.model.CodeEntry;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;

/**
 * Handler for the Delete Investigator Command.
 *
 */
public class DeleteInvestigatorHandler extends AbstractHandler
{

	/**
	 * 
	 */
	private static final String NEWLINE = "\n"; //$NON-NLS-1$

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		ISelection selection = page.getSelection();
		Shell shell = HandlerUtil.getActiveShell(event).getShell();
		
		if(selection != null && selection instanceof IStructuredSelection)
		{
			List<String> conflicts = new ArrayList<String>();
			List<Investigator> toDelete = new ArrayList<Investigator>();
			List<Project> projects = new ArrayList<Project>();
			for(Object element : ((IStructuredSelection) selection).toArray())
			{
				if(element instanceof Investigator)
				{
					Investigator investigator = (Investigator) element;
					Project project = investigator.getProject();
					if(!projects.contains(project))
					{
						projects.add(project);
					}
					conflicts.addAll(checkForConflicts(investigator));
					toDelete.add(investigator);
				}
			}
			
			if(projects.size() > 1)
			{
				MessageDialog.openError(shell, 
						Messages.getString("handlers.DeleteInvestigatorHandler.deleteFailed"), //$NON-NLS-1$
						Messages.getString("handlers.DeleteInvestigatorHandler.multipleProjects")); //$NON-NLS-1$ 
			}
			else if(projects.get(0).getInvestigators().size() == toDelete.size())
			{
				MessageDialog.openError(shell, 
						Messages.getString("handlers.DeleteInvestigatorHandler.deleteFailed"), //$NON-NLS-1$
						Messages.getString("handlers.DeleteInvestigatorHandler.tooManyInves")); //$NON-NLS-1$ 
			}
			else if(conflicts.size() > 0)
			{
				String errorMsg = printErrors(conflicts);
				MessageDialog.openError(shell, Messages.getString(
						"handlers.DeleteInvestigatorHandler.cannotDelete"), errorMsg); //$NON-NLS-1$
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
	private void proceedWithDeletion(IWorkbenchPage page, Shell shell, List<Investigator> toDelete)
	{
		String msg = ""; //$NON-NLS-1$
		if(toDelete.size() == 1)
		{
			msg = Messages.getString("handlers.DeleteInvestigatorHandler.confirm"); //$NON-NLS-1$
		}
		else
		{
			msg = Messages.getString("handlers.DeleteInvestigatorHandler.confirm"); //$NON-NLS-1$
		}
		
		boolean check = MessageDialog.openConfirm(shell, Messages.getString(
		"handlers.DeleteInvestigatorHandler.deleteInvestigator"), msg); //$NON-NLS-1$
		
		if(check)
		{
			for(Investigator investigator : toDelete)
			{	
				Facade.getInstance().deleteInvestigator(investigator);
			}
			CommonNavigator view;
			view = (CommonNavigator) page.findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
			view.getCommonViewer().refresh();
		}	
	}
	
	/**
	 * @param conflicts
	 * @return
	 */
	private String printErrors(List<String> conflicts)
	{
		String output = Messages.getString("handlers.DeleteInvestigatorHandler.conflicts"); //$NON-NLS-1$
		for(String str : conflicts)
		{
			output += NEWLINE+str; 
		}
		
		return output;
	}

	/**
	 * @param investigator
	 * @param project
	 * @param session
	 * @return
	 */
	private ArrayList<String> checkForConflicts(Investigator investigator)
	{
		ArrayList<String> conflicts = new ArrayList<String>();
		Project project = investigator.getProject();
		
		for(Memo memo : project.getMemos())
		{
			if(memo.getAuthor().equals(investigator))
			{
				conflicts.add(Messages.getString(
						"handlers.DeleteInvestigatorHandler.memo") + memo.getName()); //$NON-NLS-1$
			}
			else
			{
				Memo lMemo = Facade.getInstance().forceMemoLoad(memo);
				int numAnnotations = 0;
				int numCodeEntries = 0;
				for(Fragment fragment : ((Memo) lMemo).getFragments())
				{
					numAnnotations += countAnnotations(investigator, fragment);	
					numCodeEntries += countCodeEntries(investigator, fragment);
				}
				String str = buildMemoString(numAnnotations, numCodeEntries, memo);
				if(!str.isEmpty())
				{
					conflicts.add(str);
				}
			}
		}
		
		for(Transcript transcript : project.getTranscripts())
		{
			Transcript lTranscript = Facade.getInstance().forceTranscriptLoad(transcript);
			int numAnnotations = 0;
			int numCodeEntries = 0;
			for(Fragment fragment : ((Transcript) lTranscript).getFragments())
			{
				numAnnotations += countAnnotations(investigator, fragment);	
				numCodeEntries += countCodeEntries(investigator, fragment);
			}
			String str = buildTranscriptString(numAnnotations, numCodeEntries, transcript);
			if(!str.isEmpty())
			{
				conflicts.add(str);
			}
		}
		
		return conflicts;
	}

	/**
	 * @param numAnnotations
	 * @param numCodeEntries
	 * @param memo
	 * @return
	 */
	private String buildMemoString(int numAnnotations, int numCodeEntries, Memo memo)
	{
		String str = ""; //$NON-NLS-1$
		if(numAnnotations == 1)
		{
			str += Messages.getString(
					"handlers.DeleteInvestigatorHandler.oneAnnotationMemo") + memo.getName(); //$NON-NLS-1$
		}
		else if(numAnnotations > 1)
		{
			str += numAnnotations+Messages.getString(
					"handlers.DeleteInvestigatorHandler.annotationsMemo") + memo.getName(); //$NON-NLS-1$
		}
		
		if(numAnnotations > 0 && numCodeEntries > 0)
		{
			str += NEWLINE;
		}
		
		if(numCodeEntries == 1)
		{	
			str += Messages.getString("handlers.DeleteInvestigatorHandler.oneCodeMemo") + memo.getName(); //$NON-NLS-1$
		}
		else if(numCodeEntries > 1)
		{
			str += numCodeEntries + Messages.getString(
					"handlers.DeleteInvestigatorHandler.codesMemo") + memo.getName(); //$NON-NLS-1$
		}
		
		return str;
	}
	
	/**
	 * @param numAnnotations
	 * @param numCodeEntries
	 * @param memo
	 * @return
	 */
	private String buildTranscriptString(int numAnnotations, int numCodeEntries, Transcript transcript)
	{
		String str = ""; //$NON-NLS-1$
		if(numAnnotations == 1)
		{
			str += Messages.getString("handlers.DeleteInvestigatorHandler.oneAnnotationTranscript") + //$NON-NLS-1$
				transcript.getName(); 
		}
		else if(numAnnotations > 1)
		{
			str += numAnnotations+Messages.getString(
					"handlers.DeleteInvestigatorHandler.annotationsTranscript") + transcript.getName(); //$NON-NLS-1$
		}
		
		if(numAnnotations > 0 && numCodeEntries > 0)
		{
			str += NEWLINE;
		}
		
		if(numCodeEntries == 1)
		{	
			str += Messages.getString(
					"handlers.DeleteInvestigatorHandler.oneCodeTranscript") + transcript.getName(); //$NON-NLS-1$
		}
		else if(numCodeEntries > 1)
		{
			str += numCodeEntries + Messages.getString(
					"handlers.DeleteInvestigatorHandler.codesTranscript") + transcript.getName(); //$NON-NLS-1$
		}
		
		return str;
	}

	/**
	 * @param investigator
	 * @param fragment
	 */
	private int countCodeEntries(Investigator investigator, Fragment fragment)
	{
		int count = 0;
		for(CodeEntry codeEntry : fragment.getCodeEntries())
		{
			if(investigator.equals(codeEntry.getInvestigator()))
			{
				count++;
			}
		}
		return count;
	}

	/**
	 * @param investigator
	 * @param fragment
	 */
	private int countAnnotations(Investigator investigator, Fragment fragment)
	{
		int count = 0;
		for(Annotation annotation : fragment.getAnnotations())
		{
			if(annotation.getInvestigator().equals(investigator))
			{
				count++;
			}
		}
		return count;
	}

}
