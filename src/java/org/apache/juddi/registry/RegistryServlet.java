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
package org.apache.juddi.registry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This servlet is ONLY used to initialize the jUDDI webapp on
 * startup and cleanup the jUDDI webapp on shutdown.
 * 
 * @author Steve Viens (sviens@apache.org)
 */
public class RegistryServlet extends HttpServlet
{
  // xml config file name.
  private static final String PROPERTY_FILE_NAME = "juddi.properties";

  // private reference to the webapp's logger.
  private static Log log = LogFactory.getLog(RegistryServlet.class);

  // file containing the webapp's properties/state
  private static File propertyFile = null;
  
  // registry singleton instance
  private static RegistryEngine registry = null;

  /**
   * Grab the shared instance of jUDDI's Registry class
   * (this will typically create the registry for the first
   * time) and call it's "init()" method to get all core
   * components initialized.
   */
  public void init(ServletConfig config) 
    throws ServletException
  {
    super.init(config);    

    log.info("jUDDI Starting: Initializing resources and subsystems.");

    // Import jUDDI configuration from the 
    // juddi.properties file found in the 
    // WEB-INF directory.

    StringBuffer propFilePath = new StringBuffer(255);
    propFilePath.append(getServletContext().getRealPath("/"));
    propFilePath.append(File.separator);
    propFilePath.append("WEB-INF");
    propFilePath.append(File.separator);
    propFilePath.append(PROPERTY_FILE_NAME);
      
    // Store path to the webapp's properties 
    // file in a static location.
    
    RegistryServlet.propertyFile = new File(propFilePath.toString());
  }

  /**
   * Grab the shared instance of jUDDI's Registry class and
   * call it's "dispose()" method to notify all sub-components
   * to stop any background threads and release any external
   * resources they may have aquired.
   */
  public void destroy()
  {
    super.destroy();
    
    log.info("jUDDI Stopping: Cleaning up existing resources.");

    RegistryEngine registry = RegistryServlet.getRegistry();
    if (registry != null)
      registry.dispose();
  }

  /**
   *
   */
  public static RegistryEngine getRegistry()
  {
    if (registry == null)
      registry = createRegistry();
    return registry;
  }

  /**
   *
   */
  private static synchronized RegistryEngine createRegistry()
  {
    if (registry != null)
      return registry;
    
    Properties props = new Properties();

    try
    {      
      if (propertyFile.exists())
        props.load(new FileInputStream(propertyFile));
      else
      {
        props.put("juddi.operatorName","Apache.org");        
        props.put("juddi.operatorSiteURL","http://localhost:8080/juddi");
        props.put("juddi.adminEmailAddress","nobody@apache.org");
        props.put("juddi.dataSource","java:comp/env/jdbc/juddiDB");
        props.put("juddi.useConnectionPool","false"); // ??
              
        props.put("juddi.auth","org.apache.juddi.auth.DefaultAuthenticator");
        props.put("juddi.cryptor","org.apache.juddi.cryptor.DefaultCryptor");
        props.put("juddi.dataStore","org.apache.juddi.datastore.jdbc.JDBCDataStore");
        //props.put("juddi.monitor","org.apache.juddi.monitor.jdbc.JDBCMonitor");
        props.put("juddi.uuidgen","org.apache.juddi.uuidgen.DefaultUUIDGen");
        props.put("juddi.validator","org.apache.juddi.validator.DefaultValidator");  

        props.put("juddi.maxNameLength","255");
        props.put("juddi.maxNameElementsAllowed","5");
        props.put("juddi.maxBusinessEntitiesPerUser","25");
        props.put("juddi.maxBusinessServicesPerBusiness","20");
        props.put("juddi.maxBindingTemplatesPerService","10");
        props.put("juddi.maxTModelsPerUser","100");
        props.put("juddi.maxRowsLimit","10");
        
        props.store(new FileOutputStream(propertyFile),"");
      }
    }
    catch(IOException ioex) {
      log.error(ioex.getMessage(),ioex);
    }

    registry = new RegistryEngine(props);
    registry.init();

    return registry;
  }
}