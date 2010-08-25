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
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.navigator.CommonNavigator;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.dialogs.NewCodeDialog;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.CodeTableInput;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.CodeTableInput.CodeTableRow;
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
import ca.mcgill.cs.swevo.qualyzer.providers.CodeTableContentProvider;
import ca.mcgill.cs.swevo.qualyzer.providers.CodeTableLabelProvider;
import ca.mcgill.cs.swevo.qualyzer.providers.CodeTreeContentProvider;
import ca.mcgill.cs.swevo.qualyzer.providers.CodeTreeLabelProvider;
import ca.mcgill.cs.swevo.qualyzer.providers.Node;
import ca.mcgill.cs.swevo.qualyzer.providers.TableDragListener;
import ca.mcgill.cs.swevo.qualyzer.providers.TreeDropListener;
import ca.mcgill.cs.swevo.qualyzer.providers.TreeModel;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;

/**
 * The page for the code editor.
 */
public class CodeEditorPage extends FormPage implements CodeListener, ProjectListener, TranscriptListener, MemoListener
{

	private static final GridData LARGE_LAYOUT = new GridData(SWT.FILL, SWT.FILL, true, true);
	private static final GridData SMALL_LAYOUT = new GridData(SWT.FILL, SWT.NULL, true, false);
	private static final int NAME_WIDTH = 120;
	private static final int FREQ_WIDTH = 80;
	private static final int TREE_NAME_WIDTH = 100;
	private static final int TREE_FREQ_WIDTH = 60;
	private static final String EMPTY = ""; //$NON-NLS-1$
	private static final int THRESHHOLD = 18;

	private static final String DELETE_CODE = Messages.getString(
			"editors.pages.CodeEditorPage.deleteCode"); //$NON-NLS-1$

	private Project fProject;

	private TableViewer fTableViewer;
	private CodeTableSorter fSorter;
	private CodeTableRow fCurrentRow;

	private Text fName;
	private StyledText fDescription;

	private boolean fIsDirty;

	private ScrolledForm fForm;
	private Composite fNameArea;
	private TreeViewer fTreeViewer;
	private Composite fTreeArea;
	private Composite fTableArea;

	/**
	 * Constructor.
	 * @param editor
	 */
	public CodeEditorPage(FormEditor editor, Project project)
	{
		super(editor, Messages.getString("editors.pages.CodeEditorPage.codeEditor"), //$NON-NLS-1$
				Messages.getString("editors.pages.CodeEditorPage.codeEditor")); //$NON-NLS-1$ 
		fProject = project;
				
		fIsDirty = false;
						
		ListenerManager listenerManager = Facade.getInstance().getListenerManager();
		listenerManager.registerCodeListener(fProject, this);
		listenerManager.registerProjectListener(fProject, this);
		listenerManager.registerTranscriptListener(fProject, this);
		listenerManager.registerMemoListener(fProject, this);
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
		
		fTableArea = toolkit.createComposite(body);
		fTableArea.setLayout(new GridLayout(1, true));
		fTableArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Button button = toolkit.createButton(fTableArea, "Show Hierachies", SWT.TOGGLE);
		buildTableViewer(fTableArea);

		if(fProject.getCodes().size() < THRESHHOLD)
		{
			fTableViewer.getTable().setLayoutData(SMALL_LAYOUT);
		}
		else
		{
			fTableViewer.getTable().setLayoutData(LARGE_LAYOUT);
		}
		
		Composite composite = toolkit.createComposite(body, SWT.BORDER);
		StackLayout sLayout = new StackLayout();
		composite.setLayout(sLayout);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		fNameArea = createNameDescriptionArea(toolkit, composite);
		fTreeArea = createTreeViewer(toolkit, composite);
		sLayout.topControl = fNameArea;
		
		toolkit.paintBordersFor(composite);
		toolkit.paintBordersFor(body);
		toolkit.paintBordersFor(fNameArea);
		
		fTableViewer.addSelectionChangedListener(createTableSelectionListener());
		createTableContextMenu();
		button.addSelectionListener(createToggleAdapter(button, sLayout, composite));
	}

