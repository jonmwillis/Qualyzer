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
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
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
	private Combo fCodeName;
	private Text fDescription;

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
		setTitle(Messages.getString("dialogs.CodeChooserDialog.add")); //$NON-NLS-1$
		setMessage(Messages.getString("dialogs.CodeChooserDialog.enterName")); //$NON-NLS-1$
		
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
		label.setText(Messages.getString("dialogs.CodeChooserDialog.code")); //$NON-NLS-1$
		
		fCodeName = new Combo(parent, SWT.BORDER | SWT.DROP_DOWN);
		fCodeName.setText(""); //$NON-NLS-1$
		fCodeName.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		
		for(Code code : fProject.getCodes())
		{
			fCodeName.add(code.getCodeName());
		}
		
		label = new Label(parent, SWT.NULL);
		label.setText(Messages.getString("dialogs.CodeChooserDialog.description")); //$NON-NLS-1$
		
		fDescription = new Text(parent, SWT.MULTI | SWT.BORDER);
		fDescription.setText(""); //$NON-NLS-1$
		fDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		String[] proposals = buildProposals();
		
		@SuppressWarnings("unused")
		AutoCompleteField field = new AutoCompleteField(fCodeName, new ComboContentAdapter(), proposals);
		
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
					if(validator.getErrorMessage().equals(Messages.getString(
							"dialogs.CodeChooserDialog.nameTaken"))) //$NON-NLS-1$
					{
						setErrorMessage(null);
						setMessage(Messages.getString("dialogs.CodeChooserDialog.enterName")); //$NON-NLS-1$
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
					setMessage(Messages.getString(
							"dialogs.CodeChooserDialog.doesNotExist"), IMessageProvider.WARNING); //$NON-NLS-1$
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
	public void okPressed()
	{	
		for(Code code : fProject.getCodes())
		{
			if(code.getCodeName().equals(fCodeName.getText()))
			{
				fCode = code;
				break;
			}
		}
		
		if(fCode == null)
		{
			fCode = Facade.getInstance().createCode(fCodeName.getText(), fDescription.getText(), fProject);
			CommonNavigator view = (CommonNavigator) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
			.getActivePage().findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
			view.getCommonViewer().refresh();
		}
		
		super.okPressed();
	}
	
	/**
	 *
	 * @return
	 */
	public Combo getCodeName()
	{
		return fCodeName;
	}
}
