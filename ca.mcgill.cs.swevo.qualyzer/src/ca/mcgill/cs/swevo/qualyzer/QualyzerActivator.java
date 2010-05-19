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
package ca.mcgill.cs.swevo.qualyzer;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import ca.mcgill.cs.swevo.qualyzer.model.HibernateDBManager;

/**
 * The activator class controls the plug-in life cycle.
 * 
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 */
public class QualyzerActivator extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "ca.mcgill.cs.swevo.qualyzer";

	public static final String PROJECT_EXPLORER_VIEW_ID = "ca.mcgill.cs.swevo.qualyzer.projectexplorer";

	// The shared instance
	private static QualyzerActivator gPlugin;

	// Indexed by Project Name
	private Map<String, HibernateDBManager> fHibernateManagers = new HashMap<String, HibernateDBManager>();

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
