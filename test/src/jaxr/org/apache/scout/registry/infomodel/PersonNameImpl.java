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
import  javax.xml.registry.JAXRException;
 
/**
 * Implements JAXR Interface.
 * For futher details, look into the JAXR API Javadoc.
 * @author Anil Saldhana  <anil@apache.org>
 */
public class PersonNameImpl 
implements javax.xml.registry.infomodel.PersonName {
    
    private String firstname = "";
    private String fullname = "";
    private String lastname = "";
    private String middlename = "";
    
    /** Creates a new instance of PersonNameImpl */
    public PersonNameImpl() {
    }
    
    public PersonNameImpl( String fn,String mn, String ln){
        this.firstname = fn;
        this.middlename = mn;
        this.lastname = ln;
    }
    
    public PersonNameImpl( String fullname){
        this.fullname = fullname;
    }
    
    public String getFirstName() throws JAXRException {
        return this.firstname;
    }
    
    public String getFullName() throws JAXRException {
        return this.fullname;
    }
    
    public String getLastName() throws JAXRException {
        return this.lastname;
    }
    
    public String getMiddleName() throws JAXRException {
        return this.middlename;
    }
    
    public void setFirstName(String str) throws JAXRException {
       this.firstname = str;
    }
    
    public void setFullName(String str) throws JAXRException {
       this.fullname = str;
    }
    
    public void setLastName(String str) throws JAXRException {
        this.lastname = str;
    }
    
    public void setMiddleName(String str) throws JAXRException {
        this.middlename = str;
    }
    
}
