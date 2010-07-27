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
package ca.mcgill.cs.swevo.qualyzer.editors.pages;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import ca.mcgill.cs.swevo.qualyzer.editors.inputs.InvestigatorEditorInput;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.InvestigatorListener;
import ca.mcgill.cs.swevo.qualyzer.model.ListenerManager;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.MemoListener;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.ProjectListener;
import ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType;
import ca.mcgill.cs.swevo.qualyzer.model.validation.InvestigatorValidator;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;

/**
 * The main page of the Investigator editor.
 *
 */
public class InvestigatorEditorPage extends FormPage implements ProjectListener, InvestigatorListener, MemoListener
{
	/**
	 * 
	 */
	private static final String INVESTIGATOR = Messages.getString(
			"editors.pages.InvestigatorEditorPage.investigator"); //$NON-NLS-1$
	private Text fNickname;
	private Text fFullname;
	private Text fInstitution;
	private FormToolkit fToolkit;
	private Composite fMemoSectionClient;
	
	private Investigator fInvestigator;
	private boolean fIsDirty;
	private ScrolledForm fForm;
	/**
	 * @param editor
	 * @param investigator 
	 */
	public InvestigatorEditorPage(FormEditor editor, Investigator investigator)
	{
		super(editor, INVESTIGATOR, INVESTIGATOR);
		fInvestigator = investigator;
		fIsDirty = false;
		
		ListenerManager listenerManager = Facade.getInstance().getListenerManager();
		listenerManager.registerProjectListener(fInvestigator.getProject(), this);
		listenerManager.registerInvestigatorListener(fInvestigator.getProject(), this);
		listenerManager.registerMemoListener(fInvestigator.getProject(), this);
	}

	@Override
	public void createFormContent(IManagedForm managed)
	{
		fForm = managed.getForm();
		fToolkit = managed.getToolkit();
		fForm.setText(INVESTIGATOR);
		
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		Composite body = fForm.getBody();
		body.setLayout(layout);
		
		@SuppressWarnings("unused")
		Label label = fToolkit.createLabel(body, 
				Messages.getString("editors.pages.InvestigatorEditorPage.nickname")); //$NON-NLS-1$
		fNickname = createText(fInvestigator.getNickName(), body);
		
		label = fToolkit.createLabel(body, 
				Messages.getString("editors.pages.InvestigatorEditorPage.fullName")); //$NON-NLS-1$
		fFullname = createText(fInvestigator.getFullName(), body);
		fNickname.addKeyListener(createKeyAdapter(fForm));

		label = fToolkit.createLabel(body, 
				Messages.getString("editors.pages.InvestigatorEditorPage.institution")); //$NON-NLS-1$
		fInstitution = createText(fInvestigator.getInstitution(), body);
		
		//Removing placeholders until they are used - JF
//		createInterviewSection(form, toolkit, body);
//		
//		createCodedSection(form, toolkit, body);
//		
		createMemoSection(fForm, body);
	
		fToolkit.paintBordersFor(body);
	}

	/**
	 * @param form 
	 * @return
	 */
	private KeyAdapter createKeyAdapter(final ScrolledForm form)
	{
		return new KeyAdapter(){
			private ScrolledForm fForm = form;
			
			@Override
			public void keyReleased(KeyEvent event)
			{
				InvestigatorValidator lValidator = new InvestigatorValidator(fNickname.getText(),
						fInvestigator.getNickName(), fFullname.getText(), fInstitution.getText(), 
						fInvestigator.getProject());
				
				if(!lValidator.isValid())
				{
					fForm.setMessage(lValidator.getErrorMessage(), IMessageProvider.ERROR);
					notDirty();
				}
				else
				{
					fForm.setMessage(null, IMessageProvider.NONE);
				}
			}
		};
	}

