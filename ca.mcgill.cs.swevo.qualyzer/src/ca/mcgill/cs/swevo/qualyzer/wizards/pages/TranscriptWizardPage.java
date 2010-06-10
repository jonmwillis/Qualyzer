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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import ca.mcgill.cs.swevo.qualyzer.model.AudioFile;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.util.FileUtil;

/**
 * The only page in the new Transcript Wizard.
 *
 */
public class TranscriptWizardPage extends WizardPage
{
	/**
	 * 
	 */
	private static final int COMPOSITE_COLS = 3;

	private static final String AUDIO_PATH = File.separator+"audio"+File.separator; //$NON-NLS-1$
	
	protected Table fTable;
	protected Text fName;
	protected Text fAudioFile;
	protected boolean fAudioFileSelected;
	
	private Composite fContainer;
	private Text fDate;
	private Project fProject;
	private ArrayList<Participant> fParticipants;
	private Text fDescription;
	private final String fWorkspacePath;
	
	/**
	 * 
	 * @param project
	 */
	public TranscriptWizardPage(Project project)
	{
		super("New Transcript");
		setTitle("New Transcript");
		setDescription("Please enter the following information to create a new transcript.");
		
		fProject = project;
		fParticipants = new ArrayList<Participant>();
		fAudioFileSelected = false;
		fContainer = null;
		
		IProject wProject = ResourcesPlugin.getWorkspace().getRoot().getProject(fProject.getName());
		fWorkspacePath = wProject.getLocation().toString();
	}
	
	/**
	 * 
	 * @param project
	 * @param id
	 */
	public TranscriptWizardPage(Project project, String id)
	{
		super(id);
		setTitle(id);
		
		fProject = project;
		fParticipants = new ArrayList<Participant>();
		fAudioFileSelected = false;
		fContainer = null;
		
		IProject wProject = ResourcesPlugin.getWorkspace().getRoot().getProject(fProject.getName());
		fWorkspacePath = wProject.getLocation().toString();
	}

	/**
	 * 
	 * @return
	 */
	protected Composite getfContainer()
	{
		return fContainer;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent)
	{
		if(fContainer == null)
		{
			fContainer = new Composite(parent, SWT.NULL);
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			fContainer.setLayout(layout);
		}
		
		@SuppressWarnings("unused")
		Label label = createLabel(fContainer, "Transcript name:");
		fName = createText(fContainer);
		
		label = createLabel(fContainer, "Date:");
		fDate = createText(fContainer);
		
		createLongLabel();
		createTable();
		
		label = createLabel(fContainer, "Description:");
		fDescription = createText(fContainer);
		
		Composite composite = createComposite();
		label = createLabel(composite, "Audio File:");
		fAudioFile = createText(composite);
		Button button = new Button(composite, SWT.PUSH);
		button.setText("Browse");
		button.addSelectionListener(createButtonListener());
		
		setControl(fContainer);
		setPageComplete(false);
	}
	
	/**
	 * 
	 * @param parent
	 * @param container
	 */
	protected void createControl(Composite parent, Composite container)
	{
		fContainer = container;
		createControl(parent);
	}

	/**
	 * 
	 */
	private void createLongLabel()
	{
		GridData gd;
		Label label;
		label = createLabel(fContainer, "Select the Participants");
		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		gd.horizontalSpan = 2;
		label.setLayoutData(gd);
	}

	/**
	 * @return
	 */
	private Composite createComposite()
	{
		GridLayout layout;
		GridData gd;
		Composite composite = new Composite(fContainer, SWT.NULL);
		layout = new GridLayout();
		layout.numColumns = COMPOSITE_COLS;
		composite.setLayout(layout);
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.horizontalSpan = 2;
		composite.setLayoutData(gd);
		return composite;
	}

