/*
 * Copyright 2001-2010 The Apache Software Foundation.
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
 *
 */
package org.apache.juddi.xlt.util;

import java.util.ArrayList;
import java.util.List;

import org.uddi.api_v3.Address;
import org.uddi.api_v3.AddressLine;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessServices;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.Contact;
import org.uddi.api_v3.Contacts;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.DiscoveryURL;
import org.uddi.api_v3.DiscoveryURLs;
import org.uddi.api_v3.Email;
import org.uddi.api_v3.IdentifierBag;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.PersonName;
import org.uddi.api_v3.Phone;
import org.w3._2000._09.xmldsig_.SignatureType;
import javax.xml.bind.JAXBElement;

import sun.security.krb5.internal.crypto.Des;

import com.sun.crypto.provider.DESParameters;

public class UDDIUtils
{
	/**
	 * Generates a Name object for names of businesses or services.
	 * @param value
	 * @return 
	 */
	public static Name generateName(String value)
	{
		return generateName(value, null);
	}
	
	/**
	 * Generates a Name object for names of businesses or services. A
	 * language qualifier can also be included. This can be useful when 
	 * multiple name are applied businesses and services in different 
	 * languages.
	 * @param value
	 * @param lang
	 * @return
	 */
	public static Name generateName(String value, String lang)
	{
		Name name = new Name();
		name.setValue(value);
		if(lang != null)
			name.setLang(lang);
			
		return name;
	}
	
	/**
	 * Generates a Description object to describe businesses, services 
	 * and other objects.
	 * @param value
	 * @return
	 */
	public static Description generateDescription(String value)
	{
		
		return generateDescription(value, null);
	}
	
	/**
	 * Generates a Description object to describe businesses, services 
	 * and other objects. A language qualifier can also be included. 
	 * This can be useful when multiple name are applied businesses and 
	 * services in different languages.
	 * @param value
	 * @param lang
	 * @return
	 */
	public static Description generateDescription(String value, String lang)
	{
		Description description = new Description();
		description.setValue(value);
		if(lang != null)
			description.setLang(lang);
		
		return description;
	}
	
	/**
	 * Generates a DiscoveryUrl Object for businesses.
	 * @param businessUrl
	 * @return
	 */
	public static DiscoveryURL generateDiscoveryURL(String businessUrl)
	{
		return generateDiscoveryURL(businessUrl, null);
	}
	
	/**
	 * Generates a DiscoveryUrl Object for businesses. Url can 
	 * also be assigned a use type.
	 * @param businessUrl
	 * @param useType
	 * @return
	 */
	public static DiscoveryURL generateDiscoveryURL(String businessUrl, String useType)
	{
		DiscoveryURL discoveryURL = new DiscoveryURL();
		discoveryURL.setValue(businessUrl);
		if(useType != null)
			discoveryURL.setUseType(useType);
		
		return discoveryURL;
	}
	
	/**
	 * Generates a Contact object that can be associated with a 
	 * business. Undefined entities can be replaced with a "null" 
	 * value.
	 * @param address
	 * @param description
	 * @param email
	 * @param personName
	 * @param phone
	 * @param useType
	 * @return
	 */
	public static Contact generateContact(List<Address> addresses, List<Description> descriptions, 
			List<Email> emails, List<PersonName> personNames, List<Phone> phones, String useType)
	{
		Contact contact = new Contact();
		if (addresses != null)
			for (Address a : addresses)
				contact.getAddress().add(a);
		if (descriptions != null)
			for (Description d : descriptions)
				contact.getDescription().add(d);
		if (emails != null)
			for (Email e : emails)
				contact.getEmail().add(e);
		if (personNames != null)
			for (PersonName pn : personNames)
				contact.getPersonName().add(pn);
		if (phones != null)
			for (Phone p : phones)
				contact.getPhone().add(p);
		if (useType != null)
			contact.setUseType(useType);
		
		return contact;
	}
	
