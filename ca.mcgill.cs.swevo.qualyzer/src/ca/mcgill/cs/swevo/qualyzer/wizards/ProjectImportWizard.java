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
package ca.mcgill.cs.swevo.qualyzer.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.wizards.datatransfer.DataTransferMessages;
import org.eclipse.ui.internal.wizards.datatransfer.WizardProjectsImportPage;

import ca.mcgill.cs.swevo.qualyzer.QualyzerException;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.util.FileUtil;

/**
 * 
 *
 */
@SuppressWarnings("restriction")
public class ProjectImportWizard extends Wizard implements IImportWizard
{
	private static final String EXTERNAL_PROJECT_SECTION = "ProjectImportWizard"; //$NON-NLS-1$
	private WizardProjectsImportPage fMainPage;
	private IStructuredSelection fCurrentSelection = null;
	private String fInitialPath = null;

	/**
	 * Constructor for TestImportWizard.
	 */
	public ProjectImportWizard()
	{
		this(null);
	}

	/**
	 * Constructor for TestImportWizard.
	 * 
	 * @param initialPath
	 *            Default path for wizard to import
	 * @since 3.5
	 */
	public ProjectImportWizard(String initialPath)
	{
		super();
		this.fInitialPath = initialPath;
		setNeedsProgressMonitor(true);
		IDialogSettings workbenchSettings = IDEWorkbenchPlugin.getDefault().getDialogSettings();

		IDialogSettings wizardSettings = workbenchSettings.getSection(EXTERNAL_PROJECT_SECTION);
		if (wizardSettings == null)
		{
			wizardSettings = workbenchSettings.addNewSection(EXTERNAL_PROJECT_SECTION);
		}
		setDialogSettings(wizardSettings);
	}

	@Override
	public void addPages()
	{
		super.addPages();
		fMainPage = new WizardProjectsImportPage(
				"wizardExternalProjectsPage", fInitialPath, fCurrentSelection); //$NON-NLS-1$
		addPage(fMainPage);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection currentSelection)
	{
		setWindowTitle(DataTransferMessages.DataTransfer_importTitle);
		setDefaultPageImageDescriptor(IDEWorkbenchPlugin.getIDEImageDescriptor(
				"wizban/importproj_wiz.png")); //$NON-NLS-1$
		this.fCurrentSelection = currentSelection;
	}

	@Override
	public boolean performCancel()
	{
		fMainPage.performCancel();
		return true;
	}

	@Override
	public boolean performFinish()
	{
		boolean toReturn =  fMainPage.createProjects();
		for(IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects())
		{
			PersistenceManager.getInstance().refreshManager(project);
			try
			{
				FileUtil.refreshSubFolders(project);
			}
			catch(QualyzerException e)
			{
				MessageDialog.openError(getShell(), "Import Error", e.getMessage());
			}
		}
		return toReturn;
	}

}
