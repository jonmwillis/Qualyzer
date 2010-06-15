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
import ca.mcgill.cs.swevo.qualyzer.util.ResourcesUtil;

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
		super(Messages.getString("wizards.pages.AddParticipantPage.addParticipant")); //$NON-NLS-1$
		setTitle(Messages.getString("wizards.pages.AddParticipantPage.addParticipant")); //$NON-NLS-1$
		setDescription(Messages.getString("wizards.pages.AddParticipantPage.enterInfo")); //$NON-NLS-1$
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
		label.setText(Messages.getString("wizards.pages.AddParticipantPage.participantId")); //$NON-NLS-1$
		fIdText = new Text(fContainer, SWT.BORDER);
		fIdText.setText(""); //$NON-NLS-1$
		fIdText.addKeyListener(createKeyListener());
		
		label = new Label(fContainer, SWT.NULL);
		label.setText(Messages.getString("wizards.pages.AddParticipantPage.fullName")); //$NON-NLS-1$
		fFullNameText = new Text(fContainer, SWT.BORDER);
		fFullNameText.setText(""); //$NON-NLS-1$
		
		//JF: removing for 0.1 to be consistent with the editor
//		label = new Label(fContainer, SWT.NULL);
//		label.setText("Contact Info");
//		fContactInfoText = new Text(fContainer, SWT.BORDER);
//		fContactInfoText.setText(""); //$NON-NLS-1$
//		
//		label = new Label(fContainer, SWT.NULL);
//		label.setText("Notes");
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
					setErrorMessage(Messages.getString("wizards.pages.AddParticipantPage.enterId")); //$NON-NLS-1$
					setPageComplete(false);
				}
				else if(!ResourcesUtil.verifyID(fIdText.getText()))
				{
					setPageComplete(false);
					setErrorMessage(Messages.getString("wizards.pages.AddParticipantPage.invalidID")); //$NON-NLS-1$
				}
				else if(idExists())
				{
					setErrorMessage(Messages.getString("wizards.pages.AddParticipantPage.idInUse")); //$NON-NLS-1$
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
