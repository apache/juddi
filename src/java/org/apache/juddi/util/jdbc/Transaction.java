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
