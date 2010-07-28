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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
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
import ca.mcgill.cs.swevo.qualyzer.model.ListenerManager;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.MemoListener;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.ProjectListener;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.model.TranscriptListener;
import ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType;
import ca.mcgill.cs.swevo.qualyzer.model.validation.CodeValidator;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;

/**
 * The page for the code editor.
 */
public class CodeEditorPage extends FormPage implements CodeListener, ProjectListener, TranscriptListener, MemoListener
{
	/**
	 * 
	 */
	private static final int BORDER_SIZE = 10;

	private static final String DELETE_CODE = Messages.getString(
			"editors.pages.CodeEditorPage.deleteCode"); //$NON-NLS-1$

	private Project fProject;
	
	private ArrayList<Code> fCodes;
	private Code[] fModified;
	private int[] fFrequency;

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
		
		clearModified();
		
		countFrequency();
		
		ListenerManager listenerManager = Facade.getInstance().getListenerManager();
		listenerManager.registerCodeListener(fProject, this);
		listenerManager.registerProjectListener(fProject, this);
		listenerManager.registerTranscriptListener(fProject, this);
		listenerManager.registerMemoListener(fProject, this);
	}
	
	/**
	 * 
	 */
	private void countFrequency()
	{
		fFrequency = new int[fCodes.size()];
		for(int i = 0; i < fFrequency.length; i++)
		{
			fFrequency[i] = 0;
		}
		
		for(Transcript transcript : fProject.getTranscripts())
		{
			Transcript lTranscript = Facade.getInstance().forceTranscriptLoad(transcript);
			for(Fragment fragment : lTranscript.getFragments())
			{
				for(CodeEntry entry : fragment.getCodeEntries())
				{
					int index = fCodes.indexOf(entry.getCode());
					if(index != -1)
					{
						fFrequency[index]++;
					}
				}
			}
		}
		
		for(Memo memo : fProject.getMemos())
		{
			Memo lMemo = Facade.getInstance().forceMemoLoad(memo);
			for(Fragment fragment : lMemo.getFragments())
			{
				for(CodeEntry entry : fragment.getCodeEntries())
				{
					int index = fCodes.indexOf(entry.getCode());
					if(index != -1)
					{
						fFrequency[index]++;
					}
				}
			}
		}
		
	}

	@Override
	protected void createFormContent(IManagedForm managedForm)
	{
		fForm = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		Composite body = fForm.getBody();
		fForm.setText(Messages.getString("editors.pages.CodeEditorPage.codes")); //$NON-NLS-1$
		
		GridLayout layout = new GridLayout(2, true);
		body.setLayout(layout);
		
		fTable = toolkit.createTable(body, SWT.BORDER | SWT.SINGLE);
		fTable.setLinesVisible(true);
		fTable.setHeaderVisible(true);
		TableColumn col1 = new TableColumn(fTable, SWT.NONE);
		col1.setText(Messages.getString("editors.pages.CodeEditorPage.codeName")); //$NON-NLS-1$
		col1.setMoveable(false);
		TableColumn col2 = new TableColumn(fTable, SWT.LEFT);
		col2.setText(Messages.getString("editors.pages.CodeEditorPage.frequency")); //$NON-NLS-1$
		col2.setMoveable(false);
		col2.setResizable(false);

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
		
		body.pack();
		
		toolkit.paintBordersFor(composite);
		toolkit.paintBordersFor(body);
		
		buildFormTable();
		fTable.addSelectionListener(createTableSelectionListener());
		createTableContextMenu();
		
		fCurrentSelection = fTable.getSelectionIndex();
	}
	
	@Override
	public void createPartControl(Composite parent)
	{
		super.createPartControl(parent);
		
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
		item.setText(DELETE_CODE);
		item.addSelectionListener(deleteCodeSelected());
		
		item = new MenuItem(menu, SWT.PUSH);
		item.setText(Messages.getString("editors.pages.CodeEditorPage.viewFragments")); //$NON-NLS-1$
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
					check = MessageDialog.openConfirm(getSite().getShell(), DELETE_CODE,
					Messages.getString("editors.pages.CodeEditorPage.confirm")); //$NON-NLS-1$
				}
				else
				{
					check = MessageDialog.openConfirm(getSite().getShell(), DELETE_CODE, 
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
									Facade.getInstance().saveDocument(fragment.getDocument());
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
					CommonNavigator view = (CommonNavigator) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
					view.getCommonViewer().refresh();
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
		
		for(Memo memo : fProject.getMemos())
		{
			Memo lMemo = Facade.getInstance().forceMemoLoad(memo);
			for(Fragment fragment : lMemo.getFragments())
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
		countFrequency();
		
		for(int i = 0; i < fCodes.size(); i++)
		{
			Code code = fCodes.get(i);
			TableItem item = new TableItem(fTable, SWT.NULL);
			item.setText(0, code.getCodeName());
			item.setText(1, ""+fFrequency[i]); //$NON-NLS-1$
		}
		
		fTable.getColumn(0).pack();
		fTable.getColumn(1).pack();
		Rectangle rect = fTable.getBounds();
		Rectangle newRect = new Rectangle(rect.x, rect.y, rect.width, rect.height);
		fTable.pack();
		newRect.height = fTable.getBounds().height;
		newRect.width = rect.width > fTable.getBounds().width ? rect.width : fTable.getBounds().width;

		if(newRect.height + BORDER_SIZE <= fTable.getParent().getBounds().height)
		{
			fTable.setBounds(newRect);
		}
		else
		{
			newRect.height = fTable.getParent().getBounds().height - BORDER_SIZE;
			fTable.setBounds(newRect);
		}
		
		int width = fTable.getBounds().width - 2;
		fTable.getColumn(0).setWidth(width -width/2);
		fTable.getColumn(1).setWidth(width/2);

		fForm.getBody().redraw();
		fForm.update();
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
		fProject = PersistenceManager.getInstance().getProject(fProject.getName());
		fTable.removeAll();
		fCodes.clear();
		for(Code code : fProject.getCodes())
		{
			fCodes.add(code);
		}
		clearModified();
		
		buildFormTable();
		
		fCurrentSelection = fTable.getSelectionIndex();
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
		ListenerManager listenerManager = Facade.getInstance().getListenerManager();
		listenerManager.unregisterCodeListener(fProject, this);
		listenerManager.unregisterProjectListener(fProject, this);
		listenerManager.unregisterTranscriptListener(fProject, this);
		listenerManager.unregisterMemoListener(fProject, this);
		super.dispose();
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.model.TranscriptListener#transcriptChanged(
	 * ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType, 
	 * ca.mcgill.cs.swevo.qualyzer.model.Transcript[], ca.mcgill.cs.swevo.qualyzer.model.Facade)
	 */
	@Override
	public void transcriptChanged(ChangeType cType, Transcript[] transcripts, Facade facade)
	{
		if(cType == ChangeType.MODIFY || cType == ChangeType.DELETE)
		{
			fProject = PersistenceManager.getInstance().getProject(fProject.getName());
			fTable.removeAll();
			fCodes.clear();
			for(Code code : fProject.getCodes())
			{
				fCodes.add(code);
			}
			clearModified();
			
			buildFormTable();
			fCurrentSelection = fTable.getSelectionIndex();
		}
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.model.MemoListener#memoChanged(
	 * ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType, ca.mcgill.cs.swevo.qualyzer.model.Memo[],
	 *  ca.mcgill.cs.swevo.qualyzer.model.Facade)
	 */
	@Override
	public void memoChanged(ChangeType cType, Memo[] memos, Facade facade)
	{
		if(cType == ChangeType.MODIFY || cType == ChangeType.DELETE)
		{
			fProject = PersistenceManager.getInstance().getProject(fProject.getName());
			fTable.removeAll();
			fCodes.clear();
			for(Code code : fProject.getCodes())
			{
				fCodes.add(code);
			}
			clearModified();
			
			buildFormTable();
			fCurrentSelection = fTable.getSelectionIndex();
		}
		
	}
}
