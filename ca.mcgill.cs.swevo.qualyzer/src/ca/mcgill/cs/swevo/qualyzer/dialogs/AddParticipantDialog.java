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

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class AddParticipantDialog extends TitleAreaDialog
{
	private String fParticipantId;
	private String fFullname;
	private String fContactInfo;
	private String  fDescription;
	
	private Text fIdText;
	private Text fFullNameText;
	private Text fContactInfoText;
	private Text fDescriptionText;

	/**
	 * @param parentShell
	 */
	public AddParticipantDialog(Shell parentShell)
	{
		super(parentShell);
	}
	
	@Override
	public void create()
	{
		super.create();
		setTitle("Add a participant");
		setMessage("Please enter the Participant's information");
	}
	
	@Override
	public Control createDialogArea(Composite parent)
	{
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		parent.setLayout(layout);
		
		Label label = new Label(parent, SWT.NULL);
		label.setText("Participant ID");
		fIdText = new Text(parent, SWT.BORDER | SWT.SINGLE);
		fIdText.setText("");
		
		label = new Label(parent, SWT.NULL);
		label.setText("Full Name");
		fFullNameText = new Text(parent, SWT.BORDER | SWT.SINGLE);
		fFullNameText.setText("");
		
		label = new Label(parent, SWT.NULL);
		label.setText("Contact Info");
		fContactInfoText = new Text(parent, SWT.BORDER | SWT.SINGLE);
		fContactInfoText.setText("");
		
		label = new Label(parent, SWT.NULL);
		label.setText("Description");
		fDescriptionText = new Text(parent, SWT.BORDER | SWT.SINGLE);
		fDescriptionText.setText("");
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		fIdText.setLayoutData(gd);
		fFullNameText.setLayoutData(gd);
		fContactInfoText.setLayoutData(gd);
		fDescriptionText.setLayoutData(gd);

		return parent;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) 
	{
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = SWT.CENTER;

		parent.setLayoutData(gridData);
		// Create Add button
		// Own method as we need to overview the SelectionAdapter
		createOkButton(parent, OK, "Add", true);
		// Add a SelectionListener

		// Create Cancel button
		Button cancelButton = createButton(parent, CANCEL, "Cancel", false);
		// Add a SelectionListener
		cancelButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e)
			{
				setReturnCode(CANCEL);
				close();
			}
		});
	}
	
	protected Button createOkButton(Composite parent, int id, String label,	boolean defaultButton) 
	{
		// increment the number of columns in the button bar
		((GridLayout) parent.getLayout()).numColumns++;
		Button button = new Button(parent, SWT.PUSH);
		button.setText(label);
		button.setFont(JFaceResources.getDialogFont());
		button.setData(new Integer(id));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event)
			{
				if (isValidInput())
				{
					okPressed();
				}
			}
		});
		if (defaultButton)
		{
			Shell shell = parent.getShell();
			if (shell != null) 
			{
				shell.setDefaultButton(button);
			}
		}
		setButtonLayoutData(button);
		return button;
	}

	private void saveInput()
	{
		fParticipantId = fIdText.getText();
		fFullname = fFullNameText.getText();
		fContactInfo = fContactInfoText.getText();
		fDescription = fDescriptionText.getText();
	}

	private boolean isValidInput()
	{
		if(fIdText.getText().length() <= 0)
		{
			setErrorMessage("Please enter a Participant ID");
			return false;
		}
		
		//TODO verify that the id is not in use
		
		return true;
	}
	
	public String getParticipantId()
	{
		return fParticipantId;
	}

	public String getFullname()
	{
		return fFullname;
	}

	public String getContactInfo()
	{
		return fContactInfo;
	}

	public String getDescription()
	{
		return fDescription;
	}
	
	@Override
	protected void okPressed()
	{
		saveInput();
		super.okPressed();
	}

}
