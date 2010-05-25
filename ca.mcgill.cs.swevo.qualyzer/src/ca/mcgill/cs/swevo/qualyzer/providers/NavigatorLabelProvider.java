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
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonLabelProvider;
import org.eclipse.ui.navigator.IDescriptionProvider;

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

	public void init(ICommonContentExtensionSite aConfig)
	{
		// init
	}

	public String getDescription(Object anElement)
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

	public void restoreState(IMemento aMemento)
	{

	}

	public void saveState(IMemento aMemento)
	{
	}

}
