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
import javax.xml.registry.BusinessLifeCycleManager;
import javax.xml.registry.Connection;
import javax.xml.registry.InvalidRequestException;
import javax.xml.registry.JAXRException;
import javax.xml.registry.RegistryService;
import javax.xml.registry.infomodel.*; 

import org.apache.scout.UDDI.JAXRUDDIManager;


/**
 * Implements JAXR BusinessLifeCycleManager Interface. 
 * For futher details, look into the JAXR API Javadoc.
 * @author Anil Saldhana  <anil@apache.org>
 */
public class BusinessLifeCycleManagerImpl extends LifeCycleManagerImpl
implements BusinessLifeCycleManager {
    
     private ConnectionImpl conn = null;
     
     private JAXRUDDIManager jaxr = null;     

    
    /** Creates a new instance of BusinessLifeCycleManagerImpl */
    public BusinessLifeCycleManagerImpl() {      
    }
    
    public void confirmAssociation( Association association) 
    throws  JAXRException,  InvalidRequestException {
    }
    
    public  BulkResponse deleteAssociations(Collection collection) 
    throws  JAXRException {
        return null;
    }
    
    public  BulkResponse deleteClassificationSchemes(Collection collection) 
    throws  JAXRException {
        return null;
    }
    
    public  BulkResponse deleteConcepts(Collection collection) 
    throws  JAXRException {
        return null;
    }      
    
    /**
     * Deletes the organizations corresponding to the specified Keys.
     */
    public  BulkResponse deleteOrganizations(Collection keys) 
    throws  JAXRException {
         return jaxr.delete_business( keys );
    }
    
    public  BulkResponse deleteServiceBindings(Collection collection) 
    throws  JAXRException {
        return null;
    }
    
    public  BulkResponse deleteServices(Collection services) 
    throws  JAXRException {
        return jaxr.delete_business( services );
    }   
     
    public  BulkResponse saveAssociations(Collection collection, boolean param) 
    throws  JAXRException {
        return null;
    }
    
    public  BulkResponse saveClassificationSchemes(Collection collection) 
    throws  JAXRException {
        return null;
    }
    
    public  BulkResponse saveConcepts(Collection collection) 
    throws  JAXRException {
        return null;
    }
     
    
    public  BulkResponse saveOrganizations(Collection orgs) 
    throws  JAXRException {
       return  jaxr.save_business(orgs );         
    }
    
    public  BulkResponse saveServiceBindings(Collection collection) 
    throws  JAXRException {
        return null;
    }
    
    public  BulkResponse saveServices(Collection services) 
    throws  JAXRException {
        return  jaxr.save_service(services ); 
    }
    
    public void unConfirmAssociation(  Association association) 
    throws  JAXRException,  InvalidRequestException {
    }   
     
    
     /** Package Protected **/
      Connection getConnection() { return conn; }
    
      void setConnection( ConnectionImpl con ) throws JAXRException{
        conn = con;
        try{
             jaxr = new JAXRUDDIManager(con.getProperties());
             jaxr.setConnection( con ); 
        }catch(Exception e){
            e.printStackTrace();
            throw new JAXRException("Apache JAXR Impl::",e);
        } 
    }      
       
      public  RegistryService getRegistryService() 
      throws JAXRException {
        return conn.getRegistryService();
    }
    
}
