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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import org.apache.commons.lang.CharUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.part.FileEditorInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.mcgill.cs.swevo.qualyzer.editors.inputs.RTFEditorInput;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
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
 * If you need to add a tag definition include the information in RTFTags and then add the behaviour to the
 * handleTag(...) method. The tag should already be parsed properly. If not then it may be defined in
 * RTFTags.IGNORE_GROUPS or it may occur within one of the ignored groups. If this is the case then you are better off
 * not using the tag as not ignore the group would lead to major changes to the parsing mechanism.
 * 
 */
public class RTFDocumentProvider extends FileDocumentProvider
{

	private static final String STAR_SLASH = "*\\"; //$NON-NLS-1$
	private static final String EMPTY = ""; //$NON-NLS-1$
	private static final String SPACE = " "; //$NON-NLS-1$

	private static Logger gLogger = LoggerFactory.getLogger(RTFDocumentProvider.class);

	private int fBoldTag;
	private int fItalicTag;
	private int fUnderlineTag;
	private Stack<ParserState> fStack;

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
	 * Parses the document for all the rtf tags and creates all necessary annotations. This method should be changed
	 * very carefully. Preferably not at all.
	 * 
	 * @see org.eclipse.ui.editors.text.StorageDocumentProvider#setDocumentContent(org.eclipse.jface.text.IDocument,
	 *      java.io.InputStream, java.lang.String)
	 */
	@Override
	protected void setDocumentContent(IDocument document, InputStream contentStream, String encoding)
			throws CoreException
	{
		RTFDocument rtfDocument = (RTFDocument) document;
		StringBuilder text = new StringBuilder(EMPTY);
		fBoldTag = -1;
		fItalicTag = -1;
		fUnderlineTag = -1;
		fStack = new Stack<ParserState>();
		fStack.push(new ParserState(false, false, false));

		try
		{
			int c;
			boolean justStarted = true;
			while ((c = contentStream.read()) != -1)
			{
				char ch = (char) c;
				EscapePair pair = null;
				char lastchar = ch;
				if (ch == '\0')
				{
					break;
				}

				if (ch == '{' || ch == '}')
				{
					if (justStarted)
					{
						justStarted = false;
					}
					else
					{
						handleBracket(ch, text, contentStream, rtfDocument);
					}
				}
				else if (ch == '\\')
				{
					String escape = EMPTY;
					boolean stop = false;
					do
					{
						pair = nextTag(contentStream);
						escape = pair.getEscape();
						text.append(handleTag(escape, rtfDocument, text, contentStream, pair));
						stop = escape.equals(RTFTags.IGNORE) || escape.equals(STAR_SLASH);
						lastchar = escape.charAt(escape.length() - 1);
					} while (escape.length() > 1 && lastchar == '\\' && !stop);
					
					// Did we just eat a bracket?
					if (pair.getLastchar() == '{') 
					{
						handleBracket(pair.getLastchar(), text, contentStream, rtfDocument);
					}
				}
				else if ((!Character.isWhitespace(ch) && ch != '\0') || ch == ' ')
				{
					text.append(ch);
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		rtfDocument.set(text.toString());
	}

	private void handleBracket(char currentCharacter, StringBuilder text, InputStream contentStream, 
			RTFDocument document)
			throws IOException
	{
		char ch = currentCharacter;
		char lastchar = '\0';
		EscapePair pair = null;
		if (ch == '{')
		{
			ch = (char) contentStream.read();
			boolean push = true;
			if (ch == '\\')
			{
				String groupTag = EMPTY;
				boolean stop = false;
				do
				{
					pair = nextTag(contentStream);
					groupTag = pair.getEscape();
					if (push && !isIgnoredGroup(groupTag))
					{
						pushState((RTFDocument) document, text, contentStream);
						push = false;
					}
					text.append(handleTag(groupTag, document, text, contentStream, pair));
					stop = isIgnoredGroup(groupTag) || groupTag.equals(RTFTags.IGNORE) || groupTag.equals(STAR_SLASH);
					lastchar = groupTag.charAt(groupTag.length() - 1);
				} while (!stop && groupTag.length() > 1 && lastchar == '\\');

				// Did we just eat a bracket?
				if (pair.getLastchar() == '{') 
				{
					handleBracket(pair.getLastchar(), text, contentStream, document);
				}
				
			}
			else if ((!Character.isWhitespace(ch) && ch != '\0') || ch == ' ' || ch == '\n')
			{
				pushState((RTFDocument) document, text, contentStream);
				push = false;
				text.append(ch);
			}
		}
		else if (ch == '}')
		{
			popState((RTFDocument) document, text, contentStream);
		}
	}

	/**
	 * Checks to see if the tag is an ignored group tag as defined in RTFTags.IGNORE_GROUPS.
	 * 
	 * @param groupTag
	 *            The tag to check.
	 * @return
	 */
	private boolean isIgnoredGroup(String groupTag)
	{
		for (String tag : RTFTags.IGNORE_GROUPS)
		{
			if (groupTag.contains(tag))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Handles the various tags. Any new tag behaviour should be added here.
	 * 
	 * @param escape
	 *            The tag to handle.
	 * @param document
	 *            The document being parsed.
	 * @param currentText
	 *            The parsed text so far.
	 * @param stream
	 *            The stream from which the document is being read.
	 * @param pair
	 * 			  The last token read. Last char will be reset if group is skipped. Can be null.
	 * @return String The text to add to the parsed text.
	 * @throws IOException
	 */
	private String handleTag(String escape, RTFDocument document, StringBuilder currentText, InputStream stream, 
			EscapePair pair) throws IOException
	{
		String string = escape.trim();
		String toReturn = EMPTY;
		if (!string.isEmpty())
		{
			if (string.charAt(string.length() - 1) == '\\')
			{
				string = string.substring(0, string.length() - 1);
			}
			string = string.trim();

			if (isIgnoredGroup(string))
			{
				skipGroup(stream, string);
				if (pair != null) 
				{
					pair.setLastchar('\0');
				}
				return toReturn;
			}

			if (string.isEmpty())
			{
				toReturn = RTFTags.BACKSLASH;
			}
			else if (Character.isDigit(string.charAt(0)))
			{
				toReturn = getUnicodeCharacter(string);
			}
			else if (string.equals(RTFTags.RIGHT_BRACE) || string.equals(RTFTags.LEFT_BRACE))
			{
				toReturn = string;
			}
			else if (string.equals(RTFTags.NEW_LINE))
			{
				toReturn = "\n"; //$NON-NLS-1$
			}
			else if (string.equals(RTFTags.PAR_DEFAULT) || string.equals(RTFTags.PLAIN))
			{
				endBold(document, currentText);
				endItalic(document, currentText);
				endUnderline(document, currentText);
			}
			else if (string.equals(RTFTags.TAB))
			{
				toReturn = "\t"; //$NON-NLS-1$
			}
			else
			{
				handleFormatTag(document, currentText, string);
			}
		}
		return toReturn;
	}

	/**
	 * Parses a string for an integer code and then converts it to its Unicode value.
	 * 
	 * @param code
	 * @return
	 */
	private String getUnicodeCharacter(String code)
	{
		int unicode = Integer.parseInt(code);

		return EMPTY + (char) unicode;
	}

	/**
	 * Skip a group based on the tag.
	 * 
	 * @param stream
	 *            The stream from which the document is being read.
	 * @param tag
	 *            The tag that started the group.
	 * @throws IOException
	 */
	private void skipGroup(InputStream stream, String tag) throws IOException
	{
		int count = tag.equals(RTFTags.IGNORE) || tag.equals(RTFTags.COLOR_TABLE) ? 1 : 2;
		while (count > 0)
		{
			char c = (char) stream.read();
			if (c == '{')
			{
				count++;
			}
			else if (c == '}')
			{
				count--;
			}
		}
	}

	/**
	 * Handles the various annotation tags.
	 * 
	 * @param document
	 * @param currentText
	 * @param string
	 */
	private void handleFormatTag(RTFDocument document, StringBuilder currentText, String string)
	{
		if (string.equals(RTFTags.BOLD_START))
		{
			startBold(document, currentText);
		}
		else if (string.equals(RTFTags.BOLD_END))
		{
			endBold(document, currentText);
		}
		else if (string.equals(RTFTags.ITALIC_START))
		{
			startItalic(document, currentText);
		}
		else if (string.equals(RTFTags.ITALIC_END))
		{
			endItalic(document, currentText);
		}
		else if (string.equals(RTFTags.UNDERLINE_START))
		{
			startUnderline(document, currentText);
		}
		else if (string.equals(RTFTags.UNDERLINE_END))
		{
			endUnderline(document, currentText);
		}
	}

	/**
	 * @param document
	 * @param currentText
	 */
	private void endUnderline(RTFDocument document, StringBuilder currentText)
	{
		if (fUnderlineTag == -1)
		{
			return;
		}

		Annotation annotation;
		int curPos = currentText.length();
		Position position = new Position(fUnderlineTag, curPos - fUnderlineTag);

		if (fBoldTag != -1 && fItalicTag != -1)
		{
			annotation = new Annotation(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE, true, EMPTY);
			fBoldTag = curPos;
			fItalicTag = curPos;
		}
		else if (fBoldTag != -1)
		{
			annotation = new Annotation(RTFConstants.BOLD_UNDERLINE_TYPE, true, EMPTY);
			fBoldTag = curPos;
		}
		else if (fItalicTag != -1)
		{
			annotation = new Annotation(RTFConstants.ITALIC_UNDERLINE_TYPE, true, EMPTY);
			fItalicTag = curPos;
		}
		else
		{
			annotation = new Annotation(RTFConstants.UNDERLINE_TYPE, true, EMPTY);
		}

		if (position.length > 0)
		{
			document.addAnnotation(position, annotation);
		}

		fUnderlineTag = -1;
	}

	/**
	 * @param document
	 * @param currentText
	 */
	private void startUnderline(RTFDocument document, StringBuilder currentText)
	{
		if (fUnderlineTag != -1)
		{
			return;
		}

		fUnderlineTag = currentText.length();

		if (fBoldTag != -1 && fItalicTag != -1 && fBoldTag != fUnderlineTag)
		{
			Annotation annotation = new Annotation(RTFConstants.BOLD_ITALIC_TYPE, true, EMPTY);
			Position position = new Position(fBoldTag, fUnderlineTag - fBoldTag);

			document.addAnnotation(position, annotation);
			fBoldTag = fUnderlineTag;
			fItalicTag = fUnderlineTag;
		}
		else if (fBoldTag != -1 && fBoldTag != fUnderlineTag)
		{
			Annotation annotation = new Annotation(RTFConstants.BOLD_TYPE, true, EMPTY);
			Position position = new Position(fBoldTag, fUnderlineTag - fBoldTag);

			document.addAnnotation(position, annotation);
			fBoldTag = fUnderlineTag;
		}
		else if (fItalicTag != -1 && fItalicTag != fUnderlineTag)
		{
			Annotation annotation = new Annotation(RTFConstants.ITALIC_TYPE, true, EMPTY);
			Position position = new Position(fItalicTag, fUnderlineTag - fItalicTag);

			document.addAnnotation(position, annotation);
			fItalicTag = fUnderlineTag;
		}
	}

	/**
	 * @param document
	 * @param currentText
	 */
	private void endItalic(RTFDocument document, StringBuilder currentText)
	{
		if (fItalicTag == -1)
		{
			return;
		}

		Annotation annotation;
		int curPos = currentText.length();
		Position position = new Position(fItalicTag, curPos - fItalicTag);

		if (fBoldTag != -1 && fUnderlineTag != -1)
		{
			annotation = new Annotation(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE, true, EMPTY);
			fBoldTag = curPos;
			fUnderlineTag = curPos;
		}
		else if (fBoldTag != -1)
		{
			annotation = new Annotation(RTFConstants.BOLD_ITALIC_TYPE, true, EMPTY);
			fBoldTag = curPos;
		}
		else if (fUnderlineTag != -1)
		{
			annotation = new Annotation(RTFConstants.ITALIC_UNDERLINE_TYPE, true, EMPTY);
			fUnderlineTag = curPos;
		}
		else
		{
			annotation = new Annotation(RTFConstants.ITALIC_TYPE, true, EMPTY);
		}

		if (position.length > 0)
		{
			document.addAnnotation(position, annotation);
		}

		fItalicTag = -1;
	}

	/**
	 * @param document
	 * @param currentText
	 */
	private void startItalic(RTFDocument document, StringBuilder currentText)
	{
		if (fItalicTag != -1)
		{
			return;
		}

		fItalicTag = currentText.length();

		if (fBoldTag != -1 && fUnderlineTag != -1 && fBoldTag != fItalicTag)
		{
			Annotation annotation = new Annotation(RTFConstants.BOLD_UNDERLINE_TYPE, true, EMPTY);
			Position position = new Position(fBoldTag, fItalicTag - fBoldTag);

			document.addAnnotation(position, annotation);
			fBoldTag = fItalicTag;
			fUnderlineTag = fItalicTag;
		}
		else if (fBoldTag != -1 && fBoldTag != fItalicTag)
		{
			Annotation annotation = new Annotation(RTFConstants.BOLD_TYPE, true, EMPTY);
			Position position = new Position(fBoldTag, fItalicTag - fBoldTag);

			document.addAnnotation(position, annotation);
			fBoldTag = fItalicTag;
		}
		else if (fUnderlineTag != -1 && fUnderlineTag != fItalicTag)
		{
			Annotation annotation = new Annotation(RTFConstants.UNDERLINE_TYPE, true, EMPTY);
			Position position = new Position(fUnderlineTag, fItalicTag - fUnderlineTag);

			document.addAnnotation(position, annotation);
			fUnderlineTag = fItalicTag;
		}
	}

	/**
	 * @param document
	 * @param currentText
	 */
	private void endBold(RTFDocument document, StringBuilder currentText)
	{
		if (fBoldTag == -1)
		{
			return;
		}

		Annotation annotation;
		int curPos = currentText.length();
		Position position = new Position(fBoldTag, curPos - fBoldTag);

		if (fItalicTag != -1 && fUnderlineTag != -1)
		{
			annotation = new Annotation(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE, true, EMPTY);
			fItalicTag = curPos;
			fUnderlineTag = curPos;
		}
		else if (fItalicTag != -1)
		{
			annotation = new Annotation(RTFConstants.BOLD_ITALIC_TYPE, true, EMPTY);
			fItalicTag = curPos;
		}
		else if (fUnderlineTag != -1)
		{
			annotation = new Annotation(RTFConstants.BOLD_UNDERLINE_TYPE, true, EMPTY);
			fUnderlineTag = curPos;
		}
		else
		{
			annotation = new Annotation(RTFConstants.BOLD_TYPE, true, EMPTY);
		}

		if (position.length > 0)
		{
			document.addAnnotation(position, annotation);
		}

		fBoldTag = -1;
	}

	/**
	 * @param document
	 * @param currentText
	 */
	private void startBold(RTFDocument document, StringBuilder currentText)
	{
		if (fBoldTag != -1)
		{
			return;
		}

		fBoldTag = currentText.length();

		if (fItalicTag != -1 && fUnderlineTag != -1 && fItalicTag != fBoldTag)
		{
			Annotation annotation = new Annotation(RTFConstants.ITALIC_UNDERLINE_TYPE, true, EMPTY);
			Position position = new Position(fItalicTag, fBoldTag - fItalicTag);

			document.addAnnotation(position, annotation);
			fItalicTag = fBoldTag;
			fUnderlineTag = fBoldTag;
		}
		else if (fItalicTag != -1 && fItalicTag != fBoldTag)
		{
			Annotation annotation = new Annotation(RTFConstants.ITALIC_TYPE, true, EMPTY);
			Position position = new Position(fItalicTag, fBoldTag - fItalicTag);

			document.addAnnotation(position, annotation);
			fItalicTag = fBoldTag;
		}
		else if (fUnderlineTag != -1 && fUnderlineTag != fBoldTag)
		{
			Annotation annotation = new Annotation(RTFConstants.UNDERLINE_TYPE, true, EMPTY);
			Position position = new Position(fUnderlineTag, fBoldTag - fUnderlineTag);

			document.addAnnotation(position, annotation);
			fUnderlineTag = fBoldTag;
		}
	}

	/**
	 * Find the next tag that needs to be handled. Assumes that the text at the start of the stream forms a tag. (i.e.
	 * assumes that the last character read was a '\\')
	 * 
	 * @param ioStream
	 * @return
	 * @throws IOException
	 */
	private EscapePair nextTag(InputStream ioStream) throws IOException
	{
		StringBuilder escape = new StringBuilder(EMPTY);
		char ch2 = (char) ioStream.read();
		if (ch2 == 'u')
		{
			escape.append(ch2);
			ch2 = (char) ioStream.read();
			StringBuilder unicode = new StringBuilder(EMPTY);
			while (Character.isDigit(ch2))
			{
				escape.append(ch2);
				unicode.append(ch2);
				ch2 = (char) ioStream.read();
			}

			if (!unicode.toString().isEmpty())
			{
				int letterCount = Character.isLetter(ch2) ? 1 : 0;
				while (letterCount < 1)
				{
					ch2 = (char) ioStream.read();
					if (Character.isLetter(ch2))
					{
						letterCount++;
					}
				}
				return new EscapePair(unicode.toString(), ch2);
			}
		}
		boolean notBracket = ch2 != '{' && ch2 != '}';
		while (ch2 != ' ' && notBracket && ch2 != '\\' && ch2 != '\n')
		{
			escape.append(ch2);
			ch2 = (char) ioStream.read();
			notBracket = ch2 != '{' && ch2 != '}';
		}
		if (ch2 == '\\')
		{
			escape.append(RTFTags.BACKSLASH);
		}
		else if (ch2 != '{' || escape.toString().isEmpty())
		{
			escape.append(ch2);
		}
		return new EscapePair(escape.toString(), ch2);
	}

	/**
	 * Converts the contents of the document back into rtf so that it can be saved to disk.
	 * 
	 * @see org.eclipse.ui.editors.text.FileDocumentProvider#doSaveDocument(org.eclipse.core.runtime.IProgressMonitor,
	 *      java.lang.Object, org.eclipse.jface.text.IDocument, boolean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void doSaveDocument(IProgressMonitor monitor, Object element, IDocument document, boolean overwrite)
			throws CoreException
	{
		FileEditorInput input = (FileEditorInput) element;
		IAnnotationModel model = getAnnotationModel(element);

		StringBuilder contents = new StringBuilder(document.get());
		StringBuilder toWrite = new StringBuilder(EMPTY);

		toWrite = buildRTFString(contents, model);

		InputStream stream = new ByteArrayInputStream(toWrite.toString().getBytes());
		try
		{
			input.getFile().setContents(stream, IResource.FORCE, new NullProgressMonitor());
		}
		catch (CoreException e)
		{

		}

		// This seems to be necessary for the timestamp markers to persist across saves.
		FileInfo info = (FileInfo) getElementInfo(element);
		if (info != null)
		{
			RTFAnnotationModel rtfModel = (RTFAnnotationModel) info.fModel;
			rtfModel.updateMarkers(info.fDocument);
		}

		// Updates all of the fragment positions, and removes any annotations that have length 0.
		IAnnotatedDocument rtfDoc = ((RTFEditorInput) element).getDocument();
		Iterator<Annotation> iter = model.getAnnotationIterator();
		while (iter.hasNext())
		{
			Annotation annotation = iter.next();
			if (annotation instanceof FragmentAnnotation)
			{
				updateFragment(model, rtfDoc, annotation);
			}
			else
			{
				if (model.getPosition(annotation).length == 0)
				{
					model.removeAnnotation(annotation);
				}
			}
		}

		Facade.getInstance().saveDocument(rtfDoc);
	}

	/**
	 * Updates the given annotation's fragment to match it's new offset and length. Then updates the map key so that it
	 * matches the new offset. If the fragment has a length of 0 it gets removed from the model (and the DB).
	 * 
	 * @param model
	 * @param rtfDoc
	 * @param annotation
	 */
	private void updateFragment(IAnnotationModel model, IAnnotatedDocument rtfDoc, Annotation annotation)
	{
		Fragment fragment = ((FragmentAnnotation) annotation).getFragment();
		Position position = model.getPosition(annotation);
		if (position.length == 0)
		{
			model.removeAnnotation(annotation);
		}
		else
		{
			fragment.setOffset(position.offset);
			fragment.setLength(position.length);
			rtfDoc.getFragments().remove(position.offset);
			rtfDoc.getFragments().put(position.offset, fragment);
		}
	}

	/**
	 * Goes through the editor text and all the annotations to build the string that will be written to the disk.
	 * Converts any special characters to their RTF tags as well.
	 * 
	 * @param contents
	 * @param model
	 * @return
	 */
	private StringBuilder buildRTFString(StringBuilder contents, IAnnotationModel model)
	{
		StringBuilder output = new StringBuilder(RTFTags.HEADER);
		ArrayList<Position> positions = new ArrayList<Position>();
		ArrayList<Annotation> annotations = new ArrayList<Annotation>();
		prepareAnnotationLists(model, positions, annotations);

		Position position = null;
		Annotation annotation = null;

		for (int i = 0; i < contents.length(); i++)
		{
			if (position == null)
			{
				for (int j = 0; j < positions.size(); j++)
				{
					if (positions.get(j).offset == i)
					{
						position = positions.remove(j);
						annotation = annotations.remove(j);
						break;
					}
					else if (positions.get(j).offset > i)
					{
						break;
					}
				}
				if (position != null)
				{
					output.append(getStartTagFromAnnotation(annotation));
				}
			}

			char c = contents.charAt(i);
			output.append(getMiddleChar(c));

			if (position != null && i == position.offset + position.length - 1)
			{
				output.append(getEndTagFromAnnotation(annotation));

				position = null;
				annotation = null;
			}

			output.append(getEndChar(c));
		}

		return output.append(RTFTags.FOOTER);
	}

	/**
	 * Gets the annotations and their positions from the model and sorts them by position. Sets them into the two
	 * provided arraylists.
	 * 
	 * @param model
	 * @param positions
	 * @param annotations
	 */
	@SuppressWarnings("unchecked")
	private void prepareAnnotationLists(IAnnotationModel model, ArrayList<Position> positions,
			ArrayList<Annotation> annotations)
	{
		Iterator<Annotation> iter = model.getAnnotationIterator();
		while (iter.hasNext())
		{
			Annotation annotation = iter.next();
			String type = annotation.getType();
			if (!(annotation instanceof FragmentAnnotation) && !type.equals(RTFConstants.TIMESTAMP_TYPE))
			{
				if (positions.isEmpty())
				{
					annotations.add(annotation);
					positions.add(model.getPosition(annotation));
				}
				else
				{
					Position position = model.getPosition(annotation);
					int i;
					for (i = 0; i < positions.size(); i++)
					{
						Position curPos = positions.get(i);
						if (position.offset < curPos.offset)
						{
							annotations.add(i, annotation);
							positions.add(i, position);
							break;
						}
					}

					if (i >= positions.size())
					{
						annotations.add(annotation);
						positions.add(position);
					}
				}

			}
		}
	}

	/**
	 * Gets the rtf representations of newline and tab if the current character is one of those.
	 * 
	 * @param c
	 * @return
	 */
	private String getEndChar(char c)
	{
		StringBuilder output = new StringBuilder(EMPTY);
		if (c == '\n')
		{
			output.append(RTFTags.NEW_LINE_TAG);
		}
		else if (c == '\t')
		{
			output.append(RTFTags.TAB_TAG);
		}
		return output.toString();
	}

	/**
	 * Stops newlines tabs and EOF from being written, adds an escape to brackets and backslash, converts non-ascii
	 * characters to their RTF tag and lets all other characters through.
	 * 
	 * @param c
	 * @return
	 */
	private String getMiddleChar(char c)
	{
		StringBuilder output = new StringBuilder(EMPTY);
		if (c != '\n' && c != '\t' && c != '\0')
		{
			if (c == '{' || c == '}' || c == '\\')
			{
				output.append(RTFTags.BACKSLASH);
			}

			if (CharUtils.isAscii(c))
			{
				output.append(c);
			}
			else
			{
				int unicode = (int) c;
				output = new StringBuilder(RTFTags.UNICODE_START_TAG + unicode + RTFTags.UNICODE_END_TAG);
			}
		}
		return output.toString();
	}

	/**
	 * @param annotation
	 * @return
	 */
	private String getEndTagFromAnnotation(Annotation annotation)
	{
		String tag = EMPTY;
		String type = annotation.getType();

		if (type.equals(RTFConstants.BOLD_TYPE))
		{
			tag = RTFTags.BOLD_END_TAG + SPACE;
		}
		else if (type.equals(RTFConstants.ITALIC_TYPE))
		{
			tag = RTFTags.ITALIC_END_TAG + SPACE;
		}
		else if (type.equals(RTFConstants.UNDERLINE_TYPE))
		{
			tag = RTFTags.UNDERLINE_END_TAG + SPACE;
		}
		else if (type.equals(RTFConstants.BOLD_ITALIC_TYPE))
		{
			tag = RTFTags.BOLD_END_TAG + RTFTags.ITALIC_END_TAG + SPACE;
		}
		else if (type.equals(RTFConstants.BOLD_UNDERLINE_TYPE))
		{
			tag = RTFTags.BOLD_END_TAG + RTFTags.UNDERLINE_END_TAG + SPACE;
		}
		else if (type.equals(RTFConstants.ITALIC_UNDERLINE_TYPE))
		{
			tag = RTFTags.ITALIC_END_TAG + RTFTags.UNDERLINE_END_TAG + SPACE;
		}
		else if (type.equals(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE))
		{
			tag = RTFTags.BOLD_END_TAG + RTFTags.ITALIC_END_TAG + RTFTags.UNDERLINE_END_TAG + SPACE;
		}

		return tag;
	}

	/**
	 * @param annotation
	 * @return
	 */
	private String getStartTagFromAnnotation(Annotation annotation)
	{
		String tag = EMPTY;
		String type = annotation.getType();

		if (type.equals(RTFConstants.BOLD_TYPE))
		{
			tag = RTFTags.BOLD_START_TAG + SPACE;
		}
		else if (type.equals(RTFConstants.ITALIC_TYPE))
		{
			tag = RTFTags.ITALIC_START_TAG + SPACE;
		}
		else if (type.equals(RTFConstants.UNDERLINE_TYPE))
		{
			tag = RTFTags.UNDERLINE_START_TAG + SPACE;
		}
		else if (type.equals(RTFConstants.BOLD_ITALIC_TYPE))
		{
			tag = RTFTags.BOLD_START_TAG + RTFTags.ITALIC_START_TAG + SPACE;
		}
		else if (type.equals(RTFConstants.BOLD_UNDERLINE_TYPE))
		{
			tag = RTFTags.BOLD_START_TAG + RTFTags.UNDERLINE_START_TAG + SPACE;
		}
		else if (type.equals(RTFConstants.ITALIC_UNDERLINE_TYPE))
		{
			tag = RTFTags.ITALIC_START_TAG + RTFTags.UNDERLINE_START_TAG + SPACE;
		}
		else if (type.equals(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE))
		{
			tag = RTFTags.BOLD_START_TAG + RTFTags.ITALIC_START_TAG + RTFTags.UNDERLINE_START_TAG + SPACE;
		}

		return tag;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.editors.text.FileDocumentProvider#createAnnotationModel(java.lang.Object)
	 */
	@Override
	protected IAnnotationModel createAnnotationModel(Object element) throws CoreException
	{
		if (element instanceof RTFEditorInput)
		{
			return new RTFAnnotationModel((RTFEditorInput) element);
		}

		return super.createAnnotationModel(element);
	}

	/**
	 * Handles a group start by pushing the current state of formatting onto the stack. But first calls handleTag on
	 * \\plain which ends all current formatting..
	 * 
	 * @param document
	 * @param text
	 * @param stream
	 * @throws IOException
	 */
	private void pushState(RTFDocument document, StringBuilder text, InputStream stream) throws IOException
	{
		ParserState state = new ParserState(fBoldTag != -1, fItalicTag != -1, fUnderlineTag != -1);
		fStack.push(state);
		handleTag(RTFTags.PLAIN, document, text, stream, null);
		if (state.isBold())
		{
			handleTag(RTFTags.BOLD_START, document, text, stream, null);
		}
		if (state.isItalic())
		{
			handleTag(RTFTags.ITALIC_START, document, text, stream, null);
		}
		if (state.isUnderline())
		{
			handleTag(RTFTags.UNDERLINE_START, document, text, stream, null);
		}
	}

	/**
	 * Handles the end of a group by popping the last formatting state off the stack. It first ends all current
	 * formatting and then restores the formatting of the popped state.
	 * 
	 * @param document
	 * @param text
	 * @param stream
	 * @throws IOException
	 */
	private void popState(RTFDocument document, StringBuilder text, InputStream stream) throws IOException
	{
		handleTag(RTFTags.PLAIN, document, text, stream, null);
		ParserState state = fStack.pop();
		if (state.isBold())
		{
			handleTag(RTFTags.BOLD_START, document, text, stream, null);
		}
		if (state.isItalic())
		{
			handleTag(RTFTags.ITALIC_START, document, text, stream, null);
		}
		if (state.isUnderline())
		{
			handleTag(RTFTags.UNDERLINE_START, document, text, stream, null);
		}
	}
}

/**
 * Used to keep track of the last read character when reading an escape sequence. 
 *
 */
class EscapePair 
{
	private final String fEscape;
	private char fLastchar;
	
	/**
	 * @param escape
	 * @param lastchar
	 */
	public EscapePair(String escape, char lastchar)
	{
		super();
		this.fEscape = escape;
		this.fLastchar = lastchar;
	}
	public String getEscape()
	{
		return fEscape;
	}
	public char getLastchar()
	{
		return fLastchar;
	}
	
	public void setLastchar(char lastchar)
	{
		this.fLastchar = lastchar;
	}
	
	
}
