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
import java.util.Vector;

/**
 * Transaction txn = new Transaction();
 * txn.begin(conn1);
 * txn.begin(conn2);
 * txn.begin(conn3);
 * txn.commit();
 * txn.rollback();
 *
 * @author  Graeme Riddell
 */
public class Transaction
{
  /**
   * Vector of all connections involved in this transaction
   */
  private Vector vect = null;

  /**
   * default constructor
   */
  public Transaction()
  {
    this.vect = new Vector();
  }

  /**
   * If the connection is known then do nothing. If the connection is
   * new then issue a SQL begin work and hold onto it for later. Actually the
   * begin work is implicit and autocommit drives whether a transaction is
   * progressed.
   */
  public void begin(Connection conn)
    throws SQLException
  {
    // make sure auto commit is disabled
    if (conn.getAutoCommit() == true)
      conn.setAutoCommit(false);

    // If this connection has already been begun then
    // just return to the caller. Nothing more to do.
    for (int i=0; i<vect.size(); i++)
    {
      if ((Connection)(vect.elementAt(i)) == conn)
        return;
    }

    // add new connection to the collection
    vect.add(conn);
  }

 /**
  * commit on all connections. This is not XA, but it could be one day.
  */
  public void commit()
    throws SQLException
  {
    // loop through all connections and commit them
    for (int i=0; i<vect.size(); i++)
    {
      Connection conn = (Connection)vect.elementAt(i);
      conn.commit();
    }

    // they're all committed, now let's discard them
    vect.removeAllElements();
  }

 /**
  * rollback on all connections. This is not XA, but it could be one day.
  */
  public void rollback()
    throws SQLException
  {
    // loop through all collections and roll them back
    for (int i=0; i<vect.size(); i++)
    {
      Connection conn = (Connection)vect.elementAt(i);
      conn.rollback();
    }

    // they're all rolled back, now let's discard them
    vect.removeAllElements();
  }
}
