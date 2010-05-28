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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class TranscriptWizardPage extends WizardPage
{
	private static final String AUDIO_PATH = "";  //projectpath/audio/
	
	private Composite fContainer;
	private Table fTable;
	private Text fDate;
	private Text fName;
	private Text fAudioFile;
	private Project fProject;
	private ArrayList<Participant> fParticipants;
	private Text fDescription;
	
	public TranscriptWizardPage(Project project)
	{
		super("New Transcript");
		setTitle("New Transcript");
		setDescription("Please enter the following information to create a new transcript.");
		fProject = project;
		fParticipants = new ArrayList<Participant>();
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
		GridData gd;
		
		Label label = createLabel(fContainer, "Transcript name:");
		
		fName = createText(fContainer);
		
		label = createLabel(fContainer, "Date:");
		
		fDate = createText(fContainer);
		
		label = createLabel(fContainer, "Select the Participants");
		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		gd.horizontalSpan = 2;
		label.setLayoutData(gd);
		
		fTable = new Table(fContainer, SWT.MULTI | SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.horizontalSpan = 2;
		fTable.setLayoutData(gd);
		populateTable();
		fTable.addSelectionListener(createSelectionListener());
		//TODO Selection listener?
		
		label = createLabel(fContainer, "Description:");
		
		fDescription = createText(fContainer);
		
		Composite composite = new Composite(fContainer, SWT.NULL);
		layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.horizontalSpan = 2;
		composite.setLayoutData(gd);
		
		label = createLabel(fContainer, "Audio File:");
		
		fAudioFile = createText(composite);
		
		Button button = new Button(composite, SWT.PUSH);
		button.setText("Browse");
		
		setControl(fContainer);
		setPageComplete(false);
	}

	/**
	 * @return
	 */
	private SelectionListener createSelectionListener()
	{
		return new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e)	{}

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if(transcriptExists())
				{
					setErrorMessage("That name is already in use");
					setPageComplete(false);
					return;
				}
				if(fName.getText().isEmpty())
				{
					setErrorMessage("Please enter a name");
					setPageComplete(false);
				}
				else if(fTable.getSelectionCount() > 0)
				{
					setErrorMessage(null);
					setPageComplete(true);
				}
				else
				{
					setErrorMessage("Select at least one participant");
					setPageComplete(false);
				}
			}
		};
	}

	/**
	 * @param string 
	 * @param fContainer2 
	 * 
	 */
	private Label createLabel(Composite container, String string)
	{
		Label label = new Label(container, SWT.NULL);
		label.setText(string);
		return label;
	}
	
	private Text createText(Composite parent)
	{
		Text text = new Text(parent, SWT.BORDER);
		text.setText("");
		GridData gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		text.setLayoutData(gd);
		text.addKeyListener(createKeyListener());
		
		return text;
	}

	/**
	 * @return
	 */
	private KeyListener createKeyListener()
	{//TODO finish
		return new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e){}

			@Override
			public void keyReleased(KeyEvent e)
			{
				if(transcriptExists())
				{
					setErrorMessage("That name is already in use");
					setPageComplete(false);
					return;
				}
				if(fName.getText().isEmpty())
				{
					setErrorMessage("Please enter a name");
					setPageComplete(false);
				}
				else if(fTable.getSelectionCount() > 0)
				{
					setErrorMessage(null);
					setPageComplete(true);
				}
				else
				{
					setErrorMessage("Select at least one participant");
					setPageComplete(false);
				}
			}
		};
	}

	/**
	 * @return
	 */
	protected boolean transcriptExists()
	{
		for(Transcript transcript : fProject.getTranscripts())
		{
			if(transcript.getName().equals(fName.getText()))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 */
	private void populateTable()
	{
		for(Participant participant : fProject.getParticipants())
		{
			TableItem item = new TableItem(fTable, SWT.NULL);
			item.setText(participant.getParticipantId());
		}
	}
	
	public String getDate()
	{
		return fDate.getText();
	}
	
	public String getName()
	{
		return fName.getText();
	}
	
	public String getAudioFile()
	{
		return fAudioFile.getText();
	}
	
	public String getTranscriptDescription()
	{
		return fDescription.getText();
	}
	
	public List<Participant> getParticipants()
	{
		return fParticipants;
	}
	
	public Transcript getTranscript()
	{		
		Transcript transcript = new Transcript();
		
		transcript.setName(fName.getText());
		transcript.setFileName(fName.getText()+".txt");
		buildParticipants();
		transcript.setParticipants(fParticipants);
		
		return transcript;
	}

	/**
	 * 
	 */
	private void buildParticipants()
	{
		TableItem[] items = fTable.getItems();
		
		for(Participant participant : fProject.getParticipants())
		{
			for(TableItem item : items)
			{
				if(participant.getParticipantId().equals(item.getText()))
				{
					fParticipants.add(participant);
				}
			}
		}
	}

}
