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
import org.apache.juddi.datatype.IdentifierBag;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.request.FindQualifiers;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.ConnectionManager;
import org.apache.juddi.util.jdbc.DynamicQuery;
import org.apache.juddi.util.jdbc.Transaction;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class FindTModelByIdentifierQuery
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(FindTModelByIdentifierQuery.class);

  static String selectSQL;
  static
  {
    // build selectSQL
    StringBuffer sql = new StringBuffer(200);
    sql.append("SELECT M.TMODEL_KEY,M.LAST_UPDATE ");
    sql.append("FROM TMODEL M,TMODEL_IDENTIFIER I ");
    selectSQL = sql.toString();
  }

  /**
   * Select ...
   *
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector select(IdentifierBag identifierBag,Vector keysIn,FindQualifiers qualifiers,Connection connection)
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
    appendWhere(sql,identifierBag,qualifiers);
    appendIn(sql,keysIn);
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
  private static void appendWhere(DynamicQuery sql,IdentifierBag identifierBag,FindQualifiers qualifiers)
  {
    sql.append("WHERE M.TMODEL_KEY = I.TMODEL_KEY ");
    if(identifierBag != null)
    {

      Vector keyedRefVector = identifierBag.getKeyedReferenceVector();
      if(keyedRefVector != null)
      {
        int vectorSize = keyedRefVector.size();
        if (vectorSize > 0)
        {
          sql.append("AND (");

          for (int i=0; i<vectorSize; i++)
          {
            // When determining whether a keyedReference matches 
            // a passed keyedReference, a match occurs if and only 
            // if 1) the tModelKeys refer to the same tModel and 2) 
            // the keyValues are identical. The keyNames are not 
            // significant. - UDDI Programmers API v2.04, Pg 25
            //
            KeyedReference keyedRef = (KeyedReference)keyedRefVector.elementAt(i);
            
            String key = keyedRef.getTModelKey();
            if (key == null)
              key = "";
            
            String value = keyedRef.getKeyValue();
            if (value == null)
              value = "";

            sql.append("(I.TMODEL_KEY_REF = ? AND I.KEY_VALUE = ?)");
            sql.addValue(key);
            sql.addValue(value);

            if (i+1 < vectorSize)
              sql.append(" OR ");
          }

          sql.append(") ");
        }
      }
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

    if (qualifiers == null)
      sql.append("M.LAST_UPDATE DESC");
    else if (qualifiers.sortByDateAsc)
      sql.append("M.LAST_UPDATE ASC");
    else
      sql.append("M.LAST_UPDATE DESC");
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
    IdentifierBag identifierBag = new IdentifierBag();
    Vector keyedRefVector = new Vector();
    keyedRefVector.addElement(new KeyedReference("af176f34-00ca-4d93-97f8-2b62aa3a75e5","blah, blah, blah"));
    keyedRefVector.addElement(new KeyedReference("54a827e0-1b51-4381-8775-ad02c377bb25","Haachachachacha"));
    keyedRefVector.addElement(new KeyedReference("xxxxxxxxxxxxxxxx","xxxxxxxxxxx"));
    identifierBag.setKeyedReferenceVector(keyedRefVector);

    Vector keysIn = new Vector();
    keysIn.add("824c73af-22e0-4816-9689-2429101c7727");
    keysIn.add("c311085b-3277-470d-8ce9-07b81c484e4b");
    keysIn.add("6b368a5a-6a62-4f23-a002-f11e22780a91");
    keysIn.add("45994713-d3c3-40d6-87b5-6ce51f36001c");
    keysIn.add("901b15c5-799c-4387-8337-a1a35fceb791");
    keysIn.add("80fdae14-0e5d-4ea6-8eb8-50fde422056d");
    keysIn.add("e1996c33-c436-4004-9e3e-14de191bcc6b");
    keysIn.add("f715d9ff-d4eb-4073-92e8-2411cd5d0d68");

    Transaction txn = new Transaction();

    if (connection != null)
    {
      try
      {
        // begin a new transaction
        txn.begin(connection);

        select(identifierBag,keysIn,null,connection);
        select(identifierBag,null,null,connection);

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
