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
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.juddi.registry.RegistryEngine;
import org.apache.juddi.util.Config;


/**
 * @author Steve Viens (sviens@apache.org)
 */
public class Database
{
  // grab the JDBC properties we'll need to setup 
  // the connection pool.
  private static String jdbcDriver = Config.getStringProperty(
		  RegistryEngine.PROPNAME_JDBC_DRIVER,RegistryEngine.DEFAULT_JDBC_DRIVER);
  private static String jdbcURL = Config.getStringProperty(
		  RegistryEngine.PROPNAME_JDBC_URL,RegistryEngine.DEFAULT_JDBC_URL);
  private static String jdbcUser = Config.getStringProperty(
		  RegistryEngine.PROPNAME_JDBC_USERNAME,RegistryEngine.DEFAULT_JDBC_USERNAME);
  private static String jdbcPassword = Config.getStringProperty(
		  RegistryEngine.PROPNAME_JDBC_PASSWORD,RegistryEngine.DEFAULT_JDBC_PASSWORD);

  /**
   *
   */
  public static Connection aquireConnection()
    throws SQLException
  {
   

    // make sure the JDBC Driver is loaded
    
    try {
    	Class.forName(jdbcDriver);
    }
    catch(ClassNotFoundException cnfex) {
      throw new SQLException("Could not locate JDBC Driver '" +
      		jdbcDriver+"' in classpath: "+cnfex.getMessage());
    }

    // okay, get and return the connection
    
    Connection connection = 
    	DriverManager.getConnection(jdbcURL,jdbcUser,jdbcPassword);
    	
    return connection;
  }
}
