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
import org.apache.juddi.datatype.response.AssertionStatusItem;
import org.apache.juddi.datatype.response.CompletionStatus;
import org.apache.juddi.datatype.response.KeysOwned;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author anou_mana@apache.org
 */
public class AssertionStatusItemHandlerTests extends TestCase
{
	private static final String TEST_ID = "juddi.handler.AssertionStatusItem.test";
	private AssertionStatusItemHandler handler = null;

  public AssertionStatusItemHandlerTests(String arg0)
  {
    super(arg0);
  }

  public static void main(String[] args)
  {
    junit.textui.TestRunner.run(AssertionStatusItemHandlerTests.class);
  }

  public void setUp()
  {
		HandlerMaker maker = HandlerMaker.getInstance();
		handler = (AssertionStatusItemHandler)maker.lookup(AssertionStatusItemHandler.TAG_NAME);
  }

	private RegistryObject getRegistryObject()
	{

		AssertionStatusItem object = new AssertionStatusItem();
		KeysOwned keysOwned = new KeysOwned();
		keysOwned.setToKey("dfddb58c-4853-4a71-865c-f84509017cc7");
		keysOwned.setFromKey("fe8af00d-3a2c-4e05-b7ca-95a1259aad4f");

		object.setCompletionStatus(new CompletionStatus(CompletionStatus.COMPLETE));
		object.setFromKey("986d9a16-5d4d-46cf-9fac-6bb80a7091f6");
		object.setToKey("dd45a24c-74fc-4e82-80c2-f99cdc76d681");
		object.setKeyedReference(new KeyedReference("uuid:8ff45356-acde-4a4c-86bf-f953611d20c6","Subsidiary","1"));
		object.setKeysOwned(keysOwned);

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

		assertNotNull("Marshalled AssertionStatusItem ", marshalledString);

	}

	public void testUnMarshal()
	{

		Element child = getMarshalledElement(null);
		RegistryObject regObject = handler.unmarshal(child);

		assertNotNull("UnMarshalled AssertionStatusItem ", regObject);

	}

  public void testMarshUnMarshal()
  {
		Element child = getMarshalledElement(null);

		String marshalledString = this.getXMLString(child);

		assertNotNull("Marshalled AssertionStatusItem ", marshalledString);

		RegistryObject regObject = handler.unmarshal(child);

		child = getMarshalledElement(regObject);

		String unMarshalledString = this.getXMLString(child);

		assertNotNull("Unmarshalled AssertionStatusItem ", unMarshalledString);

		boolean equals = marshalledString.equals(unMarshalledString);

		assertEquals("Expected result: ", marshalledString, unMarshalledString );
  }

}
