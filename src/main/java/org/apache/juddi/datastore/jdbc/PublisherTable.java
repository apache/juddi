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
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.datatype.publisher.Publisher;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class PublisherTable
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(PublisherTable.class);

  static String insertSQL = null;
  static String selectSQL = null;
  static String deleteSQL = null;
  static String updateSQL = null;
  static String verifyAdminSQL = null;

  static
  {
    // buffer used to build SQL statements
    StringBuffer sql = null;

    // build insertSQL
    sql = new StringBuffer(150);
    sql.append("INSERT INTO PUBLISHER (");
    sql.append("PUBLISHER_ID,");
    sql.append("PUBLISHER_NAME,");
    sql.append("EMAIL_ADDRESS,");
    sql.append("IS_ADMIN, ");
    sql.append("IS_ENABLED), ");
    sql.append("MAX_BUSINESSES), ");
    sql.append("MAX_SERVICES_PER_BUSINESS), ");
    sql.append("MAX_BINDINGS_PER_SERVICE), ");
    sql.append("MAX_TMODELS) ");
    sql.append("VALUES (?,?,?,?,?,?,?,?,?)");
    insertSQL = sql.toString();

    // build selectSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("PUBLISHER_NAME,");
    sql.append("EMAIL_ADDRESS,");
    sql.append("IS_ADMIN,");
    sql.append("IS_ENABLED,");
    sql.append("MAX_BUSINESSES,");
    sql.append("MAX_SERVICES_PER_BUSINESS,");
    sql.append("MAX_BINDINGS_PER_SERVICE,");
    sql.append("MAX_TMODELS ");
    sql.append("FROM PUBLISHER ");
    sql.append("WHERE PUBLISHER_ID=?");
    selectSQL = sql.toString();

    // build deleteSQL
    sql = new StringBuffer(200);
    sql.append("DELETE FROM PUBLISHER ");
    sql.append("WHERE PUBLISHER_ID=?");
    deleteSQL = sql.toString();

    // build updateSQL
    sql = new StringBuffer(200);
    sql.append("UPDATE PUBLISHER ");
    sql.append("SET PUBLISHER_NAME=?,");
    sql.append("EMAIL_ADDRESS=?,");
    sql.append("IS_ADMIN=?,");
    sql.append("IS_ENABLED=?,");
    sql.append("MAX_BUSINESSES=?,");
    sql.append("MAX_SERVICES_PER_BUSINESS=?,");
    sql.append("MAX_BINDINGS_PER_SERVICE=?,");
    sql.append("MAX_TMODELS=? ");
    sql.append("WHERE PUBLISHER_ID=?");
    updateSQL = sql.toString();
  }

  /**
   * Insert new row into the PUBLISHER table.
   *
   * @param publisher
   * @param connection JDBC connection
   * @throws SQLException
   */
  public static void insert(Publisher publisher,Connection connection)
    throws SQLException
  {
    if (publisher == null)
      return; // nothing to insert

    PreparedStatement statement = null;

    try
    {
      statement = connection.prepareStatement(insertSQL);
      statement.setString(1,publisher.getPublisherID());
      statement.setString(2,publisher.getName());
      statement.setString(3,publisher.getEmailAddress());
      statement.setString(4,String.valueOf(publisher.isAdmin()));
      statement.setString(5,String.valueOf(publisher.isEnabled()));
      statement.setInt(6,publisher.getMaxBusinesses());
      statement.setInt(7,publisher.getMaxServicesPerBusiness());
      statement.setInt(8,publisher.getMaxBindingsPerService());
      statement.setInt(9,publisher.getMaxTModels());
      
      log.debug("insert into PUBLISHER table:\n\n\t" + insertSQL +
        "\n\t PUBLISHER_ID=" + publisher.getPublisherID() +
        "\n\t PUBLISHER_NAME=" + publisher.getName() +
        "\n\t EMAIL_ADDRESS=" + publisher.getEmailAddress() +
        "\n\t IS_ADMIN=" + publisher.isAdmin() +
        "\n\t IS_ENABLED=" + publisher.isEnabled() +
        "\n\t MAX_BUSINESSES=" + publisher.getMaxBusinesses() +
        "\n\t MAX_SERVICES_PER_BUSINESS=" + publisher.getMaxServicesPerBusiness() +
        "\n\t MAX_BINDINGS_PER_SERVICE=" + publisher.getMaxBindingsPerService() +
        "\n\t MAX_TMODELS=" + publisher.getMaxTModels() + "\n");

      // insert
      statement.executeUpdate();
    }
    catch(SQLException sqlex)
    {
      log.error(sqlex.getMessage());
      throw sqlex;
    }
    finally
    {
      try { statement.close(); } catch (Exception e) { /* ignored */ }
    }
  }

  /**
   * Select one row from the PUBLISHER table.
   *
   * @param publisherID
   * @param connection JDBC connection
   * @return Publisher
   * @throws SQLException
   */
  public static Publisher select(String publisherID,Connection connection)
    throws SQLException
  {
    // return 'null' if a null publisherID is specified.
    if (publisherID == null)
      return null;

    Publisher publisher = null;
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      statement = connection.prepareStatement(selectSQL);
      statement.setString(1,publisherID);

      log.debug("select from PUBLISHER table:\n\n\t" + selectSQL +
        "\n\t PUBLISHER_ID=" + publisherID + "\n");

      resultSet = statement.executeQuery();
      if (resultSet.next())
      {
        publisher = new Publisher();
        publisher.setPublisherID(publisherID);
        publisher.setName(resultSet.getString(1));//("PUBLISHER_NAME"));
        publisher.setEmailAddress(resultSet.getString(2));//("EMAIL_ADDRESS"));
        publisher.setAdminValue(resultSet.getString(3));//("IS_ADMIN"));
        publisher.setEnabledValue(resultSet.getString(4));//("IS_ENABLED"));
        publisher.setMaxBusinesses(resultSet.getInt(5));//("MAX_BUSINESSES"));
        publisher.setMaxServicesPerBusiness(resultSet.getInt(6));//("MAX_SERVICES_PER_BUSINESS"));
        publisher.setMaxBindingsPerService(resultSet.getInt(7));//("MAX_BINDINGS_PER_SERVICE"));
        publisher.setMaxTModels(resultSet.getInt(8));//("MAX_TMODELS"));      
      }

      return publisher;
    }
    catch(SQLException sqlex)
    {
      log.error(sqlex.getMessage());
      throw sqlex;
    }
    finally
    {
      try { resultSet.close(); } catch (Exception e) { /* ignored */ }
      try { statement.close(); } catch (Exception e) { /* ignored */ }
    }
  }

  /**
   * Delete row from the PUBLISHER table.
   *
   * @param publisherID
   * @param connection
   * @throws SQLException
   */
  public static void delete(String publisherID,Connection connection)
    throws SQLException
  {
    PreparedStatement statement = null;

    try
    {
      // prepare the delete
      statement = connection.prepareStatement(deleteSQL);
      statement.setString(1,publisherID);

      log.debug("delete from PUBLISHER table:\n\n\t" + deleteSQL +
        "\n\t PUBLISHER_ID=" + "\n");

      // execute the delete
      statement.executeUpdate();
    }
    catch(SQLException sqlex)
    {
      log.error(sqlex.getMessage());
      throw sqlex;
    }
    finally
    {
      try { statement.close(); } catch (Exception e) { /* ignored */ }
    }
  }

  /**
   * Update the PUBLISHER table for a particular PublisherID.
   *
   * @param publisher
   * @param connection JDBC connection
   * @throws SQLException
   */
  public static void update(Publisher publisher,Connection connection)
    throws SQLException
  {
    PreparedStatement statement = null;

    try
    {
      // prepare
      statement = connection.prepareStatement(updateSQL);
      statement.setString(1,publisher.getName());
      statement.setString(2,publisher.getEmailAddress());
      statement.setString(3,String.valueOf(publisher.isAdmin()));
      statement.setString(4,String.valueOf(publisher.isEnabled()));      
      statement.setInt(5,publisher.getMaxBusinesses());
      statement.setInt(6,publisher.getMaxServicesPerBusiness());
      statement.setInt(7,publisher.getMaxBindingsPerService());
      statement.setInt(8,publisher.getMaxTModels());
      statement.setString(9,publisher.getPublisherID());

      log.debug("update PUBLISHER table:\n\n\t" + updateSQL +
        "\n\t PUBLISHER_NAME=" + publisher.getName() +
        "\n\t EMAIL_ADDRESS=" + publisher.getEmailAddress() +
        "\n\t IS_ADMIN=" + publisher.isAdmin() +
        "\n\t IS_ENABLED=" + publisher.isEnabled() +
        "\n\t MAX_BUSINESSES=" + publisher.getMaxBusinesses() +
        "\n\t MAX_SERVICES_PER_BUSINESS=" + publisher.getMaxServicesPerBusiness() +
        "\n\t MAX_BINDINGS_PER_SERVICE=" + publisher.getMaxBindingsPerService() +
        "\n\t MAX_TMODELS=" + publisher.getMaxTModels() +       
        "\n\t PUBLISHER_ID=" + publisher.getPublisherID() + "\n");
      
      // execute
      statement.executeUpdate();
    }
    catch(SQLException sqlex)
    {
      log.error(sqlex.getMessage());
      throw sqlex;
    }
    finally
    {
      try { statement.close(); } catch (Exception e) { /* ignored */ }
    }
  }
}