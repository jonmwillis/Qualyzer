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

import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.BACKSLASH;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.BOLD_END;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.BOLD_START;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.ESCAPE_8BIT;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.ESCAPE_CONTROLS;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.ITALIC_END;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.ITALIC_START;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.LEFT_BRACE;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.MINUS;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.NEW_LINE;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.NEW_LINE_CHAR;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.RESET;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.RIGHT_BRACE;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.SPACES;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.SPACE_CHAR;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.TAB;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.TAB_CHAR;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.UNDERLINE_END;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.UNDERLINE_START;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.UNICODE;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.UNICODE_COUNT;
import static ca.mcgill.cs.swevo.qualyzer.editors.RTFTags.UNICODE_COUNT_FULL;
import static ca.mcgill.cs.swevo.qualyzer.util.ParserUtil.equal;
import static ca.mcgill.cs.swevo.qualyzer.util.ParserUtil.getDefault;
import static ca.mcgill.cs.swevo.qualyzer.util.ParserUtil.in;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.mcgill.cs.swevo.qualyzer.editors.inputs.RTFEditorInput;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;
import ca.mcgill.cs.swevo.qualyzer.model.IAnnotatedDocument;

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

	private static final int HEX_RADIX = 16;

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

	/**
	 * This is the main loop.
	 */
	@Override
	protected void setDocumentContent(IDocument document, InputStream contentStream, String encoding)
			throws CoreException
	{
		RTFDocument rtfDocument = (RTFDocument) document;
		StringBuilder text = new StringBuilder();
		Map<String, Integer> currentTags = new HashMap<String, Integer>();
		Stack<Map<String, Integer>> state = new Stack<Map<String, Integer>>();
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
					printSpace = handleSubControl(pair.fString, text, safeState(state), rtfDocument);
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
					else if (equal(ch, SPACE_CHAR) && printSpace)
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

	private boolean handleSubControl(String control, StringBuilder text, Map<String, Integer> state,
			RTFDocument document)
	{
		boolean printSpace = false;

		if (control.equals(BOLD_START) && !state.containsKey(BOLD_START))
		{
			startBold(document, text, state);
		}
		else if (control.equals(BOLD_END) && state.containsKey(BOLD_START))
		{
			endBold(document, text, state);
		}
		else if (control.equals(ITALIC_START) && !state.containsKey(ITALIC_START))
		{
			startItalic(document, text, state);
		}
		else if (control.equals(ITALIC_END) && state.containsKey(ITALIC_START))
		{
			endItalic(document, text, state);
		}
		else if (control.equals(UNDERLINE_START) && !state.containsKey(UNDERLINE_START))
		{
			startUnderline(document, text, state);
		}
		else if (control.equals(UNDERLINE_END) && state.containsKey(UNDERLINE_START))
		{
			endUnderline(document, text, state);
		}
		else if (control.equals(NEW_LINE))
		{
			text.append(NEW_LINE_CHAR);
		}
		else if (control.equals(TAB))
		{
			text.append(TAB_CHAR);
		}
		else if (in(control, RESET))
		{
			handleSubControl(BOLD_END, text, state, document);
			handleSubControl(ITALIC_END, text, state, document);
			handleSubControl(UNDERLINE_END, text, state, document);
		}
		else if (in(control, ESCAPE_CONTROLS))
		{
			text.append(control);
			printSpace = true;
		}
		else if (control.charAt(0) == ESCAPE_8BIT)
		{
			text.append(get8bit(control.substring(1)));
			printSpace = true;
		}
		else if (control.equals(UNICODE_COUNT_FULL))
		{
			// Do nothing for now. really!
		}
		else if (isUnicode(control))
		{
			ParserPair unicode = parseUnicode(control.substring(1));
			int unicodeNumber = Integer.parseInt(unicode.fString);
			char unicodeChar = (char) unicodeNumber;
			text.append(unicodeChar);
			printSpace = true;
		}
		return printSpace;
	}

	private ParserPair parseUnicode(String unicodeStr)
	{
		StringBuilder number = new StringBuilder();
		int size = unicodeStr.length();
		for (int i = 0; i < size; i++)
		{
			char ch = unicodeStr.charAt(i);
			if (Character.isDigit(ch))
			{
				number.append(ch);
			}
			else
			{
				// For now, we don't care about the replacement...
				break;
			}
		}

		return new ParserPair(-1, number.toString());
	}

	private boolean isUnicode(String control)
	{
		return control.length() > 1 && control.charAt(0) == UNICODE && Character.isDigit(control.charAt(1));
	}

	private char get8bit(String numberStr)
	{
		int c = Integer.parseInt(numberStr, HEX_RADIX);
		return (char) c;
	}

	private ParserPair handleControl(InputStream contentStream) throws IOException
	{
		int c = contentStream.read();
		return handleControl(contentStream, c, EMPTY);
	}

	private ParserPair handleControl(InputStream contentStream, int startChar, String startControl) throws IOException
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

	private ParserPair getUnicode(InputStream contentStream) throws IOException
	{
		StringBuilder control = new StringBuilder();
		int c = contentStream.read();
		if (c != -1)
		{
			char ch = (char) c;
			if (ch == UNICODE_COUNT)
			{
				ParserPair number = getNumber(contentStream);
				control.append(UNICODE_COUNT_FULL);
				control.append(number.fString);
				c = number.fChar;
			}
			else if (!Character.isDigit(ch))
			{
				ParserPair result = handleControl(contentStream, c, String.valueOf(UNICODE));
				c = result.fChar;
				control = new StringBuilder(result.fString);
			}
			else
			{
				ParserPair number = getNumber(contentStream);
				int replacement = number.fChar;
				String replch = String.valueOf((char) replacement);
				if (equal(BACKSLASH, replch))
				{
					// This is a 8 bit
					ParserPair repl8bit = handleControl(contentStream);
					c = repl8bit.fChar;
					replch = repl8bit.fString;
				}
				else
				{
					// This was a 7 bit character
					c = contentStream.read();
				}
				control.append(UNICODE);
				control.append(number.fString);
				control.append(replch);
			}
		}

		return new ParserPair(c, control.toString());
	}

	private ParserPair getNumber(InputStream contentStream) throws IOException
	{
		StringBuilder number = new StringBuilder();
		int c = contentStream.read();
		while (c != -1)
		{
			char ch = (char) c;
			if (Character.isDigit(ch))
			{
				number.append(ch);
			}
			else
			{
				break;
			}
		}
		return new ParserPair(c, number.toString());
	}

	private boolean isEmpty(StringBuilder builder)
	{
		return builder.toString().isEmpty();
	}

	private Map<String, Integer> safeState(Stack<Map<String, Integer>> state)
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
	private Map<String, Integer> safeState(Stack<Map<String, Integer>> state, boolean pop)
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
			return new HashMap<String, Integer>();
		}
	}

	/**
	 * @param document
	 * @param currentText
	 */
	private void startBold(RTFDocument document, StringBuilder currentText, Map<String, Integer> state)
	{
		if (state.containsKey(BOLD_START))
		{
			return;
		}

		int boldPos = currentText.length();
		int italicPos = getDefault(state, ITALIC_START, -1);
		int underlinePos = getDefault(state, UNDERLINE_START, -1);

		if (italicPos != -1 && underlinePos != -1 && italicPos != boldPos)
		{
			Annotation annotation = new Annotation(RTFConstants.ITALIC_UNDERLINE_TYPE, true, EMPTY);
			Position position = new Position(italicPos, boldPos - italicPos);

			document.addAnnotation(position, annotation);
			italicPos = boldPos;
			underlinePos = boldPos;
		}
		else if (italicPos != -1 && italicPos != boldPos)
		{
			Annotation annotation = new Annotation(RTFConstants.ITALIC_TYPE, true, EMPTY);
			Position position = new Position(italicPos, boldPos - italicPos);

			document.addAnnotation(position, annotation);
			italicPos = boldPos;
		}
		else if (underlinePos != -1 && underlinePos != boldPos)
		{
			Annotation annotation = new Annotation(RTFConstants.UNDERLINE_TYPE, true, EMPTY);
			Position position = new Position(underlinePos, boldPos - underlinePos);

			document.addAnnotation(position, annotation);
			underlinePos = boldPos;
		}

		// Save state
		state.put(BOLD_START, boldPos);
		if (italicPos != -1)
		{
			state.put(ITALIC_START, italicPos);
		}
		if (underlinePos != -1)
		{
			state.put(UNDERLINE_START, underlinePos);
		}
	}

	/**
	 * @param document
	 * @param currentText
	 */
	private void endBold(RTFDocument document, StringBuilder currentText, Map<String, Integer> state)
	{
		if (!state.containsKey(BOLD_START))
		{
			return;
		}

		int boldPos = currentText.length();
		int italicPos = getDefault(state, ITALIC_START, -1);
		int underlinePos = getDefault(state, UNDERLINE_START, -1);

		Annotation annotation;
		int curPos = currentText.length();
		Position position = new Position(boldPos, curPos - boldPos);

		if (italicPos != -1 && underlinePos != -1)
		{
			annotation = new Annotation(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE, true, EMPTY);
			italicPos = curPos;
			underlinePos = curPos;
			state.put(ITALIC_START, italicPos);
			state.put(UNDERLINE_START, underlinePos);
		}
		else if (italicPos != -1)
		{
			annotation = new Annotation(RTFConstants.BOLD_ITALIC_TYPE, true, EMPTY);
			italicPos = curPos;
			state.put(ITALIC_START, italicPos);
		}
		else if (underlinePos != -1)
		{
			annotation = new Annotation(RTFConstants.BOLD_UNDERLINE_TYPE, true, EMPTY);
			underlinePos = curPos;
			state.put(UNDERLINE_START, underlinePos);
		}
		else
		{
			annotation = new Annotation(RTFConstants.BOLD_TYPE, true, EMPTY);
		}

		if (position.length > 0)
		{
			document.addAnnotation(position, annotation);
		}

		state.remove(BOLD_START);
	}

	/**
	 * @param document
	 * @param currentText
	 */
	private void endUnderline(RTFDocument document, StringBuilder currentText, Map<String, Integer> state)
	{
		if (!state.containsKey(UNDERLINE_START))
		{
			return;
		}

		int underlinePos = currentText.length();
		int italicPos = getDefault(state, ITALIC_START, -1);
		int boldPos = getDefault(state, BOLD_START, -1);

		Annotation annotation;
		int curPos = currentText.length();
		Position position = new Position(underlinePos, curPos - underlinePos);

		if (boldPos != -1 && italicPos != -1)
		{
			annotation = new Annotation(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE, true, EMPTY);
			boldPos = curPos;
			italicPos = curPos;
			state.put(BOLD_START, boldPos);
			state.put(ITALIC_START, italicPos);
		}
		else if (boldPos != -1)
		{
			annotation = new Annotation(RTFConstants.BOLD_UNDERLINE_TYPE, true, EMPTY);
			boldPos = curPos;
			state.put(BOLD_START, boldPos);
		}
		else if (italicPos != -1)
		{
			annotation = new Annotation(RTFConstants.ITALIC_UNDERLINE_TYPE, true, EMPTY);
			italicPos = curPos;
			state.put(ITALIC_START, italicPos);
		}
		else
		{
			annotation = new Annotation(RTFConstants.UNDERLINE_TYPE, true, EMPTY);
		}

		if (position.length > 0)
		{
			document.addAnnotation(position, annotation);
		}

		state.remove(UNDERLINE_START);
	}

	/**
	 * @param document
	 * @param currentText
	 */
	private void startUnderline(RTFDocument document, StringBuilder currentText, Map<String, Integer> state)
	{
		if (state.containsKey(UNDERLINE_START))
		{
			return;
		}

		int underlinePos = currentText.length();
		int italicPos = getDefault(state, ITALIC_START, -1);
		int boldPos = getDefault(state, BOLD_START, -1);

		if (boldPos != -1 && italicPos != -1 && boldPos != underlinePos)
		{
			Annotation annotation = new Annotation(RTFConstants.BOLD_ITALIC_TYPE, true, EMPTY);
			Position position = new Position(boldPos, underlinePos - boldPos);

			document.addAnnotation(position, annotation);
			boldPos = underlinePos;
			italicPos = underlinePos;
		}
		else if (boldPos != -1 && boldPos != underlinePos)
		{
			Annotation annotation = new Annotation(RTFConstants.BOLD_TYPE, true, EMPTY);
			Position position = new Position(boldPos, underlinePos - boldPos);

			document.addAnnotation(position, annotation);
			boldPos = underlinePos;
		}
		else if (italicPos != -1 && italicPos != underlinePos)
		{
			Annotation annotation = new Annotation(RTFConstants.ITALIC_TYPE, true, EMPTY);
			Position position = new Position(italicPos, underlinePos - italicPos);

			document.addAnnotation(position, annotation);
			italicPos = underlinePos;
		}

		// Save state
		state.put(UNDERLINE_START, underlinePos);
		if (italicPos != -1)
		{
			state.put(ITALIC_START, italicPos);
		}
		if (boldPos != -1)
		{
			state.put(BOLD_START, boldPos);
		}
	}

	/**
	 * @param document
	 * @param currentText
	 */
	private void endItalic(RTFDocument document, StringBuilder currentText, Map<String, Integer> state)
	{
		if (!state.containsKey(ITALIC_START))
		{
			return;
		}

		int italicPos = currentText.length();
		int underlinePos = getDefault(state, UNDERLINE_START, -1);
		int boldPos = getDefault(state, BOLD_START, -1);

		Annotation annotation;
		int curPos = currentText.length();
		Position position = new Position(italicPos, curPos - italicPos);

		if (boldPos != -1 && underlinePos != -1)
		{
			annotation = new Annotation(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE, true, EMPTY);
			boldPos = curPos;
			underlinePos = curPos;
			state.put(BOLD_START, boldPos);
			state.put(UNDERLINE_START, underlinePos);
		}
		else if (boldPos != -1)
		{
			annotation = new Annotation(RTFConstants.BOLD_ITALIC_TYPE, true, EMPTY);
			boldPos = curPos;
			state.put(BOLD_START, boldPos);
		}
		else if (underlinePos != -1)
		{
			annotation = new Annotation(RTFConstants.ITALIC_UNDERLINE_TYPE, true, EMPTY);
			underlinePos = curPos;
			state.put(UNDERLINE_START, underlinePos);
		}
		else
		{
			annotation = new Annotation(RTFConstants.ITALIC_TYPE, true, EMPTY);
		}

		if (position.length > 0)
		{
			document.addAnnotation(position, annotation);
		}

		state.remove(ITALIC_START);
	}

	/**
	 * @param document
	 * @param currentText
	 */
	private void startItalic(RTFDocument document, StringBuilder currentText, Map<String, Integer> state)
	{
		if (state.containsKey(ITALIC_START))
		{
			return;
		}

		int italicPos = currentText.length();
		int underlinePos = getDefault(state, UNDERLINE_START, -1);
		int boldPos = getDefault(state, BOLD_START, -1);

		if (boldPos != -1 && underlinePos != -1 && boldPos != italicPos)
		{
			Annotation annotation = new Annotation(RTFConstants.BOLD_UNDERLINE_TYPE, true, EMPTY);
			Position position = new Position(boldPos, italicPos - boldPos);

			document.addAnnotation(position, annotation);
			boldPos = italicPos;
			underlinePos = italicPos;
		}
		else if (boldPos != -1 && boldPos != italicPos)
		{
			Annotation annotation = new Annotation(RTFConstants.BOLD_TYPE, true, EMPTY);
			Position position = new Position(boldPos, italicPos - boldPos);

			document.addAnnotation(position, annotation);
			boldPos = italicPos;
		}
		else if (underlinePos != -1 && underlinePos != italicPos)
		{
			Annotation annotation = new Annotation(RTFConstants.UNDERLINE_TYPE, true, EMPTY);
			Position position = new Position(underlinePos, italicPos - underlinePos);

			document.addAnnotation(position, annotation);
			underlinePos = italicPos;
		}

		// Save state
		state.put(ITALIC_START, italicPos);
		if (italicPos != -1)
		{
			state.put(UNDERLINE_START, underlinePos);
		}
		if (boldPos != -1)
		{
			state.put(BOLD_START, boldPos);
		}
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
