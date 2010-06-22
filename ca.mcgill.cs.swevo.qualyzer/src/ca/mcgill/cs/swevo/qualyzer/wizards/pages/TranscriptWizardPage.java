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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;

/**
 * The only page in the new Transcript Wizard.
 *
 */
public class TranscriptWizardPage extends WizardPage
{
	/**
	 * 
	 */
	private static final int COMPOSITE_COLS = 4;

	private static final String AUDIO_PATH = File.separator+"audio"+File.separator; //$NON-NLS-1$
	
	protected Table fTable;
	protected Text fName;
	protected Label fAudioFile;
	protected boolean fAudioFileSelected;
	
	private Composite fContainer;
	private DateTime fDate;
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
		super(Messages.getString("wizards.pages.TranscriptWizardPage.newTranscript")); //$NON-NLS-1$
		setTitle(Messages.getString("wizards.pages.TranscriptWizardPage.newTranscript")); //$NON-NLS-1$
		setDescription(Messages.getString("wizards.pages.TranscriptWizardPage.enterInfo")); //$NON-NLS-1$
		
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
		Label label = createLabel(fContainer, 
				Messages.getString("wizards.pages.TranscriptWizardPage.name")); //$NON-NLS-1$
		fName = createText(fContainer);
		
		label = createLabel(fContainer, Messages.getString("wizards.pages.TranscriptWizardPage.date")); //$NON-NLS-1$
		fDate = createDate(fContainer);
		
		createLongLabel();
		createTable();
		
		label = createLabel(fContainer, 
				Messages.getString("wizards.pages.TranscriptWizardPage.description")); //$NON-NLS-1$
		fDescription = createText(fContainer);
		
		Composite composite = createComposite();
		label = createLabel(composite, 
				Messages.getString("wizards.pages.TranscriptWizardPage.audioFile")); //$NON-NLS-1$
		
		fAudioFile = new Label(composite, SWT.BORDER);
		fAudioFile.setText(""); //$NON-NLS-1$
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		fAudioFile.setLayoutData(gd);
		fAudioFile.addKeyListener(createAudioKeyListener());
		
		Button button = new Button(composite, SWT.PUSH);
		button.setText(Messages.getString("wizards.pages.TranscriptWizardPage.browse")); //$NON-NLS-1$
		button.addSelectionListener(createButtonListener());
		
		button = new Button(composite, SWT.PUSH);
		button.setText("Clear");
		button.addSelectionListener(createClearListener());
		
		setControl(fContainer);
		setPageComplete(false);
	}

	/**
	 * @return
	 */
	private SelectionListener createClearListener()
	{
		return new SelectionAdapter(){
			
			@Override
			public void widgetSelected(SelectionEvent event)
			{
				fAudioFile.setText("");
				fAudioFileSelected = false;
			}
		};
	}

	/**
	 * @return
	 */
	private KeyAdapter createAudioKeyListener()
	{
		return new KeyAdapter(){
			
			@Override
			public void keyReleased(KeyEvent event)
			{
				commonListenerChecks();
			}
		};
	}
	
	/**
	 * @param fContainer2
	 * @return
	 */
	private DateTime createDate(Composite container)
	{
		DateTime date = new DateTime(container, SWT.DATE | SWT.BORDER);
		return date;
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
		label = createLabel(fContainer, 
				Messages.getString("wizards.pages.TranscriptWizardPage.selectParticipants")); //$NON-NLS-1$
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
					dialog.setFilterNames(new String[]{Messages.getString(
							"wizards.pages.TranscriptWizardPage.audioExt")}); //$NON-NLS-1$
					
					String file = dialog.open();
					fAudioFile.setText(file);
					if(!file.isEmpty())
					{
						fAudioFileSelected = true;
						String errorMessage = getErrorMessage();
						if(errorMessage != null && errorMessage.equals("Please enter a valid audio filename"))
						{
							setError(null);
						}
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
				commonListenerChecks();
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
					String fileName = findAudioFile(fName.getText());
					if(!fileName.isEmpty())
					{
						fAudioFile.setText(fileName);
					}
				}
				commonListenerChecks();
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
		return (fDate.getMonth()+1)+"/"+ fDate.getDay() +"/"+ fDate.getYear(); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * @return The name field.
	 */
	public String getTranscriptName()
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
		buildParticipants();
		return fParticipants;
	}

	private void buildParticipants()
	{
		fParticipants = new ArrayList<Participant>();
		
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

	/**
	 * 
	 */
	protected void commonListenerChecks()
	{
		if(transcriptExists())
		{
			setError(Messages.getString("wizards.pages.TranscriptWizardPage.nameInUse")); //$NON-NLS-1$
		}
		else if(fName.getText().isEmpty())
		{
			setError(Messages.getString("wizards.pages.TranscriptWizardPage.enterName")); //$NON-NLS-1$
		}
		else if(!ResourcesUtil.verifyID(fName.getText()))
		{
			setError(Messages.getString("wizards.pages.TranscriptWizardPage.invalidName")); //$NON-NLS-1$
		}
		else if(fTable.getSelectionCount() <= 0)
		{
			setError(Messages.getString("wizards.pages.TranscriptWizardPage.selectOne")); //$NON-NLS-1$
		}
		else
		{
			File file = new File(fAudioFile.getText());
			if(!fAudioFile.getText().isEmpty() && !file.exists())
			{
				setError("Please enter a valid audio filename");
			}
			else
			{
				setError(null);
			}
		}
	}

}
