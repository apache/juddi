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

import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.assertion.PublisherAssertion;
import org.apache.juddi.datatype.request.AuthInfo;
import org.apache.juddi.datatype.request.DeletePublisherAssertions;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author anou_mana@apache.org
 */
public class DeletePublisherAssertionsHandlerTests extends TestCase
{
	private static final String TEST_ID = "juddi.handler.DeletePublisherAssertion.test";
	private DeletePublisherAssertionsHandler handler = null;

  public DeletePublisherAssertionsHandlerTests(String arg0)
  {
    super(arg0);
  }

  public static void main(String[] args)
  {
    junit.textui.TestRunner.run(DeletePublisherAssertionsHandlerTests.class);
  }

  public void setUp()
  {
		HandlerMaker maker = HandlerMaker.getInstance();
		handler = (DeletePublisherAssertionsHandler)maker.lookup(DeletePublisherAssertionsHandler.TAG_NAME);
  }

	private RegistryObject getRegistryObject()
	{

		AuthInfo authInfo = new AuthInfo();
		authInfo.setValue("6f157513-844e-4a95-a856-d257e6ba9726");

		PublisherAssertion assertion1 = new PublisherAssertion();
		assertion1.setFromKey("6f157513-844e-4a95-a856-d257e6ba0000");
		assertion1.setToKey("6f157513-844e-4a95-a856-d257e6ba1111");

		PublisherAssertion assertion2 = new PublisherAssertion();
		assertion2.setFromKey("6f157513-844e-4a95-a856-d257e6ba2222");
		assertion2.setToKey("6f157513-844e-4a95-a856-d257e6ba3333");

		DeletePublisherAssertions object = new DeletePublisherAssertions();
		object.setAuthInfo(authInfo);
		object.addPublisherAssertion(assertion1);
		object.addPublisherAssertion(assertion2);

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

		assertNotNull("Marshalled DeletePublisherAssertions ", marshalledString);

	}

	public void testUnMarshal()
	{

		Element child = getMarshalledElement(null);
		RegistryObject regObject = handler.unmarshal(child);

		assertNotNull("UnMarshalled DeletePublisherAssertions ", regObject);

	}

  public void testMarshUnMarshal()
  {
		Element child = getMarshalledElement(null);

		String marshalledString = this.getXMLString(child);

		assertNotNull("Marshalled DeletePublisherAssertions ", marshalledString);

		RegistryObject regObject = handler.unmarshal(child);

		child = getMarshalledElement(regObject);

		String unMarshalledString = this.getXMLString(child);

		assertNotNull("Unmarshalled DeletePublisherAssertions ", unMarshalledString);

		boolean equals = marshalledString.equals(unMarshalledString);

		assertEquals("Expected result: ", marshalledString, unMarshalledString );
  }

}
