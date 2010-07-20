/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jonathan Faubert
 *     Martin Robillard
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.editors.pages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.navigator.CommonNavigator;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.dialogs.NewCodeDialog;
import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.CodeEntry;
import ca.mcgill.cs.swevo.qualyzer.model.CodeListener;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.ProjectListener;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType;
import ca.mcgill.cs.swevo.qualyzer.model.validation.CodeValidator;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;

/**
 * The page for the code editor.
 */
public class CodeEditorPage extends FormPage implements CodeListener, ProjectListener
{
	
	/**
	 * 
	 */
	private static final String DELETE_CODE_KEY = "editors.pages.CodeEditorPage.deleteCode"; //$NON-NLS-1$

	private Project fProject;
	
	private ArrayList<Code> fCodes;
	private Code[] fModified;

	private Table fTable;

	private int fCurrentSelection;
	private Text fName;
	private Text fDescription;

	private boolean fIsDirty;

	private ScrolledForm fForm;

	/**
	 * Constructor.
	 * @param editor
	 */
	public CodeEditorPage(FormEditor editor, Project project)
	{
		super(editor, Messages.getString("editors.pages.CodeEditorPage.codeEditor"), //$NON-NLS-1$
				Messages.getString("editors.pages.CodeEditorPage.codeEditor")); //$NON-NLS-1$ 
		fProject = project;
		fCodes = new ArrayList<Code>();
		
		for(Code code : fProject.getCodes())
		{
			fCodes.add(code);
		}
				
		fIsDirty = false;
		fCurrentSelection = -1;
		
		fModified = new Code[fCodes.size()];
		clearModified();
		
		Facade.getInstance().getListenerManager().registerCodeListener(fProject, this);
		Facade.getInstance().getListenerManager().registerProjectListener(fProject, this);
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm)
	{
		fForm = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		Composite body = fForm.getBody();
		fForm.setText(Messages.getString("editors.pages.CodeEditorPage.codes")); //$NON-NLS-1$
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;
		body.setLayout(layout);
		
		fTable = toolkit.createTable(body, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		fTable.setLayoutData(gd);
		
		Composite composite = toolkit.createComposite(body, SWT.BORDER);
		layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		toolkit.createLabel(composite, Messages.getString("editors.pages.CodeEditorPage.name")); //$NON-NLS-1$
		fName = toolkit.createText(composite, ""); //$NON-NLS-1$
		fName.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		fName.addKeyListener(createKeyAdapter());
		fName.addKeyListener(createValidator());
		
		toolkit.createLabel(composite, Messages.getString("editors.pages.CodeEditorPage.description")); //$NON-NLS-1$
		fDescription = toolkit.createText(composite, "", SWT.MULTI); //$NON-NLS-1$
		fDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		fDescription.addKeyListener(createKeyAdapter());
		
		toolkit.paintBordersFor(composite);
		toolkit.paintBordersFor(body);
		
		buildFormTable();
		fTable.addSelectionListener(createTableSelectionListener());
		createTableContextMenu();
		
		fCurrentSelection = fTable.getSelectionIndex();
	}

	/**
	 * @return A keyadapter that acts as a validator for the new code name.
	 */
	private KeyAdapter createValidator()
	{
		return new KeyAdapter(){
			
			@Override
			public void keyReleased(KeyEvent e)
			{
				CodeValidator lValidator = new CodeValidator(fName.getText(), 
						fCodes.get(fCurrentSelection).getCodeName(), fProject);
				if(!lValidator.isValid())
				{
					if(fIsDirty)
					{
						fIsDirty = false;
						getEditor().editorDirtyStateChanged();
					}
					fForm.setMessage(lValidator.getErrorMessage(), IMessageProvider.ERROR);
				}
				else
				{
					fForm.setMessage(null, IMessageProvider.NONE);
				}
			}
		};
	}

	/**
	 * 
	 */
	private void createTableContextMenu()
	{
		Menu menu = new Menu(fTable);
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText(Messages.getString("editors.pages.CodeEditorPage.newCode")); //$NON-NLS-1$
		item.addSelectionListener(newCodeSelected()); 
		
		item = new MenuItem(menu, SWT.PUSH);
		item.setText(Messages.getString(DELETE_CODE_KEY));
		item.addSelectionListener(deleteCodeSelected());
		
		item = new MenuItem(menu, SWT.PUSH);
		item.setText("View Associated Fragments");
		item.addSelectionListener(viewFragmentsSelected());
		
		fTable.setMenu(menu);
	}

	/**
	 * @return
	 */
	private SelectionListener viewFragmentsSelected()
	{	
		return new SelectionAdapter(){
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				Code toView = null;
				
				TableItem item = fTable.getSelection()[0];
				
				for(Code code : fProject.getCodes())
				{
					if(code.getCodeName().equals(item.getText()))
					{
						toView = code;
						break;
					}
				}
				if(toView != null)
				{
					ResourcesUtil.openEditor(getSite().getPage(), toView);
				}
			}
		};
	}

