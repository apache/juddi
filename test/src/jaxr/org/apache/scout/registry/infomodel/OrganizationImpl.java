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

import java.util.*;

/**
 * Organization Interface 
 ** Implements JAXR Interface.
 * For futher details, look into the JAXR API Javadoc.  
 * @author Anil Saldhana  <anil@apache.org>
 */
public class OrganizationImpl extends RegistryObjectImpl
implements Organization {
    private OrganizationImpl parentorg = null;
    private Collection childorgs = new ArrayList();
    private Collection services = new ArrayList();
    private Collection users = new ArrayList();
    private Collection telephone = new ArrayList();
    
    private PostalAddress postaddr = null; 
    private User user = new UserImpl();
    
    /** Creates a new instance of OrganizationImpl */
    public OrganizationImpl() {
    }
    
    public void addChildOrganization(  Organization organization) 
    throws JAXRException {         
       this.childorgs.add(organization);
    }
    
    public void addChildOrganizations( Collection collection) 
    throws JAXRException {         
            this.childorgs.addAll(collection);        
    } 
    
   
    public void addService( Service service) 
    throws JAXRException {
        this.services.add(service );
    }
    
    public void addServices(Collection collection) 
    throws JAXRException {
          this.services.addAll(collection);   
    }
    
    
    public void addUser( User user) throws JAXRException {
        this.users.add(user );
    }
    
    public void addUsers(Collection collection) 
    throws JAXRException {
        this.users.addAll(collection);
    }
    
    public int getChildOrganizationCount() throws JAXRException {
        return this.childorgs.size();
    }
    
    public Collection getChildOrganizations() throws JAXRException {
        return childorgs;
    }    
    
    
    public Collection getDescendantOrganizations() throws JAXRException {
        return null;
    } 
    
    
    public PostalAddress getPostalAddress() throws JAXRException {
        return postaddr;
    }
    
    public  User getPrimaryContact() throws JAXRException {
        return user;
    }
    
    
    
    public  Organization getRootOrganization() throws JAXRException {
        return null;
    }
    
    public Collection getServices() throws JAXRException {
        return services;
    }
    
   
    
    public Collection getTelephoneNumbers(String str) throws JAXRException {
        return telephone;
    }
    
    public Collection getUsers() throws JAXRException {
        return users;
    }
    
   
    public void removeChildOrganization( Organization organization) 
    throws JAXRException {
        this.childorgs.remove( organization);
    }
    
    public void removeChildOrganizations(Collection collection) 
    throws JAXRException {
        this.childorgs.removeAll( collection);
    } 
    
    public void removeService( Service service) throws JAXRException {
        services.remove( service );
    }
    
    public void removeServices(Collection collection) throws JAXRException {
        services.removeAll( collection );
    }
    
    public void removeUser( User user) 
    throws JAXRException {
        users.remove( user );
    }
    
    public void removeUsers(Collection collection) 
    throws JAXRException {
        users.removeAll( collection );
    }  
    
    
    public void setPostalAddress( PostalAddress postalAddress) 
    throws JAXRException {
        this.postaddr = postalAddress;
    }
    
    public void setPrimaryContact( User user) 
    throws JAXRException {
        this.user = user;
        users.add( user );
    }
    
    public void setTelephoneNumbers(Collection collection) 
    throws JAXRException {
        this.telephone = collection;
    }    
    
    public Organization getParentOrganization() 
    throws JAXRException {
        return parentorg;
    }
    
}
