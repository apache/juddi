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
import org.apache.juddi.datatype.binding.AccessPoint;
import org.apache.juddi.datatype.binding.BindingTemplate;
import org.apache.juddi.datatype.binding.HostingRedirector;
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
class BindingTemplateTable
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(BindingTemplateTable.class);

  static String insertSQL = null;
  static String deleteSQL = null;
  static String selectSQL = null;
  static String selectByServiceKeySQL = null;
  static String deleteByServiceKeySQL = null;
  static String verifyOwnershipSQL = null;

  static
  {
    // buffer used to build SQL statements
    StringBuffer sql = null;

    // build insertSQL
    sql = new StringBuffer(150);
    sql.append("INSERT INTO BINDING_TEMPLATE (");
    sql.append("SERVICE_KEY,");
    sql.append("BINDING_KEY,");
    sql.append("ACCESS_POINT_TYPE,");
    sql.append("ACCESS_POINT_URL,");
    sql.append("HOSTING_REDIRECTOR,");
    sql.append("LAST_UPDATE) ");
    sql.append("VALUES (?,?,?,?,?,?)");
    insertSQL = sql.toString();

    // build deleteSQL
    sql = new StringBuffer(100);
    sql.append("DELETE FROM BINDING_TEMPLATE ");
    sql.append("WHERE BINDING_KEY=?");
    deleteSQL = sql.toString();

    // build selectSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("SERVICE_KEY,");
    sql.append("ACCESS_POINT_TYPE,");
    sql.append("ACCESS_POINT_URL,");
    sql.append("HOSTING_REDIRECTOR ");
    sql.append("FROM BINDING_TEMPLATE ");
    sql.append("WHERE BINDING_KEY=?");
    selectSQL = sql.toString();

    // build selectByServiceKeySQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("BINDING_KEY,");
    sql.append("ACCESS_POINT_TYPE,");
    sql.append("ACCESS_POINT_URL,");
    sql.append("HOSTING_REDIRECTOR ");
    sql.append("FROM BINDING_TEMPLATE ");
    sql.append("WHERE SERVICE_KEY=?");
    selectByServiceKeySQL = sql.toString();

    // build deleteByServiceKeySQL
    sql = new StringBuffer(100);
    sql.append("DELETE FROM BINDING_TEMPLATE ");
    sql.append("WHERE SERVICE_KEY=?");
    deleteByServiceKeySQL = sql.toString();

    // build verifyOwnershipSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("* ");
    sql.append("FROM BUSINESS_ENTITY e, BUSINESS_SERVICE s, BINDING_TEMPLATE t ");
    sql.append("WHERE s.SERVICE_KEY = t.SERVICE_KEY ");
    sql.append("AND e.BUSINESS_KEY = s.BUSINESS_KEY ");
    sql.append("AND t.BINDING_KEY=? ");
    sql.append("AND e.PUBLISHER_ID=?");
    verifyOwnershipSQL = sql.toString();
  }

  /**
   * Insert new row into the BINDING_TEMPLATE table.
   *
   * @param  binding Binding Template object holding values to be inserted
   * @param  connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void insert(BindingTemplate binding,Connection connection)
    throws java.sql.SQLException
  {
    PreparedStatement statement = null;
    Timestamp timeStamp = new Timestamp(System.currentTimeMillis());

    try
    {
      // pull the raw AccessPoint attributes out (if any)
      String urlType = null;
      String url = null;
      AccessPoint accessPoint = binding.getAccessPoint();
      if (accessPoint != null)
      {
        urlType = accessPoint.getURLType();
        url = accessPoint.getURL();
      }

      // pull the raw HostingRedirector attributes out (if any)
      String redirectorKey = null;
      HostingRedirector redirector = binding.getHostingRedirector();
      if (redirector != null)
      {
        if (redirector.getBindingKey() != null)
          redirectorKey = redirector.getBindingKey();
      }

      // prepare and execute the insert
      statement = connection.prepareStatement(insertSQL);
      statement.setString(1,binding.getServiceKey().toString());
      statement.setString(2,binding.getBindingKey().toString());
      statement.setString(3,urlType);
      statement.setString(4,url);
      statement.setString(5,redirectorKey);
      statement.setTimestamp(6,timeStamp);

      log.debug("insert into BINDING_TEMPLATE table:\n\n\t" + insertSQL +
        "\n\t SERVICE_KEY=" + binding.getServiceKey().toString() +
        "\n\t BINDING_KEY=" + binding.getBindingKey().toString() +
        "\n\t ACCESS_POINT_TYPE=" + urlType +
        "\n\t ACCESS_POINT_URL=" + url +
        "\n\t HOSTING_REDIRECTOR=" + redirectorKey +
        "\n\t LAST_UPDATE=" + timeStamp.getTime() + "\n");

      statement.executeUpdate();
    }
    finally
    {
      try { statement.close(); } catch (Exception e) { /* ignored */ }
    }
  }

  /**
   * Delete row from the BINDING_TEMPLATE table.
   *
   * @param bindingKey primary key value
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void delete(String bindingKey,Connection connection)
    throws java.sql.SQLException
  {
    PreparedStatement statement = null;

    try
    {
      // prepare the delete
      statement = connection.prepareStatement(deleteSQL);
      statement.setString(1,bindingKey);

      log.debug("delete from BINDING_TEMPLATE table:\n\n\t" + deleteSQL +
        "\n\t BINDING_KEY=" + bindingKey + "\n");

      // execute
      statement.executeUpdate();
    }
    finally
    {
      try { statement.close(); } catch (Exception e) { /* ignored */ }
    }
  }

  /**
   * Select one row from the BINDING_TEMPLATE table.
   *
   * @param bindingKey primary key value
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static BindingTemplate select(String bindingKey,Connection connection)
    throws java.sql.SQLException
  {
    BindingTemplate binding = null;
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      statement = connection.prepareStatement(selectSQL);
      statement.setString(1,bindingKey.toString());

      log.debug("select from BINDING_TEMPLATE table:\n\n\t" + selectSQL +
        "\n\t BINDING_KEY=" + bindingKey.toString() + "\n");

      resultSet = statement.executeQuery();
      if (resultSet.next())
      {
        binding = new BindingTemplate();
        binding.setServiceKey(resultSet.getString(1));//("SERVICE_KEY"));
        binding.setBindingKey(bindingKey);

        String urlType = resultSet.getString(2);//("ACCESS_POINT_TYPE");
        String url = resultSet.getString(3);//("ACCESS_POINT_URL");
        if ((urlType != null) && (url != null))
          binding.setAccessPoint(new AccessPoint(urlType,url));

        String redirectorKey = resultSet.getString(4);//("HOSTING_REDIRECTOR");
        if (redirectorKey != null)
          binding.setHostingRedirector(new HostingRedirector(redirectorKey));
      }

      return binding;
    }
    finally
    {
      try { resultSet.close(); } catch (Exception e) { /* ignored */ }
      try { statement.close(); } catch (Exception e) { /* ignored */ }
    }
  }

  /**
   * Select all rows from the business_service table for a given
   * BusinessKey.
   *
   * @param  serviceKey ServiceKey
   * @param  connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector selectByServiceKey(String serviceKey,Connection connection)
    throws java.sql.SQLException
  {
    Vector bindList = new Vector();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      // create a statement to query with
      statement = connection.prepareStatement(selectByServiceKeySQL);
      statement.setString(1,serviceKey.toString());

      log.debug("select from BINDING_TEMPLATE table:\n\n\t" + selectByServiceKeySQL +
        "\n\t SERVICE_KEY=" + serviceKey.toString() + "\n");

      // execute the statement
      resultSet = statement.executeQuery();

      BindingTemplate binding = null;
      while (resultSet.next())
      {
        binding = new BindingTemplate();
        binding.setServiceKey(serviceKey);
        binding.setBindingKey(resultSet.getString(1));//("BINDING_KEY"));

        String urlType = resultSet.getString(2);//("ACCESS_POINT_TYPE");
        String url = resultSet.getString(3);//("ACCESS_POINT_URL");
        if ((urlType != null) && (url != null))
          binding.setAccessPoint(new AccessPoint(urlType,url));

        String redirectorKey = resultSet.getString(4);//("HOSTING_REDIRECTOR");
        if (redirectorKey != null)
          binding.setHostingRedirector(new HostingRedirector(redirectorKey));

        bindList.add(binding);
        binding = null;
      }

      return bindList;
    }
    finally
    {
      try { resultSet.close(); } catch (Exception e) { /* ignored */ }
      try { statement.close(); } catch (Exception e) { /* ignored */ }
    }
  }

  /**
   * Delete multiple rows from the BINDING_TEMPLATE table that are assigned to
   * the BusinessKey specified.
   *
   * @param  serviceKey ServiceKey
   * @param  connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void deleteByServiceKey(String serviceKey,Connection connection)
    throws java.sql.SQLException
  {
    PreparedStatement statement = null;

    try
    {
      // prepare the delete
      statement = connection.prepareStatement(deleteByServiceKeySQL);
      statement.setString(1,serviceKey.toString());

      log.debug("delete from BINDING_TEMPLATE table:\n\n\t" + deleteByServiceKeySQL +
        "\n\t SERVICE_KEY=" + serviceKey.toString() + "\n");

      // execute
      int returnCode = statement.executeUpdate();

      log.info("delete was successful, rows deleted=" + returnCode);
    }
    finally
    {
      try { statement.close(); } catch (Exception e) { /* ignored */ }
    }
  }

  /**
   * Verify that 'authorizedName' has the authority to update or delete
   * BindingTemplate identified by the bindingKey parameter
   *
   * @param bindingKey
   * @param publisherID
   * @param connection
   * @throws java.sql.SQLException
   */
  public static boolean verifyOwnership(String bindingKey,String publisherID,Connection connection)
    throws java.sql.SQLException
  {
    if ((bindingKey == null) || (publisherID == null))
      return false;

    boolean authorized = false;
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      statement = connection.prepareStatement(verifyOwnershipSQL);
      statement.setString(1,bindingKey);
      statement.setString(2,publisherID);

      log.debug("checking ownership of BINDING_TEMPLATE:\n\n\t" + verifyOwnershipSQL +
        "\n\t BINDNG_KEY=" + bindingKey +
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

        String bindingKey = uuidgen.uuidgen();
        BindingTemplate binding = new BindingTemplate();
        binding.setServiceKey(serviceKey);
        binding.setBindingKey(bindingKey);
        binding.setAccessPoint(new AccessPoint("http","http://juddi.org/bindingtemplate.html"));

        String authorizedUserID = "sviens";

        // begin a new transaction
        txn.begin(connection);

        // insert a new BusinessEntity
        BusinessEntityTable.insert(business,authorizedUserID,connection);

        // insert a new BusinessService
        BusinessServiceTable.insert(service,connection);

        // insert a new BindingTemplate
        BindingTemplateTable.insert(binding,connection);

        // insert another BindingTemplate
        binding.setBindingKey(uuidgen.uuidgen());
        BindingTemplateTable.insert(binding,connection);

        // insert one more BindingTemplate
        binding.setBindingKey(uuidgen.uuidgen());
        BindingTemplateTable.insert(binding,connection);

        // select a particular BindingTemplate
        binding = BindingTemplateTable.select(bindingKey,connection);

        // delete that BindingTemplate
        BindingTemplateTable.delete(bindingKey,connection);

        // re-select that BindingTemplate
        binding = BindingTemplateTable.select(bindingKey,connection);

        // select a Collection of BindingTemplate objects (by ServiceKey)
        BindingTemplateTable.selectByServiceKey(serviceKey,connection);

        // delete a Collection of BindingTemplate objects (by ServiceKey)
        BindingTemplateTable.deleteByServiceKey(serviceKey,connection);

        // re-select a Collection of BindingTemplate objects (by ServiceKey)
        BindingTemplateTable.selectByServiceKey(serviceKey,connection);

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