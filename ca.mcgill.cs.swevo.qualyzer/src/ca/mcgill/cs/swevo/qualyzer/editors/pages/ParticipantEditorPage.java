/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jonathan Faubert
 *     Martin Robillard
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.editors.pages;

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
import org.hibernate.LazyInitializationException;

import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;

/**
 * The form used to edit participant data.
 */
public class ParticipantEditorPage extends FormPage
{
	private static final String LABEL_PARTICIPANT_NAME = "Participant Name:";
	private static final String LABEL_PARTICIPANT_ID = "Participant ID:";
	private static final String LABEL_PARTICIPANT = "Participant";
	private Participant fParticipant;
	private Text fID;
	private Text fFullname;
	private Text fContactInfo;
	private Text fNotes;
	private boolean fIsDirty;

	/**
	 * Construct a new participant page, the only one for the editor.
	 * @param editor The form editor.
	 * @param participant The participant to edit.
	 */
	public ParticipantEditorPage(FormEditor editor, Participant participant)
	{
		super(editor, "ParticipantEditorPage", "Participant");
		fParticipant = participant;
		fIsDirty = false;
	}
	
	@Override
	public void createFormContent(IManagedForm managedForm)
	{
		final ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		Composite body = form.getBody();
		form.setText(LABEL_PARTICIPANT);
		
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		body.setLayout(layout);
		
		toolkit.createLabel(body, LABEL_PARTICIPANT_ID);
		fID = createText(toolkit, fParticipant.getParticipantId(), body);
		
		toolkit.createLabel(body, LABEL_PARTICIPANT_NAME);
		fFullname = createText(toolkit, fParticipant.getFullName(), body);
		
		// MPR: removed to simplify the UI for 0.1
//		toolkit.createLabel(body, "Contact Info:");
//		fContactInfo = createText(toolkit, fParticipant.getContactInfo(), body);
				
//		createLongLabel(toolkit, body, "Notes");
//		createNotesArea(toolkit, body);
		
		//TODO add +/- buttons
		//buildInterviewsSection(form, toolkit, body);
		
		//buildCodesSection(form, toolkit, body);
		
		toolkit.paintBordersFor(body);
	}

	/**
	 * @param toolkit
	 * @param body
	 */
	// Let's leave this out for 0.1
//	private void createNotesArea(FormToolkit toolkit, Composite body)
//	{
//		TableWrapData td;
//		fNotes = toolkit.createText(body, fParticipant.getNotes());
//		td = new TableWrapData(TableWrapData.FILL_GRAB);
//		td.rowspan = 2;
//		td.colspan = 2;
//		fNotes.setLayoutData(td);
//		fNotes.addKeyListener(createKeyListener());
//	}

	/**
	 * @param form
	 * @param toolkit
	 * @param body
	 */
	// Let's leave this out for 0.1
//	private void buildCodesSection(final ScrolledForm form, FormToolkit toolkit, Composite body)
//	{
//		TableWrapData td;
//		Label label;
//		Section section;
//		Composite sectionClient;
//		GridLayout gridLayout;
//		section = toolkit.createSection(body, Section.EXPANDED | Section.TWISTIE | Section.TITLE_BAR);
//		section.setText("Codes");
//		td = new TableWrapData(TableWrapData.FILL_GRAB);
//		td.colspan = 2;
//		section.setLayoutData(td);
//		section.addExpansionListener(createExpansionListener(form));
//		sectionClient = toolkit.createComposite(section);
//		//TODO get all codes related to participant
//		gridLayout = new GridLayout();
//		gridLayout.numColumns = 2;
//		gridLayout.makeColumnsEqualWidth = true;
//		sectionClient.setLayout(gridLayout);
//		GridData gd = new GridData(SWT.FILL, SWT.NULL, true, false);
//		label = toolkit.createLabel(sectionClient, "Example code");
//		label.setLayoutData(gd);
//		label = toolkit.createLabel(sectionClient, "Example Interview");
//		label.setLayoutData(gd);
//		section.setClient(sectionClient);
//	}

