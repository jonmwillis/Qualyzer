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
/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.providers;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 *
 */
public class TreeDropListener extends ViewerDropAdapter
{

	private Viewer fViewer;
	private Node fTarget;

	/**
	 * @param viewer
	 */
	public TreeDropListener(Viewer viewer)
	{
		super(viewer);
		fViewer = viewer;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerDropAdapter#drop(org.eclipse.swt.dnd.DropTargetEvent)
	 */
	@Override
	public void drop(DropTargetEvent event)
	{
		Node target = (Node) determineTarget(event);
		Node root = (Node) fViewer.getInput();
		
		if(target != null)
		{
			fTarget = target;
		}
		else
		{
			fTarget = root;
		}
		
		super.drop(event);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerDropAdapter#performDrop(java.lang.Object)
	 */
	@Override
	public boolean performDrop(Object data)
	{
		Long id = Long.parseLong(((String) data).split(":")[1]);
		
		if(fTarget != null)
		{
			Node child = fTarget.getChild(id);
			if(child != null)
			{
				return false;
			}
			
			Node parent = fTarget;
			while(parent != null)
			{
				if(parent.getPersistenceId() == id)
				{
					return false;
				}
				parent = parent.getParent();
			}
			
			fTarget.addChild((String) data);
			Node root = (Node) fViewer.getInput();
			root.computeFreq();
			fViewer.refresh();
			return true;
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerDropAdapter#validateDrop(
	 * java.lang.Object, int, org.eclipse.swt.dnd.TransferData)
	 */
	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType)
	{
		
		
		if(TextTransfer.getInstance().isSupportedType(transferType))
		{
			return true;
		}
		return false;
	}

}
