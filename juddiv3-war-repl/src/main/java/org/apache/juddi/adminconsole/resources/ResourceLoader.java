/*
 * Copyright 2001-2008 The Apache Software Foundation.
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
package org.apache.juddi.adminconsole.resources;

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
         * @throws MissingResourceException if the resource bundle can't be
         * found
         */
        public static String GetResource(HttpSession session, String key) throws MissingResourceException {
                if (key == null) {
                        throw new IllegalArgumentException("key");
                }
                String locale = (String) session.getAttribute("locale");
                return GetResource(locale, key);
        }

        /**
         * returns a localized string in the locale defined within locale or in
         * the default locale, en
         *
         * @param locale
         * @param key
         * @return a localized string
         * @throws IllegalArgumentException if the key is null
         * @throws MissingResourceException if the resource bundle can't be
         * found
         */
        public static String GetResource(String locale, String key) throws MissingResourceException {
                if (key == null) {
                        throw new IllegalArgumentException("key");
                }

                ResourceBundle bundle = (ResourceBundle) map.get(locale);
                if (bundle == null) {
                        bundle = ResourceBundle.getBundle("org.apache.juddi.adminconsole.resources.web", new Locale(locale));
                        map.put(locale, bundle);
                }
                try {
                        return bundle.getString(key.trim());
                } catch (Exception ex) {
                        return "key " + key + " not found " + ex.getMessage();
                }
        }
}
