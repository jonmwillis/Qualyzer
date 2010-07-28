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

package ca.mcgill.cs.swevo.qualyzer;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * PreferencePage for Qualyzer.
 *
 */
public class QualyzerPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
	/**
	 * Constructor.
	 */
	public QualyzerPreferencePage()
	{
		super(GRID);
	}

	@Override
	protected void createFieldEditors()
	{
		addField(new StringFieldEditor("DefaultInvestigator", "Default Investigator Name", getFieldEditorParent()));
		
		addField(new ColorFieldEditor("FragmentColor", "Marked Fragment Color", getFieldEditorParent()));
		
		
	}

	@Override
	public void init(IWorkbench workbench)
	{
		setPreferenceStore(QualyzerActivator.getDefault().getPreferenceStore());
		setDescription("Settings to use in Qualyzer");
	}

}