	/**
	 * @return
	 */
	private SelectionAdapter deleteCodeSelected()
	{
		return new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				Code toDelete = fCodes.get(fTable.getSelectionIndex());
				List<Fragment> conflicts = detectConflicts(toDelete);
				boolean check = false;
				if(conflicts.size() == 0)
				{
					check = MessageDialog.openConfirm(getSite().getShell(), Messages.getString(DELETE_CODE_KEY),
					Messages.getString("editors.pages.CodeEditorPage.confirm")); //$NON-NLS-1$
				}
				else
				{
					check = MessageDialog.openConfirm(getSite().getShell(), Messages.getString(DELETE_CODE_KEY), 
							Messages.getString("editors.pages.CodeEditorPage.confirmMany") + //$NON-NLS-1$
							conflicts.size() + Messages.getString(
									"editors.pages.CodeEditorPage.confirmMany2")); //$NON-NLS-1$
					if(check)
					{
						for(Fragment fragment : conflicts)
						{
							for(int i = 0; i < fragment.getCodeEntries().size(); i++)
							{
								CodeEntry entry = fragment.getCodeEntries().get(i);
								if(entry.getCode().equals(toDelete))
								{
									fragment.getCodeEntries().remove(i);
									Facade.getInstance().saveTranscript((Transcript)fragment.getDocument());
									break;
								}
							}
							if(fragment.getCodeEntries().isEmpty())
							{
								Facade.getInstance().deleteFragment(fragment);
							}
						}
					}
				}
				if(check)
				{
					Facade.getInstance().deleteCode(toDelete);
					CommonNavigator view = (CommonNavigator) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
					view.getCommonViewer().refresh();
				}
			}
		};
	}

	/**
	 * @return
	 */
	private SelectionAdapter newCodeSelected()
	{
		return new SelectionAdapter(){
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				NewCodeDialog dialog = new NewCodeDialog(getEditor().getSite().getShell(), fProject);
				dialog.create();
				if(dialog.open() == Window.OK)
				{
					Facade.getInstance().createCode(dialog.getName(), dialog.getDescription(), fProject);
					fTable.setSelection(fCurrentSelection);
				}
			}
		};
	}

	/**
	 * @param toDelete
	 * @return
	 */
	protected List<Fragment> detectConflicts(Code toDelete)
	{
		List<Fragment> conflicts = new ArrayList<Fragment>();
		
		for(Transcript transcript : fProject.getTranscripts())
		{
			Transcript lTranscript = Facade.getInstance().forceTranscriptLoad(transcript);
			for(Fragment fragment : lTranscript.getFragments())
			{
				for(CodeEntry entry : fragment.getCodeEntries())
				{
					if(entry.getCode().equals(toDelete))
					{
						conflicts.add(fragment);
					}
				}
			}
		}
		
		return conflicts;
	}

	/**
	 * @return
	 */
	private KeyAdapter createKeyAdapter()
	{
		return new KeyAdapter(){
			
			@Override
			public void keyReleased(KeyEvent e)
			{
				if(!fIsDirty)
				{
					Code code = fCodes.get(fCurrentSelection);
					if(!fName.getText().equals(code.getCodeName()) || 
							!fDescription.getText().equals(code.getDescription()))
					{
						fIsDirty = true;
						getEditor().editorDirtyStateChanged();
					}
				}
			}
		};
	}

	/**
	 * Toggle the dirty state to clean.
	 */
	public void notDirty()
	{
		fIsDirty = false;
		clearModified();
		getEditor().editorDirtyStateChanged();
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
				
				if(fForm.getMessageType() == IMessageProvider.ERROR)
				{
					fTable.setSelection(fCurrentSelection);
					return;
				}
				
				if(index != fCurrentSelection)
				{
					if(fCurrentSelection != -1 && index != -1)
					{
						Code old = fCodes.get(fCurrentSelection);
						if(!old.getCodeName().equals(fName.getText()) || 
								!old.getDescription().equals(fDescription.getText()))
						{
							fModified[fCurrentSelection] = old;
							old.setCodeName(fName.getText());
							old.setDescription(fDescription.getText());
							fTable.getItem(fCurrentSelection).setText(fName.getText());
						}
					}
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
		Collections.sort(fCodes);
		
		for(Code code : fCodes)
		{
			TableItem item = new TableItem(fTable, SWT.NULL);
			item.setText(code.getCodeName());
		}
	}
	
	@Override
	public boolean isDirty()
	{
		return fIsDirty;
	}
	
	/**
	 * Get the list of Codes that have been modified.
	 * @return
	 */
	public Code[] getModifiedCodes()
	{
		Code current = fCodes.get(fCurrentSelection);
		if(!current.getCodeName().equals(fName.getText()) || !current.getDescription().equals(fDescription.getText()))
		{
			current.setCodeName(fName.getText());
			current.setDescription(fDescription.getText());
			fModified[fCurrentSelection] = current;
		}
		
		ArrayList<Code> codes = new ArrayList<Code>();
		for(Code code : fModified)
		{
			if(code != null)
			{
				codes.add(code);
			}
		}
		return codes.toArray(new Code[]{});
	}

	@Override
	public void codeChanged(ChangeType cType, Code[] codes, Facade facade)
	{
		if(cType == ChangeType.ADD || cType == ChangeType.DELETE)
		{
			fProject = codes[0].getProject();
			fTable.removeAll();
			fCodes.clear();
			for(Code code : fProject.getCodes())
			{
				fCodes.add(code);
			}
			clearModified();
			
			buildFormTable();
		}
		else if(cType == ChangeType.MODIFY)
		{
			for(Code code : codes)
			{
				int index = fCodes.indexOf(code);
				fTable.getItem(index).setText(code.getCodeName());
			}
		}
	}

	/**
	 * 
	 */
	private void clearModified()
	{
		fModified = new Code[fCodes.size()];
		for(int i = 0; i < fModified.length; i++)
		{
			fModified[i] = null;
		}
		
	}

	@Override
	public void projectChanged(ChangeType cType, Project project, Facade facade)
	{
		if(cType == ChangeType.DELETE)
		{
			IWorkbenchPage page = getEditor().getSite().getPage();
			ResourcesUtil.closeEditor(page, getEditor().getEditorInput().getName());
		}
		
	}
	
	@Override
	public void dispose()
	{
		Facade.getInstance().getListenerManager().unregisterCodeListener(fProject, this);
		Facade.getInstance().getListenerManager().unregisterProjectListener(fProject, this);
		super.dispose();
	}
}
