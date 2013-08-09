/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.juddi.webconsole;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;
import javax.servlet.ServletContextEvent;

/**
 * This startup servlet's job is to generate an encryption key which will be used for encrypting
 * cached user credentials in the http session object
 * @author Alex O'Ree
 */
public class StartupServlet implements javax.servlet.ServletContextListener {

    /**
     * creates a new AES key and stores it to the properties files
     * @param sce 
     */
    public void contextInitialized(ServletContextEvent sce) {
        FileOutputStream fos = null;
        try {
            //URL resource = sce.getServletContext().getResource("/META-INF/config.properties");
            Properties p = new Properties();
            InputStream is = sce.getServletContext().getResourceAsStream("/WEB-INF/config.properties");
            p.load(is);
            p.remove("key");
            String key = AES.GEN(256);
            if (key == null) {
                key = AES.GEN(128);
            }
            p.put("key", key);
            fos = new FileOutputStream(sce.getServletContext().getRealPath("/WEB-INF/config.properties"));

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
     * @param sce 
     */
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
