/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
                log.info("juddi-admin gui startup");
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
                                        log.info("256 bit key validation failed.");
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
                        fos = new FileOutputStream(sce.getServletContext().getRealPath("/WEB-INF/config.properties"));

                        log.log(Level.INFO, "Storing key to " + sce.getServletContext().getRealPath("/WEB-INF/config.properties"));
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
                        log.info("Cleaning up juddi-admin");
                        Properties p = new Properties();
                        InputStream is = sce.getServletContext().getResourceAsStream("/WEB-INF/config.properties");
                        p.load(is);
                        p.remove("key");
                        is.close();
                        fos = new FileOutputStream(sce.getServletContext().getRealPath("/WEB-INF/config.properties"));
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
