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

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;

import ca.mcgill.cs.swevo.qualyzer.editors.pages.CodeEditorPage;

/**
 *
 */
public class TreeDropListener extends ViewerDropAdapter
{

	private Viewer fViewer;
	private CodeEditorPage fPage;
	private Node fTarget;

	/**
	 * @param viewer
	 * @param codeEditorPage 
	 */
	public TreeDropListener(Viewer viewer, CodeEditorPage codeEditorPage)
	{
		super(viewer);
		fViewer = viewer;
		fPage = codeEditorPage;
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
		//TODO add more complex checks for validity.
		if(fTarget != null)
		{	
			fTarget.addChild((String) data);
			Node root = (Node) fViewer.getInput();
			root.computeFreq();
			fViewer.refresh();
			fPage.setDirty();
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
		LocalSelectionTransfer sel = LocalSelectionTransfer.getTransfer();
		IStructuredSelection selection = (IStructuredSelection) sel.getSelection();
		
		String data = (String) selection.getFirstElement();
		Long id = Long.parseLong(data.split(":")[1]);
		
		Node nTarget = (Node) target;
		if(nTarget == null)
		{
			nTarget = (Node) fViewer.getInput();
		}
		
		if(nTarget != null)
		{
			Node child = nTarget.getChild(id);
			if(child != null)
			{
				return false;
			}
			
			Node parent = nTarget;
			while(parent != null)
			{
				if(parent.getPersistenceId() == id)
				{
					return false;
				}
				parent = parent.getParent();
			}
		}
		
		return TextTransfer.getInstance().isSupportedType(transferType);
	}

}
