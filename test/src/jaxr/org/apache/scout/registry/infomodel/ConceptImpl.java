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
import javax.xml.registry.infomodel.*;

/**
 * Implements JAXR Interface.
 * For futher details, look into the JAXR API Javadoc.
 * @author Anil Saldhana  <anil@apache.org>
 */
public class ConceptImpl 
extends RegistryObjectImpl
implements javax.xml.registry.infomodel.Concept {
    private String value = new  String(); 
    
    private RegistryObject parent = new RegistryObjectImpl();
    private Concept parentconcept = null;
     
    private ClassificationSchemeImpl scheme = new ClassificationSchemeImpl();
    private Collection childconcepts = new ArrayList();

    private String path;
    
    /** Creates a new instance of ConceptImpl */
    public ConceptImpl() {
    } 
    public void addChildConcept(Concept concept) 
    throws JAXRException {
        this.childconcepts.add( concept);
    }
    
    public void addChildConcepts(Collection collection) 
    throws JAXRException {
        this.childconcepts.addAll( collection );
    }
    
    public int getChildConceptCount() 
    throws JAXRException {
        return this.childconcepts.size();
    }
    
    public Collection getChildrenConcepts() 
    throws JAXRException {
        return this.childconcepts;
    }
    
    public ClassificationScheme getClassificationScheme() 
    throws JAXRException {
        return scheme;
    }
    
     
    public Collection getDescendantConcepts() 
    throws JAXRException {
        return null;
    }
    
    public RegistryObject getParent() 
    throws JAXRException {
        return parent;
    }
    
    public Concept getParentConcept() 
    throws JAXRException {
        return parentconcept;
    }
    
    public String getPath() throws JAXRException {
        return path;
    }
    
    public String getValue() throws JAXRException {
        return value;
    }
    
    public void removeChildConcept(Concept concept) 
    throws JAXRException {
    }
    
    public void removeChildConcepts(Collection collection) 
    throws JAXRException {
        this.childconcepts.removeAll( collection );
    }    
     
    
    public void setValue(String str) 
    throws JAXRException {
        value = str;
    }
    
     
    public void setParent( RegistryObject reg){
        parent = reg;
    }
    
    public void setParentConcept( Concept c){
        parentconcept = c;
    }
}
