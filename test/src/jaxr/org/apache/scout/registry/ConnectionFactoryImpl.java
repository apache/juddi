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

package org.apache.scout.registry;

import java.util.Collection;
import java.util.Properties;

import javax.xml.registry.*;

 

/**
 * Extends the ConnectionFactory class of JAXR
 * For futher details, look into the JAXR API Javadoc.
 * @author Anil Saldhana  <anil@apache.org>
 */
public class ConnectionFactoryImpl extends ConnectionFactory {
    private static Properties conprops = null;
    private static ConnectionFactoryImpl factory= null;
    
    /** Creates a new instance of ConnectionFactoryImpl */
    private ConnectionFactoryImpl() {
    }
    
    public  Connection createConnection() throws  JAXRException {
         ConnectionImpl conn = new ConnectionImpl();
         conn.setProperties(conprops );
         return conn;
    }
    
    public  FederatedConnection createFederatedConnection(Collection collection)
    throws JAXRException {
        //For now return Null
        return null;
    }
    
    public  Properties getProperties() throws JAXRException {
         return conprops;
    }
    
    public void setProperties(Properties properties) 
    throws JAXRException {
         conprops = properties;
    }
    
    public static ConnectionFactory newInstance() {
        if(factory == null )  factory = new ConnectionFactoryImpl();
        return factory;
    }
    
}
