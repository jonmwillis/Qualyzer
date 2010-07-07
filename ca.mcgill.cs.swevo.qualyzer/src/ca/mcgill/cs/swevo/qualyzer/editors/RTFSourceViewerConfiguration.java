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

import org.eclipse.jface.text.DefaultTextHover;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.graphics.Point;

import ca.mcgill.cs.swevo.qualyzer.model.CodeEntry;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;

/**
 *
 */
public class RTFSourceViewerConfiguration extends SourceViewerConfiguration
{
	
//	/* (non-Javadoc)
//	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getTextHover(
//	 * org.eclipse.jface.text.source.ISourceViewer, java.lang.String)
//	 */
//	@Override
//	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType)
//	{
//		return new ITextHover(){
//
//			@Override
//			public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion)
//			{
//				System.out.println("here");
//				if(textViewer instanceof RTFSourceViewer)
//				{				System.out.println("here");
//
//					IAnnotationModel model = ((RTFSourceViewer) textViewer).getAnnotationModel();
//					Iterator<Annotation> iter = model.getAnnotationIterator();
//					while(iter.hasNext())
//					{
//						Annotation annotation = iter.next();
//						if(annotation instanceof FragmentAnnotation)
//						{
//							Position position = model.getPosition(annotation);
//							if(position.offset <= hoverRegion.getOffset() && 
//									position.offset + position.length >= hoverRegion.getOffset())
//							{
//								String toReturn = "";
//								Fragment fragment = ((FragmentAnnotation) annotation).getFragment();
//								for(CodeEntry entry : fragment.getCodeEntries())
//								{
//									toReturn += entry.getCode().getCodeName() + ", ";
//								}
//								
//								return toReturn.isEmpty() ? null : toReturn.substring(0, toReturn.length() - 2);
//							}
//						}
//					}
//				}
//				return null;
//			}
//
//			@Override
//			public IRegion getHoverRegion(ITextViewer textViewer, int offset)
//			{	
//				System.out.println("print");
//				Point selection = textViewer.getSelectedRange();
//				if(selection.x <= offset && selection.x + selection.y >= offset)
//				{
//					return new Region(selection.x, selection.y);
//				}
//				return new Region(offset, 0);
//			}
//			
//		};
//	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getTextHover(org.eclipse.jface.text.source.ISourceViewer, java.lang.String)
	 */
	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType)
	{
		return new DefaultTextHover(sourceViewer);
	}
}
