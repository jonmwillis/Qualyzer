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
package ca.mcgill.cs.swevo.qualyzer.editors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.Position;
import org.eclipse.swt.graphics.Point;

/**
 * The bold action in the RTF Editor.
 * Mostly delegates to the SourceViewer.
 */
public class BoldAction extends Action
{
	private RTFEditor fEditor;
	private RTFSourceViewer fSourceViewer;

	/**
	 * Constructor.
	 */
	public BoldAction(RTFEditor editor, RTFSourceViewer viewer)
	{
		super(Messages.getString("editors.BoldAction.bold"), Action.AS_CHECK_BOX); //$NON-NLS-1$
		fEditor = editor;
		fSourceViewer = viewer;
		
		setEnabled(false);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run()
	{
		Point selection = fSourceViewer.getSelectedRange();
		Position position = new Position(selection.x, selection.y);
		
		fSourceViewer.toggleBold(position);
		fEditor.setDirty();
	}
}
