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
package org.apache.scout.UDDI;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;
 
import javax.xml.registry.JAXRException;
import javax.xml.registry.infomodel.EmailAddress;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.PostalAddress;
import javax.xml.registry.infomodel.RegistryObject;
import javax.xml.registry.infomodel.Service;
import javax.xml.registry.infomodel.TelephoneNumber;
import javax.xml.registry.infomodel.User;

import org.apache.scout.registry.infomodel.InternationalStringImpl;
import org.uddi4j.datatype.business.Address;
import org.uddi4j.datatype.business.AddressLine;
import org.uddi4j.datatype.business.BusinessEntity;
import org.uddi4j.datatype.business.Contact;
import org.uddi4j.datatype.business.Contacts;
import org.uddi4j.datatype.business.Email;
import org.uddi4j.datatype.business.Phone;
import org.uddi4j.datatype.service.BusinessService;
import org.uddi4j.datatype.service.BusinessServices;

/**
 * Helper class that creates UDDI4J objects from JAXR Objects sent from client.
 * @author Anil Saldhana  <anil@apache.org>
 */
public class UDDIHelper {
//  *****************************
    //UDDI Related Methods
    //*****************************
    
    
    /**
     * Package Protected:Gets UDDI4J BusinessService object from 
     * JAXR Service Object.
     */    
    BusinessService getBusinessServiceFromJAXRService( Service serve ) 
    throws JAXRException {
        BusinessService bs = new BusinessService();
        try{
            InternationalStringImpl iname = (InternationalStringImpl)((RegistryObject) serve).getName();
            String name = iname.getValue();             
            bs.setDefaultNameString( name, Locale.getDefault().getLanguage());
            bs.setBusinessKey( ((RegistryObject) serve).getKey().getId() );
            bs.setDefaultDescriptionString( ((RegistryObject) serve).getDescription().getValue() );
            System.out.println( "BusinessService="+bs.toString());
        }catch(Exception ud){
                throw new JAXRException( "Apache JAXR Impl:", ud);
        }
        return bs;          
    }
    
    /**
     * Package Protected:Gets UDDI4J BusinessEntity object from JAXR Organization.
     */    
    BusinessEntity getBusinessEntityFromJAXROrg( Organization org ) 
    throws JAXRException {
        BusinessEntity biz = new BusinessEntity(); 
        BusinessServices bss = new BusinessServices();
        Contacts cts = new Contacts();
        Vector bvect =  new Vector();
        Vector cvect = new Vector();
        
        try{
            //Lets get the Organization attributes at the top level
            String language = Locale.getDefault().getLanguage();
             
            biz.setDefaultNameString( org.getName().getValue() , language);
            biz.setDefaultDescriptionString( org.getDescription().getValue() );
            
            Collection s = org.getServices();
            System.out.println( "?Org has services="+s.isEmpty());
            Iterator iter = s.iterator();
            while( iter.hasNext()){
                BusinessService bs = 
                  getBusinessServiceFromJAXRService( (Service)iter.next());
                bvect.add(bs);
            }
            
            Collection users = org.getUsers();
            System.out.println( "?Org has users="+users.isEmpty());
            Iterator it  = users.iterator();
            while( it.hasNext()){
                Contact ct = 
                  getContactFromJAXRUser( (User)it.next());
                cvect.add(ct);
            }
            
            bss.setBusinessServiceVector( bvect );
            cts.setContactVector( cvect );
            biz.setContacts( cts );
            biz.setBusinessServices( bss ); 
            log("Business="+biz.toString());
        }catch(Exception ud){
                throw new JAXRException( "Apache JAXR Impl:", ud);
        }
        return biz;  
        
    }
    
    /**
     * Package Protected: Convert JAXR User Object to UDDI4J Contact
     */    
    Contact getContactFromJAXRUser( User user)
    throws JAXRException {
         Contact ct = new Contact();
         Vector addvect = new Vector();
         Vector phonevect = new Vector();
         Vector emailvect = new Vector();
         try{
             ct.setPersonName( user.getPersonName().getFullName() );
             //Postal Address
             Collection postc = user.getPostalAddresses();
             Iterator iterator  = postc.iterator();
              while( iterator.hasNext()){
                  PostalAddress post = (PostalAddress)iterator.next();
                  addvect.add( getAddress( post) );                   
              }
             //Phone Numbers
             Collection ph = user.getTelephoneNumbers(null);
             Iterator it  = ph.iterator();
              while( it.hasNext()){
                 TelephoneNumber t = (TelephoneNumber)it .next();
                 Phone phone = new Phone(); 
                 String str = t.getCountryCode()+"-"+t.getAreaCode()+"-"+t.getNumber();
                 System.out.println( "Telephone="+str );
                 phone.setText( str );
                 phonevect.add(phone);
             }
             
             //Email Addresses
             Collection ec = user.getEmailAddresses();
             Iterator iter = ec.iterator();
             while( iter.hasNext()){
                 EmailAddress ea = (EmailAddress)iter.next();
                 Email email = new Email();
                 email.setText( ea.getAddress() );
                 email.setUseType(ea.getType());
                 emailvect.add(email);
             }
             ct.setAddressVector( addvect );
             ct.setPhoneVector( phonevect );
             ct.setEmailVector( emailvect );
             
         }catch(Exception ud){
                throw new JAXRException( "Apache JAXR Impl:", ud);
        }
        return ct;   
    }
    
    /**
     * Package protected:Get UDDI4J Address given JAXR Postal Address
     */    
    Address getAddress( PostalAddress post)
    throws JAXRException {
        Address address = new Address();
        
        Vector addvect  = new Vector();
        
        String stnum = post.getStreetNumber();
        String st  = post.getStreet();
        String city = post.getCity();
        String country = post.getCountry();
        String code = post.getPostalCode();
        String state = post.getStateOrProvince();
        
        AddressLine stnumAL = new AddressLine();
        stnumAL.setKeyName( "STREET_NUMBER");
        stnumAL.setKeyValue(stnum );
        
        AddressLine stAL = new AddressLine();
        stAL.setKeyName( "STREET");
        stAL.setKeyValue(st );
        
        AddressLine cityAL = new AddressLine();
        cityAL.setKeyName( "CITY");
        cityAL.setKeyValue(city );
        
        AddressLine countryAL = new AddressLine();
        countryAL.setKeyName( "COUNTRY");
        countryAL.setKeyValue(country );
        
        AddressLine codeAL = new AddressLine();
        codeAL.setKeyName( "POSTALCODE");
        codeAL.setKeyValue(code );
        
        AddressLine stateAL = new AddressLine();
        stateAL.setKeyName( "STATE");
        stateAL.setKeyValue(state );
        
        //Add the AddressLine to vector
        addvect.add(stnumAL);
        addvect.add(stAL);
        addvect.add(cityAL);
        addvect.add(countryAL);
        addvect.add(codeAL);
        addvect.add(stateAL);
        
        address.setAddressLineVector( addvect );
        
        return address;        
    } 
    
    private void log( String str ){
        System.out.println( str );
    }

}
