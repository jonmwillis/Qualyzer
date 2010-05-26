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

import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperInvestigator;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class InvestigatorEditorInput implements IEditorInput
{
	private Investigator fInvestigator;
	
	public InvestigatorEditorInput(Investigator investigator)
	{
		fInvestigator = investigator;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	@Override
	public boolean exists()
	{
		return fInvestigator != null && fInvestigator.getNickName().length() > 0;
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
		return fInvestigator.getNickName();
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
		return fInvestigator.getFullName();
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
					return InvestigatorEditorInput.this.getImageDescriptor();
				}

				public String getLabel(Object o) 
				{
					return InvestigatorEditorInput.this.getName();
				}

				public Object getParent(Object o)
				{
					return new WrapperInvestigator(InvestigatorEditorInput.this.getInvestigator().getProject());
				}
			};
		}

		IAdapterManager manager = AdapterManager.getDefault();
		return manager.getAdapter(this, adapter);
	}

	public Investigator getInvestigator()
	{
		return fInvestigator;
	}
	
	@Override
	public int hashCode()
	{
		return fInvestigator.hashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null)
		{
			return false;
		}
		if(obj.getClass().equals(getClass()))
		{
			return fInvestigator.equals(((InvestigatorEditorInput)obj).getInvestigator());
		}
		
		return false;
	}
	
}
