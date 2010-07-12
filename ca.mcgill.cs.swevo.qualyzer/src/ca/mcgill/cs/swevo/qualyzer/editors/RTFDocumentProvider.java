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

import ca.mcgill.cs.swevo.qualyzer.editors.inputs.RTFEditorInput;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;

/**
 * The DocumentProvider for our editor.
 *
 */
public class RTFDocumentProvider extends FileDocumentProvider
{
	
	/**
	 * 
	 */
	private static final String EMPTY = ""; //$NON-NLS-1$
	private int fBoldTag;
	private int fItalicTag;
	private int fUnderlineTag;
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.StorageDocumentProvider#createDocument(java.lang.Object)
	 */
	@Override
	protected IDocument createDocument(Object element) throws CoreException
	{
		RTFDocument doc = (RTFDocument) super.createDocument(element);
		
		Transcript transcript = ((RTFEditorInput) element).getTranscript();
		
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
						int count = 1;
						while(count >= 1)
						{
							ch = (char) contentStream.read();
							if(ch == '{')
							{
								count++;
							}
							else if(ch == '}')
							{
								count--;
							}
						}
					}
				}
				else if(ch == '\\')
				{
					String escape = " ";  //$NON-NLS-1$
					do
					{
						escape = nextTag(contentStream);
						text += handleTag(escape, (RTFDocument) document, text);
					}while(escape.charAt(escape.length() - 1) == '\\');
					
				}
				else if((!Character.isWhitespace(ch) && ch != '\0') || ch == ' ')
				{
					text += ch;
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		//It seems that some editors (wordpad) don't put ending tags if the style reaches the EOF
		if(fBoldTag != -1)
		{
			text += handleTag("b0", (RTFDocument)document, text); //$NON-NLS-1$
		}
		if(fItalicTag != -1)
		{
			text += handleTag("i0", (RTFDocument)document, text); //$NON-NLS-1$
		}
		if(fUnderlineTag != -1)
		{
			text += handleTag("ulnone", (RTFDocument)document, text); //$NON-NLS-1$
		}
		
		document.set(text);

	}
	
	private String handleTag(String escape, RTFDocument document, String currentText)
	{
		String string = escape.trim();
		if(escape.charAt(escape.length() - 1) == '\\')
		{
			string = escape.substring(0, escape.length() - 1);
		}
		string = string.trim();
		
		if(string.equals("par")) //$NON-NLS-1$
		{
			return "\n"; //$NON-NLS-1$
		}
		else if(string.equals("tab")) //$NON-NLS-1$
		{
			return "\t"; //$NON-NLS-1$
		}
		else if(string.equals("b")) //$NON-NLS-1$
		{
			fBoldTag = currentText.length();
			
			if(fItalicTag != -1 && fUnderlineTag != -1 && fItalicTag != fBoldTag)
			{
				Annotation annotation = new Annotation(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE, true, string);
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
		else if(string.equals("b0")) //$NON-NLS-1$
		{	
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
		else if(string.equals("i")) //$NON-NLS-1$
		{
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
		else if(string.equals("i0")) //$NON-NLS-1$
		{
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
		else if(string.equals("ul")) //$NON-NLS-1$
		{
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
		else if(string.equals("ulnone")) //$NON-NLS-1$
		{
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
		
		return EMPTY;
	}
	
	private String nextTag(InputStream ioStream) throws IOException
	{
		String escape = EMPTY;
		char ch2 = (char) ioStream.read();
		while(ch2 != ' ' && ch2 != '{' && ch2 != '}' && ch2 != '\\' && ch2 != '\n')
		{
			escape += ch2;
			ch2 = (char) ioStream.read();
		}
		
		if(ch2 == '\\')
		{
			escape += "\\"; //$NON-NLS-1$
		}
		
		if(ch2 == '{')
		{
			int count = 1;
			while(count >= 1)
			{
				ch2 = (char) ioStream.read();
				if(ch2 == '{')
				{
					count++;
				}
				else if(ch2 == '}')
				{
					count--;
				}
			}
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
					//Facade.getInstance().deleteFragment(fragment);
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
		
		//Facade.getInstance().saveTranscript(((RTFEditorInput) element).getTranscript());
	}

	/**
	 * @param contents
	 * @param model
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String buildRTFString(String contents, IAnnotationModel model)
	{
		String output = "{\\rtf1\\ansi\\deff0\n"; //$NON-NLS-1$
		
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
				output += "\\par \n"; //$NON-NLS-1$
			}
			else if(c == '\t')
			{
				output += "\\tab "; //$NON-NLS-1$
			}
			
		}
					
		return output + "\n}\n\0"; //$NON-NLS-1$
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
			tag = "\\b0 "; //$NON-NLS-1$
		}
		else if(type.equals(RTFConstants.ITALIC_TYPE))
		{
			tag = "\\i0 "; //$NON-NLS-1$
		}
		else if(type.equals(RTFConstants.UNDERLINE_TYPE))
		{
			tag = "\\ulnone "; //$NON-NLS-1$
		}
		else if(type.equals(RTFConstants.BOLD_ITALIC_TYPE))
		{
			tag = "\\b0\\i0 "; //$NON-NLS-1$
		}
		else if(type.equals(RTFConstants.BOLD_UNDERLINE_TYPE))
		{
			tag = "\\b0\\ulnone "; //$NON-NLS-1$
		}
		else if(type.equals(RTFConstants.ITALIC_UNDERLINE_TYPE))
		{
			tag = "\\i0\\ulnone "; //$NON-NLS-1$
		}
		else if(type.equals(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE))
		{
			tag = "\\b0\\i0\\ulnone "; //$NON-NLS-1$
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
			tag = "\\b "; //$NON-NLS-1$
		}
		else if(type.equals(RTFConstants.ITALIC_TYPE))
		{
			tag = "\\i "; //$NON-NLS-1$
		}
		else if(type.equals(RTFConstants.UNDERLINE_TYPE))
		{
			tag = "\\ul "; //$NON-NLS-1$
		}
		else if(type.equals(RTFConstants.BOLD_ITALIC_TYPE))
		{
			tag = "\\b\\i "; //$NON-NLS-1$
		}
		else if(type.equals(RTFConstants.BOLD_UNDERLINE_TYPE))
		{
			tag = "\\b\\ul "; //$NON-NLS-1$
		}
		else if(type.equals(RTFConstants.ITALIC_UNDERLINE_TYPE))
		{
			tag = "\\i\\ul "; //$NON-NLS-1$
		}
		else if(type.equals(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE))
		{
			tag = "\\b\\i\\ul "; //$NON-NLS-1$
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
	
}

