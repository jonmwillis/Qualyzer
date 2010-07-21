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

package ca.mcgill.cs.swevo.qualyzer.editors;

import java.io.File;
import java.util.Map;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

import org.eclipse.swt.widgets.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.mcgill.cs.swevo.qualyzer.QualyzerException;

/**
 *
 */
public class AudioPlayer
{
	
	private static final int MICROSECONDS = 1000000;
	
	private Logger fLogger;
	
	private BasicPlayer fPlayer;
//	private long fByteNumber;
	private long fMicroSecondsPos;
	private int fSecondsPos;
	private double fLength;
	
	//private Label fTimeLabel;
	
	private String fAudioFile;
	private boolean fIsMP3;
	private boolean fIsWAV;
	
	//WAV only
//	private long fJumpSizeMicSeconds;
	private long fMicSecondPosAfterSeek;
//	private double fMicSecondsPerByte;
	
	/**
	 * 
	 */
	public AudioPlayer(String audioFile, Label timeLabel)
	{
		fLogger = LoggerFactory.getLogger(AudioPlayer.class);
		
		fPlayer = new BasicPlayer();
		//fTimeLabel = timeLabel;
		fAudioFile = audioFile;
		
//		fByteNumber = 0;
		fMicroSecondsPos = 0;
		fSecondsPos = 0;
		fLength = 0;
		
		fIsMP3 = false;
		fIsWAV = false;
		
		fPlayer.addBasicPlayerListener(createBasicPlayerListener());
		
		File file = new File(fAudioFile);
		try
		{
			fPlayer.open(file);
		}
		catch(BasicPlayerException e)
		{
			fLogger.error("AudioPlayer: Could not open", e);
		}
	}
	
	/**
	 * @return
	 */
	private BasicPlayerListener createBasicPlayerListener()
	{
		return new BasicPlayerListener(){

			@SuppressWarnings("unchecked")
			@Override
			public void opened(Object arg0, Map arg1)
			{
				fLength = ((Integer) arg1.get("audio.length.frames")) / ((Float) arg1.get("audio.framerate.fps"));
				updateTimeLabel();
				
				if(arg1.get("audio.type").equals("WAVE"))
				{
					fIsWAV = true;
//					double lengthMicSec = fLength * MICROSECONDS;
//					fMicSecondsPerByte = lengthMicSec / (Integer)arg1.get("audio.length.bytes");
				}
				else if(arg1.get("audio.type").equals("MP3"))
				{
					fIsMP3 = true;
				}
			}

			@SuppressWarnings("unchecked")
			@Override
			public void progress(int arg0, long arg1, byte[] arg2, Map arg3)
			{
				if(fIsMP3)
				{
//					fByteNumber = (Long) arg3.get("mp3.position.byte");
					fMicroSecondsPos = Long.valueOf((Long) arg3.get("mp3.position.microseconds"));
				}
				else if(fIsWAV)
				{
					fMicroSecondsPos = fMicSecondPosAfterSeek + arg1;
//					fByteNumber = (long) (fMicroSecondsPos / fMicSecondsPerByte);
				}
				
				if(fSecondsPos != fMicroSecondsPos / MICROSECONDS)
				{
					fSecondsPos = (int) fMicroSecondsPos / MICROSECONDS;
					updateTimeLabel();
				}
			}

			@Override
			public void setController(BasicController arg0){}
			@Override
			public void stateUpdated(BasicPlayerEvent arg0){}			
		};
	}

	/**
	 * 
	 */
	protected void updateTimeLabel()
	{
		//TODO convert to mm:ss
		//fTimeLabel.setText(fSecondsPos + "/" + (int) fLength);
		
	}

	/**
	 * Handles the play button being pressed.
	 */
	public void play()
	{
		try
		{
			if(fPlayer.getStatus() == BasicPlayer.PAUSED)
			{
				fPlayer.resume();
			}
			else if(fPlayer.getStatus() == BasicPlayer.STOPPED || fPlayer.getStatus() == BasicPlayer.OPENED)
			{
				fPlayer.play();
			}
		}
		catch(BasicPlayerException e)
		{
			fLogger.error("AudioPlayer: Could not play audio", e);
			throw new QualyzerException("Unable to play audio", e);
		}
	}
	
	/**
	 * Handles the pause button being pressed.
	 */
	public void pause()
	{
		try
		{
			if(fPlayer.getStatus() == BasicPlayer.PLAYING)
			{
				fPlayer.pause();
			}
		}
		catch(BasicPlayerException e)
		{
			fLogger.error("AudioPlayer: Could not pause", e);
			throw new QualyzerException("Unable to pause audio", e);
		}
	}
	
	/**
	 * Handles the stop button being pressed.
	 */
	public void stop()
	{
		try
		{
			fPlayer.stop();
		}
		catch(BasicPlayerException e)
		{
			fLogger.error("AudioPlayer: Could not stop.", e);
			throw new QualyzerException("Unable to stop audio", e);
		}
	}
	
}