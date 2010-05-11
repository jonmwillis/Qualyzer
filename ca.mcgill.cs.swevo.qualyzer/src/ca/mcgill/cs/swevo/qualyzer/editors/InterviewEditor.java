package ca.mcgill.cs.swevo.qualyzer.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.IDEActionFactory;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

public class InterviewEditor extends TextEditor {

	private ColorManager colorManager;

	public InterviewEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new XMLConfiguration(colorManager));
		setDocumentProvider(new XMLDocumentProvider());
	}
	
	
	/** 
	 * <p>Does something.</p>
	 *
	 * @param parent
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		// Necessary to avoid all the contributions to the default text editor.
		// It's probably better to manually add the actions.
		// Just look at popup menu contribution in org.eclipse.ui.editors for examples.
		setRulerContextMenuId("#InterviewRulerContext");
		setOverviewRulerContextMenuId("#InterviewOverviewRulerContext");
		
		super.createPartControl(parent);
	}


	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}
	/** 
	 *
	 * @see org.eclipse.ui.editors.text.TextEditor#createActions()
	 */
	@Override
	protected void createActions()
	{
		super.createActions();
		
		// Remove unnecessary actions.
		setAction(IDEActionFactory.BOOKMARK.getId(), null);
		setAction(IDEActionFactory.ADD_TASK.getId(), null);
//		setAction(ITextEditorActionConstants.QUICKDIFF_TOGGLE, null);
//		System.out.println("BOOKMARK: " + getAction(ITextEditorActionConstants.RULER_MANAGE_BOOKMARKS));
		
	}

	
	
}
