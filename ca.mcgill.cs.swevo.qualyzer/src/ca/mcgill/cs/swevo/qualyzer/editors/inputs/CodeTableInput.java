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
package ca.mcgill.cs.swevo.qualyzer.editors.inputs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.CodeEntry;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;

/**
 * Input for the TableViewer in CodeEditorPage.
 */
public class CodeTableInput
{
	private Project fProject;
	private Map<String, Integer> fFreqs;
	private Map<String, Code> fCodes;
	
	/**
	 * 
	 * @param project
	 */
	public CodeTableInput(Project project)
	{
		fProject = project;
		fFreqs = new HashMap<String, Integer>();
		fCodes = new HashMap<String, Code>();
		
		for(Code code : fProject.getCodes())
		{
			fCodes.put(code.getCodeName(), code);
		}
		
		countFrequencies();	
	}

	private void countFrequencies()
	{
		for(Code code : fProject.getCodes())
		{
			fFreqs.put(code.getCodeName(), 0);
		}
		
		for(Transcript transcript : fProject.getTranscripts())
		{
			Transcript lTranscript = Facade.getInstance().forceTranscriptLoad(transcript);
			for(Fragment fragment : lTranscript.getFragments().values())
			{
				for(CodeEntry entry : fragment.getCodeEntries())
				{
					int count = fFreqs.get(entry.getCode().getCodeName());
					fFreqs.put(entry.getCode().getCodeName(), ++count);
				}
			}
		}
		
		for(Memo memo : fProject.getMemos())
		{
			Memo lMemo = Facade.getInstance().forceMemoLoad(memo);
			for(Fragment fragment : lMemo.getFragments().values())
			{
				for(CodeEntry entry : fragment.getCodeEntries())
				{
					int count = fFreqs.get(entry.getCode().getCodeName());
					fFreqs.put(entry.getCode().getCodeName(), ++count);
				}
			}
		}
	}
	
	/**
	 * Get the data represented by this input.
	 * @return
	 */
	public CodeTableRow[] getData()
	{
		ArrayList<CodeTableRow> data = new ArrayList<CodeTableRow>();
		
		for(String key : fFreqs.keySet())
		{
			data.add(new CodeTableRow(fCodes.get(key), fFreqs.get(key)));
		}
		
		return data.toArray(new CodeTableRow[0]);
	}
	
	/**
	 * 
	 *
	 */
	public class CodeTableRow
	{
		private Code fCode;
		private int fFreq;
		
		/**
		 * 
		 * @param name
		 * @param count
		 */
		public CodeTableRow(Code code, int count)
		{
			fCode = code;
			fFreq = count;
		}
		
		/**
		 * 
		 * @return
		 */
		public String getName()
		{
			return fCode.getCodeName();
		}
		
		/**
		 * 
		 * @return
		 */
		public int getFrequency()
		{
			return fFreq;
		}
		
		/**
		 * 
		 * @param name
		 */
		public void setName(String name)
		{
			fCode.setCodeName(name);
		}
		
		/**
		 * 
		 * @param desc
		 */
		public void setDescription(String desc)
		{
			fCode.setDescription(desc);
		}

		/**
		 * @return
		 */
		public String getDescription()
		{
			return fCode.getDescription();
		}
	}
	
}
