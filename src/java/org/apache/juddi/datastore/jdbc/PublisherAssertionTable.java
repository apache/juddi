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
import org.apache.juddi.datatype.assertion.PublisherAssertion;
import org.apache.juddi.datatype.response.AssertionStatusItem;
import org.apache.juddi.datatype.response.CompletionStatus;
import org.apache.juddi.datatype.response.KeysOwned;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.ConnectionManager;
import org.apache.juddi.util.jdbc.Transaction;

/**
 * @author Steve Viens (steve@users.sourceforge.net)
 */
class PublisherAssertionTable
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(PublisherAssertionTable.class);

  static String insertSQL = null;
  static String selectSQL = null;
  static String deleteDeadAssertionsSQL = null;
  static String updateFromCheckSQL = null;
  static String updateToCheckSQL = null;
  static String updateFromCheckByFromKeySQL = null;
  static String updateToCheckByToKeySQL = null;
  static String selectAssertionsSQL = null;
  static String selectRelationships = null;

  static {
    // buffer used to build SQL statements
    StringBuffer sql = null;

    // build insertSQL
    sql = new StringBuffer(150);
    sql.append("INSERT INTO PUBLISHER_ASSERTION (");
    sql.append("FROM_KEY,");
    sql.append("TO_KEY,");
    sql.append("TMODEL_KEY,");
    sql.append("KEY_NAME,");
    sql.append("KEY_VALUE,");
    sql.append("FROM_CHECK,");
    sql.append("TO_CHECK) ");
    sql.append("VALUES (?,?,?,?,?,?,?)");
    insertSQL = sql.toString();

    // build selectSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("FROM_KEY,");
    sql.append("TO_KEY,");
    sql.append("TMODEL_KEY,");
    sql.append("KEY_NAME,");
    sql.append("KEY_VALUE ");
    sql.append("FROM PUBLISHER_ASSERTION ");
    sql.append("WHERE FROM_KEY=? ");
    sql.append("AND TO_KEY=? ");
    sql.append("AND TMODEL_KEY=? ");
    sql.append("AND KEY_NAME=? ");
    sql.append("AND KEY_VALUE=?");
    selectSQL = sql.toString();

    // build deleteDeadAssertionsSQL
    sql = new StringBuffer(200);
    sql.append("DELETE FROM PUBLISHER_ASSERTION ");
    sql.append("WHERE FROM_CHECK='false' ");
    sql.append("AND TO_CHECK='false'");
    deleteDeadAssertionsSQL = sql.toString();

    // build updateFromCheckSQL
    sql = new StringBuffer(200);
    sql.append("UPDATE PUBLISHER_ASSERTION ");
    sql.append("SET FROM_CHECK=? ");
    sql.append("WHERE FROM_KEY=? ");
    sql.append("AND TO_KEY=? ");
    sql.append("AND TMODEL_KEY=? ");
    sql.append("AND KEY_NAME=? ");
    sql.append("AND KEY_VALUE=?");
    updateFromCheckSQL = sql.toString();

    // build updateToCheckSQL
    sql = new StringBuffer(200);
    sql.append("UPDATE PUBLISHER_ASSERTION ");
    sql.append("SET TO_CHECK=? ");
    sql.append("WHERE FROM_KEY=? ");
    sql.append("AND TO_KEY=? ");
    sql.append("AND TMODEL_KEY=? ");
    sql.append("AND KEY_NAME=? ");
    sql.append("AND KEY_VALUE=?");
    updateToCheckSQL = sql.toString();

    // build updateFromCheckByFromKeySQL
    sql = new StringBuffer(200);
    sql.append("UPDATE PUBLISHER_ASSERTION ");
    sql.append("SET FROM_CHECK=? ");
    sql.append("WHERE FROM_KEY IN ");
    updateFromCheckByFromKeySQL = sql.toString();

    // build updateFromCheckByFromKeySQL
    sql = new StringBuffer(200);
    sql.append("UPDATE PUBLISHER_ASSERTION ");
    sql.append("SET TO_CHECK=? ");
    sql.append("WHERE TO_KEY IN ");
    updateFromCheckByFromKeySQL = sql.toString();

    // build selectAssertionsSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("FROM_KEY,");
    sql.append("TO_KEY,");
    sql.append("TMODEL_KEY,");
    sql.append("KEY_NAME,");
    sql.append("KEY_VALUE,");
    sql.append("FROM_CHECK,");
    sql.append("TO_CHECK ");
    sql.append("FROM PUBLISHER_ASSERTION ");
    selectAssertionsSQL = sql.toString();

    // build selectRelationships
    sql = new StringBuffer(200);
    sql.append("SELECT TMODEL_KEY,KEY_NAME,KEY_VALUE ");
    sql.append("FROM PUBLISHER_ASSERTION ");
    sql.append(
      "WHERE ((FROM_KEY = ? AND TO_KEY = ?) OR (FROM_KEY = ? AND TO_KEY = ?)) ");
    sql.append("AND FROM_CHECK = 'true' ");
    sql.append("AND TO_CHECK = 'true' ");
    selectRelationships = sql.toString();
  }

  /**
   * Insert new row into the PUBLISHER_ASSERTION table.
   *
   * @param assertion Publisher Assertion object holding values to be inserted
   * @param fromCheck boolean true if the FROM_KEY is owned by the individual 'adding' this assertion (otherwise false).
   * @param toCheck boolean true if the TO_KEY is owned by the individual 'adding' this assertion (otherwise false).
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void insert(
    PublisherAssertion assertion,
    boolean fromCheck,
    boolean toCheck,
    Connection connection)
    throws java.sql.SQLException
  {
    PreparedStatement statement = null;

    try
    {
      // prep insert values
      String tModelKey = null;
      String keyedRefName = null;
      String keyedRefValue = null;

      if (assertion.getKeyedReference() != null)
      {
        tModelKey = assertion.getKeyedReference().getTModelKey();
        keyedRefName = assertion.getKeyedReference().getKeyName();
        keyedRefValue = assertion.getKeyedReference().getKeyValue();
      }

      statement = connection.prepareStatement(insertSQL);
      statement.setString(1, assertion.getFromKey());
      statement.setString(2, assertion.getToKey());
      statement.setString(3, tModelKey);
      statement.setString(4, keyedRefName);
      statement.setString(5, keyedRefValue);
      statement.setString(6, String.valueOf(fromCheck));
      statement.setString(7, String.valueOf(toCheck));

      log.debug(
        "insert into PUBLISHER_ASSERTION table:\n\n\t"
          + insertSQL
          + "\n\t FROM_KEY="
          + assertion.getFromKey()
          + "\n\t TO_KEY="
          + assertion.getToKey()
          + "\n\t TMODEL_KEY="
          + tModelKey
          + "\n\t KEY_NAME="
          + keyedRefName
          + "\n\t KEY_VALUE="
          + keyedRefValue
          + "\n\t FROM_CHECK="
          + fromCheck
          + "\n\t TO_CHECK="
          + toCheck
          + "\n");

      // insert
      statement.executeUpdate();
    }
    catch (java.sql.SQLException sqlex)
    {
      log.error(sqlex.getMessage());
      throw sqlex;
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
   * Select one row from the PUBLISHER_ASSERTION table.
   *
   * @param assertionIn
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static PublisherAssertion select(
    PublisherAssertion assertionIn,
    Connection connection)
    throws java.sql.SQLException
  {
    PublisherAssertion assertionOut = null;
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      KeyedReference keyedRefIn = assertionIn.getKeyedReference();

      statement = connection.prepareStatement(selectSQL);
      statement.setString(1, assertionIn.getFromKey());
      statement.setString(2, assertionIn.getToKey());
      statement.setString(3, keyedRefIn.getTModelKey());
      statement.setString(4, keyedRefIn.getKeyName());
      statement.setString(5, keyedRefIn.getKeyValue());

      log.debug(
        "select from PUBLISHER_ASSERTION table:\n\n\t"
          + selectSQL
          + "\n\t FROM_KEY="
          + assertionIn.getFromKey()
          + "\n\t TO_KEY="
          + assertionIn.getToKey()
          + "\n\t TMODEL_KEY="
          + keyedRefIn.getTModelKey()
          + "\n\t KEY_NAME="
          + keyedRefIn.getKeyName()
          + "\n\t KEY_VALUE="
          + keyedRefIn.getKeyValue()
          + "\n");

      resultSet = statement.executeQuery();
      if (resultSet.next())
      {
        KeyedReference keyedRefOut = new KeyedReference();
        keyedRefOut.setKeyName(resultSet.getString(4)); //("KEY_NAME"));
        keyedRefOut.setKeyValue(resultSet.getString(5)); //("KEY_VALUE"));
        keyedRefOut.setTModelKey(resultSet.getString(3)); //("TMODEL_KEY"));

        assertionOut = new PublisherAssertion();
        assertionOut.setFromKey(resultSet.getString(1)); //("FROM_KEY"));
        assertionOut.setToKey(resultSet.getString(2)); //("TO_KEY"));
        assertionOut.setKeyedReference(keyedRefOut);
      }

      return assertionOut;
    }
    catch (java.sql.SQLException sqlex)
    {
      log.error(sqlex.getMessage());
      throw sqlex;
    }
    finally
    {
      try
      {
        resultSet.close();
      }
      catch (Exception e)
      { /* ignored */
      }
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
   * Delete row from the PUBLISHER_ASSERTION table.
   *
   * @throws java.sql.SQLException
   */
  public static void deleteDeadAssertions(Connection connection)
    throws java.sql.SQLException
  {
    PreparedStatement statement = null;

    try
    {
      // prepare the delete
      statement = connection.prepareStatement(deleteDeadAssertionsSQL);

      log.debug(
        "delete from PUBLISHER_ASSERTION table:\n\n\t"
          + deleteDeadAssertionsSQL
          + "\n");

      // execute
      statement.executeUpdate();
    }
    catch (java.sql.SQLException sqlex)
    {
      log.error(sqlex.getMessage());
      throw sqlex;
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
   * Update the FROM_CHECK column in the PUBLISHER_ASSERTION table for a
   * particular PublisherAssertion.
   *
   * @param  assertion The PublisherAssertion to update BusinessKey
   * @param  fromCheck The value to set the FROM_CHECK column to.
   * @param  connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void updateFromCheck(
    PublisherAssertion assertion,
    boolean fromCheck,
    Connection connection)
    throws java.sql.SQLException
  {
    KeyedReference keyedRef = assertion.getKeyedReference();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      log.debug(
        "update PUBLISHER_ASSERTION table:\n\n\t"
          + updateFromCheckSQL
          + "\n\t FROM_CHECK="
          + String.valueOf(fromCheck)
          + "\n\t FROM_KEY="
          + assertion.getFromKey()
          + "\n\t TO_KEY="
          + assertion.getToKey()
          + "\n\t TMODEL_KEY="
          + keyedRef.getTModelKey()
          + "\n\t KEY_NAME="
          + keyedRef.getKeyName()
          + "\n\t KEY_VALUE="
          + keyedRef.getKeyValue()
          + "\n");

      // create a statement to query with
      statement = connection.prepareStatement(updateFromCheckSQL);
      statement.setString(1, String.valueOf(fromCheck));
      statement.setString(2, assertion.getFromKey());
      statement.setString(3, assertion.getToKey());
      statement.setString(4, keyedRef.getTModelKey());
      statement.setString(5, keyedRef.getKeyName());
      statement.setString(6, keyedRef.getKeyValue());

      // execute
      statement.executeUpdate();
    }
    catch (java.sql.SQLException sqlex)
    {
      log.error(sqlex.getMessage());
      throw sqlex;
    }
    finally
    {
      try
      {
        resultSet.close();
      }
      catch (Exception e)
      { /* ignored */
      }
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
   * Update the TO_CHECK column in the PUBLISHER_ASSERTION table
   * for a particular PublisherAssertion.
   *
   * @param assertion The PublisherAssertion to update BusinessKey
   * @param toCheck The value to set the TO_CHECK column to.
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void updateToCheck(
    PublisherAssertion assertion,
    boolean toCheck,
    Connection connection)
    throws java.sql.SQLException
  {
    KeyedReference keyedRef = assertion.getKeyedReference();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      log.debug(
        "update PUBLISHER_ASSERTION table:\n\n\t"
          + updateToCheckSQL
          + "\n\t TO_CHECK="
          + String.valueOf(toCheck)
          + "\n\t FROM_KEY="
          + assertion.getFromKey()
          + "\n\t TO_KEY="
          + assertion.getToKey()
          + "\n\t TMODEL_KEY="
          + keyedRef.getTModelKey()
          + "\n\t KEY_NAME="
          + keyedRef.getKeyName()
          + "\n\t KEY_VALUE="
          + keyedRef.getKeyValue()
          + "\n");

      // create a statement to query with
      statement = connection.prepareStatement(updateToCheckSQL);
      statement.setString(1, String.valueOf(toCheck));
      statement.setString(2, assertion.getFromKey());
      statement.setString(3, assertion.getToKey());
      statement.setString(4, keyedRef.getTModelKey());
      statement.setString(5, keyedRef.getKeyName());
      statement.setString(6, keyedRef.getKeyValue());

      // execute
      statement.executeUpdate();
    }
    catch (java.sql.SQLException sqlex)
    {
      log.error(sqlex.getMessage());
      throw sqlex;
    }
    finally
    {
      try
      {
        resultSet.close();
      }
      catch (Exception e)
      { /* ignored */
      }
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
   * Update the FROM_CHECK column for all rows from in the PUBLISHER_ASSERTION
   * table whose FROM_KEY is in the Vector of BusinessKeys passed in.
   *
   * @param  fromKeysIn A Vector of BusinessKeys to update
   * @param  fromCheck The value to set the FROM_CHECK column to
   * @param  connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void updateFromCheckByFromKey(
    Vector fromKeysIn,
    boolean fromCheck,
    Connection connection)
    throws java.sql.SQLException
  {
    StringBuffer sql = new StringBuffer();
    sql.append(updateFromCheckByFromKeySQL);
    sql.append("WHERE FROM_KEY IN ");
    appendIn(sql, fromKeysIn);

    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      // prepare
      statement = connection.prepareStatement(sql.toString());
      statement.setString(1, String.valueOf(fromCheck));

      log.debug(
        "update PUBLISHER_ASSERTION table:\n\n\t" + sql.toString() + "\n");

      // execute
      statement.executeUpdate();
    }
    catch (java.sql.SQLException sqlex)
    {
      log.error(sqlex.getMessage());
      throw sqlex;
    }
    finally
    {
      try
      {
        resultSet.close();
      }
      catch (Exception e)
      { /* ignored */
      }
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
   * Update the TO_CHECK column for all rows from in the PUBLISHER_ASSERTION
   * table whose TO_KEY is in the Vector of BusinessKeys passed in.
   *
   * @param toKeysIn A Vector of BusinessKeys to update
   * @param toCheck The value to set the TO_KEY column to
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void updateToCheckByToKey(
    Vector toKeysIn,
    boolean toCheck,
    Connection connection)
    throws java.sql.SQLException
  {
    StringBuffer sql = new StringBuffer();
    sql.append(updateFromCheckByFromKeySQL);
    sql.append("WHERE TO_KEY IN ");
    appendIn(sql, toKeysIn);

    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      // create a statement to query with
      statement = connection.prepareStatement(sql.toString());
      statement.setString(1, String.valueOf(toCheck));

      log.debug(
        "update PUBLISHER_ASSERTION table:\n\n\t" + sql.toString() + "\n");

      // execute
      statement.executeUpdate();
    }
    catch (java.sql.SQLException sqlex)
    {
      log.error(sqlex.getMessage());
      throw sqlex;
    }
    finally
    {
      try
      {
        resultSet.close();
      }
      catch (Exception e)
      { /* ignored */
      }
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
   * Select any rows from the PUBLISHER_ASSERTION table where the FROM_KEY
   * or TO_KEY column value is found in the Vector of BusinessKeys passed in
   * and return the results as a Vector of assertionStatusItem instances.
   *
   * The assertionStatusItems returned represent PublisherAssertions in
   * which the fromKey and toKey are both are under the control of a
   * particular Publisher.
   *
   * NOTE: Each AssertionStatusItem returned from this method will have a
   *       completion stauts of 'status:complete' because only assertions
   *       in which both business entities are managed (was published) by
   *       same publisher.
   *
   * @param keysIn Vector business keys to look for in the FROM_KEY and TO_KEY column.
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector selectBothKeysOwnedAssertion(
    Vector keysIn,
    Connection connection)
    throws java.sql.SQLException
  {
    if ((keysIn == null) || (keysIn.size() == 0))
      return null;

    StringBuffer sql = new StringBuffer();
    sql.append(selectAssertionsSQL);
    sql.append("WHERE FROM_KEY IN ");
    appendIn(sql, keysIn);
    sql.append("AND TO_KEY IN ");
    appendIn(sql, keysIn);

    Vector itemList = new Vector();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      statement = connection.prepareStatement(sql.toString());

      log.debug(
        "select from PUBLISHER_ASSERTION table:\n\n\t" + selectSQL + "\n");

      resultSet = statement.executeQuery();
      while (resultSet.next())
      {
        AssertionStatusItem item = new AssertionStatusItem();
        item.setFromKey(resultSet.getString(1)); //("FROM_KEY"));
        item.setToKey(resultSet.getString(2)); //("TO_KEY"));

        // construct and set the KeyedReference instance
        KeyedReference keyedRef = new KeyedReference();
        keyedRef.setTModelKey(resultSet.getString(3)); //("TMODEL_KEY"));
        keyedRef.setKeyName(resultSet.getString(4)); //("KEY_NAME"));
        keyedRef.setKeyValue(resultSet.getString(5)); //("KEY_VALUE"));
        item.setKeyedReference(keyedRef);

        // construct and set the KeysOwned instance
        KeysOwned keysOwned = new KeysOwned();
        keysOwned.setFromKey(item.getFromKey());
        keysOwned.setToKey(item.getToKey());
        item.setKeysOwned(keysOwned);

        // determine & set the 'completionStatus' (always 'status:complete' here)
        item.setCompletionStatus(
          new CompletionStatus(CompletionStatus.COMPLETE));

        // add the assertionStatusItem
        itemList.addElement(item);
      }

      return itemList;
    }
    catch (java.sql.SQLException sqlex)
    {
      log.error(sqlex.getMessage());
      throw sqlex;
    }
    finally
    {
      try
      {
        resultSet.close();
      }
      catch (Exception e)
      { /* ignored */
      }
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
   * Select any rows from the PUBLISHER_ASSERTION table where the FROM_KEY column
   * DOES CONTAIN one of the business keys found in the Vector of keys passed in
   * and the TO_KEY column DOES NOT CONTAIN one of the business keys from the same
   * Vector of keys. Return the results as a Vector of assertionStatusItem instances.
   *
   * The assertionStatusItems returned represent PublisherAssertions in
   * which ONLY the "fromKey" is under the control of a particular Publisher.

   * @param keysIn Vector business keys to look for in the FROM_KEY and TO_KEY column.
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector selectFromKeyOwnedAssertion(
    Vector keysIn,
    Connection connection)
    throws java.sql.SQLException
  {
    if ((keysIn == null) || (keysIn.size() == 0))
      return null;

    StringBuffer sql = new StringBuffer();
    sql.append(selectAssertionsSQL);
    sql.append("WHERE FROM_KEY IN ");
    appendIn(sql, keysIn);
    sql.append("AND TO_KEY NOT IN ");
    appendIn(sql, keysIn);

    Vector itemList = new Vector();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      statement = connection.prepareStatement(sql.toString());

      log.debug(
        "select from PUBLISHER_ASSERTION table:\n\n\t" + selectSQL + "\n");

      resultSet = statement.executeQuery();
      while (resultSet.next())
      {
        AssertionStatusItem item = new AssertionStatusItem();
        item.setFromKey(resultSet.getString(1)); //("FROM_KEY"));
        item.setToKey(resultSet.getString(2)); //("TO_KEY"));

        // construct and set the KeyedReference instance
        KeyedReference keyedRef = new KeyedReference();
        keyedRef.setTModelKey(resultSet.getString(3)); //("TMODEL_KEY"));
        keyedRef.setKeyName(resultSet.getString(4)); //("KEY_NAME"));
        keyedRef.setKeyValue(resultSet.getString(5)); //("KEY_VALUE"));
        item.setKeyedReference(keyedRef);

        // construct and set the KeysOwned instance
        KeysOwned keysOwned = new KeysOwned();
        keysOwned.setFromKey(item.getFromKey());
        keysOwned.setToKey(null);
        item.setKeysOwned(keysOwned);

        // determine and set the assertions 'completionStatus'
        CompletionStatus status = null;
        boolean fromCheck =
          new Boolean(resultSet.getString(6)).booleanValue();//("FROM_CHECK")
        boolean toCheck =
          new Boolean(resultSet.getString(7)).booleanValue();//("TO_CHECK")
        if ((fromCheck) && (toCheck))
          status = new CompletionStatus(CompletionStatus.COMPLETE);
        else if ((fromCheck) && (!toCheck))
          status = new CompletionStatus(CompletionStatus.TOKEY_INCOMPLETE);
        else if ((!fromCheck) && (toCheck))
          status = new CompletionStatus(CompletionStatus.FROMKEY_INCOMPLETE);
        item.setCompletionStatus(status);

        // add the assertionStatusItem
        itemList.addElement(item);
      }

      return itemList;
    }
    catch (java.sql.SQLException sqlex)
    {
      log.error(sqlex.getMessage());
      throw sqlex;
    }
    finally
    {
      try
      {
        resultSet.close();
      }
      catch (Exception e)
      { /* ignored */
      }
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
   * Select any rows from the PUBLISHER_ASSERTION table where the FROM_KEY column
   * DOES NOT CONTAIN one of the business keys found in the Vector of keys passed
   * in and the TO_KEY column DOES CONTAIN one of the business keys from the same
   * Vector of keys. Return the results as a Vector of assertionStatusItem instances.
   *
   * The assertionStatusItems returned represent PublisherAssertions in
   * which ONLY the "toKey" is under the control of a particular Publisher.
   *
   * @param keysIn Vector business keys to look for in the FROM_KEY and TO_KEY column.
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector selectToKeyOwnedAssertion(
    Vector keysIn,
    Connection connection)
    throws java.sql.SQLException
  {
    if ((keysIn == null) || (keysIn.size() == 0))
      return null;

    StringBuffer sql = new StringBuffer();
    sql.append(selectAssertionsSQL);
    sql.append("WHERE FROM_KEY NOT IN ");
    appendIn(sql, keysIn);
    sql.append("AND TO_KEY IN ");
    appendIn(sql, keysIn);

    Vector itemList = new Vector();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      statement = connection.prepareStatement(sql.toString());

      log.debug(
        "select from PUBLISHER_ASSERTION table:\n\n\t" + selectSQL + "\n");

      resultSet = statement.executeQuery();
      while (resultSet.next())
      {
        AssertionStatusItem item = new AssertionStatusItem();
        item.setFromKey(resultSet.getString(1));//("FROM_KEY"));
        item.setToKey(resultSet.getString(2));//("TO_KEY"));

        // construct and set the KeyedReference instance
        KeyedReference keyedRef = new KeyedReference();
        keyedRef.setKeyName(resultSet.getString(4));//("KEY_NAME"));
        keyedRef.setKeyValue(resultSet.getString(5));//("KEY_VALUE"));
        keyedRef.setTModelKey(resultSet.getString(3));//("TMODEL_KEY"));
        item.setKeyedReference(keyedRef);

        // construct and set the KeysOwned instance
        KeysOwned keysOwned = new KeysOwned();
        keysOwned.setFromKey(null);
        keysOwned.setToKey(item.getToKey());
        item.setKeysOwned(keysOwned);

        // determine and set the assertions 'completionStatus'
        CompletionStatus status = null;
        boolean fromCheck =
          new Boolean(resultSet.getString(6)).booleanValue();//("FROM_CHECK"));
        boolean toCheck =
          new Boolean(resultSet.getString(7)).booleanValue();//("TO_CHECK"));
        if ((fromCheck) && (toCheck))
          status = new CompletionStatus(CompletionStatus.COMPLETE);
        else if ((fromCheck) && (!toCheck))
          status = new CompletionStatus(CompletionStatus.TOKEY_INCOMPLETE);
        else if ((!fromCheck) && (toCheck))
          status = new CompletionStatus(CompletionStatus.FROMKEY_INCOMPLETE);
        item.setCompletionStatus(status);

        // add the assertionStatusItem
        itemList.addElement(item);
      }

      return itemList;
    }
    catch (java.sql.SQLException sqlex)
    {
      log.error(sqlex.getMessage());
      throw sqlex;
    }
    finally
    {
      try
      {
        resultSet.close();
      }
      catch (Exception e)
      { /* ignored */
      }
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
   * Select any rows from the PUBLISHER_ASSERTION table where the FROM_KEY column
   * CONTAINS one of the business keys found in the Vector of keys passed in OR
   * the TO_KEY column CONTAINS one of the business keys from the same Vector
   * f keys. Return the results as a Vector of PublisherAssertion instances.
   *
   * @param keysIn Vector business keys to look for in the FROM_KEY and TO_KEY column.
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector selectAssertions(Vector keysIn, Connection connection)
    throws java.sql.SQLException
  {
    if ((keysIn == null) || (keysIn.size() == 0))
      return null;

    StringBuffer sql = new StringBuffer();
    sql.append(selectAssertionsSQL);
    sql.append("WHERE (FROM_KEY IN ");
    appendIn(sql, keysIn);
    sql.append("AND FROM_CHECK = 'true') ");
    sql.append("OR (TO_KEY IN ");
    appendIn(sql, keysIn);
    sql.append("AND TO_CHECK = 'true')");

    Vector assertionList = new Vector();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      statement = connection.prepareStatement(sql.toString());

      log.debug(
        "select from PUBLISHER_ASSERTION table:\n\n\t" + selectSQL + "\n");

      resultSet = statement.executeQuery();
      while (resultSet.next())
      {
        PublisherAssertion assertion = new PublisherAssertion();
        assertion.setFromKey(resultSet.getString(1));//("FROM_KEY"));
        assertion.setToKey(resultSet.getString(2));//("TO_KEY"));

        // construct and set the KeyedReference instance
        KeyedReference keyedRef = new KeyedReference();
        keyedRef.setKeyName(resultSet.getString(4));//("KEY_NAME"));
        keyedRef.setKeyValue(resultSet.getString(5));//("KEY_VALUE"));
        keyedRef.setTModelKey(resultSet.getString(3));//("TMODEL_KEY"));
        assertion.setKeyedReference(keyedRef);

        // add the assertionStatusItem
        assertionList.addElement(assertion);
      }

      return assertionList;
    }
    catch (java.sql.SQLException sqlex)
    {
      log.error(sqlex.getMessage());
      throw sqlex;
    }
    finally
    {
      try
      {
        resultSet.close();
      }
      catch (Exception e)
      { /* ignored */
      }
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
   * Retrieve the TMODEL_KEY, KEY_NAME and KEY_VALUE from all assertions
   * where the FROM_KEY = businessKey and the TO_KEY = relatedBusinessKey
   * parameters or the FROM_KEY = relatedBusinessKey and the TO_KEY =
   * businessKey.
   *
   * @param businessKey The BusinessKey we're searching for relationships to.
   * @param relatedKey The BusinessKey of the related BusinessEntity.
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector selectRelatedBusinesses(
    String businessKey,
    String relatedKey,
    Connection connection)
    throws java.sql.SQLException
  {
    Vector refList = new Vector();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      statement = connection.prepareStatement(selectRelationships);
      statement.setString(1, businessKey);
      statement.setString(2, relatedKey);
      statement.setString(3, relatedKey);
      statement.setString(4, businessKey);

      log.debug(
        "select from PUBLISHER_ASSERTION table:\n\n\t"
          + selectRelationships
          + "\n\t BUSINESS_KEY="
          + businessKey.toString()
          + "\n\t RELATED_BUSINESS_KEY="
          + relatedKey.toString()
          + "\n");

      resultSet = statement.executeQuery();
      if (resultSet.next())
      {
        KeyedReference keyedRef = new KeyedReference();
        keyedRef.setKeyName(resultSet.getString(2));//("KEY_NAME"));
        keyedRef.setKeyValue(resultSet.getString(3));//("KEY_VALUE"));
        keyedRef.setTModelKey(resultSet.getString(1));//("TMODEL_KEY"));

        // add the KeyedRef to the Vector
        refList.addElement(keyedRef);
      }

      return refList;
    }
    catch (java.sql.SQLException sqlex)
    {
      log.error(sqlex.getMessage());
      throw sqlex;
    }
    finally
    {
      try
      {
        resultSet.close();
      }
      catch (Exception e)
      { /* ignored */
      }
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
   * Utility method used to construct SQL "IN" statements such as
   * the following SQL example:
   *
   *   SELECT * FROM TABLE WHERE MONTH IN ('jan','feb','mar')
   *
   * @param sql StringBuffer to append the final results to
   * @param keysIn Vector of Strings used to construct the "IN" clause
   */
  private static void appendIn(StringBuffer sql, Vector keysIn)
  {
    if (keysIn == null)
      return;

    sql.append("(");

    int keyCount = keysIn.size();
    for (int i = 0; i < keyCount; i++)
    {
      String key = (String) keysIn.elementAt(i);
      sql.append("'").append(key).append("'");

      if ((i + 1) < keyCount)
        sql.append(",");
    }

    sql.append(") ");
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

    if (connection != null)
    {
      try
      {
        // begin a new transaction
        txn.begin(connection);

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
