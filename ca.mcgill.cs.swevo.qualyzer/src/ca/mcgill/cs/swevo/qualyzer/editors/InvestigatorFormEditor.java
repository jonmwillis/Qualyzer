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
package ca.mcgill.cs.swevo.qualyzer.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.custom.CTabFolder;
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
 */
public class InvestigatorFormEditor extends FormEditor
{
	public static final String ID = "ca.mcgill.cs.swevo.qualyzer.editors.investigatorFormEditor"; //$NON-NLS-1$

	private InvestigatorEditorPage fPage;
	private Investigator fInvestigator;

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
	public void doSaveAs(){}

	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}
	
	// This override is to eliminate the single tab at the bottom of the editor.
	@Override
	protected void createPages() 
	{
		super.createPages();
	    if(getPageCount() == 1 && getContainer() instanceof CTabFolder) 
	    {
	    	((CTabFolder) getContainer()).setTabHeight(0);
	    }
	}

}
