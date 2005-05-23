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

import org.apache.juddi.datatype.Address;
import org.apache.juddi.datatype.business.BusinessEntity;
import org.apache.juddi.datatype.business.Contact;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.Transaction;
import org.apache.juddi.uuidgen.UUIDGen;
import org.apache.juddi.uuidgen.UUIDGenFactory;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class TestAddressTable
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

  public static void test(Connection connection) throws Exception
  {
    Transaction txn = new Transaction();
    UUIDGen uuidgen = UUIDGenFactory.getUUIDGen();

    if (connection != null)
    {
      try
      {
        String authorizedUserID = "sviens";

        String businessKey = uuidgen.uuidgen();
        BusinessEntity business = new BusinessEntity();
        business.setBusinessKey(businessKey);
        business.setAuthorizedName("sviens");
        business.setOperator("WebServiceRegistry.com");

        Vector contactList = new Vector();
        Contact contact = new Contact("Bill Bob");
        contact.setUseType("server");
        contactList.add(contact);
        int contactID = 0;

        Vector addrList = new Vector();
        Address address = null;

        address = new Address();
        address.setUseType("Mailing");
        address.setSortCode("a");
        addrList.add(address);

        address = new Address();
        address.setUseType("Shipping");
        address.setSortCode("b");
        addrList.add(address);

        address = new Address();
        address.setUseType("Marketing");
        address.setSortCode("c");
        addrList.add(address);

        address = new Address();
        address.setUseType("Sales");
        address.setSortCode("d");
        addrList.add(address);

        address = new Address();
        address.setUseType("Engineering");
        address.setSortCode("e");
        addrList.add(address);

        // begin a new transaction
        txn.begin(connection);

        // insert a new BusinessEntity
        BusinessEntityTable.insert(business, authorizedUserID, connection);

        // insert a new Contact
        ContactTable.insert(businessKey, contactList, connection);

        // insert a Collection of Address objects
        AddressTable.insert(businessKey, contactID, addrList, connection);

        // select the Collection of Address objects
        addrList = AddressTable.select(businessKey, contactID, connection);

        // delete the Collection of Address objects
        AddressTable.delete(businessKey, connection);

        // re-select the Collection of Address objects
        addrList = AddressTable.select(businessKey, contactID, connection);

        // commit the transaction
        txn.commit();
      }
      catch (Exception ex)
      {
        try {
          txn.rollback();
        }
        catch (java.sql.SQLException sqlex) {
          sqlex.printStackTrace();
        }

        throw ex;
      }
    }
  }
}