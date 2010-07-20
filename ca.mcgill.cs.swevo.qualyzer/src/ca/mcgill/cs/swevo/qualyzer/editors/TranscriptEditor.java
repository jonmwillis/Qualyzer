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

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.ui.IWorkbenchPage;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.QualyzerException;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.model.TranscriptListener;
import ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;

/**
 *
 */
public class TranscriptEditor extends RTFEditor implements TranscriptListener
{

	public static final String ID = "ca.mcgill.cs.swevo.qualyzer.editors.transcriptEditor"; //$NON-NLS-1$
	
	private static final int NUM_COLS = 4;
	
	private static final String PLAY_IMG = "PLAY_IMG";
	private static final String PAUSE_IMG = "PAUSE_IMG";
	private static final String STOP_IMG = "STOP_IMG";
	private static final String BOLD_IMG = "BOLD_IMG";
	private static final String ITALIC_IMG = "ITALIC_IMG";
	private static final String UNDERLINE_IMG = "UNDERLINE_IMG";
	private static final String CODE_IMG = "CODE_IMG";

	private Button fBoldButton;
	private Button fUnderlineButton;
	private Button fItalicButton;
	private Button fCodeButton;
	private Button fPlayButton;
	private Button fStopButton;
	private AudioPlayer fAudioPlayer;

