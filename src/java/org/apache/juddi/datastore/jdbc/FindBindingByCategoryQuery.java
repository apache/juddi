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
import org.apache.juddi.datatype.CategoryBag;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.request.FindQualifiers;
import org.apache.juddi.datatype.tmodel.TModel;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.ConnectionManager;
import org.apache.juddi.util.jdbc.DynamicQuery;
import org.apache.juddi.util.jdbc.Transaction;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class FindBindingByCategoryQuery
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(FindBindingByCategoryQuery.class);

  static String selectSQL;
  static
  {
    // build selectSQL
    StringBuffer sql = new StringBuffer(200);
    sql.append("SELECT B.BINDING_KEY,B.LAST_UPDATE ");
    sql.append("FROM BINDING_TEMPLATE B,BINDING_CATEGORY C ");
    selectSQL = sql.toString();
  }

  /**
   * Select ...
   *
   * @param serviceKey
   * @param categoryBag
   * @param keysIn
   * @param qualifiers
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector select(String serviceKey,CategoryBag categoryBag,Vector keysIn,FindQualifiers qualifiers,Connection connection)
    throws java.sql.SQLException
  {
    // If there is a keysIn vector but it doesn't contain
    // any keys then the previous query has exhausted
    // all possibilities of a match so skip this call.
    //
    if ((keysIn != null) && (keysIn.size() == 0))
      return keysIn;

    Vector keysOut = new Vector();
    PreparedStatement statement = null;
    ResultSet resultSet = null;
    DynamicQuery dynStmt = new DynamicQuery();
    
    DynamicQuery sql = new DynamicQuery(selectSQL);
    appendWhere(sql,serviceKey,categoryBag,qualifiers);
    appendIn(sql,keysIn);
    appendOrderBy(sql,qualifiers);
    
    try
    {
      log.debug(sql.toString());
      
      statement = sql.buildPreparedStatement(connection);
      resultSet = statement.executeQuery();      
      while (resultSet.next())
      {
        keysOut.addElement(resultSet.getString(1));//("SERVICE_KEY"));
      }

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
          "the Find BindingTemplate ResultSet: "+e.getMessage(),e);
      }

      try {
        statement.close();
      }
      catch (Exception e)
      {
        log.warn("An Exception was encountered while attempting to close " +
          "the Find BindingTemplate Statement: "+e.getMessage(),e);
      }
    }
  }

  /**
   *
   */
  private static void appendWhere(DynamicQuery sql,String serviceKey,CategoryBag categoryBag,FindQualifiers qualifiers)
  {
    sql.append("WHERE C.BINDING_KEY = B.BINDING_KEY ");
    if (serviceKey != null)
    {
      sql.append("AND B.SERVICE_KEY = ? ");
      sql.addValue(serviceKey);
    }
    
    if (categoryBag != null)
    {
      Vector keyedRefVector = categoryBag.getKeyedReferenceVector();
  
      if (keyedRefVector != null)
      {
        int vectorSize = keyedRefVector.size();
        if (vectorSize > 0)
        {
          sql.append("AND (");
  
          for (int i=0; i<vectorSize; i++)
          {
            KeyedReference keyedRef = (KeyedReference)keyedRefVector.elementAt(i);
            String key = keyedRef.getTModelKey();
            String name = keyedRef.getKeyName();
            String value = keyedRef.getKeyValue();
            
            // A null or zero-length tModelKey is treated as 
            // though the tModelKey for uddiorg:general_keywords 
            // had been specified.
            //
            if ((key == null) || (key.trim().length() == 0))
              key = TModel.GENERAL_KEYWORDS_TMODEL_KEY;
            
            if (key == null)
              key = "";
            
            if (value == null)
              value = "";
            
            // If the tModelKey involved is that of uddi-org:general_keywords, 
            // the keyNames are identical (DO NOT IGNORE keyName). Otherwise 
            // keyNames are not significant. Omitted keyNames are treated as 
            // identical to empty (zero length) keyNames.
            //
            if (key.equals(TModel.GENERAL_KEYWORDS_TMODEL_KEY)) 
            {
              sql.append("(C.TMODEL_KEY_REF = ? AND C.KEY_NAME = ? AND C.KEY_VALUE = ?)");
              sql.addValue(key);
              sql.addValue(name);
              sql.addValue(value);

              if (i+1 < vectorSize)
                sql.append(" OR ");
            }
            else 
            {
              sql.append("(C.TMODEL_KEY_REF = ? AND C.KEY_VALUE = ?)");
              sql.addValue(key);
              sql.addValue(value);

              if (i+1 < vectorSize)
                sql.append(" OR ");
            }
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

    sql.append("AND B.BINDING_KEY IN (");

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
      sql.append("B.LAST_UPDATE DESC");
    else if (qualifiers.sortByDateAsc)
      sql.append("B.LAST_UPDATE ASC");
    else
      sql.append("B.LAST_UPDATE DESC");
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
    String serviceKey = "5E2D4E60-9876-11D8-AE77-AC68422E7D92";

    CategoryBag categoryBag = new CategoryBag();
    Vector keyedRefVector = new Vector();
    keyedRefVector.addElement(new KeyedReference("uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4","ntis-gov:NAICS:1997","51121"));
    keyedRefVector.addElement(new KeyedReference("uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4","Mining","21"));
    keyedRefVector.addElement(new KeyedReference("uuid:DB77450D-9FA8-45D4-A7BC-04411D14E384",null,"abcdefg"));
    keyedRefVector.addElement(new KeyedReference("uuid:DB77450D-9FA8-45D4-A7BC-04411D14E384","1775f0f8-cd47-451d-88da-73ce508836f3","blah, blah, blah"));
    categoryBag.setKeyedReferenceVector(keyedRefVector);

    Vector keysIn = new Vector();
    keysIn.add("5E305BA0-9876-11D8-AE77-D080179B6DB4");
    keysIn.add("c311085b-3277-470d-8ce9-07b81c484e4b");
    keysIn.add("6b368a5a-6a62-4f23-a002-f11e22780a91");
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

        Vector keys = null;
        
        keys = select(serviceKey,categoryBag,keysIn,null,connection);
        System.out.println(keys.size());
        
        keys = select(serviceKey,categoryBag,null,null,connection);
        System.out.println(keys.size());

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
