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

import javax.xml.registry.*;
import javax.xml.registry.infomodel.*;

import java.util.*;


/**
 * Implements JAXR Interface.
 * For futher details, look into the JAXR API Javadoc.
 * @author Anil Saldhana  <anil@apache.org>
 */
public class ClassificationSchemeImpl 
extends RegistryEntryImpl
implements ClassificationScheme{         
       
        private Collection childConcepts = new ArrayList();   
     
	    private int valueType = 1;
	    
	    private boolean external = false; 
	    
	    private Collection descendants = new ArrayList();
    
    /** Creates a new instance of ClassificationSchemeImpl */
    public ClassificationSchemeImpl() {
    }    
     
    public void addChildConcept(Concept concept) 
    throws JAXRException {
        childConcepts.add( concept );
    }
    
    public void addChildConcepts(Collection collection) 
    throws JAXRException {
        childConcepts.addAll( collection);
    } 
    
    public int getChildConceptCount() 
    throws JAXRException {
        return childConcepts.size();
    }
    
    public Collection getChildrenConcepts() throws JAXRException {
        return childConcepts;
    }
    
    public Collection getDescendantConcepts() throws JAXRException {
        return this.descendants;
    }  
    
    public int getValueType() throws JAXRException {
        return this.valueType;
    }
    
    public boolean isExternal() throws JAXRException {
        return this.external;
    }    
    
    public void removeChildConcept(Concept concept) 
    throws JAXRException {
        this.childConcepts.remove( concept);
    }
    
    public void removeChildConcepts(Collection collection) 
    throws JAXRException {
        this.childConcepts.removeAll( collection);
    }     
    
    public void setValueType(int param) throws JAXRException {
        this.valueType =  param;
    }    
     
}
