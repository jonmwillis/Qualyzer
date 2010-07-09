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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;

import ca.mcgill.cs.swevo.qualyzer.model.Fragment;

/**
 * The SourceViewer for our editor. Displays Rich Text qualities from annotations.
 *
 */
public class RTFSourceViewer extends ProjectionViewer
{

	/**
	 * 
	 */
	private static final String EMPTY = "";  //$NON-NLS-1$

	/**
	 * @param parent
	 * @param verticalRuler
	 * @param overviewRuler
	 * @param showAnnotationsOverview
	 * @param styles
	 */
	public RTFSourceViewer(Composite parent, IVerticalRuler verticalRuler, IOverviewRuler overviewRuler,
			boolean showAnnotationsOverview, int styles)
	{
		super(parent, verticalRuler, overviewRuler, showAnnotationsOverview, styles);
		
		appendVerifyKeyListener(new VerifyKeyListener(){
			
			@Override
			public void verifyKey(VerifyEvent event)
			{
				if(event.stateMask == SWT.CONTROL)
				{
					if(event.keyCode == 'i')
					{ //remove the insert tab action!!! - JF
						event.doit = false;
					}
				}
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.source.SourceViewer#setDocument(org.eclipse.jface.text.IDocument, 
	 * org.eclipse.jface.text.source.IAnnotationModel)
	 */
	@Override
	public void setDocument(IDocument document, IAnnotationModel annotationModel)
	{
		super.setDocument(document, annotationModel);
		
		RTFDocument rtfDoc = (RTFDocument) document;
		
		for(Position position : rtfDoc.getKeys())
		{
			annotationModel.addAnnotation(rtfDoc.getAnnotation(position), position);
		}

	}
	
	/**
	 * Toggle bold for the text at the given position.
	 * @param position
	 */
	public void toggleBold(Position position)
	{
		IAnnotationModel model = getAnnotationModel();
		ArrayList<Annotation> current = new ArrayList<Annotation>();
		ArrayList<Position> currentPos = new ArrayList<Position>();
		
		findOverlaps(position, model, current, currentPos);
		
		if(current.size() == 0)
		{
			Annotation annotation = new Annotation(RTFConstants.BOLD_TYPE, false, EMPTY);
			model.addAnnotation(annotation, position);
		}
		else if(current.size() == 1)
		{
			Map<Position, Annotation> toggle = handleToggle1(model, position, currentPos, current);
			
			for(Position key : toggle.keySet())
			{
				Annotation annotation = createBoldToggledAnnotation(toggle.get(key));
				if(annotation != null)
				{
					model.addAnnotation(annotation, key);
				}
			}
		}
		else
		{
			Map<Position, Annotation> toggle = handleToggle2(current, currentPos, position, model);
			
			for(Position key : toggle.keySet())
			{
				Annotation annotation = createBoldToggledAnnotation(toggle.get(key));
				if(annotation != null)
				{
					model.addAnnotation(annotation, key);
				}
			}
		}
	}
	
	private Map<Position, Annotation> handleToggle1(IAnnotationModel model, Position position, 
			ArrayList<Position> currentPos, ArrayList<Annotation> current)
	{
		Map<Position, Annotation> toToggle = new HashMap<Position, Annotation>();
		
		Position curPos = currentPos.get(0);
		if(curPos.offset < position.offset)
		{
			Annotation annotation = new Annotation(current.get(0).getType(), false, EMPTY);
			model.addAnnotation(annotation, new Position(curPos.offset, position.offset - curPos.offset));
		}
		else if(position.offset < curPos.offset)
		{
			Annotation annotation = new Annotation(false);
			toToggle.put(new Position(position.offset, curPos.offset - position.offset), annotation);
		}
		
		if(position.offset + position.length < curPos.offset + curPos.length)
		{
			Annotation annotation = new Annotation(current.get(0).getType(), false, EMPTY);
			int offset = position.offset + position.length;
			model.addAnnotation(annotation, new Position(offset, curPos.offset + curPos.length - offset));
		}
		else if(position.offset + position.length > curPos.offset + curPos.length)
		{
			Annotation annotation = new Annotation(false);
			int offset = curPos.offset + curPos.length;
			toToggle.put(new Position(offset, position.offset + position.length - offset), annotation);
		}
		
		Annotation annotation = current.get(0);

		Position annotPos = position.length <= curPos.length ? position : curPos;
		toToggle.put(annotPos, annotation);
		
		return toToggle;
	}
	
	private Map<Position, Annotation> handleToggle2(ArrayList<Annotation> current, ArrayList<Position> currentPos,
			Position position, IAnnotationModel model)
	{
		Map<Position, Annotation> toToggle = new HashMap<Position, Annotation>();
		
		handleHead(current, currentPos, position, model, toToggle);
		
		handleTail(current, currentPos, position, model, toToggle);
		
		//handle gaps
		for(int i = 0; i < current.size() - 1; i++)
		{
			Position pos1 = currentPos.get(i);
			Position pos2 = currentPos.get(i+1);
			
			if(pos1.offset + pos1.length != pos2.offset)
			{
				Annotation annotation = new Annotation(false);
				int offset = pos1.offset + pos1.length;
				Position newPos = new Position(offset, pos2.offset - offset);
				toToggle.put(newPos, annotation);
			}
		}
		
		for(int i = 0; i < current.size(); i++)
		{
			toToggle.put(currentPos.get(i), current.get(i));
		}
		
		return toToggle;
	}

	/**
	 * @param current
	 * @param currentPos
	 * @param position
	 * @param model
	 * @param toToggle
	 */
	private void handleTail(ArrayList<Annotation> current, ArrayList<Position> currentPos, Position position,
			IAnnotationModel model, Map<Position, Annotation> toToggle)
	{
		Annotation tail = current.get(current.size() - 1);
		Position tailPos = currentPos.get(current.size() - 1);
		
		if(tailPos.offset + tailPos.length > position.offset + position.length)
		{
			current.remove(current.size() - 1);
			currentPos.remove(currentPos.size() - 1);
			
			Annotation newTail = new Annotation(tail.getType(), false, EMPTY);
			int offset = position.offset + position.length;
			Position newTailPos = new Position(offset, tailPos.offset + tailPos.length - offset);
			model.addAnnotation(newTail, newTailPos);
			
			newTail = new Annotation(tail.getType(), false, EMPTY);
			newTailPos = new Position(tailPos.offset, offset - tailPos.offset);
			current.add(newTail);
			currentPos.add(newTailPos);
		}
		else if(tailPos.offset + tailPos.length < position.offset + position.length)
		{
			Annotation annotation = new Annotation(false);
			int offset = tailPos.offset + tailPos.length;
			Position annotPos = new Position(offset, position.offset + position.length - offset);
			toToggle.put(annotPos, annotation);
		}
	}

	/**
	 * @param current
	 * @param currentPos
	 * @param position
	 * @param model
	 * @param toToggle
	 */
	private void handleHead(ArrayList<Annotation> current, ArrayList<Position> currentPos, Position position,
			IAnnotationModel model, Map<Position, Annotation> toToggle)
	{
		Annotation head = current.get(0);
		Position headPos = currentPos.get(0);
		
		if(headPos.offset < position.offset)
		{
			current.remove(0);
			currentPos.remove(0);
			
			Annotation newHead = new Annotation(head.getType(), false, EMPTY);
			Position newHeadPos = new Position(headPos.offset, position.offset - headPos.offset);
			model.addAnnotation(newHead, newHeadPos);
			
			newHead = new Annotation(head.getType(), false, EMPTY);
			newHeadPos = new Position(headPos.offset + newHeadPos.length, headPos.length - newHeadPos.length);
			current.add(0, newHead);
			currentPos.add(0, newHeadPos);
		}
		else if(position.offset < headPos.offset)
		{
			Annotation annotation = new Annotation(false);
			Position annotPos = new Position(position.offset, headPos.offset - position.offset);
			toToggle.put(annotPos, annotation);
		}
	}
	
	/**
	 * @param annotation
	 * @return
	 */
	private Annotation createBoldToggledAnnotation(Annotation annotation)
	{
		Annotation toReturn = null;
		
		if(annotation.getType().equals(Annotation.TYPE_UNKNOWN))
		{
			toReturn = new Annotation(RTFConstants.BOLD_TYPE, false, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.BOLD_ITALIC_TYPE))
		{
			toReturn = new Annotation(RTFConstants.ITALIC_TYPE, false, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE))
		{
			toReturn = new Annotation(RTFConstants.ITALIC_UNDERLINE_TYPE, false, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.BOLD_UNDERLINE_TYPE))
		{
			toReturn = new Annotation(RTFConstants.UNDERLINE_TYPE, false, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.ITALIC_UNDERLINE_TYPE))
		{
			toReturn = new Annotation(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE, false, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.UNDERLINE_TYPE))
		{
			toReturn = new Annotation(RTFConstants.BOLD_UNDERLINE_TYPE, false, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.ITALIC_TYPE))
		{
			toReturn = new Annotation(RTFConstants.BOLD_ITALIC_TYPE, false, EMPTY);
		}
		
		return toReturn;
	}
	
	/**
	 * @param annotation
	 * @return
	 */
	private Annotation createItalicToggledAnnotation(Annotation annotation)
	{
		Annotation toReturn = null;
		
		if(annotation.getType().equals(Annotation.TYPE_UNKNOWN))
		{
			toReturn = new Annotation(RTFConstants.ITALIC_TYPE, false, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.BOLD_TYPE))
		{
			toReturn = new Annotation(RTFConstants.BOLD_ITALIC_TYPE, false, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.BOLD_ITALIC_TYPE))
		{
			toReturn = new Annotation(RTFConstants.BOLD_TYPE, false, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.BOLD_UNDERLINE_TYPE))
		{
			toReturn = new Annotation(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE, false, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.ITALIC_UNDERLINE_TYPE))
		{
			toReturn = new Annotation(RTFConstants.UNDERLINE_TYPE, false, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE))
		{
			toReturn = new Annotation(RTFConstants.BOLD_UNDERLINE_TYPE, false, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.UNDERLINE_TYPE))
		{
			toReturn = new Annotation(RTFConstants.ITALIC_UNDERLINE_TYPE, false, EMPTY);
		}
		
		return toReturn;
	}

	/**
	 * @param annotation
	 * @return
	 */
	private Annotation createUnderlineToggledAnnotation(Annotation annotation)
	{
		Annotation toReturn = null;
		
		if(annotation.getType().equals(Annotation.TYPE_UNKNOWN))
		{
			toReturn = new Annotation(RTFConstants.UNDERLINE_TYPE, false, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.BOLD_TYPE))
		{
			toReturn = new Annotation(RTFConstants.BOLD_UNDERLINE_TYPE, false, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.BOLD_ITALIC_TYPE))
		{
			toReturn = new Annotation(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE, false, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.BOLD_UNDERLINE_TYPE))
		{
			toReturn = new Annotation(RTFConstants.BOLD_TYPE, false, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.ITALIC_UNDERLINE_TYPE))
		{
			toReturn = new Annotation(RTFConstants.ITALIC_TYPE, false, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE))
		{
			toReturn = new Annotation(RTFConstants.BOLD_ITALIC_TYPE, false, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.ITALIC_TYPE))
		{
			toReturn = new Annotation(RTFConstants.ITALIC_UNDERLINE_TYPE, false, EMPTY);
		}
		
		return toReturn;
	}
	
	/**
	 * Toggle italics for the text at the given position.
	 * @param position
	 */
	public void toggleItalic(Position position)
	{
		IAnnotationModel model = getAnnotationModel();
		ArrayList<Annotation> current = new ArrayList<Annotation>();
		ArrayList<Position> currentPos = new ArrayList<Position>();
		
		findOverlaps(position, model, current, currentPos);
		
		if(current.size() == 0)
		{
			Annotation annotation = new Annotation(RTFConstants.ITALIC_TYPE, false, EMPTY);
			model.addAnnotation(annotation, position);
		}
		else if(current.size() == 1)
		{
			Map<Position, Annotation> toggle = handleToggle1(model, position, currentPos, current);
			
			for(Position key : toggle.keySet())
			{
				Annotation annotation = createItalicToggledAnnotation(toggle.get(key));
				if(annotation != null)
				{
					model.addAnnotation(annotation, key);
				}
			}
		}
		else
		{
			Map<Position, Annotation> toggle = handleToggle2(current, currentPos, position, model);
			
			for(Position key : toggle.keySet())
			{
				Annotation annotation = createItalicToggledAnnotation(toggle.get(key));
				if(annotation != null)
				{
					model.addAnnotation(annotation, key);
				}
			}
		}

	}

	/**
	 * @param position
	 * @param model
	 * @param current
	 * @param currentPos
	 * @param type 0: bold, 1: italic, -1: underlined
	 */
	@SuppressWarnings("unchecked")
	private void findOverlaps(Position position, IAnnotationModel model, ArrayList<Annotation> current,
			ArrayList<Position> currentPos)
	{
		Iterator<Annotation> iter = model.getAnnotationIterator();
		
		while(iter.hasNext())
		{
			Annotation next = iter.next();
			Position pos = model.getPosition(next);
			
			if(!(next instanceof FragmentAnnotation))
			{
				if(position.overlapsWith(pos.offset, pos.length))
				{
					current.add(next);
					currentPos.add(pos);
					model.removeAnnotation(next);
				}
			}
		}
		if(current.size() > 1)
		{
			sort(current, currentPos, model);
		}
	}
	
	private void sort(ArrayList<Annotation> current, ArrayList<Position> currentPos, final IAnnotationModel model)
	{
		HashMap<Position, Annotation> data = new HashMap<Position, Annotation>();
		
		for(int i = 0; i < current.size(); i++)
		{
			data.put(currentPos.get(i), current.get(i));
		}
		
		Collections.sort(currentPos, new Comparator<Position>()
		{

			@Override
			public int compare(Position o1, Position o2)
			{
				if(o1.offset < o2.offset)
				{
					return -1;
				}
				else if(o2.offset < o1.offset)
				{
					return 1;
				}
				else
				{
					return 0;
				}
			}
			
		});
		
		current.clear();
		for(Position position : currentPos)
		{
			current.add(data.get(position));
		}
		System.out.println();
	}

	/**
	 * Toggle underlining for the text at the given position.
	 * @param position
	 */
	public void toggleUnderline(Position position)
	{
		IAnnotationModel model = getAnnotationModel();
		ArrayList<Annotation> current = new ArrayList<Annotation>();
		ArrayList<Position> currentPos = new ArrayList<Position>();
		
		findOverlaps(position, model, current, currentPos);
		
		if(current.size() == 0)
		{
			Annotation annotation = new Annotation(RTFConstants.UNDERLINE_TYPE, false, EMPTY);
			model.addAnnotation(annotation, position);
		}
		else if(current.size() == 1)
		{
			Map<Position, Annotation> toggle = handleToggle1(model, position, currentPos, current);
			
			for(Position key : toggle.keySet())
			{
				Annotation annotation = createUnderlineToggledAnnotation(toggle.get(key));
				if(annotation != null)
				{
					model.addAnnotation(annotation, key);
				}
			}
		}
		else
		{
			Map<Position, Annotation> toggle = handleToggle2(current, currentPos, position, model);
			
			for(Position key : toggle.keySet())
			{
				Annotation annotation = createUnderlineToggledAnnotation(toggle.get(key));
				if(annotation != null)
				{
					model.addAnnotation(annotation, key);
				}
			}
		}
	}

	/**
	 * @param fragment
	 */
	@SuppressWarnings("unchecked")
	public void markFragment(Fragment fragment)
	{
		IAnnotationModel model = getAnnotationModel();
		Position position = new Position(fragment.getOffset(), fragment.getLength());
		
		Iterator<Annotation> iter = model.getAnnotationIterator();
		
		while(iter.hasNext())
		{
			Annotation annot = iter.next();
			if(annot instanceof FragmentAnnotation)
			{
				Position pos = model.getPosition(annot);
				if(pos.offset == position.offset)
				{
					model.removeAnnotation(annot);
				}
			}
		}
		
		Annotation annotation = new FragmentAnnotation(fragment);
		model.addAnnotation(annotation, position);
	}


}
