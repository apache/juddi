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
 
/**
 * Implements EmailAddress
 ** Implements JAXR Interface.
 * For futher details, look into the JAXR API Javadoc.
 * @author Anil Saldhana  <anil@apache.org>
 */
public class EmailAddressImpl implements EmailAddress {
    private String email = "";
    private String type = "EMAIL";
    
    /** Creates a new instance of EmailAddressImpl */
    public EmailAddressImpl() {
    }
    
    public String getAddress() throws  JAXRException {
        return email;
    }
    
    public String getType() throws  JAXRException {
        return type;
    }
    
    public void setAddress(String str) throws  JAXRException {
        this.email = str;
    }
    
    
    public void setType(String str) throws  JAXRException {
        this.type = str;
    }
    
}
