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
import org.eclipse.swt.widgets.Text;

import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class AddInvestigatorPage extends NewProjectPageTwo
{
	private Project fProject;
	
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
		for(Investigator inves : fProject.getInvestigators())
		{
			if(inves.getNickName().equals(getInvestigatorNickname()))
			{
				setErrorMessage("That nickname is already in use.");
				break;
			}
		}
		setPageComplete(false);
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
					setErrorMessage(null);
				}
				else
				{
					boolean inUse = false;
					for(Investigator inves : fProject.getInvestigators())
					{
						if(inves.getNickName().equals(getInvestigatorNickname()))
						{
							setErrorMessage("That nickname is already in use.");
							setPageComplete(false);
							inUse = true;
							break;
						}
					}
					if(!inUse)
					{
						setPageComplete(true);
						setErrorMessage(null);
					}
				}
			}
		};
	}
	
	public Investigator getInvestigator()
	{
		Investigator investigator = new Investigator();
		investigator.setFullName(getInvestigatorFullname());
		investigator.setNickName(getInvestigatorNickname());
		investigator.setInstitution(getInstitution());
		return investigator;
	}

}
