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
import org.apache.juddi.datatype.AddressLine;
import org.apache.juddi.datatype.business.BusinessEntity;
import org.apache.juddi.datatype.business.Contact;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.ConnectionManager;
import org.apache.juddi.util.jdbc.Transaction;
import org.apache.juddi.uuidgen.UUIDGen;
import org.apache.juddi.uuidgen.UUIDGenFactory;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class TestAddressLineTable
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

        Contact contact = new Contact();
        contact.setPersonNameValue("Bill Bob");
        contact.setUseType("server");

        Vector contactList = new Vector();
        contactList.add(contact);
        int contactID = 0;

        Address address = new Address();
        address.setUseType("Mailing");
        address.setSortCode("a");

        Vector addrList = new Vector();
        addrList.add(address);
        int addressID = 0;

        AddressLine addrLine1 = new AddressLine();
        addrLine1.setLineValue("SteveViens.com, Inc.");

        AddressLine addrLine2 = new AddressLine();
        addrLine2.setLineValue("PO BOX 6856");

        AddressLine addrLine3 = new AddressLine();
        addrLine3.setLineValue("78 Marne Avenue");

        AddressLine addrLine4 = new AddressLine();
        addrLine4.setLineValue("Portsmouth");

        AddressLine addrLine5 = new AddressLine();
        addrLine5.setLineValue("New Hampshire");

        Vector lineList = new Vector();
        lineList.add(addrLine1);
        lineList.add(addrLine2);
        lineList.add(addrLine3);
        lineList.add(addrLine4);
        lineList.add(addrLine5);

        // begin a new transaction
        txn.begin(connection);

        // insert a new BusinessEntity
        BusinessEntityTable.insert(business,authorizedUserID,connection);

        // insert a new Contact
        ContactTable.insert(businessKey,contactList,connection);

        // insert a new Address
        AddressTable.insert(businessKey,contactID,addrList,connection);

        // insert a Collection of AddressLine objects
        AddressLineTable.insert(businessKey,contactID,addressID,lineList,connection);

        // select the Collection of AddressLine objects
        lineList = AddressLineTable.select(businessKey,contactID,addressID,connection);

        // delete the Collection of AddressLine objects
        //AddressLineTable.delete(businessKey,connection);

        // re-select the Collection of AddressLine objects
        lineList = AddressLineTable.select(businessKey,contactID,addressID,connection);

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
