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
package ca.mcgill.cs.swevo.qualyzer.dialogs;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 *
 */
public class ReportIssueDialog extends TitleAreaDialog
{
//	private static Logger gLogger = LoggerFactory.getLogger(ReportIssueDialog.class);

	/**
	 * 
	 */
	private static final int REPORT_HEIGHT = 150;

	private Text fReportText;
	
	private Button fCopyButton;
	
	private Clipboard fClipboard;
	
	/**
	 * Constructor.
	 * 
	 * @param shell
	 */
	public ReportIssueDialog(Shell shell)
	{
		super(shell);
		fClipboard = new Clipboard(PlatformUI.getWorkbench().getDisplay());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	@Override
	public void create()
	{
		super.create();
		setTitle(Messages.getString("dialogs.ReportIssueDialog.title")); //$NON-NLS-1$
		setMessage(Messages.getString("dialogs.ReportIssueDialog.message")); //$NON-NLS-1$
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		// CSOFF:
		fReportText = new Text(composite, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL);
		// CSON:
		GridData gData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gData.heightHint = REPORT_HEIGHT;
		fReportText.setLayoutData(gData);
		fReportText.setText(getReportText());
		
		fCopyButton = new Button(composite, SWT.PUSH);
		fCopyButton.setText(Messages.getString("dialogs.ReportIssueDialog.copyButton"));
		fCopyButton.addSelectionListener(new SelectionListener()
		{
			
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				copyToClipboard();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		});
		
		return parent;
	}
	
	private String getReportText()
	{
		return "Hello\nWorld!\n\n\n\n\n\n\n\nHello!\n\n\n\n\n\n\n\n\n\n\n\n\nHello!";
	}
	
	private void copyToClipboard()
	{
		
		String textData = fReportText.getText();
        TextTransfer textTransfer = TextTransfer.getInstance();
        fClipboard.setContents(new Object[] { textData },
            new Transfer[] { textTransfer });
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	public void okPressed()
	{
		super.okPressed();
	}

	@Override
	public boolean close()
	{
		if (fClipboard != null) 
		{
			fClipboard.dispose();
			fClipboard = null;
		}
		
		return super.close();
	}
	
	
	
	
}
