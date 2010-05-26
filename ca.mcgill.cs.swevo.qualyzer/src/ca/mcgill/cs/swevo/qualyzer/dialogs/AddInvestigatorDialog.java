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
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class AddInvestigatorDialog extends TitleAreaDialog
{
	private String fNickname;
	private String fFullname;
	private String fInstitution;
	
	private Text fNicknameText;
	private Text fFullnameText;
	private Text fInstitutionText;

	/**
	 * @param parentShell
	 */
	public AddInvestigatorDialog(Shell parentShell)
	{
		super(parentShell);
	}
	
	@Override
	public void create()
	{
		super.create();
		setTitle("Add an Investigator");
		setMessage("Please enter the following information to add an investigator");
	}
	
	@Override
	public Control createDialogArea(Composite parent)
	{
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		parent.setLayout(layout);
		
		Label label = new Label(parent, SWT.NULL);
		label.setText("Nickname:");
		fNicknameText = new Text(parent, SWT.BORDER | SWT.SINGLE);
		fNicknameText.setText("");
		
		label = new Label(parent, SWT.NULL);
		label.setText("Fullname:");
		fFullnameText = new Text(parent, SWT.BORDER | SWT.SINGLE);
		fFullnameText.setText("");
		
		label = new Label(parent, SWT.NULL);
		label.setText("Institution:");
		fInstitutionText = new Text(parent, SWT.BORDER | SWT.SINGLE);
		fInstitutionText.setText("");
		
		GridData gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		fNicknameText.setLayoutData(gd);
		fFullnameText.setLayoutData(gd);
		fInstitutionText.setLayoutData(gd);
		return parent;
	}
	
	@Override
	public void okPressed()
	{
		fNickname = fNicknameText.getText();
		fFullname = fFullnameText.getText();
		fInstitution = fInstitutionText.getText();
		super.okPressed();
	}
	
	public String getNickname()
	{
		return fNickname;
	}
	
	public String getFullname()
	{
		return fFullname;
	}
	
	public String getInstitution()
	{
		return fInstitution;
	}

}
