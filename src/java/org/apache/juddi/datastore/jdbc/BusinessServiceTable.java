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
import org.apache.juddi.datatype.service.BusinessService;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.ConnectionManager;
import org.apache.juddi.util.jdbc.Transaction;
import org.apache.juddi.uuidgen.UUIDGen;
import org.apache.juddi.uuidgen.UUIDGenFactory;

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

    if (connection != null)
    {
      try
      {
        String businessKey = uuidgen.uuidgen();
        BusinessEntity business = new BusinessEntity();
        business.setBusinessKey(businessKey);
        business.setAuthorizedName("sviens");
        business.setOperator("WebServiceRegistry.com");

        String serviceKey = uuidgen.uuidgen();
        BusinessService service = new BusinessService();
        service.setBusinessKey(businessKey);
        service.setServiceKey(serviceKey);

        // begin a new transaction
        txn.begin(connection);

        String authorizedUserID = "sviens";

        // insert a new BusinessEntity
        BusinessEntityTable.insert(business,authorizedUserID,connection);

        // insert a new BusinessService
        BusinessServiceTable.insert(service,connection);

        // insert another new BusinessService
        service.setServiceKey(uuidgen.uuidgen());
        BusinessServiceTable.insert(service,connection);

        // insert one more new BusinessService
        service.setServiceKey(uuidgen.uuidgen());
        BusinessServiceTable.insert(service,connection);

        // select a BusinessService object
        service = BusinessServiceTable.select(serviceKey,connection);

        // delete a BusinessService object
        BusinessServiceTable.delete(serviceKey,connection);

        // select a BusinessService object
        service = BusinessServiceTable.select(serviceKey,connection);

        // select a Collection BusinessService objects by BusinessKey
        BusinessServiceTable.selectByBusinessKey(businessKey,connection);

        // delete a Collection BusinessService objects by BusinessKey
        BusinessServiceTable.deleteByBusinessKey(businessKey,connection);

        // select a Collection BusinessService objects by BusinessKey
        BusinessServiceTable.selectByBusinessKey(businessKey,connection);

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
}
