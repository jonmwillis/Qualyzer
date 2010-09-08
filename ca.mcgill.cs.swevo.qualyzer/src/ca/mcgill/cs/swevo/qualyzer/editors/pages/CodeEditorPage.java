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
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TableColumn;
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
import ca.mcgill.cs.swevo.qualyzer.dialogs.RenameCodeDialog;
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
import ca.mcgill.cs.swevo.qualyzer.providers.CodeTableContentProvider;
import ca.mcgill.cs.swevo.qualyzer.providers.CodeTableLabelProvider;
import ca.mcgill.cs.swevo.qualyzer.providers.CodeTreeContentProvider;
import ca.mcgill.cs.swevo.qualyzer.providers.CodeTreeLabelProvider;
import ca.mcgill.cs.swevo.qualyzer.providers.Node;
import ca.mcgill.cs.swevo.qualyzer.providers.TableDragListener;
import ca.mcgill.cs.swevo.qualyzer.providers.TableFilter;
import ca.mcgill.cs.swevo.qualyzer.providers.TreeDragListener;
import ca.mcgill.cs.swevo.qualyzer.providers.TreeDropListener;
import ca.mcgill.cs.swevo.qualyzer.providers.TreeModel;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;

/**
 * The page for the code editor.
 */
public class CodeEditorPage extends FormPage implements CodeListener, ProjectListener, TranscriptListener, MemoListener
{

	private static final int FONT_SIZE = 10;
	private static final int DESCRIPTION_HEIGHT = 15;
	private static final GridData LARGE_LAYOUT = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
	private static final int NAME_WIDTH = 180;
	private static final int FREQ_WIDTH = 80;
	private static final int TREE_NAME_WIDTH = 180;
	private static final int TREE_FREQ_WIDTH = 60;
	private static final String EMPTY = ""; //$NON-NLS-1$

	private static final String DELETE_CODE = Messages.getString(
			"editors.pages.CodeEditorPage.deleteCode"); //$NON-NLS-1$

	private Project fProject;

	private TableViewer fTableViewer;
	private CodeTableSorter fSorter;
	private CodeTableRow fCurrentRow;

	private Label fCodeName;
	private StyledText fDescription;

	private boolean fIsDirty;

	private ScrolledForm fForm;
	private TreeViewer fTreeViewer;
	private Composite fTreeArea;
	private Composite fTableArea;
	private TreeModel fTreeModel;
	private Button fFilterButton;
	
	private ArrayList<MenuItem> fTableCanDisable;
	private ArrayList<MenuItem> fTreeCanDisable;

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
		fTableCanDisable = new ArrayList<MenuItem>();
		fTreeCanDisable = new ArrayList<MenuItem>();
		
		ListenerManager listenerManager = Facade.getInstance().getListenerManager();
		listenerManager.registerCodeListener(fProject, this);
		listenerManager.registerProjectListener(fProject, this);
		listenerManager.registerTranscriptListener(fProject, this);
		listenerManager.registerMemoListener(fProject, this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm)
	{
		fForm = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		Composite body = fForm.getBody();
		fForm.setText(Messages.getString("editors.pages.CodeEditorPage.codes")); //$NON-NLS-1$
		
		body.setLayout(new GridLayout(1, true));
		
		Button button = toolkit.createButton(body, Messages.getString(
				"editors.pages.CodeEditorPage.swap"), SWT.PUSH); //$NON-NLS-1$
		
		Composite mainArea = toolkit.createComposite(body, SWT.NULL);
		mainArea.setLayout(new GridLayout(2, true));
		mainArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Composite leftArea = toolkit.createComposite(mainArea, SWT.NULL);
		leftArea.setLayout(new GridLayout(1, true));
		leftArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		buildTableViewer(toolkit, leftArea);
		
		Composite rightArea = toolkit.createComposite(mainArea, SWT.NULL);
		rightArea.setLayout(new GridLayout(1, true));
		rightArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		createTreeViewer(toolkit, rightArea);
		
		fCodeName = toolkit.createLabel(body, Messages.getString(
				"editors.pages.CodeEditorPage.selectedCode")); //$NON-NLS-1$
		fCodeName.setFont(new Font(Display.getCurrent(), new FontData("", FONT_SIZE, SWT.BOLD))); //$NON-NLS-1$
		fCodeName.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		
		fDescription = new StyledText(body, SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
		fDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, DESCRIPTION_HEIGHT));
		fDescription.addKeyListener(createKeyAdapter());
		
