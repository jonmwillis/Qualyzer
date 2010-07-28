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
public class RTFDecorationSupport extends SourceViewerDecorationSupport
{
	private static ColorManager gManager = new ColorManager();
	private static final Color BLACK = gManager.getColor(ColorManager.DEFAULT);
	
	private static final String BOLD = "BOLD"; //$NON-NLS-1$
	private static final String ITALIC = "ITALIC"; //$NON-NLS-1$
	private static final String BOLD_UNDERLINE = "BOLDUNDERLINE"; //$NON-NLS-1$
	private static final String BOLD_ITALIC = "BOLDITALIC"; //$NON-NLS-1$
	private static final String ITALIC_UNDERLINE = "ITALICUNDERLINE"; //$NON-NLS-1$
	private static final String BOLD_ITALIC_UNDERLINE = "BOLDITALICUNDERLINE"; //$NON-NLS-1$
	private static final String MARK_FRAGMENT = "MARK_FRAGMENT";

	
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
	
	private static ITextStyleStrategy gMarkFragmentStrategy = new ITextStyleStrategy()
	{

		@Override
		public void applyTextStyle(StyleRange styleRange, Color annotationColor)
		{
			String rgbString = QualyzerActivator.getDefault().getPreferenceStore().getString("FragmentColor");
			String[] parts = rgbString.split(",");
			RGB rgb = new RGB(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
			Color color = gManager.getColor(rgb);
			styleRange.background = color;
		}
	};

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
		AnnotationPainter painter =  super.createAnnotationPainter();
		
		painter.addTextStyleStrategy(BOLD, gBoldStrategy);
		painter.addTextStyleStrategy(ITALIC, gItalicStrategy);
		painter.addTextStyleStrategy(BOLD_ITALIC, gBoldItalicStrategy);
		painter.addTextStyleStrategy(BOLD_UNDERLINE, gBoldUnderlineStrategy);
		painter.addTextStyleStrategy(ITALIC_UNDERLINE, gItalicUnderlineStrategy);
		painter.addTextStyleStrategy(BOLD_ITALIC_UNDERLINE, gBoldItalicUnderlineStrategy);
		painter.addTextStyleStrategy(MARK_FRAGMENT, gMarkFragmentStrategy);
		
		painter.addAnnotationType(RTFConstants.BOLD_TYPE, BOLD);
		painter.addAnnotationType(RTFConstants.ITALIC_TYPE, ITALIC);
		painter.addAnnotationType(RTFConstants.BOLD_ITALIC_TYPE, BOLD_ITALIC);
		painter.addAnnotationType(RTFConstants.BOLD_UNDERLINE_TYPE, BOLD_UNDERLINE);
		painter.addAnnotationType(RTFConstants.ITALIC_UNDERLINE_TYPE, ITALIC_UNDERLINE);
		painter.addAnnotationType(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE, BOLD_ITALIC_UNDERLINE);
		painter.addAnnotationType(RTFConstants.FRAGMENT_TYPE, MARK_FRAGMENT);
		
		painter.setAnnotationTypeColor(RTFConstants.BOLD_TYPE, BLACK);
		painter.setAnnotationTypeColor(RTFConstants.ITALIC_TYPE, BLACK);
		painter.setAnnotationTypeColor(RTFConstants.BOLD_ITALIC_TYPE, BLACK);
		painter.setAnnotationTypeColor(RTFConstants.BOLD_UNDERLINE_TYPE, BLACK);
		painter.setAnnotationTypeColor(RTFConstants.ITALIC_UNDERLINE_TYPE, BLACK);
		painter.setAnnotationTypeColor(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE, BLACK);
		painter.setAnnotationTypeColor(RTFConstants.FRAGMENT_TYPE, BLACK);

		return painter;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.SourceViewerDecorationSupport#dispose()
	 */
	@Override
	public void dispose()
	{
		gManager.dispose();
		super.dispose();
	}

}
