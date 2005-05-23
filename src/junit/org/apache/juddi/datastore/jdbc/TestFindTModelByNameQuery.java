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

import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.ConnectionManager;
import org.apache.juddi.util.jdbc.Transaction;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class TestFindTModelByNameQuery
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
    String name = new String("");

    Vector keysIn = new Vector();
    keysIn.add("uuid:327A56F0-3299-4461-BC23-5CD513E95C55");
    keysIn.add("uuid:4064C064-6D14-4F35-8953-9652106476A9");
    keysIn.add("uuid:4E49A8D6-D5A2-4FC2-93A0-0411D8D19E88");
    keysIn.add("uuid:807A2C6A-EE22-470D-ADC7-E0424A337C03");
    keysIn.add("uuid:8609C81E-EE1F-4D5A-B202-3EB13AD01823");
    keysIn.add("uuid:A035A07C-F362-44DD-8F95-E2B134BF43B4");
    keysIn.add("uuid:B1B1BAF5-2329-43E6-AE13-BA8E97195039");
    keysIn.add("uuid:C0B9FE13-179F-413D-8A5B-5004DB8E5BB2");
    keysIn.add("uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4");
    keysIn.add("uuid:CD153257-086A-4237-B336-6BDCBDCC6634");
    keysIn.add("uuid:DB77450D-9FA8-45D4-A7BC-04411D14E384");
    keysIn.add("uuid:E59AE320-77A5-11D5-B898-0004AC49CC1E");

    Transaction txn = new Transaction();

    if (connection != null)
    {
      try
      {
        // begin a new transaction
        txn.begin(connection);

        FindTModelByNameQuery.select(name,keysIn,null,connection);
        FindTModelByNameQuery.select(name,null,null,connection);

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
