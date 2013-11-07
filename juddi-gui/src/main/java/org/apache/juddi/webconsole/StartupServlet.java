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

    /**
     * creates a new AES key and stores it to the properties files
     *
     * @param sce
     */
    public void contextInitialized(ServletContextEvent sce) {
        FileOutputStream fos = null;
        try {
            Logger log = Logger.getLogger(this.getClass().getCanonicalName());
            //URL resource = sce.getServletContext().getResource("/META-INF/config.properties");
            Properties p = new Properties();
            InputStream is = sce.getServletContext().getResourceAsStream("/META-INF/config.properties");
            p.load(is);
            is.close();
            p.remove("key");
            log.info("Attempting to generate 256 bit AES key");
            String key = AES.GEN(256);
            if (key == null) {
                log.info("FAILEd. Now attempting to generate 128 bit AES key");
                key = AES.GEN(128);
            }
            if (key == null) {
                log.log(Level.SEVERE, "128 bit key generation failed! user credentials may not be encrypted");
            }
            p.put("key", key);
            fos = new FileOutputStream(sce.getServletContext().getRealPath("/META-INF/config.properties"));

            p.store(fos, "No comments");
            fos.flush();
            fos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
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

            Logger log = Logger.getLogger(this.getClass().getCanonicalName());
            //URL resource = sce.getServletContext().getResource("/META-INF/config.properties");
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
            ex.printStackTrace();
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
            }
        }
    }
}
