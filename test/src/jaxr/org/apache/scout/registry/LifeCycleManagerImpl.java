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

import javax.xml.registry.infomodel.*;
import javax.xml.registry.*;

import java.util.*;
import javax.activation.DataHandler ;

import org.apache.scout.registry.infomodel.*;

/**
 * Implements JAXR LifeCycleManager Interface
 * For futher details, look into the JAXR API Javadoc.
 * @author Anil Saldhana  <anil@apache.org>
 */
public class LifeCycleManagerImpl 
implements javax.xml.registry.LifeCycleManager{
    //protected RegistryService regservice = null;

    /** Creates a new instance of LifeCycleManagerImpl */
    public LifeCycleManagerImpl() {
    }
    
    public Association createAssociation(RegistryObject registryObject, 
        Concept concept) 
    throws JAXRException {
        return null;
    }
    
    public   Classification createClassification(  Concept concept) 
    throws  JAXRException,  InvalidRequestException {
        ClassificationImpl  cl   = new  ClassificationImpl(); 
        cl.setConcept( concept); 
        return cl;
    }
    
    public  Classification createClassification(  
                           ClassificationScheme cscheme, 
                           String name, String value) throws  JAXRException {
        InternationalStringImpl iname = new InternationalStringImpl(name);
        ClassificationImpl  cl   = new  ClassificationImpl(iname,value); 
        cl.setClassificationScheme( cscheme );
        return cl;         
    }
    
    public   Classification createClassification(  
                           ClassificationScheme scheme,
                           InternationalString name,
                           String value) throws  JAXRException {
        ClassificationImpl  cl   = new  ClassificationImpl(name,value); 
        cl.setClassificationScheme( scheme );
        return cl;
    }
    
    public ClassificationScheme createClassificationScheme(
      String name, String desc) throws  JAXRException,  InvalidRequestException {
        ClassificationSchemeImpl cls  = new  ClassificationSchemeImpl(); 
        cls.setName( new InternationalStringImpl(name));
        cls.setDescription( new InternationalStringImpl(desc));
        return cls;
    }  
    
     
    public  ClassificationScheme createClassificationScheme(Concept concept) 
    throws  JAXRException,  InvalidRequestException {
       return concept.getClassificationScheme();
    }
    
    public ClassificationScheme createClassificationScheme(  
     InternationalString name,  
     InternationalString desc) 
     throws  JAXRException,  InvalidRequestException {
        ClassificationSchemeImpl cls  = new  ClassificationSchemeImpl(); 
        cls.setName(  name );
        cls.setDescription(  desc );
        return cls;
    }
    
    public Concept createConcept(  RegistryObject parent,  
                   InternationalString name, String value) 
    throws  JAXRException {
        ConceptImpl c = new ConceptImpl();
        c.setParent( parent );
        c.setName( name);
        c.setValue( value);
        return c;
    }
    
    public Concept createConcept( RegistryObject parent, 
                    String name, String value) throws  JAXRException {
           ConceptImpl c = new ConceptImpl();
           c.setParent( parent );
           c.setName( new InternationalStringImpl(name));
           c.setValue( value);
           return c;
    }
    
   public EmailAddress createEmailAddress(String type) 
    throws  JAXRException {
        EmailAddressImpl email = new EmailAddressImpl();
        email.setAddress( "" );
        email.setType( type );
        return email;
    }
    
    public EmailAddress createEmailAddress(String str, String type) 
    throws  JAXRException {
        EmailAddressImpl email = new EmailAddressImpl();
        email.setAddress( str );
        email.setType( type );
        return email;
    }
    
   /**
     *    Creates an ExternalIdentifier instance using the specified parameters, where the name is an InternationalString.
          Parameters:        identificationScheme - the ClassificationScheme used
                name - the name of the external identifier (an InternationalString)
                value - the value of the external identifier
          Returns:        the ExternalIdentifier instance created
     */
    public ExternalIdentifier createExternalIdentifier(  
        ClassificationScheme cs,   
        InternationalString name, String str) throws  JAXRException {
           ExternalIdentifierImpl ext = new ExternalIdentifierImpl();
           ext.setIdentificationScheme(cs);
           ext.setName( name );
           ext.setValue(str );
           ext.setLifeCycleManager( this );
           return ext;
    }
    
    public ExternalIdentifier createExternalIdentifier(  
           ClassificationScheme cs, 
           String name, String value) throws  JAXRException {
                ExternalIdentifierImpl ext = new ExternalIdentifierImpl();
                ext.setIdentificationScheme(cs);
                ext.setName( new InternationalStringImpl(name) );
                ext.setValue(value );
                ext.setLifeCycleManager( this );
                return ext;
    }
    
    
    public ExternalLink createExternalLink(String uri, String desc) 
    throws JAXRException {
         return this.createExternalLink(uri,
            new InternationalStringImpl( desc ));
    }
    
    public ExternalLink createExternalLink(String uri, InternationalString desc) 
    throws JAXRException {
        ExternalLinkImpl ext = new ExternalLinkImpl();
        ext.setExternalURI( uri );
        ext.setDescription(desc ); 
        ext.setLifeCycleManager( this );
        return ext;
    }
    
    public   ExtrinsicObject createExtrinsicObject( DataHandler dataHandler) 
    throws  JAXRException {
        return null;
    }
    
    public   InternationalString createInternationalString() 
    throws  JAXRException {
        return new InternationalStringImpl();
    }
    
    public   InternationalString createInternationalString(String str) 
    throws  JAXRException {
        return new InternationalStringImpl(str);
    }
    
    public   InternationalString createInternationalString( Locale locale, 
                                  String str) throws  JAXRException {                                      
      return new InternationalStringImpl(locale,str);
    }
    
    public Key createKey(String str) throws  JAXRException {
        return new KeyImpl(str);
    }
       
    public   LocalizedString createLocalizedString( Locale locale, 
                           String str) throws  JAXRException {
          return new LocalizedStringImpl(locale, str);
    }
    
    public   LocalizedString createLocalizedString( Locale locale, 
                           String str, String charset) throws  JAXRException {
         String charstr = "";
         try{
             charstr = new String(str.getBytes(), charset);
         }catch( java.io.UnsupportedEncodingException io){
             //Assume that we will use the default charset
             charstr = str;
         }
         return new LocalizedStringImpl(locale, charstr);
    }
    
    
    public Object createObject(String str) throws  JAXRException,  
              InvalidRequestException,  UnsupportedCapabilityException {
                  return null;
    }
    
    public   Organization createOrganization(String str) 
    throws  JAXRException {
        return this.createOrganization(new InternationalStringImpl( str) );        
    }
    
    public   Organization createOrganization( 
             InternationalString istr) throws  JAXRException {
                 OrganizationImpl org = new OrganizationImpl();
                 org.setName( istr);
                 org.setLifeCycleManager( this); 
                 return org;
    }
    
    
    public   PersonName createPersonName(String fullname) throws  JAXRException {
        PersonNameImpl pn = new PersonNameImpl(fullname);
        return pn;
    }
    
    public   PersonName createPersonName( String firstName,
                                    String middleName, String lastName) 
               throws  JAXRException {
        PersonNameImpl pn = new PersonNameImpl( firstName,middleName,lastName);
        return pn;
    }
    
    public   PostalAddress createPostalAddress( String streetNumber,
                                        String street,
                                          String city,
                                         String stateOrProvince,
                                       String country,
                                         String postalCode,
                                       String type) 
    throws  JAXRException {  
         PostalAddressImpl post = new PostalAddressImpl();
         post.setStreetNumber(streetNumber);
         post.setStreet(street);
         post.setCity( city);
         post.setStateOrProvince(stateOrProvince);
         post.setCountry( country);
         post.setPostalCode(postalCode);
         post.setType( type );
         return post;
    }
    
    public   RegistryPackage createRegistryPackage(String str) 
    throws  JAXRException {
        return null;
    }
    
    public   RegistryPackage createRegistryPackage(  
                            InternationalString internationalString) 
    throws  JAXRException {
        return null;
    }
    
    public   Service createService(String str) throws  JAXRException {
        ServiceImpl serve = new ServiceImpl();
        serve.setName( new InternationalStringImpl(str ));
        return serve;         
    }
    
    public   Service createService(  InternationalString istr) 
    throws  JAXRException {
        ServiceImpl serve = new ServiceImpl();
        serve.setName( istr );
        return serve;
    }
    
    public   ServiceBinding createServiceBinding() throws  JAXRException {
        return null;
    }
    
    public Slot createSlot(String str,  Collection collection, String str2) 
    throws JAXRException {
        return null;
    }
    
    public Slot createSlot(String str, String str1, String str2) 
    throws JAXRException {
        return null;
    }
    
    public SpecificationLink createSpecificationLink() throws JAXRException {
        return null;
    }
    
     public   TelephoneNumber createTelephoneNumber() throws  JAXRException {
        TelephoneNumberImpl tel = new TelephoneNumberImpl();
        return tel;
    }
    
    public   User createUser() throws  JAXRException {
        return new UserImpl();
    }
    
    public  BulkResponse deleteObjects( Collection collection) 
    throws JAXRException {
        return null;
    }
    
    public  BulkResponse deleteObjects( Collection collection, String str) 
    throws JAXRException {
        return null;
    }
    
    public  BulkResponse deprecateObjects( Collection collection) 
    throws JAXRException {
      return null;
    }
    
    public  RegistryService getRegistryService() 
    throws JAXRException {
        return null;
    }
    
    public  BulkResponse saveObjects( Collection collection) 
    throws JAXRException {
        return null;
    }
    
    public  BulkResponse unDeprecateObjects( Collection collection) 
    throws JAXRException {
        return null;
    }
    
}
