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

import org.apache.juddi.datatype.Email;
import org.apache.juddi.datatype.business.BusinessEntity;
import org.apache.juddi.datatype.business.Contact;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.Transaction;
import org.apache.juddi.uuidgen.UUIDGen;
import org.apache.juddi.uuidgen.UUIDGenFactory;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class TestEmailTable
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
        String businessKey = uuidgen.uuidgen();
        BusinessEntity business = new BusinessEntity();
        business.setBusinessKey(businessKey);
        business.setAuthorizedName("sviens");
        business.setOperator("WebServiceRegistry.com");

        Vector contactList = new Vector();
        Contact contact = new Contact("Billy Bob");
        contact.setUseType("server");
        contactList.add(contact);
        int contactID = 0;

        Vector emailList = new Vector();
        Email email = null;

        email = new Email("support@steveviens.com");
        email.setUseType("Support");
        emailList.add(email);

        email = new Email("marketing@steveviens.com");
        email.setUseType("Advertising");
        emailList.add(email);

        email = new Email("info@steveviens.com");
        email.setUseType("Information");
        emailList.add(email);

        email = new Email("admin@steveviens.com");
        email.setUseType("Administration");
        emailList.add(email);

        email = new Email("webmaster@steveviens.com");
        email.setUseType("Web Master");
        emailList.add(email);

        String authorizedUserID = "sviens";

        // begin a new transaction
        txn.begin(connection);

        // insert a new BusinessEntity
        BusinessEntityTable.insert(business, authorizedUserID, connection);

        // insert a new Contact
        ContactTable.insert(businessKey, contactList, connection);

        // insert a Collection of Email objects
        EmailTable.insert(businessKey, contactID, emailList, connection);

        // select a Collection of Email objects by BusinessKey
        emailList = EmailTable.select(businessKey, contactID, connection);

        // delete a Collection of Email objects by BusinessKey
        EmailTable.delete(businessKey, connection);

        // re-select a Collection of Email objects by BusinessKey
        emailList = EmailTable.select(businessKey, contactID, connection);

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
