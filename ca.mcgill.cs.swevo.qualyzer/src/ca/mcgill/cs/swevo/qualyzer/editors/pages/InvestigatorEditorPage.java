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

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
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

import ca.mcgill.cs.swevo.qualyzer.model.Investigator;

/**
 * The main page of the Investigator editor.
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class InvestigatorEditorPage extends FormPage
{
	/**
	 * 
	 */
	private static final String INVESTIGATOR = "Investigator";
	private Text fNickname;
	private Text fFullname;
	private Text fInstitution;
	
	private Investigator fInvestigator;
	private boolean fIsDirty;
	/**
	 * @param editor
	 * @param investigator 
	 */
	public InvestigatorEditorPage(FormEditor editor, Investigator investigator)
	{
		super(editor, INVESTIGATOR, INVESTIGATOR);
		fInvestigator = investigator;
		fIsDirty = false;
	}

	@Override
	public void createFormContent(IManagedForm managed)
	{
		final ScrolledForm form = managed.getForm();
		FormToolkit toolkit = managed.getToolkit();
		form.setText(INVESTIGATOR);
		
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		Composite body = form.getBody();
		body.setLayout(layout);
		
		@SuppressWarnings("unused")
		Label label = toolkit.createLabel(body, "Nickname:");
		fNickname = createText(toolkit, fInvestigator.getNickName(), body);
		
		label = toolkit.createLabel(body, "Full Name:");
		fFullname = createText(toolkit, fInvestigator.getFullName(), body);
		fNickname.addKeyListener(createKeyAdapter(form));

		label = toolkit.createLabel(body, "Institution:");
		fInstitution = createText(toolkit, fInvestigator.getInstitution(), body);
		
		createInterviewSection(form, toolkit, body);
		
		createCodedSection(form, toolkit, body);
		
		createMemoSection(form, toolkit, body);
	
		toolkit.paintBordersFor(body);
	}

	/**
	 * @param form 
	 * @return
	 */
	private KeyAdapter createKeyAdapter(final ScrolledForm form)
	{
		return new KeyAdapter(){
			private ScrolledForm fForm = form;
			
			@Override
			public void keyReleased(KeyEvent event)
			{
				if(fNickname.getText().isEmpty())
				{
					fForm.setMessage("Please enter a nickname", 
							IMessageProvider.ERROR);
					notDirty();
				}
				else if(nicknameInUse())
				{
					fForm.setMessage("That nickname is taken", 
							IMessageProvider.ERROR);
					notDirty();
				}
				else
				{
					fForm.setMessage(null, IMessageProvider.NONE);
				}
			}
		};
	}

	/**
	 * @return
	 */
	protected boolean nicknameInUse()
	{
		for(Investigator investigator : fInvestigator.getProject().getInvestigators())
		{
			if(!investigator.equals(fInvestigator) && fNickname.getText().equals(investigator.getNickName()))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * @param form
	 * @param toolkit
	 * @param body
	 */
	private void createMemoSection(final ScrolledForm form, FormToolkit toolkit, Composite body)
	{
		TableWrapData td;
		Label label;
		Section section;
		Composite sectionClient;
		GridLayout grid;
		GridData gd;
		section = toolkit.createSection(body, Section.EXPANDED | Section.TITLE_BAR | Section.TWISTIE);
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.colspan = 2;
		section.setLayoutData(td);
		section.setText("Memos");
		section.addExpansionListener(createExpansionListener(form));
		sectionClient = toolkit.createComposite(section);
		grid = new GridLayout();
		grid.numColumns = 1;
		sectionClient.setLayout(grid);
		//TODO generate the memo data
		//TODO make clickable
		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		label = toolkit.createLabel(sectionClient, "Example Memo"); //$NON-NLS-1$
		label.setLayoutData(gd);
		section.setClient(sectionClient);
	}

	/**
	 * @param form
	 * @param toolkit
	 * @param body
	 */
	private void createCodedSection(final ScrolledForm form, FormToolkit toolkit, Composite body)
	{
		TableWrapData td;
		Label label;
		Section section;
		Composite sectionClient;
		GridLayout grid;
		GridData gd;
		section = toolkit.createSection(body, Section.EXPANDED | Section.TITLE_BAR | Section.TWISTIE);
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.colspan = 2;
		section.setLayoutData(td);
		section.setText("Coded Transcripts");
		section.addExpansionListener(createExpansionListener(form));
		sectionClient = toolkit.createComposite(section);
		grid = new GridLayout();
		grid.numColumns = 1;
		sectionClient.setLayout(grid);
		//TODO generate the interview data
		//TODO make clickable
		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		label = toolkit.createLabel(sectionClient, "Example Interview"); //$NON-NLS-1$
		label.setLayoutData(gd);
		section.setClient(sectionClient);
	}

	/**
	 * @param form
	 * @param toolkit
	 * @param body
	 */
	private void createInterviewSection(final ScrolledForm form, FormToolkit toolkit, Composite body)
	{
		TableWrapData td;
		Label label;
		Section section = toolkit.createSection(body, Section.EXPANDED | Section.TITLE_BAR | Section.TWISTIE);
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.colspan = 2;
		section.setLayoutData(td);
		section.setText("Transcripts for Conducted Interviews");
		section.addExpansionListener(createExpansionListener(form));
		Composite sectionClient = toolkit.createComposite(section);
		GridLayout grid = new GridLayout();
		grid.numColumns = 1;
		sectionClient.setLayout(grid);
		//TODO generate the interview data
		//TODO make clickable
		GridData gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		label = toolkit.createLabel(sectionClient, "Example Interview"); //$NON-NLS-1$
		label.setLayoutData(gd);
		section.setClient(sectionClient);
	}

	/**
	 * @param form
	 * @return
	 */
	private ExpansionAdapter createExpansionListener(final ScrolledForm form)
	{
		return new ExpansionAdapter(){
			public void expansionStateChanged(ExpansionEvent e)
			{
				form.reflow(true);
			}
		};
	}
	
	private Text createText(FormToolkit toolkit, String data, Composite parent)
	{
		Text text = toolkit.createText(parent, data);
		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		text.setLayoutData(td);
		text.addKeyListener(createKeyListener());
		
		return text;
	}
	
	@Override
	public boolean isDirty()
	{
		return fIsDirty;
	}
	
	/**
	 * Get the Nickname that was entered for this Investigator.
	 * @return The Nickname field.
	 */
	public String getNickname()
	{
		return fNickname.getText();
	}
	
	/**
	 * Get the full name that was entered for this Investigator.
	 * @return The full name field.
	 */
	public String getFullname()
	{
		return fFullname.getText();
	}
	
	/**
	 * Get the Institution that was entered for this Investigator.
	 * @return The Institution field.
	 */
	public String getInstitution()
	{
		return fInstitution.getText();
	}
	
	private KeyListener createKeyListener()
	{
		return new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e){}

			@Override
			public void keyReleased(KeyEvent e)
			{
				if(!fIsDirty && fieldHasChanged())
				{
					fIsDirty = true;
					getEditor().editorDirtyStateChanged();
				}
			}

			private boolean fieldHasChanged()
			{
				boolean hasChanged = false;
				if(!fNickname.getText().equals(fInvestigator.getNickName()))
				{
					hasChanged = true;
				}
				else if(!fFullname.getText().equals(fInvestigator.getFullName()))
				{
					hasChanged = true;
				}
				else if(!fInstitution.getText().equals(fInvestigator.getInstitution()))
				{
					hasChanged = true;
				}
				return hasChanged;
			}
			
		};
	}

	/**
	 * Set dirty to false.
	 */
	public void notDirty()
	{
		fIsDirty = false;
		getEditor().editorDirtyStateChanged();
	}
}
