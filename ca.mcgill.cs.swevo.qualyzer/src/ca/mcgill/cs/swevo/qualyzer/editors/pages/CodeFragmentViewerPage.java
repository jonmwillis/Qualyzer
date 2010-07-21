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
package ca.mcgill.cs.swevo.qualyzer.editors.pages;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import ca.mcgill.cs.swevo.qualyzer.editors.ColorManager;
import ca.mcgill.cs.swevo.qualyzer.editors.RTFDocumentProvider;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.RTFEditorInput;
import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.CodeEntry;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;

/**
 *
 */
public class CodeFragmentViewerPage extends FormPage
{
	
	private static final String VIEW_FRAGMENTS = Messages.getString(
			"editors.pages.CodeFragmentViewerPage.viewFragments"); //$NON-NLS-1$
	private Code fCode;
	private ScrolledForm fForm;
	private ColorManager fManager;
	
	/**
	 * 
	 */
	public CodeFragmentViewerPage(FormEditor editor, Code code)
	{
		super(editor, VIEW_FRAGMENTS, VIEW_FRAGMENTS); 
		fCode = code;
		fManager = new ColorManager();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm)
	{
		fForm = managedForm.getForm();
		fForm.setText(Messages.getString("editors.pages.CodeFragmentViewerPage.viewAllFragments") + //$NON-NLS-1$
				fCode.getCodeName()); 
		Composite body = fForm.getBody();
		FormToolkit toolkit = managedForm.getToolkit();
		
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 1;
		body.setLayout(layout);
		
		Project project = fCode.getProject();
		Collections.sort(project.getTranscripts());
		for(Transcript transcript : project.getTranscripts())
		{
			ArrayList<Fragment> contents = findFragments(transcript);
			
			if(!contents.isEmpty())
			{
				buildSection(transcript, contents, toolkit, body);
			}
		}
	}

	/**
	 * @param transcript
	 * @param contents
	 * @param toolkit
	 * @param body
	 */
	private void buildSection(Transcript transcript, ArrayList<Fragment> contents, FormToolkit toolkit, Composite body)
	{
		Section section = toolkit.createSection(body, Section.EXPANDED | Section.TITLE_BAR | Section.TWISTIE);
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		
		section.setText(transcript.getName());
		section.addExpansionListener(new ExpansionAdapter(){

			@Override
			public void expansionStateChanged(ExpansionEvent e)
			{
				fForm.reflow(true);
			}
		});
		
		Composite sectionClient = toolkit.createComposite(section);
		sectionClient.setLayout(new TableWrapLayout());
		sectionClient.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		
		RTFDocumentProvider provider = new RTFDocumentProvider();
		
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(transcript.getProject().getName());
		IFile file = project.getFile(Messages.getString(
				"editors.pages.CodeFragmentViewerPage.transcripts") + //$NON-NLS-1$
				File.separator + transcript.getFileName()); 
		RTFEditorInput input = new RTFEditorInput(file, Facade.getInstance().forceDocumentLoad(transcript));
		IDocument document = provider.getCreatedDocument(input);
		
		String text = document.get();
		
		sort(contents);
		
		for(Fragment fragment : contents)
		{
			createTextBox(sectionClient, text, fragment);
		}
		
		section.setClient(sectionClient);
	}

	/**
	 * @param sectionClient
	 * @param text
	 * @param fragment
	 */
	private void createTextBox(Composite sectionClient, String text, Fragment fragment)
	{
		int start = fragment.getOffset();
		while(start > 0 && text.charAt(start-1) != '\n' && text.charAt(start-1) != '.' && text.charAt(start-1) != '\t')
		{
			start--;
		}
		
		int end = fragment.getOffset() + fragment.getLength();
		while(end < text.length() && text.charAt(end) != '\n' && text.charAt(end) != '.' && text.charAt(end) != '\t')
		{
			end++;
		}
		
		String fragText = text.substring(start, end);
		StyledText style = new StyledText(sectionClient, SWT.READ_ONLY | SWT.WRAP | SWT.MULTI | SWT.BORDER);
		style.setText(fragText);
		style.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		
		StyleRange range = new StyleRange();
		range.start = fragment.getOffset() - start;
		range.length = fragment.getLength();
		range.underline = true;
		range.underlineStyle = SWT.UNDERLINE_SINGLE;
		range.underlineColor = fManager.getColor(ColorManager.TAG);
		range.foreground = fManager.getColor(ColorManager.TAG);
		style.setStyleRange(range);
	}

	/**
	 * @param contents
	 */
	private void sort(ArrayList<Fragment> contents)
	{
		Collections.sort(contents, new Comparator<Fragment>(){

			@Override
			public int compare(Fragment o1, Fragment o2)
			{
				return o1.getOffset() - o2.getOffset();
			}
		});
		
	}

	/**
	 * @param transcript
	 * @return
	 */
	private ArrayList<Fragment> findFragments(Transcript transcript)
	{
		Transcript lTranscript = Facade.getInstance().forceTranscriptLoad(transcript);
		ArrayList<Fragment> toReturn = new ArrayList<Fragment>();
		
		for(Fragment fragment : lTranscript.getFragments())
		{
			for(CodeEntry entry : fragment.getCodeEntries())
			{
				if(entry.getCode().equals(fCode))
				{
					toReturn.add(fragment);
				}
			}
		}
		return toReturn;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormPage#dispose()
	 */
	@Override
	public void dispose()
	{
		fManager.dispose();
		super.dispose();
	}
}
