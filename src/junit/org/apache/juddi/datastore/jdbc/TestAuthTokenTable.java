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
import org.apache.juddi.util.jdbc.Transaction;
import org.apache.juddi.uuidgen.UUIDGen;
import org.apache.juddi.uuidgen.UUIDGenFactory;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class TestAuthTokenTable
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
    Transaction txn = new Transaction();
    UUIDGen uuidgen = UUIDGenFactory.getUUIDGen();

    if (connection != null)
    {
      try
      {
        // begin a new transaction
        txn.begin(connection);

        String authToken1 = "juddi:"+uuidgen.uuidgen();
        AuthTokenTable.insert(authToken1,new Publisher("sviens","Steve Viens"),connection);
        AuthTokenTable.selectLastUsed(authToken1,connection);

        String authToken2 = "juddi:"+uuidgen.uuidgen();
        AuthTokenTable.insert(authToken2,new Publisher("sviens","Steve Viens"),connection);
        AuthTokenTable.selectLastUsed(authToken2,connection);

        System.out.println("PublisherID: "+AuthTokenTable.selectPublisher(authToken1,connection));

        AuthTokenTable.delete(authToken1,connection);

        Thread.sleep(3000);

        AuthTokenTable.touch(authToken2,connection);
        AuthTokenTable.touch(authToken2,connection);
        AuthTokenTable.touch(authToken2,connection);
        AuthTokenTable.selectLastUsed(authToken2,connection);
        AuthTokenTable.touch(authToken2,connection);
        AuthTokenTable.touch(authToken2,connection);
        AuthTokenTable.touch(authToken2,connection);
        AuthTokenTable.selectLastUsed(authToken2,connection);
        AuthTokenTable.touch(authToken2,connection);
        AuthTokenTable.touch(authToken2,connection);
        AuthTokenTable.touch(authToken2,connection);
        AuthTokenTable.selectLastUsed(authToken2,connection);
        AuthTokenTable.touch(authToken2,connection);
        AuthTokenTable.invalidate(authToken2,connection);
        AuthTokenTable.selectLastUsed(authToken2,connection);

        System.out.println("PublisherID: "+AuthTokenTable.selectPublisher(authToken1,connection));
        System.out.println("PublisherID: "+AuthTokenTable.selectPublisher(authToken2,connection));

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
