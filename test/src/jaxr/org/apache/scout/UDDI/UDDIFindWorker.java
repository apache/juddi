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
import java.util.Vector;

import javax.xml.registry.JAXRException;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.Service;

import org.apache.scout.registry.BulkResponseImpl;
import org.uddi4j.UDDIException;
import org.uddi4j.client.UDDIProxy;
import org.uddi4j.response.BusinessInfo;
import org.uddi4j.response.BusinessList;
import org.uddi4j.response.ServiceInfo;
import org.uddi4j.response.ServiceList;
import org.uddi4j.transport.TransportException;
import org.uddi4j.util.CategoryBag;
import org.uddi4j.util.DiscoveryURLs;
import org.uddi4j.util.FindQualifiers;
import org.uddi4j.util.IdentifierBag;
import org.uddi4j.util.TModelBag;

/**
 * Helper Class that deals with finding UDDI Objects from the registry
 * @author Anil Saldhana  <anil@apache.org>
 */
public class UDDIFindWorker {
    private JAXRHelper jaxr = new JAXRHelper();

//  --------------------------------------------------------------------------
    /*        FIND  METHODS        */
     //-----------------------------------------------------------------------
     
    /**
     * Find Businesses given business keys and pattern.
     */    
     public BulkResponseImpl find_business(  UDDIProxy proxy, Vector names, 
                                            DiscoveryURLs discoveryURLs, 
                                            IdentifierBag identifierBag, 
                                            CategoryBag categoryBag, 
                                            TModelBag tModelBag, 
                                            FindQualifiers findQualifiers, 
                                            int maxRows)
     throws JAXRException {
            BulkResponseImpl bulk = new BulkResponseImpl();
            try{
                BusinessList bizList = proxy.find_business(names,
                                             discoveryURLs, identifierBag, 
                                             categoryBag, tModelBag,  
                                             findQualifiers, maxRows);  
            
                 Collection coll = new ArrayList();
                 //Create JAXR Organization Objects now
                 Vector bizInfoVect = bizList.getBusinessInfos().getBusinessInfoVector();
                 for( int i = 0; bizInfoVect!= null && i < bizInfoVect.size(); i++ )
                 {              
                     BusinessInfo bizInfo = (BusinessInfo)bizInfoVect.elementAt(i);                
                     Organization org =  jaxr.createJAXROrganization( bizInfo) ;                
                     //Add the Org to the collection
                     coll.add( org );               
                  }//end for      

                 bulk.setCollection( coll );
            
             }catch( UDDIException ud){
                 throw new JAXRException( "Apache JAXR Impl:", ud);
             }catch( TransportException tran){
                 throw new JAXRException( "Apache JAXR Impl:",tran);
             }
            
            return bulk;       
     }
     
    /**
     * Find Services given business keys and pattern.
     */   
     public BulkResponseImpl find_service( UDDIProxy proxy,String businessKey,
             Vector names, CategoryBag categoryBag, 
             TModelBag tModelBag, 
             FindQualifiers findQualifiers, 
             int maxRows) throws JAXRException {
         System.out.println( "Inside find_service in UDDIFindHelper");
         BulkResponseImpl bulk = new BulkResponseImpl();
         try{
             ServiceList serveList = proxy.find_service(businessKey,names,
                                           categoryBag, tModelBag,  
                                          findQualifiers, maxRows);  
         
              Collection coll = new ArrayList();
              //Create JAXR Organization Objects now
              Vector sInfoVect = serveList.getServiceInfos().getServiceInfoVector();
              for( int i = 0; sInfoVect!= null && i < sInfoVect.size(); i++ )
              {              
                  ServiceInfo sInfo = (ServiceInfo)sInfoVect.elementAt(i);                
                  Service serve =  jaxr.createJAXRService( sInfo) ;                
                  //Add the Org to the collection
                  coll.add( serve );               
               }//end for      

              bulk.setCollection( coll );
         
          }catch( UDDIException ud){
              throw new JAXRException( "Apache JAXR Impl:", ud);
          }catch( TransportException tran){
              throw new JAXRException( "Apache JAXR Impl:",tran);
          }
         return bulk;         
     }
}
