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
package ca.mcgill.cs.swevo.qualyzer.util;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 * 
 */
public final class HibernateUtil
{
	private HibernateUtil()
	{

	}

	public static void closeSession(Session session)
	{
		if (session != null)
		{
			try
			{
				session.close();
			}
			catch (HibernateException e)
			{
				// logger.error("Error while closing a session.", e);
			}
		}
	}

	public static void rollbackTransaction(Transaction transaction)
	{
		if (transaction != null)
		{
			try
			{
				transaction.rollback();
			}
			catch (HibernateException e)
			{
				// logger.error("Error while rolling back a transaction.", e);
			}
		}
	}
}
