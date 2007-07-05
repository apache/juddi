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

import org.apache.juddi.datatype.CategoryBag;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.TModelBag;
import org.apache.juddi.datatype.TModelKey;
import org.apache.juddi.datatype.request.FindQualifier;
import org.apache.juddi.datatype.request.FindService;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author anou_mana@apache.org
 */
public class  FindServiceHandlerTests extends HandlerTestCase
{
	private static final String TEST_ID = "juddi.handler.DeletePublisher.test";
	private  FindServiceHandler handler = null;

  public  FindServiceHandlerTests(String arg0)
  {
    super(arg0);
  }

  public static void main(String[] args)
  {
    junit.textui.TestRunner.run( FindServiceHandlerTests.class);
  }

  public void setUp()
  {
		HandlerMaker maker = HandlerMaker.getInstance();
		handler = ( FindServiceHandler)maker.lookup( FindServiceHandler.TAG_NAME);
  }

	private RegistryObject getRegistryObject()
	{
		FindService object = new FindService();

		CategoryBag catBag = new CategoryBag();
		catBag.addKeyedReference(new KeyedReference("catBagKeyName","catBagKeyValue"));
		catBag.addKeyedReference(new KeyedReference("uuid:8ff45356-acde-4a4c-86bf-f953611d20c6","catBagKeyName2","catBagKeyValue2"));

		TModelBag tModBag = new TModelBag();
		tModBag.addTModelKey("uuid:35d9793b-9911-4b85-9f0e-5d4c65b4f253");
		tModBag.addTModelKey(new TModelKey("uuid:c5ab113f-0d6b-4247-b3c4-8c012726acd8"));

		object.addName(new Name("serviceNm"));
		object.addName(new Name("serviceNm2","en"));
		object.addFindQualifier(new FindQualifier(FindQualifier.SORT_BY_DATE_ASC));
		object.addFindQualifier(new FindQualifier(FindQualifier.AND_ALL_KEYS));
		object.setMaxRows(50);
		object.setBusinessKey("fd36dbce-bc3e-468b-8346-5374975a0843");
		object.setTModelBag(tModBag);
		object.setCategoryBag(catBag);

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

		assertNotNull("Marshalled  FindService ", marshalledString);

	}

	public void testUnMarshal()
	{

		Element child = getMarshalledElement(null);
		RegistryObject regObject = handler.unmarshal(child);

		assertNotNull("UnMarshalled  FindService ", regObject);

	}

  public void testMarshUnMarshal()
  {
		Element child = getMarshalledElement(null);

		String marshalledString = this.getXMLString(child);

		assertNotNull("Marshalled  FindService ", marshalledString);

		RegistryObject regObject = handler.unmarshal(child);

		child = getMarshalledElement(regObject);

		String unMarshalledString = this.getXMLString(child);

		assertNotNull("Unmarshalled  FindService ", unMarshalledString);

		boolean equals = marshalledString.equals(unMarshalledString);

		assertEquals("Expected result: ", marshalledString, unMarshalledString );
  }

}
