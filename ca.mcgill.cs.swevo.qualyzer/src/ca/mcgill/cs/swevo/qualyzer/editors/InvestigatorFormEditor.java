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
import org.eclipse.ui.navigator.CommonNavigator;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.InvestigatorEditorInput;
import ca.mcgill.cs.swevo.qualyzer.editors.pages.InvestigatorEditorPage;
import ca.mcgill.cs.swevo.qualyzer.model.HibernateDBManager;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.util.HibernateUtil;

/**
 * An editor for Investigator Objects.
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
		HibernateUtil.quietSave(manager, fInvestigator);
		
		CommonNavigator view;
		view = (CommonNavigator) getSite().getPage().findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
		view.getCommonViewer().refresh(fInvestigator);
		
		fPage.notDirty();
	}
	
	@Override
	public boolean isDirty()
	{
		if(fPage.isDirty())
		{
			setPartName(fInvestigator.getNickName()+(char)0);
		}
		
		return fPage.isDirty();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs(){}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}

}
