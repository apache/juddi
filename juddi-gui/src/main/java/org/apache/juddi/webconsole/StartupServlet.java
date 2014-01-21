/*
 * Copyright 2001-2013 The Apache Software Foundation.
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
 *
 */
package org.apache.juddi.webconsole;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;

/**
 * This startup servlet's job is to generate an encryption key which will be
 * used for encrypting cached user credentials in the http session object
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class StartupServlet implements javax.servlet.ServletContextListener {

        static final Logger log = Logger.getLogger(StartupServlet.class.getCanonicalName());

        /**
         * creates a new AES key and stores it to the properties files
         *
         * @param sce
         */
        public void contextInitialized(ServletContextEvent sce) {
                log.info("juddi-gui startup");
                FileOutputStream fos = null;
                try {
                        //URL resource = sce.getServletContext().getResource("/META-INF/config.properties");
                        Properties p = new Properties();

                        log.info("Attempting to generate 256 bit AES key");
                        boolean ok = false;
                        String key = AES.GEN(256);
                        if (key == null) {
                                ok = false;
                        } else {
                                if (AES.ValidateKey(key)) {
                                        log.info("Generation of 256 bit AES key successful");
                                        ok = true;
                                } else {
                                        log.warning("256 bit key validation failed. To use higher key sizes, try installing the Java Cryptographic Extensions (JCE) Unlimited Strength");
                                }
                        }
                        if (!ok) {
                                log.info("Attempting to generate 128 bit AES key");
                                key = AES.GEN(128);
                                if (key == null) {
                                        log.log(Level.SEVERE, "128 bit key generation failed! user's won't be able to login!");
                                        return;
                                } else if (AES.ValidateKey(key)) {
                                        log.info("Generation of 128 bit AES key successful");
                                } else {
                                        log.severe("128 bit key validation failed! giving up, user's won't be able to login! ");
                                        return;

                                }
                        }

                        p.put("key", key);
                        fos = new FileOutputStream(sce.getServletContext().getRealPath("/META-INF/config.properties"));

                        log.log(Level.INFO, "Storing key to " + sce.getServletContext().getRealPath("/META-INF/config.properties"));
                        p.store(fos, "No comments");
                        fos.flush();
                        fos.close();
                } catch (Exception ex) {
                        log.log(Level.WARNING, null, ex);
                        try {
                                if (fos != null) {
                                        fos.close();
                                }
                        } catch (Exception e) {
                        }
                }
        }

        /**
         * does nothing
         *
         * @param sce
         */
        public void contextDestroyed(ServletContextEvent sce) {
                FileOutputStream fos = null;
                try {
                        log.info("Cleaning up juddi-gui");
                        Properties p = new Properties();
                        InputStream is = sce.getServletContext().getResourceAsStream("/META-INF/config.properties");
                        p.load(is);
                        p.remove("key");
                        is.close();
                        fos = new FileOutputStream(sce.getServletContext().getRealPath("/META-INF/config.properties"));
                        p.store(fos, "No comments");
                        fos.flush();
                        fos.close();
                } catch (Exception ex) {
                        log.log(Level.WARNING, null, ex);
                        try {
                                if (fos != null) {
                                        fos.close();
                                }
                        } catch (Exception e) {
                        }
                }
                try {
                        sce.getServletContext().removeAttribute("username");
                        sce.getServletContext().removeAttribute("password");
                        sce.getServletContext().removeAttribute("locale");
                        sce.getServletContext().removeAttribute("hub");
                } catch (Exception ex) {
                        log.log(Level.WARNING, null, ex);
                }

        }
}
