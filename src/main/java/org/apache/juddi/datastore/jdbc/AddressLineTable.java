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
import org.apache.juddi.datatype.AddressLine;
import org.apache.juddi.registry.RegistryEngine;
import org.apache.juddi.util.Config;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class AddressLineTable
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(AddressLineTable.class);

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
    sql.append("INSERT INTO ").append(tablePrefix).append("ADDRESS_LINE (");
    sql.append("BUSINESS_KEY,");
    sql.append("CONTACT_ID,");
    sql.append("ADDRESS_ID,");
    sql.append("ADDRESS_LINE_ID,");
    sql.append("LINE,");
    sql.append("KEY_NAME,");
    sql.append("KEY_VALUE) ");
    sql.append("VALUES (?,?,?,?,?,?,?)");
    insertSQL = sql.toString();

    // build selectSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("LINE,");
    sql.append("KEY_NAME,");
    sql.append("KEY_VALUE, ");
    sql.append("ADDRESS_LINE_ID ");
    sql.append("FROM ").append(tablePrefix).append("ADDRESS_LINE ");
    sql.append("WHERE BUSINESS_KEY=? ");
    sql.append("AND CONTACT_ID=? ");
    sql.append("AND ADDRESS_ID=? ");
    sql.append("ORDER BY ADDRESS_LINE_ID");
    selectSQL = sql.toString();

    // build deleteSQL
    sql = new StringBuffer(100);
    sql.append("DELETE FROM "+ tablePrefix + "ADDRESS_LINE ");
    sql.append("WHERE BUSINESS_KEY=?");
    deleteSQL = sql.toString();
  }

  /**
   * Insert new row into the ADDRESS_LINE table.
   *
   * @param businessKey BusinessKey to the BusinessEntity object that owns the Contact to be inserted
   * @param contactID The unique ID generated when saving the parent Contact instance.
   * @param addressID The unique ID generated when saving the parent Address instance.
   * @param lineList Enumeration of AddressLines objects holding values to be inserted.
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void insert(String businessKey,int contactID,int addressID,Vector lineList,Connection connection)
    throws java.sql.SQLException
  {
    if ((lineList == null) || (lineList.size() == 0))
      return; // everything is valid but no elements to insert

    PreparedStatement statement = null;

    try
    {
      statement = connection.prepareStatement(insertSQL);
      statement.setString(1,businessKey.toString());
      statement.setInt(2,contactID);
      statement.setInt(3,addressID);

      int listSize = lineList.size();
      for (int lineID=0; lineID<listSize; lineID++)
      {
        AddressLine line = (AddressLine)lineList.elementAt(lineID);
        statement.setInt(4,lineID);
        statement.setString(5,line.getLineValue());
        statement.setString(6,line.getKeyName());
        statement.setString(7,line.getKeyValue());

        if (log.isDebugEnabled()) {
            log.debug("insert into "+ tablePrefix + "ADDRESS_LINE table:\n\n\t" + insertSQL +
              "\n\t BUSINESS_KEY=" + businessKey.toString() +
              "\n\t CONTACT_ID=" + contactID +
              "\n\t ADDRESS_ID=" + addressID +
              "\n\t ADDRESS_LINE_ID=" + lineID +
              "\n\t LINE=" + line.getLineValue() +
              "\n\t KEY_NAME=" + line.getKeyName() +
              "\n\t KEY_VALUE=" + line.getKeyValue() + "\n");
        }

        statement.executeUpdate();
      }
    }
    finally
    {
      try {
        statement.close();
      }
      catch (Exception e) { /* ignored */ }
    }
  }

  /**
   * Select all rows from the CONTACT table for a given BusinessKey.
   *
   * @param  businessKey BusinessKey
   * @param  contactID Unique ID representing the parent Contact instance
   * @param  connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector select(String businessKey,int contactID,int addressID,Connection connection)
    throws java.sql.SQLException
  {
    Vector lineList = new Vector();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      // create a statement to query with
      statement = connection.prepareStatement(selectSQL);
      statement.setString(1,businessKey.toString());
      statement.setInt(2,contactID);
      statement.setInt(3,addressID);

      if (log.isDebugEnabled()) {
          log.debug("select from "+ tablePrefix + "ADDRESS_LINE table:\n\n\t" + selectSQL +
            "\n\t BUSINESS_KEY=" + businessKey.toString() +
            "\n\t CONTACT_KEY=" + contactID +
            "\n\t ADDRESS_ID=" + addressID + "\n");
      }

      // execute the statement
      resultSet = statement.executeQuery();
      while (resultSet.next())
      {
        AddressLine line = new AddressLine();
        line.setLineValue(resultSet.getString(1));//("LINE")
        line.setKeyName(resultSet.getString(2));//("KEY_NAME"));
        line.setKeyValue(resultSet.getString(3));//("KEY_VALUE"));
        lineList.add(line);
      }

      return lineList;
    }
    finally
    {
      try {
        resultSet.close();
        statement.close();
      }
      catch (Exception e) { /* ignored */ }
    }
  }

  /**
   * Delete multiple rows from the ADDRESS_LINE table that are assigned to the
   * BusinessKey specified.
   *
   * @param  businessKey String
   * @param  connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void delete(String businessKey,Connection connection)
    throws java.sql.SQLException
  {
    PreparedStatement statement = null;

    try
    {
      // prepare the delete
      statement = connection.prepareStatement(deleteSQL);
      statement.setString(1,businessKey.toString());

      if (log.isDebugEnabled()) {
          log.debug("delete from "+ tablePrefix + "ADDRESS_LINE table:\n\n\t" + deleteSQL +
            "\n\t BUSINESS_KEY=" + businessKey.toString() + "\n");
      }

      // execute
      statement.executeUpdate();
    }
    finally
    {
      try {
        statement.close();
      }
      catch (Exception e) { /* ignored */ }
    }
  }
}
