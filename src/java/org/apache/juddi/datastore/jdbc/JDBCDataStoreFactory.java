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
package org.apache.juddi.datastore.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.datastore.DataStore;
import org.apache.juddi.datastore.DataStoreFactory;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.ConnectionManager;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class JDBCDataStoreFactory extends DataStoreFactory
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(JDBCDataStoreFactory.class);

  /**
   *
   */
  public JDBCDataStoreFactory()
  {
    super();
  }

  /**
   *
   */
  public DataStore acquireDataStore()
  {
    log.info("aquire a JDBC connection from the pool " +
      "(useConnectionPool = "+Config.useConnectionPool()+")");

    // grab a JDBC connection
    Connection conn = null;

    try {
      conn = ConnectionManager.aquireConnection();
    }
    catch(SQLException sqlex) {
      log.error("Exception occured while attempting to aquire " +
        "a JDBC connection: "+sqlex.getMessage());
    }

    // create a JDBCDataStore with the connection.
    return new JDBCDataStore(conn);
  }

  /**
   *
   */
  public void releaseDataStore(DataStore datastore)
  {
    log.info("close a JDBC connection back into the the pool " +
      "(useConnectionPool = "+Config.useConnectionPool()+")");

    // pull the JDBC connection from of the DataStore.
    Connection conn = ((JDBCDataStore)datastore).getConnection();

    try {
      if (conn != null)
        conn.close();
    }
    catch(SQLException sqlex) {
      log.error("Exception occured while attempting to " +
        "close a JDBC connection: "+sqlex.getMessage());
    }
  }

  /**
   *
   */
  public void init()
  {
  }

  /**
   *
   */
  public void destroy()
  {
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String[] args)
  {
    // delecare work variables
    int connCount = 10;
    JDBCDataStoreFactory factory = new JDBCDataStoreFactory();
    JDBCDataStore[] stores = new JDBCDataStore[connCount];

    // aquire some connections
    for (int i=0; i<connCount; i++)
    {
      stores[i] = (JDBCDataStore)factory.acquireDataStore();
      if (stores[i] != null)
        System.out.println("Connection "+i+" aquired: "+stores[i].getConnection().toString());
      else
        System.out.println("Couldn't create connection "+i);
    }

    // release those connections
    for (int i=0; i<connCount; i++)
    {
      if (stores[i] != null)
      {
        factory.releaseDataStore(stores[i]);
        System.out.println("Connection "+i+" released: "+stores[i].getConnection().toString());
      }
      else
        System.out.println("Connection "+i+" was never successfully created.");
    }
  }
}