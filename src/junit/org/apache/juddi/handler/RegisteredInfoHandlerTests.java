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

import org.apache.juddi.datatype.Description;
import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.response.BusinessInfo;
import org.apache.juddi.datatype.response.BusinessInfos;
import org.apache.juddi.datatype.response.RegisteredInfo;
import org.apache.juddi.datatype.response.ServiceInfo;
import org.apache.juddi.datatype.response.TModelInfo;
import org.apache.juddi.datatype.response.TModelInfos;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author anou_mana@apache.org
 */
public class  RegisteredInfoHandlerTests extends TestCase
{
	private static final String TEST_ID = "juddi.handler.DeleteRegisteredInfo.test";
	private  RegisteredInfoHandler handler = null;

  public  RegisteredInfoHandlerTests(String arg0)
  {
    super(arg0);
  }

  public static void main(String[] args)
  {
    junit.textui.TestRunner.run( RegisteredInfoHandlerTests.class);
  }

  public void setUp()
  {
		HandlerMaker maker = HandlerMaker.getInstance();
		handler = ( RegisteredInfoHandler)maker.lookup( RegisteredInfoHandler.TAG_NAME);
  }

	private RegistryObject getRegistryObject()
	{
		ServiceInfo sInfo = new ServiceInfo();
		sInfo.addName(new Name("regInfoServNm"));
		sInfo.addName(new Name("regInfoServNm2","en"));
		sInfo.setServiceKey("826e6443-e3c5-442b-9bf8-943071ca83f4");
		sInfo.setBusinessKey("56644a68-8779-46c7-93ce-90eeb7569f3f");

		BusinessInfo bInfo = new BusinessInfo();
		bInfo.setBusinessKey("56644a68-8779-46c7-93ce-90eeb7569f3f");
		bInfo.addName(new Name("regInfoBizNm"));
		bInfo.addName(new Name("regInfoBizNm2","en"));
		bInfo.addDescription(new Description("regInfoBiz whatever"));
		bInfo.addDescription(new Description("regInfoBiz whatever too","fr"));
		bInfo.addServiceInfo(sInfo);

		BusinessInfos bInfos = new BusinessInfos();
		bInfos.addBusinessInfo(bInfo);
		bInfos.addBusinessInfo(bInfo);

		TModelInfo tInfo = new TModelInfo();
		tInfo.setTModelKey("uuid:e86bd2a9-03f6-44c4-b619-400ef2cd7e47");
		tInfo.setName(new Name("RegInfoTestDriver"));

		TModelInfos tInfos = new TModelInfos();
		tInfos.addTModelInfo(tInfo);
		tInfos.addTModelInfo(tInfo);

		RegisteredInfo object = new RegisteredInfo();
		object.setGeneric("2.0");
		object.setOperator("jUDDI.org");
		object.setBusinessInfos(bInfos);
		object.setTModelInfos(tInfos);


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

		assertNotNull("Marshalled  RegisteredInfo ", marshalledString);

	}

	public void testUnMarshal()
	{

		Element child = getMarshalledElement(null);
		RegistryObject regObject = handler.unmarshal(child);

		assertNotNull("UnMarshalled  RegisteredInfo ", regObject);

	}

  public void testMarshUnMarshal()
  {
		Element child = getMarshalledElement(null);

		String marshalledString = this.getXMLString(child);

		assertNotNull("Marshalled  RegisteredInfo ", marshalledString);

		RegistryObject regObject = handler.unmarshal(child);

		child = getMarshalledElement(regObject);

		String unMarshalledString = this.getXMLString(child);

		assertNotNull("Unmarshalled  RegisteredInfo ", unMarshalledString);

		boolean equals = marshalledString.equals(unMarshalledString);

		assertEquals("Expected result: ", marshalledString, unMarshalledString );
  }

}
