/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "jUDDI" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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