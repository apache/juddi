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
import org.apache.juddi.datatype.Phone;
import org.apache.juddi.registry.RegistryEngine;
import org.apache.juddi.util.Config;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class PhoneTable
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(PhoneTable.class);

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
    sql.append("INSERT INTO ").append(tablePrefix).append("PHONE (");
    sql.append("BUSINESS_KEY,");
    sql.append("CONTACT_ID,");
    sql.append("PHONE_ID,");
    sql.append("USE_TYPE,");
    sql.append("PHONE_NUMBER) ");
    sql.append("VALUES (?,?,?,?,?)");
    insertSQL = sql.toString();

    // build selectSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("USE_TYPE,");
    sql.append("PHONE_NUMBER, ");
    sql.append("PHONE_ID ");
    sql.append("FROM ").append(tablePrefix).append("PHONE ");
    sql.append("WHERE BUSINESS_KEY=? ");
    sql.append("AND CONTACT_ID=? ");
    sql.append("ORDER BY PHONE_ID");
    selectSQL = sql.toString();

    // build deleteSQL
    sql = new StringBuffer(100);
    sql.append("DELETE FROM ").append(tablePrefix).append("PHONE ");
    sql.append("WHERE BUSINESS_KEY=?");
    deleteSQL = sql.toString();
  }

  /**
   * Insert new row into the PHONE table.
   *
   * @param businessKey String to the BusinessEntity object that owns the Contact to be inserted
   * @param contactID The unique ID generated when saving the parent Contact instance.
   * @param phoneList Vector of Phone objects holding values to be inserted
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void insert(
    String businessKey,
    int contactID,
    Vector phoneList,
    Connection connection)
    throws java.sql.SQLException
  {
    if ((phoneList == null) || (phoneList.size() == 0))
      return; // everything is valid but no elements to insert

    PreparedStatement statement = null;

    try
    {
      statement = connection.prepareStatement(insertSQL);
      statement.setString(1, businessKey.toString());
      statement.setInt(2, contactID);

      int listSize = phoneList.size();
      for (int phoneID = 0; phoneID < listSize; phoneID++)
      {
        Phone phone = (Phone) phoneList.elementAt(phoneID);

        statement.setInt(3, phoneID);
        statement.setString(4, phone.getUseType());
        statement.setString(5, phone.getValue());

        if (log.isDebugEnabled()) {
            log.debug(
              "insert into " + tablePrefix + "PHONE table:\n\n\t"
                + insertSQL
                + "\n\t BUSINESS_KEY="
                + businessKey.toString()
                + "\n\t CONTACT_ID="
                + contactID
                + "\n\t PHONE_ID="
                + phoneID
                + "\n\t USE_TYPE="
                + phone.getUseType()
                + "\n\t PHONE_NUMBER="
                + phone.getValue()
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
   * Select all rows from the CONTACT table for a given BusinessKey.
   *
   * @param  businessKey String
   * @param  contactID Unique ID representing the parent Contact instance
   * @param  connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector select(
    String businessKey,
    int contactID,
    Connection connection)
    throws java.sql.SQLException
  {
    Vector phoneList = new Vector();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      // create a statement to query with
      statement = connection.prepareStatement(selectSQL);
      statement.setString(1, businessKey.toString());
      statement.setInt(2, contactID);

      if (log.isDebugEnabled()) {
          log.debug(
            "select from " + tablePrefix + "PHONE table:\n\n\t"
              + selectSQL
              + "\n\t BUSINESS_KEY="
              + businessKey.toString()
              + "\n\t CONTACT_ID="
              + contactID
              + "\n");
      }

      // execute the statement
      resultSet = statement.executeQuery();
      while (resultSet.next())
      {
        Phone phone = new Phone();
        phone.setUseType(resultSet.getString(1));//("USE_TYPE"));
        phone.setValue(resultSet.getString(2));//("PHONE_NUMBER"));
        phoneList.add(phone);
      }

      return phoneList;
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
   * Delete multiple rows from the PHONE table that are assigned to the
   * BusinessKey specified.
   *
   * @param businessKey String
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void delete(String businessKey, Connection connection)
    throws java.sql.SQLException
  {
    PreparedStatement statement = null;

    try
    {
      // prepare the delete
      statement = connection.prepareStatement(deleteSQL);
      statement.setString(1, businessKey.toString());

      if (log.isDebugEnabled()) {
          log.debug(
            "delete from the " + tablePrefix + "PHONE table:\n\n\t"
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
