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
package ca.mcgill.cs.swevo.qualyzer.editors.pages;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
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
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class InvestigatorEditorPage extends FormPage
{

	/**
	 * @param editor
	 */
	public InvestigatorEditorPage(FormEditor editor)
	{
		super(editor, "Investigator", "Investigator");
	}

	@Override
	public void createFormContent(IManagedForm managed)
	{
		final ScrolledForm form = managed.getForm();
		FormToolkit toolkit = managed.getToolkit();
		
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		form.getBody().setLayout(layout);
		
		Label label = toolkit.createLabel(form.getBody(), "Investigator ID");
		Text text = toolkit.createText(form.getBody(), "");
		text.setLayoutData(td);
		
		label = toolkit.createLabel(form.getBody(), "Investigator's name");
		text = toolkit.createText(form.getBody(), "");
		text.setLayoutData(td);
		
		label = toolkit.createLabel(form.getBody(), "Institution");
		text = toolkit.createText(form.getBody(), "");
		text.setLayoutData(td);
		
		Section section = toolkit.createSection(form.getBody(), Section.EXPANDED | Section.TITLE_BAR | Section.TWISTIE);
		td = new TableWrapData(TableWrapData.FILL);
		td.colspan = 2;
		section.setLayoutData(td);
		section.setText("Conducted Interviews");
		section.addExpansionListener(new ExpansionAdapter(){
			public void expansionStateChanged(ExpansionEvent e)
			{
				form.reflow(true);
			}
		});
		Composite sectionClient = toolkit.createComposite(section);
		GridLayout grid = new GridLayout();
		grid.numColumns = 1;
		sectionClient.setLayout(grid);
		//TODO generate the interview data
		//TODO make clickable
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		label = toolkit.createLabel(sectionClient, "Example Interview");
		label.setLayoutData(gd);
		section.setClient(sectionClient);
		
		section = toolkit.createSection(form.getBody(), Section.EXPANDED | Section.TITLE_BAR | Section.TWISTIE);
		td = new TableWrapData(TableWrapData.FILL);
		td.colspan = 2;
		section.setLayoutData(td);
		section.setText("Coded Interviews");
		section.addExpansionListener(new ExpansionAdapter(){
			public void expansionStateChanged(ExpansionEvent e)
			{
				form.reflow(true);
			}
		});
		sectionClient = toolkit.createComposite(section);
		grid = new GridLayout();
		grid.numColumns = 1;
		sectionClient.setLayout(grid);
		//TODO generate the interview data
		//TODO make clickable
		gd = new GridData(GridData.FILL_HORIZONTAL);
		label = toolkit.createLabel(sectionClient, "Example Interview");
		label.setLayoutData(gd);
		section.setClient(sectionClient);
		
		section = toolkit.createSection(form.getBody(), Section.EXPANDED | Section.TITLE_BAR | Section.TWISTIE);
		td = new TableWrapData(TableWrapData.FILL);
		td.colspan = 2;
		section.setLayoutData(td);
		section.setText("Memos");
		section.addExpansionListener(new ExpansionAdapter(){
			public void expansionStateChanged(ExpansionEvent e)
			{
				form.reflow(true);
			}
		});
		sectionClient = toolkit.createComposite(section);
		grid = new GridLayout();
		grid.numColumns = 1;
		sectionClient.setLayout(grid);
		//TODO generate the memo data
		//TODO make clickable
		gd = new GridData(GridData.FILL_HORIZONTAL);
		label = toolkit.createLabel(sectionClient, "Example Memo");
		label.setLayoutData(gd);
		section.setClient(sectionClient);
		
	}
}
