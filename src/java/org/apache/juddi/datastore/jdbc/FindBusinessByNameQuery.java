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
import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.request.FindQualifiers;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.ConnectionManager;
import org.apache.juddi.util.jdbc.DynamicQuery;
import org.apache.juddi.util.jdbc.Transaction;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class FindBusinessByNameQuery
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(FindBusinessByNameQuery.class);

  static String selectSQL;
  static
  {
    // build selectSQL
    StringBuffer sql = new StringBuffer(200);
    sql.append("SELECT B.BUSINESS_KEY,B.LAST_UPDATE,N.NAME ");
    sql.append("FROM BUSINESS_ENTITY B,BUSINESS_NAME N ");
    selectSQL = sql.toString();
  }

  /**
   * Select ...
   *
   * @param names
   * @param keysIn
   * @param qualifiers
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector select(Vector names,Vector keysIn,FindQualifiers qualifiers,Connection connection)
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
    appendWhere(sql,names,qualifiers);
    appendIn(sql,keysIn);
    appendOrderBy(sql,qualifiers);
    
    try
    {
      log.debug(sql.toString());
      
      statement = sql.buildPreparedStatement(connection);
      resultSet = statement.executeQuery();

      while (resultSet.next())
        keysOut.addElement(resultSet.getString(1));//("BUSINESS_KEY"));

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
          "the Find BusinessEntity ResultSet: "+e.getMessage(),e);
      }

      try {
        statement.close();
      }
      catch (Exception e)
      {
        log.warn("An Exception was encountered while attempting to close " +
          "the Find BusinessEntity Statement: "+e.getMessage(),e);
      }
    }
  }

  /**
   *
   */
  private static void appendWhere(DynamicQuery sql,Vector names,FindQualifiers qualifiers)
  {
    sql.append("WHERE B.BUSINESS_KEY = N.BUSINESS_KEY ");

    if (names != null)
    {
      int nameSize = names.size();
      if (nameSize > 0)
      {
        sql.append("AND (");

        for (int i=0; i<nameSize; i++)
        {
          Name name = (Name)names.elementAt(i);
          String text = name.getValue();
          String lang = name.getLanguageCode();

          if ((text != null) && (text.length() > 0))
          {
            if (qualifiers == null) // default
            {
              sql.append("(UPPER(NAME) LIKE ?");
              sql.addValue(text.endsWith("%") ? text.toUpperCase() : text.toUpperCase()+"%");
            }
            else if ((qualifiers.caseSensitiveMatch) && (qualifiers.exactNameMatch))
            {
              sql.append("(NAME = ?");
              sql.addValue(text);
            }
            else if ((!qualifiers.caseSensitiveMatch) && (qualifiers.exactNameMatch))
            {
              sql.append("(UPPER(NAME) = ?");
              sql.addValue(text.toUpperCase());
            }
            else if ((qualifiers.caseSensitiveMatch) && (!qualifiers.exactNameMatch))
            {
              sql.append("(NAME LIKE ?");
              sql.addValue(text.endsWith("%") ? text : text+"%");
            }
            else if ((!qualifiers.caseSensitiveMatch) && (!qualifiers.exactNameMatch))
            {
              sql.append("(UPPER(NAME) LIKE ?");
              sql.addValue(text.endsWith("%") ? text.toUpperCase() : text.toUpperCase()+"%");
            }

            if ((lang != null) && (lang.length() > 0))
            {
              sql.append(" AND LANG_CODE = ?");
              sql.addValue(lang);
            }
            
            sql.append(")");

            if (i+1 < nameSize)
              sql.append(" OR ");
          }
        }
      }

      sql.append(") ");
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
  private static void appendIn(DynamicQuery sql,Vector keysIn)
  {
    if (keysIn == null)
      return;

    sql.append("AND B.BUSINESS_KEY IN (");

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
      sql.append("N.NAME ASC,B.LAST_UPDATE DESC");
    }
    else if (qualifiers.sortByNameAsc || qualifiers.sortByNameDesc)
    {
      if (qualifiers.sortByDateAsc || qualifiers.sortByDateDesc) 
      {
        if (qualifiers.sortByNameAsc && qualifiers.sortByDateDesc)
          sql.append("N.NAME ASC,B.LAST_UPDATE DESC");
        else if (qualifiers.sortByNameAsc && qualifiers.sortByDateAsc)
          sql.append("N.NAME ASC,B.LAST_UPDATE ASC");
        else if (qualifiers.sortByNameDesc && qualifiers.sortByDateDesc)
          sql.append("N.NAME DESC,B.LAST_UPDATE DESC");
        else
          sql.append("N.NAME DESC,B.LAST_UPDATE ASC");
      } 
      else
      {
        if (qualifiers.sortByNameAsc)
          sql.append("N.NAME ASC,B.LAST_UPDATE DESC");
        else
          sql.append("N.NAME DESC,B.LAST_UPDATE DESC");
      }
    }
    else if (qualifiers.sortByDateAsc || qualifiers.sortByDateDesc)
    {
      if (qualifiers.sortByDateDesc)
        sql.append("B.LAST_UPDATE ASC,N.NAME ASC");
      else
        sql.append("B.LAST_UPDATE DESC,N.NAME ASC");
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
    Vector names = new Vector();
    names.add(new Name("St"));
    names.add(new Name("X"));
    names.add(new Name("Select","en"));
    names.add(new Name("Inflex","en"));

    Vector keysIn = null;
    keysIn = new Vector();
    keysIn.add("0e70128c-f7c6-4854-b292-d2f13b638acf");
    keysIn.add("b405450a-64f5-4f95-8131-450429d0ae8c");
    keysIn.add("3009f336-98c1-4193-a22f-fea73e79c909");
    keysIn.add("45994713-d3c3-40d6-87b5-6ce51f36001c");
    keysIn.add("901b15c5-799c-4387-8337-a1a35fceb791");
    keysIn.add("80fdae14-0e5d-4ea6-8eb8-50fde422056d");
    keysIn.add("e1996c33-c436-4004-9e3e-14de191bcc6b");
    keysIn.add("3ef4772f-e04b-46ed-8065-c5a4e167b5ba");

    Transaction txn = new Transaction();

    if (connection != null)
    {
      try
      {
        // begin a new transaction
        txn.begin(connection);

        select(names,keysIn,null,connection);

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
