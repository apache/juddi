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

import org.apache.juddi.datatype.CategoryBag;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.Transaction;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class TestFindTModelByCategoryQuery
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
    CategoryBag categoryBag = new CategoryBag();
    Vector keyedRefVector = new Vector();
    keyedRefVector.addElement(new KeyedReference("uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4","ntis-gov:NAICS:1997","51121"));
    keyedRefVector.addElement(new KeyedReference("uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4","Mining","21"));
    keyedRefVector.addElement(new KeyedReference("uuid:DB77450D-9FA8-45D4-A7BC-04411D14E384",null,"abcdefg"));
    keyedRefVector.addElement(new KeyedReference("uuid:DB77450D-9FA8-45D4-A7BC-04411D14E384","1775f0f8-cd47-451d-88da-73ce508836f3","blah, blah, blah"));
    categoryBag.setKeyedReferenceVector(keyedRefVector);

    Vector keysIn = new Vector();
    keysIn.add("740d75b1-3cde-4547-85dd-9578cd3ea1cd");
    keysIn.add("c311085b-3277-470d-8ce9-07b81c484e4b");
    keysIn.add("6b368a5a-6a62-4f23-a002-f11e22780a91");
    keysIn.add("45994713-d3c3-40d6-87b5-6ce51f36001c");
    keysIn.add("901b15c5-799c-4387-8337-a1a35fceb791");
    keysIn.add("80fdae14-0e5d-4ea6-8eb8-50fde422056d");
    keysIn.add("e1996c33-c436-4004-9e3e-14de191bcc6b");
    keysIn.add("3ef4772f-e04b-46ed-8065-c5a4e167b5ba");

    Transaction txn = new Transaction();

    if (connection != null)
    {
      try
      {
        // begin a new transaction
        txn.begin(connection);

        FindTModelByCategoryQuery.select(categoryBag,keysIn,null,connection);
        FindTModelByCategoryQuery.select((KeyedReference)keyedRefVector.elementAt(0),null,null,connection);

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
