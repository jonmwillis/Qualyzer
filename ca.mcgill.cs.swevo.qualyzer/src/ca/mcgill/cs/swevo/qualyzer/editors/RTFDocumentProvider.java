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
 * The DocumentProvider for our editor.
 *
 */
public class RTFDocumentProvider extends FileDocumentProvider
{	
	
	private static final String STAR_SLASH = "*\\"; //$NON-NLS-1$
	private static final String EMPTY = "";  //$NON-NLS-1$
	private static final String SPACE = " "; //$NON-NLS-1$
	
	private static Logger gLogger = LoggerFactory.getLogger(RTFDocumentProvider.class);
	
	private int fBoldTag;
	private int fItalicTag;
	private int fUnderlineTag;
	private Stack<ParserState> fStack;
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.StorageDocumentProvider#createDocument(java.lang.Object)
	 */
	@Override
	protected IDocument createDocument(Object element) throws CoreException
	{
		RTFDocument doc = (RTFDocument) super.createDocument(element);
		
		IAnnotatedDocument transcript = ((RTFEditorInput) element).getDocument();
		
		for(Fragment fragment : transcript.getFragments())
		{
			Position position = new Position(fragment.getOffset(), fragment.getLength());
			doc.addAnnotation(position, new FragmentAnnotation(fragment));
		}
		
		return doc;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.StorageDocumentProvider#createEmptyDocument()
	 */
	@Override
	protected IDocument createEmptyDocument()
	{
		return new RTFDocument();
	}
	
	/**
	 * Exists only for use by things that need parsed documents, but that don't want to open the editor.
	 * @param element
	 * @return
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
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.StorageDocumentProvider#setDocumentContent(
	 * org.eclipse.jface.text.IDocument, java.io.InputStream, java.lang.String)
	 */
	@Override
	protected void setDocumentContent(IDocument document, InputStream contentStream, String encoding)
			throws CoreException
	{
		String text = EMPTY;
		fBoldTag = -1;
		fItalicTag = -1;
		fUnderlineTag = -1;
		fStack = new Stack<ParserState>();
		fStack.push(new ParserState(false, false, false));
		
		try
		{
			int c;
			boolean justStarted = true;
			while((c = contentStream.read()) != -1)
			{
				char ch = (char) c;
				if(ch == '{' || ch == '}')
				{
					if(justStarted)
					{
						justStarted = false;
						continue;
					}
					else if(ch == '{')
					{
						ch = (char) contentStream.read();
						boolean push = true;
						if(ch == '\\')
						{
							String groupTag = EMPTY;
							boolean stop = false;
							do
							{
								groupTag = nextTag(contentStream);
								if(push && !isIgnoredGroup(groupTag))
								{
									pushState((RTFDocument) document, text, contentStream);
									push = false;
								}
								text += handleTag(groupTag, (RTFDocument) document, text, contentStream);
								stop = isIgnoredGroup(groupTag) || groupTag.equals(RTFTags.IGNORE) || 
									groupTag.equals(STAR_SLASH);
							}while(!stop && groupTag.length() > 1 && groupTag.charAt(groupTag.length() - 1) == '\\');
						}
					}
					else if(ch == '}')
					{
						popState((RTFDocument) document, text, contentStream);
					}
				}
				else if(ch == '\\')
				{
					String escape = EMPTY;  
					boolean stop = false;
					do
					{
						escape = nextTag(contentStream);
						text += handleTag(escape, (RTFDocument) document, text, contentStream);
						stop = escape.equals(RTFTags.IGNORE) || escape.equals(STAR_SLASH);
					}while(escape.length() > 1 && escape.charAt(escape.length() - 1) == '\\' && !stop);
					
				}
				else if((!Character.isWhitespace(ch) && ch != '\0') || ch == ' ')
				{
					text += ch;
				}
			}
		
			//It seems that some editors (wordpad) don't put ending tags if the style reaches the EOF
			text += handleTag(RTFTags.BOLD_END, (RTFDocument)document, text, contentStream); 
			text += handleTag(RTFTags.ITALIC_END, (RTFDocument)document, text, contentStream); 
			text += handleTag(RTFTags.UNDERLINE_END, (RTFDocument)document, text, contentStream); 
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		document.set(text);

	}
	
	/**
	 * @param groupTag
	 * @return
	 */
	private boolean isIgnoredGroup(String groupTag)
	{
		for(String tag : RTFTags.IGNORE_GROUPS)
		{
			if(tag.equals(groupTag))
			{
				return true;
			}
		}
		return false;
	}

	private String handleTag(String escape, RTFDocument document, String currentText, InputStream stream) 
		throws IOException
	{
		String string = escape.trim();
		String toReturn = EMPTY;
		
		if(string.isEmpty())
		{
			return toReturn;
		}
		
		if(string.charAt(string.length() - 1) == '\\')
		{
			string = string.substring(0, string.length() - 1);
		}
		string = string.trim();
		
		if(isIgnoredGroup(string))
		{
			int count = string.equals(RTFTags.IGNORE) || string.equals(RTFTags.COLOR_TABLE) ? 1 : 2;
			while(count > 0)
			{
				char c = (char) stream.read();
				if(c == '{')
				{
					count++;
				}
				else if(c == '}')
				{
					count--;
				}
			}
			return toReturn;
		}
		
		if(string.isEmpty())
		{
			toReturn = RTFTags.BACKSLASH; 
		}
		else if(string.equals(RTFTags.RIGHT_BRACE) || string.equals(RTFTags.LEFT_BRACE))  
		{
			toReturn = string;
		}
		else if(string.equals(RTFTags.NEW_LINE)) 
		{
			toReturn = "\n";  //$NON-NLS-1$
		}
		else if(string.equals(RTFTags.PAR_DEFAULT) || string.equals(RTFTags.PLAIN)) 
		{
			endBold(document, currentText);
			endItalic(document, currentText);
			endUnderline(document, currentText);
		}
		else if(string.equals(RTFTags.TAB)) 
		{
			toReturn = "\t";  //$NON-NLS-1$
		}
		else if(string.equals(RTFTags.BOLD_START)) 
		{
			startBold(document, currentText);
		}
		else if(string.equals(RTFTags.BOLD_END)) 
		{	
			endBold(document, currentText);
		}
		else if(string.equals(RTFTags.ITALIC_START)) 
		{
			startItalic(document, currentText);
		}
		else if(string.equals(RTFTags.ITALIC_END)) 
		{
			endItalic(document, currentText);
		}
		else if(string.equals(RTFTags.UNDERLINE_START)) 
		{
			startUnderline(document, currentText);
		}
		else if(string.equals(RTFTags.UNDERLINE_END)) 
		{
			endUnderline(document, currentText);
		}
		
		return toReturn;
	}

	/**
	 * @param document
	 * @param currentText
	 */
	private void endUnderline(RTFDocument document, String currentText)
	{
		if(fUnderlineTag == -1)
		{
			return;
		}
		
		Annotation annotation;
		int curPos = currentText.length();
		Position position = new Position(fUnderlineTag, curPos - fUnderlineTag);
		
		if(fBoldTag != -1 && fItalicTag != -1)
		{
			annotation = new Annotation(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE, true, EMPTY);
			fBoldTag = curPos;
			fItalicTag = curPos;
		}
		else if(fBoldTag != -1)
		{
			annotation = new Annotation(RTFConstants.BOLD_UNDERLINE_TYPE, true, EMPTY);
			fBoldTag = curPos;
		}
		else if(fItalicTag != -1)
		{
			annotation = new Annotation(RTFConstants.ITALIC_UNDERLINE_TYPE, true, EMPTY);
			fItalicTag = curPos;
		}
		else
		{
			annotation = new Annotation(RTFConstants.UNDERLINE_TYPE, true, EMPTY);
		}
		
		if(position.length > 0)
		{
			document.addAnnotation(position, annotation);
		}
		
		fUnderlineTag = -1;
	}

	/**
	 * @param document
	 * @param currentText
	 */
	private void startUnderline(RTFDocument document, String currentText)
	{
		if(fUnderlineTag != -1)
		{
			return;
		}
		
		fUnderlineTag = currentText.length();
		
		if(fBoldTag != -1 && fItalicTag != -1 && fBoldTag != fUnderlineTag)
		{
			Annotation annotation = new Annotation(RTFConstants.BOLD_ITALIC_TYPE, true, EMPTY);
			Position position = new Position(fBoldTag, fUnderlineTag - fBoldTag);
			
			document.addAnnotation(position, annotation);
			fBoldTag = fUnderlineTag;
			fItalicTag = fUnderlineTag;
		}
		else if(fBoldTag != -1 && fBoldTag != fUnderlineTag)
		{
			Annotation annotation = new Annotation(RTFConstants.BOLD_TYPE, true, EMPTY);
			Position position = new Position(fBoldTag, fUnderlineTag - fBoldTag);
			
			document.addAnnotation(position, annotation);
			fBoldTag = fUnderlineTag;
		}
		else if(fItalicTag != -1 && fItalicTag != fUnderlineTag)
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
	private void endItalic(RTFDocument document, String currentText)
	{
		if(fItalicTag == -1)
		{
			return;
		}
		
		Annotation annotation;
		int curPos = currentText.length();
		Position position = new Position(fItalicTag, curPos - fItalicTag);
		
		if(fBoldTag != -1 && fUnderlineTag != -1)
		{
			annotation = new Annotation(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE, true, EMPTY);
			fBoldTag = curPos;
			fUnderlineTag = curPos;
		}
		else if(fBoldTag != -1)
		{
			annotation = new Annotation(RTFConstants.BOLD_ITALIC_TYPE, true, EMPTY);
			fBoldTag = curPos;
		}
		else if(fUnderlineTag != -1)
		{
			annotation = new Annotation(RTFConstants.ITALIC_UNDERLINE_TYPE, true, EMPTY);
			fUnderlineTag = curPos;
		}
		else
		{
			annotation = new Annotation(RTFConstants.ITALIC_TYPE, true, EMPTY);
		}
		
		if(position.length > 0)
		{
			document.addAnnotation(position, annotation);
		}
		
		fItalicTag = -1;
	}

	/**
	 * @param document
	 * @param currentText
	 */
	private void startItalic(RTFDocument document, String currentText)
	{
		if(fItalicTag != -1)
		{
			return;
		}
		
		fItalicTag = currentText.length();
		
		if(fBoldTag != -1 && fUnderlineTag != -1 && fBoldTag != fItalicTag)
		{
			Annotation annotation = new Annotation(RTFConstants.BOLD_UNDERLINE_TYPE, true, EMPTY);
			Position position = new Position(fBoldTag, fItalicTag - fBoldTag);
			
			document.addAnnotation(position, annotation);
			fBoldTag = fItalicTag;
			fUnderlineTag = fItalicTag;
		}
		else if(fBoldTag != -1 && fBoldTag != fItalicTag)
		{
			Annotation annotation = new Annotation(RTFConstants.BOLD_TYPE, true, EMPTY);
			Position position = new Position(fBoldTag, fItalicTag - fBoldTag);
			
			document.addAnnotation(position, annotation);
			fBoldTag = fItalicTag;
		}
		else if(fUnderlineTag != -1 && fUnderlineTag != fItalicTag)
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
	private void endBold(RTFDocument document, String currentText)
	{
		if(fBoldTag == -1)
		{
			return;
		}
		
		Annotation annotation;
		int curPos = currentText.length();
		Position position = new Position(fBoldTag, curPos - fBoldTag);
		
		if(fItalicTag != -1 && fUnderlineTag != -1)
		{
			annotation = new Annotation(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE, true, EMPTY);
			fItalicTag = curPos;
			fUnderlineTag = curPos;
		}
		else if(fItalicTag != -1)
		{
			annotation = new Annotation(RTFConstants.BOLD_ITALIC_TYPE, true, EMPTY);
			fItalicTag = curPos;
		}
		else if(fUnderlineTag != -1)
		{
			annotation = new Annotation(RTFConstants.BOLD_UNDERLINE_TYPE, true, EMPTY);
			fUnderlineTag = curPos;
		}
		else
		{
			annotation = new Annotation(RTFConstants.BOLD_TYPE, true, EMPTY);
		}
		
		if(position.length > 0)
		{
			document.addAnnotation(position, annotation);
		}
		
		fBoldTag = -1;
	}

	/**
	 * @param document
	 * @param currentText
	 */
	private void startBold(RTFDocument document, String currentText)
	{
		if(fBoldTag != -1)
		{
			return;
		}
		
		fBoldTag = currentText.length();
		
		if(fItalicTag != -1 && fUnderlineTag != -1 && fItalicTag != fBoldTag)
		{
			Annotation annotation = new Annotation(RTFConstants.ITALIC_UNDERLINE_TYPE, true, EMPTY);
			Position position = new Position(fItalicTag, fBoldTag - fItalicTag);
			
			document.addAnnotation(position, annotation);
			fItalicTag = fBoldTag;
			fUnderlineTag = fBoldTag;
		}
		else if(fItalicTag != -1 && fItalicTag != fBoldTag)
		{
			Annotation annotation = new Annotation(RTFConstants.ITALIC_TYPE, true, EMPTY);
			Position position = new Position(fItalicTag, fBoldTag - fItalicTag);
			
			document.addAnnotation(position, annotation);
			fItalicTag = fBoldTag;
		}
		else if(fUnderlineTag != -1 && fUnderlineTag != fBoldTag)
		{
			Annotation annotation = new Annotation(RTFConstants.UNDERLINE_TYPE, true, EMPTY);
			Position position = new Position(fUnderlineTag, fBoldTag - fUnderlineTag);
			
			document.addAnnotation(position, annotation);
			fUnderlineTag = fBoldTag;
		}
	}
	
	private String nextTag(InputStream ioStream) throws IOException
	{
		String escape = EMPTY;
		char ch2 = (char) ioStream.read();
		boolean notBracket = ch2 != '{' && ch2 != '}';
		while(ch2 != ' ' && notBracket && ch2 != '\\' && ch2 != '\n')
		{
			escape += ch2;
			ch2 = (char) ioStream.read();
			notBracket = ch2 != '{' && ch2 != '}';
		}
		
		if(ch2 == '\\')
		{
			escape += RTFTags.BACKSLASH; 
		}
		else if(ch2 != '{')
		{
			escape += ch2;
		}
		
		return escape;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.FileDocumentProvider#doSaveDocument(org.eclipse.core.runtime.IProgressMonitor,
	 *  java.lang.Object, org.eclipse.jface.text.IDocument, boolean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void doSaveDocument(IProgressMonitor monitor, Object element, IDocument document, boolean overwrite)
			throws CoreException
	{
		FileEditorInput input = (FileEditorInput) element;
		IAnnotationModel model = getAnnotationModel(element);
		
		String contents = document.get();
		String toWrite = EMPTY;
		
		toWrite = buildRTFString(contents, model);
		
		InputStream stream = new ByteArrayInputStream(toWrite.getBytes());
		try
		{
			input.getFile().setContents(stream, IResource.FORCE, new NullProgressMonitor());
		}
		catch(CoreException e)
		{
			
		}
				
		Iterator<Annotation> iter = model.getAnnotationIterator();
		while(iter.hasNext())
		{
			Annotation annotation = iter.next();
			if(annotation instanceof FragmentAnnotation)
			{
				Fragment fragment = ((FragmentAnnotation) annotation).getFragment();
				Position position = model.getPosition(annotation);
				if(position.length == 0)
				{
					model.removeAnnotation(annotation);
				}
				else
				{
					fragment.setOffset(position.offset);
					fragment.setLength(position.length);
				}
			}
			else
			{
				if(model.getPosition(annotation).length == 0)
				{
					model.removeAnnotation(annotation);
				}
			}
		}
		
		Facade.getInstance().saveDocument(((RTFEditorInput) element).getDocument());
	}

	/**
	 * @param contents
	 * @param model
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String buildRTFString(String contents, IAnnotationModel model)
	{
		String output = RTFTags.HEADER; 
		
		ArrayList<Position> positions = new ArrayList<Position>();
		ArrayList<Annotation> annotations = new ArrayList<Annotation>();
		
		Iterator<Annotation> iter = model.getAnnotationIterator();
		while(iter.hasNext())
		{
			Annotation annotation = iter.next();
			annotations.add(annotation);
			positions.add(model.getPosition(annotation));
		}
		
		Position position = null;
		Annotation annotation = null;
		
		for(int i = 0; i < contents.length(); i++)
		{
			if(position == null)
			{
				for(int j = 0; j < positions.size(); j++)
				{
					if(positions.get(j).offset == i)
					{
						position = positions.get(j);
						annotation = annotations.get(j);
						break;
					}
				}
				
				if(position != null)
				{
					output += getStartTagFromAnnotation(annotation);
				}
			}
			
			char c = contents.charAt(i);
			
			if(c != '\n' && c != '\t' && c!= '\0')
			{
				if(c == '{' || c == '}' || c =='\\')
				{
					output += RTFTags.BACKSLASH;
				}
				output += c;
			}
			
			if(position != null && i == position.offset + position.length - 1)
			{
				output += getEndTagFromAnnotation(annotation);
				
				position = null;
				annotation = null;
			}
			
			if(c == '\n')
			{
				output += RTFTags.NEW_LINE_TAG; 
			}
			else if(c == '\t')
			{
				output += RTFTags.TAB_TAG; 
			}
			
		}
					
		return output + RTFTags.FOOTER; 
	}

	/**
	 * @param annotation
	 * @return
	 */
	private String getEndTagFromAnnotation(Annotation annotation)
	{
		String tag = EMPTY;
		String type = annotation.getType();
		
		if(type.equals(RTFConstants.BOLD_TYPE))
		{
			tag = RTFTags.BOLD_END_TAG + SPACE; 
		}
		else if(type.equals(RTFConstants.ITALIC_TYPE))
		{
			tag = RTFTags.ITALIC_END_TAG + SPACE; 
		}
		else if(type.equals(RTFConstants.UNDERLINE_TYPE))
		{
			tag = RTFTags.UNDERLINE_END_TAG + SPACE; 
		}
		else if(type.equals(RTFConstants.BOLD_ITALIC_TYPE))
		{
			tag = RTFTags.BOLD_END_TAG + RTFTags.ITALIC_END_TAG + SPACE; 
		}
		else if(type.equals(RTFConstants.BOLD_UNDERLINE_TYPE))
		{
			tag = RTFTags.BOLD_END_TAG + RTFTags.UNDERLINE_END_TAG + SPACE; 
		}
		else if(type.equals(RTFConstants.ITALIC_UNDERLINE_TYPE))
		{
			tag = RTFTags.ITALIC_END_TAG + RTFTags.UNDERLINE_END_TAG + SPACE; 
		}
		else if(type.equals(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE))
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
		
		if(type.equals(RTFConstants.BOLD_TYPE))
		{
			tag = RTFTags.BOLD_START_TAG + SPACE; 
		}
		else if(type.equals(RTFConstants.ITALIC_TYPE))
		{
			tag = RTFTags.ITALIC_START_TAG + SPACE; 
		}
		else if(type.equals(RTFConstants.UNDERLINE_TYPE))
		{
			tag = RTFTags.UNDERLINE_START_TAG + SPACE; 
		}
		else if(type.equals(RTFConstants.BOLD_ITALIC_TYPE))
		{
			tag = RTFTags.BOLD_START_TAG + RTFTags.ITALIC_START_TAG + SPACE; 
		}
		else if(type.equals(RTFConstants.BOLD_UNDERLINE_TYPE))
		{
			tag = RTFTags.BOLD_START_TAG + RTFTags.UNDERLINE_START_TAG + SPACE; 
		}
		else if(type.equals(RTFConstants.ITALIC_UNDERLINE_TYPE))
		{
			tag = RTFTags.ITALIC_START_TAG + RTFTags.UNDERLINE_START_TAG + SPACE; 
		}
		else if(type.equals(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE))
		{
			tag = RTFTags.BOLD_START_TAG + RTFTags.ITALIC_START_TAG + RTFTags.UNDERLINE_START_TAG + SPACE; 
		}
		
		return tag;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.FileDocumentProvider#createAnnotationModel(java.lang.Object)
	 */
	@Override
	protected IAnnotationModel createAnnotationModel(Object element) throws CoreException
	{
		if(element instanceof RTFEditorInput)
		{
			return new RTFAnnotationModel((RTFEditorInput) element);
		}
		
		return super.createAnnotationModel(element);
	}
	
	private void pushState(RTFDocument document, String text, InputStream stream) throws IOException
	{
		ParserState state = new ParserState(fBoldTag != -1, fItalicTag != -1, fUnderlineTag != -1);
		fStack.push(state);
		handleTag(RTFTags.PLAIN, document, text, stream);
		if(state.isBold())
		{
			handleTag(RTFTags.BOLD_START, document, text, stream);
		}
		if(state.isItalic())
		{
			handleTag(RTFTags.ITALIC_START, document, text, stream);
		}
		if(state.isUnderline())
		{
			handleTag(RTFTags.UNDERLINE_START, document, text, stream);
		}
	}
	
	private void popState(RTFDocument document, String text, InputStream stream) throws IOException
	{
		handleTag(RTFTags.PLAIN, document, text, stream);
		ParserState state = fStack.pop();
		if(state.isBold())
		{
			handleTag(RTFTags.BOLD_START, document, text, stream);
		}
		if(state.isItalic())
		{
			handleTag(RTFTags.ITALIC_START, document, text, stream);
		}
		if(state.isUnderline())
		{
			handleTag(RTFTags.UNDERLINE_START, document, text, stream);
		}
	}
	
}

