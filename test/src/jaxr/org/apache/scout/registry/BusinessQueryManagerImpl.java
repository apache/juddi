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

import  javax.xml.registry.*;

import  javax.xml.registry.infomodel.*;

import java.util.*;

import org.uddi4j.util.FindQualifiers;
import org.uddi4j.util.FindQualifier;
import org.uddi4j.datatype.Name;

import org.apache.scout.UDDI.JAXRUDDIManager;

/**
 * Implements the JAXR BusinessQueryManager Interface
 * For futher details, look into the JAXR API Javadoc.
 * @author Anil Saldhana  <anil@apache.org>
 */
public class BusinessQueryManagerImpl extends QueryManagerImpl
implements BusinessQueryManager {
    
     private ConnectionImpl conn = null;
    
    /* Change the MAX_ROWS valuse dynamically later */
    private int MAX_ROWS = 15;
    
    /** Creates a new instance of BusinessQueryManagerImpl */
    public BusinessQueryManagerImpl() {
    }
    
    public  BulkResponse findAssociations( Collection collection, 
         String str, String str2,  Collection collection3) 
         throws  JAXRException {
              return null;
    }
    
    public  BulkResponse findCallerAssociations( Collection collection, 
       Boolean booleanParam, Boolean booleanParam2,  Collection collection3) 
       throws  JAXRException {
            return null;
    }
    
    public  ClassificationScheme findClassificationSchemeByName( 
          Collection collection, String str) 
          throws  JAXRException {
               return null;
    }
    
    public  BulkResponse findClassificationSchemes( 
             Collection collection,  Collection collection1, 
             Collection collection2,  Collection collection3) 
             throws  JAXRException {
                  return null;
    }
    
    public  Concept findConceptByPath(String str) throws  JAXRException {
         return null;
    }
    
    public  BulkResponse findConcepts( Collection collection,  
    Collection collection1,  Collection collection2,  
    Collection collection3,  Collection collection4) throws  JAXRException {
         return null;
    }
    
    /**
     *   findQualifiers - a Collection of find qualifiers 
     *    namePatterns - a Collection that may consist 
                   of either String or LocalizedString objects.  
     *    classifications - a Collection of Classification objects 
                     that classify the object. 
                     (Analogous to a catgegoryBag in the UDDI). 
     *    specifications - a Collection of RegistryObjects that represent 
     *     externalIdentifiers - a Collection of ExternalIdentifier objects 
              (Analogous to an identifierBag in the UDDI)
     *    externalLinks - a Collection of ExternalLink objects  
           (Analogous to an overviewDoc in the UDDI)  
     */
    public  BulkResponse findOrganizations( Collection findqualifiers,  
    Collection namepatterns,  Collection classifications,  
    Collection specs,  Collection extids,  
    Collection extlinks) throws  JAXRException {
        FindQualifierImpl fqimpl = new FindQualifierImpl();
        BulkResponse br = new BulkResponseImpl();
        try{
            //First we need to convert JAXR FindQualifier to UDDI4J FQ
            FindQualifiers findQualifiers = new FindQualifiers();
            Vector qualifier = new Vector();
            
            Iterator iter = findqualifiers.iterator();
            while( iter.hasNext()){
                String fq = (String)iter.next();
                String str = fqimpl.getUDDI4JFindQualifier( fq );
                qualifier.add(new FindQualifier( str ));
            }  
            findQualifiers.setFindQualifierVector(qualifier);
            
            //Name Patterns
            Vector names = new Vector();
            Iterator iternames = namepatterns.iterator();
            while( iternames.hasNext()){  
                 String name = (String)iternames.next();
                 names.add(new Name(name));
            } 
            JAXRUDDIManager jaxr = new JAXRUDDIManager(conn.getProperties());
            br = jaxr.find_business( names, 
                             null,null, null, null, 
                             findQualifiers, MAX_ROWS);
            
        }catch(Exception e){
            e.printStackTrace();
            throw new JAXRException("Apache JAXR Impl::",e);
        }     
        
        return br;
         
    }
    
    public  BulkResponse findRegistryPackages( Collection collection,  
    Collection collection1,  Collection collection2,  
    Collection collection3) throws  JAXRException {
         return null;
    }
     
    
    public  BulkResponse findServiceBindings(  Key key,  
    Collection collection,  Collection collection2,  
    Collection collection3) throws  JAXRException {
         return null;
    }
    
    /*     orgKey - Key identifying an Organization. Required for UDDI providers.
     *     findQualifiers - a Collection of find qualifiers as 
     *                     defined by the FindQualifier interface, 
     *                     which specifies qualifiers that affect 
     *                     string matching, sorting, boolean predicate logic, 
     *                     and the like.
     *     namePatterns - a Collection that may consist of either String or 
     *                    LocalizedString objects. Each String or value 
     *                    within a LocalizedString is a partial or full name 
     *                    pattern with wildcard searching as specified by the 
     *                    SQL-92 LIKE specification. Unless otherwise specified 
     *                    in findQualifiers, this is a logical OR, and a match 
     *                    on any name qualifies as a match for this criterion.
     *      classifications - a Collection of Classification objects that 
     *                    classify the object. It is analogous to a catgegoryBag 
     *                    in the UDDI specification. Unless otherwise specified in 
     *                    findQualifiers, this is a logical AND, and a match on 
     *                    all specified Classifications qualifies as a match for 
     *                    this criterion. The programmer may use the 
     *                    LifeCycleManager.createClassification method to 
     *                    create a transient Classification for use in this Collection.
     *     specifications - a Collection of RegistryObjects that represent 
     *                   (proxy) a technical specification. It is analogous to a 
     *                   tModelBag in the UDDI specification. In the case of a UDDI 
     *                   provider, the RegistryObject is a specification Concept. 
     *                   In the case of an ebXML provider, the RegistryObject is 
     *                   likely to be an ExtrinsicObject. Unless otherwise specified 
     *                   in findQualifiers, this is a logical AND, and a match on all 
     *                   specified Specifications qualifies as a match for this criterion.
     * @see javax.xml.registry.BusinessQueryManager#findServices(javax.xml.registry.infomodel.Key, 
     *               java.util.Collection, java.util.Collection, java.util.Collection, 
     *               java.util.Collection)
     */
    public  BulkResponse findServices(  Key key,  
             Collection findqualifiers,  
             Collection namepatterns,  
             Collection classifications,  
             Collection specifications) 
    throws  JAXRException {
        FindQualifierImpl fqimpl = new FindQualifierImpl();
        BulkResponse br = new BulkResponseImpl();
        try{
            //First we need to convert JAXR FindQualifier to UDDI4J FQ
            FindQualifiers findQualifiers = new FindQualifiers();
            Vector qualifier = new Vector();
            
            Iterator iter = findqualifiers.iterator();
            while( iter.hasNext()){
                String fq = (String)iter.next();
                String str = fqimpl.getUDDI4JFindQualifier( fq );
                qualifier.add(new FindQualifier( str ));
            }  
            findQualifiers.setFindQualifierVector(qualifier);
            
            //Name Patterns
            Vector names = new Vector();
            Iterator iternames = namepatterns.iterator();
            while( iternames.hasNext()){  
                 String name = (String)iternames.next();
                 names.add(new Name(name));
            } 
            JAXRUDDIManager jaxr = new JAXRUDDIManager(conn.getProperties());
            br = jaxr.find_service( key.getId(),names, 
                             null,null,findQualifiers, MAX_ROWS);
            
        }catch(Exception e){
            e.printStackTrace();
            throw new JAXRException("Apache JAXR Impl::",e);
        }
        return br;
    }   
    
     /** Package Protected **/
      Connection getConnection() { return conn; }
    
      void setConnection( ConnectionImpl con ){
        conn = con;
    }
      /**
       * @param regservice The regservice to set.
       */
      void setRegistryService(RegistryService reg ) {
          super.regservice = reg ;
      }
}
