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
 * @author Jonathan Faubert (jonfaub@gmail.com)
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
	
	public AddParticipantPage(Project project)
	{
		super("Add Participant");
		setTitle("Add Participant");
		setDescription("Enter the information for a new Participant");
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
		label.setText("Participant ID");
		fIdText = new Text(fContainer, SWT.BORDER);
		fIdText.setText("");
		fIdText.addKeyListener(createKeyListener());
		
		label = new Label(fContainer, SWT.NULL);
		label.setText("Full Name");
		fFullNameText = new Text(fContainer, SWT.BORDER);
		fFullNameText.setText("");
		
		label = new Label(fContainer, SWT.NULL);
		label.setText("Contact Info");
		fContactInfoText = new Text(fContainer, SWT.BORDER);
		fContactInfoText.setText("");
		
		label = new Label(fContainer, SWT.NULL);
		label.setText("Notes");
		fNotesText = new Text(fContainer, SWT.BORDER);
		fNotesText.setText("");
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
					setErrorMessage(null);
					setPageComplete(false);
				}
				else if(idExists())
				{
					setErrorMessage("This ID is already in use.");
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
		fContactInfoText.setLayoutData(gd);
		fNotesText.setLayoutData(gd);
	}
	
	public String getParticipantId()
	{
		return fIdText.getText();
	}

	public String getFullname()
	{
		return fFullNameText.getText();
	}

	public String getContactInfo()
	{
		return fContactInfoText.getText();
	}

	public String getNotes()
	{
		return fNotesText.getText();
	}
	
	public Participant getParticipant()
	{
		Participant participant = new Participant();
		participant.setParticipantId(getParticipantId());
		participant.setFullName(getFullname());
		participant.setNotes(getNotes());
		participant.setContactInfo(getContactInfo());
		return participant;
	}

}
