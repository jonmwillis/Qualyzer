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
package ca.mcgill.cs.swevo.qualyzer.dialogs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;

/**
 * 
 *
 */
public class TranscriptPropertiesDialog extends TitleAreaDialog
{
	/**
	 * 
	 */
	private static final int COLS = 3;
	private static final String TRANSCRIPT = File.separator+"transcripts"+File.separator;
	
	private final String fProjectName;
	
	private Transcript fTranscript;
	private Text fDate;
	private String fAudioPath;
	private List<Participant> fParticipants;
	private Table fTable;
	
	private String fDateS;
	private Label fAudioLabel;
	
	/**
	 * Constructor.
	 * @param shell
	 * @param transcript
	 */
	public TranscriptPropertiesDialog(Shell shell, Transcript transcript)
	{
		super(shell);
		fTranscript = transcript;
		fParticipants = new ArrayList<Participant>();
		fProjectName = fTranscript.getProject().getName();
		fAudioPath = "";
	}
	
	@Override
	public void create()
	{
		super.create();
		setTitle("Properties");
	}
	
	@Override
	public Control createDialogArea(Composite parent)
	{
		GridLayout layout = new GridLayout(2, false);
		parent.setLayout(layout);
		
		Label label = createLabel(parent, "Transcript Name:");
		label = new Label(parent, SWT.BORDER);
		label.setText(fTranscript.getName());
		label.setLayoutData(createTextGridData());
		
		label = createLabel(parent, "File path:");
		label = new Label(parent, SWT.BORDER);
		label.setText(fProjectName + TRANSCRIPT + fTranscript.getFileName());
		label.setLayoutData(createTextGridData());
		
		label = createLabel(parent, "Date:");
		fDate = createText(fTranscript.getDate(), parent);
		
		Composite composite = createComposite(parent);
		
		label = createLabel(composite, "Participants:");
		label.setLayoutData(createTextGridData());
		Button button = new Button(composite, SWT.PUSH);
		button.setText("+"); //TODO add listener
		button = new Button(composite, SWT.PUSH);
		button.setText("-"); //TODO add listener
		
		fTable = new Table(parent, SWT.MULTI);
		GridData gd = createTextGridData();
		gd.horizontalSpan = 2;
		fTable.setLayoutData(gd);
		//TODO fill out table with participants
		
		composite = createComposite(parent);
		
		label = createLabel(composite, "Audio File Path:");
		if(fTranscript.getAudioFile() != null)
		{
			fAudioPath = fProjectName + fTranscript.getAudioFile().getRelativePath();
		}
		fAudioLabel = new Label(composite, SWT.BORDER);
		fAudioLabel.setText(fAudioPath);
		fAudioLabel.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false)); //TODO why does this look weird?
		button = new Button(composite, SWT.PUSH);
		button.setText("Browse");
		button.addSelectionListener(createSelectionAdapter());
				
		return parent;
	}

	/**
	 * @return
	 */
	private SelectionAdapter createSelectionAdapter()
	{
		return new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent event)
			{
				FileDialog dialog = new FileDialog(getShell());
				//dialog.setFilterPath("");
				dialog.setFilterExtensions(new String[]{"*.mp3;*.wav"});
				dialog.setFilterNames(new String[]{"Audio (.mp3, .wav)"});
				
				fAudioPath = dialog.open();
				if(fAudioPath != null)
				{
					setMessage("Warning, changing the audio file may cause the " +
							"current audio file to be overwritten.", IMessageProvider.WARNING);
					fAudioLabel.setText(fAudioPath);
				}
			}
			
		};
	}

	/**
	 * @param parent
	 * @return
	 */
	private Composite createComposite(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = COLS;
		composite.setLayout(layout);
		GridData gd = createTextGridData();
		gd.horizontalSpan = 2;
		composite.setLayoutData(gd);
		return composite;
	}

	/**
	 * @param parent
	 */
	private Label createLabel(Composite parent, String text)
	{
		Label label = new Label(parent, SWT.NULL);
		label.setText(text);
		
		return label;
	}
	
	private Text createText(String info, Composite parent)
	{
		Text text = new Text(parent, SWT.BORDER);
		text.setText(info);
		text.setLayoutData(createTextGridData());
		
		return text;
	}

	/**
	 * @return
	 */
	private GridData createTextGridData()
	{
		GridData gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		return gd;
	}
	
	/**
	 * Get the Transcript being edited by this dialog.
	 * @return
	 */
	public Transcript getTranscript()
	{
		return fTranscript;
	}
	
	/**
	 * Get the date that was entered into the dialog.
	 * @return
	 */
	public String getDate()
	{
		return fDateS;
	}
	
	@Override
	protected void okPressed()
	{
		save();
		super.okPressed();
	}
	
	/**
	 * Get the selected audio file path.
	 * @return
	 */
	public String getAudioFile()
	{
		return fAudioPath;
	}
	
	private void save()
	{
		fDateS = fDate.getText();
		//TODO save participants
	}
}