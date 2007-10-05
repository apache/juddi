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
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.registry.RegistryEngine;
import org.apache.juddi.util.Config;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class FindRelatedBusinessQuery
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(FindRelatedBusinessQuery.class);

  static String selectSQL;
  static String selectWithKeyedRefSQL;

  static String tablePrefix;
  static
  {
   tablePrefix = Config.getStringProperty(
        RegistryEngine.PROPNAME_TABLE_PREFIX,RegistryEngine.DEFAULT_TABLE_PREFIX);
    StringBuffer sql = null;

    // build selectSQL
    sql = new StringBuffer(300);
    sql.append("SELECT FROM_KEY,TO_KEY,TMODEL_KEY,KEY_NAME,KEY_VALUE ");
    sql.append("FROM ").append(tablePrefix).append("PUBLISHER_ASSERTION ");
    sql.append("WHERE (FROM_KEY=? OR TO_KEY=?) ");
    sql.append("AND FROM_CHECK='true' ");
    sql.append("AND TO_CHECK='true'");
    selectSQL = sql.toString();

    // build selectWithKeyedRefSQL
    sql = new StringBuffer(300);
    sql.append("SELECT FROM_KEY,TO_KEY,TMODEL_KEY,KEY_NAME,KEY_VALUE ");
    sql.append("FROM ").append(tablePrefix).append("PUBLISHER_ASSERTION ");
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

      if (log.isDebugEnabled()) {
          log.debug("select from " + tablePrefix + "PUBLISHER_ASSERTION table:\n\n\t" + selectSQL +
            "\n\t FROM_KEY=" + businessKey+
            "\n\t TO_KEY=" + businessKey + "\n");
      }

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

      if (log.isDebugEnabled()) {
          log.debug("select from " + tablePrefix + "PUBLISHER_ASSERTION table:\n\n\t" + selectWithKeyedRefSQL +
            "\n\t FROM_KEY=" + businessKey +
            "\n\t TO_KEY=" + businessKey +
            "\n\t TMODEL_KEY=" + keyedRef.getTModelKey() +
            "\n\t KEY_NAME=" + keyedRef.getKeyName() +
            "\n\t KEY_VALUE=" + keyedRef.getKeyValue() + "\n");
      }

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
}