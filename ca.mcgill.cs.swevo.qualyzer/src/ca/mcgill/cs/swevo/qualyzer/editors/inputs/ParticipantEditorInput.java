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
package ca.mcgill.cs.swevo.qualyzer.editors.inputs;

import org.eclipse.core.internal.runtime.AdapterManager;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.model.IWorkbenchAdapter;

import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperParticipant;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class ParticipantEditorInput implements IEditorInput
{
	private Participant fParticipant;
	
	public ParticipantEditorInput(Participant participant)
	{
		fParticipant = participant;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	@Override
	public boolean exists()
	{
		return fParticipant != null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 */
	@Override
	public ImageDescriptor getImageDescriptor()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getName()
	 */
	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return fParticipant.getParticipantId();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
	@Override
	public IPersistableElement getPersistable()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	@Override
	public String getToolTipText()
	{
		// TODO Auto-generated method stub
		return fParticipant.getFullName();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(Class adapter)
	{
		if (IWorkbenchAdapter.class.equals(adapter))
		{
			return new IWorkbenchAdapter() {

				public Object[] getChildren(Object o) 
				{
					return new Object[0];
				}

				public ImageDescriptor getImageDescriptor(Object object)
				{
					return ParticipantEditorInput.this.getImageDescriptor();
				}

				public String getLabel(Object o) 
				{
					return ParticipantEditorInput.this.getName();
				}

				public Object getParent(Object o)
				{
					return new WrapperParticipant(ParticipantEditorInput.this.getParticipant().getProject());
				}
			};
		}

		IAdapterManager manager = AdapterManager.getDefault();
		return manager.getAdapter(this, adapter);
	}

	/**
	 * @return
	 */
	public Participant getParticipant()
	{
		return fParticipant;
	}
	
	@Override
	public int hashCode()
	{
		return fParticipant.hashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this)
		{
			return true;
		}
		if(obj.getClass().equals(getClass()))
		{
			return fParticipant.equals(((ParticipantEditorInput) obj).getParticipant());
		}
		return false;
	}

}
