/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "jUDDI" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.ConnectionManager;
import org.apache.juddi.util.jdbc.Transaction;
import org.apache.juddi.uuidgen.UUIDGen;
import org.apache.juddi.uuidgen.UUIDGenFactory;

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

  static
  {
    // buffer used to build SQL statements
    StringBuffer sql = null;

    // build insertSQL
    sql = new StringBuffer(150);
    sql.append("INSERT INTO BUSINESS_ENTITY (");
    sql.append("BUSINESS_KEY,");
    sql.append("AUTHORIZED_NAME,");
    sql.append("PUBLISHER_ID,");
    sql.append("OPERATOR,");
    sql.append("LAST_UPDATE) ");
    sql.append("VALUES (?,?,?,?,?)");
    insertSQL = sql.toString();

    // build deleteSQL
    sql = new StringBuffer(100);
    sql.append("DELETE FROM BUSINESS_ENTITY ");
    sql.append("WHERE BUSINESS_KEY=?");
    deleteSQL = sql.toString();

    // build selectSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("AUTHORIZED_NAME,");
    sql.append("OPERATOR ");
    sql.append("FROM BUSINESS_ENTITY ");
    sql.append("WHERE BUSINESS_KEY=?");
    selectSQL = sql.toString();

    // build selectByPublisherIDSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("BUSINESS_KEY ");
    sql.append("FROM BUSINESS_ENTITY ");
    sql.append("WHERE PUBLISHER_ID=?");
    selectByPublisherSQL = sql.toString();

    // build verifyOwnershipSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("* ");
    sql.append("FROM BUSINESS_ENTITY ");
    sql.append("WHERE BUSINESS_KEY=? ");
    sql.append("AND PUBLISHER_ID=?");
    verifyOwnershipSQL = sql.toString();

    // build selectPublisherSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("PUBLISHER_ID ");
    sql.append("FROM BUSINESS_ENTITY ");
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

      log.debug("insert into BUSINESS_ENTITY table:\n\n\t" + insertSQL +
        "\n\t BUSINESS_KEY=" + business.getBusinessKey() +
        "\n\t AUTHORIZED_NAME=" + business.getAuthorizedName() +
        "\n\t PUBLISHER_ID=" + publisherID +
        "\n\t OPERATOR=" + business.getOperator() +
        "\n\t LAST_UPDATE=" + timeStamp.getTime() + "\n");

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

      log.debug("delete from BUSINESS_ENTITY table:\n\n\t" + deleteSQL +
        "\n\t BUSINESS_KEY=" + businessKey.toString() + "\n");

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

      log.debug("select from BUSINESS_ENTITY table:\n\n\t" + selectSQL +
        "\n\t BUSINESS_KEY=" + businessKey.toString() + "\n");

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

      log.debug("select from BUSINESS_ENTITY table:\n\n\t" + selectByPublisherSQL +
        "\n\t PUBLISHER_ID=" + publisherID + "\n");

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

      log.debug("checking ownership of BUSINESS_ENTITY:\n\n\t" + verifyOwnershipSQL +
        "\n\t BUSINESS_KEY=" + businessKey +
        "\n\t PUBLISHER_ID=" + publisherID + "\n");

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

      log.debug("fetching publishers ID for BUSINESS_ENTITY:\n\n\t" + selectPublisherSQL +
        "\n\t BUSINESS_KEY=" + businessKey + "\n");

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

  public static void test(Connection connection)
    throws Exception
  {
    Transaction txn = new Transaction();
    UUIDGen uuidgen = UUIDGenFactory.getUUIDGen();

    try
    {
      String businessKey = uuidgen.uuidgen();
      BusinessEntity business = new BusinessEntity();
      business.setBusinessKey(businessKey);
      business.setAuthorizedName("Steve Viens");
      business.setOperator("www.jUDDI.org");

      String publisherID = "sviens";

      // begin a new transaction
      txn.begin(connection);

      // insert a new BusinessEntity
      BusinessEntityTable.insert(business,publisherID,connection);

      // select one of the BusinessEntity objects
      business = BusinessEntityTable.select(businessKey,connection);

      // delete that BusinessEntity object
      //BusinessEntityTable.delete(businessKey,connection);

      // re-select that BusinessEntity object
      business = BusinessEntityTable.select(businessKey,connection);

      // commit the transaction
      txn.commit();
    }
    catch(Exception ex)
    {
      try { txn.rollback(); }
      catch(java.sql.SQLException sqlex) { sqlex.printStackTrace(); }
      throw ex;
    }
  }
}
