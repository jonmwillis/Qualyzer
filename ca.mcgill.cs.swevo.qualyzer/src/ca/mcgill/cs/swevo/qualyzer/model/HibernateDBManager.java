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
package ca.mcgill.cs.swevo.qualyzer.model;

//import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

import ca.mcgill.cs.swevo.qualyzer.QualyzerException;

/**
 * 
 * A HibernateDBManager manages the connections with a single database.
 * 
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 * 
 */
public class HibernateDBManager
{
	private final SessionFactory fSessionFactory;

	// private final static Logger logger = Logger.getLogger(HibernateUtil.class);

	private final Configuration fConfiguration;

	/**
	 * Initializes a Hibernate Configuration instance and a SessionFactory instance.
	 * 
	 * @param connectionString
	 * @param userName
	 * @param password
	 * @param driver
	 * @param dialect
	 */
	public HibernateDBManager(String connectionString, String userName, String password, String driver, String dialect)
	{
		try
		{
			AnnotationConfiguration tempConfiguration = new AnnotationConfiguration().setProperty(
					"hibernate.connection.url", connectionString)
					.setProperty("hibernate.connection.username", userName).setProperty(
							"hibernate.connection.password", password).setProperty("hibernate.dialect", dialect)
					.setProperty("hibernate.connection.driver_class", driver);
			// .setProperty("hibernate.show_sql","true").setProperty("hibernate.format_sql",
			// "true");

			// Add classes
			tempConfiguration = tempConfiguration.addAnnotatedClass(Project.class);
			tempConfiguration = tempConfiguration.addAnnotatedClass(Code.class);
			// Configure
			tempConfiguration = tempConfiguration.configure();
			
			fConfiguration = tempConfiguration;

			fSessionFactory = fConfiguration.buildSessionFactory();
		}
		catch (HibernateException ex)
		{
			// logger.error("Error while initializing HibernateUtil", ex);
			throw new QualyzerException(ex);
		}
	}


	public Configuration getConfiguration()
	{
		return fConfiguration;
	}

	public SessionFactory getSessionFactory()
	{
		return fSessionFactory;
	}

	public Session openSession()
	{
		return fSessionFactory.openSession();
	}

	
}
