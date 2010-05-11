package ca.mcgill.cs.swevo.qualyzer;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	public final static String PROJECT_EXPLORER_VIEW_ID = "ca.mcgill.cs.swevo.qualyzer.projectexplorer";
	
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();

        // Top left.
        IFolderLayout topLeft = layout.createFolder(
                "topLeft", IPageLayout.LEFT, (float) 0.26, editorArea);//$NON-NLS-1$
        topLeft.addView(PROJECT_EXPLORER_VIEW_ID);
        topLeft.addPlaceholder(IPageLayout.ID_BOOKMARKS);

	}

}
