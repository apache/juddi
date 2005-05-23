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

import org.apache.juddi.datatype.DiscoveryURL;
import org.apache.juddi.datatype.DiscoveryURLs;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.ConnectionManager;
import org.apache.juddi.util.jdbc.Transaction;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class TestFindBusinessByDiscoveryURLQuery
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

    DiscoveryURLs discoveryURLs = new DiscoveryURLs();
    Vector urls = new Vector();
    urls.add(new DiscoveryURL("http://www.uddi.org/","wsdl"));
    urls.add(new DiscoveryURL("http://www.sviens.com/","soap"));
    urls.add(new DiscoveryURL("http://www.juddi.org/","wsdl"));
    discoveryURLs.setDiscoveryURLVector(urls);

    Vector keysIn = new Vector();
    keysIn.add("d77170a5-cf1e-45e8-bbf3-5b0756b29199");
    keysIn.add("c311085b-3277-470d-8ce9-07b81c484e4b");
    keysIn.add("e2805dbe-3957-4490-9230-5f06d85fd50d");
    keysIn.add("45994713-d3c3-40d6-87b5-6ce51f36001c");
    keysIn.add("901b15c5-799c-4387-8337-a1a35fceb791");
    keysIn.add("80fdae14-0e5d-4ea6-8eb8-50fde422056d");
    keysIn.add("e1996c33-c436-4004-9e3e-14de191bcc6b");
    keysIn.add("36f5d745-3be5-4e8f-8c1a-5f59a4c69070");

    Transaction txn = new Transaction();

    if (connection != null)
    {
      try
      {
        // begin a new transaction
        txn.begin(connection);

        FindBusinessByDiscoveryURLQuery.select(discoveryURLs,keysIn,null,connection);
        FindBusinessByDiscoveryURLQuery.select(discoveryURLs,null,null,connection);

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
