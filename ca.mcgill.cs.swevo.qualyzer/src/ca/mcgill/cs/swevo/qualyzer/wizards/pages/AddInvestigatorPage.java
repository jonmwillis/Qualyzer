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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.util.ResourcesUtil;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class AddInvestigatorPage extends WizardPage
{
	private Project fProject;
	private Composite fContainer;
	private Text fNickname;
	private Text fFullname;
	private Text fInstitution;
	
	/**
	 * Constructor.
	 * @param project
	 */
	public AddInvestigatorPage(Project project)
	{
		super(Messages.getString("wizards.pages.AddInvestigatorPage.addInvestigator")); //$NON-NLS-1$
		setTitle(Messages.getString("wizards.pages.AddInvestigatorPage.addInvestigator")); //$NON-NLS-1$
		setDescription(Messages.getString("wizards.pages.AddInvestigatorPage.enterInfo")); //$NON-NLS-1$
		fProject = project;
	}
	
	@Override
	public void createControl(Composite parent)
	{
		fContainer = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		fContainer.setLayout(layout);
		layout.numColumns = 2;
		Label label = new Label(fContainer, SWT.NULL);
		label.setText(Messages.getString("wizards.pages.AddInvestigatorPage.nickname")); //$NON-NLS-1$

		fNickname = new Text(fContainer, SWT.BORDER | SWT.SINGLE);
		fNickname.setText(System.getProperty("user.name")); //$NON-NLS-1$
		if(idInUse())
		{
			fNickname.setText(""); //$NON-NLS-1$
		}
		
		//Only allows the user to proceed if a valid name is entered
		fNickname.addKeyListener(createKeyListener());
		
		label = new Label(fContainer, SWT.NULL);
		label.setText(Messages.getString("wizards.pages.AddInvestigatorPage.fullName")); //$NON-NLS-1$
		
		fFullname = new Text(fContainer, SWT.BORDER | SWT.SINGLE);
		fFullname.setText(""); //$NON-NLS-1$
		
		label = new Label(fContainer, SWT.NULL);
		label.setText(Messages.getString("wizards.pages.AddInvestigatorPage.insitution")); //$NON-NLS-1$
		
		fInstitution = new Text(fContainer, SWT.BORDER | SWT.SINGLE);
		fInstitution.setText(""); //$NON-NLS-1$
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		fNickname.setLayoutData(gd);
		fFullname.setLayoutData(gd);
		fInstitution.setLayoutData(gd);
		
		// Required to avoid an error in the system
		setControl(fContainer);
		setPageComplete(!fNickname.getText().isEmpty());
	}

	/**
	 * Get the Nickname field.
	 * @return
	 */
	public String getInvestigatorNickname()
	{
		return fNickname.getText();
	}
	
	/**
	 * Get the fullname field.
	 * @return
	 */
	public String getInvestigatorFullname()
	{
		return fFullname.getText();
	}
	
	/**
	 * Get the Institution field.
	 * @return
	 */
	public String getInstitution()
	{
		return fInstitution.getText();
	}
	
	private KeyListener createKeyListener()
	{
		return new KeyListener(){
			@Override
			public void keyPressed(KeyEvent e){}

			@Override
			public void keyReleased(KeyEvent e)
			{
				if(fNickname.getText().isEmpty())
				{
					setPageComplete(false);
					setErrorMessage(Messages.getString(
							"wizards.pages.AddInvestigatorPage.enterNickname")); //$NON-NLS-1$
				}
				else if(!ResourcesUtil.verifyID(fNickname.getText()))
				{
					setPageComplete(false);
					setErrorMessage(Messages.getString("wizards.pages.AddInvestigatorPage.invalidName")); //$NON-NLS-1$
				}
				else if(!idInUse())
				{
					setPageComplete(true);
					setErrorMessage(null);
				}
				else
				{
					setErrorMessage(Messages.getString(
							"wizards.pages.AddInvestigatorPage.nicknameTaken")); //$NON-NLS-1$
					setPageComplete(false);
				}
			}
		};
	}
	
	private boolean idInUse()
	{
		for(Investigator inves : fProject.getInvestigators())
		{
			if(inves.getNickName().equals(fNickname.getText()))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Build the investigator represented by the information entered in the wizard.
	 * @return The resulting investigator.
	 */
	public Investigator getInvestigator()
	{
		Investigator investigator = new Investigator();
		investigator.setFullName(fFullname.getText());
		investigator.setNickName(fNickname.getText());
		investigator.setInstitution(fInstitution.getText());
		return investigator;
	}

}
