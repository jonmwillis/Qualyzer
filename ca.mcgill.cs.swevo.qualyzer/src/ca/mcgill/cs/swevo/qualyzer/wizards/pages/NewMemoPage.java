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
package ca.mcgill.cs.swevo.qualyzer.wizards.pages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.validation.MemoValidator;

/**
 *
 */
public class NewMemoPage extends WizardPage
{
	/**
	 * 
	 */
	private static final String SLASH = "/";  //$NON-NLS-1$
	private Project fProject;
	private List<Participant> fParticipants;
	private Investigator fAuthor;
	private Composite fContainer;
	private Text fName;
	private Combo fAuthorName;
	private Table fTable;
	private DateTime fDate;

	/**
	 * Constructor.
	 * @param project
	 */
	public NewMemoPage(Project project)
	{
		super(Messages.getString("wizards.pages.NewMemoPage.newMemo")); //$NON-NLS-1$
		fProject = project;
		setTitle(Messages.getString("wizards.pages.NewMemoPage.newMemoWizard")); //$NON-NLS-1$
		setDescription(Messages.getString("wizards.pages.NewMemoPage.enterInfo")); //$NON-NLS-1$
		
		fParticipants = new ArrayList<Participant>();
	}
	
	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.wizards.pages.TranscriptWizardPage#createControl(
	 * org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent)
	{	
		fContainer = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		fContainer.setLayout(layout);
		
		createLabel(fContainer, Messages.getString("wizards.pages.NewMemoPage.memoName"));  //$NON-NLS-1$
		fName = new Text(fContainer, SWT.BORDER);
		fName.setText(""); //$NON-NLS-1$
		fName.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		fName.addModifyListener(createModifyListener());
		
		createLabel(fContainer, Messages.getString("wizards.pages.NewMemoPage.date")); //$NON-NLS-1$
		fDate = new DateTime(fContainer, SWT.DATE);
		
		createLabel(fContainer, Messages.getString("wizards.pages.NewMemoPage.author")); //$NON-NLS-1$
		fAuthorName = new Combo(fContainer, SWT.DROP_DOWN | SWT.READ_ONLY);
		fAuthorName.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		fAuthorName.addModifyListener(createModifyListener());
		
		for(Investigator investigator : fProject.getInvestigators())
		{
			fAuthorName.add(investigator.getNickName());
		}
		
		createLongLabel();
		createTable();
		
		setControl(fContainer);
		setPageComplete(false);
	}

	/**
	 * @return
	 */
	private ModifyListener createModifyListener()
	{
		return new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				MemoValidator validator = new MemoValidator(fName.getText(), getAuthor(), fProject);
				
				if(validator.isValid())
				{
					setErrorMessage(null);
					setPageComplete(true);
				}
				else
				{
					setErrorMessage(validator.getErrorMessage());
					setPageComplete(false);
				}
			}
		};
	}
	
	/**
	 * 
	 */
	private void createTable()
	{
		GridData gd;
		fTable = new Table(fContainer, SWT.MULTI | SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.horizontalSpan = 2;
		fTable.setLayoutData(gd);
		populateTable();		
	}

	/**
	 * 
	 */
	private void populateTable()
	{
		for(Participant participant : fProject.getParticipants())
		{
			TableItem item = new TableItem(fTable, SWT.NULL);
			item.setText(participant.getParticipantId());
		}
	}

	/**
	 * 
	 */
	private void createLongLabel()
	{
		GridData gd;
		Label label = new Label(fContainer, SWT.NULL);
		label.setText(Messages.getString("wizards.pages.NewMemoPage.participants")); //$NON-NLS-1$
		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		gd.horizontalSpan = 2;
		label.setLayoutData(gd);
		
	}

	/**
	 * @param container
	 * @param string
	 */
	private void createLabel(Composite container, String string)
	{
		Label label = new Label(container, SWT.NULL);
		label.setText(string);
	}

	/**
	 * Get the name of the Memo.
	 * @return
	 */
	public String getMemoName()
	{
		return fName.getText();
	}
	
	/**
	 * Get the participants that are part of this memo.
	 * @return
	 */
	public List<Participant> getParticipants()
	{
		buildParticipants();
		return fParticipants;
	}
	
	private void buildParticipants()
	{
		fParticipants = new ArrayList<Participant>();
		
		TableItem[] items = fTable.getSelection();
		
		for(Participant participant : fProject.getParticipants())
		{
			for(TableItem item : items)
			{
				if(participant.getParticipantId().equals(item.getText()))
				{
					fParticipants.add(participant);
				}
			}
		}
	}
	
	/**
	 * Get the investigator that authored this memo.
	 * @return
	 */
	public Investigator getAuthor()
	{
		if(fAuthorName.getText().isEmpty())
		{
			fAuthor = null;
		}
		else
		{
			for(Investigator investigator : fProject.getInvestigators())
			{
				if(investigator.getNickName().equals(fAuthorName.getText()))
				{
					fAuthor = investigator;
				}
			}
		}
		
		return fAuthor;
	}
	
	/**
	 * Get the date the was assigned to the memo.
	 * In the format MM/DD/YYYY.
	 * @return
	 */
	public String getDate()
	{
		return (fDate.getMonth() + 1) + SLASH + fDate.getDay() + SLASH + fDate.getYear();
	}
}
