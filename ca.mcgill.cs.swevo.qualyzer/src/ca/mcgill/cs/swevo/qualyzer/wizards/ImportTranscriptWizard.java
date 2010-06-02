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
import java.io.FileReader;
import java.io.FileWriter;
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
	
	public ImportTranscriptWizard(Project project)
	{
		fPage = new ImportTranscriptPage(project);
		fProject = project;
	}
	
	public void addPages()
	{
		addPage(fPage);
	}
	
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
		fTranscript = fPage.getTranscript();
		fProject.getTranscripts().add(fTranscript);
		fTranscript.setProject(fProject);

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject wProject = root.getProject(fProject.getName());
		String path = wProject.getLocation()+File.separator+"transcripts"+File.separator+fTranscript.getFileName();
		File file = new File(path);
		File fileOrig = new File(fPage.getTranscriptFile());
		
		copyFile(fileOrig, file);

		HibernateDBManager manager = QualyzerActivator.getDefault().getHibernateDBManagers().get(fProject.getName());
		HibernateUtil.quietSave(manager, fProject);

		return true;
	}

	/**
	 * @param fileOrig
	 * @param file
	 */
	private void copyFile(File fileOrig, File file)
	{
		try
		{
			FileReader in = new FileReader(fileOrig);
			FileWriter out = new FileWriter(file);
			
			int c;
			while((c = in.read()) != -1)
			{
				out.write(c);
			}
			out.close();
			in.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
	}

}