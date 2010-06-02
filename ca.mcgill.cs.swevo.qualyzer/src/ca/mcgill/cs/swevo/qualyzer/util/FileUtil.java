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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public final class FileUtil
{
	private FileUtil(){}

	public static void copyFile(File input, File output) throws IOException
	{
		FileChannel in = null;
		FileChannel out = null;
		
		try
		{
			in = new FileInputStream(input).getChannel();
			out = new FileOutputStream(output).getChannel();
			in.transferTo(0, in.size(), out);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(in != null)
			{
				in.close();
			}
			if(out != null)
			{
				out.close();
			}
		}
	}
}
