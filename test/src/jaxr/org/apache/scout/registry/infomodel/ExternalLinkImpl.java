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

import javax.xml.registry.*;
 
/**
 * Implements JAXR Interface.
 * For futher details, look into the JAXR API Javadoc.
 * @author Anil Saldhana  <anil@apache.org>
 */
public class ExternalLinkImpl extends RegistryObjectImpl
implements javax.xml.registry.infomodel.ExternalLink{
    private String uri = new String();
    private boolean validateuri = false;
    private Collection links = new ArrayList();
    
    /** Creates a new instance of ExternalLinkImpl */
    public ExternalLinkImpl() {
    }
    
    public String getExternalURI() throws JAXRException {
        return uri;
    }
    
    public  Collection getLinkedObjects() throws JAXRException {
        return links;
    }
    
    public boolean getValidateURI() throws JAXRException {
        return validateuri;
    }
    
    public void setExternalURI(String str) throws JAXRException {
        this.uri = str;
    }
    
    public void setValidateURI(boolean param) throws JAXRException {
        this.validateuri = param;
    }
    
}
