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

import org.apache.juddi.datatype.TModelBag;
import org.apache.juddi.datatype.TModelKey;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.ConnectionManager;
import org.apache.juddi.util.jdbc.Transaction;

/**
 * tModelBag: This is a list of tModel uuid_key values that represent the 
 * technical fingerprint of a bindingTemplate structure to find. All 
 * bindingTemplate structures within any businessService associated with the 
 * businessEntity specified by the businessKey argument will be searched. 
 * 
 * If more than one tModel key is specified in this structure, only 
 * businessService structures that contain bindingTemplate structures with 
 * fingerprint information that matches all of the tModel keys specified will 
 * be returned (logical AND only).
 * 
 * @author Steve Viens (sviens@apache.org)
 */
class TestFindServiceByTModelKeyQuery
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
    String businessKey = "13411e97-24cf-43d1-bee0-455e7ec5e9fc";

    TModelBag tModelBag = new TModelBag();
    Vector tModelKeyVector = new Vector();
    tModelKeyVector.addElement(new TModelKey(""));
    tModelKeyVector.addElement(new TModelKey(""));
    tModelKeyVector.addElement(new TModelKey("2a33d7d7-2b73-4de9-99cd-d4c51c186bce"));
    tModelKeyVector.addElement(new TModelKey("2a33d7d7-2b73-4de9-99cd-d4c51c186bce"));
    tModelBag.setTModelKeyVector(tModelKeyVector);

    Vector keysIn = new Vector();
    keysIn.add("13411e97-24cf-43d1-bee0-455e7ec5e9fc");
    keysIn.add("3f244f19-7ba7-4c3e-a93e-ae33e530794b");
    keysIn.add("3009f336-98c1-4193-a22f-fea73e79c909");
    keysIn.add("3ef4772f-e04b-46ed-8065-c5a4e167b5ba");

    Transaction txn = new Transaction();

    if (connection != null)
    {
      try
      {
        // begin a new transaction
        txn.begin(connection);

        FindServiceByTModelKeyQuery.select(businessKey,tModelBag,keysIn,null,connection);
        FindServiceByTModelKeyQuery.select(businessKey,tModelBag,null,null,connection);

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
