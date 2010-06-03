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
/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.wizards.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 * The page of the Add Participant Wizard.
 *
 */
public class AddParticipantPage extends WizardPage
{
	private Composite fContainer;
	private Project fProject;
	private Text fIdText;
	private Text fFullNameText;
	private Text fContactInfoText;
	private Text fNotesText;
	
	/**
	 * Constructor.
	 * @param project
	 */
	public AddParticipantPage(Project project)
	{
		super(Messages.wizard_pages_AddParticipantPage_addParticipant);
		setTitle(Messages.wizard_pages_AddParticipantPage_addParticipant);
		setDescription(Messages.wizard_pages_AddParticipantPage_enterInfo);
		fProject = project;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent)
	{
		fContainer = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		fContainer.setLayout(layout);
		
		Label label = new Label(fContainer, SWT.NULL);
		label.setText(Messages.wizard_pages_AddParticipantPage_partID);
		fIdText = new Text(fContainer, SWT.BORDER);
		fIdText.setText(""); //$NON-NLS-1$
		fIdText.addKeyListener(createKeyListener());
		
		label = new Label(fContainer, SWT.NULL);
		label.setText(Messages.wizard_pages_AddParticipantPage_fullName);
		fFullNameText = new Text(fContainer, SWT.BORDER);
		fFullNameText.setText(""); //$NON-NLS-1$
		
		//JF: removing for 0.1 to be consistent with the editor
//		label = new Label(fContainer, SWT.NULL);
//		label.setText(Messages.wizard_pages_AddParticipantPage_ContactInfo);
//		fContactInfoText = new Text(fContainer, SWT.BORDER);
//		fContactInfoText.setText(""); //$NON-NLS-1$
//		
//		label = new Label(fContainer, SWT.NULL);
//		label.setText(Messages.wizard_pages_AddParticipantPage_notes);
//		fNotesText = new Text(fContainer, SWT.BORDER);
//		fNotesText.setText(""); //$NON-NLS-1$
		
		setGridData();
		setControl(fContainer);
		setPageComplete(false);
	}
	
	/**
	 * @return
	 */
	private KeyListener createKeyListener()
	{
		return new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e){}

			@Override
			public void keyReleased(KeyEvent e)
			{
				if(fIdText.getText().isEmpty())
				{
					setErrorMessage(Messages.wizard_pages_AddParticipantPage_pleaseEnterId);
					setPageComplete(false);
				}
				else if(idExists())
				{
					setErrorMessage(Messages.wizard_pages_AddParticipantPage_idUsed);
					setPageComplete(false);
				}
				else
				{
					setPageComplete(true);
					setErrorMessage(null);
				}
			}
		};
	}
	
	private boolean idExists()
	{
		for(Participant part : fProject.getParticipants())
		{
			if(part.getParticipantId().equals(fIdText.getText()))
			{
				return true;
			}
		}
		return false;
	}

	private void setGridData()
	{
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		fIdText.setLayoutData(gd);
		fFullNameText.setLayoutData(gd);
		//fContactInfoText.setLayoutData(gd);
		//fNotesText.setLayoutData(gd);
	}
	
	/**
	 * Get the Participant ID field.
	 * @return
	 */
	public String getParticipantId()
	{
		return fIdText.getText();
	}

	/**
	 * Get the Fullname field.
	 * @return
	 */
	public String getFullname()
	{
		return fFullNameText.getText();
	}

	/**
	 * Get the contact info field.
	 * @return
	 */
	public String getContactInfo()
	{
		return fContactInfoText.getText();
	}

	/**
	 * Get the notes field.
	 * @return
	 */
	public String getNotes()
	{
		return fNotesText.getText();
	}
	
	/**
	 * Build the participant represented by the information entered in this page.
	 * @return The participant that was built.
	 */
	public Participant getParticipant()
	{
		Participant participant = new Participant();
		participant.setParticipantId(getParticipantId());
		participant.setFullName(getFullname());
		//participant.setNotes(getNotes());
		//participant.setContactInfo(getContactInfo());
		return participant;
	}

}
