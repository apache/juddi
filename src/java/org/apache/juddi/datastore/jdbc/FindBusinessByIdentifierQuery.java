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
import org.apache.juddi.datatype.IdentifierBag;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.request.FindQualifiers;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.ConnectionManager;
import org.apache.juddi.util.jdbc.Transaction;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class FindBusinessByIdentifierQuery
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(FindBusinessByIdentifierQuery.class);

  static String selectSQL;
  static
  {
    // build selectSQL
    StringBuffer sql = new StringBuffer(200);
    sql.append("SELECT B.BUSINESS_KEY,B.LAST_UPDATE ");
    sql.append("FROM BUSINESS_ENTITY B,BUSINESS_IDENTIFIER I ");
    selectSQL = sql.toString();
  }

  /**
   * Select ...
   *
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector select(IdentifierBag identifierBag,Vector keysIn,FindQualifiers qualifiers,Connection connection)
    throws java.sql.SQLException
  {
    // if there is a keysIn vector but it doesn't contain
    // any keys then the previous query has exhausted
    // all possibilities of a match so skip this call.
    if ((keysIn != null) && (keysIn.size() == 0))
      return keysIn;

    Vector keysOut = new Vector();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    // construct the SQL statement
    StringBuffer sql = new StringBuffer(selectSQL);
    appendWhere(sql,identifierBag,qualifiers);
    appendIn(sql,keysIn);
    appendOrderBy(sql,qualifiers);

    try
    {
      log.debug("select from BUSINESS_ENTITY & BUSINESS_IDENTIFIER tables:\n\n\t" + sql.toString() + "\n");

      statement = connection.prepareStatement(sql.toString());
      resultSet = statement.executeQuery();

      while (resultSet.next())
        keysOut.addElement(resultSet.getString(1));//("BUSINESS_KEY"));

      return keysOut;
    }
    finally
    {
      try {
        resultSet.close();
      }
      catch (Exception e)
      {
        log.warn("An Exception was encountered while attempting to close " +
          "the Find BusinessEntity ResultSet: "+e.getMessage(),e);
      }

      try {
        statement.close();
      }
      catch (Exception e)
      {
        log.warn("An Exception was encountered while attempting to close " +
          "the Find BusinessEntity Statement: "+e.getMessage(),e);
      }
    }
  }

  /**
   *
   */
  private static void appendWhere(StringBuffer sql,IdentifierBag identifierBag,FindQualifiers qualifiers)
  {
    sql.append("WHERE B.BUSINESS_KEY = I.BUSINESS_KEY ");

    if(identifierBag != null)
    {
      Vector keyedRefVector = identifierBag.getKeyedReferenceVector();
      if(keyedRefVector != null)
      {
        int vectorSize = keyedRefVector.size();
        if (vectorSize > 0)
        {
          sql.append("AND (");

          for (int i=0; i<vectorSize; i++)
          {
            KeyedReference keyedRef = (KeyedReference)keyedRefVector.elementAt(i);
            String name = keyedRef.getKeyName();
            String value = keyedRef.getKeyValue();

            if ((name != null) && (value != null))
            {
              sql.append("(I.KEY_NAME = '").append(name).append("' AND I.KEY_VALUE = '").append(value).append("')");

              if (i+1 < vectorSize)
                sql.append(" OR ");
            }
          }

          sql.append(") ");
        }
      }
    }
  }

  /**
   * Utility method used to construct SQL "IN" statements such as
   * the following SQL example:
   *
   *   SELECT * FROM TABLE WHERE MONTH IN ('jan','feb','mar')
   *
   * @param sql StringBuffer to append the final results to
   * @param keysIn Vector of Strings used to construct the "IN" clause
   */
  private static void appendIn(StringBuffer sql,Vector keysIn)
  {
    if (keysIn == null)
      return;

    sql.append("AND B.BUSINESS_KEY IN (");

    int keyCount = keysIn.size();
    for (int i=0; i<keyCount; i++)
    {
      String key = (String)keysIn.elementAt(i);
      sql.append("'").append(key).append("'");

      if ((i+1) < keyCount)
        sql.append(",");
    }

    sql.append(") ");
  }

  /**
   *
   */
  private static void appendOrderBy(StringBuffer sql,FindQualifiers qualifiers)
  {
    sql.append("ORDER BY ");

    if (qualifiers == null)
      sql.append("B.LAST_UPDATE DESC");
    else if (qualifiers.sortByDateAsc)
      sql.append("B.LAST_UPDATE ASC");
    else
      sql.append("B.LAST_UPDATE DESC");
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

  public static void test(Connection connection)
    throws Exception
  {
    IdentifierBag identifierBag = new IdentifierBag();
    Vector keyedRefVector = new Vector();
    keyedRefVector.addElement(new KeyedReference("af176f34-00ca-4d93-97f8-2b62aa3a75e5","blah, blah, blah"));
    keyedRefVector.addElement(new KeyedReference("54a827e0-1b51-4381-8775-ad02c377bb25","Haachachachacha"));
    keyedRefVector.addElement(new KeyedReference("xxxxxxxxxxxxxxxx","xxxxxxxxxxx"));
    identifierBag.setKeyedReferenceVector(keyedRefVector);

    Vector keysIn = new Vector();
    keysIn.add("824c73af-22e0-4816-9689-2429101c7727");
    keysIn.add("c311085b-3277-470d-8ce9-07b81c484e4b");
    keysIn.add("6b368a5a-6a62-4f23-a002-f11e22780a91");
    keysIn.add("45994713-d3c3-40d6-87b5-6ce51f36001c");
    keysIn.add("901b15c5-799c-4387-8337-a1a35fceb791");
    keysIn.add("80fdae14-0e5d-4ea6-8eb8-50fde422056d");
    keysIn.add("e1996c33-c436-4004-9e3e-14de191bcc6b");
    keysIn.add("f715d9ff-d4eb-4073-92e8-2411cd5d0d68");

    Transaction txn = new Transaction();

    if (connection != null)
    {
      try
      {
        // begin a new transaction
        txn.begin(connection);

        select(identifierBag,keysIn,null,connection);
        select(identifierBag,null,null,connection);

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