	/**
	 * @param form
	 * @param toolkit
	 * @param body
	 */
	// Let's leave this out for 0.1
//	private void buildInterviewsSection(final ScrolledForm form, FormToolkit toolkit, Composite body)
//	{
//		TableWrapData td;
//		Section section = toolkit.createSection(body, Section.EXPANDED | Section.TITLE_BAR | Section.TWISTIE);
//		td = new TableWrapData(TableWrapData.FILL_GRAB);
//		td.colspan = 2;
//		section.setLayoutData(td);
//		section.addExpansionListener(createExpansionListener(form));
//		section.setText("Interviews");
//		Composite sectionClient = toolkit.createComposite(section);
//		GridLayout gridLayout = new GridLayout();
//		gridLayout.numColumns = 1;
//		sectionClient.setLayout(gridLayout);
//		//TODO make clickable
//TODO fix the Lazy Initialization Exception
//		buildInterviews(toolkit, sectionClient);
//		sectionClient.setLayoutData(td);
//		section.setClient(sectionClient);
//	}

	/**
	 * @param toolkit
	 * @param body
	 */
	// Let's leave this out for 0.1
//	private Label createLongLabel(FormToolkit toolkit, Composite body, String text)
//	{
//		TableWrapData td;
//		Label label;
//		label = toolkit.createLabel(body, text);
//		td = new TableWrapData(TableWrapData.FILL_GRAB);
//		td.colspan = 2;
//		label.setLayoutData(td);
//		return label;
//	}

	/**
	 * @param toolkit 
	 * @param sectionClient 
	 * 
	 */
	// Let's leave this out for 0.1
//	private void buildInterviews(FormToolkit toolkit, Composite sectionClient)
//	{
//		try
//		{
//			for(Transcript transcript : fParticipant.getProject().getTranscripts())		
//			{
//				if(transcript.getParticipants().contains(fParticipant))
//				{
//					GridData gd = new GridData(SWT.FILL, SWT.NULL, true, false);
//					Label label = toolkit.createLabel(sectionClient, transcript.getName());
//					label.setLayoutData(gd);	
//				}
//			}
//		}
//		catch(LazyInitializationException e)
//		{
//			Label label = toolkit.createLabel(sectionClient, "example interview - unable to lazily initialize");
//			GridData gd = new GridData(SWT.FILL, SWT.NULL, true, false);
//			label.setLayoutData(gd);
//		}
//	}

	/**
	 * @param form
	 * @return
	 */
	// Let's leave this out for 0.1
//	private ExpansionAdapter createExpansionListener(final ScrolledForm form)
//	{
//		return new ExpansionAdapter(){
//			public void expansionStateChanged(ExpansionEvent e)
//			{
//				form.reflow(true);
//			}
//		};
//	}
	
	private Text createText(FormToolkit toolkit, String data, Composite parent)
	{
		Text text = toolkit.createText(parent, data);
		TableWrapData tableData = new TableWrapData(TableWrapData.FILL_GRAB);
		text.setLayoutData(tableData);
		text.addKeyListener(createKeyListener());
		
		return text;
	}
	
	/**
	 * Get the Participant Id that was entered.
	 * @return The Participant Id
	 */
	public String getId()
	{
		return fID.getText();
	}
	
	/**
	 * Get the Participant's full name.
	 * @return The full name field.
	 */
	public String getFullname()
	{
		return fFullname.getText();
	}
	
	/**
	 * Get the participant's contact info.
	 * @return The contact info field.
	 */
	public String getContactInfo()
	{
		return fContactInfo.getText();
	}

	/**
	 * Get the notes related to this participant.
	 * @return The notes field.
	 */
	public String getNotes()
	{
		return fNotes.getText();
	}
	
	private KeyListener createKeyListener()
	{
		return new KeyAdapter() 
		{	
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
				if(!fID.getText().equals(fParticipant.getParticipantId()))
				{
					return true;
				}
				else if(!fFullname.getText().equals(fParticipant.getFullName()))
				{
					return true;
				}
				return false;
			}
		};
	}
	
	@Override
	public boolean isDirty()
	{
		return fIsDirty;
	}
	
	/**
	 * Clears the dirty flag.
	 */
	public void notDirty()
	{
		fIsDirty = false;
		getEditor().editorDirtyStateChanged();
	}
}
