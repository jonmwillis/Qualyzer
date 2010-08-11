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

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.texteditor.ResourceMarkerAnnotationModel;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.RTFEditorInput;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;

/**
 *
 */
public class RTFAnnotationModel extends ResourceMarkerAnnotationModel
{
	
	/**
	 * Constructor.
	 * @param element
	 */
	public RTFAnnotationModel(RTFEditorInput element)
	{
		super(element.getFile());
	}
	
	/**
	 * If a fragment is being removed and has length zero then also removes the fragment from the document.
	 * @see org.eclipse.jface.text.source.AnnotationModel#removeAnnotation(org.eclipse.jface.text.source.Annotation)
	 */
	@Override
	public void removeAnnotation(Annotation annotation)
	{
		Position position = getPosition(annotation);
		
		if(position.length == 0 && annotation instanceof FragmentAnnotation)
		{
			Fragment fragment = ((FragmentAnnotation) annotation).getFragment();
			Facade.getInstance().deleteFragment(fragment);
		}
		CommonNavigator view = (CommonNavigator) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
		.getActivePage().findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
		
		super.removeAnnotation(annotation);
		view.getCommonViewer().refresh();
	}
	
	
}
