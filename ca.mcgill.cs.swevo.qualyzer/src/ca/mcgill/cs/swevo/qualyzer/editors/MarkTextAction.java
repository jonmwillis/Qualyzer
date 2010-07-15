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
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Point;

import ca.mcgill.cs.swevo.qualyzer.dialogs.CodeChooserDialog;
import ca.mcgill.cs.swevo.qualyzer.model.CodeEntry;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;
import ca.mcgill.cs.swevo.qualyzer.model.IAnnotatedDocument;

/**
 *
 */
public class MarkTextAction extends Action
{

	private RTFEditor fEditor;
	private RTFSourceViewer fSourceViewer;
	
	/**
	 * 
	 */
	public MarkTextAction(RTFEditor editor, RTFSourceViewer viewer)
	{
		super(Messages.getString("editors.MarkTextAction.addCode")); //$NON-NLS-1$
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
		IAnnotatedDocument document = fEditor.getDocument();
		Point selection = fSourceViewer.getSelectedRange();
		Position position = new Position(selection.x, selection.y);
		
		Fragment fragment = null;
		for(Fragment existingFragment : document.getFragments())
		{
			if(existingFragment.getOffset() == position.offset && 
					existingFragment.getLength() == position.length)
			{
				fragment = existingFragment;
				break;
			}
		}
		
		CodeChooserDialog dialog = new CodeChooserDialog(fEditor.getSite().getShell(), document.getProject(),
				fragment);
		dialog.create();
		if(dialog.open() == Window.OK)
		{	
			CodeEntry entry = new CodeEntry();
			entry.setCode(dialog.getCode());
			
			if(fragment == null)
			{
				fragment = Facade.getInstance().createFragment(document, position.offset,
						position.length);
			}
			
			fragment.getCodeEntries().add(entry);
			fSourceViewer.markFragment(fragment);
			
			Facade.getInstance().saveDocument(document);
			
			fEditor.setDirty();
		}

	}
}
