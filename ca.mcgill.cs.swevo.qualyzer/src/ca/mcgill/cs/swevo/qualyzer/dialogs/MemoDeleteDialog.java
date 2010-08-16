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
public class MemoDeleteDialog extends TitleAreaDialog
{
	private boolean fCodes;
	private Button fButton;
	/**
	 * Constructor.
	 * @param shell
	 */
	public MemoDeleteDialog(Shell shell)
	{
		super(shell);
		fCodes = false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	@Override
	public void create()
	{
		super.create();
		setTitle(Messages.getString("dialogs.MemoDeleteDialog.deleteMemo")); //$NON-NLS-1$
		setMessage(Messages.getString("dialogs.MemoDeleteDialog.warning"),  //$NON-NLS-1$
				IMessageProvider.WARNING);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		container.setLayout(layout);
		
		Label label = new Label(container, SWT.NULL);
		label.setText(Messages.getString("dialogs.MemoDeleteDialog.confirm")); //$NON-NLS-1$
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		label.setLayoutData(gd);
		
		fButton = new Button(container, SWT.CHECK);
		fButton.setSelection(false);
		fButton.setText(Messages.getString("dialogs.MemoDeleteDialog.deleteCodes")); //$NON-NLS-1$
		
		return parent;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	public void okPressed()
	{
		fCodes = fButton.getSelection();
		super.okPressed();
	}
	
	/**
	 * Should the codes be deleted too?
	 * @return
	 */
	public boolean deleteCodes()
	{
		return fCodes;
	}
	
	/**
	 * 
	 * @return
	 */
	public Button getCheckBox()
	{
		return fButton;
	}
}