	/**
	 * 
	 */
	private void createTable()
	{
		GridData gd;
		fTable = new Table(fContainer, SWT.MULTI | SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.horizontalSpan = 2;
		fTable.setLayoutData(gd);
		populateTable();
		fTable.addSelectionListener(createSelectionListener());
	}

	/**
	 * @return
	 */
	private SelectionListener createButtonListener()
	{
		return new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e){}

			@Override
			public void widgetSelected(SelectionEvent e)
			{
					FileDialog dialog = new FileDialog(fContainer.getShell());
					dialog.setFilterPath(fWorkspacePath+AUDIO_PATH);
					dialog.setFilterExtensions(new String[]{"*.mp3;*.wav"}); //$NON-NLS-1$
					dialog.setFilterNames(new String[]{"Audio (.mp3, .wav)"});
					
					String file = dialog.open();
					fAudioFile.setText(file);
					if(!file.isEmpty())
					{
						fAudioFileSelected = true;
					}
			}
			
		};
	}

	/**
	 * @return
	 */
	protected SelectionListener createSelectionListener()
	{
		return new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e)	{}

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if(transcriptExists())
				{
					setError("That name is already in use");
				}
				else if(fName.getText().isEmpty())
				{
					setError("Please enter a name");
				}
				else if(fTable.getSelectionCount() > 0)
				{
					setError(null);
				}
				else
				{
					setError("Select at least one participant");
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
		text.setText(""); //$NON-NLS-1$
		GridData gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		text.setLayoutData(gd);
		text.addKeyListener(createKeyListener());
		
		return text;
	}

	/**
	 * @return
	 */
	protected KeyListener createKeyListener()
	{
		return new KeyListener(){
			@Override
			public void keyPressed(KeyEvent e){}
			@Override
			public void keyReleased(KeyEvent e)
			{
				if(!fAudioFileSelected && !fName.getText().isEmpty())
				{
					fAudioFile.setText(findAudioFile(fName.getText()));
				}
				if(transcriptExists())
				{
					setError("That name is already in use");
				}
				else if(fName.getText().isEmpty())
				{
					setError("Please enter a name");
				}
				else if(fTable.getSelectionCount() > 0)
				{
					setError(null);
				}
				else
				{
					setError("Select at least one participant");
				}
			}
		};
	}
	
	/**
	 * Sets the error message and the page complete status.
	 * @param message
	 */
	protected void setError(String message)
	{
		setErrorMessage(message);
		if(message == null)
		{
			setPageComplete(true);
		}
		else
		{
			setPageComplete(false);
		}
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
	
	/**
	 * 
	 * @return The data field.
	 */
	public String getDate()
	{
		return fDate.getText();
	}
	
	/**
	 * @return The name field.
	 */
	public String getName()
	{
		return fName.getText();
	}
	
	/**
	 * 
	 * @return The audio file's absoulte path.
	 */
	public String getAudioFile()
	{
		return fAudioFile.getText();
	}
	
	/**
	 * 
	 * @return The Description field.
	 */
	public String getTranscriptDescription()
	{
		return fDescription.getText();
	}
	
	/**
	 * 
	 * @return The list of participants that were selected.
	 */
	public List<Participant> getParticipants()
	{
		return fParticipants;
	}
	
	/**
	 * 
	 * @return The Transcript represented by the data entered in the wizard.
	 */
	public Transcript getTranscript()
	{		
		Transcript transcript = new Transcript();
		
		transcript.setName(fName.getText());
		transcript.setFileName(fName.getText()+".txt"); //$NON-NLS-1$
		transcript.setDate(fDate.getText());
		buildParticipants();
		transcript.setParticipants(fParticipants);
		
		if(!fAudioFile.getText().isEmpty())
		{
			//if the audio file is not in the workspace then copy it there.
			AudioFile audioFile = new AudioFile();
			String audioPath = fAudioFile.getText();
			int i = audioPath.lastIndexOf('.');
			String relativePath = transcript.getName()+audioPath.substring(i);
			
			if(audioPath.indexOf(fWorkspacePath) == -1 || namesAreDifferent(transcript.getName(), audioPath))
			{
				copyAudioFile(audioPath, relativePath);
			}
			audioFile.setRelativePath(AUDIO_PATH+relativePath);
			transcript.setAudioFile(audioFile);
		}
		
		return transcript;
	}

	/**
	 * @param name
	 * @param audioPath
	 * @return
	 */
	private boolean namesAreDifferent(String name, String audioPath)
	{
		int i = audioPath.lastIndexOf(File.separatorChar) + 1;
		int j = audioPath.lastIndexOf('.');
		return !name.equals(audioPath.substring(i, j));
	}

	/**
	 * @param audioPath
	 * @param relativePath
	 */
	private void copyAudioFile(String audioPath, String relativePath)
	{
		File file = new File(audioPath);
		File fileCpy = new File(fWorkspacePath+AUDIO_PATH+relativePath);
		
		try
		{
			FileUtil.copyFile(file, fileCpy);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private void buildParticipants()
	{
		TableItem[] items = fTable.getSelection();
		
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
	
	/**
	 * 
	 * @param filename
	 * @return The absolute path of the audio file represented by the filename.
	 */
	protected String findAudioFile(String filename)
	{	
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(fProject.getName());
		
		String path = project.getLocation() + AUDIO_PATH + filename +".mp3"; //$NON-NLS-1$
		File file = new File(path);
		
		if(file.exists())
		{
			return path;
		}
		
		path =  project.getLocation() + AUDIO_PATH + filename +".wav"; //$NON-NLS-1$
		file = new File(path);
		
		return file.exists() ? path : ""; //$NON-NLS-1$
	}

}
