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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import ca.mcgill.cs.swevo.qualyzer.editors.inputs.CodeTableInput;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.CodeTableInput.CodeTableRow;
import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 *
 */
public final class TreeModel
{
	private static final String SLASH = "/";
	
	private static Map<String, TreeModel> gModels = new HashMap<String, TreeModel>();
	
	private Project fProject;
	private CodeTableInput fInput;
	
	private LinkedList<Node> fNewLocalList;
	private Node fTreeRoot;
	
	private TreeModel(CodeTableInput input)
	{
		fProject = input.getProject();
		fInput = input;
		fNewLocalList = new LinkedList<Node>();
		fTreeRoot = new Node();
		
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
			if(code.getCodeName().equals(node.getCodeName()))
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
			if(node.getPathToRoot().split(SLASH).length >= length)
			{
				return start;
			}
			else
			{
				return start + 1;
			}
		}
		else
		{
			int index = (start + end)/2;
			Node node = fNewLocalList.get(index);
			int nodeLength = node.getPathToRoot().split(SLASH).length;
			if(nodeLength == length)
			{
				return index;
			}
			else if(nodeLength > length)
			{
				if(index == end)
				{
					index--;
				}
				return binarySearch(length, start, index);
			}
			else
			{
				if(index == start)
				{
					index++;
				}
				return binarySearch(length, index, end);
			}
		}
	}
}