		toolkit.paintBordersFor(body);
		
		fTableViewer.addSelectionChangedListener(createTableSelectionListener());
		fFilterButton.addSelectionListener(setFilter(fFilterButton));
		createTableContextMenu();
		button.addSelectionListener(createToggleAdapter());
		
		updateSelection();
	}
	
	/**
	 * @return
	 */
	private SelectionListener createToggleAdapter()
	{
		return new SelectionAdapter(){
			
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				Composite lParent = fTreeArea.getParent();
				Composite rParent = fTableArea.getParent();
				
				fTreeArea.setParent(rParent);
				fTableArea.setParent(lParent);
			}
		};
	}

	/**
	 * @param toolkit 
	 * @param composite
	 * @return
	 */
	private void createTreeViewer(FormToolkit toolkit, Composite parent)
	{
		fTreeArea = toolkit.createComposite(parent);
		fTreeArea.setLayout(new GridLayout(2, false));
		fTreeArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label label = toolkit.createLabel(fTreeArea, 
				Messages.getString("editors.pages.CodeEditorPage.hierarchy")); //$NON-NLS-1$
		label.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1));
		Button button = toolkit.createButton(fTreeArea, "", SWT.PUSH); //$NON-NLS-1$
		button.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, false, 1, 1));
		button.setVisible(false);
		button.setEnabled(false);
		
		fTreeViewer = new TreeViewer(fTreeArea, SWT.SINGLE | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.BORDER);
		
		Tree tree = fTreeViewer.getTree();
		TreeColumn col = new TreeColumn(tree, SWT.NONE);
		col.setText(Messages.getString("editors.pages.CodeEditorPage.code")); //$NON-NLS-1$
		col.setWidth(TREE_NAME_WIDTH);
		
		col = new TreeColumn(tree, SWT.NONE);
		col.setText(Messages.getString("editors.pages.CodeEditorPage.count")); //$NON-NLS-1$
		col.setWidth(TREE_FREQ_WIDTH);
		
		col = new TreeColumn(tree, SWT.NONE);
		col.setText(Messages.getString("editors.pages.CodeEditorPage.totalCount")); //$NON-NLS-1$
		col.setWidth(TREE_FREQ_WIDTH);
		
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		fTreeViewer.setContentProvider(new CodeTreeContentProvider());
		fTreeViewer.setLabelProvider(new CodeTreeLabelProvider());
		fTreeModel = TreeModel.getTreeModel(fProject);
		fTreeViewer.setInput(fTreeModel.getRoot());
		fTreeModel.addListener(fTreeViewer);
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transferTypes = new Transfer[]{TextTransfer.getInstance()};
		fTreeViewer.addDropSupport(operations, transferTypes, new TreeDropListener(fTreeViewer, this));
		fTreeViewer.addDragSupport(operations, transferTypes, new TreeDragListener(fTreeViewer));
		
		fTreeViewer.addSelectionChangedListener(createTreeSelectionListener());
		fTreeViewer.setSorter(new ViewerSorter());
		fTreeViewer.addDoubleClickListener(createDoubleClickListenerTree());
		
		createTreeContextMenu();		
	}

	/**
	 * @return
	 */
	private ISelectionChangedListener createTreeSelectionListener()
	{
		return new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				Node node = (Node) ((IStructuredSelection) fTreeViewer.getSelection()).getFirstElement();
				if(node != null)
				{
					setTreeItemsEnabled(true);
					int i = 0;
					CodeTableRow row = (CodeTableRow) fTableViewer.getElementAt(i);
					while(row != null)
					{
						if(node.getPersistenceId().equals(row.getPersistenceId()))
						{
							fTableViewer.setSelection(new StructuredSelection(row));
							break;
						}
						
						i++;
						row = (CodeTableRow) fTableViewer.getElementAt(i);
					}
				}
				else
				{
					Node[] elements = fTreeModel.getRoot().getChildren().values().toArray(new Node[0]);
					if(elements.length > 0)
					{
						fTreeViewer.setSelection(new StructuredSelection(elements[0]));
						setTreeItemsEnabled(true);
					}
					else
					{
						setTreeItemsEnabled(false);
					}
				}
			}
		};
	}

	/**
	 * @param b
	 */
	protected void setTreeItemsEnabled(boolean b)
	{
		for(MenuItem item : fTreeCanDisable)
		{
			item.setEnabled(b);
		}
	}
	
	/**
	 * 
	 * @param b
	 */
	protected void setTableItemsEnabled(boolean b)
	{
		for(MenuItem item : fTableCanDisable)
		{
			item.setEnabled(b);
		}
	}

	/**
	 * 
	 */
	private void createTreeContextMenu()
	{
		Menu menu = new Menu(fTreeViewer.getTree());
		
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText(Messages.getString("editors.pages.CodeEditorPage.newRootCode")); //$NON-NLS-1$
		item.addSelectionListener(newRootCodeSelected());
		
		item = new MenuItem(menu, SWT.PUSH);
		item.setText(Messages.getString("editors.pages.CodeEditorPage.newSubCode")); //$NON-NLS-1$
		item.addSelectionListener(createSubCodeSelected());
		fTreeCanDisable.add(item);
		
		item = new MenuItem(menu, SWT.PUSH);
		item.setText(Messages.getString("editors.pages.CodeEditorPage.renameCode")); //$NON-NLS-1$
		item.addSelectionListener(renameCodeSelectedTree());
		fTreeCanDisable.add(item);
		
		item = new MenuItem(menu, SWT.PUSH);
		item.setText(Messages.getString("editors.pages.CodeEditorPage.removeCode")); //$NON-NLS-1$
		item.addSelectionListener(removeCodeSelected());
		fTreeCanDisable.add(item);
		
		item = new MenuItem(menu, SWT.PUSH);
		item.setText(Messages.getString("editors.pages.CodeEditorPage.viewFragments")); //$NON-NLS-1$
		item.addSelectionListener(viewFragmentsSelectedTree());
		fTreeCanDisable.add(item);
		
		fTreeViewer.getTree().setMenu(menu);
		
		setTreeItemsEnabled(false);
	}

	/**
	 * @return
	 */
	private SelectionListener viewFragmentsSelectedTree()
	{
		return new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				Code toView = null;
				Node node = (Node) ((IStructuredSelection) fTreeViewer.getSelection()).getFirstElement();
				for(Code aCode : fProject.getCodes())
				{
					if(aCode.getCodeName().equals(node.getCodeName()))
					{
						toView = aCode;
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
	private SelectionListener renameCodeSelectedTree()
	{
		return new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				Code code = null;
				Node node = (Node) ((IStructuredSelection) fTreeViewer.getSelection()).getFirstElement();
				for(Code aCode : fProject.getCodes())
				{
					if(aCode.getCodeName().equals(node.getCodeName()))
					{
						code = aCode;
						break;
					}
				}
				
				RenameCodeDialog dialog = new RenameCodeDialog(getSite().getShell(), code);
				dialog.open();
				if(dialog.getReturnCode() == Window.OK)
				{
					String name = dialog.getName();
					code.setCodeName(name);
					Facade.getInstance().saveCodes(new Code[]{code});
				}
			}
		};
	}

	/**
	 * @return
	 */
	private SelectionAdapter removeCodeSelected()
	{
		return new SelectionAdapter(){
			
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				IStructuredSelection selection = (IStructuredSelection) fTreeViewer.getSelection();
				Node node = (Node) selection.getFirstElement();
				
				boolean check = true;
				if(!node.getChildren().isEmpty())
				{
					check = MessageDialog.openConfirm(getSite().getShell(), 
							Messages.getString("editors.pages.CodeEditorPage.removeCode"), //$NON-NLS-1$
							Messages.getString("editors.pages.CodeEditorPage.removeConfirm")); //$NON-NLS-1$ 
				}
				
				if(check)
				{
					fTreeModel.removeNode(node);
					fTreeModel.getRoot().computeFreq();
					fTreeViewer.refresh();
					if(fFilterButton.getSelection())
					{
						fTableViewer.refresh();
					}
					setDirty();
				}
			}
		};
	}

	/**
	 * @return
	 */
	private SelectionListener newRootCodeSelected()
	{
		return new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				Node node = fTreeModel.getRoot();
				
				if(node == null)
				{
					return;
				}
				
				NewCodeDialog dialog = new NewCodeDialog(getEditor().getSite().getShell(), fProject);
				dialog.create();
				if(dialog.open() == Window.OK)
				{
					Code code = Facade.getInstance().createCode(dialog.getName(), dialog.getDescription(), fProject);
					CommonNavigator view = (CommonNavigator) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
					view.getCommonViewer().refresh();
					
					new Node(node, code.getCodeName(), code.getPersistenceId(), 0);
					fTreeViewer.refresh();
					fTreeViewer.expandToLevel(node, 1);
					if(fFilterButton.getSelection())
					{
						fTableViewer.refresh();
					}
					setDirty();
				}
			}
		};
	}

	/**
	 * @return
	 */
	private SelectionListener createSubCodeSelected()
	{
		return new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				IStructuredSelection selection = (IStructuredSelection) fTreeViewer.getSelection();
				Node node = (Node) selection.getFirstElement();
				
				if(node == null)
				{
					return;
				}
				
				NewCodeDialog dialog = new NewCodeDialog(getEditor().getSite().getShell(), fProject);
				dialog.create();
				if(dialog.open() == Window.OK)
				{
					Code code = Facade.getInstance().createCode(dialog.getName(), dialog.getDescription(), fProject);
					CommonNavigator view = (CommonNavigator) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
					view.getCommonViewer().refresh();
					
					new Node(node, code.getCodeName(), code.getPersistenceId(), 0);
					fTreeViewer.refresh();
					fTreeViewer.expandToLevel(node, 1);
					if(fFilterButton.getSelection())
					{
						fTableViewer.refresh();
					}
					setDirty();
				}
			}
		};
	}

	/**
	 * @param body 
	 * 
	 */
	private void buildTableViewer(FormToolkit toolkit, Composite body)
	{
		fTableArea = toolkit.createComposite(body, SWT.NULL);
		fTableArea.setLayout(new GridLayout(2, false));
		fTableArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label label = toolkit.createLabel(fTableArea, Messages.getString(
				"editors.pages.CodeEditorPage.list")); //$NON-NLS-1$
		label.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1));
		fFilterButton = toolkit.createButton(fTableArea, 
				Messages.getString("editors.pages.CodeEditorPage.filter"), SWT.TOGGLE); //$NON-NLS-1$
		fFilterButton.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, false, 1, 1));
		
		fTableViewer = new TableViewer(fTableArea, SWT.SINGLE |  SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL);
		
		TableColumn col = new TableColumn(fTableViewer.getTable(), SWT.NONE);
		col.setText(Messages.getString("editors.pages.CodeEditorPage.codeName")); //$NON-NLS-1$
		col.setWidth(NAME_WIDTH);
		col.addSelectionListener(createColSortListener(0, col));
		col.setMoveable(false);
		
		col = new TableColumn(fTableViewer.getTable(), SWT.NONE);
		col.setText(Messages.getString("editors.pages.CodeEditorPage.count")); //$NON-NLS-1$
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
		fTableViewer.addDoubleClickListener(createDoubleClickListenerTable());
		
		fTableViewer.getTable().setSortColumn(fTableViewer.getTable().getColumn(0));
		fTableViewer.getTable().setSortDirection(SWT.DOWN);
		fTableViewer.getTable().setLayoutData(LARGE_LAYOUT);
	}

	/**
	 * @return
	 */
	private IDoubleClickListener createDoubleClickListenerTable()
	{
		return new IDoubleClickListener()
		{
			@Override
			public void doubleClick(DoubleClickEvent event)
			{
				IStructuredSelection selection = (IStructuredSelection) fTableViewer.getSelection();
				CodeTableRow row = (CodeTableRow) selection.getFirstElement();
				Code code = row.getCode();
				
				if(code != null)
				{
					ResourcesUtil.openEditor(getSite().getPage(), code);
				}
			}
		};
	}

	/**
	 * @return
	 */
	private SelectionListener setFilter(final Button button)
	{
		return new SelectionAdapter()
		{
			private TableFilter fFilter = new TableFilter(fTreeModel);
			
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if(button.getSelection())
				{
					if(fCurrentRow != null)
					{
						fCurrentRow.setDescription(fDescription.getText());
					}
					
					if(!fFilter.select(fTableViewer, null, fCurrentRow))
					{
						fCurrentRow = null;
					}
					
					fTableViewer.addFilter(fFilter);
					fTableViewer.refresh();
					updateSelection();
				}
				else
				{
					if(fCurrentRow != null)
					{
						fCurrentRow.setDescription(fDescription.getText());
					}
					
					fTableViewer.removeFilter(fFilter);
					fTableViewer.refresh();
					updateSelection();
				}
			}
		};
	}

	/**
	 * @return
	 */
	private IDoubleClickListener createDoubleClickListenerTree()
	{
		return new IDoubleClickListener()
		{
			@Override
			public void doubleClick(DoubleClickEvent event)
			{
				Code toView = null;
				if(fFilterButton.getSelection())
				{
					Node node = (Node) ((IStructuredSelection) fTreeViewer.getSelection()).getFirstElement();
					for(Code aCode : fProject.getCodes())
					{
						if(aCode.getCodeName().equals(node.getCodeName()))
						{
							toView = aCode;
							break;
						}
					}
				}
				else
				{
					IStructuredSelection selection = (IStructuredSelection) fTableViewer.getSelection();
					toView = ((CodeTableRow) selection.getFirstElement()).getCode();
				}
				
				if(toView != null)
				{
					ResourcesUtil.openEditor(getSite().getPage(), toView);
				}
			}
		};
	}

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
					dir = SWT.DOWN;
				}
				
				fTableViewer.getTable().setSortColumn(column);
				fTableViewer.getTable().setSortDirection(dir);
				
				fTableViewer.refresh();
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
		item.setText(Messages.getString("editors.pages.CodeEditorPage.renameCode")); //$NON-NLS-1$
		item.addSelectionListener(renameCodeSelected());
		fTableCanDisable.add(item);
		
		item = new MenuItem(menu, SWT.PUSH);
		item.setText(DELETE_CODE);
		item.addSelectionListener(deleteCodeSelected());
		fTableCanDisable.add(item);
		
		item = new MenuItem(menu, SWT.PUSH);
		item.setText(Messages.getString("editors.pages.CodeEditorPage.viewFragments")); //$NON-NLS-1$
		item.addSelectionListener(viewFragmentsSelected());
		fTableCanDisable.add(item);
		
		fTableViewer.getTable().setMenu(menu);
		
		setTableItemsEnabled(false);
	}

	/**
	 * @return
	 */
	private SelectionListener renameCodeSelected()
	{
		return new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				IStructuredSelection selection = (IStructuredSelection) fTableViewer.getSelection();
				Code code = ((CodeTableRow) selection.getFirstElement()).getCode();
				
				RenameCodeDialog dialog = new RenameCodeDialog(getSite().getShell(), code);
				dialog.open();
				if(dialog.getReturnCode() == Window.OK)
				{
					String name = dialog.getName();
					code.setCodeName(name);
					Facade.getInstance().saveCodes(new Code[]{code});
				}
				
			}
		};
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
				if(!fIsDirty && (!fDescription.getText().equals(fCurrentRow.getDescription())))
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
						fCurrentRow.setDescription(fDescription.getText().trim());
						fTableViewer.refresh(fCurrentRow);
					}
					
					fCurrentRow = row;
					fDescription.setText(fCurrentRow.getDescription());
					fCodeName.setText(Messages.getString(
							"editors.pages.CodeEditorPage.selectedCode") + fCurrentRow.getName()); //$NON-NLS-1$
				}
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
		List<CodeTableRow> list = null;
		if(fIsDirty)
		{
			list = getDirtyRows();
		}
		
		fProject = PersistenceManager.getInstance().getProject(fProject.getName());

		fTableViewer.setInput(new CodeTableInput(fProject));
		
		if(fIsDirty)
		{
			updateDescriptions(list);
		}
		
		updateSelection();
				
		fTableArea.layout();
		fTableArea.redraw();
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
			setTableItemsEnabled(true);
		}
		else
		{
			setTableItemsEnabled(false);
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
		if(fTreeModel != null)
		{
			fTreeModel.removeListener(fTreeViewer);
		}
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
			List<CodeTableRow> list = null;
			if(fIsDirty)
			{
				list = getDirtyRows();
			}
			
			fProject = PersistenceManager.getInstance().getProject(fProject.getName());
			
			CodeTableInput input = new CodeTableInput(fProject);
			fTableViewer.setInput(input);
			
			if(fIsDirty)
			{
				updateDescriptions(list);
			}
			
			fTreeModel.updateFrequencies(input);
			fTreeViewer.refresh();
			
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
			List<CodeTableRow> list = null;
			if(fIsDirty)
			{
				list = getDirtyRows();
			}
			
			fProject = PersistenceManager.getInstance().getProject(fProject.getName());
			
			CodeTableInput input = new CodeTableInput(fProject);
			fTableViewer.setInput(input);
			
			if(fIsDirty)
			{
				updateDescriptions(list);
			}
			
			fTreeModel.updateFrequencies(input);
			fTreeViewer.refresh();

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
			fCurrentRow.setDescription(fDescription.getText().trim());
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
	
	private List<CodeTableRow> getDirtyRows()
	{
		if(fCurrentRow != null)
		{
			fCurrentRow.setDescription(fDescription.getText().trim());
		}
		
		ArrayList<CodeTableRow> list = new ArrayList<CodeTableRow>();
		
		int i = 0;
		CodeTableRow row = (CodeTableRow) fTableViewer.getElementAt(i);
		while(row != null)
		{
			if(row.isDirty())
			{
				list.add(row);
			}
			i++;
			row = (CodeTableRow) fTableViewer.getElementAt(i);
		}
		
		return list;
	}
	
	private void updateDescriptions(List<CodeTableRow> list)
	{
		for(CodeTableRow row : list)
		{
			int i = 0;
			CodeTableRow tableRow = (CodeTableRow) fTableViewer.getElementAt(i);
			while(tableRow != null)
			{
				if(tableRow.getPersistenceId().equals(row.getPersistenceId()))
				{
					tableRow.setDescription(row.getDescription());
					break;
				}
				i++;
				tableRow = (CodeTableRow) fTableViewer.getElementAt(i);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormPage#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor)
	{
		fTreeModel.save();
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

	/**
	 * @return
	 */
	public TreeModel getTreeModel()
	{
		return fTreeModel;
	}
	
	/**
	 * Refreshes the table and tree viewers.
	 */
	public void refresh()
	{
		fTableViewer.refresh();
		fTreeViewer.refresh();
	}
}
