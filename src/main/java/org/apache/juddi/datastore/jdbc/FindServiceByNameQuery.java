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
import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.request.FindQualifiers;
import org.apache.juddi.util.jdbc.DynamicQuery;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class FindServiceByNameQuery
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(FindServiceByNameQuery.class);

  static String selectSQL;
  static
  {
    // build selectSQL
    StringBuffer sql = new StringBuffer(200);
    sql.append("SELECT S.SERVICE_KEY,S.LAST_UPDATE,N.NAME ");
    sql.append("FROM BUSINESS_SERVICE S,SERVICE_NAME N ");
    selectSQL = sql.toString();
  }

  /**
   * Select ...
   *
   * @param businessKey primary key value
   * @param names
   * @param keysIn
   * @param qualifiers
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector select(String businessKey,Vector names,Vector keysIn,FindQualifiers qualifiers,Connection connection)
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
    appendWhere(sql,businessKey,names,qualifiers);
    appendIn(sql,keysIn);
    appendOrderBy(sql,qualifiers);

    try
    {
      log.debug(sql.toString());

      statement = sql.buildPreparedStatement(connection);
      resultSet = statement.executeQuery();

      while (resultSet.next())
        keysOut.addElement(resultSet.getString(1));//("SERVICE_KEY"));

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
          "the Find BusinessService ResultSet: "+e.getMessage(),e);
      }

      try {
        statement.close();
      }
      catch (Exception e)
      {
        log.warn("An Exception was encountered while attempting to close " +
          "the Find BusinessService Statement: "+e.getMessage(),e);
      }
    }
  }

  /**
   *
   */
  private static void appendWhere(DynamicQuery sql,String businessKey,Vector names,FindQualifiers qualifiers)
  {
    sql.append("WHERE N.SERVICE_KEY = S.SERVICE_KEY ");

    // per UDDI v2.0 Programmers API Errata (pg. 14), businessKey is
    // is no longer a required attribute of the find_service server.
    if((businessKey != null) && (businessKey.length() > 0))
    {
      sql.append("AND S.BUSINESS_KEY = ? ");
      sql.addValue(businessKey);
    }

    if (names != null)
    {
      int nameSize = names.size();
      if (nameSize > 0)
      {
        sql.append("AND (");

        for (int i=0; i<nameSize; i++)
        {
          Name name = (Name)names.elementAt(i);
          String text = name.getValue();
          String lang = name.getLanguageCode();

          if ((text != null) && (text.length() > 0))
          {
            if (qualifiers == null) // default
            {
              sql.append("(UPPER(NAME) LIKE ?");
              sql.addValue(text.endsWith("%") ? text.toUpperCase() : text.toUpperCase()+"%");
            }
            else if ((qualifiers.caseSensitiveMatch) && (qualifiers.exactNameMatch))
            {
              sql.append("(NAME = ?");
              sql.addValue(text);
            }
            else if ((!qualifiers.caseSensitiveMatch) && (qualifiers.exactNameMatch))
            {
              sql.append("(UPPER(NAME) = ?");
              sql.addValue(text.toUpperCase());
            }
            else if ((qualifiers.caseSensitiveMatch) && (!qualifiers.exactNameMatch))
            {
              sql.append("(NAME LIKE ?");
              sql.addValue(text.endsWith("%") ? text : text+"%");
            }
            else if ((!qualifiers.caseSensitiveMatch) && (!qualifiers.exactNameMatch))
            {
              sql.append("(UPPER(NAME) LIKE ?");
              sql.addValue(text.endsWith("%") ? text.toUpperCase() : text.toUpperCase()+"%");
            }

            // If lang is "en" we'll need to match with "en", "en_US" or "en_UK"
            if ((lang != null) && (lang.length() > 0))
            {
              sql.append(" AND (UPPER(LANG_CODE) LIKE ?)");
              sql.addValue(lang.toUpperCase()+"%");
            }
            
            sql.append(")");

            if (i+1 < nameSize)
              sql.append(" OR ");
          }
        }
      }

      sql.append(") ");
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
  private static void appendIn(DynamicQuery sql,Vector keysIn)
  {
    if (keysIn == null)
      return;

    sql.append("AND S.SERVICE_KEY IN (");

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
      sql.append("N.NAME ASC,S.LAST_UPDATE DESC");
    }
    else if (qualifiers.sortByNameAsc || qualifiers.sortByNameDesc)
    {
      if (qualifiers.sortByDateAsc || qualifiers.sortByDateDesc) 
      {
        if (qualifiers.sortByNameAsc && qualifiers.sortByDateDesc)
          sql.append("N.NAME ASC,S.LAST_UPDATE DESC");
        else if (qualifiers.sortByNameAsc && qualifiers.sortByDateAsc)
          sql.append("N.NAME ASC,S.LAST_UPDATE ASC");
        else if (qualifiers.sortByNameDesc && qualifiers.sortByDateDesc)
          sql.append("N.NAME DESC,S.LAST_UPDATE DESC");
        else
          sql.append("N.NAME DESC,S.LAST_UPDATE ASC");
      } 
      else
      {
        if (qualifiers.sortByNameAsc)
          sql.append("N.NAME ASC,S.LAST_UPDATE DESC");
        else
          sql.append("N.NAME DESC,S.LAST_UPDATE DESC");
      }
    }
    else if (qualifiers.sortByDateAsc || qualifiers.sortByDateDesc)
    {
      if (qualifiers.sortByDateDesc)
        sql.append("S.LAST_UPDATE DESC,N.NAME ASC");
      else
        sql.append("S.LAST_UPDATE ASC,N.NAME ASC");
    }
  }
}
