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

import javax.xml.registry.JAXRException;
import javax.xml.registry.infomodel.ClassificationScheme;

import java.util.Collection;

/**
 * Implements PostalAddress Interface
 * Implements JAXR Interface.
 * For futher details, look into the JAXR API Javadoc.
 * @author Anil Saldhana  <anil@apache.org>
 */
public class PostalAddressImpl extends ExtensibleObjectImpl
implements javax.xml.registry.infomodel.PostalAddress {
    
     private String city = new String();
     private String country = new String(); 
     private String postalCode = new String(); 
     private ClassificationScheme postalScheme = new ClassificationSchemeImpl(); 
     private String stateOrProvince = new String(); 
     private String street = new String(); 
     private String streetNumber = new String();
     private String type = new String(); 
    
    
    /** Creates a new instance of PostalAddressImpl */
    public PostalAddressImpl() {
    }     
    
    public String getCity() 
    throws JAXRException {
        return city;
    }
    
    public String getCountry() 
    throws JAXRException {
        return country;
    }
    
    public String getPostalCode() 
    throws JAXRException {
        return postalCode;
    }
    
    public ClassificationScheme getPostalScheme() 
    throws JAXRException {
        return postalScheme;
    } 
    
    public Collection getSlots() 
    throws JAXRException {
        return null;
    }
    
    public String getStateOrProvince() 
    throws JAXRException {
        return stateOrProvince;
    }
    
    public String getStreet() 
    throws JAXRException {
        return street;
    }
    
    public String getStreetNumber() 
    throws JAXRException {
        return streetNumber;
    }
    
    public String getType() 
    throws JAXRException {
           return type;  
    }     
    
    public void setCity(String str) 
    throws JAXRException {
        city = str;
    }
    
    public void setCountry(String str) 
    throws JAXRException {
        country = str;
    }
    
    public void setPostalCode(String str) 
    throws JAXRException {
        postalCode = str;
    }
    
    public void setPostalScheme( ClassificationScheme cscheme) 
    throws JAXRException {
        postalScheme = cscheme;
    }
    
    public void setStateOrProvince(String str) 
    throws JAXRException {
        stateOrProvince = str;
    }
    
    public void setStreet(String str) 
    throws JAXRException {
        street  =str;
    }
    
    public void setStreetNumber(String str) 
    throws JAXRException {
        streetNumber =str;
    }
    
    public void setType(String str) 
    throws JAXRException {
        type = str;
    }
    
}
