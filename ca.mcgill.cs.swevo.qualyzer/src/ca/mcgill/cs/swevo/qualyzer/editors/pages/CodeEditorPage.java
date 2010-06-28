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

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 *
 */
public class CodeEditorPage extends FormPage
{
	
	private Project fProject;
	
	private ArrayList<Code> fCodes;

	private Table fTable;

	private int fCurrentSelection;
	private Text fName;
	private Text fDescription;

	/**
	 * Constructor.
	 * @param editor
	 */
	public CodeEditorPage(FormEditor editor, Project project)
	{
		super(editor, "Code Editor", "Code Editor");
		fProject = project;
		fCodes = new ArrayList<Code>();
		
		Code aCode = new Code();
		aCode.setCodeName("Code Name");
		aCode.setDescription("Code Description\n\n\n\nReally long.");
		aCode.setProject(fProject);
		
		fProject.getCodes().add(aCode);
		
		for(Code code : fProject.getCodes())
		{
			fCodes.add(code);
		}
		
		fCurrentSelection = -1;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm)
	{
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		Composite body = form.getBody();
		form.setText("Codes");
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		body.setLayout(layout);
		
		fTable = toolkit.createTable(body, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		fTable.setLayoutData(gd);
		
		Composite composite = toolkit.createComposite(body, SWT.BORDER);
		layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		toolkit.createLabel(composite, "Name:");
		fName = toolkit.createText(composite, "");
		fName.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		
		toolkit.createLabel(composite, "Description:");
		fDescription = toolkit.createText(composite, "", SWT.MULTI);
		fDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		toolkit.paintBordersFor(composite);
		toolkit.paintBordersFor(body);
		
		buildFormTable();
		fTable.addSelectionListener(createTableSelectionListener());
	}

	/**
	 * @return
	 */
	private SelectionAdapter createTableSelectionListener()
	{
		return new SelectionAdapter(){
		
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				int index = fTable.getSelectionIndex();
				
				if(index != fCurrentSelection)
				{
					//TODO save old selection
					fCurrentSelection = index;
					Code code = fCodes.get(fCurrentSelection);
					fName.setText(code.getCodeName());
					fDescription.setText(code.getDescription());
				}
			}
		};
	}

	/**
	 * 
	 */
	private void buildFormTable()
	{
		for(Code code : fCodes)
		{
			TableItem item = new TableItem(fTable, SWT.NULL);
			item.setText(code.getCodeName());
		}
		
	}
}
