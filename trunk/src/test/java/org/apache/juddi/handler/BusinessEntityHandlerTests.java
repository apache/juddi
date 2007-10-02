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
package org.apache.juddi.handler;

import org.apache.juddi.datatype.Address;
import org.apache.juddi.datatype.AddressLine;
import org.apache.juddi.datatype.CategoryBag;
import org.apache.juddi.datatype.Description;
import org.apache.juddi.datatype.DiscoveryURL;
import org.apache.juddi.datatype.DiscoveryURLs;
import org.apache.juddi.datatype.Email;
import org.apache.juddi.datatype.IdentifierBag;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.OverviewDoc;
import org.apache.juddi.datatype.Phone;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.binding.AccessPoint;
import org.apache.juddi.datatype.binding.BindingTemplate;
import org.apache.juddi.datatype.binding.BindingTemplates;
import org.apache.juddi.datatype.binding.HostingRedirector;
import org.apache.juddi.datatype.binding.InstanceDetails;
import org.apache.juddi.datatype.binding.TModelInstanceDetails;
import org.apache.juddi.datatype.binding.TModelInstanceInfo;
import org.apache.juddi.datatype.business.BusinessEntity;
import org.apache.juddi.datatype.business.Contact;
import org.apache.juddi.datatype.business.Contacts;
import org.apache.juddi.datatype.service.BusinessService;
import org.apache.juddi.datatype.service.BusinessServices;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author anou_mana@apache.org
 */
public class BusinessEntityHandlerTests extends HandlerTestCase
{
	private static final String TEST_ID = "juddi.handler.BusinessEntity.test";
	private BusinessEntityHandler handler = null;

  public BusinessEntityHandlerTests(String arg0)
  {
    super(arg0);
  }

  public static void main(String[] args)
  {
    junit.textui.TestRunner.run(BusinessEntityHandlerTests.class);
  }

  public void setUp()
  {
		HandlerMaker maker = HandlerMaker.getInstance();
		handler = (BusinessEntityHandler)maker.lookup(BusinessEntityHandler.TAG_NAME);
  }

