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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.navigator.CommonNavigator;
import org.hibernate.Session;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.model.Annotation;
import ca.mcgill.cs.swevo.qualyzer.model.CodeEntry;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;
import ca.mcgill.cs.swevo.qualyzer.model.HibernateDBManager;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
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
				if(conflicts.size() > 0 || project.getInvestigators().size() == 1)
				{
					String errorMsg;
					if(project.getInvestigators().size() == 1)
					{
						errorMsg = Messages.getString("handlers.DeleteInvestigatorHandler.oneRequired"); //$NON-NLS-1$
					}
					else
					{
						errorMsg = printErrors(conflicts);
					}
					MessageDialog.openError(shell, Messages.getString(
							"handlers.DeleteInvestigatorHandler.cannotDelete"), errorMsg); //$NON-NLS-1$
				}
				else
				{
					boolean check = MessageDialog.openConfirm(shell, 
							Messages.getString("handlers.DeleteInvestigatorHandler.deleteInvestigator"),  //$NON-NLS-1$
							Messages.getString("handlers.DeleteInvestigatorHandler.confirm")); //$NON-NLS-1$
					
					if(check)
					{
						PersistenceManager.getInstance().deleteInvestigator(investigator, manager);
						CommonNavigator view;
						view = (CommonNavigator) page.findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
						view.getCommonViewer().refresh();
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
	private ArrayList<String> checkForConflicts(Investigator investigator, Project project, Session session)
	{
		ArrayList<String> conflicts = new ArrayList<String>();
		
		for(Memo memo : project.getMemos())
		{
			if(memo.getAuthor().equals(investigator))
			{
				conflicts.add(Messages.getString(
						"handlers.DeleteInvestigatorHandler.memo") + memo.getName()); //$NON-NLS-1$
			}
			else
			{
				Object lMemo = session.get(Memo.class, memo.getPersistenceId());
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
			Object lTranscript = session.get(Transcript.class, transcript.getPersistenceId());
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
		
		session.close();
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
			if(codeEntry.getInvestigator().equals(investigator))
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
