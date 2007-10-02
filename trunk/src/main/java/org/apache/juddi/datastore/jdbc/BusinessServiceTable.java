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
import java.sql.Timestamp;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.datatype.service.BusinessService;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class BusinessServiceTable
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(BusinessServiceTable.class);

  static String insertSQL = null;
  static String deleteSQL = null;
  static String selectSQL = null;
  static String selectByBusinessKeySQL = null;
  static String deleteByBusinessKeySQL = null;
  static String verifyOwnershipSQL = null;

  static
  {
    // buffer used to build SQL statements
    StringBuffer sql = null;

    // build insertSQL
    sql = new StringBuffer(100);
    sql.append("INSERT INTO BUSINESS_SERVICE (");
    sql.append("BUSINESS_KEY,");
    sql.append("SERVICE_KEY,");
    sql.append("LAST_UPDATE) ");
    sql.append("VALUES (?,?,?)");
    insertSQL = sql.toString();

    // build deleteSQL
    sql = new StringBuffer(100);
    sql.append("DELETE FROM BUSINESS_SERVICE ");
    sql.append("WHERE SERVICE_KEY=?");
    deleteSQL = sql.toString();

    // build selectSQL
    sql = new StringBuffer(100);
    sql.append("SELECT ");
    sql.append("BUSINESS_KEY ");
    sql.append("FROM BUSINESS_SERVICE ");
    sql.append("WHERE SERVICE_KEY=?");
    selectSQL = sql.toString();

    // build selectByBusinessKeySQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("SERVICE_KEY ");
    sql.append("FROM BUSINESS_SERVICE ");
    sql.append("WHERE BUSINESS_KEY=?");
    selectByBusinessKeySQL = sql.toString();

    // build deleteByBusinessKeySQL
    sql = new StringBuffer(100);
    sql.append("DELETE FROM BUSINESS_SERVICE ");
    sql.append("WHERE BUSINESS_KEY=?");
    deleteByBusinessKeySQL = sql.toString();

    // build verifyOwnershipSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("* ");
    sql.append("FROM BUSINESS_ENTITY e, BUSINESS_SERVICE s ");
    sql.append("WHERE e.BUSINESS_KEY = s.BUSINESS_KEY ");
    sql.append("AND s.SERVICE_KEY=? ");
    sql.append("AND e.PUBLISHER_ID=?");
    verifyOwnershipSQL = sql.toString();
  }

  /**
   * Insert new row into the BUSINESS_ENTITIES table.
   *
   * @param service object holding values to be inserted
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void insert(BusinessService service,Connection connection)
    throws java.sql.SQLException
  {
    PreparedStatement statement = null;
    Timestamp timeStamp = new Timestamp(System.currentTimeMillis());

    try
    {
      statement = connection.prepareStatement(insertSQL);
      statement.setString(1,service.getBusinessKey().toString());
      statement.setString(2,service.getServiceKey().toString());
      statement.setTimestamp(3,timeStamp);

      log.debug("insert into BUSINESS_SERVICE table:\n\n\t" + insertSQL +
        "\n\t BUSINESS_KEY=" + service.getBusinessKey().toString() +
        "\n\t SERVICE_KEY=" + service.getServiceKey().toString() +
        "\n\t LAST_UPDATE=" + timeStamp.getTime() + "\n");

      statement.executeUpdate();
    }
    finally
    {
      try { statement.close(); } catch (Exception e) { /* ignored */ }
    }
  }

  /**
   * Delete row from the BUSINESS_SERVICE table.
   *
   * @param serviceKey Primary key value
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void delete(String serviceKey,Connection connection)
    throws java.sql.SQLException
  {
    PreparedStatement statement = null;

    try
    {
      // prepare
      statement = connection.prepareStatement(deleteSQL);
      statement.setString(1,serviceKey.toString());

      log.debug("delete from BUSINESS_SERVICE table:\n\n\t" + deleteSQL +
        "\n\t SERVICE_KEY=" + serviceKey.toString() + "\n");

      // execute
      statement.executeUpdate();
    }
    finally
    {
      try { statement.close(); } catch (Exception e) { /* ignored */ }
    }
  }

  /**
   * Select one row from the BUSINESS_SERVICE table.
   *
   * @param serviceKey primary key value
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static BusinessService select(String serviceKey,Connection connection)
    throws java.sql.SQLException
  {
    BusinessService service = null;
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      statement = connection.prepareStatement(selectSQL);
      statement.setString(1,serviceKey.toString());

      log.debug("select from BUSINESS_SERVICE table:\n\n\t" + selectSQL +
        "\n\t SERVICE_KEY=" + serviceKey.toString() + "\n");

      resultSet = statement.executeQuery();
      if (resultSet.next())
      {
        service = new BusinessService();
        service.setBusinessKey(resultSet.getString(1));//("BUSINESS_KEY"));
        service.setServiceKey(serviceKey);
      }

      return service;
    }
    finally
    {
      try { resultSet.close(); } catch (Exception e) { /* ignored */ }
      try { statement.close(); } catch (Exception e) { /* ignored */ }
    }
  }

  /**
   * Delete multiple rows from the BUSINESS_SERVICE table that are assigned to
   * the BusinessKey specified.
   *
   * @param businessKey BusinessKey
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void deleteByBusinessKey(String businessKey,Connection connection)
    throws java.sql.SQLException
  {
    PreparedStatement statement = null;

    try
    {
      // prepare the delete
      statement = connection.prepareStatement(deleteByBusinessKeySQL);
      statement.setString(1,businessKey.toString());

      log.debug("delet from the BUSINESS_SERVICE table:\n\n\t" + deleteByBusinessKeySQL +
        "\n\t BUSINESS_KEY=" + businessKey.toString() + "\n");

      // execute
      statement.executeUpdate();
    }
    finally
    {
      try { statement.close(); } catch (Exception e) { /* ignored */ }
    }
  }

  /**
   * Select all rows from the business_service table for
   * a given BusinessKey.
   *
   * @param businessKey BusinessKey
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector selectByBusinessKey(String businessKey,Connection connection)
    throws java.sql.SQLException
  {
    Vector serviceList = new Vector();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
     // create a statement to query with
      statement = connection.prepareStatement(selectByBusinessKeySQL);
      statement.setString(1,businessKey.toString());

      log.debug("select from BUSINESS_SERVICE table:\n\n\t" + selectByBusinessKeySQL +
        "\n\t BUSINESS_KEY=" + businessKey.toString() + "\n");

      // execute the statement
      resultSet = statement.executeQuery();
      while (resultSet.next())
      {
        BusinessService service = new BusinessService();
        service.setBusinessKey(businessKey);
        service.setServiceKey(resultSet.getString(1));//("SERVICE_KEY"));
        serviceList.add(service);
      }

      return serviceList;
    }
    finally
    {
      try { resultSet.close(); } catch (Exception e) { /* ignored */ }
      try { statement.close(); } catch (Exception e) { /* ignored */ }
    }
  }

  /**
   * Verify that 'authorizedName' has the authority to update or delete
   * BusinessService identified by the serviceKey parameter
   *
   * @param serviceKey
   * @param publisherID
   * @param connection
   * @throws java.sql.SQLException
   */
  public static boolean verifyOwnership(String serviceKey,String publisherID,Connection connection)
    throws java.sql.SQLException
  {
    if ((serviceKey == null) || (publisherID == null))
      return false;

    boolean authorized = false;
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      statement = connection.prepareStatement(verifyOwnershipSQL);
      statement.setString(1,serviceKey);
      statement.setString(2,publisherID);

      log.debug("checking ownership of BUSINESS_SERVICE:\n\n\t" + verifyOwnershipSQL +
        "\n\t SERVICE_KEY=" + serviceKey +
        "\n\t PUBLISHER_ID=" + publisherID + "\n");

      resultSet = statement.executeQuery();
      if (resultSet.next())
        authorized = true;

      return authorized;
    }
    finally
    {
      try { resultSet.close(); } catch (Exception e) { /* ignored */ }
      try { statement.close(); } catch (Exception e) { /* ignored */ }
    }
  }
}
