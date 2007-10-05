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
import org.apache.juddi.datatype.request.FindQualifiers;
import org.apache.juddi.registry.RegistryEngine;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.DynamicQuery;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class FindTModelByNameQuery
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(FindTModelByNameQuery.class);

  static String selectSQL;
  static String tablePrefix="";
  static
  {
   tablePrefix = Config.getStringProperty(
       RegistryEngine.PROPNAME_TABLE_PREFIX,RegistryEngine.DEFAULT_TABLE_PREFIX);
    // build selectSQL
    StringBuffer sql = new StringBuffer(200);
    sql.append("SELECT M.TMODEL_KEY,M.LAST_UPDATE,M.NAME,M.DELETED ");
    sql.append("FROM ").append(tablePrefix).append("TMODEL M ");
    selectSQL = sql.toString();
  }

  /**
   * Select ...
   *
   * @param name primary key value
   * @param keysIn primary key value
   * @param qualifiers primary key value
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector select(String name,Vector keysIn,FindQualifiers qualifiers,Connection connection)
    throws java.sql.SQLException
  {
    // if there is a keysIn vector but it doesn't contain
    // any keys then the previous query has exhausted
    // all possibilities of a match so skip this call.
    if ((keysIn != null) && (keysIn.size() == 0))
      return keysIn;

    Vector keysOut = new Vector();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    // construct the SQL statement
    DynamicQuery sql = new DynamicQuery(selectSQL);
    appendWhere(sql,name,qualifiers);
    appendIn(sql,name,keysIn);
    appendOrderBy(sql,qualifiers);

    try
    {
      log.debug(sql.toString());

      statement = sql.buildPreparedStatement(connection);
      resultSet = statement.executeQuery();

      while (resultSet.next())
        keysOut.addElement(resultSet.getString(1));//("TMODEL_KEY"));

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
          "the Find TModel ResultSet: "+e.getMessage(),e);
      }

      try {
        statement.close();
      }
      catch (Exception e)
      {
        log.warn("An Exception was encountered while attempting to close " +
          "the Find TModel Statement: "+e.getMessage(),e);
      }
    }
  }

  /**
   *
   */
  private static void appendWhere(DynamicQuery sql,String name,FindQualifiers qualifiers)
  {
    if ((name == null) || (name.length() == 0))
      return;

    // When TModels are deleted they are only marked as
    // deleted they're not actually removed from the database.
    //
    sql.append("WHERE M.DELETED IS NULL ");

    if ((qualifiers != null) && (qualifiers.exactNameMatch))
    {
      sql.append("AND M.NAME = ? ");
      sql.addValue(name);
    }
    else
    {
      sql.append("AND M.NAME LIKE ? ");
      sql.addValue(name.endsWith("%") ? name : name+"%");
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
  private static void appendIn(DynamicQuery sql,String name,Vector keysIn)
  {
    if (keysIn == null)
      return;

    if ((name == null) || (name.length() == 0))
      sql.append("WHERE M.TMODEL_KEY IN (");
    else
      sql.append("AND M.TMODEL_KEY IN (");

    int keyCount = keysIn.size();
    for (int i=0; i<keyCount; i++)
    {
      String key = (String)keysIn.elementAt(i);
      sql.append("?");
      sql.addValue(key);

      if ((i+1) < keyCount)
        sql.append(",");
    }

    sql.append(") ");
  }

  /**
   *
   */
  private static void appendOrderBy(DynamicQuery sql,FindQualifiers qualifiers)
  {
    sql.append("ORDER BY ");

    if ((qualifiers == null) ||
       ((!qualifiers.sortByNameAsc) && (!qualifiers.sortByNameDesc) &&
        (!qualifiers.sortByDateAsc) && (!qualifiers.sortByDateDesc)))
    {
      sql.append("M.NAME ASC,M.LAST_UPDATE DESC");
    }
    else if (qualifiers.sortByNameAsc || qualifiers.sortByNameDesc)
    {
      if (qualifiers.sortByDateAsc || qualifiers.sortByDateDesc)
      {
        if (qualifiers.sortByNameAsc && qualifiers.sortByDateDesc)
          sql.append("M.NAME ASC,M.LAST_UPDATE DESC");
        else if (qualifiers.sortByNameAsc && qualifiers.sortByDateAsc)
          sql.append("M.NAME ASC,M.LAST_UPDATE ASC");
        else if (qualifiers.sortByNameDesc && qualifiers.sortByDateDesc)
          sql.append("M.NAME DESC,M.LAST_UPDATE DESC");
        else
          sql.append("M.NAME DESC,M.LAST_UPDATE ASC");
      }
      else
      {
        if (qualifiers.sortByNameAsc)
          sql.append("M.NAME ASC,M.LAST_UPDATE DESC");
        else
          sql.append("M.NAME DESC,M.LAST_UPDATE DESC");
      }
    }
    else if (qualifiers.sortByDateAsc || qualifiers.sortByDateDesc)
    {
      if (qualifiers.sortByDateDesc)
        sql.append("M.LAST_UPDATE DESC,M.NAME ASC");
      else
        sql.append("M.LAST_UPDATE ASC,M.NAME ASC");
    }
  }
}
