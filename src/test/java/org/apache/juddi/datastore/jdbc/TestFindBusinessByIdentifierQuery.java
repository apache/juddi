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

import org.apache.juddi.datatype.IdentifierBag;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.Transaction;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class TestFindBusinessByIdentifierQuery
{
  public static void main(String[] args)
    throws Exception
  {
    // make sure we're using a DBCP DataSource and
    // not trying to use JNDI to aquire one.
    Config.setStringProperty("juddi.useConnectionPool","true");

    Connection conn = null;
    try {
      conn = Database.aquireConnection();
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
    IdentifierBag identifierBag = new IdentifierBag();
    Vector keyedRefVector = new Vector();
    keyedRefVector.addElement(new KeyedReference("af176f34-00ca-4d93-97f8-2b62aa3a75e5","blah, blah, blah"));
    keyedRefVector.addElement(new KeyedReference("54a827e0-1b51-4381-8775-ad02c377bb25","Haachachachacha"));
    keyedRefVector.addElement(new KeyedReference("xxxxxxxxxxxxxxxx","xxxxxxxxxxx"));
    identifierBag.setKeyedReferenceVector(keyedRefVector);

    Vector keysIn = new Vector();
    keysIn.add("824c73af-22e0-4816-9689-2429101c7727");
    keysIn.add("c311085b-3277-470d-8ce9-07b81c484e4b");
    keysIn.add("6b368a5a-6a62-4f23-a002-f11e22780a91");
    keysIn.add("45994713-d3c3-40d6-87b5-6ce51f36001c");
    keysIn.add("901b15c5-799c-4387-8337-a1a35fceb791");
    keysIn.add("80fdae14-0e5d-4ea6-8eb8-50fde422056d");
    keysIn.add("e1996c33-c436-4004-9e3e-14de191bcc6b");
    keysIn.add("f715d9ff-d4eb-4073-92e8-2411cd5d0d68");

    Transaction txn = new Transaction();

    if (connection != null)
    {
      try
      {
        // begin a new transaction
        txn.begin(connection);

        FindBusinessByIdentifierQuery.select(identifierBag,keysIn,null,connection);
        FindBusinessByIdentifierQuery.select(identifierBag,null,null,connection);

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
