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

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import ca.mcgill.cs.swevo.qualyzer.model.CodeEntry;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;

/**
 *
 */
public class RemoveCodeAction extends Action
{

	private RTFEditor fEditor;
	private RTFSourceViewer fSourceViewer;
	
	/**
	 * Contructor.
	 * @param editor
	 * @param viewer
	 */
	public RemoveCodeAction(RTFEditor editor, RTFSourceViewer viewer)
	{
		super(Messages.getString("editors.RemoveCodeAction.removeCode")); //$NON-NLS-1$
		fEditor = editor;
		fSourceViewer = viewer;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void run()
	{
		Annotation annotation = null;
		Fragment fragment = null;
		IAnnotationModel model = fSourceViewer.getAnnotationModel();
		Point selection = fSourceViewer.getSelectedRange();
		Iterator<Annotation> iter = model.getAnnotationIterator();
		
		while(iter.hasNext())
		{
			annotation = iter.next();
			if(annotation instanceof FragmentAnnotation)
			{
				Position position = model.getPosition(annotation);
				if(position.offset == selection.x && position.length == selection.y)
				{
					fragment = ((FragmentAnnotation) annotation).getFragment();
					break;
				}
			}
		}
		
		if(fragment != null)
		{
			Object[] codesToDelete = openSelectionDialog(fragment);
			
			deleteCodes(fragment, codesToDelete);
			
			Position p = model.getPosition(annotation);
			model.removeAnnotation(annotation);
			
			if(fragment.getCodeEntries().isEmpty())
			{
				Facade.getInstance().deleteFragment(fragment);
			}
			else
			{
				annotation = new FragmentAnnotation(fragment);
				model.addAnnotation(annotation, p);
			}
			fEditor.setDirty();
		}
	}

	/**
	 * @param fragment
	 * @param codesToDelete
	 */
	private void deleteCodes(Fragment fragment, Object[] codesToDelete)
	{
		for(Object toDelete : codesToDelete)
		{
			for(int i = 0; i < fragment.getCodeEntries().size(); i++)
			{
				CodeEntry entry = fragment.getCodeEntries().get(i);
				if(entry.getCode().getCodeName().equals(toDelete))
				{
					fragment.getCodeEntries().remove(i);
					Facade.getInstance().saveTranscript(fragment.getTranscript());
					break;
				}
			}
		}
	}
	
	/**
	 * @param fragment
	 * @return
	 */
	private Object[] openSelectionDialog(Fragment fragment)
	{
		String[] codes = new String[fragment.getCodeEntries().size()];
		for(int i = 0; i < codes.length; i++)
		{
			codes[i] = fragment.getCodeEntries().get(i).getCode().getCodeName();
		}
		
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(fEditor.getSite().getShell(),
				new LabelProvider());
		dialog.setElements(codes);
		dialog.setTitle(Messages.getString("editors.RTFEditor.removeCode")); //$NON-NLS-1$
		
		dialog.open();
		Object[] codesToDelete = dialog.getResult();
		return codesToDelete;
	}
}
