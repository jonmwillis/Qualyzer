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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
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
import ca.mcgill.cs.swevo.qualyzer.model.validation.ImportTranscriptValidator;

/**
 * @author Jonathan Faubert
 *
 */
public class ImportMemoPage extends NewMemoPage
{

	private static final int COLS = 3;
	private Text fMemoFile;

	/**
	 * @param project
	 */
	public ImportMemoPage(Project project)
	{
		super(project);
		setTitle(Messages.getString("wizards.pages.ImportMemoPage.importWizard")); //$NON-NLS-1$
		setDescription(Messages.getString("wizards.pages.ImportMemoPage.chooseFile")); //$NON-NLS-1$
	}
	
	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.wizards.pages.NewMemoPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent)
	{
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
		label.setText("Filename:"); //$NON-NLS-1$
		fMemoFile = new Text(composite, SWT.BORDER);
		fMemoFile.setText(""); //$NON-NLS-1$
		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		fMemoFile.setLayoutData(gd);
		fMemoFile.addModifyListener(createModifyTextListener());
		
		Button button = new Button(composite, SWT.PUSH);
		button.addSelectionListener(createNewSelectionListener());
		button.setText("Browse"); //$NON-NLS-1$
		
		setfContainer(container);
		super.createControl(parent);
	}

	/**
	 * @return
	 */
	private ModifyListener createModifyTextListener()
	{
		return new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				validate();
			}
		};
	}
	
	private SelectionListener createNewSelectionListener()
	{
		return new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				FileDialog dialog = new FileDialog(getShell());
				dialog.setFilterExtensions(new String[]{"*.rtf"}); //$NON-NLS-1$
				dialog.setFilterNames(new String[]{"Rich Text Files (.rtf)"}); //$NON-NLS-1$
				
				String file = dialog.open();
				fMemoFile.setText(file);
				
				if(!fileDoesNotExist())
				{
					fillOutForm();
				}
				
				validate();
			}
		};
	}
	
	private void fillOutForm()
	{
		int begin = fMemoFile.getText().lastIndexOf(File.separatorChar) + 1;
		int end = fMemoFile.getText().lastIndexOf('.');
		String name = fMemoFile.getText().substring(begin, end);
		
		fName.setText(name);
	}
	
	/**
	 * @return
	 */
	protected boolean fileDoesNotExist()
	{
		File file = new File(fMemoFile.getText());
		return !file.exists();
	}
	
	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.wizards.pages.NewMemoPage#validate()
	 */
	@Override
	protected void validate()
	{
		ImportTranscriptValidator validator = new ImportTranscriptValidator(fMemoFile.getText());
		if(!validator.isValid())
		{
			setErrorMessage(validator.getErrorMessage());
			setPageComplete(false);
		}
		else
		{
			super.validate();
		}
	}
	
	/**
	 * Get the file that was chosen to import.
	 * @return
	 */
	public String getMemoFile()
	{
		return fMemoFile.getText();
	}

}