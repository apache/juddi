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
import org.apache.juddi.datatype.Name;
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
class ServiceNameTable
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(ServiceNameTable.class);

  static String insertSQL = null;
  static String selectSQL = null;
  static String deleteSQL = null;

  static {
    // buffer used to build SQL statements
    StringBuffer sql = null;

    // build insertSQL
    sql = new StringBuffer(150);
    sql.append("INSERT INTO SERVICE_NAME (");
    sql.append("SERVICE_KEY,");
    sql.append("SERVICE_NAME_ID,");
    sql.append("LANG_CODE,");
    sql.append("NAME) ");
    sql.append("VALUES (?,?,?,?)");
    insertSQL = sql.toString();

    // build selectSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("LANG_CODE,");
    sql.append("NAME, ");
    sql.append("SERVICE_NAME_ID ");
    sql.append("FROM SERVICE_NAME ");
    sql.append("WHERE SERVICE_KEY=? ");
    sql.append("ORDER BY SERVICE_NAME_ID");
    selectSQL = sql.toString();

    // build deleteSQL
    sql = new StringBuffer(100);
    sql.append("DELETE FROM SERVICE_NAME ");
    sql.append("WHERE SERVICE_KEY=?");
    deleteSQL = sql.toString();
  }

  /**
   * Insert new row into the SERVICE_NAME table.
   *
   * @param serviceKey String to the BusinessEntity object that owns the Contact to be inserted
   * @param nameList Vector of Name objects holding values to be inserted
   * @param  connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void insert(
    String serviceKey,
    Vector nameList,
    Connection connection)
    throws java.sql.SQLException
  {
    if ((nameList == null) || (nameList.size() == 0))
      return; // everything is valid but no elements to insert

    PreparedStatement statement = null;

    try
    {
      statement = connection.prepareStatement(insertSQL);
      statement.setString(1, serviceKey.toString());

      int listSize = nameList.size();
      for (int nameID = 0; nameID < listSize; nameID++)
      {
        Name name = (Name) nameList.elementAt(nameID);

        statement.setInt(2, nameID);
        statement.setString(3, name.getLanguageCode());
        statement.setString(4, name.getValue());

        log.debug(
          "insert into SERVICE_NAME table:\n\n\t"
            + insertSQL
            + "\n\t SERVICE_KEY="
            + serviceKey.toString()
            + "\n\t SERVICE_NAME_ID="
            + nameID
            + "\n\t LANG_CODE="
            + name.getLanguageCode()
            + "\n\t NAME="
            + name.getValue()
            + "\n");

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
   * Select all rows from the SERVICE_NAME table for a given ServiceKey.
   *
   * @param  serviceKey String
   * @param  connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector select(String serviceKey, Connection connection)
    throws java.sql.SQLException
  {
    Vector nameList = new Vector();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      // create a statement to query with
      statement = connection.prepareStatement(selectSQL);
      statement.setString(1, serviceKey.toString());

      log.debug(
        "select from SERVICE_NAME table:\n\n\t"
          + selectSQL
          + "\n\t SERVICE_KEY="
          + serviceKey.toString()
          + "\n");

      // execute the statement
      resultSet = statement.executeQuery();
      while (resultSet.next())
      {
        Name name = new Name();
        name.setLanguageCode(resultSet.getString(1)); //("LANG_CODE"));
        name.setValue(resultSet.getString(2)); //("NAME"));
        nameList.add(name);
      }

      return nameList;
    }
    finally
    {
      try
      {
        statement.close();
        resultSet.close();
      }
      catch (Exception e)
      { /* ignored */
      }
    }
  }

  /**
   * Delete multiple rows from the SERVICE_NAME table that are assigned to the
   * ServiceKey specified.
   *
   * @param  serviceKey String
   * @param  connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void delete(String serviceKey, Connection connection)
    throws java.sql.SQLException
  {
    PreparedStatement statement = null;

    try
    {
      // prepare the delete
      statement = connection.prepareStatement(deleteSQL);
      statement.setString(1, serviceKey.toString());

      log.debug(
        "delete from SERVICE_NAME table:\n\n\t"
          + deleteSQL
          + "\n\t SERVICE_KEY="
          + serviceKey.toString()
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

        String serviceKey = uuidgen.uuidgen();
        BusinessService service = new BusinessService();
        service.setServiceKey(serviceKey);
        service.setBusinessKey(businessKey);

        Vector nameList = new Vector();
        nameList.add(new Name("SteveViens.com", "en"));
        nameList.add(new Name("EsephanoViens.com", "it"));
        nameList.add(new Name("AsdfJkl.com", "cy"));

        String authorizedUserID = "sviens";

        // begin a new transaction
        txn.begin(connection);

        // insert a new BusinessEntity
        BusinessEntityTable.insert(business, authorizedUserID, connection);

        // insert a new BusinessService
        BusinessServiceTable.insert(service, connection);

        // insert a Collection of Name objects
        ServiceNameTable.insert(serviceKey, nameList, connection);

        // select the Collection of Name objects
        nameList = ServiceNameTable.select(serviceKey, connection);

        // delete the Collection of Name objects
        ServiceNameTable.delete(serviceKey, connection);

        // re-select the Collection of Name objects
        nameList = ServiceNameTable.select(serviceKey, connection);

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
