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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.TreeViewer;

import ca.mcgill.cs.swevo.qualyzer.editors.inputs.CodeTableInput;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.CodeTableInput.CodeTableRow;
import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.CodeListener;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.ListenerManager;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.MemoListener;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.ProjectListener;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.model.TranscriptListener;
import ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType;

/**
 *
 */
public final class TreeModel implements CodeListener, MemoListener, TranscriptListener, ProjectListener
{
	private static final String SLASH = "/";
	
	private static Map<String, TreeModel> gModels = new HashMap<String, TreeModel>();
	
	private Project fProject;
	private CodeTableInput fInput;
	
	private LinkedList<Node> fNewLocalList;
	private Node fTreeRoot;
	private HashMap<Long, List<Node>> fCodes;
	private List<TreeViewer> fListeners;
	
	private TreeModel(CodeTableInput input)
	{
		fProject = input.getProject();
		fInput = input;
		fNewLocalList = new LinkedList<Node>();
		fTreeRoot = new Node(this);
		fCodes = new HashMap<Long, List<Node>>();
		
		for(CodeTableRow row : fInput.getData())
		{
			Code code = row.getCode();
			for(String path : code.getParents())
			{
				insertIntoList(row, path);
			}
		}
				
		buildModel();
		
		aggregateFreqs();
		
		ListenerManager listenerManager = Facade.getInstance().getListenerManager();
		listenerManager.registerCodeListener(fProject, this);
		listenerManager.registerMemoListener(fProject, this);
		listenerManager.registerProjectListener(fProject, this);
		listenerManager.registerTranscriptListener(fProject, this);
		
		fListeners = new ArrayList<TreeViewer>();
	}
	
	/**
	 * 
	 */
	private void aggregateFreqs()
	{
		for(Node child : fTreeRoot.getChildren().values())
		{
			child.computeFreq();
		}
		
	}

	private void buildModel()
	{	
		Iterator<Node> iNode = fNewLocalList.iterator();
		while(iNode.hasNext())
		{
			Node node = iNode.next();
			if(node.getPathToRoot().equals(SLASH))
			{
				node.setParent(fTreeRoot);
				iNode.remove();
			}
		}
		
		for(Node node : fNewLocalList)
		{
			String path = node.getPathToRoot();
			String[] pathNodes = path.split(SLASH);
			
			Node parent = fTreeRoot;
			for(String nextNode : pathNodes)
			{
				parent = parent.getChild(Long.parseLong(nextNode));
			}
			
			node.setParent(parent);
		}
	}
	
	/**
	 * Get the TreeModel for this project.
	 * @param project
	 * @return
	 */
	public static TreeModel getTreeModel(Project project)
	{
		TreeModel model = gModels.get(project.getName());
		if(model == null)
		{
			model = new TreeModel(new CodeTableInput(project));
			gModels.put(project.getName(), model);
		}
		return model;
	}
	
	/**
	 * Get the TreeModel for this input.
	 * @param input
	 * @return
	 */
	public static TreeModel getTreeModel(CodeTableInput input)
	{
		TreeModel model = gModels.get(input.getProject().getName());
		if(model == null)
		{
			model = new TreeModel(input);
			gModels.put(input.getProject().getName(), model);
		}
		return model;
	}
	
	/**
	 * 
	 * @return
	 */
	public Node getRoot()
	{
		return fTreeRoot;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return fTreeRoot.toString();
	}
	
	/**
	 * 
	 */
	public void updatePaths()
	{
		for(Code code : fProject.getCodes())
		{
			code.getParents().clear();
		}
		
		depthFirstUpdate(fTreeRoot);
		
	}
	
	private void depthFirstUpdate(Node node)
	{
		for(Code code : fProject.getCodes())
		{
			if(code.getPersistenceId().equals(node.getPersistenceId()))
			{
				String pathToRoot = node.getPathToRoot();
				if(!pathToRoot.isEmpty())
				{
					code.getParents().add(pathToRoot);
				}
				else if(!node.getChildren().isEmpty())
				{
					code.getParents().add("/");
				}
				break;
			}
		}
		
		for(Node child : node.getChildren().values())
		{
			depthFirstUpdate(child);
		}
	}
	
	private void insertIntoList(CodeTableRow row, String pathToRoot)
	{
		Node node = new Node(row, pathToRoot);
		
		if(pathToRoot.split(SLASH).length == 0)
		{
			fNewLocalList.addFirst(node);
		}
		else
		{
			int index = binarySearch(pathToRoot.split(SLASH).length, 0, fNewLocalList.size() - 1);
			fNewLocalList.add(index, node);
		}
	}

