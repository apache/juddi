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
import org.apache.juddi.datatype.Description;
import org.apache.juddi.registry.RegistryEngine;
import org.apache.juddi.util.Config;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class InstanceDetailsDescTable
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(InstanceDetailsDescTable.class);

  static String insertSQL = null;
  static String selectSQL = null;
  static String deleteSQL = null;
  static String tablePrefix;
  
  static
  {
   tablePrefix = Config.getStringProperty(
       RegistryEngine.PROPNAME_TABLE_PREFIX,RegistryEngine.DEFAULT_TABLE_PREFIX);
    // buffer used to build SQL statements
    StringBuffer sql = null;

    // build insertSQL
    sql = new StringBuffer(150);
    sql.append("INSERT INTO ").append(tablePrefix).append("INSTANCE_DETAILS_DESCR (");
    sql.append("BINDING_KEY,");
    sql.append("TMODEL_INSTANCE_INFO_ID,");
    sql.append("INSTANCE_DETAILS_DESCR_ID,");
    sql.append("LANG_CODE,");
    sql.append("DESCR) ");
    sql.append("VALUES (?,?,?,?,?)");
    insertSQL = sql.toString();

    // build selectSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("LANG_CODE,");
    sql.append("DESCR, ");
    sql.append("INSTANCE_DETAILS_DESCR_ID ");
    sql.append("FROM ").append(tablePrefix).append("INSTANCE_DETAILS_DESCR ");
    sql.append("WHERE BINDING_KEY=? ");
    sql.append("AND TMODEL_INSTANCE_INFO_ID=? ");
    sql.append("ORDER BY INSTANCE_DETAILS_DESCR_ID");
    selectSQL = sql.toString();

    // build deleteSQL
    sql = new StringBuffer(100);
    sql.append("DELETE FROM ").append(tablePrefix).append("INSTANCE_DETAILS_DESCR ");
    sql.append("WHERE BINDING_KEY=?");
    deleteSQL = sql.toString();
  }

  /**
   * Insert new row into the INSTANCE_DETAILS_DESCR table.
   *
   * @param bindingKey String to the BindingTemplate object
   * @param tModelInstanceInfoID String to the BusinessEntity object that owns the Contact to be inserted
   * @param descList Vector of Description objects holding values to be inserted
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void insert(
    String bindingKey,
    int tModelInstanceInfoID,
    Vector descList,
    Connection connection)
    throws java.sql.SQLException
  {
    if ((descList == null) || (descList.size() == 0))
      return; // everything is valid but no elements to insert

    PreparedStatement statement = null;

    try
    {
      statement = connection.prepareStatement(insertSQL);
      statement.setString(1, bindingKey.toString());
      statement.setInt(2, tModelInstanceInfoID);

      int listSize = descList.size();
      for (int descID = 0; descID < listSize; descID++)
      {
        Description desc = (Description) descList.elementAt(descID);

        // okay, set the values to be inserted
        statement.setInt(3, descID); // Sequence Number aka Desc ID
        statement.setString(4, desc.getLanguageCode());
        statement.setString(5, desc.getValue());

        if (log.isDebugEnabled()) {
            log.debug(
              "insert into " + tablePrefix + "INSTANCE_DETAILS_DESCR table:\n\n\t"
                + insertSQL
                + "\n\t BINDING_KEY="
                + bindingKey.toString()
                + "\n\t TMODEL_INSTANCE_INFO_ID="
                + tModelInstanceInfoID
                + "\n\t INSTANCE_DETAILS_DESCR_ID="
                + descID
                + "\n\t LANG_CODE="
                + desc.getLanguageCode()
                + "\n\t DESCR="
                + desc.getValue()
                + "\n");
        }

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
   * Select all rows from the TMODEL_INST_INFO table for a given BusinessKey.
   *
   * @param  bindingKey String
   * @param  tModelInstanceInfoID ID (sequence number) of the parent TModelInstanceInfo object
   * @param  connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector select(
    String bindingKey,
    int tModelInstanceInfoID,
    Connection connection)
    throws java.sql.SQLException
  {
    Vector descList = new Vector();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      // create a statement to query with
      statement = connection.prepareStatement(selectSQL);
      statement.setString(1, bindingKey.toString());
      statement.setInt(2, tModelInstanceInfoID);

      if (log.isDebugEnabled()) {
          log.debug(
            "select from " + tablePrefix + "INSTANCE_DETAILS_DESCR table:\n\n\t"
              + selectSQL
              + "\n\t BINDING_KEY="
              + bindingKey.toString()
              + "\n\t TMODEL_INSTANCE_INFO_ID="
              + tModelInstanceInfoID
              + "\n");
      }

      // execute the statement
      resultSet = statement.executeQuery();
      while (resultSet.next())
      {
        Description desc = new Description();
        desc.setLanguageCode(resultSet.getString(1)); //("LANG_CODE"));
        desc.setValue(resultSet.getString(2)); //("DESCR"));
        descList.add(desc);
        desc = null;
      }

      return descList;
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
   * Delete multiple rows from the INSTANCE_DETAILS_DESCR table that are
   * assigned to the BindingKey specified.
   *
   * @param bindingKey String
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void delete(String bindingKey, Connection connection)
    throws java.sql.SQLException
  {
    PreparedStatement statement = null;

    try
    {
      // prepare
      statement = connection.prepareStatement(deleteSQL);
      statement.setString(1, bindingKey.toString());

      if (log.isDebugEnabled()) {
          log.debug(
            "delete from " + tablePrefix + "INSTANCE_DETAILS_DESCR table:\n\n\t"
              + deleteSQL
              + "\n\t BINDING_KEY="
              + bindingKey.toString()
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
