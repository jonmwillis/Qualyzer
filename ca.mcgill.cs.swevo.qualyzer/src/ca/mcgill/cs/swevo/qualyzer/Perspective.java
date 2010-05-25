/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     McGill University - initial API and implementation
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * 
 * Default perspective of Qualyzer.
 * 
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 *
 */
public class Perspective implements IPerspectiveFactory
{

	public static final String PROJECT_EXPLORER_VIEW_ID = "ca.mcgill.cs.swevo.qualyzer.projectexplorer";

	// CSOFF:
	public void createInitialLayout(IPageLayout layout)
	{
		/* I added all of this to a perspectiveExtension --Jonathan Faubert*/
		
//		String editorArea = layout.getEditorArea();
//
//		// Top left.
//		IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, (float) 0.26, editorArea);//$NON-NLS-1$
//		topLeft.addView(PROJECT_EXPLORER_VIEW_ID);
//		topLeft.addPlaceholder(IPageLayout.ID_BOOKMARKS);

	}
	// CSON:

}
