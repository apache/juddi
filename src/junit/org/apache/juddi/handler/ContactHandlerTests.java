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

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.apache.juddi.datatype.Address;
import org.apache.juddi.datatype.AddressLine;
import org.apache.juddi.datatype.Description;
import org.apache.juddi.datatype.Email;
import org.apache.juddi.datatype.Phone;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.business.Contact;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author anou_mana@apache.org
 */
public class ContactHandlerTests extends TestCase
{
	private static final String TEST_ID = "juddi.handler.Contact.test";
	private ContactHandler handler = null;

  public ContactHandlerTests(String arg0)
  {
    super(arg0);
  }

  public static void main(String[] args)
  {
    junit.textui.TestRunner.run(ContactHandlerTests.class);
  }

  public void setUp()
  {
		HandlerMaker maker = HandlerMaker.getInstance();
		handler = (ContactHandler)maker.lookup(ContactHandler.TAG_NAME);
  }

	private RegistryObject getRegistryObject()
	{


		Address address = new Address();
		address.setUseType("myAddressUseType");
		address.setSortCode("sortThis");
		address.setTModelKey(null);
		address.addAddressLine(new AddressLine("AddressLine1","keyNameAttr","keyValueAttr"));
		address.addAddressLine(new AddressLine("AddressLine2"));

		Contact object = new Contact();
		object.setUseType("myAddressUseType");
		object.setPersonNameValue("Person Whatever");
		object.addDescription(new Description("Description1"));
		object.addDescription(new Description("Description2","es"));
		object.addEmail(new Email("person@whatever.com"));
		object.addPhone(new Phone("(123)456-7890"));
		object.addAddress(address);

		return object;

	}

	private String getXMLString(Element element)
	{
		StringWriter writer = new StringWriter();
		XMLUtils.writeXML(element,writer);

		String xmlString = writer.toString();

		try
		{
			writer.close();
		}
		catch(IOException exp)
		{
		}

		return xmlString;
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

		assertNotNull("Marshalled Contact ", marshalledString);

	}

	public void testUnMarshal()
	{

		Element child = getMarshalledElement(null);
		RegistryObject regObject = handler.unmarshal(child);

		assertNotNull("UnMarshalled Contact ", regObject);

	}

  public void testMarshUnMarshal()
  {
		Element child = getMarshalledElement(null);

		String marshalledString = this.getXMLString(child);

		assertNotNull("Marshalled Contact ", marshalledString);

		RegistryObject regObject = handler.unmarshal(child);

		child = getMarshalledElement(regObject);

		String unMarshalledString = this.getXMLString(child);

		assertNotNull("Unmarshalled Contact ", unMarshalledString);

		boolean equals = marshalledString.equals(unMarshalledString);

		assertEquals("Expected result: ", marshalledString, unMarshalledString );
  }

}
