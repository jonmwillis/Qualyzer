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

import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.validation.CodeValidator;

/**
 * Dialog for adding a new code.
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
	
	@Override
	public void create()
	{
		super.create();
		setTitle(Messages.getString("dialogs.NewCodeDialog.newCode")); //$NON-NLS-1$
		setMessage(Messages.getString("dialogs.NewCodeDialog.enterName")); //$NON-NLS-1$
		
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}
	
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label label = new Label(composite, SWT.NULL);
		label.setText(Messages.getString("dialogs.NewCodeDialog.name")); //$NON-NLS-1$
		fNameText = new Text(composite, SWT.BORDER);
		fNameText.setText(""); //$NON-NLS-1$
		fNameText.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		fNameText.addKeyListener(createKeyAdapter());
		
		label = new Label(composite, SWT.NULL);
		label.setText(Messages.getString("dialogs.NewCodeDialog.description")); //$NON-NLS-1$
		fDescriptionText = new Text(composite, SWT.BORDER);
		fDescriptionText.setText(""); //$NON-NLS-1$
		fDescriptionText.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
	
		return parent;
	}

	/**
	 * @return
	 */
	private KeyAdapter createKeyAdapter()
	{
		return new KeyAdapter(){
			
			@Override
			public void keyReleased(KeyEvent e)
			{
				CodeValidator lValidator = new CodeValidator(fNameText.getText(), fProject);
				
				if(!lValidator.isValid())
				{
					getButton(IDialogConstants.OK_ID).setEnabled(false);
					setErrorMessage(lValidator.getErrorMessage());
				}
				else
				{
					getButton(IDialogConstants.OK_ID).setEnabled(true);
					setErrorMessage(null);
				}
			}
		};
	}
	
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
