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
package org.apache.scout.UDDI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.registry.JAXRException;

import org.apache.scout.registry.BulkResponseImpl;
import org.apache.scout.registry.ConnectionImpl;
import org.apache.scout.registry.infomodel.KeyImpl;

import org.uddi4j.UDDIException;
import org.uddi4j.client.UDDIProxy;
import org.uddi4j.response.AuthToken;
import org.uddi4j.response.DispositionReport;
import org.uddi4j.transport.TransportException;

/**
 * Utility Class that deals with deletion of UDDI Objects.
 * @author Anil Saldhana  <anil@apache.org>
 */
public class UDDIDeleteWorker {
    private JAXRHelper jaxr = new JAXRHelper();
     
    /**
     * Delete businesses with given business keys.
     */    
    public BulkResponseImpl delete_business( UDDIProxy proxy,
            Collection orgkeys, ConnectionImpl connection,
            AuthToken token)
    throws JAXRException {
        BulkResponseImpl bulk = new BulkResponseImpl();
        Collection coll = new ArrayList();
        Collection exceptions = new ArrayList();
        
        try{
            token = proxy.get_authToken( connection.getUserName(),
                 connection.getPassword() );
            Iterator iter = orgkeys.iterator();
            while( iter.hasNext()){
                KeyImpl key = (KeyImpl)iter.next();
                String keyid =  key.getId();
               
                // Delete business 
                DispositionReport  dr = proxy.delete_business(
                                token.getAuthInfoString(),  keyid  ); 
                
                if( dr.success()) { coll.add( key ); continue;}      
                
                exceptions.addAll(jaxr.prepareBulkResponseExceptions( dr, key)); 
            
            }//while iter
            
            bulk.setCollection( coll );  
            bulk.setExceptions( exceptions);
              
         }catch( UDDIException ud){
                throw new JAXRException( "Apache JAXR Impl:", ud);
            }catch( TransportException tran){
                throw new JAXRException( "Apache JAXR Impl:",tran);
            }   
         return bulk;     
    }
    
    /**
     * Delete services with given business keys.
     */  
    public BulkResponseImpl delete_service( UDDIProxy proxy,
            Collection skeys, ConnectionImpl connection,
            AuthToken token)
    throws JAXRException {
        BulkResponseImpl bulk = new BulkResponseImpl();
        Collection coll = new ArrayList();
        Collection exceptions = new ArrayList();
        
        try{
             token = proxy.get_authToken( connection.getUserName(),
                 connection.getPassword() );
            Iterator iter = skeys.iterator();
            while( iter.hasNext()){
                KeyImpl key = (KeyImpl)iter.next();
                String keyid =  key.getId();
               
                // Delete business 
                DispositionReport  dr = proxy.delete_service(
                                token.getAuthInfoString(),  keyid  ); 
                
                if( dr.success()) { coll.add( key ); continue;}       
                
                exceptions.addAll(jaxr.prepareBulkResponseExceptions( dr, key));            
            }//while iter
            
            bulk.setCollection( coll );  
            bulk.setExceptions( exceptions);
              
         }catch( UDDIException ud){
                throw new JAXRException( "Apache JAXR Impl:", ud);
            }catch( TransportException tran){
                throw new JAXRException( "Apache JAXR Impl:",tran);
            }   
         return bulk;     
    }
   
}
