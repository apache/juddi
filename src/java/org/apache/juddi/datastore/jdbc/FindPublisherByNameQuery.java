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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.datatype.request.FindQualifiers;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.ConnectionManager;
import org.apache.juddi.util.jdbc.DynamicQuery;
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
    DynamicQuery sql = new DynamicQuery(selectSQL);
    appendWhere(sql,name,qualifiers);
    appendIn(sql,name,idsIn);
    appendOrderBy(sql,qualifiers);

    try
    {
      log.debug("select from PUBLISHER table:\n\n\t" + sql.toString() + "\n");

      statement = sql.buildPreparedStatement(connection);
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
  private static void appendWhere(DynamicQuery sql,String name,FindQualifiers qualifiers)
  {
    if ((name == null) || (name.length() == 0))
      return;

    if ((qualifiers != null) && (qualifiers.exactNameMatch))
    {
      sql.append("WHERE P.PUBLISHER_NAME = ? ");
      sql.addValue(name);
    }
    else
    {
      sql.append("WHERE P.PUBLISHER_NAME LIKE ? ");
      sql.addValue(name+"%");
    }
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
  private static void appendIn(DynamicQuery sql,String name,Vector keysIn)
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
      sql.append("?");
      sql.addValue(key);

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
  private static void appendOrderBy(DynamicQuery sql,FindQualifiers qualifiers)
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