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

/**
 * The form used to edit Participant Data.
 * @author Jonathan Faubert
 *
 */
public class ParticipantEditorPage extends FormPage
{

	public ParticipantEditorPage(FormEditor editor)
	{
		super(editor, "ParticipantPage", "Edit a Participant");
	}
	
	@Override
	public void createFormContent(IManagedForm managedForm)
	{
		final ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		
		form.setText("Participant ID");
		
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		TableWrapData td;
		form.getBody().setLayout(layout);
		
		Label label = toolkit.createLabel(form.getBody(), "Participant ID:");
		Text text = toolkit.createText(form.getBody(), "");
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		text.setLayoutData(td);
		
		label = toolkit.createLabel(form.getBody(), "Participant Name:");
		text = toolkit.createText(form.getBody(), "");
		text.setLayoutData(td);
		
		label = toolkit.createLabel(form.getBody(), "Characteristics");
		Composite composite = toolkit.createComposite(form.getBody());
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		composite.setLayout(gridLayout);
		Button plus = toolkit.createButton(composite, "+", SWT.PUSH);
		Button minus = toolkit.createButton(composite, "-", SWT.PUSH);
		
		Table table = toolkit.createTable(form.getBody(), SWT.MULTI);
		td = new TableWrapData(TableWrapData.FILL);
		td.colspan = 2;
		table.setLayoutData(td);
		//TODO build the rest of the table
		
		label = toolkit.createLabel(form.getBody(), "Notes");
		td = new TableWrapData(TableWrapData.FILL);
		td.colspan = 2;
		label.setLayoutData(td);
		text = toolkit.createText(form.getBody(), "");
		td = new TableWrapData(TableWrapData.FILL);
		td.rowspan = 2;
		td.colspan = 2;
		text.setLayoutData(td);
		
		//TODO add +/- buttons
		Section section = toolkit.createSection(form.getBody(), Section.EXPANDED | Section.TITLE_BAR | Section.TWISTIE);
		td = new TableWrapData(TableWrapData.FILL);
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
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		label = toolkit.createLabel(sectionClient, "An example interview name");
		label.setLayoutData(gd);
		section.setClient(sectionClient);
		
		section = toolkit.createSection(form.getBody(), Section.EXPANDED | Section.TWISTIE | Section.TITLE_BAR);
		section.setText("Codes");
		td = new TableWrapData(TableWrapData.FILL);
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
		gridLayout = new GridLayout(2, true);
		sectionClient.setLayout(gridLayout);
		label = toolkit.createLabel(sectionClient, "Example code");
		label = toolkit.createLabel(sectionClient, "Example Interview");
	}
	
	

}
