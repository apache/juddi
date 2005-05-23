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

import org.apache.juddi.datatype.publisher.Publisher;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.ConnectionManager;
import org.apache.juddi.util.jdbc.Transaction;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class TestPublisherTable
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
    Transaction txn = new Transaction();

    if (connection != null)
    {
      try
      {
        // begin a new transaction
        txn.begin(connection);

        // insert a few new publishers
        Publisher publisher = new Publisher();
        publisher.setPublisherID("bcrosby");
        publisher.setName("Bing Crosby");
        publisher.setEmailAddress("bcrosby@juddi.org");
        publisher.setAdmin(false);
        publisher.setEnabled(false);
        PublisherTable.insert(publisher,connection);

        // select each inserted publisher
        System.out.println(PublisherTable.select("bcrosby",connection));

        publisher.setName("Barthalomue Crosby");
        publisher.setEnabled(true);
        PublisherTable.update(publisher,connection);

        // select each inserted publisher
        System.out.println(PublisherTable.select("bcrosby",connection));

        // delete two of the inserted publishers
        PublisherTable.delete("bcrosby",connection);

        // select each inserted publisher
        System.out.println(PublisherTable.select("bcrosby",connection));
        System.out.println("");

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