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
 
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.registry.JAXRException;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.ServiceBinding;

/**
 * Implements JAXR Interface.
 * For futher details, look into the JAXR API Javadoc.
 * @author Anil Saldhana  <anil@apache.org>
 */
public class ServiceImpl extends RegistryEntryImpl
implements   javax.xml.registry.infomodel.Service{
    
    private Organization org = null;
    private Collection serviceBindings = new ArrayList();
    
    /** Creates a new instance of ServiceImpl */
    public ServiceImpl() {
    }
    
    public void addServiceBinding(  ServiceBinding serviceBinding) 
    throws JAXRException {
        serviceBindings.add( serviceBinding );
    }
    
    public void addServiceBindings( Collection collection) 
    throws JAXRException {
        serviceBindings.addAll( collection);
    }
    
    public   Organization getProvidingOrganization() 
    throws JAXRException {
        if (org == null ) return super.getSubmittingOrganization();
        return org;
    }    
   
    public  Collection getServiceBindings() throws JAXRException {
        return serviceBindings;
    }
    
    public void removeServiceBinding(  ServiceBinding serviceBinding) 
    throws JAXRException {
        serviceBindings.remove( serviceBinding );
    }
    
    public void removeServiceBindings( Collection collection) 
    throws JAXRException {
        serviceBindings.removeAll( collection );
    }    
    
    public void setProvidingOrganization(  Organization organization) 
    throws JAXRException {
        this.org = organization;
    }     
}
