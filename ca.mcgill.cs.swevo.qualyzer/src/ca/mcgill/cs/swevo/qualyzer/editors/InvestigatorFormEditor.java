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
/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.InvestigatorEditorInput;
import ca.mcgill.cs.swevo.qualyzer.editors.pages.InvestigatorEditorPage;
import ca.mcgill.cs.swevo.qualyzer.model.HibernateDBManager;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.util.HibernateUtil;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class InvestigatorFormEditor extends FormEditor
{
	public static final String ID = "ca.mcgill.cs.swevo.qualyzer.editors.investigatorFormEditor";

	private InvestigatorEditorPage fPage;
	private Investigator fInvestigator;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
	 */
	@Override
	protected void addPages()
	{
		fInvestigator = ((InvestigatorEditorInput)getEditorInput()).getInvestigator();
		setPartName(fInvestigator.getNickName());
		fPage = new InvestigatorEditorPage(this, fInvestigator);
		try
		{
			addPage(fPage);
		}
		catch (PartInitException e)
		{
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor)
	{
		fInvestigator.setFullName(fPage.getFullname());
		fInvestigator.setNickName(fPage.getNickname());
		fInvestigator.setInstitution(fPage.getInstitution());
		
		HibernateDBManager manager;
		manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(fInvestigator.getProject().getName());
		HibernateUtil.quietSave(manager, fInvestigator.getProject());
	}
	
	@Override
	public boolean isDirty()
	{
		if(!fInvestigator.getFullName().equals(fPage.getFullname()))
		{
			return true;
		}
		else if(!fInvestigator.getInstitution().equals(fPage.getInstitution()))
		{
			return true;
		}
		else
		{
			return !fInvestigator.getNickName().equals(fPage.getNickname());
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs()
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}

}
