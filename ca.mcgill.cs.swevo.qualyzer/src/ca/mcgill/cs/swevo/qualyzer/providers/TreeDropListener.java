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

	private static final String SPLIT = ":"; //$NON-NLS-1$
	private static final int TREE_DATA_SIZE = 2;
	private static final int TABLE_DATA_SIZE = 3;
	
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
		String info = (String) data;
		String[] values = info.split(SPLIT);
		
		if(fTarget != null)
		{
			if(values.length == TREE_DATA_SIZE)
			{
				Long id = Long.parseLong(values[0]);
				Node toMove = findNode(values[1], id);
				
				Node oldParent = toMove.getParent();
				oldParent.getChildren().remove(toMove.getPersistenceId());
				
				toMove.setParent(fTarget);
				toMove.updatePaths();
				
				refreshEditor();
				return true;
			}
			else if(values.length == TABLE_DATA_SIZE)
			{
				fTarget.addChild((String) data);
				refreshEditor();
				return true;
			}
		}
		
		return false;
	}

	private void refreshEditor()
	{
		Node root = (Node) fViewer.getInput();
		root.computeFreq();
		fViewer.refresh();
		fPage.setDirty();
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
		
		Node nTarget = (Node) target;
		if(nTarget == null)
		{
			nTarget = (Node) fViewer.getInput();
		}
		
		String data = (String) selection.getFirstElement();
		String[] values = data.split(SPLIT);
		Long id = null;
		
		boolean valid = true;
		
		if(nTarget != null)
		{
			if(values.length == TABLE_DATA_SIZE)
			{
				id = Long.parseLong(values[1]);
				
				if(containsCycle(id, nTarget))
				{
					valid = false;
				}
			}
			else if(values.length == TREE_DATA_SIZE)
			{
				id = Long.parseLong(values[0]);
				
				Node node = findNode(values[1], id);
				
				if(depthFirstContainsCycle(node, nTarget))
				{
					valid = false;
				}
			}
			
			Node child = nTarget.getChild(id);
			if(child != null)
			{
				valid = false;
			}
			
			return valid && TextTransfer.getInstance().isSupportedType(transferType);
		}
		return false;
	}

	/**
	 * @param path
	 * @param id
	 * @return
	 */
	private Node findNode(String path, Long id)
	{
		Node node = (Node) fViewer.getInput();
		for(String next : path.split("/")) //$NON-NLS-1$
		{
			node = node.getChild(Long.parseLong(next));
		}
		node = node.getChild(id);
		return node;
	}

	/**
	 * @param id
	 * @param nTarget
	 * @param string
	 * @return
	 */
	private boolean depthFirstContainsCycle(Node node, Node nTarget)
	{
		boolean hasCycle = false;
		
		hasCycle = hasCycle || containsCycle(node.getPersistenceId(), nTarget);
		
		for(Node child : node.getChildren().values())
		{
			hasCycle = hasCycle || depthFirstContainsCycle(child, nTarget);
		}
		
		return hasCycle;
	}

	/**
	 * @param id
	 * @param nTarget
	 */
	private boolean containsCycle(Long id, Node nTarget)
	{
		Node parent = nTarget;
		while(parent != null)
		{
			if(parent.getPersistenceId().equals(id))
			{
				return true;
			}
			parent = parent.getParent();
		}
		
		return false;
	}

}
