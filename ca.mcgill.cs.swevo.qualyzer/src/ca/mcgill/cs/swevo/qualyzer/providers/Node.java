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
package ca.mcgill.cs.swevo.qualyzer.providers;

import java.util.LinkedHashMap;
import java.util.Map;

import ca.mcgill.cs.swevo.qualyzer.editors.inputs.CodeTableInput.CodeTableRow;

/**
 * 
 */
public class Node
{	
	
	private static final int CHILD_SPLIT_SIZE = 3;
	private static final long ROOT_ID = -1L;
	private static final String EMPTY = "";
	private static final String SLASH = "/";
	
	private TreeModel fModel;
	
	private Node fParent;
	private LinkedHashMap<Long, Node> fChildren;
	private String fPathToRoot;
	private String fCodeName;
	private Long fPersistenceId;
	private int fLocalFreq;
	private int fAggrFreq;
	
	/**
	 * Build the root node.
	 */
	public Node(TreeModel model)
	{
		fModel = model;
		
		fParent = null;
		fChildren = new LinkedHashMap<Long, Node>();
		fPathToRoot = EMPTY;
		fCodeName = EMPTY;
		fLocalFreq = -1;
		fAggrFreq = -1;
		fPersistenceId = ROOT_ID;
	}
	
	/**
	 * Build a child node.
	 * @param parent
	 * @param codeName
	 */
	public Node(Node parent, CodeTableRow row)
	{
		fParent = parent;
		fModel = fParent.fModel;
		
		fParent.getChildren().put(row.getPersistenceId(), this);
		fChildren = new LinkedHashMap<Long, Node>();
		fPathToRoot = EMPTY;
		
		if(parent.fPersistenceId != ROOT_ID)
		{
			if(!parent.fPathToRoot.equals(SLASH))
			{
				fPathToRoot = parent.fPathToRoot + SLASH;
			}
			fPathToRoot += parent.getPersistenceId().toString();
		}
		else
		{
			fPathToRoot = SLASH;
		}
		
		fCodeName = row.getName();
		fLocalFreq = row.getFrequency();
		fAggrFreq = fLocalFreq;
		fPersistenceId = row.getPersistenceId();
		
		fModel.addNodeToCodes(this);
	}
	
	/**
	 * Make a node with no link to its parent.
	 * To be used only by the TreeModel.
	 * @param row
	 * @param pathToRoot
	 */
	public Node(CodeTableRow row, String pathToRoot)
	{
		fParent = null;
		fChildren = new LinkedHashMap<Long, Node>();
		fPathToRoot = pathToRoot;
		fCodeName = row.getName();
		fLocalFreq = row.getFrequency();
		fAggrFreq = fLocalFreq;
		fPersistenceId = row.getPersistenceId();
	}
	
	/**
	 * @param node
	 * @param string
	 * @param parseLong
	 * @param parseInt
	 */
	public Node(Node parent, String name, long persistenceId, int frequency)
	{
		fParent = parent;
		fModel = fParent.fModel;
		fParent.getChildren().put(persistenceId, this);
		fChildren = new LinkedHashMap<Long, Node>();
		fPathToRoot = EMPTY;
		
		if(parent.fPersistenceId != ROOT_ID)
		{
			if(!parent.fPathToRoot.equals(SLASH))
			{
				fPathToRoot = parent.fPathToRoot + SLASH;
			}
			fPathToRoot += parent.getPersistenceId().toString();
		}
		else
		{
			fPathToRoot = SLASH;
		}
		
		fCodeName = name;
		fLocalFreq = frequency;
		fAggrFreq = fLocalFreq;
		fPersistenceId = persistenceId;
		
		fModel.addNodeToCodes(this);
	}

	/**
	 * 
	 * @param node
	 */
	public void setParent(Node node)
	{
		fParent = node;
		if(fParent != null)
		{
			fModel = fParent.fModel;
			fParent.getChildren().put(fPersistenceId, this);
			
			fModel.addNodeToCodes(this);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public String getPathToRoot()
	{
		return fPathToRoot;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getCodeName()
	{
		return fCodeName;
	}
	
	/**
	 * 
	 * @return
	 */
	public Map<Long, Node> getChildren()
	{
		return fChildren;
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public Node getChild(Long id)
	{
		return fChildren.get(id);
	}
	
	/**
	 * 
	 * @return
	 */
	public Node getParent()
	{
		return fParent;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{	
		return fCodeName + ":" + fPathToRoot;
	}

	/**
	 * 
	 * @return
	 */
	public int computeFreq()
	{
		fAggrFreq = fLocalFreq;
		
		for(Node child : fChildren.values())
		{
			fAggrFreq += child.computeFreq();
		}
		
		return fAggrFreq;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getAggragateFreq()
	{
		return fAggrFreq;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getLocalFreq()
	{
		return fLocalFreq;
	}

	/**
	 * @param data
	 */
	public void addChild(String data)
	{
		String[] values = data.split(":");
		if(values.length != CHILD_SPLIT_SIZE)
		{
			return;
		}
		
		new Node(this, values[0], Long.parseLong(values[1]), Integer.parseInt(values[2]));
		
	}
	
	/**
	 * 
	 * @return
	 */
	public Long getPersistenceId()
	{
		return fPersistenceId;
	}

	/**
	 * @param codeName
	 */
	public void setCodeName(String codeName)
	{
		fCodeName = codeName;
	}

	/**
	 * 
	 */
	public void updatePaths()
	{
		fPathToRoot = EMPTY;
		
		if(fParent.fPersistenceId != ROOT_ID)
		{
			if(!fParent.fPathToRoot.equals(SLASH))
			{
				fPathToRoot = fParent.fPathToRoot + SLASH;			
			}
			fPathToRoot += fParent.getPersistenceId().toString();
		}
		else
		{
			fPathToRoot = SLASH;
		}
		
		for(Node child : fChildren.values())
		{
			child.updatePaths();
		}
		
	}
}