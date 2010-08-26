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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

import ca.mcgill.cs.swevo.qualyzer.IQualyzerPreferenceConstants;
import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.QualyzerException;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.RTFEditorInput;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.model.TranscriptListener;
import ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType;

/**
 *	Extends the RTFEditor with a formatting bar and an audio bar.
 */
public class TranscriptEditor extends RTFEditor implements TranscriptListener
{	
	public static final String ID = "ca.mcgill.cs.swevo.qualyzer.editors.transcriptEditor"; //$NON-NLS-1$
	
	private static final int NUM_COLS = 10;
	private static final int SECONDS_PER_MINUTE = 60;
	private static final int TEN = 10;
	
	private static final String PLAY_IMG = "PLAY_IMG"; //$NON-NLS-1$
	private static final String PAUSE_IMG = "PAUSE_IMG"; //$NON-NLS-1$
	private static final String STOP_IMG = "STOP_IMG"; //$NON-NLS-1$
	private static final String BOLD_IMG = "BOLD_IMG"; //$NON-NLS-1$
	private static final String ITALIC_IMG = "ITALIC_IMG"; //$NON-NLS-1$
	private static final String UNDERLINE_IMG = "UNDERLINE_IMG"; //$NON-NLS-1$
	private static final String CODE_IMG = "CODE_IMG"; //$NON-NLS-1$
	private static final String BACK_IMG = "BACK_IMG"; //$NON-NLS-1$
	private static final String FORWARD_IMG = "FORWARD_IMG"; //$NON-NLS-1$

	private Button fBoldButton;
	private Button fUnderlineButton;
	private Button fItalicButton;
	private Button fCodeButton;
	private Button fPlayButton;
	private Button fStopButton;
	private Button fBackButton;
	private Button fForwardButton;
	private AudioPlayer fAudioPlayer;

	private Label fTimeLabel;
	private Scale fAudioSlider;
	private int fAudioLength;
	
	private Composite fTopBar;
	
	private int fSeekTime;
	
	private Action fAddTimeStampAction;

	/**
	 * Constructor.
	 */
	public TranscriptEditor()
	{
		addImage(PLAY_IMG, QualyzerActivator.PLUGIN_ID, "icons/play.png"); //$NON-NLS-1$
		addImage(PAUSE_IMG, QualyzerActivator.PLUGIN_ID, "icons/pause.png"); //$NON-NLS-1$
		addImage(STOP_IMG, QualyzerActivator.PLUGIN_ID, "icons/stop.png"); //$NON-NLS-1$
		addImage(BOLD_IMG, QualyzerActivator.PLUGIN_ID, "icons/text_bold.png"); //$NON-NLS-1$
		addImage(ITALIC_IMG, QualyzerActivator.PLUGIN_ID, "icons/text_italic.png"); //$NON-NLS-1$
		addImage(UNDERLINE_IMG, QualyzerActivator.PLUGIN_ID, "icons/text_underline.png"); //$NON-NLS-1$
		addImage(CODE_IMG, QualyzerActivator.PLUGIN_ID, "icons/code_obj.gif"); //$NON-NLS-1$
		addImage(BACK_IMG, QualyzerActivator.PLUGIN_ID, "icons/seek-backward.png"); //$NON-NLS-1$
		addImage(FORWARD_IMG, QualyzerActivator.PLUGIN_ID, "icons/seek-forward.png"); //$NON-NLS-1$
		
		fSeekTime = QualyzerActivator.getDefault().getPreferenceStore().getInt(
				IQualyzerPreferenceConstants.SEEK_TIME);
		QualyzerActivator.getDefault().getPreferenceStore().addPropertyChangeListener(this);		
	}

	/**
	 * 
	 */
	private void createTimeStampAction()
	{
		fAddTimeStampAction = new Action("Add Time Stamp")
		{
			@Override
			public void run()
			{
				addTimeStamp();
			}
		};
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
		
		fTopBar = new Composite(parent, SWT.BORDER);
		fTopBar.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		fTopBar.setLayout(new GridLayout(NUM_COLS, false));
		
		createFormatButtonBar(fTopBar);
		createMusicBar(fTopBar);
		
		super.createPartControl(parent);
		
		parent.getChildren()[1].setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		if(((Transcript) getDocument()).getAudioFile() == null)
		{
			setEnable(fTopBar, false);
			fAudioPlayer = null;
		}
		else
		{
			Transcript trans = (Transcript) getDocument();
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(trans.getProject().getFolderName());
			String audioFile = project.getLocation() + File.separator + trans.getAudioFile().getRelativePath();
			
			try
			{
				fAudioPlayer = new AudioPlayer(audioFile, this);
			}
			catch(QualyzerException e)
			{
				MessageDialog.openError(getSite().getShell(), Messages.getString(
						"editors.TranscriptEditor.audioPlayerError"), e.getMessage()); //$NON-NLS-1$
				setEnable(fTopBar, false);
			}
		}
		
		hookupButtonActions();
		
		Facade.getInstance().getListenerManager().registerTranscriptListener(getDocument().getProject(), this);
	}
	
