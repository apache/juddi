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
import org.apache.juddi.datatype.Address;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class AddressTable
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(AddressTable.class);

  static String insertSQL = null;
  static String selectSQL = null;
  static String deleteSQL = null;

  static {
      // buffer used to build SQL statements
      StringBuffer sql = null;

      // build insertSQL
      sql = new StringBuffer(150);
      sql.append("INSERT INTO ADDRESS (");
      sql.append("BUSINESS_KEY,");
      sql.append("CONTACT_ID,");
      sql.append("ADDRESS_ID,");
      sql.append("USE_TYPE,");
      sql.append("SORT_CODE,");
      sql.append("TMODEL_KEY) ");
      sql.append("VALUES (?,?,?,?,?,?)");
      insertSQL = sql.toString();

      // build selectSQL
      sql = new StringBuffer(200);
      sql.append("SELECT ");
      sql.append("USE_TYPE,");
      sql.append("SORT_CODE,");
      sql.append("TMODEL_KEY, ");
      sql.append("ADDRESS_ID ");
      sql.append("FROM ADDRESS ");
      sql.append("WHERE BUSINESS_KEY=? ");
      sql.append("AND CONTACT_ID=? ");
      sql.append("ORDER BY ADDRESS_ID");
      selectSQL = sql.toString();

      // build deleteSQL
      sql = new StringBuffer(100);
      sql.append("DELETE FROM ADDRESS ");
      sql.append("WHERE BUSINESS_KEY=?");
      deleteSQL = sql.toString();
  }

  /**
   * Insert new row into the ADDRESS table.
   *
   * @param  businessKey String to the BusinessEntity object that owns the Contact to be inserted
   * @param  contactID The unique ID generated when saving the parent Contact instance.
   * @param  addrList Collection of Address objects to be inserted
   * @param  connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void insert(String businessKey,int contactID,Vector addrList,Connection connection)
    throws java.sql.SQLException
  {
    if ((addrList == null) || (addrList.size() == 0))
      return; // everything is valid but no elements to insert
  
    PreparedStatement statement = null;
  
    try
    {
      statement = connection.prepareStatement(insertSQL);
      statement.setString(1, businessKey.toString());
      statement.setInt(2, contactID);
  
      int listSize = addrList.size();
      for (int addressID = 0; addressID < listSize; addressID++)
      {
        Address address = (Address) addrList.elementAt(addressID);
        statement.setInt(3, addressID);
        statement.setString(4, address.getUseType());
        statement.setString(5, address.getSortCode());
        statement.setString(6, address.getTModelKey());
  
        log.debug(
          "insert into ADDRESS table:\n\n\t"
            + insertSQL
            + "\n\t BUSINESS_KEY="
            + businessKey.toString()
            + "\n\t CONTACT_ID="
            + contactID
            + "\n\t ADDRESS_ID="
            + addressID
            + "\n\t USE_TYPE="
            + address.getUseType()
            + "\n\t SORT_CODE="
            + address.getSortCode()
            + "\n\t TMODEL_KEY="
            + address.getTModelKey()
            + "\n");
  
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
   * @param  businessKey String
   * @param  contactID Unique ID representing the parent Contact instance
   * @param  connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector select(String businessKey,int contactID,Connection connection)
    throws java.sql.SQLException
  {
    Vector addrList = new Vector();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      // create a statement to query with
      statement = connection.prepareStatement(selectSQL);
      statement.setString(1, businessKey.toString());
      statement.setInt(2, contactID);

      log.debug(
        "select from ADDRESS table:\n\n\t"
          + selectSQL
          + "\n\t BUSINESS_KEY="
          + businessKey.toString()
          + "\n\t CONTACT_ID="
          + contactID
          + "\n");

      // execute the statement
      resultSet = statement.executeQuery();
      while (resultSet.next())
      {
        Address address = new Address();
        address.setUseType(resultSet.getString(1));//("USE_TYPE"));
        address.setSortCode(resultSet.getString(2));//("SORT_CODE"));
        address.setTModelKey(resultSet.getString(3));//("TMODEL_KEY"));
        addrList.add(address);
        address = null;
      }

      return addrList;
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
   * Delete multiple rows from the ADDRESS table that are assigned to the
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
      // prepare the delete
      statement = connection.prepareStatement(deleteSQL);
      statement.setString(1, businessKey.toString());

      log.debug(
        "delete from ADDRESS table:\n\n\t"
          + deleteSQL
          + "\n\t BUSINESS_KEY="
          + businessKey.toString()
          + "\n");

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