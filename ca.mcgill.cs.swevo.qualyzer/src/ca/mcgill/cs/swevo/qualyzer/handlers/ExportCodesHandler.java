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
package ca.mcgill.cs.swevo.qualyzer.handlers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import ca.mcgill.cs.swevo.qualyzer.QualyzerException;
import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.CodeEntry;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperCode;

/**
 * 
 *
 */
public class ExportCodesHandler extends AbstractHandler
{

	/**
	 * 
	 */
	private static final String CSV = ".csv";
	/**
	 * 
	 */
	private static final String COMMA = ",";

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		ISelection selection = page.getSelection();
		
		if(selection != null && selection instanceof IStructuredSelection)
		{
			IStructuredSelection structured = (IStructuredSelection) selection;
			Object element = structured.getFirstElement();
			
			if(element instanceof WrapperCode)
			{
				Project project = ((WrapperCode) element).getProject();
				Facade facade = Facade.getInstance();
				
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				
				FileDialog dialog = new FileDialog(shell, SWT.SAVE);
				dialog.setFilterExtensions(new String[]{CSV});
				String fileName = dialog.open();
				
				if(fileName !=  null)
				{
					int index = fileName.indexOf('.');
					if(index == -1 || !fileName.substring(index).equals(CSV))
					{
						fileName += CSV;
					}
					
					Map<String, List<String>> transcriptMap = new HashMap<String, List<String>>();
					Map<String, List<Integer>> freqMap = new HashMap<String, List<Integer>>();
					
					
					for(Code code : project.getCodes())
					{
						List<String> transcripts = new ArrayList<String>();
						List<Integer> frequencies = new ArrayList<Integer>();
						
						for(Transcript transcript : project.getTranscripts())
						{
							Transcript lTranscript = facade.forceTranscriptLoad(transcript);
							int freq = 0;
							
							for(Fragment fragment : lTranscript.getFragments())
							{
								for(CodeEntry entry : fragment.getCodeEntries())
								{
									if(entry.getCode().equals(code))
									{
										freq++;
										break;
									}
								}
							}
							
							if(freq > 0)
							{
								transcripts.add(transcript.getFileName());
								frequencies.add(freq);
							}
						}
						
						transcriptMap.put(code.getCodeName(), transcripts);
						freqMap.put(code.getCodeName(), frequencies);
					}
					
					StringBuffer buffer = new StringBuffer();
					
					for(String codeName : transcriptMap.keySet())
					{
						int totalFreq = sum(freqMap.get(codeName));
						buffer.append(codeName + COMMA + totalFreq + COMMA);
						List<String> transcripts = transcriptMap.get(codeName);
						List<Integer> frequencies = freqMap.get(codeName);
						
						for(int i = 0; i < transcripts.size(); i++)
						{
							String transcriptName = transcripts.get(i);
							Integer frequency = frequencies.get(i);
							buffer.append(transcriptName + COMMA + frequency + COMMA);
						}
						buffer.append("\n");
					}
					
					File file = new File(fileName);
					FileWriter writer = null;
					try
					{
						writer = new FileWriter(file);
						writer.write(buffer.toString());
					}
					catch (IOException e)
					{
						throw new QualyzerException("Unable to write the exported code file.");
					}
					finally
					{
						try
						{
							if(writer != null)
							{
								writer.close();
							}
						}
						catch (IOException e)
						{
							throw new QualyzerException("Problem closing the exported code file.");
						}
					}
				}
				
				
				
				
			}
		}
		return null;
	}

	/**
	 * @param list
	 * @return
	 */
	private int sum(List<Integer> list)
	{
		int sum = 0;
		
		for(Integer val : list)
		{
			sum += val;
		}
		
		return sum;
	}


}
