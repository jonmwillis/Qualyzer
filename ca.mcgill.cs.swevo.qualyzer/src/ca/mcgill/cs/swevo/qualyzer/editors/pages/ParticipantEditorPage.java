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
package ca.mcgill.cs.swevo.qualyzer.editors.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import ca.mcgill.cs.swevo.qualyzer.model.Participant;

/**
 * The form used to edit Participant Data.
 * @author Jonathan Faubert
 *
 */
public class ParticipantEditorPage extends FormPage
{
	private Participant fParticipant;
	
	private Text fID;
	private Text fFullname;
	private Text fContactInfo;
	private Text fNotes;

	public ParticipantEditorPage(FormEditor editor, Participant participant)
	{
		super(editor, "ParticipantEditorPage", "Participant");
		fParticipant = participant;
	}
	
	@Override
	public void createFormContent(IManagedForm managedForm)
	{
		final ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		Composite body = form.getBody();
		
		form.setText("Participant");
		
		//TableWrapLayout layout = new TableWrapLayout();
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		TableWrapData td;
		body.setLayout(layout);
		
		Label label = toolkit.createLabel(body, "Participant ID:");
		fID = toolkit.createText(body, fParticipant.getParticipantId());
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		fID.setLayoutData(td);
		
		label = toolkit.createLabel(body, "Participant Name:");
		fFullname = toolkit.createText(body, fParticipant.getFullName());
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		fFullname.setLayoutData(td);
		
		label = toolkit.createLabel(body, "Contact Info:");
		fContactInfo = toolkit.createText(body, fParticipant.getContactInfo());
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		fContactInfo.setLayoutData(td);
		
		label = toolkit.createLabel(body, "Characteristics");
		Composite composite = toolkit.createComposite(body);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		composite.setLayout(gridLayout);
		Button plus = toolkit.createButton(composite, "+", SWT.PUSH);
		Button minus = toolkit.createButton(composite, "-", SWT.PUSH);
		
		Table table = toolkit.createTable(body, SWT.BORDER);
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.colspan = 2;
		table.setLayoutData(td);
		table.setLinesVisible(true);

		//TODO build the rest of the table
		
		label = toolkit.createLabel(body, "Notes");
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.colspan = 2;
		label.setLayoutData(td);
		fNotes = toolkit.createText(body, fParticipant.getNotes());
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.rowspan = 2;
		td.colspan = 2;
		fNotes.setLayoutData(td);
		
		//TODO add +/- buttons
		Section section = toolkit.createSection(body, Section.EXPANDED | Section.TITLE_BAR | Section.TWISTIE);
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.colspan = 2;
		section.setLayoutData(td);
		section.addExpansionListener(new ExpansionAdapter(){
			public void expansionStateChanged(ExpansionEvent e)
			{
				form.reflow(true);
			}
		});
		section.setText("Interviews");
		Composite sectionClient = toolkit.createComposite(section);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		sectionClient.setLayout(gridLayout);
		//TODO build the list of interviews
		//TODO make clickable
		GridData gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		label = toolkit.createLabel(sectionClient, "An example interview name");
		label.setLayoutData(gd);
		sectionClient.setLayoutData(td);
		section.setClient(sectionClient);
		
		section = toolkit.createSection(body, Section.EXPANDED | Section.TWISTIE | Section.TITLE_BAR);
		section.setText("Codes");
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.colspan = 2;
		section.setLayoutData(td);
		section.addExpansionListener(new ExpansionAdapter(){
			public void expansionStateChanged(ExpansionEvent e)
			{
				form.reflow(true);
			}
		});
		sectionClient = toolkit.createComposite(section);
		//TODO get all codes related to participant
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.makeColumnsEqualWidth = true;
		sectionClient.setLayout(gridLayout);
		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		label = toolkit.createLabel(sectionClient, "Example code");
		label.setLayoutData(gd);
		label = toolkit.createLabel(sectionClient, "Example Interview");
		label.setLayoutData(gd);
		section.setClient(sectionClient);
		
		toolkit.paintBordersFor(body);
	}
	
	public String getId()
	{
		return fID.getText();
	}
	
	public String getFullname()
	{
		return fFullname.getText();
	}
	
	public String getContactInfo()
	{
		return fContactInfo.getText();
	}

	public String getNotes()
	{
		return fNotes.getText();
	}
}
