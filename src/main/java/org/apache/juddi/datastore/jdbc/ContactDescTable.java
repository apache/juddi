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
class ContactDescTable
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(ContactDescTable.class);

  static String insertSQL = null;
  static String selectSQL = null;
  static String deleteSQL = null;
  static String tablePrefix = "";

  static {
    tablePrefix = Config.getStringProperty(
        RegistryEngine.PROPNAME_TABLE_PREFIX,RegistryEngine.DEFAULT_TABLE_PREFIX);
    // buffer used to build SQL statements
    StringBuffer sql = null;

    // build insertSQL
    sql = new StringBuffer(150);
    sql.append("INSERT INTO ").append(tablePrefix).append("CONTACT_DESCR (");
    sql.append("BUSINESS_KEY,");
    sql.append("CONTACT_ID,");
    sql.append("CONTACT_DESCR_ID,");
    sql.append("LANG_CODE,");
    sql.append("DESCR) ");
    sql.append("VALUES (?,?,?,?,?)");
    insertSQL = sql.toString();

    // build selectSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("LANG_CODE,");
    sql.append("DESCR, ");
    sql.append("CONTACT_DESCR_ID ");
    sql.append("FROM ").append(tablePrefix).append("CONTACT_DESCR ");
    sql.append("WHERE BUSINESS_KEY=? ");
    sql.append("AND CONTACT_ID=? ");
    sql.append("ORDER BY CONTACT_DESCR_ID");
    selectSQL = sql.toString();

    // build deleteSQL
    sql = new StringBuffer(100);
    sql.append("DELETE FROM ").append(tablePrefix).append("CONTACT_DESCR ");
    sql.append("WHERE BUSINESS_KEY=?");
    deleteSQL = sql.toString();
  }

  /**
   * Insert new row into the CONTACT_DESCR table.
   *
   * @param businessKey String to the BusinessEntity object that owns the Description to be inserted
   * @param contactID Unique ID of the parent Contact object of these Descriptions
   * @param descList Vector of Description objects holding values to be inserted
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void insert(
    String businessKey,
    int contactID,
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
      statement.setString(1, businessKey.toString());
      statement.setInt(2, contactID);

      int listSize = descList.size();
      for (int descID = 0; descID < listSize; descID++)
      {
        Description desc = (Description) descList.elementAt(descID);

        statement.setInt(3, descID);
        statement.setString(4, desc.getLanguageCode());
        statement.setString(5, desc.getValue());

        if (log.isDebugEnabled()) {
            log.debug(
              "insert into " + tablePrefix + "CONTACT_DESCR table:\n\n\t"
                + insertSQL
                + "\n\t BUSINESS_KEY="
                + businessKey.toString()
                + "\n\t CONTACT_ID="
                + contactID
                + "\n\t CONTACT_DESCR_ID="
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
   * Select all rows from the CONTACT_DESCR table for a given BusinessKey.
   *
   * @param  businessKey String to the BusinessEntity object that contains the Contact that owns the Descriptions to be selected
   * @param  contactID Unique ID of the parent Contact object whose Descriptions we're attempting to select from the database
   * @param  connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector select(
    String businessKey,
    int contactID,
    Connection connection)
    throws java.sql.SQLException
  {
    Vector descList = new Vector();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      // prepare
      statement = connection.prepareStatement(selectSQL);
      statement.setString(1, businessKey.toString());
      statement.setInt(2, contactID);

      if (log.isDebugEnabled()) {
          log.debug(
            "select from " + tablePrefix + "CONTACT_DESCR table:\n\n\t"
              + selectSQL
              + "\n\t BUSINESS_KEY="
              + businessKey.toString()
              + "\n\t CONTACT_KEY="
              + contactID
              + "\n");
      }

      // execute
      resultSet = statement.executeQuery();
      while (resultSet.next())
      {
        Description desc = new Description();
        desc.setLanguageCode(resultSet.getString(1));//("LANG_CODE"));
        desc.setValue(resultSet.getString(2));//("DESCR"));
        descList.add(desc);
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
   * Delete multiple rows from the CONTACT_DESCR table that are assigned to the
   * BusinessKey specified.
   *
   * @param  businessKey String
   * @param  connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void delete(String businessKey, Connection connection)
    throws java.sql.SQLException
  {
    PreparedStatement statement = null;

    try
    {
      // prepare
      statement = connection.prepareStatement(deleteSQL);
      statement.setString(1, businessKey.toString());
      if (log.isDebugEnabled()) {
          log.debug(
            "delet from " + tablePrefix + "CONTACT_DESCR table:\n\n\t"
              + deleteSQL
              + "\n\t BUSINESS_KEY="
              + businessKey.toString()
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
