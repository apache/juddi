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
import javax.xml.registry.RegistryException;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.Service;

import org.apache.scout.registry.infomodel.InternationalStringImpl;
import org.apache.scout.registry.infomodel.KeyImpl;
import org.apache.scout.registry.infomodel.OrganizationImpl;
import org.apache.scout.registry.infomodel.ServiceImpl;
import org.uddi4j.response.BusinessInfo;
import org.uddi4j.response.DispositionReport;
import org.uddi4j.response.ErrInfo;
import org.uddi4j.response.Result;
import org.uddi4j.response.ServiceInfo;
import org.uddi4j.response.ServiceInfos;

/**
 * Utility Class that deals with conversion to JAXR Objects.
 * @author Anil Saldhana  <anil@apache.org>
 */
public class JAXRHelper {
    /**
     * Creates a JAXROrganization from a UDDI4J BusinessInfo Object
     */    
    Organization createJAXROrganization(BusinessInfo bizInfo)
    throws JAXRException {
        //Create a new JAXR Organization
        Organization org = new OrganizationImpl();
        try{
            String namestr = bizInfo.getDefaultNameString();
            org.setName( new InternationalStringImpl( namestr));
        
            //Get the Businmss Key
            Key key = new KeyImpl();
            key.setId( bizInfo.getBusinessKey() );
            org.setKey( key );
            org.setDescription(
               new InternationalStringImpl(
                       bizInfo.getDefaultDescriptionString())); 
            
            //Get the Business Services Information
            ServiceInfos sinfos = bizInfo.getServiceInfos();
            Vector svect = sinfos.getServiceInfoVector();
            addServiceInfoToJAXROrganization( svect, org);
        }catch(Exception ud){
                throw new JAXRException( "Apache JAXR Impl:", ud);
        }
        
        return org;
    }     
    /**Creates a JAXR Service from a UDDI4J ServiceInfo Object
     * @param info
     * @return
     */
    Service createJAXRService(ServiceInfo serviceinfo) 
    throws JAXRException {
//      Create a new JAXR Organization
        Service serve = new ServiceImpl();
        try{
            String namestr = serviceinfo.getDefaultNameString();
            serve.setName( new InternationalStringImpl( namestr));
        
            //Get the Businmss Key
            Key key = new KeyImpl();
            key.setId( serviceinfo.getServiceKey() );
            serve.setKey( key );
            //UDDI4J Does not provide description for service
            //Check juddi alternative
        }catch(Exception ud){
                throw new JAXRException( "Apache JAXR Impl:", ud);
        }        
        return serve;
    }
    
    void addServiceInfoToJAXROrganization( Vector svect, 
               Organization org)
    throws JAXRException {
          for( int i = 0; svect!= null && i < svect.size(); i++ )
          {              
             ServiceInfo sInfo = (ServiceInfo)svect.elementAt(i); 
             ServiceImpl serve = new ServiceImpl();
             String name = sInfo.getDefaultNameString();
             log( "Service Name to store="+name);
             serve.setName(new InternationalStringImpl(name));
             serve.setKey( new KeyImpl(sInfo.getServiceKey()));
             /**
              * UDDI4J Does Not Have a Description for Service
              */
             serve.setDescription( new InternationalStringImpl(name));
             org.addService( serve );                                   
          }//end for 
    }//end method
    
    Collection prepareBulkResponseExceptions(DispositionReport  dr,
        KeyImpl key)    throws JAXRException {
        Collection exceptions = new ArrayList();
        Vector resultvect = dr.getResultVector();         
                
          //Now convert Result objects into BulkResponse
          for( int i = 0;  resultvect != null &&  i< resultvect.size(); 
                               i++ ){
                    Result res = (Result)resultvect.elementAt( i );
                    String str = "Error Num="+res.getErrno();
                    ErrInfo err = res.getErrInfo();
                    if( err != null){
                      str += ": errCode:"  + err.getErrCode() +
                       ": errInfoText:" + err.getText();
                    }
                    JAXRException jax = new JAXRException(str);
                    //Create a RegistryException object on JAXR end
                    RegistryException re  = new RegistryException(jax);
                    re.setErrorObjectKey( key );            
                    //Add the RegistryException object to the collections of
                    //exceptions
                    exceptions.add(  re );
                }
        return exceptions;        
    }  
    private void log( String str ){
        System.out.println( str );
    }
}
