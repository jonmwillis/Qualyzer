/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Barthelemy Dagenais
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

/**
 */
public class XMLConfiguration extends SourceViewerConfiguration
{
	private XMLDoubleClickStrategy fDoubleClickStrategy;
	private XMLTagScanner fTagScanner;
	private XMLScanner fScanner;
	private ColorManager fColorManager;

	/**
	 * @param colorManager
	 */
	public XMLConfiguration(ColorManager colorManager)
	{
		this.fColorManager = colorManager;
	}

	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer)
	{
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE, XMLPartitionScanner.XML_COMMENT,
				XMLPartitionScanner.XML_TAG };
	}

	@Override
	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType)
	{
		if (fDoubleClickStrategy == null)
		{
			fDoubleClickStrategy = new XMLDoubleClickStrategy();
		}
		return fDoubleClickStrategy;
	}

	/**
	 * @return
	 */
	protected XMLScanner getXMLScanner()
	{
		if (fScanner == null)
		{
			fScanner = new XMLScanner(fColorManager);
			fScanner.setDefaultReturnToken(new Token(new TextAttribute(fColorManager
					.getColor(ColorManager.DEFAULT))));
		}
		return fScanner;
	}

	/**
	 * @return
	 */
	protected XMLTagScanner getXMLTagScanner()
	{
		if (fTagScanner == null)
		{
			fTagScanner = new XMLTagScanner(fColorManager);
			fTagScanner.setDefaultReturnToken(new Token(new TextAttribute(fColorManager
					.getColor(ColorManager.TAG))));
		}
		return fTagScanner;
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer)
	{
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getXMLTagScanner());
		reconciler.setDamager(dr, XMLPartitionScanner.XML_TAG);
		reconciler.setRepairer(dr, XMLPartitionScanner.XML_TAG);

		dr = new DefaultDamagerRepairer(getXMLScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
		
		// This is probably what we will need to use. 
		// A partition scanner will partition the document in coded fragments
		// Coded fragments will be assigned to a color.
		NonRuleBasedDamagerRepairer ndr = new NonRuleBasedDamagerRepairer(new TextAttribute(fColorManager
				.getColor(ColorManager.XML_COMMENT)));
		reconciler.setDamager(ndr, XMLPartitionScanner.XML_COMMENT);
		reconciler.setRepairer(ndr, XMLPartitionScanner.XML_COMMENT);

		return reconciler;
	}

}