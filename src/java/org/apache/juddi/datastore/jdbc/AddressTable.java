/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "jUDDI" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.juddi.datastore.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.datatype.Address;
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
class AddressTable
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(AddressTable.class);

  static String insertSQL = null;
  static String selectSQL = null;
  static String deleteSQL = null;

  static {
      // buffer used to build SQL statements
      StringBuffer sql = null;

      // build insertSQL
      sql = new StringBuffer(150);
      sql.append("INSERT INTO ADDRESS (");
      sql.append("BUSINESS_KEY,");
      sql.append("CONTACT_ID,");
      sql.append("ADDRESS_ID,");
      sql.append("USE_TYPE,");
      sql.append("SORT_CODE,");
      sql.append("TMODEL_KEY) ");
      sql.append("VALUES (?,?,?,?,?,?)");
      insertSQL = sql.toString();

      // build selectSQL
      sql = new StringBuffer(200);
      sql.append("SELECT ");
      sql.append("USE_TYPE,");
      sql.append("SORT_CODE,");
      sql.append("TMODEL_KEY, ");
      sql.append("ADDRESS_ID ");
      sql.append("FROM ADDRESS ");
      sql.append("WHERE BUSINESS_KEY=? ");
      sql.append("AND CONTACT_ID=? ");
      sql.append("ORDER BY ADDRESS_ID");
      selectSQL = sql.toString();

      // build deleteSQL
      sql = new StringBuffer(100);
      sql.append("DELETE FROM ADDRESS ");
      sql.append("WHERE BUSINESS_KEY=?");
      deleteSQL = sql.toString();
  }

  /**
   * Insert new row into the ADDRESS table.
   *
   * @param  businessKey String to the BusinessEntity object that owns the Contact to be inserted
   * @param  contactID The unique ID generated when saving the parent Contact instance.
   * @param  addrList Collection of Address objects to be inserted
   * @param  connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void insert(String businessKey,int contactID,Vector addrList,Connection connection)
    throws java.sql.SQLException
  {
    if ((addrList == null) || (addrList.size() == 0))
      return; // everything is valid but no elements to insert
  
    PreparedStatement statement = null;
  
    try
    {
      statement = connection.prepareStatement(insertSQL);
      statement.setString(1, businessKey.toString());
      statement.setInt(2, contactID);
  
      int listSize = addrList.size();
      for (int addressID = 0; addressID < listSize; addressID++)
      {
        Address address = (Address) addrList.elementAt(addressID);
        statement.setInt(3, addressID);
        statement.setString(4, address.getUseType());
        statement.setString(5, address.getSortCode());
        statement.setString(6, address.getTModelKey());
  
        log.debug(
          "insert into ADDRESS table:\n\n\t"
            + insertSQL
            + "\n\t BUSINESS_KEY="
            + businessKey.toString()
            + "\n\t CONTACT_ID="
            + contactID
            + "\n\t ADDRESS_ID="
            + addressID
            + "\n\t USE_TYPE="
            + address.getUseType()
            + "\n\t SORT_CODE="
            + address.getSortCode()
            + "\n\t TMODEL_KEY="
            + address.getTModelKey()
            + "\n");
  
        statement.executeUpdate();
      }
    }
    finally
    {
      try {
        statement.close();
      }
      catch (Exception e) { /* ignored */ }
    }
  }

  /**
   * Select all rows from the CONTACT table for a given BusinessKey.
   *
   * @param  businessKey String
   * @param  contactID Unique ID representing the parent Contact instance
   * @param  connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector select(String businessKey,int contactID,Connection connection)
    throws java.sql.SQLException
  {
    Vector addrList = new Vector();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      // create a statement to query with
      statement = connection.prepareStatement(selectSQL);
      statement.setString(1, businessKey.toString());
      statement.setInt(2, contactID);

      log.debug(
        "select from ADDRESS table:\n\n\t"
          + selectSQL
          + "\n\t BUSINESS_KEY="
          + businessKey.toString()
          + "\n\t CONTACT_ID="
          + contactID
          + "\n");

      // execute the statement
      resultSet = statement.executeQuery();
      while (resultSet.next())
      {
        Address address = new Address();
        address.setUseType(resultSet.getString(1));//("USE_TYPE"));
        address.setSortCode(resultSet.getString(2));//("SORT_CODE"));
        address.setTModelKey(resultSet.getString(3));//("TMODEL_KEY"));
        addrList.add(address);
        address = null;
      }

      return addrList;
    }
    finally
    {
      try {
        resultSet.close();
        statement.close();
      }
      catch (Exception e) { /* ignored */ }
    }
  }

  /**
   * Delete multiple rows from the ADDRESS table that are assigned to the
   * BusinessKey specified.
   *
   * @param  businessKey String
   * @param  connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void delete(String businessKey, Connection connection)
    throws java.sql.SQLException
  {
    PreparedStatement statement = null;

    try
    {
      // prepare the delete
      statement = connection.prepareStatement(deleteSQL);
      statement.setString(1, businessKey.toString());

      log.debug(
        "delete from ADDRESS table:\n\n\t"
          + deleteSQL
          + "\n\t BUSINESS_KEY="
          + businessKey.toString()
          + "\n");

      // execute
      statement.executeUpdate();
    }
    finally
    {
      try {
        statement.close();
      }
      catch (Exception e) { /* ignored */ }
    }
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


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