/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.handlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.TestUtil;
import ca.mcgill.cs.swevo.qualyzer.dialogs.QualyzerWizardDialog;
import ca.mcgill.cs.swevo.qualyzer.editors.IDialogTester;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.wizards.pages.AddInvestigatorPage;

/**
 * @author Jonathan Faubert
 *
 */
public class AddInvestigatorHandlerTest
{
	/**
	 * 
	 */
	private static final String PROJECT = "Project";
	/**
	 * 
	 */
	private static final String INSTITUTE = "mcgill";
	/**
	 * 
	 */
	private static final String FULL = "jonathan";
	/**
	 * 
	 */
	private static final String NAME = "jon";
	private Project fProject;
	private IProject wProject;
	
	@Before
	public void setUp()
	{
		fProject = Facade.getInstance().createProject(PROJECT, "inv", "", "");
		wProject = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT);
	}
	
	@After
	public void tearDown()
	{
		Facade.getInstance().deleteProject(fProject);
	}
	
	@Test
	public void testAddInvestigator()
	{
		TestUtil.setProjectExplorerSelection(wProject);
		
		AddInvestigatorHandler handler = new AddInvestigatorHandler();
		handler.setWindowsBlock(false);
		handler.setTester(new IDialogTester(){

			@Override
			public void execute(Dialog dialog)
			{
				IWizardPage wizardPage = ((WizardDialog) dialog).getCurrentPage();
				
				AddInvestigatorPage page = (AddInvestigatorPage) wizardPage;
				
				page.getNicknameText().setText(NAME);
				page.getFullNameText().setText(FULL);
				page.getInstitutionText().setText(INSTITUTE);
				
				((QualyzerWizardDialog) dialog).finishPressed();
			}
			
		});
		
		try
		{
			handler.execute(null);
		}
		catch (ExecutionException e)
		{
			fail();
		}
		
		fProject = PersistenceManager.getInstance().getProject(PROJECT);
		
		assertEquals(fProject.getInvestigators().size(), 2);
		Investigator test = fProject.getInvestigators().get(1);
		assertEquals(test.getNickName(), NAME);
		assertEquals(test.getFullName(), FULL);
		assertEquals(test.getInstitution(), INSTITUTE);
		
	}
}