	/**
	 * @param form
	 * @param toolkit
	 * @param body
	 */
	private void createMemoSection(final ScrolledForm form, Composite body)
	{
		TableWrapData td;
		Section section;
		GridLayout grid;
		section = fToolkit.createSection(body, Section.EXPANDED | Section.TITLE_BAR | Section.TWISTIE);
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.colspan = 2;
		section.setLayoutData(td);
		section.setText(Messages.getString("editors.pages.InvestigatorEditorPage.memos")); //$NON-NLS-1$
		section.addExpansionListener(createExpansionListener(form));
		fMemoSectionClient = fToolkit.createComposite(section);
		grid = new GridLayout();
		grid.numColumns = 1;
		fMemoSectionClient.setLayout(grid);
		buildMemos();
		section.setClient(fMemoSectionClient);
	}

	
	/**
	 * @param sectionClient
	 * @param toolkit
	 */
	private void buildMemos()
	{
		for(Control control : fMemoSectionClient.getChildren())
		{
			control.dispose();
		}
		
		for(Memo memo : fInvestigator.getProject().getMemos())
		{
			if(fInvestigator.equals(memo.getAuthor()))
			{
				Hyperlink link = fToolkit.createHyperlink(fMemoSectionClient, memo.getName(), SWT.NULL);
				link.addHyperlinkListener(openMemoListener(memo));
			}
		}
		
		fForm.reflow(true);
		
	}

	/**
	 * @param memo
	 * @return
	 */
	private HyperlinkAdapter openMemoListener(final Memo memo)
	{
		return new HyperlinkAdapter(){
			
			@Override
			public void linkActivated(HyperlinkEvent e)
			{
				ResourcesUtil.openEditor(getSite().getPage(), memo);
			}
		};
	}

	//Removing for 0.2 as they are not yet used - JF
//	/**
//	 * @param form
//	 * @param toolkit
//	 * @param body
//	 */
//	private void createCodedSection(final ScrolledForm form, FormToolkit toolkit, Composite body)
//	{
//		TableWrapData td;
//		Label label;
//		Section section;
//		Composite sectionClient;
//		GridLayout grid;
//		GridData gd;
//		section = toolkit.createSection(body, Section.EXPANDED | Section.TITLE_BAR | Section.TWISTIE);
//		td = new TableWrapData(TableWrapData.FILL_GRAB);
//		td.colspan = 2;
//		section.setLayoutData(td);
//		section.setText(Messages.getString("editors.pages.InvestigatorEditorPage.codedTranscripts")); //$NON-NLS-1$
//		section.addExpansionListener(createExpansionListener(form));
//		sectionClient = toolkit.createComposite(section);
//		grid = new GridLayout();
//		grid.numColumns = 1;
//		sectionClient.setLayout(grid);
//		//TODO generate the interview data
//		//TODO make clickable
//		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
//		label = toolkit.createLabel(sectionClient, "Example Interview"); //$NON-NLS-1$
//		label.setLayoutData(gd);
//		section.setClient(sectionClient);
//	}

	
	//Removing for 0.2 as NYI - JF
//	/**
//	 * @param form
//	 * @param toolkit
//	 * @param body
//	 */
//	private void createInterviewSection(final ScrolledForm form, FormToolkit toolkit, Composite body)
//	{
//		TableWrapData td;
//		Label label;
//		Section section = toolkit.createSection(body, Section.EXPANDED | Section.TITLE_BAR | Section.TWISTIE);
//		td = new TableWrapData(TableWrapData.FILL_GRAB);
//		td.colspan = 2;
//		section.setLayoutData(td);
//		section.setText(Messages.getString("editors.pages.InvestigatorEditorPage.transcriptsConducted")); //$NON-NLS-1$
//		section.addExpansionListener(createExpansionListener(form));
//		Composite sectionClient = toolkit.createComposite(section);
//		GridLayout grid = new GridLayout();
//		grid.numColumns = 1;
//		sectionClient.setLayout(grid);
//		//TODO generate the interview data
//		//TODO make clickable
//		GridData gd = new GridData(SWT.FILL, SWT.NULL, true, false);
//		label = toolkit.createLabel(sectionClient, "Example Interview"); //$NON-NLS-1$
//		label.setLayoutData(gd);
//		section.setClient(sectionClient);
//	}

	
	/**
	 * @param form
	 * @return
	 */
	private ExpansionAdapter createExpansionListener(final ScrolledForm form)
	{
		return new ExpansionAdapter(){
			public void expansionStateChanged(ExpansionEvent e)
			{
				form.reflow(true);
			}
		};
	}
	
