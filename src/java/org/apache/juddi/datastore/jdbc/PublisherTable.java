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
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.datatype.publisher.Publisher;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.ConnectionManager;
import org.apache.juddi.util.jdbc.Transaction;

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
    sql.append("LAST_NAME,");
    sql.append("FIRST_NAME,");
    sql.append("MIDDLE_INIT,");
    sql.append("WORK_PHONE,");
    sql.append("MOBILE_PHONE,");
    sql.append("PAGER,");
    sql.append("EMAIL_ADDRESS,");
    sql.append("ADMIN, ");
    sql.append("ENABLED) ");
    sql.append("VALUES (?,?,?,?,?,?,?,?,?,?,?)");
    insertSQL = sql.toString();

    // build selectSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("PUBLISHER_NAME,");
    sql.append("LAST_NAME,");
    sql.append("FIRST_NAME,");
    sql.append("MIDDLE_INIT,");
    sql.append("WORK_PHONE,");
    sql.append("MOBILE_PHONE,");
    sql.append("PAGER,");
    sql.append("EMAIL_ADDRESS,");
    sql.append("ADMIN,");
    sql.append("ENABLED ");
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
    sql.append("LAST_NAME=?,");
    sql.append("FIRST_NAME=?,");
    sql.append("MIDDLE_INIT=?,");
    sql.append("WORK_PHONE=?,");
    sql.append("MOBILE_PHONE=?,");
    sql.append("PAGER=?,");
    sql.append("EMAIL_ADDRESS=?,");
    sql.append("ADMIN=?,");
    sql.append("ENABLED=? ");
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
      statement.setString(3,publisher.getFirstName());
      statement.setString(4,publisher.getLastName());
      statement.setString(5,publisher.getMiddleInit());
      statement.setString(6,publisher.getWorkPhone());
      statement.setString(7,publisher.getMobilePhone());
      statement.setString(8,publisher.getPager());
      statement.setString(9,publisher.getEmailAddress());
      statement.setString(10,String.valueOf(publisher.isAdmin()));
      statement.setString(11,String.valueOf(publisher.isEnabled()));

      log.debug("insert into PUBLISHER table:\n\n\t" + insertSQL +
        "\n\t PUBLISHER_ID=" + publisher.getPublisherID() +
        "\n\t PUBLISHER_NAME=" + publisher.getName() +
        "\n\t FIRST_NAME=" + publisher.getFirstName() +
        "\n\t LAST_NAME=" + publisher.getLastName() +
        "\n\t MIDDLE_INIT=" + publisher.getMiddleInit() +
        "\n\t WORK_PHONE=" + publisher.getWorkPhone() +
        "\n\t MOBILE_PHONE=" + publisher.getMobilePhone() +
        "\n\t PAGER=" + publisher.getPager() +
        "\n\t EMAIL_ADDRESS=" + publisher.getEmailAddress() +
        "\n\t ADMIN=" + publisher.isAdmin() +
        "\n\t ENABLED=" + publisher.isEnabled() + "\n");

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
        publisher.setLastName(resultSet.getString(2));//("LAST_NAME"));
        publisher.setFirstName(resultSet.getString(3));//("FIRST_NAME"));
        publisher.setMiddleInit(resultSet.getString(4));//("MIDDLE_INIT"));
        publisher.setWorkPhone(resultSet.getString(5));//("WORK_PHONE"));
        publisher.setMobilePhone(resultSet.getString(6));//("MOBILE_PHONE"));
        publisher.setPager(resultSet.getString(7));//("PAGER"));
        publisher.setEmailAddress(resultSet.getString(8));//("EMAIL_ADDRESS"));
        publisher.setAdminValue(resultSet.getString(9));//("ADMIN"));
        publisher.setEnabledValue(resultSet.getString(10));//("ENABLED"));
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
      statement.setString(2,publisher.getFirstName());
      statement.setString(3,publisher.getLastName());
      statement.setString(4,publisher.getMiddleInit());
      statement.setString(5,publisher.getWorkPhone());
      statement.setString(6,publisher.getMobilePhone());
      statement.setString(7,publisher.getPager());
      statement.setString(8,publisher.getEmailAddress());
      statement.setString(9,String.valueOf(publisher.isAdmin()));
      statement.setString(10,String.valueOf(publisher.isEnabled()));
      statement.setString(11,publisher.getPublisherID());

      log.debug("update PUBLISHER table:\n\n\t" + updateSQL +
        "\n\t PUBLISHER_NAME=" + publisher.getName() +
        "\n\t FIRST_NAME=" + publisher.getFirstName() +
        "\n\t LAST_NAME=" + publisher.getLastName() +
        "\n\t MIDDLE_INIT=" + publisher.getMiddleInit() +
        "\n\t WORK_PHONE=" + publisher.getWorkPhone() +
        "\n\t MOBILE_PHONE=" + publisher.getMobilePhone() +
        "\n\t PAGER=" + publisher.getPager() +
        "\n\t EMAIL_ADDRESS=" + publisher.getEmailAddress() +
        "\n\t ADMIN=" + publisher.isAdmin() +
        "\n\t ENABLED=" + publisher.isEnabled() +
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

    if (connection != null)
    {
      try
      {
        // begin a new transaction
        txn.begin(connection);

        // insert a few new publishers
        Publisher publisher = new Publisher();
        publisher.setPublisherID("bcrosby");
        publisher.setName("Bing Crosby");
        publisher.setLastName("Crosby");
        publisher.setFirstName("Bing");
        publisher.setWorkPhone("978.123-4567");
        publisher.setMobilePhone("617-765-9876");
        publisher.setPager("800-123-4655 ID: 501");
        publisher.setEmailAddress("bcrosby@juddi.org");
        publisher.setAdmin(false);
        publisher.setEnabled(false);
        PublisherTable.insert(publisher,connection);

        // select each inserted publisher
        System.out.println(PublisherTable.select("bcrosby",connection));

        publisher.setFirstName("Bart");
        publisher.setName("Barthalomue Crosby");
        publisher.setEnabled(true);
        PublisherTable.update(publisher,connection);

        // select each inserted publisher
        System.out.println(PublisherTable.select("bcrosby",connection));

        // delete two of the inserted publishers
        PublisherTable.delete("bcrosby",connection);

        // select each inserted publisher
        System.out.println(PublisherTable.select("bcrosby",connection));
        System.out.println("");

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