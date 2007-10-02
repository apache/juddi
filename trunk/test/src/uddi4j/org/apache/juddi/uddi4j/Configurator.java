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
package org.apache.juddi.uddi4j;

/*
 * The source code contained herein is licensed under the IBM Public License
 * Version 1.0, which has been approved by the Open Source Initiative.
 * Copyright (C) 2001, International Business Machines Corporation
 * Copyright (C) 2001, Hewlett-Packard Company
 * All Rights Reserved.
 *
 */

import java.util.Properties;

import org.uddi4j.transport.TransportFactory;

/**
 * Configures the environment for the UDDI4J samples.
 * <OL>
 * <LI>Reads samples property file.
 * <LI>Sets SOAP transport according to property file.
 * <LI>Configures SSL/JSSE provider
 * </OL>
 *
 * @author David Melgar (dmelgar@us.ibm.com)
 */

public class Configurator {

   /**
    * Loads configuration file. File may require
    * modification before running samples.
    *
    * @return Loaded properties object
    */
   public static Properties load() {

     ClassLoader loader = Thread.currentThread().getContextClassLoader();

//     java.io.InputStream is = loader.getResourceAsStream("samples.prop");
     java.io.InputStream is = loader.getResourceAsStream("org/apache/juddi/uddi4j/samples.prop");

      Properties config = new Properties();
      try {
         config.load(is);
      } catch (Exception e) {
         System.out.println("Error loading samples property file\n" + e.toString());
      }

      // Configure UDDI4J system properties. Normally set on commandline or elsewhere
      // SOAP transport being used
      if (System.getProperty(TransportFactory.PROPERTY_NAME)==null) {
         System.setProperty(TransportFactory.PROPERTY_NAME, config.getProperty("TransportClassName"));
      }
      // Logging
      if (System.getProperty("org.uddi4j.logEnabled")==null) {
         System.setProperty("org.uddi4j.logEnabled", config.getProperty("logEnabled"));
      }

      // Configure JSSE support
      try {
         System.setProperty("java.protocol.handler.pkgs", config.getProperty("handlerPackageName"));

         // Dynamically loads security provider based on properties. Typically configured in JRE
         java.security.Security.addProvider((java.security.Provider)
            Class.forName(config.getProperty("securityClassName")).newInstance());
      } catch (Exception e) {
         System.out.println("Error configuring JSSE provider. Make sure JSSE is in classpath.\n" + e);
      }
      return config;
   }
}
