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
 
import java.util.Collection; 
import java.util.Properties;
import java.util.Vector;

import javax.xml.registry.JAXRException;
 

import org.uddi4j.client.UDDIProxy;
import org.uddi4j.response.*; 
import org.uddi4j.util.*; 
import org.uddi4j.transport.TransportFactory; 

import org.apache.scout.registry.*;
/**
 * Class that handles JAXR -> UDDI4J calls
 * @author Anil Saldhana  <anil@apache.org>
 */
public class JAXRUDDIManager {   
    private ConnectionImpl connection = null;
    
    private UDDIProxy proxy = new UDDIProxy();

    private AuthToken token = null;
 
    private UDDIFindWorker finder = new UDDIFindWorker();
    private UDDISaveWorker saver = new UDDISaveWorker();
    private UDDIDeleteWorker deleter = new UDDIDeleteWorker();

    /** Creates a new instance of UDDIManager */
    public JAXRUDDIManager() {  
        if (System.getProperty(TransportFactory.PROPERTY_NAME)==null) {
         System.setProperty(TransportFactory.PROPERTY_NAME, 
                   "org.uddi4j.transport.ApacheAxisTransport");
      } 
    }    
    
    public JAXRUDDIManager( String inquiry, String publish, String userid, 
                  String passwd ) 
    throws JAXRException {       
        this();
        getProxy(inquiry,publish,userid, passwd );
    }
    
    public JAXRUDDIManager( Properties props) 
    throws JAXRException {  
        this();
        if( props == null ) 
            throw new JAXRException("Apache JAXR Impl:Connection Properties Null");
        try{
            proxy.setInquiryURL( props.getProperty("javax.xml.registry.queryManagerURL") );
            proxy.setPublishURL( props.getProperty("javax.xml.registry.lifeCycleManagerURL") );   
         }catch( Exception e){
             throw new JAXRException("Apache JAXR Impl:",e);
         }
           
    }//end method
    
    private void getProxy( String inquiry, String publish, 
                 String userid, String passwd ) 
    throws JAXRException {
         try {
               // Select the desired UDDI server node
               proxy.setInquiryURL( inquiry );
               proxy.setPublishURL( publish );

               // Pass in userid and password registered at the UDDI site
               token = proxy.get_authToken( userid, passwd);                
        }catch( Exception e){         
            e.printStackTrace();
            throw new JAXRException( "Apache JAXR Impl Error::", e); 
        }
    }
    
    
    public void setConnection( ConnectionImpl con)
    throws JAXRException {
        connection = con;
    }  
    
   //--------------------------------------------------------------------------
   /*        FIND  METHODS        */
    //-----------------------------------------------------------------------
    /**
     * Finds Businesses with give business key.
     */
    public BulkResponseImpl find_business( Vector names, 
                                           DiscoveryURLs discoveryURLs, 
                                           IdentifierBag identifierBag, 
                                           CategoryBag categoryBag, 
                                           TModelBag tModelBag, 
                                           FindQualifiers findQualifiers, 
                                           int maxRows)
    throws JAXRException {
        return finder.find_business(proxy,names,discoveryURLs,
                identifierBag,categoryBag,
                tModelBag,findQualifiers, maxRows );               
    }
    /**
     * Finds Services with give business key.
     */
    public BulkResponseImpl find_service( String businessKey,
                         Vector names, CategoryBag categoryBag, 
                         TModelBag tModelBag, 
                         FindQualifiers findQualifiers, 
                         int maxRows) throws JAXRException {
        return finder.find_service(proxy,businessKey,names, 
                 categoryBag,tModelBag,findQualifiers, maxRows );               
    }
   
    //--------------------------------------------------------------------------
   /*        DELETE  METHODS        */
    //-----------------------------------------------------------------------
    
    /**
     * Deletes Businesses whose keys are in the Collection.
     */    
    public BulkResponseImpl delete_business( Collection orgkeys)
    throws JAXRException {
        return deleter.delete_business(proxy,orgkeys,connection, token);          
    }
    /**
     * Deletes Services whose keys are in the Collection.
     */
    public BulkResponseImpl delete_service( Collection skeys)
    throws JAXRException {
        return deleter.delete_service(proxy,skeys,connection, token);              
    }
   
    //--------------------------------------------------------------------------
   /*        SAVE  METHODS        */
    //-----------------------------------------------------------------------
    /**
     * Save Businesses whose details are in the Collection.
     */ 
    public BulkResponseImpl save_business( Collection orgs)
    throws JAXRException {
        System.out.println( "Method:save_business:Args length="+orgs.size() );
        return saver.save_business( proxy, orgs, connection, token);            
    }
    /**
     * Save Services whose details are in the Collection.
     */
    public BulkResponseImpl save_service( Collection services )
    throws JAXRException {
        return saver.save_service( proxy, services, connection, token);            
    } 
    
}//end class