	/**
	 * @param sLayout
	 * @return
	 */
	private SelectionListener createToggleAdapter(final Button button, final StackLayout sLayout,
			final Composite composite)
	{
		return new SelectionAdapter(){
			
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if(button.getSelection())
				{
					sLayout.topControl = fTreeArea;
					composite.layout();
				}
				else
				{
					sLayout.topControl = fNameArea;
					composite.layout();
				}
			}
		};
	}

	/**
	 * @param composite
	 * @return
	 */
	private Composite createTreeViewer(FormToolkit toolkit, Composite parent)
	{
		Composite composite = toolkit.createComposite(parent);
		composite.setLayout(new GridLayout(1, true));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		fTreeViewer = new TreeViewer(composite, SWT.SINGLE | SWT.FULL_SELECTION | SWT.V_SCROLL);
		
		Tree tree = fTreeViewer.getTree();
		TreeColumn col = new TreeColumn(tree, SWT.NONE);
		col.setText("Code");
		col.setWidth(TREE_NAME_WIDTH);
		
		col = new TreeColumn(tree, SWT.NONE);
		col.setText("Count");
		col.setWidth(TREE_FREQ_WIDTH);
		
		col = new TreeColumn(tree, SWT.NONE);
		col.setText("Total Count");
		col.setWidth(TREE_FREQ_WIDTH);
		
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		fTreeViewer.setContentProvider(new CodeTreeContentProvider());
		fTreeViewer.setLabelProvider(new CodeTreeLabelProvider());
		fTreeViewer.setInput(TreeModel.getTreeModel(fProject).getRoot());
		fTreeViewer.addDropSupport(DND.DROP_COPY | DND.DROP_MOVE, 
				new Transfer[]{TextTransfer.getInstance()}, new TreeDropListener(fTreeViewer, this));
		
		fTreeViewer.setSorter(new ViewerSorter());
		
		createTreeContextMenu();
		
		return composite;
	}

	/**
	 * 
	 */
	private void createTreeContextMenu()
	{
		Menu menu = new Menu(fTreeViewer.getTree());
		
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText("Remove Code");
		item.addSelectionListener(new SelectionAdapter(){
			
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				IStructuredSelection selection = (IStructuredSelection) fTreeViewer.getSelection();
				Node node = (Node) selection.getFirstElement();
				
				node.getParent().getChildren().remove(node.getPersistenceId());
				fTreeViewer.refresh();
				setDirty();
			}
		});
		
		fTreeViewer.getTree().setMenu(menu);
	}

	/**
	 * @param toolkit
	 * @param composite
	 */
	private Composite createNameDescriptionArea(FormToolkit toolkit, Composite parent)
	{
		Composite composite = toolkit.createComposite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		toolkit.createLabel(composite, Messages.getString("editors.pages.CodeEditorPage.name")); //$NON-NLS-1$
		fName = toolkit.createText(composite, EMPTY); //$NON-NLS-1$
		fName.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		fName.addKeyListener(createKeyAdapter());
		fName.addKeyListener(createValidator());
		
		toolkit.createLabel(composite, Messages.getString("editors.pages.CodeEditorPage.description")); //$NON-NLS-1$
		fDescription = new StyledText(composite, SWT.WRAP | SWT.BORDER); //$NON-NLS-1$
		fDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		fDescription.addKeyListener(createKeyAdapter());
		
		return composite;
	}

	/**
	 * @param body 
	 * 
	 */
	private void buildTableViewer(Composite body)
	{
		fTableViewer = new TableViewer(body, SWT.SINGLE |  SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL);
		
		TableColumn col = new TableColumn(fTableViewer.getTable(), SWT.NONE);
		col.setText("Code Name");
		col.setWidth(NAME_WIDTH);
		col.addSelectionListener(createColSortListener(0, col));
		col.setMoveable(false);
		
		col = new TableColumn(fTableViewer.getTable(), SWT.NONE);
		col.setText("Frequency");
		col.setWidth(FREQ_WIDTH);
		col.addSelectionListener(createColSortListener(1, col));
		col.setMoveable(false);
		
		fTableViewer.setContentProvider(new CodeTableContentProvider());
		fTableViewer.setLabelProvider(new CodeTableLabelProvider());
		fTableViewer.setInput(new CodeTableInput(fProject));
		fTableViewer.getTable().setHeaderVisible(true);
		fSorter = new CodeTableSorter();
		fTableViewer.setSorter(fSorter);
		fTableViewer.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE, 
				new Transfer[]{TextTransfer.getInstance()}, new TableDragListener(fTableViewer));
	}

	/**
	 * @param i
	 * @return
	 */
	private SelectionListener createColSortListener(final int colIndex, final TableColumn column)
	{
		return new SelectionAdapter()
		{
			
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				fSorter.setColumn(colIndex);
				
				int dir = fTableViewer.getTable().getSortDirection();
				if(fTableViewer.getTable().getSortColumn() == column)
				{
					dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
				}
				else
				{
					dir = SWT.UP;
				}
				
				fTableViewer.getTable().setSortColumn(column);
				fTableViewer.getTable().setSortDirection(dir);
				
				fTableViewer.refresh();
			}
		};
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
				IStructuredSelection sel = (IStructuredSelection) fTableViewer.getSelection();
				if(sel.getFirstElement() == null)
				{
					return;
				}
				CodeValidator lValidator = new CodeValidator(fName.getText().trim(), 
						fCurrentRow.getCode().getCodeName(), fProject);
				if(!lValidator.isValid())
				{
					if(fIsDirty)
					{
						fIsDirty = false;
						getEditor().editorDirtyStateChanged();
					}
					fForm.getForm().setMessage(lValidator.getErrorMessage(), IMessageProvider.ERROR);
				}
				else
				{
					fForm.getForm().setMessage(null, IMessageProvider.NONE);
				}
			}
		};
	}

	/**
	 * Builds the context menu that gives access to the New Code, Delete Code, and View Fragments actions.
	 */
	private void createTableContextMenu()
	{
		Menu menu = new Menu(fTableViewer.getTable());
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText(Messages.getString("editors.pages.CodeEditorPage.newCode")); //$NON-NLS-1$
		item.addSelectionListener(newCodeSelected()); 
		
		item = new MenuItem(menu, SWT.PUSH);
		item.setText(DELETE_CODE);
		item.addSelectionListener(deleteCodeSelected());
		
		item = new MenuItem(menu, SWT.PUSH);
		item.setText(Messages.getString("editors.pages.CodeEditorPage.viewFragments")); //$NON-NLS-1$
		item.addSelectionListener(viewFragmentsSelected());
		
		fTableViewer.getTable().setMenu(menu);
	}

	/**
	 * Handles the selection of the View Associated Fragments Action.
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
				CodeTableRow row = (CodeTableRow)((IStructuredSelection) fTableViewer.getSelection()).getFirstElement();
				
				Code toView = row.getCode();
				
				if(toView != null)
				{
					ResourcesUtil.openEditor(getSite().getPage(), toView);
				}
			}
		};
	}

	/**
	 * Handles the selection of the Delete Code Action.
	 * Checks if there are any memos stopping the deletion.
	 * Then finds all the fragments that contain the code.
	 * Displays a warning/confirmation.
	 * Removes the code from all associated fragments and then deletes the code.
	 * @return
	 */
	private SelectionAdapter deleteCodeSelected()
	{
		return new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				IStructuredSelection selection = (IStructuredSelection) fTableViewer.getSelection();
				Code toDelete = ((CodeTableRow) selection.getFirstElement()).getCode();
				
				List<Memo> hardConflicts = detectHardConflicts(toDelete);
				if(!hardConflicts.isEmpty())
				{
					String message = buildErrorString(hardConflicts);
					MessageDialog.openError(getSite().getShell(), Messages.getString(
							"editors.pages.CodeEditorPage.unableToDelete"), message); //$NON-NLS-1$
					return;
				}
				
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
						removeCodeFromFragments(toDelete, conflicts);
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
	 * @param conflicts 
	 * @return
	 */
	protected String buildErrorString(List<Memo> conflicts)
	{
		String message = Messages.getString("editors.pages.CodeEditorPage.conflicts"); //$NON-NLS-1$
		
		for(Memo memo : conflicts)
		{
			message += Messages.getString("editors.pages.CodeEditorPage.memo") + memo.getName(); //$NON-NLS-1$
		}
		
		return message;
	}

	/**
	 * Goes through the fragments and removes the code from them, saving each one.
	 * @param toDelete
	 * @param conflicts
	 */
	private void removeCodeFromFragments(Code toDelete, List<Fragment> conflicts)
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
	
	/**
	 * Finds any memos that reference the code to be deleted.
	 * @param code the code that will be deleted.
	 * @return The list of memos "about" it.
	 */
	private List<Memo> detectHardConflicts(Code code)
	{
		List<Memo> memos = new ArrayList<Memo>();
		
		for(Memo memo : fProject.getMemos())
		{
			if(code.equals(memo.getCode()))
			{
				memos.add(memo);
			}
		}
		
		return memos;
	}

	/**
	 * Handles the selection of the new code action.
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
					CommonNavigator view = (CommonNavigator) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
					view.getCommonViewer().refresh();
				}
			}
		};
	}

	/**
	 * Finds all the fragments that reference this code.
	 * @param toDelete
	 * @return
	 */
	protected List<Fragment> detectConflicts(Code toDelete)
	{
		List<Fragment> conflicts = new ArrayList<Fragment>();
		
		for(Transcript transcript : fProject.getTranscripts())
		{
			Transcript lTranscript = Facade.getInstance().forceTranscriptLoad(transcript);
			for(Fragment fragment : lTranscript.getFragments().values())
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
			for(Fragment fragment : lMemo.getFragments().values())
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
	 * Handles updating the dirty state.
	 * @return
	 */
	private KeyListener createKeyAdapter()
	{
		return new KeyAdapter(){

			@Override
			public void keyReleased(KeyEvent e)
			{
				if(!fIsDirty && (!fName.getText().equals(fCurrentRow.getName()) || 
						!fDescription.getText().equals(fCurrentRow.getDescription())))
				{				
					fIsDirty = true;
					getEditor().editorDirtyStateChanged();
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
		getEditor().editorDirtyStateChanged();
	}
	
	/**
	 * Updates the Name and Description boxes on the right as the selected item in the table changes.
	 * @return
	 */
	private ISelectionChangedListener createTableSelectionListener()
	{
		return new ISelectionChangedListener()
		{
			
			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				IStructuredSelection selection = (IStructuredSelection) fTableViewer.getSelection();
				CodeTableRow row = (CodeTableRow) selection.getFirstElement();
				
				if(row == null)
				{
					fName.setText(EMPTY);
					fDescription.setText(EMPTY);
					return;
				}
				
				if(fForm.getMessageType() == IMessageProvider.ERROR)
				{
					fTableViewer.setSelection(new StructuredSelection(fCurrentRow));
				}
				
				if(row != fCurrentRow)
				{
					if(fCurrentRow != null)
					{
						fCurrentRow.setName(fName.getText().trim());
						fCurrentRow.setDescription(fDescription.getText().trim());
						fTableViewer.refresh(fCurrentRow);
					}
				}
				
				fCurrentRow = row;
				fName.setText(fCurrentRow.getName());
				fDescription.setText(fCurrentRow.getDescription());
			}

		};
	}

	
	@Override
	public boolean isDirty()
	{
		return fIsDirty;
	}

	@Override
	public void codeChanged(ChangeType cType, Code[] codes, Facade facade)
	{
		fProject = PersistenceManager.getInstance().getProject(fProject.getName());

		fTableViewer.setInput(new CodeTableInput(fProject));
		
		updateSelection();
		
		if(fProject.getCodes().size() > THRESHHOLD)
		{
			fTableViewer.getTable().setLayoutData(LARGE_LAYOUT);
		}
		else
		{
			fTableViewer.getTable().setLayoutData(SMALL_LAYOUT);
		}
		
		fTableArea.layout();
		fTableArea.redraw();
		fForm.getBody().layout();
		fForm.getBody().redraw();
	}

	/**
	 * 
	 */
	private void updateSelection()
	{
		CodeTableRow row;
		if(fCurrentRow == null)
		{
			row = (CodeTableRow) fTableViewer.getElementAt(0);
		}
		else
		{
			int index = 0;
			while((row = (CodeTableRow) fTableViewer.getElementAt(index)) != null)
			{
				if(row.getName().equals(fCurrentRow.getName()))
				{
					break;
				}
				index++;
			}
		}
		if(row == null)
		{
			row = (CodeTableRow) fTableViewer.getElementAt(0);
		}
		
		if(row != null)
		{
			fTableViewer.setSelection(new StructuredSelection(row));
		}
	}

	@Override
	public void projectChanged(ChangeType cType, Project project, Facade facade)
	{
		if(cType == ChangeType.DELETE)
		{
			getEditor().close(false);
		}
		else if(cType == ChangeType.RENAME)
		{
			ResourcesUtil.closeEditor(getSite().getPage(), getEditorInput().getName());
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
			
			fTableViewer.setInput(new CodeTableInput(fProject));
			
			updateSelection();
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
			
			fTableViewer.setInput(new CodeTableInput(fProject));
			
			updateSelection();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public Code[] getCodes()
	{
		if(fCurrentRow != null)
		{
			fCurrentRow.setName(fName.getText().trim());
			fCurrentRow.setDescription(fDescription.getText().trim());
			fTableViewer.refresh(fCurrentRow);
		}
		
		List<Code> codes = new ArrayList<Code>();
		
		int index = 0;
		CodeTableRow row = (CodeTableRow) fTableViewer.getElementAt(index);
		
		while(row != null)
		{
			Code codeToSave = row.getCodeToSave();
			if(codeToSave != null)
			{
				codes.add(codeToSave);
			}
			
			index++;
			row = (CodeTableRow) fTableViewer.getElementAt(index);
		}
		
		return codes.toArray(new Code[0]);
			
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormPage#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor)
	{
		TreeModel.getTreeModel(fProject).save();
	}

	/**
	 * 
	 */
	public void setDirty()
	{
		if(!fIsDirty)
		{
			fIsDirty = true;
			getEditor().editorDirtyStateChanged();
		}
		
	}
}
