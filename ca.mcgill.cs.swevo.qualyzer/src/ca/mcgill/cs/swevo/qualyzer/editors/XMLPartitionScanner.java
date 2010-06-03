/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     McGill University - initial API and implementation
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.editors;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * 
 * This will be replaced by a simpler IPartitionTokenScanner generating tokens for each fragments (and non-fragment) of the text.
 * A token contains data: this data is the name of the partition in the case of partition scanners.
 * Refresh will be interesting though: is everything repartitioned? how can we ask for a partial refresh? 
 * This could probably be done by firing a DocumentChanged event with the text that was coded...
 * 
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 * 
 */
public class XMLPartitionScanner extends RuleBasedPartitionScanner
{
	public static final String XML_COMMENT = "__xml_comment"; //$NON-NLS-1$
	public static final String XML_TAG = "__xml_tag"; //$NON-NLS-1$

	public XMLPartitionScanner()
	{

		IToken xmlComment = new Token(XML_COMMENT);
		IToken tag = new Token(XML_TAG);

		IPredicateRule[] rules = new IPredicateRule[2];

		rules[0] = new MultiLineRule("<!--", "-->", xmlComment); //$NON-NLS-1$ //$NON-NLS-2$
		rules[1] = new TagRule(tag);

		setPredicateRules(rules);
	}
}
