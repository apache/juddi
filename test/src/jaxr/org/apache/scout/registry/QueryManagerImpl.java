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

import javax.xml.registry.BulkResponse;
import javax.xml.registry.JAXRException;
import javax.xml.registry.QueryManager;
import javax.xml.registry.RegistryService;
import javax.xml.registry.infomodel.RegistryObject;

/** 
 * Implements JAXR QueryManager Interface.
 * For futher details, look into the JAXR API Javadoc. 
 * @author Anil Saldhana  <anil@apache.org>
 */
public class QueryManagerImpl 
implements QueryManager {
    protected RegistryService regservice = new RegistryServiceImpl();

    /* (non-Javadoc)
     * @see javax.xml.registry.QueryManager#getRegistryObject(java.lang.String, java.lang.String)
     */
    public RegistryObject getRegistryObject(String arg0, String arg1)
    throws JAXRException {
        // TODO 
        return null;
    }
    /* (non-Javadoc)
     * @see javax.xml.registry.QueryManager#getRegistryObject(java.lang.String)
     */
    public RegistryObject getRegistryObject(String arg0) 
    throws JAXRException {
        // TODO 
        return null;
    }
    /* (non-Javadoc)
     * @see javax.xml.registry.QueryManager#getRegistryObjects()
     */
    public BulkResponse getRegistryObjects() 
    throws JAXRException {
        // TODO 
        return null;
    }
    /* (non-Javadoc)
     * @see javax.xml.registry.QueryManager#getRegistryObjects(java.util.Collection, java.lang.String)
     */
    public BulkResponse getRegistryObjects(Collection arg0, String arg1)
    throws JAXRException {
        // TODO 
        return null;
    }
    /* (non-Javadoc)
     * @see javax.xml.registry.QueryManager#getRegistryObjects(java.util.Collection)
     */
    public BulkResponse getRegistryObjects(Collection arg0)
    throws JAXRException {
        // TODO 
        return null;
    }
    /* (non-Javadoc)
     * @see javax.xml.registry.QueryManager#getRegistryObjects(java.lang.String)
     */
    public BulkResponse getRegistryObjects(String arg0) 
    throws JAXRException {
        // TODO 
        return null;
    }
    /* (non-Javadoc)
     * @see javax.xml.registry.QueryManager#getRegistryService()
     */
    public RegistryService getRegistryService() 
    throws JAXRException {
        // TODO 
        return regservice;
    }
     
    /**
     * @param regservice The regservice to set.
     */
    void setRegservice(RegistryService reg ) {
        this.regservice = reg ;
    }
}
