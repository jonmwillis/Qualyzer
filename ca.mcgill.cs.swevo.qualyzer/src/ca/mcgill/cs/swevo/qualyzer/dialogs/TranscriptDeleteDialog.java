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

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 *
 */
public class TranscriptDeleteDialog extends TitleAreaDialog
{
	private boolean deleteAudio;
	private boolean deleteParticipants;
	private boolean deleteCodes;
	
	private Button audioButton;
	private Button participantButton;
	private Button codeButton;
	
	/**
	 * Constructor.
	 * @param shell
	 */
	public TranscriptDeleteDialog(Shell shell)
	{
		super(shell);
		deleteAudio = false;
		deleteParticipants = false;
		deleteCodes = false;
	}
	
	@Override
	public void create()
	{
		super.create();
		setTitle("Delete Transcript");
		setMessage("Deleting this transcript will also remove it from disk.", IMessageProvider.WARNING);
	}
	
	@Override
	public Control createDialogArea(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NULL);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		GridData gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		gd.horizontalSpan = 2;
		composite.setLayout(layout);
		parent.setLayout(layout);
		composite.setLayoutData(gd);
		
		Label label = new Label(composite, SWT.NULL);
		label.setText("Are you sure you want to delete this transcript?");
		
		label.setLayoutData(gd);
		
		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		audioButton = new Button(composite, SWT.CHECK);
		label = new Label(composite, SWT.NULL);
		label.setText("Delete associated audio file");
		label.setLayoutData(gd);
		
		codeButton = new Button(composite, SWT.CHECK);
		label = new Label(composite, SWT.NULL);
		label.setText("Delete codes only used with this transcript");
		label.setLayoutData(gd);
		
		participantButton = new Button(composite, SWT.CHECK);
		label = new Label(composite, SWT.NULL);
		label.setText("Delete participants only associated with this transcript");
		label.setLayoutData(gd);
		
		return parent;
	}
	
	@Override
	public void okPressed()
	{
		deleteAudio = audioButton.getSelection();
		deleteParticipants = participantButton.getSelection();
		deleteCodes = codeButton.getSelection();
		
		super.okPressed();
	}
	
	/**
	 * Get if the delete audio file button is selected.
	 * @return
	 */
	public boolean getDeleteAudio()
	{
		return deleteAudio;
	}
	
	/**
	 * Get if the delete participants button was selected.
	 * @return
	 */
	public boolean getDeleteParticipants()
	{
		return deleteParticipants;
	}
	
	/**
	 * Get if the delete codes button was selected.
	 * @return
	 */
	public boolean getDeleteCodes()
	{
		return deleteCodes;
	}
	
	
}
