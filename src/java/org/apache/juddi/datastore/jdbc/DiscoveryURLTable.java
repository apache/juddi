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
import org.apache.juddi.datatype.DiscoveryURL;
import org.apache.juddi.datatype.business.BusinessEntity;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.ConnectionManager;
import org.apache.juddi.util.jdbc.Transaction;
import org.apache.juddi.uuidgen.UUIDGen;
import org.apache.juddi.uuidgen.UUIDGenFactory;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class DiscoveryURLTable
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(DiscoveryURLTable.class);

  static String insertSQL = null;
  static String selectSQL = null;
  static String deleteSQL = null;

  static {
    // buffer used to build SQL statements
    StringBuffer sql = null;

    // build insertSQL
    sql = new StringBuffer(150);
    sql.append("INSERT INTO DISCOVERY_URL (");
    sql.append("BUSINESS_KEY,");
    sql.append("DISCOVERY_URL_ID,");
    sql.append("USE_TYPE,");
    sql.append("URL) ");
    sql.append("VALUES (?,?,?,?)");
    insertSQL = sql.toString();

    // build selectSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("USE_TYPE,");
    sql.append("URL, ");
    sql.append("DISCOVERY_URL_ID ");
    sql.append("FROM DISCOVERY_URL ");
    sql.append("WHERE BUSINESS_KEY=? ");
    sql.append("ORDER BY DISCOVERY_URL_ID");
    selectSQL = sql.toString();

    // build deleteSQL
    sql = new StringBuffer(100);
    sql.append("DELETE FROM DISCOVERY_URL ");
    sql.append("WHERE BUSINESS_KEY=?");
    deleteSQL = sql.toString();
  }

  /**
   * Insert new row into the DISCOVERY_URL table.
   *
   * @param  businessKey String to the BusinessEntity object that owns
   *  the Description to be inserted
   * @param urlList Vector of Description objects holding values to be inserted
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void insert(
    String businessKey,
    Vector urlList,
    Connection connection)
    throws java.sql.SQLException
  {
    if ((urlList == null) || (urlList.size() == 0))
      return; // everything is valid but no elements to insert

    PreparedStatement statement = null;

    try
    {
      statement = connection.prepareStatement(insertSQL);
      statement.setString(1, businessKey.toString());

      int listSize = urlList.size();
      for (int urlID = 0; urlID < listSize; urlID++)
      {
        DiscoveryURL url = (DiscoveryURL) urlList.elementAt(urlID);
        String urlValue = url.getValue();

        log.debug(
          "insert into DISCOVERY_URL table:\n\n\t"
            + insertSQL
            + "\n\t BUSINESS_KEY="
            + businessKey.toString()
            + "\n\t DISCOVERY_URL_ID="
            + urlID
            + "\n\t USE_TYPE="
            + url.getUseType()
            + "\n\t URL="
            + urlValue
            + "\n");

        statement.setInt(2, urlID);
        statement.setString(3, url.getUseType());
        statement.setString(4, urlValue);
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
   * Select all rows from the DISCOVERY_URL table for a given BusinessKey.
   *
   * @param  businessKey String
   * @param  connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector select(String businessKey, Connection connection)
    throws java.sql.SQLException
  {
    Vector urlList = new Vector();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      // create a statement to query with
      statement = connection.prepareStatement(selectSQL);
      statement.setString(1, businessKey.toString());

      log.debug(
        "select from DISCOVERY_URL table:\n\n\t"
          + selectSQL
          + "\n\t BUSINESS_KEY="
          + businessKey.toString()
          + "\n");

      // execute the statement
      resultSet = statement.executeQuery();
      while (resultSet.next())
      {
        DiscoveryURL url = new DiscoveryURL();
        url.setUseType(resultSet.getString(1));//("USE_TYPE"));
        url.setValue(resultSet.getString(2));//("URL"));
        urlList.add(url);
      }

      return urlList;
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
   * Delete multiple rows from the DISCOVERY_URL table that are assigned to the
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
        "delete from DISCOVERY_URL table:\n\n\t"
          + deleteSQL
          + "\n\t BUSINESS_KEY="
          + businessKey.toString()
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

  public static void test(Connection connection) throws Exception
  {
    Transaction txn = new Transaction();
    UUIDGen uuidgen = UUIDGenFactory.getUUIDGen();

    if (connection != null)
    {
      try
      {
        String businessKey = uuidgen.uuidgen();
        BusinessEntity business = new BusinessEntity();
        business.setBusinessKey(businessKey);
        business.setAuthorizedName("sviens");
        business.setOperator("WebServiceRegistry.com");

        Vector urlList = new Vector();
        urlList.add(
          new DiscoveryURL("businessEntity", "http://www.steveviens.com/abc"));
        urlList.add(
          new DiscoveryURL("businessEntity", "http://www.steveviens.com/def"));
        urlList.add(
          new DiscoveryURL(
            "businessEntityExt",
            "http://www.steveviens.com/ghi"));
        urlList.add(
          new DiscoveryURL(
            "businessEntityExt",
            "http://www.steveviens.com/jkl"));

        String authorizedUserID = "sviens";

        // begin a new transaction
        txn.begin(connection);

        // insert a new BusinessEntity
        BusinessEntityTable.insert(business, authorizedUserID, connection);

        // insert a Collection DiscoveryURL objects
        DiscoveryURLTable.insert(businessKey, urlList, connection);

        // select a Collection DiscoveryURL objects by BusinessKey
        urlList = DiscoveryURLTable.select(businessKey, connection);

        // delete a Collection DiscoveryURL objects by BusinessKey
        DiscoveryURLTable.delete(businessKey, connection);

        // re-select a Collection DiscoveryURL objects by BusinessKey
        urlList = DiscoveryURLTable.select(businessKey, connection);

        // commit the transaction
        txn.commit();
      }
      catch (Exception ex)
      {
        try
        {
          txn.rollback();
        }
        catch (java.sql.SQLException sqlex)
        {
          sqlex.printStackTrace();
        }
        throw ex;
      }
    }
  }
}
