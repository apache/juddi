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
import org.apache.juddi.datatype.OverviewDoc;
import org.apache.juddi.datatype.tmodel.TModel;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.ConnectionManager;
import org.apache.juddi.util.jdbc.Transaction;
import org.apache.juddi.uuidgen.UUIDGen;
import org.apache.juddi.uuidgen.UUIDGenFactory;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class TModelTable
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(TModelTable.class);

  static String insertSQL = null;
  static String deleteSQL = null;
  static String selectSQL = null;
  static String selectByPublisherSQL = null;
  static String verifyOwnershipSQL = null;

  static
  {
    // buffer used to build SQL statements
    StringBuffer sql = null;

    // build insertSQL
    sql = new StringBuffer(150);
    sql.append("INSERT INTO TMODEL (");
    sql.append("TMODEL_KEY,");
    sql.append("AUTHORIZED_NAME,");
    sql.append("PUBLISHER_ID,");
    sql.append("OPERATOR,");
    sql.append("NAME,");
    sql.append("OVERVIEW_URL,");
    sql.append("LAST_UPDATE) ");
    sql.append("VALUES (?,?,?,?,?,?,?)");
    insertSQL = sql.toString();

    // build deleteSQL
    sql = new StringBuffer(100);
    sql.append("DELETE FROM TMODEL ");
    sql.append("WHERE TMODEL_KEY=?");
    deleteSQL = sql.toString();

    // build selectSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("AUTHORIZED_NAME,");
    sql.append("OPERATOR,");
    sql.append("NAME,");
    sql.append("OVERVIEW_URL ");
    sql.append("FROM TMODEL ");
    sql.append("WHERE TMODEL_KEY=?");
    selectSQL = sql.toString();

    // build selectByPublisherSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("TMODEL_KEY ");
    sql.append("FROM TMODEL ");
    sql.append("WHERE PUBLISHER_ID=?");
    selectByPublisherSQL = sql.toString();

    // build verifyOwnershipSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("* ");
    sql.append("FROM TMODEL ");
    sql.append("WHERE TMODEL_KEY=? ");
    sql.append("AND PUBLISHER_ID=?");
    verifyOwnershipSQL = sql.toString();
  }

  /**
   * Insert new row into the TMODEL table.
   *
   * @param tModel TModel object holding values to be inserted
   * @param publisherID
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void insert(TModel tModel,String publisherID,Connection connection)
    throws java.sql.SQLException
  {
    PreparedStatement statement = null;
    Timestamp timeStamp = new Timestamp(System.currentTimeMillis());

    String overviewURL = null;
    if ((tModel.getOverviewDoc() != null) && (tModel.getOverviewDoc().getOverviewURL() != null))
      overviewURL = tModel.getOverviewDoc().getOverviewURL().getValue();

    try
    {
      statement = connection.prepareStatement(insertSQL);
      statement.setString(1,tModel.getTModelKey().toString());
      statement.setString(2,tModel.getAuthorizedName());
      statement.setString(3,publisherID);
      statement.setString(4,tModel.getOperator());
      statement.setString(5,tModel.getName());
      statement.setString(6,overviewURL);
      statement.setTimestamp(7,timeStamp);

      log.debug("insert into TMODEL table:\n\n\t" + insertSQL +
        "\n\t TMODEL_KEY=" + tModel.getTModelKey().toString() +
        "\n\t AUTHORIZED_NAME=" + tModel.getAuthorizedName() +
        "\n\t PUBLISHER_ID=" + publisherID +
        "\n\t OPERATOR=" + tModel.getOperator() +
        "\n\t NAME=" + tModel.getName() +
        "\n\t OVERVIEW_URL=" + overviewURL +
        "\n\t LAST_UPDATE=" + timeStamp.getTime() + "\n");

      // insert
      statement.executeUpdate();
    }
    finally
    {
      try {
        statement.close();
      }
      catch (Exception e) { /* ignored */ }
    }
  }

  /**
   * Delete row from the TMODEL table.
   *
   * @param  tModelKey
   * @param  connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void delete(String tModelKey,Connection connection)
    throws java.sql.SQLException
  {
    PreparedStatement statement = null;

    try
    {
      // prepare the delete
      statement = connection.prepareStatement(deleteSQL);
      statement.setString(1,tModelKey.toString());

      log.debug("delete from TMODEL table:\n\n\t" + deleteSQL +
        "\n\t TMODEL_KEY=" + tModelKey.toString() + "\n");

      // execute
      statement.executeUpdate();
    }
    finally
    {
      try {
        statement.close();
      }
      catch (Exception e) { /* ignored */ }
    }
  }

  /**
   * Select one row from the TMODEL table.
   *
   * @param  tModelKey
   * @param  connection
   * @throws java.sql.SQLException
   */
  public static TModel select(String tModelKey,Connection connection)
    throws java.sql.SQLException
  {
    TModel tModel = null;
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      statement = connection.prepareStatement(selectSQL);
      statement.setString(1,tModelKey.toString());

      log.debug("select from TMODEL table:\n\n\t" + selectSQL +
        "\n\t TMODEL_KEY=" + tModelKey.toString() + "\n");

      resultSet = statement.executeQuery();
      if (resultSet.next())
      {
        tModel = new TModel();
        tModel.setTModelKey(tModelKey);
        tModel.setAuthorizedName(resultSet.getString(1));//("AUTHORIZED_NAME"));
        tModel.setOperator(resultSet.getString(2));//("OPERATOR"));
        tModel.setName(resultSet.getString(3));//("NAME"));

        OverviewDoc overviewDoc = new OverviewDoc();
        overviewDoc.setOverviewURL(resultSet.getString(4));//("OVERVIEW_URL"));
        tModel.setOverviewDoc(overviewDoc);
      }

      return tModel;
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
   * Select all TModelKeys from the business_entities table for a given
   * 'publisherID' value.
   *
   * @param  publisherID The User ID of the TModel owner.
   * @param connection JDBC The JDBC connection
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
      statement.setString(1,publisherID.toString());

      log.debug("select from TMODEL table:\n\n\t" + selectByPublisherSQL +
        "\n\t PUBLISHER_ID=" + publisherID + "\n");

      // execute the statement
      resultSet = statement.executeQuery();
      while (resultSet.next())
        keyList.add(resultSet.getString(1));//("TMODEL_KEY"));

      return keyList;
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
   * Verify that 'publisherID' has the authority to update or delete
   * TModel identified by the tModelKey parameter
   *
   * @param  tModelKey
   * @param  publisherID
   * @param  connection
   * @throws java.sql.SQLException
   */
  public static boolean verifyOwnership(String tModelKey,String publisherID,Connection connection)
    throws java.sql.SQLException
  {
    if ((tModelKey == null) || (publisherID == null))
      return false;

    boolean authorized = false;
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      statement = connection.prepareStatement(verifyOwnershipSQL);
      statement.setString(1,tModelKey);
      statement.setString(2,publisherID);

      log.debug("checking ownership of TMODEL:\n\n\t" + verifyOwnershipSQL +
        "\n\t TMODEL_KEY=" + tModelKey +
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
        OverviewDoc overviewDoc = new OverviewDoc();
        overviewDoc.setOverviewURL("http://www.sviens.com/jtruss.html");

        String tModelKey = uuidgen.uuidgen();
        TModel tModel = new TModel();
        tModel.setTModelKey(tModelKey);
        tModel.setAuthorizedName("Steve Viens");
        tModel.setOperator("WebServiceRegistry.com");
        tModel.setName("Tuscany Web Service Company");
        tModel.setOverviewDoc(overviewDoc);

        String publisherID = "sviens";

        // begin a new transaction
        txn.begin(connection);

        // insert a new TModel
        TModelTable.insert(tModel,publisherID,connection);

        // select one of the TModel objects
        tModel = TModelTable.select(tModelKey,connection);

        // select a Collection of TModel keys by PublisherID
        TModelTable.selectByPublisherID(publisherID,connection);

        TModelTable.verifyOwnership(tModelKey,"mviens",connection);
        TModelTable.verifyOwnership(tModelKey,"sviens",connection);

        // delete that TModel object
        //TModelTable.delete(tModelKey,connection);

        // re-select that TModel object
        tModel = TModelTable.select(tModelKey,connection);

        // re-select a Collection of TModel keys by PublisherID
        TModelTable.selectByPublisherID(publisherID,connection);

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
