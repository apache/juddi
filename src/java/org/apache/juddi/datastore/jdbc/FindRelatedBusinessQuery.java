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
import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.ConnectionManager;
import org.apache.juddi.util.jdbc.Transaction;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class FindRelatedBusinessQuery
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(FindRelatedBusinessQuery.class);

  static String selectSQL;
  static String selectWithKeyedRefSQL;

  static
  {
    StringBuffer sql = null;

    // build selectSQL
    sql = new StringBuffer(300);
    sql.append("SELECT FROM_KEY,TO_KEY,TMODEL_KEY,KEY_NAME,KEY_VALUE ");
    sql.append("FROM PUBLISHER_ASSERTION ");
    sql.append("WHERE (FROM_KEY=? OR TO_KEY=?) ");
    sql.append("AND FROM_CHECK='true' ");
    sql.append("AND TO_CHECK='true'");
    selectSQL = sql.toString();

    // build selectWithKeyedRefSQL
    sql = new StringBuffer(300);
    sql.append("SELECT FROM_KEY,TO_KEY,TMODEL_KEY,KEY_NAME,KEY_VALUE ");
    sql.append("FROM PUBLISHER_ASSERTION ");
    sql.append("WHERE (FROM_KEY=? OR TO_KEY=?) ");
    sql.append("AND TMODEL_KEY=? ");
    sql.append("AND KEY_NAME=? ");
    sql.append("AND KEY_VALUE=? ");
    sql.append("AND FROM_CHECK='true' ");
    sql.append("AND TO_CHECK='true'");
    selectWithKeyedRefSQL = sql.toString();
  }

  /**
   * Return a Vector of business keys that - together with the business key
   * parameter passed in - represent a valid (ie: status:complete) PublisherAssertion.
   * This is done by inspecting both the FROM_KEY and TO_KEY values returned from the
   * query and adding the businessKey that IS NOT equal to the businessKey passed in.
   *
   * @param businessKey The BusinessKey to find relations for
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector select(String businessKey,Connection connection)
    throws java.sql.SQLException
  {
    Vector keysOut = new Vector();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      statement = connection.prepareStatement(selectSQL);
      statement.setString(1,businessKey.toString());
      statement.setString(2,businessKey.toString());

      log.debug("select from PUBLISHER_ASSERTION table:\n\n\t" + selectSQL +
        "\n\t FROM_KEY=" + businessKey+
        "\n\t TO_KEY=" + businessKey + "\n");

      resultSet = statement.executeQuery();
      while (resultSet.next())
      {
        String fromKey = resultSet.getString(1);//("FROM_KEY");
        String toKey = resultSet.getString(2);//("TO_KEY");

        if (!fromKey.equalsIgnoreCase(businessKey))
          keysOut.addElement(fromKey);
        else if (!toKey.equalsIgnoreCase(businessKey))
          keysOut.addElement(toKey);
      }

      return keysOut;
    }
    finally
    {
      try {
        resultSet.close();
      }
      catch (Exception e)
      {
        log.warn("An Exception was encountered while attempting to close " +
          "the Find BusinessEntity ResultSet: "+e.getMessage(),e);
      }

      try {
        statement.close();
      }
      catch (Exception e)
      {
        log.warn("An Exception was encountered while attempting to close " +
          "the Find BusinessEntity Statement: "+e.getMessage(),e);
      }
    }
  }

  /**
   * Return a Vector of business keys that - together with the business key
   * parameter passed in - represent a valid (ie: status:complete) PublisherAssertion.
   * This is done by inspecting both the FROM_KEY and TO_KEY values returned from the
   * query and adding the businessKey that IS NOT equal to the businessKey passed in.
   *
   * @param businessKey The BusinessKey to find relations for
   * @param keyedRef A KeyedReference instance to using when searching
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector selectWithKeyedRef(String businessKey,KeyedReference keyedRef,Connection connection)
    throws java.sql.SQLException
  {
    Vector keysOut = new Vector();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      statement = connection.prepareStatement(selectWithKeyedRefSQL);
      statement.setString(1,businessKey);
      statement.setString(2,businessKey);
      statement.setString(3,keyedRef.getTModelKey());
      statement.setString(4,keyedRef.getKeyName());
      statement.setString(5,keyedRef.getKeyValue());

      log.debug("select from PUBLISHER_ASSERTION table:\n\n\t" + selectWithKeyedRefSQL +
        "\n\t FROM_KEY=" + businessKey +
        "\n\t TO_KEY=" + businessKey +
        "\n\t TMODEL_KEY=" + keyedRef.getTModelKey() +
        "\n\t KEY_NAME=" + keyedRef.getKeyName() +
        "\n\t KEY_VALUE=" + keyedRef.getKeyValue() + "\n");

      resultSet = statement.executeQuery();
      while (resultSet.next())
      {
        String fromKey = resultSet.getString(1);//("FROM_KEY");
        String toKey = resultSet.getString(2);//("TO_KEY");

        if (!fromKey.equalsIgnoreCase(businessKey))
          keysOut.addElement(fromKey);
        else if (!toKey.equalsIgnoreCase(businessKey))
          keysOut.addElement(toKey);
      }

      if (keysOut.size() > 0)
        log.info("select successful, at least one matching row was found");
      else
        log.info("select executed successfully but no matching rows were found");

      return keysOut;
    }
    finally
    {
      try {
        resultSet.close();
      }
      catch (Exception e)
      {
        log.warn("An Exception was encountered while attempting to close " +
          "the Find BusinessEntity ResultSet: "+e.getMessage(),e);
      }

      try {
        statement.close();
      }
      catch (Exception e)
      {
        log.warn("An Exception was encountered while attempting to close " +
          "the Find BusinessEntity Statement: "+e.getMessage(),e);
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

  public static void test(Connection connection)
    throws Exception
  {
    Transaction txn = new Transaction();

    KeyedReference keyedRef = new KeyedReference();
    keyedRef.setTModelKey("");
    keyedRef.setKeyName("");
    keyedRef.setKeyValue("");

    if (connection != null)
    {
      try
      {
        // begin a new transaction
        txn.begin(connection);

        select("2a33d7d7-2b73-4de9-99cd-d4c51c186bce",connection);
        selectWithKeyedRef("2a33d7d7-2b73-4de9-99cd-d4c51c186bce",keyedRef,connection);

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