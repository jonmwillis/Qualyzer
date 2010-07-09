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
package ca.mcgill.cs.swevo.qualyzer.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import net.sf.colorer.eclipse.ColorerPlugin;
import net.sf.colorer.eclipse.editors.ColorerEditor;
import net.sf.colorer.eclipse.jface.TextColorer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;

import ca.mcgill.cs.swevo.qualyzer.dialogs.CodeChooserDialog;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.RTFEditorInput;
import ca.mcgill.cs.swevo.qualyzer.model.CodeEntry;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.ProjectListener;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.model.TranscriptListener;
import ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;

/**
 * A Rich Text Editor for Transcripts.
 *
 */
public class RTFEditor extends ColorerEditor implements TranscriptListener, ProjectListener
{
	public static final String ID = "ca.mcgill.cs.swevo.qualyzer.editors.RTFEditor"; //$NON-NLS-1$

	private static final char UNDERLINE_CHAR = (char) 21;
	private static final char ITALIC_CHAR = (char) 9;
	private static final char BOLD_CHAR = (char) 2;
	private static final char FRAGMENT_CHAR = (char) 11;
	
	private Action fBoldAction;
	private Action fItalicAction;
	private Action fUnderlineAction;
	private Action fMarkTextAction;
	
	private boolean fIsDirty;
	private Transcript fTranscript;
	
	/**
	 * Constructor.
	 * Initialises the actions.
	 */
	public RTFEditor()
	{
		super();
		setSourceViewerConfiguration(new RTFSourceViewerConfiguration(getTextColorer()));
		setDocumentProvider(new RTFDocumentProvider());
		
		fIsDirty = false;
		
		initialiseBoldAction();
		
		initialiseItalicAction();
		
		initialiseUnderlineAction();
		
		initialiseMarkAction();
		
		getPreferenceStore().setValue(AbstractDecoratedTextEditorPreferenceConstants.QUICK_DIFF_ALWAYS_ON, false);
	}
	
	

