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

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Point;

import ca.mcgill.cs.swevo.qualyzer.dialogs.CodeChooserDialog;
import ca.mcgill.cs.swevo.qualyzer.model.CodeEntry;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;
import ca.mcgill.cs.swevo.qualyzer.model.IAnnotatedDocument;

/**
 * Handles the marking of Text in the RTFEditor. Asks the user what code they want to add,
 * then it creates or updates the fragment and tells the sourceviewer about the change in annotation.
 */
public class MarkTextAction extends Action implements ITestableAction
{

	private RTFEditor fEditor;
	private RTFSourceViewer fSourceViewer;
	private boolean fWindowsBlock;
	private IDialogTester fTester = new NullTester();

	/**
	 * 
	 */
	public MarkTextAction(RTFEditor editor, RTFSourceViewer viewer)
	{
		super(Messages.getString("editors.MarkTextAction.addCode")); //$NON-NLS-1$
		fEditor = editor;
		fSourceViewer = viewer;
		fWindowsBlock = true;
		setEnabled(false);
	}

	/**
	 * Does something.
	 * 
	 * @return
	 * @see ca.mcgill.cs.swevo.qualyzer.editors.ITestableAction#isWindowsBlock()
	 */
	public boolean isWindowsBlock()
	{
		return fWindowsBlock;
	}

	/**
	 * Does something.
	 * 
	 * @param windowsBlock
	 * @see ca.mcgill.cs.swevo.qualyzer.editors.ITestableAction#setWindowsBlock(boolean)
	 */
	public void setWindowsBlock(boolean windowsBlock)
	{
		fWindowsBlock = windowsBlock;
	}

	/**
	 * Does something.
	 * 
	 * @return
	 * @see ca.mcgill.cs.swevo.qualyzer.editors.ITestableAction#getTester()
	 */
	public IDialogTester getTester()
	{
		return fTester;
	}

	// CSOFF:
	/**
	 * Does something.
	 * 
	 * @param tester
	 * @see ca.mcgill.cs.swevo.qualyzer.editors.ITestableAction#setTester(ca.mcgill.cs.swevo.qualyzer.editors.IDialogTester)
	 */
	public void setTester(IDialogTester tester)
	{
		this.fTester = tester;
	}

	// CSON:

	/**
	 * Opens the CodeChooser and upon selection of a code, attaches it to the selected fragment.
	 */
	@Override
	public void run()
	{
		if(fEditor.isDirty())
		{
			fEditor.doSave(new NullProgressMonitor());
		}
		
		IAnnotatedDocument document = fEditor.getDocument();
		Point selection = fSourceViewer.getSelectedRange();
		Position position = new Position(selection.x, selection.y);

		Fragment fragment = document.getFragments().get(position.offset);

		CodeChooserDialog dialog = new CodeChooserDialog(fEditor.getSite().getShell(), document.getProject(), fragment);
		dialog.setBlockOnOpen(fWindowsBlock);
		dialog.create();
		dialog.open();
		fTester.execute(dialog);
		if (dialog.getReturnCode() == Window.OK)
		{
			CodeEntry entry = new CodeEntry();
			entry.setCode(dialog.getCode());
			entry.setInvestigator(fEditor.getActiveInvestigator());

			if (fragment == null)
			{
				fragment = Facade.getInstance().createFragment(document, position.offset, position.length);
			}
			else
			{
				fragment = document.getFragments().get(position.offset);
			}

			fragment.getCodeEntries().add(entry);
			fSourceViewer.markFragment(fragment);

			Facade.getInstance().saveDocument(document);

			fEditor.setDirty();
		}

	}

}
