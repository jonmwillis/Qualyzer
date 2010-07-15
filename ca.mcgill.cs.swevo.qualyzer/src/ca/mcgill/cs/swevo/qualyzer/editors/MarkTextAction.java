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
 *
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run()
	{
<<<<<<< local
		IAnnotatedDocument document = fEditor.getDocument();
		Point selection = fSourceViewer.getSelectedRange();
		Position position = new Position(selection.x, selection.y);
		
		Fragment fragment = null;
		for(Fragment existingFragment : document.getFragments())
		{
			if(existingFragment.getOffset() == position.offset && 
					existingFragment.getLength() == position.length)
			{
				fragment = existingFragment;
				break;
			}
		}
		
		CodeChooserDialog dialog = new CodeChooserDialog(fEditor.getSite().getShell(), document.getProject(),
				fragment);
=======
		Transcript transcript = fEditor.getTranscript();
		CodeChooserDialog dialog = new CodeChooserDialog(fEditor.getSite().getShell(), transcript.getProject());
		dialog.setBlockOnOpen(fWindowsBlock);

>>>>>>> other
		dialog.create();
<<<<<<< local
		if(dialog.open() == Window.OK)
		{	
=======
		dialog.open();
		fTester.execute(dialog);
		if (dialog.getReturnCode() == Window.OK)
		{
			Point selection = fSourceViewer.getSelectedRange();
			Position position = new Position(selection.x, selection.y);

			Fragment fragment = null;
			for (Fragment existingFragment : transcript.getFragments())
			{
				if (existingFragment.getOffset() == position.offset && existingFragment.getLength() == position.length)
				{
					fragment = existingFragment;
					break;
				}
			}

>>>>>>> other
			CodeEntry entry = new CodeEntry();
			entry.setCode(dialog.getCode());

			if (fragment == null)
			{
<<<<<<< local
				fragment = Facade.getInstance().createFragment(document, position.offset,
						position.length);
=======
				fragment = Facade.getInstance().createFragment(transcript, position.offset, position.length);
>>>>>>> other
			}

			fragment.getCodeEntries().add(entry);
			fSourceViewer.markFragment(fragment);
<<<<<<< local
			
			Facade.getInstance().saveDocument(document);
			
=======

			Facade.getInstance().saveTranscript(transcript);

>>>>>>> other
			fEditor.setDirty();
		}

	}
}
