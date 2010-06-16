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
/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class FileUtilTest
{
	/**
	 * 
	 */
	private static final int NUM = 1000;
	private File fIn;
	private File fOut;
	
	/**
	 * Setup the tests.
	 * @throws IOException 
	 */
	@Before
	public void setUp() throws IOException
	{
		fIn = new File("in.txt");
		fOut = new File("out.txt");
		
		FileWriter writer = null;
		try
		{
			writer = new FileWriter(fIn);
			for(int i = 0; i < NUM; i++)
			{
				writer.write('0');
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			if(writer != null)
			{
				writer.close();
			}
		}
	}
	
	/**
	 * Tests the file copy utility.
	 */
	@Test
	public void copyFileTest()
	{
		boolean caught = false;
		try
		{
			FileUtil.copyFile(fIn, fOut);
		}
		catch (IOException e)
		{
			caught = true;
		}
		
		assertFalse(caught);
		
		try
		{
			FileReader reader1 = new FileReader(fIn);
			FileReader reader2 = new FileReader(fOut);
			
			int c;
			while((c = reader1.read()) != -1)
			{
				int c2 = reader2.read();
				
				assertEquals(c, c2);
			}
			
			assertEquals(reader2.read(), -1);
		}
		catch(IOException e)
		{
			assertFalse(true);
		}
	}
	
	/**
	 * Try copying when the second file already exists.
	 */
	@Test
	public void fileExistsTest()
	{
		boolean caught = false;
		try
		{
			FileUtil.copyFile(fIn, fOut);
		}
		catch (IOException e)
		{
			caught = true;
		}
		
		assertFalse(caught);
		
		try
		{
			FileReader reader1 = new FileReader(fIn);
			FileReader reader2 = new FileReader(fOut);
			
			int c;
			while((c = reader1.read()) != -1)
			{
				int c2 = reader2.read();
				
				assertEquals(c, c2);
			}
			
			assertEquals(reader2.read(), -1);
		}
		catch(IOException e)
		{
			assertFalse(true);
		}
	}
	
}
