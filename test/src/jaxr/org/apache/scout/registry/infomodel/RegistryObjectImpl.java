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
 * Implements  RegistryObject Interface
 * Implements JAXR Interface.
 * For futher details, look into the JAXR API Javadoc.
 * @author Anil Saldhana  <anil@apache.org>
 */
public class RegistryObjectImpl extends ExtensibleObjectImpl
implements RegistryObject {
    private OrganizationImpl parentorg = null;
    protected InternationalString name = new InternationalStringImpl();
    protected InternationalString desc = new InternationalStringImpl();
    private Key key = new KeyImpl();
    private LifeCycleManager lcm = null;
    
    private Collection externalIds = new ArrayList();
    private Collection externalLinks = new ArrayList();
    private Collection classifications = new ArrayList();
    private Collection associations = new ArrayList();     
        
    /** Creates a new instance of RegistryObjectImpl */
    public RegistryObjectImpl() {
    }
    
    public void addAssociation(Association association) throws JAXRException {
        associations.add( association );
    }
    
    public void addAssociations(Collection collection) throws JAXRException {
        associations.addAll( collection );
    }
    
    public void addClassification(Classification classification) 
    throws JAXRException {
        classifications.add( classification );
    }
    
    public void addClassifications(Collection collection)
    throws JAXRException {
        classifications.addAll( collection );
    }
    
    public void addExternalIdentifier(ExternalIdentifier externalIdentifier) 
    throws JAXRException {
        externalIds.add( externalIdentifier );
    }
    
    public void addExternalIdentifiers(Collection collection) 
    throws JAXRException {
        externalIds.addAll( collection );
    }
    
    public void addExternalLink(ExternalLink externalLink) 
    throws JAXRException {
        externalLinks.add( externalLink );
    }
    
    public void addExternalLinks(Collection collection) 
    throws JAXRException {
        externalLinks.addAll( collection );
    }     
    
    public Collection getAssociatedObjects() throws JAXRException {
        return null;
    }
    
    public Collection getAssociations() throws JAXRException {
        return associations;
    }
    
    public Collection getAuditTrail() throws JAXRException {
        return null;
    }
    
    public Collection getClassifications() throws JAXRException {
        return classifications;
    }
    
    public InternationalString getDescription() throws JAXRException {
        return desc;
    }
    
    public Collection getExternalIdentifiers() throws JAXRException {
        return externalIds;
    }
    
    public Collection getExternalLinks() throws JAXRException {
        return externalLinks;
    }    
     
    public  Key getKey() throws JAXRException {
        return key;
    }
    
    public LifeCycleManager getLifeCycleManager() 
    throws JAXRException {
        return lcm;
         
    }
    
    public  InternationalString getName() throws JAXRException {
         return name;
    }
    
    public  Concept getObjectType() throws JAXRException {
        return null;
    }
    
    public  Collection getRegistryPackages() throws JAXRException {
        return null;
    }    
     
    
    public  Organization getSubmittingOrganization() 
    throws JAXRException {
        return parentorg;
    }
    
    public void removeAssociation( Association association) 
    throws JAXRException {
         associations.remove( association);
    }
    
    
    public void removeAssociations( Collection collection) 
    throws JAXRException {
        associations.removeAll( collection);
    }
    
    public void removeClassification( Classification classification) 
    throws JAXRException {
        classifications.remove( classification );
    }
    
    public void removeClassifications( Collection collection) 
    throws JAXRException {
        classifications.removeAll( collection );
    }
    
    public void removeExternalIdentifier( ExternalIdentifier externalIdentifier) 
    throws JAXRException {
        externalIds.remove( externalIdentifier );
    }
    
    public void removeExternalIdentifiers( Collection collection) 
    throws JAXRException {
        externalIds.removeAll( collection );
    }
    
    public void removeExternalLink( ExternalLink externalLink) 
    throws JAXRException {
        externalLinks.remove( externalLink );
    }
    
    public void removeExternalLinks( Collection collection) 
    throws JAXRException {
        externalLinks.removeAll( collection );
    }    
    
    public void setAssociations( Collection collection) 
    throws JAXRException {
        associations.addAll( collection );
    }
    
    public void setClassifications( Collection collection) 
    throws JAXRException {
        classifications.addAll( collection );
    }
    
    public void setDescription( InternationalString istr) 
    throws JAXRException {
        desc = istr;
    }
    
    public void setExternalIdentifiers( Collection collection) 
    throws JAXRException {
        externalIds.addAll( collection );
    }
    
    public void setExternalLinks( Collection collection) 
    throws JAXRException {
        externalLinks.addAll( collection );
    }
    
    public void setKey( Key k ) throws JAXRException {
        key = k;
    }
    
    public void setName( InternationalString istr) 
    throws JAXRException {
        name = istr;
    }
    
    public String toXML() throws JAXRException {
        throw new UnsupportedCapabilityException();
    }
    
    public void setSubmittingOrganization( Organization org ) {
        parentorg = (OrganizationImpl)org;
    }
     public void setLifeCycleManager(LifeCycleManager l){
         this.lcm = l;
     }
    
}
