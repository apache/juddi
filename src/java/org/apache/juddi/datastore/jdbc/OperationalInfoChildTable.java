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

// java imports
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.util.Vector;

// apache imports
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Andy Cutright (acutright@apache.org)
 */

class OperationalInfoChildTable {
  private static Log log = LogFactory.getLog(OperationalInfoChildTable.class);

  private static String insertSQL = null;

  static {
    StringBuffer sql = null;
    sql = new StringBuffer(256); // @todo right size?
    sql.append("INSERT INTO OPERATIONAL_INFO_CHILD_TABLE_CLASS ");
    sql.append("(PARENT_ENTITY, CHILD_ENTITY)");
    sql.append(" VALUES ");
    sql.append("(?,?)");
    insertSQL = sql.toString();
  }

  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/
  public static void main(String [] args) {
    OperationalInfoChildTable.test();
  }
  public static void test() {
    System.out.println("insertSQL: [" + insertSQL + "]");
  }
}