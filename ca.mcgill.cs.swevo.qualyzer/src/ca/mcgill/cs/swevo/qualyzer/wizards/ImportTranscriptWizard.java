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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;

import ca.mcgill.cs.swevo.qualyzer.QualyzerException;
import ca.mcgill.cs.swevo.qualyzer.model.ModelFacade;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.wizards.pages.ImportTranscriptPage;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class ImportTranscriptWizard extends Wizard
{

	private ImportTranscriptPage fPage;
	private Transcript fTranscript;
	private Project fProject;
	
	/**
	 * Constructor.
	 * @param project
	 */
	public ImportTranscriptWizard(Project project)
	{
		fPage = new ImportTranscriptPage(project);
		fProject = project;
	}
	
	@Override
	public void addPages()
	{
		addPage(fPage);
	}
	
	/**
	 * Get the Transcript that was made by the wizard.
	 * @return
	 */
	public Transcript getTranscript()
	{
		return fTranscript;
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
				fPage.getAudioFile(), fPage.getTranscriptFile(), fPage.getParticipants(), fProject);
		}
		catch(QualyzerException e)
		{
			MessageDialog.openError(getShell(), "Transcript Error", e.getMessage());
			return false;
		}

		return true;
	}

}
