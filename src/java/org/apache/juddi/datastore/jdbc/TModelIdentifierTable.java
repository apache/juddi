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
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.datatype.KeyedReference;
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
class TModelIdentifierTable
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(TModelIdentifierTable.class);

  static String insertSQL = null;
  static String selectSQL = null;
  static String deleteSQL = null;

  static {
    // buffer used to build SQL statements
    StringBuffer sql = null;

    // build insertSQL
    sql = new StringBuffer(150);
    sql.append("INSERT INTO TMODEL_IDENTIFIER (");
    sql.append("TMODEL_KEY,");
    sql.append("IDENTIFIER_ID,");
    sql.append("TMODEL_KEY_REF,");
    sql.append("KEY_NAME,");
    sql.append("KEY_VALUE) ");
    sql.append("VALUES (?,?,?,?,?)");
    insertSQL = sql.toString();

    // build selectSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("TMODEL_KEY_REF,");
    sql.append("KEY_NAME,");
    sql.append("KEY_VALUE, ");
    sql.append("IDENTIFIER_ID ");
    sql.append("FROM TMODEL_IDENTIFIER ");
    sql.append("WHERE TMODEL_KEY=? ");
    sql.append("ORDER BY IDENTIFIER_ID");
    selectSQL = sql.toString();

    // build deleteSQL
    sql = new StringBuffer(100);
    sql.append("DELETE FROM TMODEL_IDENTIFIER ");
    sql.append("WHERE TMODEL_KEY=?");
    deleteSQL = sql.toString();
  }

  /**
   * Insert new row into the TMODEL_IDENTIFIER table.
   *
   * @param tModelKey String to the parent TModelEntity object.
   * @param keyRefs A Vector of KeyedReference instances to insert.
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void insert(
    String tModelKey,
    Vector keyRefs,
    Connection connection)
    throws java.sql.SQLException
  {
    PreparedStatement statement = null;

    try
    {
      statement = connection.prepareStatement(insertSQL);
      statement.setString(1, tModelKey.toString());

      int listSize = keyRefs.size();
      for (int identifierID = 0; identifierID < listSize; identifierID++)
      {
        KeyedReference keyRef =
          (KeyedReference) keyRefs.elementAt(identifierID);

        // extract values to insert
        String tModelKeyValue = null;
        if (keyRef.getTModelKey() != null)
          tModelKeyValue = keyRef.getTModelKey().toString();

        // set the values
        statement.setInt(2, identifierID);
        statement.setString(3, tModelKeyValue);
        statement.setString(4, keyRef.getKeyName());
        statement.setString(5, keyRef.getKeyValue());

        log.debug(
          "insert into TMODEL_IDENTIFIER table:\n\n\t"
            + insertSQL
            + "\n\t BUSINESS_KEY="
            + tModelKey.toString()
            + "\n\t IDENTIFIER_ID="
            + identifierID
            + "\n\t TMODEL_KEY_REF="
            + tModelKeyValue
            + "\n\t KEY_NAME="
            + keyRef.getKeyName()
            + "\n\t KEY_VALUE="
            + keyRef.getKeyValue()
            + "\n");

        // insert!
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
   * Select all rows from the TMODEL_IDENTIFIER table for a given TModelKey.
   *
   * @param  tModelKey String
   * @param  connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector select(String tModelKey, Connection connection)
    throws java.sql.SQLException
  {
    Vector keyRefList = new Vector();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      // create a statement to query with
      statement = connection.prepareStatement(selectSQL);
      statement.setString(1, tModelKey.toString());

      log.debug(
        "select from TMODEL_IDENTIFIER table:\n\n\t"
          + selectSQL
          + "\n\t TMODEL_KEY="
          + tModelKey.toString()
          + "\n");

      // execute the statement
      resultSet = statement.executeQuery();
      while (resultSet.next())
      {
        KeyedReference keyRef = new KeyedReference();
        keyRef.setTModelKey(resultSet.getString(1));//("TMODEL_KEY_REF"));
        keyRef.setKeyName(resultSet.getString(2));//("KEY_NAME"));
        keyRef.setKeyValue(resultSet.getString(3));//("KEY_VALUE"));
        keyRefList.add(keyRef);
      }

      return keyRefList;
    }
    finally
    {
      try
      {
        resultSet.close();
        statement.close();
      }
      catch (Exception e)
      { /* ignored */
      }
    }
  }

  /**
   * Delete multiple rows from the TMODEL_IDENTIFIER table that are assigned to the
   * TModelKey specified.
   *
   * @param  tModelKey String
   * @param  connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void delete(String tModelKey, Connection connection)
    throws java.sql.SQLException
  {
    PreparedStatement statement = null;

    try
    {
      // prepare
      statement = connection.prepareStatement(deleteSQL);
      statement.setString(1, tModelKey.toString());

      log.debug(
        "delete from TMODEL_IDENTIFIER table:\n\n\t"
          + deleteSQL
          + "\n\t TMODEL_KEY="
          + tModelKey.toString()
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
        OverviewDoc overviewDoc = new OverviewDoc();
        overviewDoc.setOverviewURL(
          "http://www.steveviens.com/overviewdoc.html");

        String tModelKey = uuidgen.uuidgen();
        TModel tModel = new TModel();
        tModel.setTModelKey(tModelKey);
        tModel.setAuthorizedName("sviens");
        tModel.setOperator("WebServiceRegistry.com");
        tModel.setName("Tuscany Web Service Company");
        tModel.setOverviewDoc(overviewDoc);

        Vector keyRefs = new Vector();
        keyRefs.add(new KeyedReference(uuidgen.uuidgen(), "blah, blah, blah"));
        keyRefs.add(
          new KeyedReference(uuidgen.uuidgen(), "Yadda, Yadda, Yadda"));
        keyRefs.add(
          new KeyedReference(uuidgen.uuidgen(), "WhoobWhoobWhoobWhoob"));
        keyRefs.add(new KeyedReference(uuidgen.uuidgen(), "Haachachachacha"));

        String authorizedUserID = "sviens";

        // begin a new transaction
        txn.begin(connection);

        // insert a new TModel
        TModelTable.insert(tModel, authorizedUserID, connection);

        // insert a Collection of new Identifier KeyedReference objects
        TModelIdentifierTable.insert(tModelKey, keyRefs, connection);

        // insert another new TModel
        tModel.setTModelKey(uuidgen.uuidgen());
        TModelTable.insert(tModel, authorizedUserID, connection);

        // insert another Collection of new Identifier KeyedReference objects
        TModelIdentifierTable.insert(
          tModel.getTModelKey(),
          keyRefs,
          connection);

        // select a Collection of Identifier KeyedReference objects
        keyRefs = TModelIdentifierTable.select(tModelKey, connection);

        // delete a Collection of Identifier KeyedReference objects
        TModelIdentifierTable.delete(tModelKey, connection);

        // re-select a Collection of Identifier KeyedReference objects
        keyRefs = TModelIdentifierTable.select(tModelKey, connection);

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
