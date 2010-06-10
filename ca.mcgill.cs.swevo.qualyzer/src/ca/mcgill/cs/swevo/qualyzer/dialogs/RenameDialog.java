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
package ca.mcgill.cs.swevo.qualyzer.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class RenameDialog extends TitleAreaDialog
{
	private Text fNewName;
	private Button fChangeAudio;
	private String fName;
	private boolean fChange;
	private Project fProject;
	private String fOldName;
	
	/**
	 * Constructor.
	 * @param shell
	 */
	public RenameDialog(Shell shell, Project project)
	{
		super(shell);
		fChange = true;
		fName = ""; //$NON-NLS-1$
		fProject = project;
	}
	
	@Override
	public void create()
	{
		super.create();
		setTitle("Renaming Transcript");
		setMessage("Rename the transcript");
	}
	
	@Override
	public Control createDialogArea(Composite parent)
	{
		GridLayout layout = new GridLayout();
		parent.setLayout(layout);
				
		Composite composite = new Composite(parent, SWT.NULL);
		layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		
		Label label = new Label(composite, SWT.NULL);
		label.setText("New Name:");
		
		fNewName = new Text(composite, SWT.BORDER);
		fNewName.setText(""); //$NON-NLS-1$
		fNewName.addKeyListener(createKeyListener());
		fNewName.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		
		composite = new Composite(parent, SWT.NULL);
		layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		
		fChangeAudio = new Button(composite, SWT.CHECK);
		fChangeAudio.addSelectionListener(createSelectionListener());
		fChangeAudio.setSelection(true);
		
		label = new Label(composite, SWT.NULL);
		label.setText("Rename the audio file as well");
		label.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		
		return parent;
	}

	/**
	 * @return
	 */
	private SelectionListener createSelectionListener()
	{
		return new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e){}

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				fChange = !fChange;
			}
			
		};
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
				if(transcriptExists())
				{
					setErrorMessage("This name is already in use");
					getButton(IDialogConstants.OK_ID).setEnabled(false);
				}
				else
				{
					setErrorMessage(null);
					fName = fNewName.getText();
					getButton(IDialogConstants.OK_ID).setEnabled(true);
				}
			}
			
		};
	}

	/**
	 * @return
	 */
	private boolean transcriptExists()
	{
		for(Transcript transcript : fProject.getTranscripts())
		{
			if(!fOldName.equals(fNewName.getText()) && transcript.getName().equals(fNewName.getText()))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Get the new name.
	 * @return
	 */
	public String getName()
	{
		return fName;
	}
	
	/**
	 * See if the audio file should be renamed.
	 * @return
	 */
	public boolean getChangeAudio()
	{
		return fChange;
	}

	/**
	 * @param name
	 */
	public void setCurrentName(String name)
	{
		fOldName = name;
	}
	
}
