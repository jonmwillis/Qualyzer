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
package ca.mcgill.cs.swevo.qualyzer.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.navigator.CommonNavigator;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.ParticipantEditorInput;
import ca.mcgill.cs.swevo.qualyzer.editors.pages.ParticipantEditorPage;
import ca.mcgill.cs.swevo.qualyzer.model.HibernateDBManager;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.util.HibernateUtil;

/**
 * An editor for Participant objects.
 * @author Jonathan Faubert
 *
 */
public class ParticipantFormEditor extends FormEditor
{
	public static final String ID = "ca.mcgill.cs.swevo.qualyzer.editors.ParticipantFormEditor";
	
	private Participant fParticipant;

	private ParticipantEditorPage fPage;

	@Override
	protected void addPages()
	{
		IEditorInput input = getEditorInput();
		if(input instanceof ParticipantEditorInput)
		{
			ParticipantEditorInput partInput = (ParticipantEditorInput) input;
			fParticipant = partInput.getParticipant();
			try
			{
				fPage = new ParticipantEditorPage(this, fParticipant);
				addPage(fPage);
				this.setPartName(fParticipant.getParticipantId());
			}
			catch(PartInitException e)
			{
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void doSave(IProgressMonitor monitor)
	{
		fParticipant.setContactInfo(fPage.getContactInfo());
		fParticipant.setFullName(fPage.getFullname());
		fParticipant.setNotes(fPage.getNotes());
		fParticipant.setParticipantId(fPage.getId());
		
		HibernateDBManager manager;
		manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(fParticipant.getProject().getName());
		HibernateUtil.quietSave(manager, fParticipant);
		
		CommonNavigator view;
		view = (CommonNavigator) getSite().getPage().findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
		view.getCommonViewer().refresh(fParticipant);
		fPage.notDirty();
	}

	@Override
	public boolean isDirty()
	{	
		if(fPage.isDirty())
		{
			char c = 0; //TODO hack
			setPartName(fParticipant.getParticipantId()+c);
		}
		return fPage.isDirty();
	}
	
	@Override
	public void doSaveAs(){}

	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}
}
