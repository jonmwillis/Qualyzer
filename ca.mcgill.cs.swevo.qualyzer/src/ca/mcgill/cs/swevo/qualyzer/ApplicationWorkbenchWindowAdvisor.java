/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Barthelemy Dagenais (bart@cs.mcgill.ca)
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer;

import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * Windows Advisor.
 */
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor
{
	public static final int DEFAULT_WIDTH = 800;
	
	public static final int DEFAULT_HEIGHT = 600;
	
	/**
	 * Just calls the super for now.
	 * @param configurer
	 */
	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer)
	{
		super(configurer);
	}

	@Override
	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer)
	{
		return new ApplicationActionBarAdvisor(configurer);
	}

	@Override
	public void preWindowOpen()
	{
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(false);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#postWindowCreate()
	 */
	@Override
	public void postWindowCreate()
	{
		PreferenceManager pm = PlatformUI.getWorkbench().getPreferenceManager();
		
		pm.remove("net.sf.colorer.eclipse.PreferencePage"); //$NON-NLS-1$
		IPreferenceNode node = pm.remove("org.eclipse.ui.preferencePages.Workbench"); //$NON-NLS-1$
		for(IPreferenceNode sub : node.getSubNodes())
		{
			if(sub.getId().equals("org.eclipse.ui.preferencePages.Keys")) //$NON-NLS-1$
			{
				pm.addToRoot(sub);
			}
		}
	}
}
