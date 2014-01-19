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
package org.apache.juddi.v3.client.cryptor;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.ClassUtil;
import org.apache.juddi.v3.client.config.Property;

/**
 * Used to create the org.apache.juddi.cryptor.Cryptor implementation as
 * specified by the 'juddi.cryptor' property. Defaults to
 * org.apache.juddi.cryptor.DefaultCryptor if an implementation is not
 * specified.
 *
 * @author Steve Viens (sviens@apache.org)
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public abstract class CryptorFactory {

    private static final Log log = LogFactory.getLog(CryptorFactory.class);
    // the shared Cryptor instance
    private static Cryptor cryptor = null;
    private static Map<String, Cryptor> cache = new HashMap<String, Cryptor>();

    /**
     * Returns a crypto provider with the matching classname, throws if not possible
     * @param className
     * @return a Cryptor instance
     * @throws Exception 
     */
    public static Cryptor getCryptor(String className) throws Exception {
        if (cache.containsKey(className)) {
            return cache.get(className);
        }
        Class<?> cryptorClass = null;
        try {
            // Use Loader to locate & load the Cryptor implementation
            cryptorClass = ClassUtil.forName(className, CryptorFactory.class);
        } catch (ClassNotFoundException e) {
            log.error("The specified Cryptor class '" + className + "' was not found in classpath.");
            log.error(e);
            throw e;
        }

        try {
            // try to instantiate the Cryptor implementation
            cryptor = (Cryptor) cryptorClass.newInstance();
            cache.put(className, cryptor);
        } catch (Exception e) {
            log.error("Exception while attempting to instantiate the implementation of Cryptor: " + cryptorClass.getName() + "\n" + e.getMessage());
            log.error(e);
            throw e;
        }

        return cryptor;
    }

    
    /**
     * Returns a new instance of a CryptorFactory.
     * 
     * @return Cryptor
     */
    public static synchronized Cryptor getCryptor() {
        if (cryptor == null) {
            cryptor = createCryptor();
        }
        return cryptor;
    }
    
    /*
     * Returns a new instance of a Cryptor.
     * 
     * @return Cryptor
     */
    private static synchronized Cryptor createCryptor() {
        if (cryptor != null) {
            return cryptor;
        }

        // grab class name of the Cryptor implementation to create
        String className = Property.DEFAULT_CRYPTOR;
       
        // write the Cryptor implementation name to the log
        log.debug("Cryptor Implementation = " + className);

        Class<?> cryptorClass = null;
        try {
            // Use Loader to locate & load the Cryptor implementation
            cryptorClass = ClassUtil.forName(className, CryptorFactory.class);
        } catch (ClassNotFoundException e) {
            log.error("The specified Cryptor class '" + className + "' was not found in classpath.");
            log.error(e);
        }

        if (cryptorClass != null) {
            try {
                // try to instantiate the Cryptor implementation
                cryptor = (Cryptor) cryptorClass.newInstance();
            } catch (Exception e) {
                log.error("Exception while attempting to instantiate the implementation of Cryptor: " + cryptorClass.getName() + "\n" + e.getMessage());
                log.error(e);
            }
        }

        return cryptor;
    }
}