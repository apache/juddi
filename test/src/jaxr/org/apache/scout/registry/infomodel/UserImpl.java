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

package org.apache.scout.registry.infomodel;

import  javax.xml.registry.*;
import  javax.xml.registry.infomodel.*;
 
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Implements JAXR Interface.
 * For futher details, look into the JAXR API Javadoc.
 * @author Anil Saldhana  <anil@apache.org>
 */
public class UserImpl extends RegistryObjectImpl 
implements User{
    private PersonName personName = new PersonNameImpl();
    
    private Collection postalAddresses = new ArrayList();
    private Collection emailAddresses= new ArrayList();
    private Collection telnumbers= new ArrayList();
    private URL url;
    private String type = "";
    
    /** Creates a new instance of UserImpl */
    public UserImpl() {
    }   
    
    public  Organization getOrganization() throws JAXRException {
        return null;
    }
    
    public  PersonName getPersonName() throws JAXRException {
        return personName;
    }
    
    public Collection getPostalAddresses() throws JAXRException {
        return postalAddresses;
    }     
    
    public Collection getTelephoneNumbers(String str) 
    throws JAXRException {
        return telnumbers;
    }
    
    public String getType() throws JAXRException {
        return type;
    }
    
    public URL getUrl() throws JAXRException {
        return url;
    }
       
    public void setEmailAddresses(Collection collection) 
    throws JAXRException {
        emailAddresses = collection;
    }   
    
    public void setPersonName( PersonName pname) throws JAXRException {
        personName = pname;
    }
    
    public void setPostalAddresses(Collection collection) 
    throws JAXRException {
        postalAddresses  =  collection;
    }
    
    public void setTelephoneNumbers(Collection collection) 
    throws JAXRException {
        telnumbers = collection;
    }
    
    public void setType(String str) throws JAXRException {
        type = str;
    }
    
    public void setUrl( URL uRL) throws JAXRException {
        this.url = uRL;
    }    
    
    public Collection getEmailAddresses() throws JAXRException {
        return emailAddresses;
    }
    
}
