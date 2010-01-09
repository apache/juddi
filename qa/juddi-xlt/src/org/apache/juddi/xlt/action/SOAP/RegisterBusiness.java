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
package org.apache.juddi.xlt.action.SOAP;

import javax.xml.bind.JAXBElement;

import org.apache.commons.lang.StringUtils;
import org.apache.juddi.xlt.util.JUDDIServiceProvider;
import org.junit.Assert;
import org.uddi.api_v3.Address;
import org.uddi.api_v3.AddressLine;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.BusinessServices;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.Contact;
import org.uddi.api_v3.Contacts;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.DiscoveryURL;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.DiscoveryURLs;
import org.uddi.api_v3.PersonName;
import org.uddi.api_v3.Phone;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.v3_service.UDDIPublicationPortType;

import com.sun.org.apache.xerces.internal.xni.QName;
import com.xceptance.xlt.api.data.GeneralDataProvider;

/**
 * Publishes a business to the jUDDI node.
 * @author jeremi
 *
 */
public class RegisterBusiness extends AbstractJUDDIAction
{
	private UDDIPublicationPortType publishService;
	private AuthToken authenticationToken;
	private BusinessDetail businessDetail;
	private String businessName; 
	private GeneralDataProvider data = GeneralDataProvider.getInstance();
	
	/**
	 * Constructor. Only the AuthToken is given here as argument meaning that 
	 * most values for this business will be randomly generated.
	 * @param authenticationToken
	 * @param businessName
	 */
	public RegisterBusiness(AuthToken authenticationToken)
	{
		super(null);
		this.authenticationToken = authenticationToken;
		this.businessName = data.getCompany(false);;
	}
	
	/**
	 * Constructor. The AuthToken and the name of the business name are given as
	 * arguments. Other values for the business will be determined randomly.
	 * @param authenticationToken
	 */
	public RegisterBusiness(AuthToken authenticationToken, String businessName)
	{
		super(null);
		this.authenticationToken = authenticationToken;
		this.businessName = businessName;
	}
	
	/**
	 * The preValidate method ensures that all necessary elements
	 * are present to execute the action. Here we insure that the 
	 * publishing service is available.
	 */
	@Override
	public void preValidate() throws Exception
	{
		publishService = JUDDIServiceProvider.getPublishService();
		Assert.assertNotNull("Publish service is missing", publishService);
		
	/**
	 * The execute method will send the SOAP message to jUDDI. In this case, 
	 * we create a BusinessEntity and fill the different values with random
	 * information. The BusinessEntity is then given to a SaveBusiness object
	 * and published to the jUDDI node.
	 */
	}
	@Override
	protected void execute() throws Exception
	{	
		//Business name in English.
		Name name = new Name();
		name.setLang("en");
		name.setValue(businessName);
		
		//Business description in English.
		Description description = new Description();
		description.setLang("en");
		description.setValue(data.getText(2, 5, false));
		
		//Business URL.
		DiscoveryURL discoveryURL = new DiscoveryURL();
		discoveryURL.setUseType("General Description");
		discoveryURL.setValue("http://www." + StringUtils.deleteWhitespace(businessName) + ".com");
		
		//All business URLs if many (here we only give one).
		DiscoveryURLs discoveryURLs = new DiscoveryURLs();
		discoveryURLs.getDiscoveryURL().add(discoveryURL);
		
		//Contact info:
		//Name of contact in English.
		PersonName personName = new PersonName();
		personName.setLang("en");
		personName.setValue(data.getFirstName(true) + data.getLastName(true));
		
		//Address lines for contact.
		AddressLine addressLineOne = new AddressLine();
		addressLineOne.setKeyName("street");
		addressLineOne.setKeyValue("street");
		addressLineOne.setValue(data.getStreet(false));
		
		AddressLine addressLineTwo = new AddressLine();
		addressLineTwo.setKeyName("town");
		addressLineTwo.setKeyValue("town");
		addressLineTwo.setValue(data.getTown(false));
		
		AddressLine addressLineThree = new AddressLine();
		addressLineThree.setKeyName("zip");
		addressLineThree.setKeyValue("zip");
		addressLineThree.setValue(data.getZip(5));
		
		//One address for the contact. Can be more but only one here.
		Address address = new Address();
		address.setLang("en");
		address.setSortCode("1");
		address.setTModelKey("uddi:tmodelKey:address");
		address.setUseType("HQ");
		address.getAddressLine().add(addressLineOne);
		address.getAddressLine().add(addressLineTwo);
		address.getAddressLine().add(addressLineThree);
		
		//Contacts phone number details
		Phone phone = new Phone();
		phone.setUseType("Hotline");
		phone.setValue(data.getUSPhoneNumber());
		
		//Contact object
		Contact contact = new Contact();
		contact.setUseType("CEO");
		contact.getPersonName().add(personName);
		contact.getAddress().add(address);
		contact.getPhone().add(phone);
		
		//All contact for the business. Here only one is generated.
		Contacts contacts = new Contacts();
		contacts.getContact().add(contact);
		
		//Pass info to business entity.
		BusinessEntity businessEntity = new BusinessEntity();
		businessEntity.setDiscoveryURLs(discoveryURLs);
		businessEntity.getName().add(name);
		businessEntity.getDescription().add(description);
		businessEntity.setContacts(contacts);
				
		//Pass Entity to SaveBussiness object and publish.
		SaveBusiness saveBusiness = new SaveBusiness();
		saveBusiness.setAuthInfo(authenticationToken.getAuthInfo());
		saveBusiness.getBusinessEntity().add(businessEntity);
		
		//Business Details are returned.
		businessDetail = publishService.saveBusiness(saveBusiness);
		
	}

	/**
	 * The postValidate ensure that the correct conditions exists after the 
	 * action has been executed. Here, we ensure that there is indeed a 
	 * returned BussinessDetail object and that it contains a business key
	 * and the proper business name.
	 */
	@Override
	protected void postValidate() throws Exception
	{
		Assert.assertNotNull("No business details were returned", businessDetail);
		String returnedName = businessDetail.getBusinessEntity().get(0).getName().get(0).getValue();
		String returnedKey = businessDetail.getBusinessEntity().get(0).getBusinessKey();
		Assert.assertNotNull("No business key was returned. Value of key: " + returnedKey, returnedKey);
		Assert.assertTrue("Business was not saved under the right name. '" 
				+ businessName + "' was expected but '" + returnedName + "' was returned.",
				returnedName.equals(businessName));
		
	}
	
	/**
	 * Returns the BusinessDetail object.
	 * @return
	 */
	public BusinessDetail getBusinessDetail()
	{
		return businessDetail;
	}

	
	

}