	private RegistryObject getRegistryObject()
	{

		OverviewDoc overDoc = new OverviewDoc();
		overDoc.setOverviewURL("http://www.juddi.org/service.html");
		overDoc.addDescription(new Description("over-doc Descr"));
		overDoc.addDescription(new Description("over-doc Descr Two","en"));

		InstanceDetails instDetails = new InstanceDetails();
		instDetails.addDescription(new Description("inst-det descr"));
		instDetails.addDescription(new Description("inst-det descr in italy","it"));
		instDetails.setInstanceParms("inst-det parameters");
		instDetails.setOverviewDoc(overDoc);

		TModelInstanceInfo tModInstInfo = new TModelInstanceInfo();
		tModInstInfo.setTModelKey("uuid:ae0f9fd4-287f-40c9-be91-df47a7c72fd9");
		tModInstInfo.addDescription(new Description("tMod-Inst-Info"));
		tModInstInfo.addDescription(new Description("tMod-Inst-Info too","es"));
		tModInstInfo.setInstanceDetails(instDetails);

		TModelInstanceDetails tModInstDet = new TModelInstanceDetails();
		tModInstDet.addTModelInstanceInfo(tModInstInfo);

		BindingTemplate binding =  new BindingTemplate();
		binding.setBindingKey("c9613c3c-fe55-4f34-a3da-b3167afbca4a");
		binding.setServiceKey("997a55bc-563d-4c04-8a94-781604870d31");
		binding.addDescription(new Description("whatever"));
		binding.addDescription(new Description("whatever too","fr"));
		binding.setHostingRedirector(new HostingRedirector("92658289-0bd7-443c-8948-0bb4460b44c0"));
		binding.setAccessPoint(new AccessPoint(AccessPoint.HTTP,"http://www.juddi.org/service.wsdl"));
		binding.setTModelInstanceDetails(tModInstDet);

		BindingTemplates bindings = new BindingTemplates();
		bindings.addBindingTemplate(binding);

		CategoryBag catBag = new CategoryBag();
		catBag.addKeyedReference(new KeyedReference("catBagKeyName","catBagKeyValue"));
		catBag.addKeyedReference(new KeyedReference("uuid:dfddb58c-4853-4a71-865c-f84509017cc7","catBagKeyName2","catBagKeyValue2"));

		IdentifierBag idBag = new IdentifierBag();
		idBag.addKeyedReference(new KeyedReference("idBagKeyName","idBagkeyValue"));
		idBag.addKeyedReference(new KeyedReference("uuid:f78a135a-4769-4e79-8604-54d440314bc0","idBagKeyName2","idBagkeyValue2"));

		DiscoveryURLs discURLs = new DiscoveryURLs();
		discURLs.addDiscoveryURL(new DiscoveryURL("http","http://www.juddi.org/service.wsdl"));
		discURLs.addDiscoveryURL(new DiscoveryURL("https","https://www.juddi.org/service.wsdl"));
		discURLs.addDiscoveryURL(new DiscoveryURL("smtp","servicemngr@juddi.org"));

		Address address = new Address();
		address.setUseType("myAddressUseType");
		address.setSortCode("sortThis");
		address.setTModelKey(null);
		address.addAddressLine(new AddressLine("AddressLine1","keyNameAttr","keyValueAttr"));
		address.addAddressLine(new AddressLine("AddressLine2"));

		Contact contact = new Contact();
		contact.setUseType("myAddressUseType");
		contact.setPersonNameValue("Person Whatever");
		contact.addDescription(new Description("Description1"));
		contact.addDescription(new Description("Description2","es"));
		contact.addEmail(new Email("person@whatever.com"));
		contact.addPhone(new Phone("(123)456-7890"));
		contact.addAddress(address);

		Contacts contacts = new Contacts();
		contacts.addContact(contact);

		BusinessService service = new BusinessService();
		service.setServiceKey("fe8af00d-3a2c-4e05-b7ca-95a1259aad4f");
		service.setBusinessKey("b8cc7266-9eed-4675-b621-34697f252a77");
		service.setBindingTemplates(bindings);
		service.setCategoryBag(catBag);
		service.addName(new Name("serviceNm"));
		service.addName(new Name("serviceNm2","en"));
		service.addDescription(new Description("service whatever"));
		service.addDescription(new Description("service whatever too","it"));

		BusinessServices services = new BusinessServices();
		services.addBusinessService(service);
		services.addBusinessService(service);

		BusinessEntity object = new BusinessEntity();
		object.setBusinessKey("6c0ac186-d36b-4b81-bd27-066a5fe0fc1f");
		object.setAuthorizedName("Guest");
		object.setOperator("jUDDI");
		object.addName(new Name("businessNm"));
		object.addName(new Name("businessNm2","en"));
		object.addDescription(new Description("business whatever"));
		object.addDescription(new Description("business whatever too","fr"));
		object.setDiscoveryURLs(discURLs);
		object.setCategoryBag(catBag);
		object.setIdentifierBag(idBag);
		object.setContacts(contacts);
		object.setBusinessServices(services);

		return object;

	}

	private Element getMarshalledElement(RegistryObject regObject)
	{
		Element parent = XMLUtils.newRootElement();
		Element child = null;

		if(regObject == null)
			regObject = this.getRegistryObject();

		handler.marshal(regObject,parent);
		child = (Element)parent.getFirstChild();
		parent.removeChild(child);

		return child;
	}

	public void testMarshal()
	{
		Element child = getMarshalledElement(null);

		String marshalledString = this.getXMLString(child);

		assertNotNull("Marshalled BusinessEntity ", marshalledString);

	}

	public void testUnMarshal()
	{

		Element child = getMarshalledElement(null);
		RegistryObject regObject = handler.unmarshal(child);

		assertNotNull("UnMarshalled BusinessEntity ", regObject);

	}

  public void testMarshUnMarshal()
  {
		Element child = getMarshalledElement(null);

		String marshalledString = this.getXMLString(child);

		assertNotNull("Marshalled BusinessEntity ", marshalledString);

		RegistryObject regObject = handler.unmarshal(child);

		child = getMarshalledElement(regObject);

		String unMarshalledString = this.getXMLString(child);

		assertNotNull("Unmarshalled BusinessEntity ", unMarshalledString);

		boolean equals = marshalledString.equals(unMarshalledString);

		assertEquals("Expected result: ", marshalledString, unMarshalledString );
  }

}