	/**
	 * Find the index to insert the node.
	 * @param length
	 * @param i
	 * @param j
	 * @return
	 */
	private int binarySearch(int length, int start, int end)
	{
		if(end < start)
		{
			return 0;
		}
		
		if(start == end)
		{
			Node node = fNewLocalList.get(start);
			int toReturn;
			if(node.getPathToRoot().split(SLASH).length >= length)
			{
				toReturn = start;
			}
			else
			{
				toReturn = start + 1;
			}
			return toReturn;
		}
		else
		{
			int index = (start + end)/2;
			Node node = fNewLocalList.get(index);
			int nodeLength = node.getPathToRoot().split(SLASH).length;
			int toReturn;
			if(nodeLength == length)
			{
				toReturn = index;
			}
			else if(nodeLength > length)
			{
				if(index == end)
				{
					index--;
				}
				toReturn = binarySearch(length, start, index);
			}
			else
			{
				if(index == start)
				{
					index++;
				}
				toReturn = binarySearch(length, index, end);
			}
			return toReturn;
		}
	}
	
	/**
	 * Save the hierarchy data.
	 */
	public void save()
	{
		fProject = PersistenceManager.getInstance().getProject(fProject.getName());
		updatePaths();
		Facade.getInstance().saveCodes(fProject.getCodes().toArray(new Code[0]));
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.model.CodeListener#codeChanged(
	 * ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType, 
	 * ca.mcgill.cs.swevo.qualyzer.model.Code[], ca.mcgill.cs.swevo.qualyzer.model.Facade)
	 */
	@Override
	public void codeChanged(ChangeType cType, Code[] codes, Facade facade)
	{
		if(cType == ChangeType.MODIFY)
		{
			for(Code code : codes)
			{
				List<Node> list = fCodes.get(code.getPersistenceId());
				if(list != null)
				{
					for(Node node : list)
					{
						node.setCodeName(code.getCodeName());
					}
				}
			}
			modelChanged();
		}
		else if(cType == ChangeType.DELETE)
		{
			for(Code code : codes)
			{
				List<Node> list = fCodes.get(code.getPersistenceId());
				if(list != null)
				{
					for(Node node : list)
					{
						depthFirstRemove(node);
					}
					list.clear();
					fCodes.put(code.getPersistenceId(), list);
				}
			}
			modelChanged();
		}
		
	}

	/**
	 * @param node
	 */
	private void depthFirstRemove(Node node)
	{
		node.getParent().getChildren().remove(node.getPersistenceId());
		node.setParent(null);
		
		for(Node child : node.getChildren().values())
		{
			depthFirstRemove(child);
		}
		
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.model.MemoListener#memoChanged(
	 * ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType, 
	 * ca.mcgill.cs.swevo.qualyzer.model.Memo[], ca.mcgill.cs.swevo.qualyzer.model.Facade)
	 */
	@Override
	public void memoChanged(ChangeType cType, Memo[] memos, Facade facade)
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.model.TranscriptListener#transcriptChanged(
	 * ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType, 
	 * ca.mcgill.cs.swevo.qualyzer.model.Transcript[], ca.mcgill.cs.swevo.qualyzer.model.Facade)
	 */
	@Override
	public void transcriptChanged(ChangeType cType, Transcript[] transcripts, Facade facade)
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.model.ProjectListener#projectChanged(
	 * ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType, 
	 * ca.mcgill.cs.swevo.qualyzer.model.Project, ca.mcgill.cs.swevo.qualyzer.model.Facade)
	 */
	@Override
	public void projectChanged(ChangeType cType, Project project, Facade facade)
	{
		if(cType == ChangeType.DELETE)
		{
			dispose();
		}
		else if(cType == ChangeType.RENAME)
		{
			
			
		}
	}
	
	private void dispose()
	{
		ListenerManager listenerManager = Facade.getInstance().getListenerManager();
		listenerManager.unregisterCodeListener(fProject, this);
		listenerManager.unregisterMemoListener(fProject, this);
		listenerManager.unregisterProjectListener(fProject, this);
		listenerManager.unregisterTranscriptListener(fProject, this);
		
		gModels.remove(fProject.getName());
	}
	
	/**
	 * Add a node to the master set of codes.
	 * @param node
	 */
	public void addNodeToCodes(Node node)
	{
		List<Node> list = fCodes.get(node.getPersistenceId());
		if(list == null)
		{
			list = new ArrayList<Node>();
		}
		if(!list.contains(node))
		{
			list.add(node);
		}
		fCodes.put(node.getPersistenceId(), list);
	}
	
	/**
	 * 
	 * @param viewer
	 */
	public void addListener(TreeViewer viewer)
	{
		if(!fListeners.contains(viewer))
		{
			fListeners.add(viewer);
		}
	}
	
	/**
	 * 
	 * @param viewer
	 */
	public void removeListener(TreeViewer viewer)
	{
		fListeners.remove(viewer);
	}
	
	private void modelChanged()
	{
		for(TreeViewer viewer : fListeners)
		{
			viewer.refresh();
		}
	}
}
