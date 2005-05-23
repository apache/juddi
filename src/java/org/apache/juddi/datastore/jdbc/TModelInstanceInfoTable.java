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
import org.apache.juddi.datatype.OverviewDoc;
import org.apache.juddi.datatype.binding.InstanceDetails;
import org.apache.juddi.datatype.binding.InstanceParms;
import org.apache.juddi.datatype.binding.TModelInstanceInfo;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class TModelInstanceInfoTable
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(TModelInstanceInfoTable.class);

  static String insertSQL = null;
  static String selectSQL = null;
  static String deleteSQL = null;

  static {
    // buffer used to build SQL statements
    StringBuffer sql = null;

    // build insertSQL
    sql = new StringBuffer(150);
    sql.append("INSERT INTO TMODEL_INSTANCE_INFO (");
    sql.append("BINDING_KEY,");
    sql.append("TMODEL_INSTANCE_INFO_ID,");
    sql.append("TMODEL_KEY, ");
    sql.append("OVERVIEW_URL,");
    sql.append("INSTANCE_PARMS) ");
    sql.append("VALUES (?,?,?,?,?)");
    insertSQL = sql.toString();

    // build selectSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("TMODEL_KEY,");
    sql.append("OVERVIEW_URL,");
    sql.append("INSTANCE_PARMS, ");
    sql.append("TMODEL_INSTANCE_INFO_ID ");
    sql.append("FROM TMODEL_INSTANCE_INFO ");
    sql.append("WHERE BINDING_KEY=? ");
    sql.append("ORDER BY TMODEL_INSTANCE_INFO_ID");
    selectSQL = sql.toString();

    // build deleteSQL
    sql = new StringBuffer(100);
    sql.append("DELETE FROM TMODEL_INSTANCE_INFO ");
    sql.append("WHERE BINDING_KEY=?");
    deleteSQL = sql.toString();
  }

  /**
   * Insert new row into the TMODEL_INSTANCE_INFO table.
   *
   * @param bindingKey String to the BusinessEntity object that owns the Contact to be inserted
   * @param infoList Vector of Contact objects holding values to be inserted
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void insert(
    String bindingKey,
    Vector infoList,
    Connection connection)
    throws java.sql.SQLException
  {
    if ((infoList == null) || (infoList.size() == 0))
      return; // everything is valid but no elements to insert

    PreparedStatement statement = null;

    try
    {
      statement = connection.prepareStatement(insertSQL);
      statement.setString(1, bindingKey.toString());

      int listSize = infoList.size();
      for (int infoID = 0; infoID < listSize; infoID++)
      {
        String tModelKey = null;
        String overURL = null;
        String instParms = null;

        TModelInstanceInfo info =
          (TModelInstanceInfo) infoList.elementAt(infoID);
        if (info.getTModelKey() != null)
          tModelKey = info.getTModelKey().toString();

        InstanceDetails details = info.getInstanceDetails();
        if (details != null)
        {
          if (details.getOverviewDoc() != null)
            overURL = details.getOverviewDoc().getOverviewURLString();

          if (details.getInstanceParms() != null)
            instParms = details.getInstanceParms().getValue();
        }

        // insert sequence number
        statement.setInt(2, infoID);
        statement.setString(3, tModelKey);
        statement.setString(4, overURL);
        statement.setString(5, instParms);

        log.debug(
          "insert into TMODEL_INSTANCE_INFO table:\n\n\t"
            + insertSQL
            + "\n\t BINDING_KEY="
            + bindingKey.toString()
            + "\n\t TMODEL_INSTANCE_INFO_ID="
            + infoID
            + "\n\t TMODEL_KEY="
            + tModelKey
            + "\n\t OVERVIEW_URL="
            + overURL
            + "\n\t INSTANCE_PARMS="
            + instParms
            + "\n");

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
   * Select all rows from the TMODEL_INST_INFO table for a given BusinessKey.
   *
   * @param bindingKey String
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector select(String bindingKey, Connection connection)
    throws java.sql.SQLException
  {
    Vector infoList = new Vector();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      // create a statement to query with
      statement = connection.prepareStatement(selectSQL);
      statement.setString(1, bindingKey.toString());

      log.debug(
        "select from TMODEL_INSTANCE_INFO table:\n\n\t"
          + selectSQL
          + "\n\t BINDING_KEY="
          + bindingKey.toString()
          + "\n");

      // execute the statement
      resultSet = statement.executeQuery();

      while (resultSet.next())
      {
        String tModelKey = resultSet.getString(1);//("TMODEL_KEY");
        String overURL = resultSet.getString(2);//("OVERVIEW_URL");
        String instParms = resultSet.getString(3);//("INSTANCE_PARMS");

        if (tModelKey != null)
        {
          TModelInstanceInfo info = new TModelInstanceInfo();
          info.setTModelKey(tModelKey);

          OverviewDoc overviewDoc = null;
          if (overURL != null)
          {
            overviewDoc = new OverviewDoc();
            overviewDoc.setOverviewURL(overURL);
          }

          InstanceParms instanceParms = null;
          if (instParms != null)
          {
            instanceParms = new InstanceParms();
            instanceParms.setText(instParms);
          }

          InstanceDetails details = null;
          if ((overviewDoc != null) || (instanceParms != null))
          {
            details = new InstanceDetails();
            details.setOverviewDoc(overviewDoc);
            details.setInstanceParms(instanceParms);
            info.setInstanceDetails(details);
          }

          infoList.add(info);
        }
      }

      return infoList;
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
   * Delete multiple rows from the TMODEL_INST_INFO table that are assigned to the
   * BusinessKey specified.
   *
   * @param bindingKey String
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void delete(String bindingKey, Connection connection)
    throws java.sql.SQLException
  {
    PreparedStatement statement = null;

    try
    {
      // prepare the delete
      statement = connection.prepareStatement(deleteSQL);
      statement.setString(1, bindingKey.toString());

      log.debug(
        "delete from TMODEL_INSTANCE_INFO table:\n\n\t"
          + deleteSQL
          + "\n\t BINDING_KEY="
          + bindingKey.toString()
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
}
