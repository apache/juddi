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
import javax.xml.registry.infomodel.RegistryObject;
 

/**
 * Implements JAXR Interface.
 * For futher details, look into the JAXR API Javadoc.
 * @author Anil Saldhana  <anil@apache.org>
 */
public class ExternalIdentifierImpl extends RegistryObjectImpl
implements javax.xml.registry.infomodel.ExternalIdentifier {
    
    private ClassificationScheme identity = new ClassificationSchemeImpl();
    private String value = new String();    
    private RegistryObject parent;
    
    /** Creates a new instance of ExternalIdentifierImpl */
    public ExternalIdentifierImpl() {
    }    
     
    public ClassificationScheme getIdentificationScheme() 
    throws JAXRException {
        return identity;
    }    
     
    public RegistryObject getRegistryObject() throws JAXRException {
        return parent;
    }
    
    public String getValue() throws JAXRException {
        return value;
    }
     
    public void setIdentificationScheme(ClassificationScheme cs) 
    throws JAXRException {
        identity = cs;
    } 
    
    public void setValue(String str) throws JAXRException {
        value = str;
    } 
    
}
