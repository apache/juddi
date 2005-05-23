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
import java.util.Vector;

import org.apache.juddi.datatype.request.FindQualifiers;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.ConnectionManager;
import org.apache.juddi.util.jdbc.Transaction;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class TestFindPublisherByNameQuery
{
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
    String name = new String("Steve");

    Vector idsIn = null;
    idsIn = new Vector();
    idsIn.add(new String("sviens"));
    idsIn.add(new String("jdoe"));
    idsIn.add(new String("steveviens"));

    Transaction txn = new Transaction();

    if (connection != null)
    {
      try
      {
        // begin a new transaction
        txn.begin(connection);

        Vector results = FindPublisherByNameQuery.select(name,idsIn,null,connection);
        if (results != null)
        {
          for (int i=0; i<results.size(); i++)
            System.out.println(i+": "+(String)results.elementAt(i));
        }

        FindQualifiers fqs = new FindQualifiers();
        fqs.sortByNameAsc = true;

        Vector resutls2 = FindPublisherByNameQuery.select(name,null,fqs,connection);
        if (results != null)
        {
          for (int i=0; i<resutls2.size(); i++)
            System.out.println(i+": "+(String)resutls2.elementAt(i));
        }

        fqs.exactNameMatch = true;

        Vector resutls3 = FindPublisherByNameQuery.select(name,null,fqs,connection);
        if (results != null)
        {
          for (int i=0; i<resutls3.size(); i++)
            System.out.println(i+": "+(String)resutls3.elementAt(i));
        }

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