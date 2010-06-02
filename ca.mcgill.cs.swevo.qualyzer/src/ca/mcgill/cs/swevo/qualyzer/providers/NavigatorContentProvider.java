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
package ca.mcgill.cs.swevo.qualyzer.providers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.model.WorkbenchContentProvider;

import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 * The content provider for our navigator content.
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class NavigatorContentProvider extends WorkbenchContentProvider
{

	private static final Object[] NO_CHILDREN = new Object[0];
	private Viewer viewer;

	/**
	 *  
	 */
	public NavigatorContentProvider()
	{
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.model.BaseWorkbenchContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object element)
	{
		if(element instanceof IWorkspaceRoot)
		{
			IWorkspaceRoot root = (IWorkspaceRoot) element;
			return root.getProjects();
		}
		return NO_CHILDREN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.model.BaseWorkbenchContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object element)
	{
		if (element instanceof IProject)
		{
			Project proj = PersistenceManager.getInstance().getProject(((IProject) element).getName());
			
			WrapperTranscript transcript = new WrapperTranscript(proj);
			WrapperInvestigator investigator = new WrapperInvestigator(proj);
			WrapperCode code = new WrapperCode(proj);
			WrapperMemo memo = new WrapperMemo(proj);
			WrapperParticipant participant = new WrapperParticipant(proj);
			
			return new Object[]{participant, investigator, transcript, memo, code };
		}
		else if(element instanceof ProjectWrapper)
		{
			ProjectWrapper wrapper = (ProjectWrapper) element;
			Object[] toReturn = null;
			if(wrapper instanceof WrapperTranscript)
			{
				toReturn = wrapper.getProject().getTranscripts().toArray();
			}
			else if(wrapper instanceof WrapperInvestigator)
			{
				toReturn = wrapper.getProject().getInvestigators().toArray();
			}
			else if(wrapper instanceof WrapperParticipant)
			{
				toReturn = wrapper.getProject().getParticipants().toArray();
			}
			else if(wrapper instanceof WrapperMemo)
			{
				toReturn = wrapper.getProject().getMemos().toArray();
			}
			else if(wrapper instanceof WrapperCode)
			{
				toReturn = wrapper.getProject().getCodes().toArray();
			}
			if(toReturn != null)
			{
				return toReturn;
			}
		}
		return NO_CHILDREN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.model.BaseWorkbenchContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element)
	{
		if (element instanceof IProject || element instanceof Project)
		{
			return true;
		}
		else if(element instanceof ProjectWrapper)
		{
			return projectHasElements((ProjectWrapper) element);
		}

		return false;
	}

	/**
	 * Checks if the project in element has children of the type specified by
	 * the kind of Wrapper element is.
	 * @param element
	 * @return
	 */
	private boolean projectHasElements(ProjectWrapper element)
	{
		boolean hasChildren = false;
		if(element.getResource().equals("transcripts"))
		{
			hasChildren = !element.getProject().getTranscripts().isEmpty();
		}
		else if(element.getResource().equals("codes"))
		{
			hasChildren = !element.getProject().getCodes().isEmpty();
		}
		else if(element.getResource().equals("investigators"))
		{
			hasChildren = !element.getProject().getInvestigators().isEmpty();
		}
		else if(element.getResource().equals("participants"))
		{
			hasChildren = !element.getProject().getParticipants().isEmpty();
		}
		else if(element.getResource().equals("memos"))
		{
			hasChildren = !element.getProject().getMemos().isEmpty();
		}
			
		return hasChildren;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.model.WorkbenchContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
	 * java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		super.inputChanged(viewer, oldInput, newInput);
		this.viewer = viewer;
	}

	/**
	 * Process the resource delta.
	 * 
	 * @param delta
	 */
	protected void processDelta(IResourceDelta delta)
	{

		Control ctrl = viewer.getControl();
		if (ctrl == null || ctrl.isDisposed())
		{
			return;
		}

		final Collection runnables = new ArrayList();
		processDelta(delta, runnables);

		if (runnables.isEmpty())
		{
			return;
		}

		// Are we in the UIThread? If so spin it until we are done
		if (ctrl.getDisplay().getThread() == Thread.currentThread())
		{
			runUpdates(runnables);
		}
		else
		{
			ctrl.getDisplay().asyncExec(new Runnable()
			{
				/*
				 * (non-Javadoc)
				 * 
				 * @see java.lang.Runnable#run()
				 */
				public void run()
				{
					// Abort if this happens after disposes
					Control ctrl = viewer.getControl();
					if (ctrl == null || ctrl.isDisposed())
					{
						return;
					}

					runUpdates(runnables);
				}
			});
		}

	}

	/**
	 * Process a resource delta. Add any runnables
	 */
	private void processDelta(IResourceDelta delta, Collection runnables)
	{
		// he widget may have been destroyed
		// by the time this is run. Check for this and do nothing if so.
		Control ctrl = viewer.getControl();
		if (ctrl == null || ctrl.isDisposed())
		{
			return;
		}

		// Get the affected resource
		final IResource resource = delta.getResource();

		// If any children have changed type, just do a full refresh of this
		// parent,
		// since a simple update on such children won't work,
		// and trying to map the change to a remove and add is too dicey.
		// The case is: folder A renamed to existing file B, answering yes to
		// overwrite B.
		IResourceDelta[] affectedChildren = delta.getAffectedChildren(IResourceDelta.CHANGED);
		for (int i = 0; i < affectedChildren.length; i++)
		{
			if ((affectedChildren[i].getFlags() & IResourceDelta.TYPE) != 0)
			{
				runnables.add(getRefreshRunnable(resource));
				return;
			}
		}

		// Check the flags for changes the Navigator cares about.
		// See ResourceLabelProvider for the aspects it cares about.
		// Notice we don't care about F_CONTENT or F_MARKERS currently.
		int changeFlags = delta.getFlags();
		if ((changeFlags & (IResourceDelta.OPEN | IResourceDelta.SYNC | IResourceDelta.TYPE | IResourceDelta.DESCRIPTION)) != 0)
		{
			// Runnable updateRunnable = new Runnable(){
			// public void run() {
			// ((StructuredViewer) viewer).update(resource, null);
			// }
			// };
			// runnables.add(updateRunnable);

			/*
			 * support the Closed Projects filter; when a project is closed, it may need to be removed from the view.
			 */
			runnables.add(getRefreshRunnable(resource.getParent()));
		}
		// Replacing a resource may affect its label and its children
		if ((changeFlags & IResourceDelta.REPLACED) != 0)
		{
			runnables.add(getRefreshRunnable(resource));
			return;
		}

		// Handle changed children .
		for (int i = 0; i < affectedChildren.length; i++)
		{
			processDelta(affectedChildren[i], runnables);
		}

		// @issue several problems here:
		// - should process removals before additions, to avoid multiple equal
		// elements in viewer
		// - Kim: processing removals before additions was the indirect cause of
		// 44081 and its varients
		// - Nick: no delta should have an add and a remove on the same element,
		// so processing adds first is probably OK
		// - using setRedraw will cause extra flashiness
		// - setRedraw is used even for simple changes
		// - to avoid seeing a rename in two stages, should turn redraw on/off
		// around combined removal and addition
		// - Kim: done, and only in the case of a rename (both remove and add
		// changes in one delta).

		IResourceDelta[] addedChildren = delta.getAffectedChildren(IResourceDelta.ADDED);
		IResourceDelta[] removedChildren = delta.getAffectedChildren(IResourceDelta.REMOVED);

		if (addedChildren.length == 0 && removedChildren.length == 0)
		{
			return;
		}

		final Object[] addedObjects;
		final Object[] removedObjects;

		// Process additions before removals as to not cause selection
		// preservation prior to new objects being added
		// Handle added children. Issue one update for all insertions.
		int numMovedFrom = 0;
		int numMovedTo = 0;
		if (addedChildren.length > 0)
		{
			addedObjects = new Object[addedChildren.length];
			for (int i = 0; i < addedChildren.length; i++)
			{
				addedObjects[i] = addedChildren[i].getResource();
				if ((addedChildren[i].getFlags() & IResourceDelta.MOVED_FROM) != 0)
				{
					++numMovedFrom;
				}
			}
		}
		else
		{
			addedObjects = new Object[0];
		}

		// Handle removed children. Issue one update for all removals.
		if (removedChildren.length > 0)
		{
			removedObjects = new Object[removedChildren.length];
			for (int i = 0; i < removedChildren.length; i++)
			{
				removedObjects[i] = removedChildren[i].getResource();
				if ((removedChildren[i].getFlags() & IResourceDelta.MOVED_TO) != 0)
				{
					++numMovedTo;
				}
			}
		}
		else
		{
			removedObjects = new Object[0];
		}
		// heuristic test for items moving within same folder (i.e. renames)
		final boolean hasRename = numMovedFrom > 0 && numMovedTo > 0;

		Runnable addAndRemove = new Runnable()
		{
			public void run()
			{
				if (viewer instanceof AbstractTreeViewer)
				{
					AbstractTreeViewer treeViewer = (AbstractTreeViewer) viewer;
					// Disable redraw until the operation is finished so we don't
					// get a flash of both the new and old item (in the case of
					// rename)
					// Only do this if we're both adding and removing files (the
					// rename case)
					if (hasRename)
					{
						treeViewer.getControl().setRedraw(false);
					}
					try
					{
						if (addedObjects.length > 0)
						{
							treeViewer.add(resource, addedObjects);
						}
						if (removedObjects.length > 0)
						{
							treeViewer.remove(removedObjects);
						}
					}
					finally
					{
						if (hasRename)
						{
							treeViewer.getControl().setRedraw(true);
						}
					}
				}
				else
				{
					((StructuredViewer) viewer).refresh(resource);
				}
			}
		};
		runnables.add(addAndRemove);
	}

	/**
	 * Return a runnable for refreshing a resource.
	 * 
	 * @param resource
	 * @return Runnable
	 */
	private Runnable getRefreshRunnable(final IResource resource)
	{
		return new Runnable()
		{
			public void run()
			{
				((StructuredViewer) viewer).refresh(resource);
			}
		};
	}

	/**
	 * Run all of the runnables that are the widget updates.
	 * 
	 * @param runnables
	 */
	private void runUpdates(Collection runnables)
	{
		Iterator runnableIterator = runnables.iterator();
		while (runnableIterator.hasNext())
		{
			((Runnable) runnableIterator.next()).run();
		}

	}

}
