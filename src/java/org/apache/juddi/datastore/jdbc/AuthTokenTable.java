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
import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.datatype.publisher.Publisher;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.ConnectionManager;
import org.apache.juddi.util.jdbc.Transaction;
import org.apache.juddi.uuidgen.UUIDGen;
import org.apache.juddi.uuidgen.UUIDGenFactory;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class AuthTokenTable
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(AuthTokenTable.class);

  static String insertSQL = null;
  static String selectPublisherSQL = null;
  static String deleteSQL = null;
  static String touchSQL = null;
  static String selectLastUsedSQL = null;
  static String invalidateSQL = null;
  static String selectTokenStateSQL = null;

  static
  {
    // buffer used to build SQL statements
    StringBuffer sql = null;

    // build insertSQL
    sql = new StringBuffer(350);
    sql.append("INSERT INTO AUTH_TOKEN (");
    sql.append("AUTH_TOKEN,");
    sql.append("PUBLISHER_ID,");
    sql.append("PUBLISHER_NAME,");
    sql.append("CREATED,");
    sql.append("LAST_USED,");
    sql.append("NUMBER_OF_USES,");
    sql.append("TOKEN_STATE) ");
    sql.append("VALUES (?,?,?,?,?,0,1)");
    insertSQL = sql.toString();

    // build selectPublisherSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("PUBLISHER_ID,");
    sql.append("PUBLISHER_NAME ");
    sql.append("FROM AUTH_TOKEN ");
    sql.append("WHERE AUTH_TOKEN=?");
    selectPublisherSQL = sql.toString();

    // build deleteSQL
    sql = new StringBuffer(100);
    sql.append("DELETE FROM AUTH_TOKEN ");
    sql.append("WHERE AUTH_TOKEN=?");
    deleteSQL = sql.toString();

    // build touchSQL
    sql = new StringBuffer(150);
    sql.append("UPDATE AUTH_TOKEN ");
    sql.append("SET LAST_USED=?,");
    sql.append("NUMBER_OF_USES=NUMBER_OF_USES+1 ");
    sql.append("WHERE AUTH_TOKEN=? ");
    touchSQL = sql.toString();

    // build selectLastUsedSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("LAST_USED ");
    sql.append("FROM AUTH_TOKEN ");
    sql.append("WHERE AUTH_TOKEN=?");
    selectLastUsedSQL = sql.toString();

    // build invalidateSQL
    sql = new StringBuffer(100);
    sql.append("UPDATE AUTH_TOKEN ");
    sql.append("SET LAST_USED=?,");
    sql.append("NUMBER_OF_USES=NUMBER_OF_USES+1,");
    sql.append("TOKEN_STATE=0 ");
    sql.append("WHERE AUTH_TOKEN=? ");
    invalidateSQL = sql.toString();

    // build selectTokenStateSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("TOKEN_STATE ");
    sql.append("FROM AUTH_TOKEN ");
    sql.append("WHERE AUTH_TOKEN=?");
    selectTokenStateSQL = sql.toString();
  }

  /**
   * Insert new row into the AUTH_TOKEN table.
   *
   * @param authToken
   * @param publisher
   * @param connection JDBC connection
   * @throws SQLException
   */
  public static void insert(String authToken,Publisher publisher,Connection connection)
    throws SQLException
  {
    PreparedStatement statement = null;
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    try
    {
      statement = connection.prepareStatement(insertSQL);
      statement.setString(1,authToken);
      statement.setString(2,publisher.getPublisherID());
      statement.setString(3,publisher.getName());
      statement.setTimestamp(4,timestamp);
      statement.setTimestamp(5,timestamp);

      log.debug("insert into AUTH_TOKEN table:\n\n\t" + insertSQL +
        "\n\t AUTH_TOKEN=" + authToken +
        "\n\t PUBLISHER_ID=" + publisher.getPublisherID() +
        "\n\t PUBLISHER_NAME=" + publisher.getName() +
        "\n\t CREATED=" + timestamp.toString() +
        "\n\t LAST_USED=" + timestamp.toString() +
        "\n\t NUMBER_OF_USES=1" +
        "\n\t TOKEN_STATE=1\n");

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
   * Select one row from the AUTH_TOKEN table.
   *
   * @param authToken
   * @param connection JDBC connection
   * @return Publisher The publisher's info
   * @throws SQLException
   */
  public static Publisher selectPublisher(String authToken,Connection connection)
    throws SQLException
  {
    Publisher publisher = null;
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      statement = connection.prepareStatement(selectPublisherSQL);
      statement.setString(1,authToken);

      log.debug("select from AUTH_TOKEN table:\n\n\t" + selectPublisherSQL +
        "\n\t AUTH_TOKEN=" + authToken + "\n");

      resultSet = statement.executeQuery();
      if (resultSet.next())
      {
        publisher = new Publisher();
        publisher.setPublisherID(resultSet.getString(1));//("PUBLISHER_ID"));
        publisher.setName(resultSet.getString(2));//("PUBLISHER_NAME"));
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
   * Delete row from the AUTH_TOKEN table.
   *
   * @param authToken
   * @param connection
   * @throws SQLException
   */
  public static void delete(String authToken,Connection connection)
    throws SQLException
  {
    PreparedStatement statement = null;

    try
    {
      // prepare the delete
      statement = connection.prepareStatement(deleteSQL);
      statement.setString(1,authToken);

      log.debug("delete from AUTH_TOKEN table:\n\n\t" + deleteSQL +
        "\n\t AUTH_TOKEN=" + authToken + "\n");

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

  /**
   * Update the PUBLISHER table for a particular PublisherID.
   *
   * @param connection JDBC connection
   * @throws SQLException
   */
  public static void touch(String authToken,Connection connection)
    throws SQLException
  {
    PreparedStatement statement = null;
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    try
    {
      // create a statement to query with
      statement = connection.prepareStatement(touchSQL);
      statement.setTimestamp(1,timestamp);
      statement.setString(2,authToken);

      log.debug("update AUTH_TOKEN table:\n\n\t" + touchSQL +
       "\n\t AUTH_TOKEN=" + authToken +
       "\n\t LAST_USED=" + timestamp.toString() + "\n");

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

  /**
   * Update the PUBLISHER table for a particular PublisherID.
   *
   * @param connection JDBC connection
   * @throws SQLException
   */
  public static long selectLastUsed(String authToken,Connection connection)
    throws SQLException
  {
    PreparedStatement statement = null;
    ResultSet resultSet = null;
    long lastUsed = -1;

    try
    {
      // create a statement to query with
      statement = connection.prepareStatement(selectLastUsedSQL);
      statement.setString(1,authToken);

      log.debug("select LAST_USED from AUTH_TOKEN table:\n\n\t" + selectLastUsedSQL +
       "\n\t AUTH_TOKEN=" + authToken + "\n");

      resultSet = statement.executeQuery();
      if (resultSet.next())
      {
        Timestamp timestamp = resultSet.getTimestamp("LAST_USED");
        if (timestamp != null)
          lastUsed = timestamp.getTime();
      }
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

    return lastUsed;
  }

  /**
   * Update the AUTH_TOKEN table's LAST_USED property for a
   * particular authToken.
   *
   * @param connection JDBC connection
   * @throws SQLException
   */
  public static void invalidate(String authToken,Connection connection)
    throws SQLException
  {
    PreparedStatement statement = null;
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    try
    {
      // create a statement to query with
      statement = connection.prepareStatement(invalidateSQL);
      statement.setTimestamp(1,timestamp);
      statement.setString(2,authToken);

      log.debug("update AUTH_TOKEN table:\n\n\t" + invalidateSQL +
        "\n\t LAST_USED=" + timestamp.toString() +
        "\n\t AUTH_TOKEN=" + authToken + "\n");

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

  /**
   * Query the token's state. This is used to determine if the authToken
   * has been discarded, for example.
   *
   * @param authToken
   * @param connection
   * @throws SQLException
   */
  public static long selectTokenState(String authToken, Connection connection)
      throws SQLException
  {
    PreparedStatement statement = null;
    ResultSet resultSet = null;
    long tokenState = -1;

    try
    {
      // create a statement to query with
      statement = connection.prepareStatement(selectTokenStateSQL);
      statement.setString(1,authToken);

      log.debug("SQL Statement: [" + selectTokenStateSQL + "], parameter authToken: [" + authToken + "]");

      resultSet = statement.executeQuery();
      if (resultSet.next())
      {
        tokenState = resultSet.getLong("TOKEN_STATE");
      }
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

    return tokenState;
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
        // begin a new transaction
        txn.begin(connection);

        String authToken1 = "juddi:"+uuidgen.uuidgen();
        AuthTokenTable.insert(authToken1,new Publisher("sviens","Steve Viens"),connection);
        AuthTokenTable.selectLastUsed(authToken1,connection);

        String authToken2 = "juddi:"+uuidgen.uuidgen();
        AuthTokenTable.insert(authToken2,new Publisher("sviens","Steve Viens"),connection);
        AuthTokenTable.selectLastUsed(authToken2,connection);

        System.out.println("PublisherID: "+AuthTokenTable.selectPublisher(authToken1,connection));

        AuthTokenTable.delete(authToken1,connection);

        Thread.sleep(3000);

        AuthTokenTable.touch(authToken2,connection);
        AuthTokenTable.touch(authToken2,connection);
        AuthTokenTable.touch(authToken2,connection);
        AuthTokenTable.selectLastUsed(authToken2,connection);
        AuthTokenTable.touch(authToken2,connection);
        AuthTokenTable.touch(authToken2,connection);
        AuthTokenTable.touch(authToken2,connection);
        AuthTokenTable.selectLastUsed(authToken2,connection);
        AuthTokenTable.touch(authToken2,connection);
        AuthTokenTable.touch(authToken2,connection);
        AuthTokenTable.touch(authToken2,connection);
        AuthTokenTable.selectLastUsed(authToken2,connection);
        AuthTokenTable.touch(authToken2,connection);
        AuthTokenTable.invalidate(authToken2,connection);
        AuthTokenTable.selectLastUsed(authToken2,connection);

        System.out.println("PublisherID: "+AuthTokenTable.selectPublisher(authToken1,connection));
        System.out.println("PublisherID: "+AuthTokenTable.selectPublisher(authToken2,connection));

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
