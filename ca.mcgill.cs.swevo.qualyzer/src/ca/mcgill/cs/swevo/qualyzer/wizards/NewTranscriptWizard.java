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

import ca.mcgill.cs.swevo.qualyzer.QualyzerException;
import ca.mcgill.cs.swevo.qualyzer.model.ModelFacade;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
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
	
	/**
	 * Constructor.
	 * @param project
	 */
	public NewTranscriptWizard(Project project)
	{
		fProject = project;
	}
	
	@Override
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
		try
		{
			fTranscript = ModelFacade.getInstance().createTranscript(fPage.getTranscriptName(), fPage.getDate(),
					fPage.getAudioFile(), "", fPage.getParticipants(), fProject);
		}
		catch(QualyzerException e)
		{
			//TODO
		}

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
