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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.hibernate.HibernateException;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.mcgill.cs.swevo.qualyzer.model.HibernateDBManager;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;

/**
 * The activator class controls the plug-in life cycle.
 * 
 */
public class QualyzerActivator extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "ca.mcgill.cs.swevo.qualyzer"; //$NON-NLS-1$

	public static final String PROJECT_EXPLORER_VIEW_ID = "ca.mcgill.cs.swevo.qualyzer.projectexplorer"; //$NON-NLS-1$

	// The shared instance
	private static QualyzerActivator gPlugin;

	// Indexed by Project Name
	private Map<String, HibernateDBManager> fHibernateManagers = new HashMap<String, HibernateDBManager>();
	
	private final Logger fLogger = LoggerFactory.getLogger(QualyzerActivator.class);

	/**
	 * The constructor.
	 */
	public QualyzerActivator()
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
	 */
	@Override
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		gPlugin = this;
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		
		for(IProject project : root.getProjects())
		{
			try
			{
				PersistenceManager.getInstance().refreshManager(project);
			}
			catch(QualyzerException e)
			{
				//Stop the exception.
			}
		}
		
		fLogger.info("Qualyzer Started"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
	 */
	@Override
	public void stop(BundleContext context) throws Exception
	{
		gPlugin = null;
		super.stop(context);
		fLogger.info("Qualyzer Stopped"); //$NON-NLS-1$
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return the shared instance
	 */
	public static QualyzerActivator getDefault()
	{
		return gPlugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path)
	{
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * 
	 *
	 * @return A Map of HibernateDBManager indexed by the project name.
	 */
	public Map<String, HibernateDBManager> getHibernateDBManagers()
	{
		return fHibernateManagers;
	}
}
