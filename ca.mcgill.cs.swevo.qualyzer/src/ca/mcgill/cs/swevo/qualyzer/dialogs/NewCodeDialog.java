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

package ca.mcgill.cs.swevo.qualyzer.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;

/**
 *
 */
public class NewCodeDialog extends TitleAreaDialog
{

	private Project fProject;
	private Text fNameText;
	private Text fDescriptionText;
	private String fName;
	private String fDescription;
	
	
	/**
	 * @param parentShell
	 */
	public NewCodeDialog(Shell parentShell, Project project)
	{
		super(parentShell);
		fProject = project;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	@Override
	public void create()
	{
		super.create();
		setTitle("New Code");
		setMessage("Enter a code name.");
		
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label label = new Label(composite, SWT.NULL);
		label.setText("Name:");
		fNameText = new Text(composite, SWT.BORDER);
		fNameText.setText("");
		fNameText.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		fNameText.addKeyListener(createKeyAdapter());
		
		label = new Label(composite, SWT.NULL);
		label.setText("Description:");
		fDescriptionText = new Text(composite, SWT.BORDER);
		fDescriptionText.setText("");
		fDescriptionText.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
	
		return parent;
	}

	/**
	 * @return
	 */
	private KeyAdapter createKeyAdapter()
	{
		return new KeyAdapter(){
			
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.KeyAdapter#keyReleased(org.eclipse.swt.events.KeyEvent)
			 */
			@Override
			public void keyReleased(KeyEvent e)
			{
				if(fNameText.getText().isEmpty())
				{
					getButton(IDialogConstants.OK_ID).setEnabled(false);
					setErrorMessage(null);
				}
				else if(!ResourcesUtil.verifyID(fNameText.getText()))
				{
					getButton(IDialogConstants.OK_ID).setEnabled(false);
					setErrorMessage("Code name is invalid. Use letters, numbers, '-' and '_'.");
				}
				else if(nameInUse())
				{
					getButton(IDialogConstants.OK_ID).setEnabled(false);
					setErrorMessage("That name has already been taken.");
				}
				else
				{
					getButton(IDialogConstants.OK_ID).setEnabled(true);
					setErrorMessage(null);
				}
			}
		};
	}
	
	/**
	 * @return
	 */
	protected boolean nameInUse()
	{
		for(Code code : fProject.getCodes())
		{
			if(code.getCodeName().equals(fNameText.getText()))
			{
				return true;
			}
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed()
	{
		fName = fNameText.getText();
		fDescription = fDescriptionText.getText();
		super.okPressed();
	}

	/**
	 * Get the code name.
	 * @return
	 */
	public String getName()
	{
		return fName;
	}
	
	/**
	 * Get the code description.
	 * @return
	 */
	public String getDescription()
	{
		return fDescription;
	}
	
}