	private Label fTimeLabel;

	
	/**
	 * Constructor.
	 */
	public TranscriptEditor()
	{
		addImage(PLAY_IMG, QualyzerActivator.PLUGIN_ID, "icons/play.png");
		addImage(PAUSE_IMG, QualyzerActivator.PLUGIN_ID, "icons/pause.png");
		addImage(STOP_IMG, QualyzerActivator.PLUGIN_ID, "icons/stop.png");
		addImage(BOLD_IMG, QualyzerActivator.PLUGIN_ID, "icons/text_bold.png");
		addImage(ITALIC_IMG, QualyzerActivator.PLUGIN_ID, "icons/text_italic.png");
		addImage(UNDERLINE_IMG, QualyzerActivator.PLUGIN_ID, "icons/text_underline.png");
		addImage(CODE_IMG, QualyzerActivator.PLUGIN_ID, "icons/code_obj.gif");
		
	}
	
/* (non-Javadoc)
 * @see ca.mcgill.cs.swevo.qualyzer.editors.RTFEditor#createSourceViewer(org.eclipse.swt.widgets.Composite,
 *  org.eclipse.jface.text.source.IVerticalRuler, int)
 */
@Override
protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles)
{
	final SourceViewer viewer = (SourceViewer) super.createSourceViewer(parent, ruler, styles);
	viewer.addSelectionChangedListener(new ISelectionChangedListener(){
		@Override
		public void selectionChanged(SelectionChangedEvent event)
		{
			IAnnotationModel model = viewer.getAnnotationModel();
			Point selection = viewer.getSelectedRange();
			
			boolean enabled = selection.y != 0;
			
			fBoldButton.setEnabled(enabled && isBoldEnabled(model, selection));
			fItalicButton.setEnabled(enabled && isItalicEnabled(model, selection));
			fUnderlineButton.setEnabled(enabled && isUnderlineEnabled(model, selection));
			fCodeButton.setEnabled(enabled && isMarkEnabled(model, selection));
			
			fBoldButton.setSelection(isBoldChecked());
			fItalicButton.setSelection(isItalicChecked());
			fUnderlineButton.setSelection(isUnderlineChecked());
		}
		
	});
	return viewer;
}
	
	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.editors.RTFEditor#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		//This controls displaying of the top button bar.
		parent.setLayout(new GridLayout(1, true));
		
		Composite topBar = new Composite(parent, SWT.BORDER);
		topBar.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		topBar.setLayout(new GridLayout(2, false));
		
		Control buttonBar = createFormatButtonBar(topBar);
		buttonBar.setLayoutData(new GridData(SWT.FILL, SWT.NULL, false, false));
		Control musicBar = createMusicBar(topBar);
		musicBar.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		
		super.createPartControl(parent);
		
		parent.getChildren()[1].setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		if(((Transcript) getDocument()).getAudioFile() == null)
		{
			musicBar.setEnabled(false);
		}
		else
		{
			Transcript trans = (Transcript) getDocument();
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(trans.getProject().getName());
			String audioFile = project.getLocation() + File.separator + trans.getAudioFile().getRelativePath();
			
			fAudioPlayer = new AudioPlayer(audioFile, fTimeLabel);
		}
		
		hookupButtonActions();
		
		Facade.getInstance().getListenerManager().registerTranscriptListener(getDocument().getProject(), this);
	}
	
	/**
	 * 
	 */
	private void hookupButtonActions()
	{
		fBoldButton.addSelectionListener(createButtonSelectionListener(getBoldAction()));
		fUnderlineButton.addSelectionListener(createButtonSelectionListener(getUnderlineAction()));
		fItalicButton.addSelectionListener(createButtonSelectionListener(getItalicAction()));
		fCodeButton.addSelectionListener(createButtonSelectionListener(getMarkTextAction()));
		
		fPlayButton.addSelectionListener(new SelectionAdapter(){
			private final Image fPLAY = getImage(PLAY_IMG, QualyzerActivator.PLUGIN_ID);
			private final Image fPAUSE = getImage(PAUSE_IMG, QualyzerActivator.PLUGIN_ID);
			private boolean fPlaying = false;
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				try
				{
					if(fPlaying)
					{
						fAudioPlayer.pause();
						fPlayButton.setImage(fPLAY);
						fPlaying = false;
					}
					else
					{
						fAudioPlayer.play();
						fPlayButton.setImage(fPAUSE);
						fPlaying = true;
					}
				}
				catch(QualyzerException ex)
				{
					System.out.println(ex.getMessage());
				}
			}
		});
		fStopButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				try
				{
					fPlayButton.setImage(getImage(PLAY_IMG, QualyzerActivator.PLUGIN_ID));
					fAudioPlayer.stop();
				}
				catch(QualyzerException ex)
				{
					System.out.println(ex.getMessage());
				}
			}
		});
	}

	//these create the top button bar.
	/**
	 * @param topBar
	 * @return
	 */
	private Control createMusicBar(Composite parent)
	{
		Composite musicBar = new Composite(parent, SWT.BORDER);
		musicBar.setLayout(new GridLayout(NUM_COLS, false));
		
		fPlayButton = new Button(musicBar, SWT.PUSH);
		fPlayButton.setImage(getImage(PLAY_IMG, QualyzerActivator.PLUGIN_ID));
		
		fStopButton = new Button(musicBar, SWT.PUSH);
		fStopButton.setImage(getImage(STOP_IMG, QualyzerActivator.PLUGIN_ID));
		
		Scale scale = new Scale(musicBar, SWT.HORIZONTAL);
		scale.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		fTimeLabel = new Label(musicBar, SWT.NULL);
		fTimeLabel.setLayoutData(new GridData(SWT.NULL, SWT.FILL, false, false));
		fTimeLabel.setText("0:00/0:00");
	
		return musicBar;
	}

	private Control createFormatButtonBar(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.BORDER);
		GridLayout layout = new GridLayout(NUM_COLS, true);
		composite.setLayout(layout);
		
		fBoldButton = new Button(composite, SWT.TOGGLE);
		fBoldButton.setImage(getImage(BOLD_IMG, QualyzerActivator.PLUGIN_ID));
		fBoldButton.setEnabled(false);
		
		fUnderlineButton = new Button(composite, SWT.TOGGLE);
		fUnderlineButton.setImage(getImage(UNDERLINE_IMG, QualyzerActivator.PLUGIN_ID));
		fUnderlineButton.setEnabled(false);
		
		fItalicButton = new Button(composite, SWT.TOGGLE);
		fItalicButton.setImage(getImage(ITALIC_IMG, QualyzerActivator.PLUGIN_ID));
		fItalicButton.setEnabled(false);
		
		fCodeButton = new Button(composite, SWT.PUSH);
		fCodeButton.setImage(getImage(CODE_IMG, QualyzerActivator.PLUGIN_ID));
		fCodeButton.setEnabled(false);
		
		return composite;
	}
	
	/**
	 * @param fBoldAction2
	 * @return
	 */
	private SelectionAdapter createButtonSelectionListener(final Action action)
	{
		return new SelectionAdapter(){
			private Action fAction = action;
			
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if(fAction.isEnabled())
				{
					fAction.run();
				}
			}
		};
	}
	
	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.model.TranscriptListener#transcriptChanged(
	 * ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType, ca.mcgill.cs.swevo.qualyzer.model.Transcript[],
	 *  ca.mcgill.cs.swevo.qualyzer.model.Facade)
	 */
	@Override
	public void transcriptChanged(ChangeType cType, Transcript[] transcripts, Facade facade)
	{
		if(cType == ChangeType.DELETE)
		{
			for(Transcript transcript : transcripts)
			{
				if(transcript.equals(getDocument()))
				{
					IWorkbenchPage page = getSite().getPage();
					ResourcesUtil.closeEditor(page, getEditorInput().getName());
					break;
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.editors.RTFEditor#dispose()
	 */
	@Override
	public void dispose()
	{
		Facade.getInstance().getListenerManager().unregisterTranscriptListener(getDocument().getProject(), this);
		super.dispose();
	}

}
