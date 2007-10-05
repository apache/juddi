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
import org.apache.juddi.datatype.business.BusinessEntity;
import org.apache.juddi.registry.RegistryEngine;
import org.apache.juddi.util.Config;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class BusinessEntityTable
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(BusinessEntityTable.class);

  static String insertSQL = null;
  static String deleteSQL = null;
  static String selectSQL = null;
  static String selectByPublisherSQL = null;
  static String verifyOwnershipSQL = null;
  static String selectPublisherSQL = null;
  static String tablePrefix = "";

  static
  {
    tablePrefix = Config.getStringProperty(
        RegistryEngine.PROPNAME_TABLE_PREFIX,RegistryEngine.DEFAULT_TABLE_PREFIX);
    // buffer used to build SQL statements
    StringBuffer sql = null;

    // build insertSQL
    sql = new StringBuffer(150);
    sql.append("INSERT INTO ").append(tablePrefix).append("BUSINESS_ENTITY (");
    sql.append("BUSINESS_KEY,");
    sql.append("AUTHORIZED_NAME,");
    sql.append("PUBLISHER_ID,");
    sql.append("OPERATOR,");
    sql.append("LAST_UPDATE) ");
    sql.append("VALUES (?,?,?,?,?)");
    insertSQL = sql.toString();

    // build deleteSQL
    sql = new StringBuffer(100);
    sql.append("DELETE FROM ").append(tablePrefix).append("BUSINESS_ENTITY ");
    sql.append("WHERE BUSINESS_KEY=?");
    deleteSQL = sql.toString();

    // build selectSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("AUTHORIZED_NAME,");
    sql.append("OPERATOR ");
    sql.append("FROM ").append(tablePrefix).append("BUSINESS_ENTITY ");
    sql.append("WHERE BUSINESS_KEY=?");
    selectSQL = sql.toString();

    // build selectByPublisherIDSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("BUSINESS_KEY ");
    sql.append("FROM ").append(tablePrefix).append("BUSINESS_ENTITY ");
    sql.append("WHERE PUBLISHER_ID=?");
    selectByPublisherSQL = sql.toString();

    // build verifyOwnershipSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("* ");
    sql.append("FROM ").append(tablePrefix).append("BUSINESS_ENTITY ");
    sql.append("WHERE BUSINESS_KEY=? ");
    sql.append("AND PUBLISHER_ID=?");
    verifyOwnershipSQL = sql.toString();

    // build selectPublisherSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("PUBLISHER_ID ");
    sql.append("FROM ").append(tablePrefix).append("BUSINESS_ENTITY ");
    sql.append("WHERE BUSINESS_KEY=?");
    selectPublisherSQL = sql.toString();
  }

  /**
   * Insert new row into the BUSINESS_ENTITY table.
   *
   * @param business object holding values to be inserted
   * @param publisherID
   * @param connection connection
   * @throws java.sql.SQLException
   */
  public static void insert(BusinessEntity business,String publisherID,Connection connection)
    throws java.sql.SQLException
  {
    PreparedStatement statement = null;
    Timestamp timeStamp = new Timestamp(System.currentTimeMillis());

    try
    {
      statement = connection.prepareStatement(insertSQL);
      statement.setString(1,business.getBusinessKey());
      statement.setString(2,business.getAuthorizedName());
      statement.setString(3,publisherID);
      statement.setString(4,business.getOperator());
      statement.setTimestamp(5,timeStamp);

      if (log.isDebugEnabled()) {
          log.debug("insert into " + tablePrefix + "BUSINESS_ENTITY table:\n\n\t" + insertSQL +
            "\n\t BUSINESS_KEY=" + business.getBusinessKey() +
            "\n\t AUTHORIZED_NAME=" + business.getAuthorizedName() +
            "\n\t PUBLISHER_ID=" + publisherID +
            "\n\t OPERATOR=" + business.getOperator() +
            "\n\t LAST_UPDATE=" + timeStamp.getTime() + "\n");
      }

      // execute
      statement.executeUpdate();
    }
    finally
    {
      try {
        statement.close();
      }
      catch (Exception e)
      {
        log.warn("An Exception was encountered while attempting to close " +
          "the Insert BusinessEntity PreparedStatement: "+e.getMessage(),e);
      }
    }
  }

  /**
   * Delete row from the BUSINESS_ENTITY table.
   *
   * @param businessKey key value
   * @param  connection JDBC Connection
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
          log.debug("delete from " + tablePrefix + "BUSINESS_ENTITY table:\n\n\t" + deleteSQL +
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
      catch (Exception e)
      {
        log.warn("An Exception was encountered while attempting to close " +
          "the Delete BusinessEntity PreparedStatement: "+e.getMessage(),e);
      }
    }
  }

  /**
   * Select one row from the BUSINESS_ENTITY table.
   *
   * @param businessKey key value
   * @param  connection JDBC Connection
   * @throws java.sql.SQLException
   */
  public static BusinessEntity select(String businessKey,Connection connection)
    throws java.sql.SQLException
  {
    BusinessEntity business = null;
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      statement = connection.prepareStatement(selectSQL);
      statement.setString(1,businessKey.toString());

      if (log.isDebugEnabled()) {
          log.debug("select from " + tablePrefix + "BUSINESS_ENTITY table:\n\n\t" + selectSQL +
            "\n\t BUSINESS_KEY=" + businessKey.toString() + "\n");
      }

      resultSet = statement.executeQuery();
      if (resultSet.next())
      {
        business = new BusinessEntity();
        business.setBusinessKey(businessKey);
        business.setAuthorizedName(resultSet.getString(1));//("AUTHORIZED_NAME"));
        business.setOperator(resultSet.getString(2));//("OPERATOR"));
      }

      return business;
    }
    finally
    {
      try {
        resultSet.close();
        statement.close();
      }
      catch (Exception e)
      {
        log.warn("An Exception was encountered while attempting to close " +
          "the Select BusinessEntity ResultSet and PreparedStatement: "+e.getMessage(),e);
      }
    }
  }

  /**
   * Select all BusinessKeys from the business_entities table for a given
   * 'publisherID' value.
   *
   * @param publisherID The user id of the BusinessEntity owner.
   * @param connection JDBC connection
   * @return Vector A Vector of BusinessKeys
   * @throws java.sql.SQLException
   */
  public static Vector selectByPublisherID(String publisherID,Connection connection)
    throws java.sql.SQLException
  {
    Vector keyList = new Vector();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      // create a statement to query with
      statement = connection.prepareStatement(selectByPublisherSQL);
      statement.setString(1,publisherID);

      if (log.isDebugEnabled()) {
          log.debug("select from " + tablePrefix + "BUSINESS_ENTITY table:\n\n\t" + selectByPublisherSQL +
            "\n\t PUBLISHER_ID=" + publisherID + "\n");
      }

      // execute the statement
      resultSet = statement.executeQuery();
      while (resultSet.next())
        keyList.add(resultSet.getString(1));//("BUSINESS_KEY"));

      return keyList;
    }
    finally
    {
      try {
        resultSet.close();
        statement.close();
      }
      catch (Exception e)
      {
        log.warn("An Exception was encountered while attempting to close " +
          "the Select BusinessEntity ResultSet and PreparedStatement: "+e.getMessage(),e);
      }
    }
  }

  /**
   * Verify that 'publisherID' has the authority to update or delete
   * BusinessEntity identified by the businessKey parameter
   *
   * @param  businessKey
   * @param  publisherID
   * @param  connection
   * @throws java.sql.SQLException
   */
  public static boolean verifyOwnership(String businessKey,String publisherID,Connection connection)
    throws java.sql.SQLException
  {
    if ((businessKey == null) || (publisherID == null))
      return false;

    boolean authorized = false;
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      statement = connection.prepareStatement(verifyOwnershipSQL);
      statement.setString(1,businessKey);
      statement.setString(2,publisherID);

      if (log.isDebugEnabled()) {
          log.debug("checking ownership of " + tablePrefix + "BUSINESS_ENTITY:\n\n\t" + verifyOwnershipSQL +
            "\n\t BUSINESS_KEY=" + businessKey +
            "\n\t PUBLISHER_ID=" + publisherID + "\n");
      }

      resultSet = statement.executeQuery();
      if (resultSet.next())
        authorized = true;

      return authorized;
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
   * Return the 'publisherID' for the BusinessEntity identified by the
   * businessKey parameter. Retusn null if the business entity key does
   * not represent a valid BuisnessEntity or if no publisherID is specified
   * for that particular BusinessEntity.
   *
   * @param businessKey
   * @param connection
   * @return publisherID or null if no publisherID is available.
   * @throws java.sql.SQLException
   */
  public static String selectPublisherID(String businessKey,Connection connection)
    throws java.sql.SQLException
  {
    if (businessKey == null)
      return null;

    String publisherID = null;
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      statement = connection.prepareStatement(selectPublisherSQL);
      statement.setString(1,businessKey);

      if (log.isDebugEnabled()) {
          log.debug("fetching publishers ID for BUSINESS_ENTITY:\n\n\t" + selectPublisherSQL +
            "\n\t BUSINESS_KEY=" + businessKey + "\n");
      }

      resultSet = statement.executeQuery();
      if (resultSet.next())
        publisherID = resultSet.getString(1);//("PUBLISHER_ID");

      return publisherID;
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
}
