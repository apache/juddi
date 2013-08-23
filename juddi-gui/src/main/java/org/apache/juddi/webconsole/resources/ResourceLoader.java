/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.juddi.webconsole.resources;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.servlet.http.HttpSession;

/**
 * This a resource loader for specific locales for internationalization,
 * provides some basic caching to prevent round trip disk access
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class ResourceLoader {

    private static Map map = new HashMap();

    /**
     * returns a localized string in the locale defined within
     * session.getAttribute("locale") or in the default locale, en
     *
     * @param session
     * @param key
     * @return a localized string
     * @throws IllegalArgumentException if the key is null
     * @throws MissingResourceException if the resource bundle can't be found
     */
    public static String GetResource(HttpSession session, String key) throws MissingResourceException {
        if (key == null) {
            throw new IllegalArgumentException("key");
        }
        String locale = (String) session.getAttribute("locale");
        return GetResource(locale, key);
    }

    /**
     * returns a localized string in the locale defined within locale or in the
     * default locale, en
     *
     * @param session
     * @param key
     * @return a localized string
     * @throws IllegalArgumentException if the key is null
     * @throws MissingResourceException if the resource bundle can't be found
     */
    public static String GetResource(String locale, String key) throws MissingResourceException {
        if (key == null) {
            throw new IllegalArgumentException("key");
        }

        ResourceBundle bundle = (ResourceBundle) map.get(locale);
        if (bundle == null) {
            bundle = ResourceBundle.getBundle("org.apache.juddi.webconsole.resources.web", new Locale(locale));
            map.put(locale, bundle);
        }
        try {
            return bundle.getString(key.trim());
        } catch (Exception ex) {
            return "key " + key + " not found " + ex.getMessage();
        }
    }
}
