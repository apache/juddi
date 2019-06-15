/*
 * Copyright 2019 The Apache Software Foundation.
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
package org.apache.juddi.security;


import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.Property;

/**
 * Provides an accessor to the access control mechanism
 * @since 3.4
 * @author Alex O'Ree
 */
public class AccessControlFactory {

    private static final Logger log = Logger.getLogger(AccessControlFactory.class.getName());
    private static IAccessControl instance = null;

    /**
     * Gets an instance of IAccessControl using the following procedure, in order<br>
     * <ol>
     * <li>Using the judiv3.properties configuration file setting Property.JUDDI_FINE_GRAIN_ACCESS_CONTROL_PROVIDER</li>
     * <li>If the previous fails to load or is not defined, DefaultCorseAccessControlImpl</li>
     * </ol>
     * @return should never return null or throw exceptions
     * @see DefaultCorseAccessControlImpl
     * @see Property
     */
    public static IAccessControl getAccessControlInstance() {
        if (instance != null) {
            return instance;
        }
        String clazz = null;
        try {
            clazz = AppConfig.getConfiguration().getString(Property.JUDDI_ACCESS_CONTROL_PROVIDER, AllowAllAccessControlImpl.class.getCanonicalName());
            Class c = Class.forName(clazz);
            IAccessControl ret = (IAccessControl) c.newInstance();
            log.log(Level.INFO, "Successfully loaded FineGrainedAccessControl provider {0}", clazz);
            instance = ret;
            return ret;
        } catch (IllegalAccessException x) {
            log.log(Level.WARNING, "error loading control provider " + clazz, x);
        } catch (InstantiationException x) {
            log.log(Level.WARNING, "error loading control provider " + clazz, x);
        } catch (ExceptionInInitializerError x) {
            log.log(Level.WARNING, "error loading control provider " + clazz, x);
        } catch (SecurityException x) {
            log.log(Level.WARNING, "error loading control provider " + clazz, x);
        } catch (ClassNotFoundException x) {
            log.log(Level.WARNING, "error loading control provider " + clazz, x);
        } catch (ConfigurationException x) {
            log.log(Level.WARNING, "error loading control provider " + clazz, x);
        } catch (Exception x) {
            log.log(Level.WARNING, "error loading control provider " + clazz, x);
        }
        return new AllowAllAccessControlImpl();
    }
}