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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.core.internal.runtime.InternalPlatform;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.internal.adaptor.EclipseEnvironmentInfo;
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

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.QualyzerException;

/**
 *
 */
@SuppressWarnings("restriction")
public class ReportIssueDialog extends TitleAreaDialog
{
	/**
	 * 
	 */
	private static final String QUALYZER_LOG = "qualyzer.log";
	// private static Logger gLogger = LoggerFactory.getLogger(ReportIssueDialog.class);

	private static final int REPORT_HEIGHT = 150;
	private static final int REPORT_WIDTH = 250;
	
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

	/*
	 * (non-Javadoc)
	 * 
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
		gData.widthHint = REPORT_WIDTH;
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

	// CSOFF:
	private String getReportText()
	{
		StringBuilder sBuilder = new StringBuilder();

		sBuilder.append("=== QUALYZER ISSUE REPORT ===\n\n"); //$NON-NLS-1$
		sBuilder.append("==== System Information ====\n"); //$NON-NLS-1$
		sBuilder.append(String.format("Qualyzer Version=%s\n", QualyzerActivator.CURRENT_VERSION)); //$NON-NLS-1$
		sBuilder.append(String.format("Java Version=%s\n", System.getProperty("java.version"))); //$NON-NLS-1$
		sBuilder.append(String.format("Java Vendor=%s\n", System.getProperty("java.vendor"))); //$NON-NLS-1$
		sBuilder.append(String.format("BootLoader constants: OS=%s, ARCH=%s, WS=%s, NL=%s\n", 
								EclipseEnvironmentInfo.getDefault().getOS(), 
								EclipseEnvironmentInfo.getDefault().getOSArch(), 
								EclipseEnvironmentInfo.getDefault().getWS(), 
								EclipseEnvironmentInfo.getDefault().getNL())); //$NON-NLS-1$
		sBuilder.append(String.format("Command-line arguments: %s\n", 
								(Object[])EclipseEnvironmentInfo.getDefault().getCommandLineArgs())); //$NON-NLS-1$
		sBuilder.append("==== End of System Information ====\n\n"); //$NON-NLS-1$
		
		sBuilder.append("==== Qualyzer Log ====\n"); //$NON-NLS-1$
		String qualyzerPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().append(QUALYZER_LOG).toOSString();
		sBuilder.append(readFile(qualyzerPath));
		sBuilder.append("==== End of Qualyzer Log ====\n\n"); //$NON-NLS-1$
		
		sBuilder.append("==== Workspace Log ====\n"); //$NON-NLS-1$
		String workspaceLogPath = InternalPlatform.getDefault().getFrameworkLog().getFile().getAbsolutePath();
		sBuilder.append(readFile(workspaceLogPath));
		sBuilder.append("==== End of Workspace Log ====\n\n"); //$NON-NLS-1$
		
		sBuilder.append("=== END OF REPORT ===\n"); //$NON-NLS-1$

		return sBuilder.toString();
	}
	// CSON:
	
	private String readFile(String filePath) 
	{
		File file = new File(filePath);
		if (!file.exists()) 
		{
			return String.format("Log file not found: %s\n", filePath);
		}
		
		StringBuilder sBuilder = new StringBuilder();
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String line = null;
			while ((line = reader.readLine()) != null) 
			{
				sBuilder.append(line);
				sBuilder.append("\n"); //$NON-NLS-1$
			}
			reader.close();
		}
		catch(IOException ioException)
		{
			throw new QualyzerException("Error while reading log file " + filePath, ioException);
		}
		
		return sBuilder.toString();
	}

	private void copyToClipboard()
	{

		String textData = fReportText.getText();
		TextTransfer textTransfer = TextTransfer.getInstance();
		fClipboard.setContents(new Object[] { textData }, new Transfer[] { textTransfer });
	}

	/*
	 * (non-Javadoc)
	 * 
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