	/**
	 * 
	 */
	private void initialiseMarkAction()
	{
		fMarkTextAction = new Action()
		{
			/* (non-Javadoc)
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run()
			{
				CodeChooserDialog dialog = new CodeChooserDialog(getSite().getShell(), fTranscript.getProject());
				dialog.create();
				if(dialog.open() == Window.OK)
				{
					RTFSourceViewer viewer = (RTFSourceViewer) getSourceViewer();
					Point selection = viewer.getSelectedRange();
					Position position = new Position(selection.x, selection.y);
					
					Fragment fragment = null;
					for(Fragment existingFragment : fTranscript.getFragments())
					{
						if(existingFragment.getOffset() == position.offset && 
								existingFragment.getLength() == position.length)
						{
							fragment = existingFragment;
							break;
						}
					}
					
					CodeEntry entry = new CodeEntry();
					entry.setCode(dialog.getCode());
					
					if(fragment == null)
					{
						fragment = Facade.getInstance().createFragment(fTranscript, position.offset,
								position.length);
					}
					
					fragment.getCodeEntries().add(entry);
					viewer.markFragment(fragment);
					
					setDirty();
				}
		
				
			}
		};
		fMarkTextAction.setText(Messages.getString("editors.RTFEditor.mark")); //$NON-NLS-1$
		fMarkTextAction.setEnabled(false);
	}

	/**
	 * 
	 */
	private void initialiseUnderlineAction()
	{
		fUnderlineAction = new Action(Messages.getString("editors.RTFEditor.underline"),  //$NON-NLS-1$
				Action.AS_CHECK_BOX){
			/* (non-Javadoc)
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run()
			{
				RTFSourceViewer viewer = (RTFSourceViewer) getSourceViewer();
				Point selection = viewer.getSelectedRange();
				Position position = new Position(selection.x, selection.y);
				
				viewer.toggleUnderline(position);
				setDirty();
				//fUnderlineAction.setChecked(!fUnderlineAction.isChecked());
			}
		};
		fUnderlineAction.setEnabled(false);
	}

	/**
	 * 
	 */
	private void initialiseItalicAction()
	{
		fItalicAction = new Action(Messages.getString("editors.RTFEditor.italic"), Action.AS_CHECK_BOX){ //$NON-NLS-1$
			/* (non-Javadoc)
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run()
			{
				RTFSourceViewer viewer = (RTFSourceViewer) getSourceViewer();
				Point selection = viewer.getSelectedRange();
				Position position = new Position(selection.x, selection.y);

				viewer.toggleItalic(position);
				setDirty();
			}
		};
		fItalicAction.setEnabled(false);
	}

	/**
	 * 
	 */
	private void initialiseBoldAction()
	{
		fBoldAction = new Action(Messages.getString("editors.RTFEditor.bold"), Action.AS_CHECK_BOX){ //$NON-NLS-1$
			
			@Override
			public void run() 
			{
				RTFSourceViewer viewer = (RTFSourceViewer) getSourceViewer();
				Point selection = viewer.getSelectedRange();
				Position position = new Position(selection.x, selection.y);
				
				viewer.toggleBold(position);
				setDirty();
			};
		};
		fBoldAction.setEnabled(false);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#createSourceViewer(org.eclipse.swt.widgets.Composite, 
	 * org.eclipse.jface.text.source.IVerticalRuler, int)
	 */
	@Override
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles)
	{
		fAnnotationAccess = getAnnotationAccess();
		fOverviewRuler = createOverviewRuler(getSharedColors());
		
		final SourceViewer viewer = new RTFSourceViewer(parent, ruler, fOverviewRuler,
				isOverviewRulerVisible(), styles);
		
		getSourceViewerDecorationSupport(viewer);
		
		viewer.showAnnotations(true);
		viewer.addSelectionChangedListener(createSelectionListener(viewer));
		
		return viewer;
	}

	/**
	 * @param viewer
	 * @return
	 */
	private ISelectionChangedListener createSelectionListener(final SourceViewer viewer)
	{
		return new ISelectionChangedListener()
		{
			
			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				Point selection = viewer.getSelectedRange();
				IAnnotationModel model = getDocumentProvider().getAnnotationModel(getEditorInput());
				
				boolean enabled = selection.y != 0;
				
				boolean boldEnabled = isBoldEnabled(model, selection);
				boolean italicEnabled = isItalicEnabled(model, selection);
				boolean underlineEnabled = isUnderlineEnabled(model, selection);
				boolean markTextEnabled = isMarkEnabled(model, selection);
				
				fBoldAction.setEnabled(enabled && boldEnabled);
				fItalicAction.setEnabled(enabled && italicEnabled);
				fUnderlineAction.setEnabled(enabled && underlineEnabled);
				fMarkTextAction.setEnabled(enabled && markTextEnabled);
			}
		};
	}
	
	@SuppressWarnings("unchecked")
	private boolean isMarkEnabled(IAnnotationModel model, Point selection)
	{
		ArrayList<Position> positions = new ArrayList<Position>();
		Iterator<Annotation> iter = model.getAnnotationIterator();
		
		while(iter.hasNext())
		{
			Annotation annotation = iter.next();
			if(annotation instanceof FragmentAnnotation)
			{
				Position position = model.getPosition(annotation);
				if(position.overlapsWith(selection.x, selection.y))
				{
					positions.add(position);
				}
			}
		}
		
		if(positions.size() == 0)
		{
			return true;
		}
		else if(positions.size() == 1)
		{
			Position position = positions.get(0);
			return position.offset == selection.x && position.length == selection.y;
		}
		else
		{
			return false;
		}
			
	}
	
	/**
	 * @param model
	 * @param selection
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected boolean isUnderlineEnabled(IAnnotationModel model, Point selection)
	{
		ArrayList<Position> positions = new ArrayList<Position>();
		Iterator<Annotation> iter = model.getAnnotationIterator();
		
		while(iter.hasNext())
		{
			Annotation annotation = iter.next();
			if(isUnderline(annotation))
			{
				Position position = model.getPosition(annotation);
				if(position.overlapsWith(selection.x, selection.y))
				{
					positions.add(position);
				}
			}
		}
				
		return handleOverlaps(positions, selection);
	}

	/**
	 * @param model
	 * @param selection
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected boolean isItalicEnabled(IAnnotationModel model, Point selection)
	{
		ArrayList<Position> positions = new ArrayList<Position>();
		Iterator<Annotation> iter = model.getAnnotationIterator();
		
		while(iter.hasNext())
		{
			Annotation annotation = iter.next();
			if(isItalic(annotation))
			{
				Position position = model.getPosition(annotation);
				if(position.overlapsWith(selection.x, selection.y))
				{
					positions.add(position);
				}
			}
		}
						
		return handleOverlaps(positions, selection);
	}

	/**
	 * @param model
	 * @param selection
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected boolean isBoldEnabled(IAnnotationModel model, Point selection)
	{
		ArrayList<Position> positions = new ArrayList<Position>();
		Iterator<Annotation> iter = model.getAnnotationIterator();
		
		while(iter.hasNext())
		{
			Annotation annotation = iter.next();
			if(isBold(annotation))
			{
				Position position = model.getPosition(annotation);
				if(position.overlapsWith(selection.x, selection.y))
				{
					positions.add(position);
				}
			}
		}
					
		return handleOverlaps(positions, selection);
	}

	private boolean handleOverlaps(ArrayList<Position> positions, Point selection)
	{
		boolean toReturn = false;
		
		if(positions.size() == 0)
		{
			toReturn = true;
		}
		else if(positions.size() == 1)
		{
			Position position = positions.get(0);
			toReturn = position.offset <= selection.x && position.offset + position.length >= selection.x + selection.y;
		}
		else
		{
			sort(positions);
			Position startPos = positions.get(0);
			Position endPos = positions.get(positions.size() - 1);
			if(startPos.offset <= selection.x && endPos.offset + endPos.length >= selection.x + selection.y)
			{
				for(int i = 0; i < positions.size() - 1; i++)
				{
					Position p1 = positions.get(i);
					
					if(p1.offset + p1.length != positions.get(i+1).offset)
					{
						return false;
					}
				}
				toReturn = true;
			}
		}
		
		return toReturn;
	}

	/**
	 * @param overlap
	 * @param positions
	 */
	private void sort(ArrayList<Position> positions)
	{
		Collections.sort(positions, new Comparator<Position>()
		{

			@Override
			public int compare(Position o1, Position o2)
			{
				if(o1.offset < o2.offset)
				{
					return -1;
				}
				else if(o1.offset == o2.offset)
				{
					return 0;
				}
				else
				{
					return 1;
				}
			}
		});
		
	}

	/**
	 * @param annotation
	 * @return
	 */
	protected static boolean isBold(Annotation annotation)
	{
		String type = annotation.getType();
		return type.equals(RTFConstants.BOLD_TYPE) || type.equals(RTFConstants.BOLD_ITALIC_TYPE) || 
		type.equals(RTFConstants.BOLD_UNDERLINE_TYPE) || type.equals(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE);
	}
	
	/**
	 * @param annotation
	 * @return
	 */
	protected static boolean isItalic(Annotation annotation)
	{
		String type = annotation.getType();
		return type.equals(RTFConstants.ITALIC_TYPE) || type.equals(RTFConstants.BOLD_ITALIC_TYPE) || 
		type.equals(RTFConstants.ITALIC_UNDERLINE_TYPE) || type.equals(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE);
	}

	/**
	 * @param annotation
	 * @return
	 */
	protected static boolean isUnderline(Annotation annotation)
	{
		String type = annotation.getType();
		return type.equals(RTFConstants.UNDERLINE_TYPE) || type.equals(RTFConstants.BOLD_UNDERLINE_TYPE) || 
		type.equals(RTFConstants.ITALIC_UNDERLINE_TYPE) || type.equals(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#getSourceViewerDecorationSupport(
	 * org.eclipse.jface.text.source.ISourceViewer)
	 */
	@Override
	protected SourceViewerDecorationSupport getSourceViewerDecorationSupport(ISourceViewer viewer)
	{
		if(fSourceViewerDecorationSupport == null)
		{
			fSourceViewerDecorationSupport = new RTFDecorationSupport(viewer, fOverviewRuler, 
					fAnnotationAccess, getSharedColors());
			configureSourceViewerDecorationSupport(fSourceViewerDecorationSupport);
		}
		
		return fSourceViewerDecorationSupport;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.TextEditor#initializeEditor()
	 */
	@Override
	protected void initializeEditor()
	{
		super.initializeEditor();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#isLineNumberRulerVisible()
	 */
	@Override
	protected boolean isLineNumberRulerVisible()
	{
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.TextEditor#createActions()
	 */
	@Override
	protected void createActions()
	{
		super.createActions();
		setAction(RTFConstants.BOLD_ACTION_ID, fBoldAction);
		setAction(RTFConstants.UNDERLINE_ACTION_ID, fUnderlineAction);
		setAction(RTFConstants.ITALIC_ACTION_ID, fItalicAction);
		setAction(RTFConstants.FRAGMENT_ACTION_ID, fMarkTextAction);
		
		setActionActivationCode(RTFConstants.BOLD_ACTION_ID, BOLD_CHAR, 'b', SWT.CONTROL);
		setActionActivationCode(RTFConstants.ITALIC_ACTION_ID, ITALIC_CHAR, 'i', SWT.CONTROL);
		setActionActivationCode(RTFConstants.UNDERLINE_ACTION_ID, UNDERLINE_CHAR, 'u', SWT.CONTROL);
		setActionActivationCode(RTFConstants.FRAGMENT_ACTION_ID, FRAGMENT_CHAR, 'k', SWT.CONTROL);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#rulerContextMenuAboutToShow(
	 * org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	protected void rulerContextMenuAboutToShow(IMenuManager menu)
	{
		//super.rulerContextMenuAboutToShow(menu);
		//This removes the ruler context menu.
		//Do we want to add any of our own actions here?
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		setRulerContextMenuId("#RTFRulerContext"); //$NON-NLS-1$
		setOverviewRulerContextMenuId("#RTFOverviewRulerContext"); //$NON-NLS-1$
		setEditorContextMenuId("#RTFEditorContext"); //$NON-NLS-1$
		
		super.createPartControl(parent);
		
		fTranscript = ((RTFEditorInput) getEditorInput()).getTranscript();

		Facade.getInstance().getListenerManager().registerProjectListener(fTranscript.getProject(), this);
		Facade.getInstance().getListenerManager().registerTranscriptListener(fTranscript.getProject(), this);
		ColorerPlugin.getDefault().setPropertyWordWrap(getTextColorer().getFileType(), 1);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.colorer.eclipse.editors.ColorerEditor#getTextColorer()
	 */
	@Override
	public TextColorer getTextColorer()
	{
		TextColorer colorer = super.getTextColorer();
		return colorer;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#overviewRulerContextMenuAboutToShow(
	 * org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	protected void overviewRulerContextMenuAboutToShow(IMenuManager menu)
	{
		// super.overviewRulerContextMenuAboutToShow(menu);
		// This removes the overview ruler context menu.
		// Do we want to add any of our own actions?
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.TextEditor#editorContextMenuAboutToShow(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	protected void editorContextMenuAboutToShow(IMenuManager menu)
	{
		super.editorContextMenuAboutToShow(menu);
		
		menu.remove(ITextEditorActionConstants.GROUP_OPEN);
		menu.remove(ITextEditorActionConstants.GROUP_PRINT);
		menu.remove(ITextEditorActionConstants.GROUP_ADD);
		menu.remove(ITextEditorActionConstants.GROUP_REST);
		menu.remove(ITextEditorActionConstants.SHIFT_RIGHT);
		menu.remove(ITextEditorActionConstants.SHIFT_LEFT);
		menu.remove(ITextEditorActionConstants.GROUP_FIND);
		menu.remove(IWorkbenchActionConstants.MB_ADDITIONS);
		menu.remove(ITextEditorActionConstants.GROUP_SETTINGS);
		menu.remove(ITextEditorActionConstants.CONTEXT_PREFERENCES);
		
		for(IContributionItem item : menu.getItems())
		{
			if(item.getId() == null)
			{
				menu.remove(item);
			}
		}
		
		addAction(menu, ITextEditorActionConstants.GROUP_EDIT, RTFConstants.BOLD_ACTION_ID);
		addAction(menu, ITextEditorActionConstants.GROUP_EDIT, RTFConstants.ITALIC_ACTION_ID);
		addAction(menu, ITextEditorActionConstants.GROUP_EDIT, RTFConstants.UNDERLINE_ACTION_ID);
		addAction(menu, ITextEditorActionConstants.GROUP_EDIT, RTFConstants.FRAGMENT_ACTION_ID);
		
		//check according to selection
		fBoldAction.setChecked(isBoldChecked());
		fItalicAction.setChecked(isItalicChecked());
		fUnderlineAction.setChecked(isUnderlineChecked());
		
	}
	
	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean isUnderlineChecked()
	{
		IAnnotationModel model = getSourceViewer().getAnnotationModel();
		Point selection = getSourceViewer().getSelectedRange();
		
		ArrayList<Position> positions = new ArrayList<Position>();
		Iterator<Annotation> iter = model.getAnnotationIterator();
		
		while(iter.hasNext())
		{
			Annotation annotation = iter.next();
			if(isUnderline(annotation))
			{
				Position position = model.getPosition(annotation);
				if(position.overlapsWith(selection.x, selection.y))
				{
					positions.add(position);
				}
			}
		}
		
		return positions.size() > 0;
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean isItalicChecked()
	{
		IAnnotationModel model = getSourceViewer().getAnnotationModel();
		Point selection = getSourceViewer().getSelectedRange();
		
		ArrayList<Position> positions = new ArrayList<Position>();
		Iterator<Annotation> iter = model.getAnnotationIterator();
		
		while(iter.hasNext())
		{
			Annotation annotation = iter.next();
			if(isItalic(annotation))
			{
				Position position = model.getPosition(annotation);
				if(position.overlapsWith(selection.x, selection.y))
				{
					positions.add(position);
				}
			}
		}
		
		return positions.size() > 0;
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean isBoldChecked()
	{
		IAnnotationModel model = getSourceViewer().getAnnotationModel();
		Point selection = getSourceViewer().getSelectedRange();
		
		ArrayList<Position> positions = new ArrayList<Position>();
		Iterator<Annotation> iter = model.getAnnotationIterator();
		
		while(iter.hasNext())
		{
			Annotation annotation = iter.next();
			if(isBold(annotation))
			{
				Position position = model.getPosition(annotation);
				if(position.overlapsWith(selection.x, selection.y))
				{
					positions.add(position);
				}
			}
		}
		
		return positions.size() > 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor progressMonitor)
	{
		super.doSave(progressMonitor);
		fIsDirty = false;
	}

	/**
	 * 
	 */
	private void setDirty()
	{
		fIsDirty = true;
		firePropertyChange(PROP_DIRTY);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#isDirty()
	 */
	@Override
	public boolean isDirty()
	{
		return super.isDirty() || fIsDirty;
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.model.TranscriptListener#transcriptChanged(
	 * ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType, ca.mcgill.cs.swevo.qualyzer.model.Transcript[],
	 *  ca.mcgill.cs.swevo.qualyzer.model.Facade)
	 */
	@Override
	public void transcriptChanged(ChangeType cType, Transcript[] transcripts, Facade facade)
	{
		if(cType == ChangeType.DELETE)
		{
			for(Transcript transcript : transcripts)
			{
				if(transcript.equals(fTranscript))
				{
					IWorkbenchPage page = getSite().getPage();
					ResourcesUtil.closeEditor(page, getEditorInput().getName());
					break;
				}
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.model.ProjectListener#projectChanged(
	 * ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType, ca.mcgill.cs.swevo.qualyzer.model.Project, 
	 * ca.mcgill.cs.swevo.qualyzer.model.Facade)
	 */
	@Override
	public void projectChanged(ChangeType cType, Project project, Facade facade)
	{
		if(ChangeType.DELETE == cType)
		{
			IWorkbenchPage page = getSite().getPage();
			ResourcesUtil.closeEditor(page, getEditorInput().getName());
		}
		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.colorer.eclipse.editors.ColorerEditor#dispose()
	 */
	@Override
	public void dispose()
	{
		Facade.getInstance().getListenerManager().unregisterProjectListener(fTranscript.getProject(), this);
		Facade.getInstance().getListenerManager().unregisterTranscriptListener(fTranscript.getProject(), this);
		super.dispose();
	}
	
}
