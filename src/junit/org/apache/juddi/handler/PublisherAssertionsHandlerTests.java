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

import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.assertion.PublisherAssertion;
import org.apache.juddi.datatype.response.PublisherAssertions;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author anou_mana@apache.org
 */
public class  PublisherAssertionsHandlerTests extends TestCase
{
	private static final String TEST_ID = "juddi.handler.DeletePublisher.test";
	private  PublisherAssertionsHandler handler = null;

  public  PublisherAssertionsHandlerTests(String arg0)
  {
    super(arg0);
  }

  public static void main(String[] args)
  {
    junit.textui.TestRunner.run( PublisherAssertionsHandlerTests.class);
  }

  public void setUp()
  {
		HandlerMaker maker = HandlerMaker.getInstance();
		handler = ( PublisherAssertionsHandler)maker.lookup( PublisherAssertionsHandler.TAG_NAME);
  }

	private RegistryObject getRegistryObject()
	{
		PublisherAssertion assertion = new PublisherAssertion();
		assertion.setFromKey("b2f072e7-6013-4385-93b4-9c1c2ece1c8f");
		assertion.setToKey("115be72d-0c04-4b5f-a729-79a522629c19");
		assertion.setKeyedReference(new KeyedReference("uuid:8ff45356-acde-4a4c-86bf-f953611d20c6","catBagKeyName2","catBagKeyValue2"));

		PublisherAssertions object = new PublisherAssertions();
		object.setGeneric("2.0");
		object.setOperator("jUDDI.org");
		object.addPublisherAssertion(assertion);
		object.addPublisherAssertion(assertion);

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

		assertNotNull("Marshalled  PublisherAssertions ", marshalledString);

	}

	public void testUnMarshal()
	{

		Element child = getMarshalledElement(null);
		RegistryObject regObject = handler.unmarshal(child);

		assertNotNull("UnMarshalled  PublisherAssertions ", regObject);

	}

  public void testMarshUnMarshal()
  {
		Element child = getMarshalledElement(null);

		String marshalledString = this.getXMLString(child);

		assertNotNull("Marshalled  PublisherAssertions ", marshalledString);

		RegistryObject regObject = handler.unmarshal(child);

		child = getMarshalledElement(regObject);

		String unMarshalledString = this.getXMLString(child);

		assertNotNull("Unmarshalled  PublisherAssertions ", unMarshalledString);

		boolean equals = marshalledString.equals(unMarshalledString);

		assertEquals("Expected result: ", marshalledString, unMarshalledString );
  }

}
