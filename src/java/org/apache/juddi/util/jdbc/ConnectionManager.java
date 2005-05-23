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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.registry.RegistryEngine;
import org.apache.juddi.util.Config;


/**
 * @author Steve Viens (sviens@apache.org)
 */
public class ConnectionManager
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(ConnectionManager.class);

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
      dataSource = lookupDataSource();

    Connection conn = null;
    if (dataSource != null)
      conn = dataSource.getConnection();

    return conn;
  }

  /**
   *
   */
  private static synchronized DataSource lookupDataSource()
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
