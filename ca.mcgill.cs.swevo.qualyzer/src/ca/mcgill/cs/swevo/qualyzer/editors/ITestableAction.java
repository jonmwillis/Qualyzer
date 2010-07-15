/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Name - Initial Contribution
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.editors;

import org.eclipse.jface.action.IAction;

/**
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 *
 */
public interface ITestableAction extends IAction
{

	/**
	 *
	 * @return
	 */
	boolean isWindowsBlock();

	/**
	 *
	 * @param windowsBlock
	 */
	void setWindowsBlock(boolean windowsBlock);

	/**
	 * @return the tester
	 */
	IDialogTester getTester();

	/**
	 * @param tester the tester to set
	 */
	void setTester(IDialogTester tester);

}