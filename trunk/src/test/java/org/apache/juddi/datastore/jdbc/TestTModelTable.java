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

import org.apache.juddi.datatype.OverviewDoc;
import org.apache.juddi.datatype.tmodel.TModel;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.Transaction;
import org.apache.juddi.uuidgen.UUIDGen;
import org.apache.juddi.uuidgen.UUIDGenFactory;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class TestTModelTable
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
        OverviewDoc overviewDoc = new OverviewDoc();
        overviewDoc.setOverviewURL("http://www.sviens.com/jtruss.html");

        String tModelKey = uuidgen.uuidgen();
        TModel tModel = new TModel();
        tModel.setTModelKey(tModelKey);
        tModel.setAuthorizedName("Steve Viens");
        tModel.setOperator("WebServiceRegistry.com");
        tModel.setName("Tuscany Web Service Company");
        tModel.setOverviewDoc(overviewDoc);

        String publisherID = "sviens";

        // begin a new transaction
        txn.begin(connection);

        // insert a new TModel
        TModelTable.insert(tModel,publisherID,connection);

        // select one of the TModel objects
        tModel = TModelTable.select(tModelKey,connection);

        // select a Collection of TModel keys by PublisherID
        TModelTable.selectByPublisherID(publisherID,connection);

        TModelTable.verifyOwnership(tModelKey,"mviens",connection);
        TModelTable.verifyOwnership(tModelKey,"sviens",connection);

        // delete that TModel object
        TModelTable.delete(tModelKey,connection);
        //TModelTable.markAsDeleted(tModelKey,connection);

        // re-select that TModel object
        tModel = TModelTable.select(tModelKey,connection);

        // re-select a Collection of TModel keys by PublisherID
        TModelTable.selectByPublisherID(publisherID,connection);

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
