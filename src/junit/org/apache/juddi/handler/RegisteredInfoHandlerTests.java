/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "jUDDI" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
 * @author anou_mana@users.sourceforge.net
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
