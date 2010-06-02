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

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class AddInvestigatorPage extends NewProjectPageTwo
{
	private Project fProject;
	
	/**
	 * Constructor.
	 * @param project
	 */
	public AddInvestigatorPage(Project project)
	{
		setTitle("Add Investigator");
		setDescription("Enter the information for a new Investigator");
		fProject = project;
	}
	
	@Override
	public void createControl(Composite parent)
	{
		super.createControl(parent);
		boolean found = false;
		for(Investigator inves : fProject.getInvestigators())
		{
			if(inves.getNickName().equals(getInvestigatorNickname()))
			{
				setErrorMessage("That nickname is already in use.");
				found = true;
				break;
			}
		}
		setPageComplete(!found);
	}

	@Override
	protected KeyListener createKeyListener()
	{
		return new KeyListener(){
			@Override
			public void keyPressed(KeyEvent e){}

			@Override
			public void keyReleased(KeyEvent e)
			{
				if(getInvestigatorNickname().length() <= 0)
				{
					setPageComplete(false);
					setErrorMessage("Please enter a nickname for the Investigator");
				}
				else
				{
					if(!idInUse())
					{
						setPageComplete(true);
						setErrorMessage(null);
					}
				}
			}
		};
	}
	
	private boolean idInUse()
	{
		for(Investigator inves : fProject.getInvestigators())
		{
			if(inves.getNickName().equals(getInvestigatorNickname()))
			{
				setErrorMessage("That nickname is already in use.");
				setPageComplete(false);
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
		investigator.setFullName(getInvestigatorFullname());
		investigator.setNickName(getInvestigatorNickname());
		investigator.setInstitution(getInstitution());
		return investigator;
	}

}
