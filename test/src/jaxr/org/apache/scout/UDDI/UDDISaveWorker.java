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
import java.util.Vector;
 
import javax.xml.registry.JAXRException;
import javax.xml.registry.JAXRResponse;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.Service;

import org.apache.scout.registry.BulkResponseImpl;
import org.apache.scout.registry.ConnectionImpl;
import org.apache.scout.registry.infomodel.KeyImpl;
import org.uddi4j.UDDIException;
import org.uddi4j.client.UDDIProxy;
import org.uddi4j.datatype.business.BusinessEntity;
import org.uddi4j.datatype.service.BusinessService;
import org.uddi4j.response.AuthToken;
import org.uddi4j.response.BusinessDetail;
import org.uddi4j.response.ServiceDetail;
import org.uddi4j.transport.TransportException;

/**
 * Helper class dealing with saving objects in UDDI Regisrty
 * @author Anil Saldhana  <anil@apache.org>
 */
public class UDDISaveWorker {
    private UDDIHelper uddi = new UDDIHelper();
    
//  --------------------------------------------------------------------------
    /*        SAVE  METHODS        */
     //-----------------------------------------------------------------------
     
    /**
     * Save businesses in the UDDI Registry.
     */    
     public BulkResponseImpl save_business( UDDIProxy proxy,
             Collection orgs, ConnectionImpl connection,
             AuthToken token)
     throws JAXRException {
         System.out.println( "Method:save_business:Args length="+orgs.size() );
         BulkResponseImpl bulk = new BulkResponseImpl();
         Vector entityvect = new Vector();
         
         Collection coll = new ArrayList();
         Collection exceptions = new ArrayList();
        
         try{
             token = proxy.get_authToken( connection.getUserName(),
                  connection.getPassword() );
             Iterator iter = orgs.iterator();
             while( iter.hasNext()){
                 BusinessEntity en = 
                   uddi.getBusinessEntityFromJAXROrg( (Organization)iter.next());
                 entityvect.add(en);
             }
             System.out.println( "Method:save_business: ENlength="+entityvect.size() );
             // Save business
             BusinessDetail bd = proxy.save_business(
                     token.getAuthInfoString(),  entityvect);  
             
             entityvect = bd.getBusinessEntityVector();
             System.out.println( "After Saving Business. Obtained vector size:"+
                 entityvect.size());
             for( int i = 0 ; entityvect != null && i < entityvect.size(); i++){
                 BusinessEntity entity = (BusinessEntity)entityvect.elementAt(i);
                 coll.add( new KeyImpl(entity.getBusinessKey() ));
             }
             
             bulk.setCollection( coll );  
             bulk.setExceptions( exceptions);               
          }catch( UDDIException ud){
                 bulk.setStatus( JAXRResponse.STATUS_FAILURE);
                 throw new JAXRException( "Apache JAXR Impl:", ud);
             }catch( TransportException tran){
                 throw new JAXRException( "Apache JAXR Impl:",tran);
             }   
          return bulk;     
     }
     /**
     * Save services in the UDDI Registry.
     */ 
     public BulkResponseImpl save_service( UDDIProxy proxy,Collection services,
             ConnectionImpl connection,
             AuthToken token)
     throws JAXRException {
         BulkResponseImpl bulk = new BulkResponseImpl();
         Vector svect = new Vector();
         
         Collection coll = new ArrayList();
         Collection exceptions = new ArrayList();
        
         try{
             token = proxy.get_authToken( connection.getUserName(),
                  connection.getPassword() );
             Iterator iter = services.iterator();
             while( iter.hasNext()){
                 BusinessService bs = 
                   uddi.getBusinessServiceFromJAXRService( (Service)iter.next());
                 svect.add(bs);
             }
             // Save Service
             ServiceDetail sd = proxy.save_service(
                     token.getAuthInfoString(),  svect);  
             svect = sd.getBusinessServiceVector();
             for( int i = 0 ; svect != null && i < svect.size(); i++){
                 BusinessService entity = (BusinessService)svect.elementAt(i);
                 coll.add( new KeyImpl(entity.getBusinessKey() ));
             }
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
