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

/**
 * Implements JAXR Interface.
 * For futher details, look into the JAXR API Javadoc.
 * @author Anil Saldhana  <anil@apache.org>
 */
public class TelephoneNumberImpl 
implements javax.xml.registry.infomodel.TelephoneNumber{
    
    private String areacode = new String();
    private String countrycode = new String();
    private String extension = new String();
    private String url =new String();
    private String number = new String();
    private String type = new String();    
    
    /** Creates a new instance of TelephoneNumberImpl */
    public TelephoneNumberImpl() {
    }
    
    public String getAreaCode() throws JAXRException {
        return this.areacode;
    }
    
    public String getCountryCode() throws JAXRException {
        return this.countrycode;
    }
    
    public String getExtension() throws JAXRException {
        return this.extension;
    }
    
    public String getNumber() throws JAXRException {
        return this.number;
    }
    
    public String getType() throws JAXRException {
        return this.type;
    }
    
    public String getUrl() throws JAXRException {
        return this.url;
    }
    
    public void setAreaCode(String str) throws JAXRException {
        this.areacode = str;
    }
    
    public void setCountryCode(String str) throws JAXRException {
        this.countrycode = str;
    }
    
    public void setExtension(String str) throws JAXRException {
        this.extension = str;
    }
    
    public void setNumber(String str) throws JAXRException {
        this.number =  str;
    }
    
    public void setType(String str) throws JAXRException {
        this.type = str;
    }
    
    public void setUrl(String str) throws JAXRException {
        this.url = str;
    }
}
