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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.datatype.request.FindQualifiers;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.ConnectionManager;
import org.apache.juddi.util.jdbc.Transaction;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class FindPublisherByNameQuery
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(FindPublisherByNameQuery.class);

  static String selectSQL;
  static
  {
    // build selectSQL
    StringBuffer sql = new StringBuffer(200);
    sql.append("SELECT P.PUBLISHER_ID,P.PUBLISHER_NAME ");
    sql.append("FROM PUBLISHER P ");
    selectSQL = sql.toString();
  }

  /**
   * Select ...
   *
   * @param name primary key value
   * @param keysIn primary key value
   * @param qualifiers primary key value
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector select(String name,Vector idsIn,FindQualifiers qualifiers,Connection connection)
    throws java.sql.SQLException
  {
    // if there is a idsIn vector but it doesn't contain
    // any publisher IDs then the previous query has exhausted
    // all possibilities of a match so skip this call.
    if ((idsIn != null) && (idsIn.size() == 0))
      return idsIn;

    Vector idsOut = new Vector();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    // construct the SQL statement
    StringBuffer sql = new StringBuffer(selectSQL);
    appendWhere(sql,name,qualifiers);
    appendIn(sql,name,idsIn);
    appendOrderBy(sql,qualifiers);

    try
    {
      log.debug("select from PUBLISHER table:\n\n\t" + sql.toString() + "\n");

      statement = connection.prepareStatement(sql.toString());
      resultSet = statement.executeQuery();

      while (resultSet.next())
        idsOut.addElement(resultSet.getString(1));//("PUBLISHER_ID"));

      return idsOut;
    }
    finally
    {
      try {
        resultSet.close();
      }
      catch (Exception e)
      {
        log.warn("An Exception was encountered while attempting to close " +
          "the Find Publisher ResultSet: "+e.getMessage(),e);
      }

      try {
        statement.close();
      }
      catch (Exception e)
      {
        log.warn("An Exception was encountered while attempting to close " +
          "the Find Publisher Statement: "+e.getMessage(),e);
      }
    }
  }

  /**
   *
   */
  private static void appendWhere(StringBuffer sql,String name,FindQualifiers qualifiers)
  {
    if ((name == null) || (name.length() == 0))
      return;

    if ((qualifiers != null) && (qualifiers.exactNameMatch))
      sql.append("WHERE P.PUBLISHER_NAME = '").append(name).append("' ");
    else
      sql.append("WHERE P.PUBLISHER_NAME LIKE '").append(name).append("%' ");
  }

  /**
   * Utility method used to construct SQL "IN" statements such as
   * the following SQL example:
   *
   *   SELECT * FROM TABLE WHERE MONTH IN ('jan','feb','mar')
   *
   * @param sql StringBuffer to append the final results to
   * @param keysIn Vector of Strings used to construct the "IN" clause
   */
  private static void appendIn(StringBuffer sql,String name,Vector keysIn)
  {
    if (keysIn == null)
      return;

    if ((name == null) || (name.length() == 0))
      sql.append("WHERE P.PUBLISHER_ID IN (");
    else
      sql.append("AND P.PUBLISHER_ID IN (");

    int keyCount = keysIn.size();
    for (int i=0; i<keyCount; i++)
    {
      String key = (String)keysIn.elementAt(i);
      sql.append("'").append(key).append("'");

      if ((i+1) < keyCount)
        sql.append(",");
    }

    sql.append(") ");
  }

  /**
   * if the qualifier parameter is null or if the
   * sortByNameDesc qualifier is true or if neither of
   * the sortByNameXxxx qualifiers are true then sort
   * the result by name in decending order.
   */
  private static void appendOrderBy(StringBuffer sql,FindQualifiers qualifiers)
  {
    sql.append("ORDER BY ");

    if ((qualifiers == null) ||
        (qualifiers.sortByNameDesc) ||
        ((!qualifiers.sortByNameAsc) && (!qualifiers.sortByNameDesc)))
    {
      sql.append("P.PUBLISHER_NAME DESC");
    }
    else if (qualifiers.sortByNameAsc)
    {
      sql.append("P.PUBLISHER_NAME ASC");
    }
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String[] args)
    throws Exception
  {
    // make sure we're using a DBCP DataSource and
    // not trying to use JNDI to aquire one.
    Config.setStringProperty("juddi.useConnectionPool","true");

    Connection conn = null;
    try {
      conn = ConnectionManager.aquireConnection();
      test(conn);
    }
    finally {
      if (conn != null)
        conn.close();
    }
  }

  public static void test(Connection connection)
    throws Exception
  {
    String name = new String("Steve");

    Vector idsIn = null;
    idsIn = new Vector();
    idsIn.add(new String("sviens"));
    idsIn.add(new String("jdoe"));
    idsIn.add(new String("steveviens"));

    Transaction txn = new Transaction();

    if (connection != null)
    {
      try
      {
        // begin a new transaction
        txn.begin(connection);

        Vector results = select(name,idsIn,null,connection);
        if (results != null)
        {
          for (int i=0; i<results.size(); i++)
            System.out.println(i+": "+(String)results.elementAt(i));
        }

        FindQualifiers fqs = new FindQualifiers();
        fqs.sortByNameAsc = true;

        Vector resutls2 = select(name,null,fqs,connection);
        if (results != null)
        {
          for (int i=0; i<resutls2.size(); i++)
            System.out.println(i+": "+(String)resutls2.elementAt(i));
        }

        fqs.exactNameMatch = true;

        Vector resutls3 = select(name,null,fqs,connection);
        if (results != null)
        {
          for (int i=0; i<resutls3.size(); i++)
            System.out.println(i+": "+(String)resutls3.elementAt(i));
        }

        // commit the transaction
        txn.commit();
      }
      catch(Exception ex)
      {
        try { txn.rollback(); }
        catch(java.sql.SQLException sqlex) { sqlex.printStackTrace(); }
        throw ex;
      }
    }
  }
}