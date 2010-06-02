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

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.wizard.Wizard;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.model.HibernateDBManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.util.HibernateUtil;
import ca.mcgill.cs.swevo.qualyzer.wizards.pages.TranscriptWizardPage;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class NewTranscriptWizard extends Wizard
{
	private TranscriptWizardPage fPage;
	private Project fProject;
	private Transcript fTranscript;
	
	public NewTranscriptWizard(Project project)
	{
		fProject = project;
	}
	
	
	public void addPages()
	{
		fPage = new TranscriptWizardPage(fProject);
		addPage(fPage);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish()
	{
		fTranscript = fPage.getTranscript();
		fProject.getTranscripts().add(fTranscript);
		fTranscript.setProject(fProject);

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject wProject = root.getProject(fProject.getName());
		String path = wProject.getLocation()+File.separator+"transcripts"+File.separator+fTranscript.getFileName();
		File file = new File(path);
		
		try
		{
			if(!file.createNewFile())
			{
				fPage.setErrorMessage("That transcript file already exists");
				return false;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(fProject.getName());
		HibernateUtil.quietSave(manager, fProject);

		return true;
	}


	/**
	 * @return
	 */
	public Transcript getTranscript()
	{
		return fTranscript;
	}

}
