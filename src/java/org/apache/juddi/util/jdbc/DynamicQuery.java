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
package org.apache.juddi.util.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Vector;

/**
 * @author Steve Viens (steve@viens.net)
 */
public class DynamicQuery
{
  /**
   * Vector of all SQL values 
   */
  private Vector values = null;
  private StringBuffer sql = null;

  /**
   * default constructor
   */
  public DynamicQuery()
  {
    this.values = new Vector();
    this.sql = new StringBuffer();
  } 
  
  public DynamicQuery(String sql)
  {
    this.values = new Vector();
    this.sql = new StringBuffer(sql);
  }
  
  public void append(String sql)
  {
    this.sql.append(sql);
  }
  
  public void addValue(Object obj)
  {
    this.values.addElement(obj);
  }
  
  public PreparedStatement buildPreparedStatement(Connection connection)
    throws SQLException
  {
    PreparedStatement stmt = connection.prepareStatement(sql.toString());
    for (int i=0; i<values.size(); i++)
      stmt.setObject(i+1,values.elementAt(i));
    
    return stmt;
  }
  
  public String toString()
  { 
    StringBuffer buffer = new StringBuffer(sql.toString());
    buffer.append("\n\n");
    
    for (int i=0; i<values.size(); i++)
    {
      Object obj = values.elementAt(i);
      
      buffer.append(i+1);
      buffer.append("\t");
      buffer.append(obj.getClass().getName());
      buffer.append("\t");
      buffer.append(obj.toString());
      buffer.append("\n");
    }
    
    return buffer.toString();
  }
}
