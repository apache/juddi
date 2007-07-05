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
import org.apache.juddi.datatype.Description;
import org.apache.juddi.datatype.IdentifierBag;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.OverviewDoc;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.tmodel.TModel;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author anou_mana@apache.org
 */
public class  TModelHandlerTests extends HandlerTestCase
{
	private static final String TEST_ID = "juddi.handler.DeleteTModel.test";
	private  TModelHandler handler = null;

  public  TModelHandlerTests(String arg0)
  {
    super(arg0);
  }

  public static void main(String[] args)
  {
    junit.textui.TestRunner.run( TModelHandlerTests.class);
  }

  public void setUp()
  {
		HandlerMaker maker = HandlerMaker.getInstance();
		handler = ( TModelHandler)maker.lookup( TModelHandler.TAG_NAME);
  }

	private RegistryObject getRegistryObject()
	{

		OverviewDoc overDoc = new OverviewDoc();
		overDoc.setOverviewURL("http://www.juddi.org/service.html");
		overDoc.addDescription(new Description("over-doc Descr"));
		overDoc.addDescription(new Description("over-doc Descr Two","en"));

		CategoryBag catBag = new CategoryBag();
		catBag.addKeyedReference(new KeyedReference("catBagKeyName","catBagKeyValue"));
		catBag.addKeyedReference(new KeyedReference("uuid:dfddb58c-4853-4a71-865c-f84509017cc7","catBagKeyName2","catBagKeyValue2"));

		IdentifierBag idBag = new IdentifierBag();
		idBag.addKeyedReference(new KeyedReference("idBagKeyName","idBagkeyValue"));
		idBag.addKeyedReference(new KeyedReference("uuid:f78a135a-4769-4e79-8604-54d440314bc0","idBagKeyName2","idBagkeyValue2"));

		TModel object = new TModel();
		object.setTModelKey("uuid:269855db-62eb-4862-8e5a-1b06f2753038");
		object.setOperator("jUDDI");
		object.setAuthorizedName("Guest");
		object.setName("jUDDI TModel");
		object.addDescription(new Description("tModel whatever"));
		object.addDescription(new Description("tModel whatever too","fr"));
		object.setCategoryBag(catBag);
		object.setIdentifierBag(idBag);
		object.setOverviewDoc(overDoc);

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

		assertNotNull("Marshalled  TModel ", marshalledString);

	}

	public void testUnMarshal()
	{

		Element child = getMarshalledElement(null);
		RegistryObject regObject = handler.unmarshal(child);

		assertNotNull("UnMarshalled  TModel ", regObject);

	}

  public void testMarshUnMarshal()
  {
		Element child = getMarshalledElement(null);

		String marshalledString = this.getXMLString(child);

		assertNotNull("Marshalled  TModel ", marshalledString);

		RegistryObject regObject = handler.unmarshal(child);

		child = getMarshalledElement(regObject);

		String unMarshalledString = this.getXMLString(child);

		assertNotNull("Unmarshalled  TModel ", unMarshalledString);

		boolean equals = marshalledString.equals(unMarshalledString);

		assertEquals("Expected TModel: ", marshalledString, unMarshalledString );
  }

}
