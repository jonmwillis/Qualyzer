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
package ca.mcgill.cs.swevo.qualyzer.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;

/**
 *
 */
public class MemoPropertiesDialog extends TitleAreaDialog
{
	private static final int COLS = 3;
	private static final String ADD_IMG = "ADD_IMG";
	private static final String REMOVE_IMG = "REMOVE_IMG";
	private static final String SLASH = "/";
	
	private Memo fMemo;
	private ImageRegistry fRegistry;
	private DateTime fDate;
	private Combo fAuthor;

	private Table fTable;
	
	/**
	 * Constructor.
	 * @param shell
	 * @param memo
	 */
	public MemoPropertiesDialog(Shell shell, Memo memo)
	{
		super(shell);
		fMemo = memo;
		fRegistry = QualyzerActivator.getDefault().getImageRegistry();
		addImage(ADD_IMG, QualyzerActivator.PLUGIN_ID, "icons/add_obj.gif");
		addImage(REMOVE_IMG, QualyzerActivator.PLUGIN_ID, "icons/remove_obj.gif");
	}
	
	private void addImage(String key, String pluginID, String path)
	{
		String fullKey = computeKey(key, pluginID);
		ImageDescriptor descriptor = fRegistry.getDescriptor(fullKey);
		if(descriptor == null)
		{
			fRegistry.put(fullKey, AbstractUIPlugin.imageDescriptorFromPlugin(pluginID, path));
		}
	}
	
	private String computeKey(String key, String pluginID)
	{
		return pluginID + "_" + key; //$NON-NLS-1$
	}
	
	private Image getImage(String key, String pluginID)
	{
		return fRegistry.get(computeKey(key, pluginID));
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	@Override
	public void create()
	{
		super.create();
		setTitle("Properties");
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		parent.setLayout(new GridLayout());
		
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(2, true));
		container.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		
		Label label = new Label(container, SWT.NULL);
		label.setText("Name");
		
		label = new Label(container, SWT.BORDER);
		label.setText(fMemo.getName());
		label.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		
		label = new Label(container, SWT.NULL);
		label.setText("File name");
		
		label = new Label(container, SWT.BORDER);
		label.setText(fMemo.getFileName());
		label.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		
		label = new Label(container, SWT.NULL);
		label.setText("Date");
		
		fDate = new DateTime(container, SWT.DATE);
		String[] info = fMemo.getDate().split(SLASH);
		fDate.setDate(Integer.parseInt(info[2]), Integer.parseInt(info[0])-1, Integer.parseInt(info[1]));
		
		label = new Label(container, SWT.NULL);
		label.setText("Author");
		
		fAuthor = new Combo(container, SWT.READ_ONLY);
		fAuthor.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		for(Investigator investigator : fMemo.getProject().getInvestigators())
		{
			fAuthor.add(investigator.getNickName());
			if(investigator.equals(fMemo.getAuthor()))
			{
				fAuthor.select(fAuthor.getItemCount() - 1);
			}
		}
		
		Composite composite = new Composite(container, SWT.NULL);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false,  2, 1));
		composite.setLayout(new GridLayout(COLS, false));
		
		label = new Label(composite, SWT.NULL);
		label.setText("Participants");
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Button button = new Button(composite, SWT.PUSH);
		button.setImage(getImage(ADD_IMG, QualyzerActivator.PLUGIN_ID));
		//TODO add listener
		
		button = new Button(composite, SWT.PUSH);
		button.setImage(getImage(REMOVE_IMG, QualyzerActivator.PLUGIN_ID));
		//TODO add listener
		
		fTable = new Table(container, SWT.MULTI);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		fTable.setLayoutData(gd);
		buildParticipants();
		
		return parent;
	}
	
	private void buildParticipants()
	{
		Memo lMemo = Facade.getInstance().forceMemoLoad(fMemo);
		for(Participant participant : lMemo.getParticipants())
		{
			TableItem item = new TableItem(fTable, SWT.NULL);
			item.setText(participant.getParticipantId());
		}
	}
	
	/**
	 * @return the fDate
	 */
	public String getDate()
	{
		return (fDate.getMonth() + 1)+SLASH+fDate.getDay()+SLASH+fDate.getYear();
	}
	
	/**
	 * @return the fAuthor
	 */
	public Investigator getAuthor()
	{
		String iName = fAuthor.getText();
		
		for(Investigator investigator : fMemo.getProject().getInvestigators())
		{
			if(investigator.getNickName().equals(iName))
			{
				return investigator;
			}
		}
		
		//Should not happen.
		return null;
	}
	
	/**
	 * Get the list of selected participants.
	 * @return
	 */
	public List<Participant> getParticipants()
	{
		List<Participant> participants = new ArrayList<Participant>();
		
		for(TableItem item : fTable.getItems())
		{
			for(Participant part : fMemo.getProject().getParticipants())
			{
				if(part.getParticipantId().equals(item.getText()))
				{
					participants.add(part);
				}
			}
		}
		
		return participants;
	}
}
