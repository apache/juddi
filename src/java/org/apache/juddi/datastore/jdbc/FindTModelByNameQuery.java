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
class FindTModelByNameQuery
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(FindTModelByNameQuery.class);

  static String selectSQL;
  static
  {
    // build selectSQL
    StringBuffer sql = new StringBuffer(200);
    sql.append("SELECT M.TMODEL_KEY,M.LAST_UPDATE,M.NAME,M.DELETED ");
    sql.append("FROM TMODEL M ");
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
  public static Vector select(String name,Vector keysIn,FindQualifiers qualifiers,Connection connection)
    throws java.sql.SQLException
  {
    // if there is a keysIn vector but it doesn't contain
    // any keys then the previous query has exhausted
    // all possibilities of a match so skip this call.
    if ((keysIn != null) && (keysIn.size() == 0))
      return keysIn;

    Vector keysOut = new Vector();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    // construct the SQL statement
    DynamicQuery sql = new DynamicQuery(selectSQL);
    appendWhere(sql,name,qualifiers);
    appendIn(sql,name,keysIn);
    appendOrderBy(sql,qualifiers);                  
     
    try
    {
      log.debug(sql.toString());

      statement = sql.buildPreparedStatement(connection);
      resultSet = statement.executeQuery();

      while (resultSet.next())
        keysOut.addElement(resultSet.getString(1));//("TMODEL_KEY"));

      return keysOut;
    }
    finally
    {
      try {
        resultSet.close();
      }
      catch (Exception e)
      {
        log.warn("An Exception was encountered while attempting to close " +
          "the Find TModel ResultSet: "+e.getMessage(),e);
      }

      try {
        statement.close();
      }
      catch (Exception e)
      {
        log.warn("An Exception was encountered while attempting to close " +
          "the Find TModel Statement: "+e.getMessage(),e);
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

    // When TModels are deleted they are only marked as 
    // deleted they're not actually removed from the database.
    //
    sql.append("WHERE M.DELETED IS NULL ");

    if ((qualifiers != null) && (qualifiers.exactNameMatch))
    {
      sql.append("AND M.NAME = ? ");
      sql.addValue(name);
    }
    else
    {
      sql.append("AND M.NAME LIKE ? ");
      sql.addValue(name.endsWith("%") ? name : name+"%");
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
      sql.append("WHERE M.TMODEL_KEY IN (");
    else
      sql.append("AND M.TMODEL_KEY IN (");

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
   *
   */
  private static void appendOrderBy(DynamicQuery sql,FindQualifiers qualifiers)
  {
    sql.append("ORDER BY ");

    if ((qualifiers == null) ||
       ((!qualifiers.sortByNameAsc) && (!qualifiers.sortByNameDesc) &&
        (!qualifiers.sortByDateAsc) && (!qualifiers.sortByDateDesc)))
    {
      sql.append("M.NAME ASC,M.LAST_UPDATE DESC");
    }
    else if (qualifiers.sortByNameAsc || qualifiers.sortByNameDesc)
    {
      if (qualifiers.sortByDateAsc || qualifiers.sortByDateDesc) 
      {
        if (qualifiers.sortByNameAsc && qualifiers.sortByDateDesc)
          sql.append("M.NAME ASC,M.LAST_UPDATE DESC");
        else if (qualifiers.sortByNameAsc && qualifiers.sortByDateAsc)
          sql.append("M.NAME ASC,M.LAST_UPDATE ASC");
        else if (qualifiers.sortByNameDesc && qualifiers.sortByDateDesc)
          sql.append("M.NAME DESC,M.LAST_UPDATE DESC");
        else
          sql.append("M.NAME DESC,M.LAST_UPDATE ASC");
      } 
      else
      {
        if (qualifiers.sortByNameAsc)
          sql.append("M.NAME ASC,M.LAST_UPDATE DESC");
        else
          sql.append("M.NAME DESC,M.LAST_UPDATE DESC");
      }
    }
    else if (qualifiers.sortByDateAsc || qualifiers.sortByDateDesc)
    {
      if (qualifiers.sortByDateDesc)
        sql.append("M.LAST_UPDATE ASC,M.NAME ASC");
      else
        sql.append("M.LAST_UPDATE DESC,M.NAME ASC");
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
    String name = new String("");

    Vector keysIn = new Vector();
		keysIn.add("uuid:327A56F0-3299-4461-BC23-5CD513E95C55");
		keysIn.add("uuid:4064C064-6D14-4F35-8953-9652106476A9");
		keysIn.add("uuid:4E49A8D6-D5A2-4FC2-93A0-0411D8D19E88");
		keysIn.add("uuid:807A2C6A-EE22-470D-ADC7-E0424A337C03");
		keysIn.add("uuid:8609C81E-EE1F-4D5A-B202-3EB13AD01823");
		keysIn.add("uuid:A035A07C-F362-44DD-8F95-E2B134BF43B4");
		keysIn.add("uuid:B1B1BAF5-2329-43E6-AE13-BA8E97195039");
		keysIn.add("uuid:C0B9FE13-179F-413D-8A5B-5004DB8E5BB2");
		keysIn.add("uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4");
		keysIn.add("uuid:CD153257-086A-4237-B336-6BDCBDCC6634");
		keysIn.add("uuid:DB77450D-9FA8-45D4-A7BC-04411D14E384");
		keysIn.add("uuid:E59AE320-77A5-11D5-B898-0004AC49CC1E");    
    
    Transaction txn = new Transaction();

    if (connection != null)
    {
      try
      {
        // begin a new transaction
        txn.begin(connection);

        select(name,keysIn,null,connection);				
				select(name,null,null,connection);

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
