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

import java.io.File;

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
import org.eclipse.swt.widgets.Text;

import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class ImportTranscriptPage extends TranscriptWizardPage
{
	
	/**
	 * 
	 */
	private static final int COLS = 3;
	private Text fTranscriptFile;
	
	/**
	 * 
	 * @param project
	 */
	public ImportTranscriptPage(Project project)
	{
		super(project, "Import Transcript");
		setDescription("Please enter the following information to import a transcript");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent)
	{	
		if(getfContainer() != null) //TODO hack : I want it to say at the parent when 
		{								//I call createControl(parent, composite)
			super.createControl(parent);
			return;
		}
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		container.setLayout(layout);
				
		Composite composite = new Composite(container, SWT.NULL);
		layout = new GridLayout();
		layout.numColumns = COLS;
		composite.setLayout(layout);
		
		GridData gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		gd.horizontalSpan = 2;
		composite.setLayoutData(gd);
		
		Label label = new Label(composite, SWT.NULL);
		label.setText("Filename:");
		fTranscriptFile = new Text(composite, SWT.BORDER);
		fTranscriptFile.setText(""); //$NON-NLS-1$
		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		fTranscriptFile.setLayoutData(gd);
		fTranscriptFile.addKeyListener(createKeyListener());
		
		Button button = new Button(composite, SWT.PUSH);
		button.addSelectionListener(createNewSelectionListener());
		button.setText("Browse");
		
		super.createControl(parent, container);

	}

	/**
	 * @return
	 */
	private SelectionListener createNewSelectionListener()
	{
		return new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e){}

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				FileDialog dialog = new FileDialog(getShell());
				dialog.setFilterExtensions(new String[]{"*.txt"}); //$NON-NLS-1$
				dialog.setFilterNames(new String[]{"Text files (.txt)"});
				
				String file = dialog.open();
				fTranscriptFile.setText(file);
				
				if(!fileDoesNotExist())
				{
					fillOutForm();
				}
				
				commonListenerChecks();
			}
		};
	}
	
	/**
	 * 
	 */
	private void fillOutForm()
	{
		int begin = fTranscriptFile.getText().lastIndexOf(File.separatorChar) + 1;
		int end = fTranscriptFile.getText().lastIndexOf('.');
		String name = fTranscriptFile.getText().substring(begin, end);
		
		fName.setText(name);
		fAudioFile.setText(findAudioFile(name));
	}

	@Override
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
				commonListenerChecks();
			}
		};
	}
	
	@Override
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
	 * @return
	 */
	protected boolean fileDoesNotExist()
	{
		File file = new File(fTranscriptFile.getText());
		return !file.exists();
	}

	/**
	 * 
	 * @return
	 */
	public String getTranscriptFile()
	{		
		return fTranscriptFile.getText();
	}

	/**
	 * 
	 */
	private void commonListenerChecks()
	{
		if(fTranscriptFile.getText().isEmpty() || fileDoesNotExist())
		{
			setError("You must choose a transcript file to import");
		}
		else if(fName.getText().isEmpty())
		{
			setError("Please enter a name");
		}
		else if(transcriptExists())
		{
			setError("That name is already in use");
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

}
