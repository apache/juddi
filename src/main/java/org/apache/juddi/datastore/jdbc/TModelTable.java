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
import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.OverviewDoc;
import org.apache.juddi.datatype.tmodel.TModel;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class TModelTable
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(TModelTable.class);

  static String insertSQL = null;
  static String deleteSQL = null;
  static String updateSQL = null;
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
    sql.append("LANG_CODE,");
    sql.append("OVERVIEW_URL,");
    sql.append("LAST_UPDATE) ");
    sql.append("VALUES (?,?,?,?,?,?,?,?)");
    insertSQL = sql.toString();

    // build deleteSQL
    sql = new StringBuffer(100);
    sql.append("DELETE FROM TMODEL ");
    sql.append("WHERE TMODEL_KEY=?");
    deleteSQL = sql.toString();

    // build updateSQL
    sql = new StringBuffer(100);
    sql.append("UPDATE TMODEL ");
    sql.append("SET DELETED='true' ");
    sql.append("WHERE TMODEL_KEY=?");
    updateSQL = sql.toString();

    // build selectSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("AUTHORIZED_NAME,");
    sql.append("OPERATOR,");
    sql.append("NAME,");
    sql.append("LANG_CODE,");
    sql.append("OVERVIEW_URL,");
    sql.append("DELETED ");
    sql.append("FROM TMODEL ");
    sql.append("WHERE TMODEL_KEY=? ");
    sql.append("AND DELETED IS NULL");
    selectSQL = sql.toString();

    // build selectByPublisherSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("TMODEL_KEY,");
    sql.append("DELETED ");
    sql.append("FROM TMODEL ");
    sql.append("WHERE PUBLISHER_ID=? ");
    sql.append("AND DELETED IS NULL");
    selectByPublisherSQL = sql.toString();

    // build verifyOwnershipSQL
    sql = new StringBuffer(200);
    sql.append("SELECT * ");
    sql.append("FROM TMODEL ");
    sql.append("WHERE TMODEL_KEY=? ");
    sql.append("AND PUBLISHER_ID=? " );
    sql.append("AND DELETED IS NULL");
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
      statement.setString(5,tModel.getName().getValue());
      statement.setString(6,tModel.getName().getLanguageCode());
      statement.setString(7,overviewURL);
      statement.setTimestamp(8,timeStamp);

      log.debug(insertSQL +
        "\n\t TMODEL_KEY=" + tModel.getTModelKey().toString() +
        "\n\t AUTHORIZED_NAME=" + tModel.getAuthorizedName() +
        "\n\t PUBLISHER_ID=" + publisherID +
        "\n\t OPERATOR=" + tModel.getOperator() +
        "\n\t NAME=" + tModel.getName().getValue() +
        "\n\t LANG_CODE=" + tModel.getName().getLanguageCode() +
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

      log.debug(deleteSQL +
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
   * Update the TMODEL table setting the value of the DELETED column to 'true'.
   *
   * @param  tModelKey
   * @param  connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void markAsDeleted(String tModelKey,Connection connection)
    throws java.sql.SQLException
  {
    PreparedStatement statement = null;

    try
    {
      // prepare the delete
      statement = connection.prepareStatement(updateSQL);
      statement.setString(1,tModelKey.toString());

      log.debug(updateSQL +
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

      log.debug(selectSQL +
        "\n\t TMODEL_KEY=" + tModelKey.toString() + "\n");

      resultSet = statement.executeQuery();
      if (resultSet.next())
      {
        tModel = new TModel();
        tModel.setTModelKey(tModelKey);
        tModel.setAuthorizedName(resultSet.getString(1));//("AUTHORIZED_NAME"));
        tModel.setOperator(resultSet.getString(2));//("OPERATOR"));

        Name name = new Name();
        name.setValue(resultSet.getString(3));//("NAME"));
        name.setLanguageCode(resultSet.getString(4));//("LANG_CODE"));

        tModel.setName(name);

        OverviewDoc overviewDoc = new OverviewDoc();
        overviewDoc.setOverviewURL(resultSet.getString(5));//("OVERVIEW_URL"));
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

      log.debug(selectByPublisherSQL +
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

      log.debug(verifyOwnershipSQL +
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
}
