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

/**
 * @author Steve Viens (sviens@apache.org)
 */
class BindingCategoryTable
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(BindingCategoryTable.class);

  static String insertSQL = null;
  static String selectSQL = null;
  static String deleteSQL = null;

  static {
    // buffer used to build SQL statements
    StringBuffer sql = null;

    // build insertSQL
    sql = new StringBuffer(150);
    sql.append("INSERT INTO BINDING_CATEGORY (");
    sql.append("BINDING_KEY,");
    sql.append("CATEGORY_ID,");
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
    sql.append("CATEGORY_ID ");
    sql.append("FROM BINDING_CATEGORY ");
    sql.append("WHERE BINDING_KEY=? ");
    sql.append("ORDER BY CATEGORY_ID");
    selectSQL = sql.toString();

    // build deleteSQL
    sql = new StringBuffer(100);
    sql.append("DELETE FROM BINDING_CATEGORY ");
    sql.append("WHERE BINDING_KEY=?");
    deleteSQL = sql.toString();
  }

  /**
   * Insert new row into the BINDING_CATEGORY table.
   *
   * @param bindingKey String to the parent BindingTemplate object.
   * @param keyRefs A Vector of KeyedReference instances to insert.
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void insert(
    String bindingKey,
    Vector keyRefs,
    Connection connection)
    throws java.sql.SQLException
  {
    PreparedStatement statement = null;

    try
    {
      statement = connection.prepareStatement(insertSQL);
      statement.setString(1, bindingKey.toString());

      int listSize = keyRefs.size();
      for (int categoryID = 0; categoryID < listSize; categoryID++)
      {
        KeyedReference keyRef = (KeyedReference) keyRefs.elementAt(categoryID);

        // extract values to insert
        String tModelKeyValue = null;
        if (keyRef.getTModelKey() != null)
          tModelKeyValue = keyRef.getTModelKey().toString();

        // set the values
        statement.setInt(2, categoryID);
        statement.setString(3, tModelKeyValue);
        statement.setString(4, keyRef.getKeyName());
        statement.setString(5, keyRef.getKeyValue());

        log.debug(
          "insert into BINDING_CATEGORY table:\n\n\t"
            + insertSQL
            + "\n\t BINDING_KEY="
            + bindingKey.toString()
            + "\n\t CATEGORY_ID="
            + categoryID
            + "\n\t TMODEL_KEY_REF="
            + tModelKeyValue
            + "\n\t KEY_NAME="
            + keyRef.getKeyName()
            + "\n\t KEY_VALUE="
            + keyRef.getKeyValue()
            + "\n");

        // insert
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
   * Select all rows from the BINDING_CATEGORY table for a given BindingKey.
   *
   * @param  bindingKey String
   * @param  connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector select(String bindingKey, Connection connection)
    throws java.sql.SQLException
  {
    Vector keyRefList = new Vector();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      // create a statement to query with
      statement = connection.prepareStatement(selectSQL);
      statement.setString(1, bindingKey.toString());

      log.debug(
        "select from BINDING_CATEGORY table:\n\n\t"
          + selectSQL
          + "\n\t BINDING_KEY="
          + bindingKey.toString()
          + "\n");

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
   * Delete multiple rows from the BINDING_CATEGORY table that are assigned to the
   * BindingKey specified.
   *
   * @param  bindingKey String
   * @param  connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void delete(String bindingKey, Connection connection)
    throws java.sql.SQLException
  {
    PreparedStatement statement = null;

    try
    {
      // prepare the delete
      statement = connection.prepareStatement(deleteSQL);
      statement.setString(1, bindingKey.toString());

      log.debug(
        "delete from BINDING_CATEGORY table:\n\n\t"
          + deleteSQL
          + "\n\t BINDING_KEY="
          + bindingKey.toString()
          + "\n");

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
