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

import javax.xml.registry.BulkResponse;
import javax.xml.registry.BusinessLifeCycleManager;
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.CapabilityProfile;
import javax.xml.registry.Connection;
import javax.xml.registry.DeclarativeQueryManager;
import javax.xml.registry.RegistryService;
import javax.xml.registry.InvalidRequestException;
import javax.xml.registry.JAXRException;
import javax.xml.registry.UnsupportedCapabilityException;
import javax.xml.registry.infomodel.ClassificationScheme;

/**
 * Scout implementation of javax.xml.registry.RegistryService
 ** For futher details, look into the JAXR API Javadoc.
 * @author Anil Saldhana  <anil@apache.org>
 */
public class RegistryServiceImpl implements RegistryService {
    
    private BusinessLifeCycleManagerImpl blcm = null;
    private BusinessQueryManagerImpl  bqm = null;
    
    private ConnectionImpl conn = null;
    
    /** Creates a new instance of RegistryServiceImpl */
    public RegistryServiceImpl() {
       // blcm.setRegistryService( this );
        //bqm.setRegistryService( this);
    }
    
    public BulkResponse getBulkResponse(String str) 
    throws InvalidRequestException, JAXRException {
         return null;
    }
    
    public  BusinessLifeCycleManager getBusinessLifeCycleManager() 
    throws  JAXRException {
        if( blcm == null) blcm = new BusinessLifeCycleManagerImpl();
        if( conn != null ) blcm.setConnection(conn);
        return blcm;
    }
    
    public  BusinessQueryManager getBusinessQueryManager() 
    throws  JAXRException {
        if( bqm == null) bqm = new BusinessQueryManagerImpl();
        if( conn != null ) bqm.setConnection(conn);
        return bqm;
    }
    
    public  CapabilityProfile getCapabilityProfile() throws  JAXRException {
         return null;
    }
    
    public  DeclarativeQueryManager getDeclarativeQueryManager() 
    throws  JAXRException,  UnsupportedCapabilityException {
         return null;
    }
    
    public  ClassificationScheme getDefaultPostalScheme() 
    throws  JAXRException {
         return null;
    }
    
    public String makeRegistrySpecificRequest(String str) throws  JAXRException {
         return null;
    }
    
    /** Package Protected **/
      Connection getConnection() { return conn; }
    
      void setConnection( ConnectionImpl con ){
        conn = con;
    }
    
}