	private Text createText(String data, Composite parent)
	{
		Text text = fToolkit.createText(parent, data);
		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		text.setLayoutData(td);
		text.addKeyListener(createKeyListener());
		
		return text;
	}
	
	@Override
	public boolean isDirty()
	{
		return fIsDirty;
	}
	
	/**
	 * Get the Nickname that was entered for this Investigator.
	 * @return The Nickname field.
	 */
	public String getNickname()
	{
		return fNickname.getText();
	}
	
	/**
	 * Get the full name that was entered for this Investigator.
	 * @return The full name field.
	 */
	public String getFullname()
	{
		return fFullname.getText();
	}
	
	/**
	 * Get the Institution that was entered for this Investigator.
	 * @return The Institution field.
	 */
	public String getInstitution()
	{
		return fInstitution.getText();
	}
	
	private KeyListener createKeyListener()
	{
		return new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e){}

			@Override
			public void keyReleased(KeyEvent e)
			{
				if(!fIsDirty && fieldHasChanged())
				{
					fIsDirty = true;
					getEditor().editorDirtyStateChanged();
				}
			}

			private boolean fieldHasChanged()
			{
				boolean hasChanged = false;
				if(!fNickname.getText().equals(fInvestigator.getNickName()))
				{
					hasChanged = true;
				}
				else if(!fFullname.getText().equals(fInvestigator.getFullName()))
				{
					hasChanged = true;
				}
				else if(!fInstitution.getText().equals(fInvestigator.getInstitution()))
				{
					hasChanged = true;
				}
				return hasChanged;
			}
			
		};
	}

	/**
	 * Set dirty to false.
	 */
	public void notDirty()
	{
		fIsDirty = false;
		getEditor().editorDirtyStateChanged();
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.model.ProjectListener#projectChanged(
	 * ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType, 
	 * ca.mcgill.cs.swevo.qualyzer.model.Project, ca.mcgill.cs.swevo.qualyzer.model.Facade)
	 */
	@Override
	public void projectChanged(ChangeType cType, Project project, Facade facade)
	{
		if(cType == ChangeType.DELETE)
		{
			IWorkbenchPage page = getEditor().getSite().getPage();
			ResourcesUtil.closeEditor(page, getEditor().getEditorInput().getName());
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormPage#dispose()
	 */
	@Override
	public void dispose()
	{
		ListenerManager listenerManager = Facade.getInstance().getListenerManager();
		listenerManager.unregisterProjectListener(fInvestigator.getProject(), this);
		listenerManager.unregisterInvestigatorListener(fInvestigator.getProject(), this);
		listenerManager.unregisterMemoListener(fInvestigator.getProject(), this);
		super.dispose();
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.model.InvestigatorListener#investigatorChanged(
	 * ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType, ca.mcgill.cs.swevo.qualyzer.model.Investigator[], 
	 * ca.mcgill.cs.swevo.qualyzer.model.Facade)
	 */
	@Override
	public void investigatorChanged(ChangeType cType, Investigator[] investigators, Facade facade)
	{
		if(cType == ChangeType.DELETE)
		{
			IWorkbenchPage page = getEditor().getSite().getPage();
			for(Investigator investigator : investigators)
			{
				if(fInvestigator.equals(investigator))
				{
					ResourcesUtil.closeEditor(page, getEditorInput().getName());
					break;
				}
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.model.MemoListener#memoChanged(
	 * ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType, 
	 * ca.mcgill.cs.swevo.qualyzer.model.Memo[], ca.mcgill.cs.swevo.qualyzer.model.Facade)
	 */
	@Override
	public void memoChanged(ChangeType cType, Memo[] memos, Facade facade)
	{
		Project project;
		if(ChangeType.DELETE == cType)
		{
			project = PersistenceManager.getInstance().getProject(fInvestigator.getProject().getName());
		}
		else
		{
			project = memos[0].getProject();
		}
		
		for(Investigator investigator : project.getInvestigators())
		{
			if(fInvestigator.equals(investigator))
			{
				setInput(new InvestigatorEditorInput(investigator));
				break;
			}
		}
		
		fInvestigator = ((InvestigatorEditorInput) getEditorInput()).getInvestigator();
		
		buildMemos();
		
	}
}
