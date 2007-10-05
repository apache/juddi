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
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.registry.RegistryEngine;
import org.apache.juddi.util.Config;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class TModelIdentifierTable
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(TModelIdentifierTable.class);

  static String insertSQL = null;
  static String selectSQL = null;
  static String deleteSQL = null;
  static String tablePrefix = "";
  
  static
  {
   tablePrefix = Config.getStringProperty(
       RegistryEngine.PROPNAME_TABLE_PREFIX,RegistryEngine.DEFAULT_TABLE_PREFIX);
    // buffer used to build SQL statements
    StringBuffer sql = null;

    // build insertSQL
    sql = new StringBuffer(150);
    sql.append("INSERT INTO ").append(tablePrefix).append("TMODEL_IDENTIFIER (");
    sql.append("TMODEL_KEY,");
    sql.append("IDENTIFIER_ID,");
    sql.append("TMODEL_KEY_REF,");
    sql.append("KEY_NAME,");
    sql.append("KEY_VALUE) ");
    sql.append("VALUES (?,?,?,?,?)");
    insertSQL = sql.toString();

    // build selectSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("TMODEL_KEY_REF,");
    sql.append("KEY_NAME,");
    sql.append("KEY_VALUE, ");
    sql.append("IDENTIFIER_ID ");
    sql.append("FROM ").append(tablePrefix).append("TMODEL_IDENTIFIER ");
    sql.append("WHERE TMODEL_KEY=? ");
    sql.append("ORDER BY IDENTIFIER_ID");
    selectSQL = sql.toString();

    // build deleteSQL
    sql = new StringBuffer(100);
    sql.append("DELETE FROM ").append(tablePrefix).append("TMODEL_IDENTIFIER ");
    sql.append("WHERE TMODEL_KEY=?");
    deleteSQL = sql.toString();
  }

  /**
   * Insert new row into the TMODEL_IDENTIFIER table.
   *
   * @param tModelKey String to the parent TModelEntity object.
   * @param keyRefs A Vector of KeyedReference instances to insert.
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void insert(
    String tModelKey,
    Vector keyRefs,
    Connection connection)
    throws java.sql.SQLException
  {
    PreparedStatement statement = null;

    try
    {
      statement = connection.prepareStatement(insertSQL);
      statement.setString(1, tModelKey.toString());

      int listSize = keyRefs.size();
      for (int identifierID = 0; identifierID < listSize; identifierID++)
      {
        KeyedReference keyRef =
          (KeyedReference) keyRefs.elementAt(identifierID);

        // extract values to insert
        String tModelKeyValue = null;
        if (keyRef.getTModelKey() != null)
          tModelKeyValue = keyRef.getTModelKey().toString();

        // set the values
        statement.setInt(2, identifierID);
        statement.setString(3, tModelKeyValue);
        statement.setString(4, keyRef.getKeyName());
        statement.setString(5, keyRef.getKeyValue());

        if (log.isDebugEnabled()) {
            log.debug(
              "insert into " + tablePrefix + "TMODEL_IDENTIFIER table:\n\n\t"
                + insertSQL
                + "\n\t BUSINESS_KEY="
                + tModelKey.toString()
                + "\n\t IDENTIFIER_ID="
                + identifierID
                + "\n\t TMODEL_KEY_REF="
                + tModelKeyValue
                + "\n\t KEY_NAME="
                + keyRef.getKeyName()
                + "\n\t KEY_VALUE="
                + keyRef.getKeyValue()
                + "\n");
        }

        // insert!
        statement.executeUpdate();
      }
    }
    finally
    {
      try
      {
        statement.close();
      }
      catch (Exception e)
      { /* ignored */
      }
    }
  }

  /**
   * Select all rows from the TMODEL_IDENTIFIER table for a given TModelKey.
   *
   * @param  tModelKey String
   * @param  connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector select(String tModelKey, Connection connection)
    throws java.sql.SQLException
  {
    Vector keyRefList = new Vector();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      // create a statement to query with
      statement = connection.prepareStatement(selectSQL);
      statement.setString(1, tModelKey.toString());

      if (log.isDebugEnabled()) {
          log.debug(
            "select from " + tablePrefix + "TMODEL_IDENTIFIER table:\n\n\t"
              + selectSQL
              + "\n\t TMODEL_KEY="
              + tModelKey.toString()
              + "\n");
      }

      // execute the statement
      resultSet = statement.executeQuery();
      while (resultSet.next())
      {
        KeyedReference keyRef = new KeyedReference();
        keyRef.setTModelKey(resultSet.getString(1));//("TMODEL_KEY_REF"));
        keyRef.setKeyName(resultSet.getString(2));//("KEY_NAME"));
        keyRef.setKeyValue(resultSet.getString(3));//("KEY_VALUE"));
        keyRefList.add(keyRef);
      }

      return keyRefList;
    }
    finally
    {
      try
      {
        resultSet.close();
        statement.close();
      }
      catch (Exception e)
      { /* ignored */
      }
    }
  }

  /**
   * Delete multiple rows from the TMODEL_IDENTIFIER table that are assigned to the
   * TModelKey specified.
   *
   * @param  tModelKey String
   * @param  connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void delete(String tModelKey, Connection connection)
    throws java.sql.SQLException
  {
    PreparedStatement statement = null;

    try
    {
      // prepare
      statement = connection.prepareStatement(deleteSQL);
      statement.setString(1, tModelKey.toString());

      if (log.isDebugEnabled()) {
          log.debug(
            "delete from " + tablePrefix + "TMODEL_IDENTIFIER table:\n\n\t"
              + deleteSQL
              + "\n\t TMODEL_KEY="
              + tModelKey.toString()
              + "\n");
      }

      // execute
      statement.executeUpdate();
    }
    finally
    {
      try
      {
        statement.close();
      }
      catch (Exception e)
      { /* ignored */
      }
    }
  }
}
