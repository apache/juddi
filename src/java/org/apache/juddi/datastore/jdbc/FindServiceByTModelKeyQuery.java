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
import org.apache.juddi.datatype.TModelBag;
import org.apache.juddi.datatype.TModelKey;
import org.apache.juddi.datatype.request.FindQualifiers;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.ConnectionManager;
import org.apache.juddi.util.jdbc.Transaction;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class FindServiceByTModelKeyQuery
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(FindServiceByTModelKeyQuery.class);

  static String selectSQL;
  static
  {
    // build selectSQL
    StringBuffer sql = new StringBuffer(200);
    sql.append("SELECT S.SERVICE_KEY,S.LAST_UPDATE ");
    sql.append("FROM BUSINESS_SERVICE S,BINDING_TEMPLATE T,TMODEL_INSTANCE_INFO I ");
    selectSQL = sql.toString();
  }

  /**
   * Select ...
   *
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector select(String businessKey,TModelBag tModelBag,Vector keysIn,FindQualifiers qualifiers,Connection connection)
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
    appendWhere(sql,businessKey,tModelBag,qualifiers);
    appendIn(sql,keysIn);
    appendOrderBy(sql,qualifiers);

    try
    {
      log.debug("select from BUSINESS_SERVICE, BINDING_TEMPLATE & TMODEL_INSTANCE_INFO tables:\n\n\t" + sql.toString() + "\n");

      statement = connection.prepareStatement(sql.toString());
      resultSet = statement.executeQuery();

      while (resultSet.next())
        keysOut.addElement(resultSet.getString(1));//("SERVICE_KEY"));

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
          "the Find BusinessService ResultSet: "+e.getMessage(),e);
      }

      try {
        statement.close();
      }
      catch (Exception e)
      {
        log.warn("An Exception was encountered while attempting to close " +
          "the Find BusinessService Statement: "+e.getMessage(),e);
      }
    }
  }

  /**
   *
   */
  private static void appendWhere(StringBuffer sql,String businessKey,TModelBag tModelBag,FindQualifiers qualifiers)
  {
    sql.append("WHERE I.BINDING_KEY = T.BINDING_KEY ");
    sql.append("AND T.SERVICE_KEY = S.SERVICE_KEY ");

    if (businessKey != null)
      sql.append("AND S.BUSINESS_KEY = '").append(businessKey).append("' ");

    Vector keyVector = tModelBag.getTModelKeyVector();

    int vectorSize = keyVector.size();
    if (vectorSize > 0)
    {
      sql.append("AND (");

      for (int i=0; i<vectorSize; i++)
      {
        String key = (String)keyVector.elementAt(i);

        sql.append("I.TMODEL_KEY = '").append(key).append("' ");

        if (i+1 < vectorSize)
          sql.append(" OR ");
      }

      sql.append(") ");
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

    sql.append("AND S.SERVICE_KEY IN (");

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
      sql.append("S.LAST_UPDATE DESC");
    else if (qualifiers.sortByDateAsc)
      sql.append("S.LAST_UPDATE ASC");
    else
      sql.append("S.LAST_UPDATE DESC");
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

        select(businessKey,tModelBag,keysIn,null,connection);
        select(businessKey,tModelBag,null,null,connection);

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
