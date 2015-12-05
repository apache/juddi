/*
 * Copyright 2015 The Apache Software Foundation.
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
package org.apache.juddi.v3.client.config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author alex
 */
public class UDDIServletContextListener implements ServletContextListener {

     public static final Log logger = LogFactory.getLog(UDDIServletContextListener.class);
     private static final String JUDDI_SERVLET_NAME = "juddi-servlet";

     @Override
     public void contextInitialized(ServletContextEvent servletContextEvent) {
          logger.info("Registering JUDDI servlet '" + JUDDI_SERVLET_NAME + "' to automatically register service");
          ServletContext servletContext = servletContextEvent.getServletContext();
          boolean success = false;
          try {
               configureUddiClient(servletContext);
               Method m = ServletContext.class.getMethod("addServlet", String.class, Servlet.class);
               if (m != null) {
                    //should have one of these 
                    // ServletRegistration.Dynamic dynamic = servletContext.addServlet(JUDDI_SERVLET_NAME, UDDIClerkServlet.class);
                    Object obj = m.invoke(servletContext, JUDDI_SERVLET_NAME, UDDIClerkServlet.class);
                    Method setLoadOnStartup = obj.getClass().getMethod("setLoadOnStartup", int.class);
                    if (setLoadOnStartup != null) {
                         setLoadOnStartup.invoke(obj, 1);
                         success = true;
                    }
               }
          } catch (NoSuchMethodException ex) {
               logger.debug("Probably not in a servlet container > v3.0. probably time to upgrade?", ex);
          } catch (SecurityException ex) {
               logger.warn("Probably not in a servlet container > v3.0. probably time to upgrade?", ex);
          } catch (IllegalAccessException ex) {
               logger.warn("Probably not in a servlet container > v3.0. probably time to upgrade?", ex);
          } catch (IllegalArgumentException ex) {
               logger.warn("Probably not in a servlet container > v3.0. probably time to upgrade?", ex);
          } catch (InvocationTargetException ex) {
               logger.warn("Probably not in a servlet container > v3.0. probably time to upgrade?", ex);
          } catch (Throwable ex) {
               logger.warn("Probably not in a servlet container > v3.0. probably time to upgrade?", ex);
          }
          if (success) {
               logger.info("Successfully automatic registration of the UDDIClerkServlet using servlet spec 3.0+.");
          } else {
               logger.info("NOT Successful - autoregisteration the UDDIClerkServlet using servlet spec 3.0+. You'll have to set this up using web.xml if desired. See error log for additional details.");
          }
          //ServletRegistration.Dynamic dynamic = servletContext.addServlet(JUDDI_SERVLET_NAME, UDDIClerkServlet.class);
          //dynamic.setLoadOnStartup(1);
     }

     /**
      * override this function if you want to do some custom configurations
      * before the juddi config files are loaded
      *
      * @param servletContext
      */
     public void configureUddiClient(ServletContext servletContext) {

     }

     @Override
     public void contextDestroyed(ServletContextEvent servletContextEvent) {
     }

}
