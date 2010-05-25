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
package ca.mcgill.cs.swevo.qualyzer.providers;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.IDescriptionProvider;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;

/**
 * Label provider for our navigator content.
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class NavigatorLabelProvider extends LabelProvider implements ILabelProvider, IDescriptionProvider
{
	private static final String FOLDER_IMG = "FOLDER_IMG";
	
	private final ImageRegistry fRegistry;
	
	public NavigatorLabelProvider()
	{
		fRegistry = QualyzerActivator.getDefault().getImageRegistry();
		addImage(FOLDER_IMG, QualyzerActivator.PLUGIN_ID, "icons/fldr_obj.gif");
	}
	
	public void init(ICommonContentExtensionSite aConfig)
	{
	}
	
	private void addImage(String key, String pluginID, String path)
	{
		String fullKey = computeKey(key, pluginID);
		ImageDescriptor descriptor = fRegistry.getDescriptor(fullKey);
		if(descriptor == null)
		{
			fRegistry.put(fullKey, AbstractUIPlugin.imageDescriptorFromPlugin(pluginID, path));
		}
	}
	
	private String computeKey(String key, String pluginID)
	{
		return pluginID + "_" + key;
	}
	
	private Image getImage(String key, String pluginID)
	{
		return fRegistry.get(computeKey(key, pluginID));
	}
	
	public Image getImage(Object element)
	{
		Image image = null;
		if(element instanceof IProject)
		{
			
		}
		else if(element instanceof ProjectWrapper)
		{
			image = getImage(FOLDER_IMG, QualyzerActivator.PLUGIN_ID);
		}
		else if(element instanceof Transcript)
		{
		}
		else if(element instanceof Investigator)
		{
		}
		else if(element instanceof Memo)
		{
		}
		else if(element instanceof Participant)
		{
		}
		else if(element instanceof Code)
		{
		}
		
		return image;
	}

	//Only displayed in the status bar
	// CSOFF:
	public String getDescription(Object anElement)
	{
//		String output = null;
//
//		if (anElement instanceof IProject)
//		{
//			output = ((IProject) anElement).getName();
//		}
//		else if(anElement instanceof Project)
//		{
//			output = ((Project) anElement).getName();
//		}
//		else if(anElement instanceof ProjectWrapper)
//		{
//			output = ((ProjectWrapper) anElement).getResource();
//		}
//		else if(anElement instanceof Transcript)
//		{
//			output = ((Transcript) anElement).getName();
//		}
//		else if(anElement instanceof Investigator)
//		{
//			output = ((Investigator) anElement).getFullName();
//		}
//		else if(anElement instanceof Memo)
//		{
//			output = ((Memo) anElement).getName();
//		}
//		else if(anElement instanceof Participant)
//		{
//			output = ((Participant) anElement).getFullName();
//		}
//		else if(anElement instanceof Code)
//		{
//			output = ((Code) anElement).getCodeName();
//		}
//
		return null;
	}
	
	public String getText(Object anElement)
	{
		String output = null;

		if (anElement instanceof IProject)
		{
			output = ((IProject) anElement).getName();
		}
		else if(anElement instanceof Project)
		{
			output = ((Project) anElement).getName();
		}
		else if(anElement instanceof ProjectWrapper)
		{
			output = ((ProjectWrapper) anElement).getResource();
		}
		else if(anElement instanceof Transcript)
		{
			output = ((Transcript) anElement).getName();
		}
		else if(anElement instanceof Investigator)
		{
			output = ((Investigator) anElement).getFullName();
		}
		else if(anElement instanceof Memo)
		{
			output = ((Memo) anElement).getName();
		}
		else if(anElement instanceof Participant)
		{
			output = ((Participant) anElement).getFullName();
		}
		else if(anElement instanceof Code)
		{
			output = ((Code) anElement).getCodeName();
		}

		return output;
	}
	// CSON:
	public void restoreState(IMemento aMemento)
	{

	}

	public void saveState(IMemento aMemento)
	{
	}

}
