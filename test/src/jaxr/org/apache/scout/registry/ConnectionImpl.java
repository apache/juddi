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

import javax.xml.registry.RegistryService;
import javax.xml.registry.JAXRException;
import javax.xml.registry.Connection;

import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.HashSet;
import java.net.PasswordAuthentication;

import org.apache.scout.UDDI.JAXRUDDIManager;

/**
 * Apache Scout Implementation of a JAXR Connection.
 * For futher details, look into the JAXR API Javadoc.
 * @author Anil Saldhana  <anil@apache.org>
 */
public class ConnectionImpl implements Connection {
    
    private RegistryServiceImpl regservice = null;      
    private String username = "";
    private String passwd = "";
    
    private Set creds = new HashSet();
     
    private Properties conprops = null;
    private JAXRUDDIManager jaxruddi = null;
    
    /** Creates a new instance of Connection */
    public ConnectionImpl() {
    }
    
    public void close() throws JAXRException {
    }
    
    public Set getCredentials() throws JAXRException {
         return creds;
    }
    
    public RegistryService getRegistryService() throws  JAXRException {
        if( regservice == null ) regservice = new RegistryServiceImpl();
        regservice.setConnection( this );
         return regservice;
    }
    
    public boolean isClosed() throws  JAXRException {
        return false;
    }
    
    public boolean isSynchronous() throws JAXRException {
        return true;
    }
    
    public void setCredentials( Set set) throws  JAXRException {
        creds = set;
        boolean isEmpty = false;
        if( set != null ) isEmpty = set.isEmpty();
        if( isEmpty ) return;
        Iterator iter = set.iterator();
        PasswordAuthentication pw = null;
        if( iter.hasNext()){
           pw = (PasswordAuthentication)iter.next();
           username =  pw.getUserName();
           passwd = new String(pw.getPassword());
        } 
        
        try{
            String queryurl = null,publishurl=null;
            
            if(conprops != null ){
                queryurl = conprops.getProperty( "javax.xml.registry.queryManagerURL");
                publishurl = conprops.getProperty("javax.xml.registry.lifeCycleManagerURL");
            }
            //Create an instance of JAXRUDDIManager which internally authenticates
            //the user credentials
            jaxruddi = new JAXRUDDIManager(queryurl,publishurl,username,passwd);
        }catch(Exception ud){
            throw new JAXRException( "Apache JAXR Impl::",ud);
        }
    }
    
    public void setSynchronous(boolean param) throws JAXRException {
    }
    
    void setProperties( Properties prop){
        this.conprops = prop;
    }
    
    Properties getProperties() { return this.conprops; }
    
    public String getUserName() { return this.username; }
    public String getPassword() { return this.passwd; }
}
