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

/**
 * Implements JAXR Interface.
 * For futher details, look into the JAXR API Javadoc.
 * @author Anil Saldhana  <anil@apache.org>
 */
public class KeyImpl 
implements javax.xml.registry.infomodel.Key{
    private String id = "";
    
    /** Creates a new instance of KeyImpl */
    public KeyImpl() {
    }
    
    public KeyImpl( String str){
        id = str;
    }
    
    public String getId() 
    throws javax.xml.registry.JAXRException {
        return id;
    }
    
    public void setId(String str) 
    throws javax.xml.registry.JAXRException {
        this.id = str;
    }
    
}
