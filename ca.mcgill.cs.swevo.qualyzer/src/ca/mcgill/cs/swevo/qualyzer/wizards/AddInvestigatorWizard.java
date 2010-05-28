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
package ca.mcgill.cs.swevo.qualyzer.wizards;

import org.eclipse.jface.wizard.Wizard;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.model.HibernateDBManager;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.util.HibernateUtil;
import ca.mcgill.cs.swevo.qualyzer.wizards.pages.AddInvestigatorPage;

/**
 * The wizard which controls the adding of a new Investigator to the project.
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class AddInvestigatorWizard extends Wizard
{

	private AddInvestigatorPage fPage;
	private Project fProject;
	private Investigator fInvestigator;
	
	public AddInvestigatorWizard(Project project)
	{
		fProject = project;
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public void addPages()
	{
		fPage = new AddInvestigatorPage(fProject);
		addPage(fPage);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish()
	{
		fInvestigator = fPage.getInvestigator();
		fProject.getInvestigators().add(fInvestigator);
		fInvestigator.setProject(fProject);
		
		HibernateDBManager manager;
		manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(fProject.getName());
		HibernateUtil.quietSave(manager, fProject);
		return true;
	}
	
	/**
	 * Gets the investigator that was created with the wizard.
	 * @return The Investigator that the user created.
	 */
	public Investigator getInvestigator()
	{
		return fInvestigator;
	}

}