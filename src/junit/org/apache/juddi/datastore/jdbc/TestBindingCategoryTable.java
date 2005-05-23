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
import org.apache.juddi.datatype.binding.AccessPoint;
import org.apache.juddi.datatype.binding.BindingTemplate;
import org.apache.juddi.datatype.business.BusinessEntity;
import org.apache.juddi.datatype.service.BusinessService;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.ConnectionManager;
import org.apache.juddi.util.jdbc.Transaction;
import org.apache.juddi.uuidgen.UUIDGen;
import org.apache.juddi.uuidgen.UUIDGenFactory;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class TestBindingCategoryTable
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
        //BusinessKey businessKey = BusinessKey.createKey();
        String businessKey = uuidgen.uuidgen();
        BusinessEntity business = new BusinessEntity();
        business.setBusinessKey(businessKey);
        business.setAuthorizedName("sviens");
        business.setOperator("WebServiceRegistry.com");

        //ServiceKey serviceKey = ServiceKey.createKey();
        String serviceKey = uuidgen.uuidgen();
        BusinessService service = new BusinessService();
        service.setServiceKey(serviceKey);
        service.setBusinessKey(businessKey);

        //BindingKey bindingKey = BindingKey.createKey();
        String bindingKey = uuidgen.uuidgen();
        BindingTemplate binding = new BindingTemplate();
        binding.setAccessPoint(
          new AccessPoint("http://www.juddi.org/binding.html", "http"));
        binding.setHostingRedirector(null);
        binding.setBindingKey(bindingKey);
        binding.setServiceKey(serviceKey);

        // Keyed References for the Binding Template CategoryBag.
        Vector keyRefs = new Vector();
        keyRefs.add(
          new KeyedReference(
            "uuid:" + uuidgen.uuidgen(),
            "alpha",
            "abcdefghi"));
        keyRefs.add(
          new KeyedReference("uuid:" + uuidgen.uuidgen(), "beta", "jklmnopqr"));
        keyRefs.add(
          new KeyedReference("uuid:" + uuidgen.uuidgen(), "omega", "stuvwxyz"));

        String authorizedUserID = "sviens";

        // begin a new transaction
        txn.begin(connection);

        // insert a new BusinessEntity
        BusinessEntityTable.insert(business, authorizedUserID, connection);

        // insert a new BusinessService
        BusinessServiceTable.insert(service, connection);

        // insert a new BindingTemplate
        BindingTemplateTable.insert(binding, connection);

        // insert a Collection of new Category KeyedReference objects
        BindingCategoryTable.insert(bindingKey, keyRefs, connection);

        // select the Collection of KeyedReference objects
        keyRefs = BindingCategoryTable.select(bindingKey, connection);

        for (int i = 0; i < keyRefs.size(); i++)
        {
          KeyedReference keyRef = (KeyedReference) keyRefs.elementAt(i);
          System.out.println(" Key Name: " + keyRef.getKeyName());
          System.out.println("Key Value: " + keyRef.getKeyValue());
          System.out.println("TModelKey: " + keyRef.getTModelKey());
        }

        // delete the Collection of KeyedReference objects
        BindingCategoryTable.delete(bindingKey, connection);

        // select the Collection of KeyedReference objects
        keyRefs = BindingCategoryTable.select(bindingKey, connection);

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