	/**
	 * Generates a Contact object. This method is much more restrictive but allows for an
	 * easier instantiation by only supplying important strings as parameters.
	 * 
	 * @param contactAddress
	 * @param contactDescription
	 * @param contactEmail
	 * @param contactName
	 * @param contactPhone
	 * @param useType
	 * @return
	 */
	public static Contact generateContact(String contactAddress, String contactDescription, 
			String contactEmail, String contactName, String contactPhone, String useType)
	{
		Contact contact = new Contact();
		List<AddressLine> addressLine = new ArrayList<AddressLine>();
		addressLine.add(generateAddressLine(contactAddress, null, null));
		
		contact.getAddress().add(generateAddress(addressLine, null, null, null, null));
		contact.getDescription().add(generateDescription(contactDescription));
		contact.getEmail().add(generateEmail(contactEmail, null));
		contact.getPersonName().add(generatePersonName(contactName, null));
		contact.getPhone().add(generatePhone(contactPhone));
		contact.setUseType(useType);
		
		return contact;
	}
	
	/**
	 * Generates as Address object that can be associated with a 
	 * contact. Undefined entities can be replaced with a "null" 
	 * value.
	 * @param addressLines
	 * @param lang
	 * @param sortCode
	 * @param tModelKey
	 * @param useType
	 * @return
	 */
	public static Address generateAddress(List<AddressLine> addressLines, String lang, String sortCode, String tModelKey, String useType)
	{
		Address address = new Address();
		if (addressLines != null)
			for (AddressLine al : addressLines)
				address.getAddressLine().add(al);
		if (lang != null)
			address.setLang(lang);
		if (sortCode != null)
			address.setSortCode(sortCode);
		if (tModelKey != null)
			address.setTModelKey(tModelKey);
		if (useType != null)
			address.setUseType(useType);
		
		return address;
	}
	
	/**
	 * Generates a Addressline object to be passed to an address. the keyName and keyValue 
	 * arguments are descriptors for the line, such as "country", "zip", "street", etc and
	 * can be replaced by the "null" value.
	 * @param value
	 * @param keyName
	 * @param keyValue
	 * @return
	 */
	public static AddressLine generateAddressLine(String value, String keyName,String keyValue)
	{
		AddressLine addressLine = new AddressLine();
		addressLine.setValue(value);
		if (keyName != null)
			addressLine.setKeyName(keyName);
		if (keyValue != null)
			addressLine.setKeyValue(keyValue);
		
		return addressLine;
	}
	
	/**
	 * Generates an Email object to pass to a contact. Email can be given a use type 
	 * or if unknown, the "null" value.
	 * @param value
	 * @param useType
	 * @return
	 */
	public static Email generateEmail (String value, String useType)
	{
		Email email = new Email();
		email.setValue(value);
		if (useType != null)
			email.setUseType(useType);
		
		return email;
	}
	
	/**
	 * Generates a {@link PersonName} object to pass to a contact. The language (lang) 
	 * parameter can be set to "null". 
	 * @param value
	 * @param lang
	 * @return
	 */
	public static PersonName generatePersonName (String value, String lang)
	{
		PersonName personName = new PersonName();
		personName.setValue(value);
		if (lang != null)
			personName.setLang(lang);
		
		return personName;
	}
	
	/** 
	 * Generates a Phone object to associate with a contact.
	 * @param phoneNumber
	 * @return
	 */
	public static Phone generatePhone(String phoneNumber)
	{
		return generatePhone(phoneNumber, null);
	}
	
	/** 
	 * Generates a Phone object to associate with a contact. Phone number can 
	 * also be assigned a use type or it can be set to "null".
	 * @param phoneNumber
	 * @param useType
	 * @return
	 */
	public static Phone generatePhone(String phoneNumber, String useType)
	{
		Phone phone = new Phone();
		phone.setValue(phoneNumber);
		if (useType != null)
			phone.setUseType(useType);
		
		return phone;
	}
	
