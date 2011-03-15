/*
 * Copyright 2001-2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.juddi;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.juddi.InitialContextInfo;
import org.apache.juddi.Property;
import org.apache.juddi.jaxb.JAXBMarshaller;
import org.junit.Test;
import org.uddi.api_v3.InstanceDetails;


/**
 * Testing marshalling functionality, making sure UTF-8 is handled correctly.
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class InitialContextInfoTest {
	
	@Test
	public void initialContextInfo_Java2XML()
	{
		try {
			List<Property> properties= new ArrayList<Property>();
			Property property1 = new Property(Context.INITIAL_CONTEXT_FACTORY,"value1");
			properties.add(property1);
			Property property2 = new Property(Context.PROVIDER_URL,"value2");
			properties.add(property2);
			InitialContextInfo contextInfo = new InitialContextInfo();
			contextInfo.setProperty(properties);
			JAXBContext jc = JAXBContext.newInstance(InitialContextInfo.class);
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        m.marshal(contextInfo, System.out);
		} catch (JAXBException e) {
			e.printStackTrace();
			fail("No exception should be thrown");
		}
	}
	
	@Test
	public void initialContextInfo_XML2JAVA()
	{
		try {
			String INITIAL_CONTEXT_XML = "<initialContextInfo>" +
								"<contextProperty value=\"value1\" name=\"name1\"/>" +
								"<contextProperty value=\"value2\" name=\"name2\"/>" +
							 "</initialContextInfo>";
			
			StringReader reader = new StringReader(INITIAL_CONTEXT_XML);
			JAXBContext jc = JAXBContext.newInstance(InitialContextInfo.class);
			Unmarshaller um = jc.createUnmarshaller();
			InitialContextInfo icInfo = (InitialContextInfo)  um.unmarshal(new StreamSource(reader), InitialContextInfo.class).getValue();
			assertEquals("name1", icInfo.getContextProperty().get(0).getName());
			assertEquals("name2", icInfo.getContextProperty().get(1).getName());
			assertEquals("value2", icInfo.getContextProperty().get(1).getValue());
		} catch (JAXBException jaxbe) {
			jaxbe.printStackTrace();
			fail("No exception should be thrown");
		}
	}
	
	@Test
	public void bindingTemplate_XML2JAVA()
	{
		try {
			String instanceDetailsStr = 
				    
		            "<instanceDetails xmlns=\"urn:uddi-org:api_v3\">" +
				      "<instanceParms><![CDATA[ " +
                        "<initialContextInfo>" +
                          "<contextProperty value=\"value1\" name=\"name1\"/>" +
                          "<contextProperty value=\"value2\" name=\"name2\"/>" +
                        "</initialContextInfo> ]]>" +
                      "</instanceParms>" +
                    "</instanceDetails>";
	
			InstanceDetails details = (InstanceDetails) JAXBMarshaller.unmarshallFromString(instanceDetailsStr,JAXBMarshaller.PACKAGE_UDDIAPI);
			String instanceParmsStr = details.getInstanceParms();
			if (instanceParmsStr!=null) {
				InitialContextInfo icInfo = (InitialContextInfo) JAXBMarshaller.unmarshallFromString(instanceParmsStr, JAXBMarshaller.PACKAGE_JUDDI);
				assertEquals("name1" , icInfo.getContextProperty().get(0).getName());
				assertEquals("name2" , icInfo.getContextProperty().get(1).getName());
				assertEquals("value2", icInfo.getContextProperty().get(1).getValue());
			} else {
				fail("We should have data in instanceParmsStr.");
			}
		} catch (JAXBException jaxbe) {
			jaxbe.printStackTrace();
			fail("No exception should be thrown");
		}
	}
	
}
