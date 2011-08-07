/*******************************************************************************
 * Copyright (c) 2011 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Barthelemy Dagenais
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.editors;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.mcgill.cs.swevo.qualyzer.editors.inputs.RTFEditorInput;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;
import ca.mcgill.cs.swevo.qualyzer.model.IAnnotatedDocument;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.*;

/**
 * The DocumentProvider for our editor. If you need to parse a document for some other task (without opening the editor)
 * do the following.
 * 
 * RTFDocumentProvider provider = new RTFDocumentProvider(); RTFEditorInput input = new RTFEditorInput(file, document);
 * IDocument parsedDocument = provider.getCreatedDocument(input);
 * 
 * String paresedText = parsedDocument.getText();
 * 
 */
public class RTFDocumentProvider2 extends FileDocumentProvider
{

	private static Logger gLogger = LoggerFactory.getLogger(RTFDocumentProvider2.class);

	private static final String EMPTY = ""; //$NON-NLS-1$

	/**
	 * Exists only for use by things that need parsed documents, but that don't want to open the editor.
	 * 
	 * @param element
	 *            The editor input that will be used to create the document.
	 * @return The parsed document.
	 */
	public IDocument getCreatedDocument(Object element)
	{
		try
		{
			return createDocument(element);
		}
		catch (CoreException e)
		{
			gLogger.error("DocumentProvider: Failed to create document.", e); //$NON-NLS-1$
		}
		return null;
	}

	/**
	 * Given the RTFEditorInput creates the document and then attaches all the fragments to the document.
	 */
	@Override
	protected IDocument createDocument(Object element) throws CoreException
	{
		RTFDocument doc = (RTFDocument) super.createDocument(element);

		IAnnotatedDocument document = ((RTFEditorInput) element).getDocument();

		for (Fragment fragment : document.getFragments().values())
		{
			Position position = new Position(fragment.getOffset(), fragment.getLength());
			doc.addAnnotation(position, new FragmentAnnotation(fragment));
		}

		return doc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.editors.text.StorageDocumentProvider#createEmptyDocument()
	 */
	@Override
	protected IDocument createEmptyDocument()
	{
		return new RTFDocument();
	}

	@Override
	protected void setDocumentContent(IDocument document, InputStream contentStream, String encoding)
			throws CoreException
	{
		RTFDocument rtfDocument = (RTFDocument) document;
		StringBuilder text = new StringBuilder();
		Set<String> currentTags = new HashSet<String>();
		Stack<Set<String>> state = new Stack<Set<String>>();
		state.push(currentTags);
		boolean printSpace = true;

		try
		{
			int c = contentStream.read();
			while (c != -1)
			{
				char ch = (char) c;
				if (ch == BACKSLASH)
				{
					ParserPair pair = handleControl(contentStream);
					printSpace = handleSubControl(pair.fString, text, safeState(state));
				}
				else if (ch == LEFT_BRACE)
				{

				}
				else if (ch == RIGHT_BRACE)
				{

					c = contentStream.read();
				}
				else
				{
					if (!in(ch, SPACES))
					{
						text.append(ch);
						printSpace = true;
					}
					else if (equals(ch, SPACE_CHAR) && printSpace)
					{
						text.append(ch);
					}
					c = contentStream.read();
				}

			}

		}
		catch (Exception e)
		{
			gLogger.error("Error while parsing a rtf file.", e); //$NON-NLS-1$
		}

		rtfDocument.set(text.toString());
	}

	private boolean handleSubControl(String fString, StringBuilder text, Set<String> safeState)
	{
		return false;
	}

	private ParserPair handleControl(InputStream contentStream) throws IOException
	{
		int c = contentStream.read();
		return handleControl(contentStream, c, EMPTY);
	}

	private ParserPair handleControl(InputStream contentStream, int startChar, String startControl)
			throws IOException
	{
		StringBuilder controlWord = new StringBuilder(startControl);
		int c = startChar;
		char ch;
		
		while (c != -1)
		{
			ch = (char) c;
			if (Character.isLetter(ch))
			{
				// Start of a control word
				controlWord.append(ch);
			}
			else if (ch == ESCAPE_8BIT && isEmpty(controlWord))
			{
				// This is an escaped 8bit char
				controlWord.append(ch);
			}
			else if (ch == UNICODE && isEmpty(controlWord))
			{
				// This is potentially an unicode char
				ParserPair pair = getUnicode(contentStream);
				c = pair.fChar;
				controlWord = new StringBuilder(pair.fString);
				break;
			}
			else if (Character.isDigit(ch))
			{
				// Unit of control word
				controlWord.append(ch);
			}
			else if (ch == MINUS)
			{
				controlWord.append(ch);
			}
			else
			{
				if (isEmpty(controlWord))
				{
					controlWord.append(ch);
					c = contentStream.read();
				}
				break;
			}
			c = contentStream.read();
		}
		
		
		return new ParserPair(c, controlWord.toString());
	}
	
	private ParserPair getUnicode(InputStream contentStream)
	{
		return null;
	}

	private boolean isEmpty(StringBuilder builder)
	{
		return builder.toString().isEmpty();
	}

	private Set<String> safeState(Stack<Set<String>> state)
	{
		return safeState(state, false);
	}

	/**
	 * Returns the set of tags at the top of the stack. Return an empty set if the stack is empty. This should never
	 * occur, but some badly-formatted RTF documents seem to lead to this situation.
	 * 
	 * @param state
	 * @param pop
	 * @return
	 */
	private Set<String> safeState(Stack<Set<String>> state, boolean pop)
	{
		if (state.isEmpty())
		{
			if (pop)
			{
				return state.pop();
			}
			else
			{
				return state.peek();
			}
		}
		else
		{
			gLogger.error("State was empty.");
			return new HashSet<String>();
		}
	}

	private boolean equals(char c, String s)
	{
		return s.charAt(0) == c;
	}

	private boolean equals(String s1, String s2)
	{
		return s2.equals(s1);
	}

	private boolean in(char c, String[] array)
	{
		String s = String.valueOf(c);
		for (String element : array)
		{
			if (s.equals(element))
			{
				return true;
			}
		}
		return false;
	}
}

/**
 * 
 *
 */
class ParserPair
{
	public final int fChar;

	public final String fString;

	public ParserPair(int ch, String str)
	{
		fChar = ch;
		fString = str;
	}
}
