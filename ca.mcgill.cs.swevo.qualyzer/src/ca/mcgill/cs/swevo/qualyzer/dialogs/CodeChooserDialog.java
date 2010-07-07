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

import java.util.ArrayList;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.validation.CodeValidator;

/**
 *
 */
public class CodeChooserDialog extends TitleAreaDialog
{

	private Project fProject;
	private Text fCodeName;
	private Text fDescription;
	
	private String fName;
	private String fDesc;
	private Code fCode;
	
	/**
	 * Constructor.
	 * @param shell
	 */
	public CodeChooserDialog(Shell shell, Project project)
	{
		super(shell);
		fProject = project;
		fCode = null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	@Override
	public void create()
	{
		super.create();
		setTitle("Add Code");
		setMessage("Enter the name of the code you want to associate with the selected text");
		
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}
	
	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.dialogs.NewCodeDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		GridLayout layout = new GridLayout(2, false);
		parent.setLayout(layout);
		
		Label label = new Label(parent, SWT.NULL);
		label.setText("Code");
		
		fCodeName = new Text(parent, SWT.BORDER);
		fCodeName.setText("");
		fCodeName.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		
		label = new Label(parent, SWT.NULL);
		label.setText("Description");
		
		fDescription = new Text(parent, SWT.MULTI | SWT.BORDER);
		fDescription.setText("");
		fDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		String[] proposals = buildProposals();
		
		AutoCompleteField field = new AutoCompleteField(fCodeName, new TextContentAdapter(), proposals);
		
		fCodeName.addModifyListener(createModifyListener());
		
		return parent;
	}

	/**
	 * @return
	 */
	private ModifyListener createModifyListener()
	{
		return new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent e)
			{
				CodeValidator validator = new CodeValidator(fCodeName.getText(), fProject);
				if(!validator.isValid())
				{
					if(validator.getErrorMessage().equals("This code name is already taken"))
					{
						setErrorMessage(null);
						setMessage("Enter the name of the code you want to associate with the selected text");
						for(Code code : fProject.getCodes())
						{
							if(code.getCodeName().equals(fCodeName.getText()))
							{
								fDescription.setText(code.getDescription());
							}
						}
						getButton(IDialogConstants.OK_ID).setEnabled(true);
					}
					else
					{
						setErrorMessage(validator.getErrorMessage());
						getButton(IDialogConstants.OK_ID).setEnabled(false);
					}
				}
				else
				{
					setErrorMessage(null);
					setMessage("This code does not exist and will be created.", IMessageProvider.WARNING);
					getButton(IDialogConstants.OK_ID).setEnabled(true);
				}
				
			}
			
		};
	}


	/**
	 * @return
	 */
	private String[] buildProposals()
	{
		ArrayList<String> proposals = new ArrayList<String>();
		for(Code code : fProject.getCodes())
		{
			proposals.add(code.getCodeName());
		}
		
		return proposals.toArray(new String[0]);
	}
	
	/**
	 * Get the code choosen by this dialog.
	 * @return
	 */
	public Code getCode()
	{
		return fCode;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed()
	{
		fName = fCodeName.getText();
		fDesc = fDescription.getText();
		
		for(Code code : fProject.getCodes())
		{
			if(code.getCodeName().equals(fName))
			{
				fCode = code;
				break;
			}
		}
		
		if(fCode == null)
		{
			fCode = Facade.getInstance().createCode(fName, fDesc, fProject);
		}
		
		super.okPressed();
	}
}
