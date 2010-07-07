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
import java.util.Iterator;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
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
			Annotation annotation = new Annotation(RTFConstants.BOLD_TYPE, true, EMPTY);
			model.addAnnotation(annotation, position);
		}
		else if(current.size() == 1 && currentPos.get(0).offset == position.offset && 
				currentPos.get(0).length == position.length)
		{
			Annotation annotation = createBoldToggledAnnotation(current.get(0));
			if(annotation != null)
			{
				model.addAnnotation(annotation, position);
			}
		}
	}
	
	/**
	 * @param annotation
	 * @return
	 */
	private Annotation createBoldToggledAnnotation(Annotation annotation)
	{
		Annotation toReturn = null;
		
		if(annotation.getType().equals(RTFConstants.BOLD_ITALIC_TYPE))
		{
			toReturn = new Annotation(RTFConstants.ITALIC_TYPE, true, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE))
		{
			toReturn = new Annotation(RTFConstants.ITALIC_UNDERLINE_TYPE, true, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.BOLD_UNDERLINE_TYPE))
		{
			toReturn = new Annotation(RTFConstants.UNDERLINE_TYPE, true, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.ITALIC_UNDERLINE_TYPE))
		{
			toReturn = new Annotation(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE, true, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.UNDERLINE_TYPE))
		{
			toReturn = new Annotation(RTFConstants.BOLD_UNDERLINE_TYPE, true, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.ITALIC_TYPE))
		{
			toReturn = new Annotation(RTFConstants.BOLD_ITALIC_TYPE, true, EMPTY);
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
		
		if(annotation.getType().equals(RTFConstants.BOLD_TYPE))
		{
			toReturn = new Annotation(RTFConstants.BOLD_ITALIC_TYPE, true, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.BOLD_ITALIC_TYPE))
		{
			toReturn = new Annotation(RTFConstants.BOLD_TYPE, true, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.BOLD_UNDERLINE_TYPE))
		{
			toReturn = new Annotation(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE, true, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.ITALIC_UNDERLINE_TYPE))
		{
			toReturn = new Annotation(RTFConstants.UNDERLINE_TYPE, true, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE))
		{
			toReturn = new Annotation(RTFConstants.BOLD_UNDERLINE_TYPE, true, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.UNDERLINE_TYPE))
		{
			toReturn = new Annotation(RTFConstants.ITALIC_UNDERLINE_TYPE, true, EMPTY);
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
		
		if(annotation.getType().equals(RTFConstants.BOLD_TYPE))
		{
			toReturn = new Annotation(RTFConstants.BOLD_UNDERLINE_TYPE, true, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.BOLD_ITALIC_TYPE))
		{
			toReturn = new Annotation(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE, true, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.BOLD_UNDERLINE_TYPE))
		{
			toReturn = new Annotation(RTFConstants.BOLD_TYPE, true, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.ITALIC_UNDERLINE_TYPE))
		{
			toReturn = new Annotation(RTFConstants.ITALIC_TYPE, true, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE))
		{
			toReturn = new Annotation(RTFConstants.BOLD_ITALIC_TYPE, true, EMPTY);
		}
		else if(annotation.getType().equals(RTFConstants.ITALIC_TYPE))
		{
			toReturn = new Annotation(RTFConstants.ITALIC_UNDERLINE_TYPE, true, EMPTY);
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
			Annotation annotation = new Annotation(RTFConstants.ITALIC_TYPE, true, EMPTY);
			model.addAnnotation(annotation, position);
		}
		else if(current.size() == 1 && currentPos.get(0).offset == position.offset && 
				currentPos.get(0).length == position.length)
		{
			Annotation annotation = createItalicToggledAnnotation(current.get(0));
			if(annotation != null)
			{
				model.addAnnotation(annotation, position);
			}
		}
	}

	/**
	 * @param position
	 * @param model
	 * @param current
	 * @param currentPos
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
			
			if(next.getType().equals(RTFConstants.FRAGMENT_TYPE))
			{
				continue;
			}
			
			if(pos.offset <= position.offset && position.offset <= pos.offset + pos.length)
			{
				current.add(next);
				currentPos.add(pos);
				model.removeAnnotation(next);
			}
			else if(pos.offset <= position.offset + position.length && 
					position.offset + position.length <= pos.offset + pos.length)
			{
				current.add(next);
				currentPos.add(pos);
				model.removeAnnotation(next);
			}
			else if(position.offset <= pos.offset && pos.offset <= position.offset + position.length)
			{
				current.add(next);
				currentPos.add(pos);
				model.removeAnnotation(next);
			}
			else if(position.offset <= pos.offset + pos.length && 
					pos.offset + pos.length <= position.offset + position.length)
			{
				current.add(next);
				currentPos.add(pos);
				model.removeAnnotation(next);
			}
		}
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
			Annotation annotation = new Annotation(RTFConstants.UNDERLINE_TYPE, true, EMPTY);
			model.addAnnotation(annotation, position);
		}
		else if(current.size() == 1 && currentPos.get(0).offset == position.offset && 
				currentPos.get(0).length == position.length)
		{
			Annotation annotation = createUnderlineToggledAnnotation(current.get(0));
			if(annotation != null)
			{
				model.addAnnotation(annotation, position);
			}
		}
	}

	/**
	 * @param fragment
	 */
	public void markFragment(Fragment fragment)
	{
		IAnnotationModel model = getAnnotationModel();
		
		Position position = new Position(fragment.getOffset(), fragment.getLength());
		Annotation annotation = new FragmentAnnotation(fragment);
		model.addAnnotation(annotation, position);
	}


}
