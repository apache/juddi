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

package org.apache.scout.registry;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.registry.BulkResponse;
import javax.xml.registry.JAXRException;
 
/**
 * Implements JAXR BulResponse Interface.
 * For futher details, look into the JAXR API Javadoc.
 * @author Anil Saldhana  <anil@apache.org>
 */
public class BulkResponseImpl implements BulkResponse {
    public static int 	STATUS_FAILURE=1;           
    public static int 	STATUS_SUCCESS = 0;           
    public static int 	STATUS_UNAVAILABLE=2;           
    public static int 	STATUS_WARNING=3;
    
    private int status = STATUS_SUCCESS;
    private boolean partialResponse  = false;
           
    private Collection exceptions = new ArrayList();
    private Collection collection = new ArrayList();
    
    /** Creates a new instance of BulkResponseImpl */
    public BulkResponseImpl() {
    }
    
    /** Get Collection of RegistryObjects **/
    public Collection getCollection() throws JAXRException {
        return collection;
    }
    
    public Collection getExceptions() throws JAXRException {
        return null;
    }
    
    public String getRequestId() throws JAXRException {
        return null;
    }
    
    public int getStatus() throws JAXRException {
        return status;
    }
    
    public boolean isAvailable() throws JAXRException {
        return false;
    }
    
    public boolean isPartialResponse() throws JAXRException {
        if( exceptions.size() > 0 ) this.partialResponse = true;        
        return this.partialResponse;
    }
    
    public void  setPartialResponse(boolean b ) throws JAXRException {
        this.partialResponse = b;
    }
    
    public void setCollection( Collection coll) throws JAXRException {
        this.collection = coll;
    }
    
    public void setStatus( int status) throws JAXRException {
        this.status =  status;
    }
    
    /**
     * Setter for property exceptions.
     * @param exceptions New value of property exceptions.
     */
    public void setExceptions( Collection exceptions) {
        this.exceptions = exceptions;
    }
    
}
