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

import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.publisher.Publisher;
import org.apache.juddi.datatype.request.AuthInfo;
import org.apache.juddi.datatype.request.SavePublisher;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author anou_mana@users.sourceforge.net
 */
public class  SavePublisherHandlerTests extends TestCase
{
	private static final String TEST_ID = "juddi.handler.DeleteSavePublisher.test";
	private  SavePublisherHandler handler = null;

  public  SavePublisherHandlerTests(String arg0)
  {
    super(arg0);
  }

  public static void main(String[] args)
  {
    junit.textui.TestRunner.run( SavePublisherHandlerTests.class);
  }

  public void setUp()
  {
		HandlerMaker maker = HandlerMaker.getInstance();
		handler = ( SavePublisherHandler)maker.lookup( SavePublisherHandler.TAG_NAME);
  }

	private RegistryObject getRegistryObject()
	{

		AuthInfo authInfo = new AuthInfo();
		authInfo.setValue("6f157513-844e-4a95-a856-d257e6ba9726");

		Publisher publisher = new Publisher();
		publisher.setPublisherID("bcrosby");
		publisher.setName("Bing Crosby");
		publisher.setLastName("Crosby");
		publisher.setFirstName("Bing");
		publisher.setWorkPhone("978.123-4567");
		publisher.setMobilePhone("617-765-9876");
		publisher.setPager("800-123-4655 ID: 501");
		publisher.setEmailAddress("bcrosby@juddi.org");
		publisher.setAdmin(true);

		SavePublisher object = new SavePublisher();
		object.setAuthInfo(authInfo);
		object.addPublisher(publisher);
		object.addPublisher(publisher);

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
		
		assertNotNull("Marshalled  SavePublisher ", marshalledString);

	}
	
	public void testUnMarshal()
	{
				
		Element child = getMarshalledElement(null);
		RegistryObject regObject = handler.unmarshal(child);
		
		assertNotNull("UnMarshalled  SavePublisher ", regObject);

	}
	
  public void testMarshUnMarshal()
  {
		Element child = getMarshalledElement(null);
		
		String marshalledString = this.getXMLString(child);
		
		assertNotNull("Marshalled  SavePublisher ", marshalledString);
		
		RegistryObject regObject = handler.unmarshal(child);
		
		child = getMarshalledElement(regObject);

		String unMarshalledString = this.getXMLString(child);
		
		assertNotNull("Unmarshalled  SavePublisher ", unMarshalledString);
		
		boolean equals = marshalledString.equals(unMarshalledString);

		assertEquals("Expected SavePublisher: ", marshalledString, unMarshalledString );
  }

}
