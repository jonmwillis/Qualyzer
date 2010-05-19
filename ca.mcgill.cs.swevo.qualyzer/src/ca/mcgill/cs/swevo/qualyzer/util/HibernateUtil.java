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

import ca.mcgill.cs.swevo.qualyzer.model.HibernateDBManager;

/**
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 * 
 */
public final class HibernateUtil
{
	private HibernateUtil()
	{

	}

	public static void quietClose(Session session)
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

	public static void quietRollback(Transaction transaction)
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

	public static void quietSave(HibernateDBManager manager, Object object)
	{
		Transaction t = null;
		Session session = null;
		try
		{
			session = manager.openSession();
			t = session.beginTransaction();
			session.saveOrUpdate(object);
			t.commit();
		}
		catch (HibernateException e)
		{
			quietRollback(t);
		}
		finally
		{
			quietClose(session);
		}
	}

	public static void quietSave(HibernateDBManager manager, Object[] objects)
	{
		Transaction t = null;
		Session session = null;
		try
		{
			session = manager.openSession();
			t = session.beginTransaction();
			for (Object object : objects)
			{
				session.saveOrUpdate(object);
			}
			t.commit();
		}
		catch (HibernateException e)
		{
			quietRollback(t);
		}
		finally
		{
			quietClose(session);
		}
	}

}