	/**
	 * Generates a BusinessEntity object by passing necessary objects as parameters.
	 * @param names
	 * @param descriptions
	 * @param contacts
	 * @param discoveryURLs
	 * @param categoryBag
	 * @param identifierBag
	 * @param signatures
	 * @param businessServices
	 * @param businessKey
	 * @return
	 */
	public static BusinessEntity generateBusinessEntity(List<Name> names, List<Description> descriptions, 
			Contacts contacts, DiscoveryURLs discoveryURLs, CategoryBag categoryBag, IdentifierBag identifierBag, 
			List<SignatureType> signatures, BusinessServices businessServices, String businessKey)
	{
		BusinessEntity businessEntity = new BusinessEntity();
		for (Name n : names)
			businessEntity.getName().add(n);
		if (descriptions != null)
		{
			for (Description d : descriptions)
				businessEntity.getDescription().add(d);
		}
		if (contacts != null)
			businessEntity.setContacts(contacts);
		if (discoveryURLs != null)
			businessEntity.setDiscoveryURLs(discoveryURLs);
		if (categoryBag != null)
			businessEntity.setCategoryBag(categoryBag);
		if (identifierBag != null)
			businessEntity.setIdentifierBag(identifierBag);
		if (signatures != null)
			for (SignatureType st : signatures)
				businessEntity.getSignature().add(st);
		if (businessServices != null)
			businessEntity.setBusinessServices(businessServices);
		if (businessKey != null)
			businessEntity.setBusinessKey(businessKey);
		
		return businessEntity;
	}
	
	/**
	 * Generates a BussinessEntity object by passing only strings as parameters. This restricts 
	 * the complexity of the {@link BusinessEntity} but it is more easily created. The returned object
	 * can be modified afterwards.
	 * 
	 * @param names
	 * @param descriptions
	 * @param contactName
	 * @param contactPhone
	 * @param contactAddress
	 * @param contactDescription
	 * @param contactEmail
	 * @param discoveryURL
	 * @param categoryBag
	 * @param identifierBag
	 * @param signature
	 * @param businessServices
	 * @param businessKey
	 * @return
	 */
	public static BusinessEntity generateBusinessEntity(String businessName, String businessDescription, 
			String contactName, String contactPhone, String contactAddress, 
			String contactDescription, String contactEmail, String discoveryURL, 
			String categoryBag, String identifierBag, String signature, String businessServices, 
			String businessKey)
	{
		BusinessEntity businessEntity = new BusinessEntity();
		
		businessEntity.getName().add(generateName(businessName));
		businessEntity.getDescription().add(generateDescription(businessDescription));
		
		Contacts contacts = new Contacts();
		contacts.getContact().add(generateContact(contactAddress, contactDescription, contactEmail,
				contactName, contactPhone, null));
		businessEntity.setContacts(contacts);
		
		DiscoveryURLs discoveryURLs = new DiscoveryURLs();
		discoveryURLs.getDiscoveryURL().add(generateDiscoveryURL(discoveryURL));
		businessEntity.setDiscoveryURLs(discoveryURLs);
		
		//businessEntity.setCategoryBag(?)
		//businessEntity.setIdentifierBag(?)
		//businessEntity.getSignature(?)
		//businessEntity.setBusinessServices(?)
		//businessEntity.setBusinessKey(?)
		
		return businessEntity;
	}
	
	public static Contacts generateContacts (List<Contact> contactList)
	{
		Contacts contacts = new Contacts();
		for (Contact c : contactList)
			contacts.getContact().add(c);
		
		return contacts;
	}
	
	public static DiscoveryURLs generateDiscoveryURLs (List<DiscoveryURL> discoveryURLList)
	{
		DiscoveryURLs discoveryURLs = new DiscoveryURLs();
		for (DiscoveryURL du : discoveryURLList)
			discoveryURLs.getDiscoveryURL().add(du);
		
		return discoveryURLs;
	}
	
	
	
	/*generateBusinessList
	generateBusinessService
	generateServiceInfo
	generateServiceInfos
	generateServiceList
	generatePersonName
	generatePhone
	generateAddress
	generateContact*/

	
	

}
