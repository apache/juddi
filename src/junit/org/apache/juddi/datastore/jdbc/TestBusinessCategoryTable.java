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

import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.business.BusinessEntity;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.ConnectionManager;
import org.apache.juddi.util.jdbc.Transaction;
import org.apache.juddi.uuidgen.UUIDGen;
import org.apache.juddi.uuidgen.UUIDGenFactory;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class TestBusinessCategoryTable
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

  public static void test(Connection connection) throws Exception
  {
    Transaction txn = new Transaction();
    UUIDGen uuidgen = UUIDGenFactory.getUUIDGen();

    if (connection != null)
    {
      try
      {
        String businessKey = uuidgen.uuidgen();
        BusinessEntity business = new BusinessEntity();
        business.setBusinessKey(businessKey);
        business.setAuthorizedName("sviens");
        business.setOperator("WebServiceRegistry.com");

        Vector keyRefs = new Vector();
        keyRefs.add(new KeyedReference(uuidgen.uuidgen(), "blah, blah, blah"));
        keyRefs.add(
          new KeyedReference(uuidgen.uuidgen(), "Yadda, Yadda, Yadda"));
        keyRefs.add(
          new KeyedReference(uuidgen.uuidgen(), "WhoobWhoobWhoobWhoob"));
        keyRefs.add(new KeyedReference(uuidgen.uuidgen(), "Haachachachacha"));

        String authorizedUserID = "sviens";

        // begin a new transaction
        txn.begin(connection);

        // insert a new BusinessEntity
        BusinessEntityTable.insert(business, authorizedUserID, connection);

        // insert a Collection of new Category KeyedReference objects
        BusinessCategoryTable.insert(businessKey, keyRefs, connection);

        // insert another new BusinessEntity
        business.setBusinessKey(uuidgen.uuidgen());
        BusinessEntityTable.insert(business, authorizedUserID, connection);

        // insert another Collection of new Category KeyedReference objects
        BusinessCategoryTable.insert(
          business.getBusinessKey(),
          keyRefs,
          connection);

        // select a Collection of Category KeyedReference objects
        keyRefs = BusinessCategoryTable.select(businessKey, connection);

        // delete a Collection of Category KeyedReference objects
        BusinessCategoryTable.delete(businessKey, connection);

        // re-select a Collection of Category KeyedReference objects
        keyRefs = BusinessCategoryTable.select(businessKey, connection);

        // commit the transaction
        txn.commit();
      }
      catch (Exception ex)
      {
        try
        {
          txn.rollback();
        }
        catch (java.sql.SQLException sqlex)
        {
          sqlex.printStackTrace();
        }
        throw ex;
      }
    }
  }
}
