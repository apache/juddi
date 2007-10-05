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
import org.apache.juddi.datatype.business.Contact;
import org.apache.juddi.registry.RegistryEngine;
import org.apache.juddi.util.Config;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class ContactTable
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(ContactTable.class);

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
    sql.append("INSERT INTO ").append(tablePrefix).append("CONTACT (");
    sql.append("BUSINESS_KEY,");
    sql.append("CONTACT_ID,");
    sql.append("USE_TYPE,");
    sql.append("PERSON_NAME) ");
    sql.append("VALUES (?,?,?,?)");
    insertSQL = sql.toString();

    // build selectSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("USE_TYPE,");
    sql.append("PERSON_NAME, ");
    sql.append("CONTACT_ID ");
    sql.append("FROM ").append(tablePrefix).append("CONTACT ");
    sql.append("WHERE BUSINESS_KEY=? ");
    sql.append("ORDER BY CONTACT_ID");
    selectSQL = sql.toString();

    // build deleteSQL
    sql = new StringBuffer(100);
    sql.append("DELETE FROM ").append(tablePrefix).append("CONTACT ");
    sql.append("WHERE BUSINESS_KEY=?");
    deleteSQL = sql.toString();
  }

  /**
   * Insert new row into the CONTACT table.
   *
   * @param businessKey BusinessKey to the BusinessEntity object that owns the Contact to be inserted
   * @param contactList Vector of Contact objects holding values to be inserted
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void insert(
    String businessKey,
    Vector contactList,
    Connection connection)
    throws java.sql.SQLException
  {
    if ((contactList == null) || (contactList.size() == 0))
      return; // everything is valid but no elements to insert

    PreparedStatement statement = null;

    try
    {
      statement = connection.prepareStatement(insertSQL);
      statement.setString(1, businessKey.toString());

      int listSize = contactList.size();
      for (int contactID = 0; contactID < listSize; contactID++)
      {
        Contact contact = (Contact) contactList.elementAt(contactID);

        statement.setInt(2, contactID);
        statement.setString(3, contact.getUseType());
        statement.setString(4, contact.getPersonNameValue());

        if (log.isDebugEnabled()) {
            log.debug(
              "insert into " + tablePrefix + "CONTACT table:\n\n\t"
                + insertSQL
                + "\n\t BUSINESS_KEY="
                + businessKey.toString()
                + "\n\t CONTACT_ID="
                + contactID
                + "\n\t USE_TYPE="
                + contact.getUseType()
                + "\n\t PERSON_NAME="
                + contact.getPersonNameValue()
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
   * @param  businessKey BusinessKey
   * @param  connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector select(String businessKey, Connection connection)
    throws java.sql.SQLException
  {
    Vector contactList = new Vector();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      // create a statement to query with
      statement = connection.prepareStatement(selectSQL);
      statement.setString(1, businessKey.toString());

      if (log.isDebugEnabled()) {
          log.debug(
            "select from " + tablePrefix + "CONTACT table:\n\n\t"
              + selectSQL
              + "\n\t BUSINESS_KEY="
              + businessKey.toString()
              + "\n");
      }

      // execute the statement
      resultSet = statement.executeQuery();
      while (resultSet.next())
      {
        Contact contact = new Contact();
        contact.setUseType(resultSet.getString(1));//("USE_TYPE"));
        contact.setPersonNameValue(resultSet.getString(2));//("PERSON_NAME"));
        contactList.add(contact);
      }

      return contactList;
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
   * Delete multiple rows from the CONTACT table that are assigned to the
   * BusinessKey specified.
   *
   * @param  businessKey BusinessKey
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
            "delete from " + tablePrefix + "CONTACT table:\n\n\t"
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