	/**
	 * Sets the 2nd half of the passed container to the enablement according to state.
	 * Used to enable or disable the music bar.
	 * @param container
	 * @param state
	 */
	private void setEnable(Composite container, boolean state)
	{
		Control[] children = container.getChildren();
		for(int i = 0; i <= children.length/2; i++)
		{
			children[children.length - (i+1)].setEnabled(state);
		}
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
		
		final PlayPushedAdapter playAdapter = new PlayPushedAdapter();
		
		fPlayButton.addSelectionListener(playAdapter);
		fStopButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				try
				{
					fPlayButton.setImage(getImage(PLAY_IMG, QualyzerActivator.PLUGIN_ID));
					playAdapter.stop();
					fAudioPlayer.stop();
					setSeconds(0);
				}
				catch(QualyzerException ex)
				{
					openErrorDialog(ex.getMessage());
				}
			}
		});
		
		fBackButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				seekBack();
			}
		});
		
		fForwardButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				seekForward();
			}
		});
	}

	//these create the top button bar.
	/**
	 * @param topBar
	 * @return
	 */
	private Composite createMusicBar(Composite parent)
	{	
		fPlayButton = new Button(parent, SWT.PUSH);
		fPlayButton.setImage(getImage(PLAY_IMG, QualyzerActivator.PLUGIN_ID));
		
		fStopButton = new Button(parent, SWT.PUSH);
		fStopButton.setImage(getImage(STOP_IMG, QualyzerActivator.PLUGIN_ID));
		
		fBackButton = new Button(parent, SWT.PUSH);
		fBackButton.setImage(getImage(BACK_IMG, QualyzerActivator.PLUGIN_ID));
		
		fAudioSlider = new Scale(parent, SWT.HORIZONTAL);
		fAudioSlider.setMinimum(0);
		fAudioSlider.setSelection(0);
		fAudioSlider.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		fAudioSlider.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseUp(MouseEvent e)
			{
				try
				{
					fAudioPlayer.jumpToTime(fAudioSlider.getSelection());
				}
				catch(QualyzerException ex)
				{
					openErrorDialog(ex.getMessage());
				}
			}
			
		});
		
		fAudioSlider.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				setSeconds(fAudioSlider.getSelection());
			}
		});
		
		fForwardButton = new Button(parent, SWT.PUSH);
		fForwardButton.setImage(getImage(FORWARD_IMG, QualyzerActivator.PLUGIN_ID));
		
		fTimeLabel = new Label(parent, SWT.NULL);
		fTimeLabel.setLayoutData(new GridData(SWT.NULL, SWT.FILL, false, false));
		fTimeLabel.setText("0:00/0:00"); //$NON-NLS-1$
	
		return parent;
	}

	private Control createFormatButtonBar(Composite parent)
	{	
		fBoldButton = new Button(parent, SWT.TOGGLE);
		fBoldButton.setImage(getImage(BOLD_IMG, QualyzerActivator.PLUGIN_ID));
		fBoldButton.setEnabled(false);
		
		fUnderlineButton = new Button(parent, SWT.TOGGLE);
		fUnderlineButton.setImage(getImage(UNDERLINE_IMG, QualyzerActivator.PLUGIN_ID));
		fUnderlineButton.setEnabled(false);
		
		fItalicButton = new Button(parent, SWT.TOGGLE);
		fItalicButton.setImage(getImage(ITALIC_IMG, QualyzerActivator.PLUGIN_ID));
		fItalicButton.setEnabled(false);
		
		fCodeButton = new Button(parent, SWT.PUSH);
		fCodeButton.setImage(getImage(CODE_IMG, QualyzerActivator.PLUGIN_ID));
		fCodeButton.setEnabled(false);
		
		return parent;
	}
	
	/**
	 * A Generic listener that handles all the button actions for the format bar.
	 * @param action
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
					close(false);
					break;
				}
			}
		}
		else if(cType == ChangeType.MODIFY)
		{
			for(Transcript transcript : transcripts)
			{
				if(transcript.equals(getDocument()))
				{
					if(fAudioPlayer == null && transcript.getAudioFile() != null)
					{
						hookupNewAudioFile(transcript);
					}
					else if(fAudioPlayer != null && ((Transcript) getDocument()).getAudioFile().getPersistenceId() != 
						transcript.getAudioFile().getPersistenceId())
					{
						Transcript editorTranscript = (Transcript) getDocument();
						editorTranscript.setAudioFile(transcript.getAudioFile());
						if(transcript.getAudioFile() == null)
						{
							setEnable(fTopBar, false);
							fAudioPlayer = null;
							setLength(0);
						}
						else
						{
							IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
									editorTranscript.getProject().getFolderName());
							String audioFile = project.getLocation() + File.separator + 
								editorTranscript.getAudioFile().getRelativePath();
							fAudioPlayer.open(audioFile);
						}
						
					}
				}
			}
		}
	}

	/**
	 * @param transcript
	 */
	private void hookupNewAudioFile(Transcript transcript)
	{
		setEnable(fTopBar, true);
		Transcript editorTranscript = (Transcript) getDocument();
		editorTranscript.setAudioFile(transcript.getAudioFile());
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				editorTranscript.getProject().getFolderName());
		String audioFile = project.getLocation() + File.separator + 
			editorTranscript.getAudioFile().getRelativePath();
		fAudioPlayer = new AudioPlayer(audioFile, this);
	}
	
	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.editors.RTFEditor#dispose()
	 */
	@Override
	public void dispose()
	{
		Facade.getInstance().getListenerManager().unregisterTranscriptListener(getDocument().getProject(), this);
		if(fAudioPlayer != null)
		{
			fAudioPlayer.stop();
		}
		
		QualyzerActivator.getDefault().getPreferenceStore().removePropertyChangeListener(this);
		super.dispose();
	}
	
	/**
	 * Called by the AudioPlayer to set the length of the audio file.
	 * @param length
	 */
	protected void setLength(final double length)
	{
		fAudioLength = (int) length;
		fAudioSlider.setMaximum(fAudioLength);
		String label = "0:00/" + getTimeString(fAudioLength); //$NON-NLS-1$
		fTimeLabel.setText(label);
	}
	
	/**
	 * Converts seconds into MM:SS.
	 * @param seconds
	 * @return
	 */
	private String getTimeString(int seconds)
	{
		int minutes = seconds / SECONDS_PER_MINUTE;
		int secondsRemaining = seconds % SECONDS_PER_MINUTE;
		String secs = (secondsRemaining < TEN) ? "0"+secondsRemaining : ""+secondsRemaining; //$NON-NLS-1$ //$NON-NLS-2$
		return minutes + ":" + secs; //$NON-NLS-1$
	}
	
	/**
	 * Called by the AudioPlayer to set the current time of the audio stream.
	 * @param seconds
	 */
	protected void setSeconds(final int seconds)
	{
		Runnable run = new Runnable(){

			@Override
			public void run()
			{
				String label = getTimeString(seconds) +	"/" + getTimeString(fAudioLength); //$NON-NLS-1$
				fTimeLabel.setText(label);
				fAudioSlider.setSelection(seconds);
			}
		};
		
		Display.getDefault().asyncExec(run);
	}
	
	private void openErrorDialog(String errorMessage)
	{
		MessageDialog.openError(getSite().getShell(), Messages.getString(
				"editors.TranscriptEditor.audioError"), errorMessage); //$NON-NLS-1$
	}
	
	/**
	 * Allows access to the seek back command so that the key binding works.
	 */
	public void seekBack()
	{
		if(fAudioPlayer != null)
		{
			int selection = fAudioSlider.getSelection();
			selection -= fSeekTime + 2;
			fAudioPlayer.jumpToTime(selection >= 0 ? selection : 0);
		}
	}

	/**
	 * Allows access to the seek forward command so that the key binding works.
	 */
	public void seekForward()
	{
		if(fAudioPlayer != null)
		{
			int selection = fAudioSlider.getSelection();
			selection += fSeekTime;
			fAudioPlayer.jumpToTime(selection <= fAudioLength ? selection : fAudioLength);
		}
	}

	/**
	 * The selection adapter for the play button.
	 *
	 */
	private class PlayPushedAdapter extends SelectionAdapter
	{
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
					//Handle slight delay.
					fAudioPlayer.jumpToTime(fAudioSlider.getSelection() - 1); 
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
				openErrorDialog(ex.getMessage());
			}
		}
		
		public void stop()
		{
			fPlaying = false;
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sf.colorer.eclipse.editors.ColorerEditor#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
		if(e == null || e.getProperty().equals(IQualyzerPreferenceConstants.SEEK_TIME))
		{
			fSeekTime = QualyzerActivator.getDefault().getPreferenceStore().getInt(
					IQualyzerPreferenceConstants.SEEK_TIME);
		}
		else
		{
			super.propertyChange(e);
		}
	}

	/**
	 * 
	 */
	public void addTimeStamp()
	{
		IFile file = ((RTFEditorInput) getEditorInput()).getFile();
		IMarker marker = null;
		
		String pos = getCursorPosition();
		int line = Integer.parseInt(pos.split(" : ")[0]);
		
		try
		{
			marker = file.createMarker("ca.mcgill.cs.swevo.qualyzer.marker.timestamp");
			marker.setAttribute("time", fAudioSlider.getSelection());
			marker.setAttribute(IMarker.LINE_NUMBER, line);
			marker.setAttribute(IMarker.MESSAGE, getTimeString(fAudioSlider.getSelection()));
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.editors.RTFEditor#editorContextMenuAboutToShow(
	 * org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	protected void editorContextMenuAboutToShow(IMenuManager menu)
	{
		super.editorContextMenuAboutToShow(menu);
		
		if(fAudioPlayer != null)
		{
			addAction(menu, ITextEditorActionConstants.GROUP_EDIT, RTFConstants.ADD_TIMESTAMP_ACTION_ID);
		}
	}
	
	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.editors.RTFEditor#createActions()
	 */
	@Override
	protected void createActions()
	{
		super.createActions();
		
		createTimeStampAction();
		
		setAction(RTFConstants.ADD_TIMESTAMP_ACTION_ID, fAddTimeStampAction);
	}

}

