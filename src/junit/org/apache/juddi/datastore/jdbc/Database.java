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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.util.Config;


/**
 * @author Steve Viens (sviens@apache.org)
 */
public class Database
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(Database.class);

  private static final String JDBC_DRIVER_KEY = "juddi.jdbcDriver";
  private static final String DEFAULT_JDBC_DRIVER = "com.mysql.jdbc.Driver";

  private static final String JDBC_URL_KEY = "juddi.jdbcURL";
  private static final String DEFAULT_JDBC_URL = "jdbc:mysql://localhost/juddi";

  private static final String JDBC_USERNAME_KEY = "juddi.jdbcUser";
  private static final String DEFAULT_JDBC_USERNAME = "juddi";

  private static final String JDBC_PASSWORD_KEY = "juddi.jdbcPassword";
  private static final String DEFAULT_JDBC_PASSWORD = "juddi";

  /**
   *
   */
  public static Connection aquireConnection()
    throws SQLException
  {
    // grab the JDBC properties we'll need to setup 
  	// the connection pool.
  	
    String jdbcDriver = Config.getStringProperty(JDBC_DRIVER_KEY,DEFAULT_JDBC_DRIVER);
    String jdbcURL = Config.getStringProperty(JDBC_URL_KEY,DEFAULT_JDBC_URL);
    String jdbcUser = Config.getStringProperty(JDBC_USERNAME_KEY,DEFAULT_JDBC_USERNAME);
    String jdbcPassword = Config.getStringProperty(JDBC_PASSWORD_KEY,DEFAULT_JDBC_PASSWORD);

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
