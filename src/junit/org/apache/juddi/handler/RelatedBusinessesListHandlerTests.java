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

import org.apache.juddi.datatype.BusinessKey;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.response.RelatedBusinessInfo;
import org.apache.juddi.datatype.response.RelatedBusinessesList;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author anou_mana@apache.org
 */
public class  RelatedBusinessesListHandlerTests extends TestCase
{
	private static final String TEST_ID = "juddi.handler.DeleteRelatedBusinessesList.test";
	private  RelatedBusinessesListHandler handler = null;

  public  RelatedBusinessesListHandlerTests(String arg0)
  {
    super(arg0);
  }

  public static void main(String[] args)
  {
    junit.textui.TestRunner.run( RelatedBusinessesListHandlerTests.class);
  }

  public void setUp()
  {
		HandlerMaker maker = HandlerMaker.getInstance();
		handler = ( RelatedBusinessesListHandler)maker.lookup( RelatedBusinessesListHandler.TAG_NAME);
  }

	private RegistryObject getRegistryObject()
	{
		RelatedBusinessesList object = new RelatedBusinessesList();
		object.setGeneric("2.0");
		object.setOperator("jUDDI.org");
		object.setTruncated(true);
		object.setBusinessKey(new BusinessKey("f9f0c35f-06ab-4bec-9c7d-b1469e73f0eb"));
		object.addRelatedBusinessInfo(new RelatedBusinessInfo("abc"));
		object.addRelatedBusinessInfo(new RelatedBusinessInfo("xyz"));


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

		assertNotNull("Marshalled  RelatedBusinessesList ", marshalledString);

	}

	public void testUnMarshal()
	{

		Element child = getMarshalledElement(null);
		RegistryObject regObject = handler.unmarshal(child);

		assertNotNull("UnMarshalled  RelatedBusinessesList ", regObject);

	}

  public void testMarshUnMarshal()
  {
		Element child = getMarshalledElement(null);

		String marshalledString = this.getXMLString(child);

		assertNotNull("Marshalled  RelatedBusinessesList ", marshalledString);

		RegistryObject regObject = handler.unmarshal(child);

		child = getMarshalledElement(regObject);

		String unMarshalledString = this.getXMLString(child);

		assertNotNull("Unmarshalled  RelatedBusinessesList ", unMarshalledString);

		boolean equals = marshalledString.equals(unMarshalledString);

		assertEquals("Expected result: ", marshalledString, unMarshalledString );
  }

}
