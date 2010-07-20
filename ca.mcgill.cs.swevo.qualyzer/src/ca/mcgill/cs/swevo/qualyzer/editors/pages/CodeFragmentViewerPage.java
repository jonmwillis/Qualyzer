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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
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
	private Code fCode;
	private ScrolledForm fForm;
	
	/**
	 * 
	 */
	public CodeFragmentViewerPage(FormEditor editor, Code code)
	{
		super(editor, "View Fragments", "View Fragments");
		fCode = code;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm)
	{
		fForm = managedForm.getForm();
		fForm.setText("View all fragments associated with " + fCode.getCodeName());
		Composite body = fForm.getBody();
		FormToolkit toolkit = managedForm.getToolkit();
		
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 1;
		body.setLayout(layout);
		
		Project project = fCode.getProject();
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
		sectionClient.setLayout(new GridLayout());
		sectionClient.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		RTFDocumentProvider provider = new RTFDocumentProvider();
		
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(transcript.getProject().getName());
		IFile file = project.getFile("transcripts" + File.separator + transcript.getFileName());
		RTFEditorInput input = new RTFEditorInput(file, Facade.getInstance().forceDocumentLoad(transcript));
		IDocument document = provider.getCreatedDocument(input);
		
		String text = document.get();
		
		sort(contents);
		
		for(Fragment fragment : contents)
		{
			String fragText = text.substring(fragment.getOffset(), fragment.getOffset() + fragment.getLength());
			Label label = toolkit.createLabel(sectionClient, fragText, SWT.BORDER);
			label.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		}
		
		section.setClient(sectionClient);
	}

	/**
	 * @param contents
	 */
	private void sort(ArrayList<Fragment> contents)
	{
		// TODO Auto-generated method stub
		
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
}
