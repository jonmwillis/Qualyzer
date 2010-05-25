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
package ca.mcgill.cs.swevo.qualyzer.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * The Second page of the New Project wizard.
 * @author Jonathan Faubert
 *
 */
public class NewProjectPageTwo extends WizardPage
{

	private Composite fContainer;
	private Text fNickname;
	private Text fFullname;
	private Text fInstitution;
	
	public NewProjectPageTwo()
	{
		super("New Project 2/2");
		setTitle("New Project");
		setDescription("Please enter the Investigator's information");
	}
	
	//CSOFF:
	@Override
	public void createControl(Composite parent)
	{
		fContainer = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		fContainer.setLayout(layout);
		layout.numColumns = 2;
		Label label = new Label(fContainer, SWT.NULL);
		label.setText("Investigator Nickname");

		fNickname = new Text(fContainer, SWT.BORDER | SWT.SINGLE);
		fNickname.setText(System.getProperty("user.name"));
		
		//Only allows the user to proceed if a name is entered
		fNickname.addKeyListener(createKeyListener());
		
		label = new Label(fContainer, SWT.NULL);
		label.setText("Full name");
		
		fFullname = new Text(fContainer, SWT.BORDER | SWT.SINGLE);
		fFullname.setText("");
		
		label = new Label(fContainer, SWT.NULL);
		label.setText("Institution");
		
		fInstitution = new Text(fContainer, SWT.BORDER | SWT.SINGLE);
		fInstitution.setText("");
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		fNickname.setLayoutData(gd);
		fFullname.setLayoutData(gd);
		fInstitution.setLayoutData(gd);
		
		// Required to avoid an error in the system
		setControl(fContainer);
		setPageComplete(true);
	}
	//CSON:
	
	public String getInvestigatorNickname()
	{
		return fNickname.getText();
	}
	
	public String getInvestigatorFullname()
	{
		return fFullname.getText();
	}
	
	public String getInstitution()
	{
		return fInstitution.getText();
	}
	
	public KeyListener createKeyListener()
	{
		return new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e)
			{
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
				if (!fNickname.getText().isEmpty()) 
				{
					setPageComplete(true);

				}
				else
				{
					setPageComplete(false);
				}
			}

		};
	}

}
