/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.juddi.util.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
 
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

import org.apache.juddi.registry.RegistryEngine;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.Loader;


/**
 * @author Steve Viens (sviens@apache.org)
 */
public class ConnectionManager
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(ConnectionManager.class);

  // DBCP DataSource property keys and default values
  private static final String USE_CONNECTION_POOL_VALUE_KEY = "juddi.useConnectionPool";
  private static final boolean DEFAULT_USE_CONNECTION_POOL_VALUE = false;
  
  private static final String JDBC_DRIVER_KEY = "juddi.jdbcDriver";
  private static final String DEFAULT_JDBC_DRIVER = "com.mysql.jdbc.Driver";

  private static final String JDBC_URL_KEY = "juddi.jdbcURL";
  private static final String DEFAULT_JDBC_URL = "jdbc:mysql://localhost/juddi";

  private static final String JDBC_USERNAME_KEY = "juddi.jdbcUser";
  private static final String DEFAULT_JDBC_USERNAME = "juddi";

  private static final String JDBC_PASSWORD_KEY = "juddi.jdbcPassword";
  private static final String DEFAULT_JDBC_PASSWORD = "juddi";

  private static final String JDBC_MAX_ACTIVITY_KEY = "juddi.maxActive";
  private static final int DEFAULT_JDBC_MAX_ACTIVITY = 10;

  private static final String JDBC_MAX_IDLE_KEY = "juddi.maxIdle";
  private static final int DEFAULT_JDBC_MAX_IDLE = 5;

  // Shared jUDDI DataSource
  private static DataSource dataSource = null;

  /**
   *
   */
  public static Connection aquireConnection()
    throws SQLException
  {
    // Make sure we've got a DataSource first
    if (dataSource == null)
      dataSource = getDataSource();

    Connection conn = null;
    if (dataSource != null)
      conn = dataSource.getConnection();

    return conn;
  }

  /**
   *
   */
  private static synchronized DataSource getDataSource()
    throws SQLException
  {
    if (dataSource != null)
      return dataSource;

    boolean useInternalConnectionPool = 
      Config.getBooleanProperty(USE_CONNECTION_POOL_VALUE_KEY,
          DEFAULT_USE_CONNECTION_POOL_VALUE);
    
    if (useInternalConnectionPool)
    {
      if (dataSource == null)
        dataSource = createDataSource(); // use DBCP (jUDDI Managed Connection Pool)
    }
    else
    {
      if (dataSource == null)
        dataSource = lookupDataSource(); // use JNDI (Container Managed Connection Pool)
    }

    return dataSource;
  }

  /**
   *
   */
  private static DataSource createDataSource()
    throws SQLException
  {
    // make sure we still need to create a DataSource.
    if (dataSource != null)
      return dataSource;

    // grab the JDBC properties we'll need to setup the connection pool.
    String dbcpDriver = Config.getStringProperty(JDBC_DRIVER_KEY,DEFAULT_JDBC_DRIVER);
    String dbcpURL = Config.getStringProperty(JDBC_URL_KEY,DEFAULT_JDBC_URL);
    String dbcpUser = Config.getStringProperty(JDBC_USERNAME_KEY,DEFAULT_JDBC_USERNAME);
    String dbcpPassword = Config.getStringProperty(JDBC_PASSWORD_KEY,DEFAULT_JDBC_PASSWORD);
    int dbcpMaxActive = Config.getIntProperty(JDBC_MAX_ACTIVITY_KEY,DEFAULT_JDBC_MAX_ACTIVITY);
    int dbcpMaxIdle = Config.getIntProperty(JDBC_MAX_IDLE_KEY,DEFAULT_JDBC_MAX_IDLE);

    log.info("Using DBCP to create a JDBC DataSource using properties: \n" +
      "\tDriver:    " + dbcpDriver + "\n" +
      "\tURL:       " + dbcpURL + "\n" +
      "\tUser:      " + dbcpUser + "\n" +
      "\tPassword:  " + dbcpPassword + "\n" +
      "\tMaxActive: " + dbcpMaxActive + "\n" +
      "\tMaxIdle:   " + dbcpMaxIdle + "\n");

    // make sure the JDBC Driver is loaded
    try {
      Loader.getClassForName(dbcpDriver);
    }
    catch(ClassNotFoundException cnfex) {
      throw new SQLException("Could not locate JDBC Driver '" +
        dbcpDriver+"' in classpath: "+cnfex.getMessage());
    }

    // create an ObjectPool that serves as the actual
    // pool of connections. We'll use a GenericObjectPool
    // instance, although any ObjectPool implementation
    // will suffice.
    GenericObjectPool connPool = new GenericObjectPool(null);
    connPool.setMaxActive(10);
    connPool.setMaxIdle(10);

    // create a ConnectionFactory that the pool will use
    // to create Connections. We'll use the
    // DriverManagerConnectionFactory, using the connect
    // string passed in the command line arguments.
    ConnectionFactory connFact =
      new DriverManagerConnectionFactory(dbcpURL,dbcpUser,dbcpPassword);

    // create the PoolableConnectionFactory, which wraps
    // the "real" Connections created by the ConnectionFactory
    // with the classes that implement the pooling functionality.
    try {
      PoolableConnectionFactory pcf =
        new PoolableConnectionFactory(connFact,connPool,null,null,false,true);
    }
    catch (Exception ex) {
      throw new SQLException(ex.getMessage());
    }

    // create the PoolingDriver itself, passing in the
    // object pool we created.
    PoolingDataSource dataSource = new PoolingDataSource(connPool);

    return dataSource;
  }

  /**
   *
   */
  private static DataSource lookupDataSource()
    throws SQLException
  {
    // make sure we still need to lookup the DataSource
    if (dataSource != null)
      return dataSource;

    // look it up.
    try
    {
      String dataSourceName =
        Config.getStringProperty(RegistryEngine.PROPNAME_DATASOURCE_NAME,
            RegistryEngine.DEFAULT_DATASOURCE_NAME);

      log.info("Using JNDI to aquire a JDBC DataSource with " +
        "name: "+dataSourceName);

      InitialContext initCtx = new InitialContext();
      dataSource = (DataSource)initCtx.lookup(dataSourceName);
    }
    catch (NamingException nex) {
      log.error("Exception occurred while attempting to acquire " +
        "a JDBC DataSource from JNDI: "+nex.getMessage());
    }

    return dataSource;
  }
}
