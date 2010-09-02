/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Barthelemy Dagenais (bart@cs.mcgill.ca)
 *     Jonathan Faubert
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.mcgill.cs.swevo.qualyzer.editors.RTFConstants;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.HibernateDBManager;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Timestamp;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.util.FileUtil;

/**
 * The activator class controls the plug-in life cycle.
 * 
 */
public class QualyzerActivator extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "ca.mcgill.cs.swevo.qualyzer"; //$NON-NLS-1$

	public static final String PROJECT_EXPLORER_VIEW_ID = "ca.mcgill.cs.swevo.qualyzer.projectexplorer"; //$NON-NLS-1$
	
	public static final String CURRENT_VERSION = "1.0.0"; //$NON-NLS-1$

	private static final int SECONDS_PER_MINUTE = 60;

	private static final int TEN = 10;
	
	// The shared instance
	private static QualyzerActivator gPlugin;

	// Indexed by Project Name
	private Map<String, HibernateDBManager> fHibernateManagers = new HashMap<String, HibernateDBManager>();
	
	private final Logger fLogger = LoggerFactory.getLogger(QualyzerActivator.class);
	
	/**
	 * Used by NavigatorContentProvider to determine whether or not to process delta.
	 */
	private boolean fCreatingProject;

	/**
	 * Used by ApplicationWorkbench advisor to display a message when the workspace is upgraded.
	 */
	private String fUpgradeMessage;
	
	/**
	 * Used by ApplicationWorkbench advisor to display a message when the workspace is upgraded.
	 */
	private boolean fUpgradeMessageError;
	
	/**
	 * The constructor.
	 */
	public QualyzerActivator()
	{
		fCreatingProject = false;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isCreatingProject()
	{
		return fCreatingProject;
	}
	
	/**
	 * 
	 * @param set
	 */
	public void setCreatingProject(boolean set)
	{
		fCreatingProject = set;
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
		
		List<String> upgradedWell = new ArrayList<String>();
		List<String> upgradedBad = new ArrayList<String>();
		
		for(IProject project : root.getProjects())
		{
			checkVersion(project, upgradedWell, upgradedBad);
			
			PersistenceManager.getInstance().refreshManager(project);
		}
		
		if (!upgradedWell.isEmpty() || !upgradedBad.isEmpty()) 
		{
			computeUpgradeMessage(upgradedWell, upgradedBad);
		}
		
		for(IProject project : root.getProjects())
		{
			renewTimestamps(project);
		}
		
		fLogger.info("Qualyzer Started"); //$NON-NLS-1$
	}

	/**
	 * @param project
	 */
	private void renewTimestamps(IProject project)
	{
		Project qProject = PersistenceManager.getInstance().getProject(project.getName());
		
		IFolder folder = project.getFolder("transcripts");
		
		for(Transcript transcript : qProject.getTranscripts())
		{
			IFile file = folder.getFile(transcript.getFileName());
			
			try
			{
				for(IMarker marker : file.findMarkers(RTFConstants.TIMESTAMP_MARKER_ID, false, 0))
				{
					marker.delete();
				}
				
				Transcript lTranscript = Facade.getInstance().forceTranscriptLoad(transcript);
				
				for(Timestamp timestamp : lTranscript.getTimestamps().values())
				{
					Map<String, Object> map = new HashMap<String, Object>();
					MarkerUtilities.setLineNumber(map, timestamp.getLineNumber());
					MarkerUtilities.setMessage(map, getTimeString(timestamp.getSeconds()));
					map.put("time", timestamp.getSeconds());
					MarkerUtilities.createMarker(file, map, RTFConstants.TIMESTAMP_MARKER_ID);
				}
			}
			catch (CoreException e)
			{
				fLogger.error("Could not update timestamps", e);
			}
		}
	}
	
	private String getTimeString(int seconds)
	{
		int minutes = seconds / SECONDS_PER_MINUTE;
		int secondsRemaining = seconds % SECONDS_PER_MINUTE;
		String secs = (secondsRemaining < TEN) ? "0"+secondsRemaining : ""+secondsRemaining; //$NON-NLS-1$ //$NON-NLS-2$
		return minutes + ":" + secs; //$NON-NLS-1$
	}

	private void computeUpgradeMessage(List<String> upgradedWell,
			List<String> upgradedBad) 
	{
		boolean error = !upgradedBad.isEmpty();
		
		
		StringBuilder builder = new StringBuilder();
		if (!upgradedWell.isEmpty())
		{
			builder.append(Messages.getString("QualyzerActivator.upgradedWell")); //$NON-NLS-1$
			builder.append(buildList(upgradedWell));
		}
		
		if (error)
		{
			if (builder.toString().length() > 0)
			{
				builder.append("\n"); //$NON-NLS-1$
			}
			builder.append(Messages.getString("QualyzerActivator.upgradedBad")); //$NON-NLS-1$
			builder.append(buildList(upgradedBad));
		}
		
			fUpgradeMessage = builder.toString();
			fUpgradeMessageError = error;
	}
	
	private String buildList(List<String> list) 
	{
		int size = list.size();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i<size-1; i++)
		{
			builder.append(list.get(i));
			builder.append(", "); //$NON-NLS-1$
		}
		
		builder.append(list.get(size-1));
		builder.append("."); //$NON-NLS-1$
		
		return builder.toString();
	}

	private void checkVersion(IProject project, List<String> upgradedWell,
			List<String> upgradedBad) 
	{
		try
		{
			String projectVersion = FileUtil.getProjectProperty(project, FileUtil.PROJECT_VERSION);
			if (projectVersion.isEmpty() || !projectVersion.equals(CURRENT_VERSION)) 
			{
				Facade.getInstance().updateProject(project);
				upgradedWell.add(project.getName());
			}
		}
		// CSOFF:
		// We really don't want to let an exception slip outside this method.
		catch(Exception e) 
		{
			fLogger.error("Error while checking project version.", e); //$NON-NLS-1$
			upgradedBad.add(project.getName());
		}
		// CSON:
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
	
	/**
	 * 
	 * @return
	 */
	public String getUpgradeMessage()
	{
		return fUpgradeMessage;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isUpgradeMessageError()
	{
		return fUpgradeMessageError;
	}
}
