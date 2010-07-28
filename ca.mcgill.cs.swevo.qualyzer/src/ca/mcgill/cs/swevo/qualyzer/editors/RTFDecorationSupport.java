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

package ca.mcgill.cs.swevo.qualyzer.editors;

import org.eclipse.jface.text.source.AnnotationPainter;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.AnnotationPainter.ITextStyleStrategy;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;

/**
 * Defines all the painting strategies for our annotations.
 *
 */
public class RTFDecorationSupport extends SourceViewerDecorationSupport implements IPropertyChangeListener
{
	private static ColorManager gManager = new ColorManager();
	private static final Color BLACK = gManager.getColor(ColorManager.DEFAULT);
	
	private static final String BOLD = "BOLD"; //$NON-NLS-1$
	private static final String ITALIC = "ITALIC"; //$NON-NLS-1$
	private static final String BOLD_UNDERLINE = "BOLDUNDERLINE"; //$NON-NLS-1$
	private static final String BOLD_ITALIC = "BOLDITALIC"; //$NON-NLS-1$
	private static final String ITALIC_UNDERLINE = "ITALICUNDERLINE"; //$NON-NLS-1$
	private static final String BOLD_ITALIC_UNDERLINE = "BOLDITALICUNDERLINE"; //$NON-NLS-1$
	
	private static ITextStyleStrategy gBoldStrategy = new ITextStyleStrategy()
	{
		
		@Override
		public void applyTextStyle(StyleRange styleRange, Color annotationColor)
		{
			styleRange.fontStyle = SWT.BOLD;
		}
	};
	
	private static ITextStyleStrategy gItalicStrategy = new ITextStyleStrategy()
	{
		
		@Override
		public void applyTextStyle(StyleRange styleRange, Color annotationColor)
		{
			styleRange.fontStyle = SWT.ITALIC;
		}
	};
	
	private static ITextStyleStrategy gBoldUnderlineStrategy = new ITextStyleStrategy()
	{
		
		@Override
		public void applyTextStyle(StyleRange styleRange, Color annotationColor)
		{
			styleRange.fontStyle = SWT.BOLD;
			styleRange.underline = true;
			styleRange.underlineColor = annotationColor;
		}
	};
	
	private static ITextStyleStrategy gBoldItalicStrategy = new ITextStyleStrategy()
	{
		
		@Override
		public void applyTextStyle(StyleRange styleRange, Color annotationColor)
		{
			styleRange.fontStyle = SWT.BOLD | SWT.ITALIC;
		}
	};
	
	private static ITextStyleStrategy gItalicUnderlineStrategy = new ITextStyleStrategy()
	{
		
		@Override
		public void applyTextStyle(StyleRange styleRange, Color annotationColor)
		{
			styleRange.fontStyle = SWT.ITALIC;
			styleRange.underline = true;
			styleRange.underlineColor = annotationColor;
		}
	};
	
	private static ITextStyleStrategy gBoldItalicUnderlineStrategy = new ITextStyleStrategy()
	{
		
		@Override
		public void applyTextStyle(StyleRange styleRange, Color annotationColor)
		{
			styleRange.fontStyle = SWT.BOLD | SWT.ITALIC;
			styleRange.underline = true;
			styleRange.underlineColor = annotationColor;
		}
	};
	
	private AnnotationPainter fPainter;

	/**
	 * @param sourceViewer
	 * @param overviewRuler
	 * @param annotationAccess
	 * @param sharedTextColors
	 */
	public RTFDecorationSupport(ISourceViewer sourceViewer, IOverviewRuler overviewRuler,
			IAnnotationAccess annotationAccess, ISharedTextColors sharedTextColors)
	{
		super(sourceViewer, overviewRuler, annotationAccess, sharedTextColors);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.SourceViewerDecorationSupport#createAnnotationPainter()
	 */
	@Override
	protected AnnotationPainter createAnnotationPainter()
	{
		fPainter =  super.createAnnotationPainter();
		
		fPainter.addTextStyleStrategy(BOLD, gBoldStrategy);
		fPainter.addTextStyleStrategy(ITALIC, gItalicStrategy);
		fPainter.addTextStyleStrategy(BOLD_ITALIC, gBoldItalicStrategy);
		fPainter.addTextStyleStrategy(BOLD_UNDERLINE, gBoldUnderlineStrategy);
		fPainter.addTextStyleStrategy(ITALIC_UNDERLINE, gItalicUnderlineStrategy);
		fPainter.addTextStyleStrategy(BOLD_ITALIC_UNDERLINE, gBoldItalicUnderlineStrategy);
		//painter.addTextStyleStrategy(MARK_FRAGMENT, gMarkFragmentStrategy);
		
		fPainter.addAnnotationType(RTFConstants.BOLD_TYPE, BOLD);
		fPainter.addAnnotationType(RTFConstants.ITALIC_TYPE, ITALIC);
		fPainter.addAnnotationType(RTFConstants.BOLD_ITALIC_TYPE, BOLD_ITALIC);
		fPainter.addAnnotationType(RTFConstants.BOLD_UNDERLINE_TYPE, BOLD_UNDERLINE);
		fPainter.addAnnotationType(RTFConstants.ITALIC_UNDERLINE_TYPE, ITALIC_UNDERLINE);
		fPainter.addAnnotationType(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE, BOLD_ITALIC_UNDERLINE);
		fPainter.addHighlightAnnotationType(RTFConstants.FRAGMENT_TYPE);
		
		fPainter.setAnnotationTypeColor(RTFConstants.BOLD_TYPE, BLACK);
		fPainter.setAnnotationTypeColor(RTFConstants.ITALIC_TYPE, BLACK);
		fPainter.setAnnotationTypeColor(RTFConstants.BOLD_ITALIC_TYPE, BLACK);
		fPainter.setAnnotationTypeColor(RTFConstants.BOLD_UNDERLINE_TYPE, BLACK);
		fPainter.setAnnotationTypeColor(RTFConstants.ITALIC_UNDERLINE_TYPE, BLACK);
		fPainter.setAnnotationTypeColor(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE, BLACK);
		
		Color color = getFragmentColor();
		fPainter.setAnnotationTypeColor(RTFConstants.FRAGMENT_TYPE, color);
		
		QualyzerActivator.getDefault().getPreferenceStore().addPropertyChangeListener(this);
		return fPainter;
	}

	/**
	 * @return
	 */
	private Color getFragmentColor()
	{
		String rgbString = QualyzerActivator.getDefault().getPreferenceStore().getString("FragmentColor");
		String[] parts = rgbString.split(",");
		RGB rgb = new RGB(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
		Color color = gManager.getColor(rgb);
		return color;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.SourceViewerDecorationSupport#dispose()
	 */
	@Override
	public void dispose()
	{
		QualyzerActivator.getDefault().getPreferenceStore().removePropertyChangeListener(this);
		gManager.dispose();
		super.dispose();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		if(event.getProperty().equals("FragmentColor"))
		{
			Color color = getFragmentColor();
			
			fPainter.setAnnotationTypeColor(RTFConstants.FRAGMENT_TYPE, color);
			fPainter.paint(AnnotationPainter.CONFIGURATION);
		}
		
	}

}
