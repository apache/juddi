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
package org.apache.juddi.api_v3;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.JAXB;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import static junit.framework.Assert.fail;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.Contact;
import org.uddi.api_v3.ObjectFactory;
import org.uddi.api_v3.PersonName;
import org.uddi.repl_v3.CommunicationGraph;
import org.uddi.repl_v3.Operator;
import org.uddi.repl_v3.OperatorStatusType;
import org.uddi.repl_v3.ReplicationConfiguration;

/**
 * Testing marshalling functionality, making sure UTF-8 is handled correctly.
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class GetPublisherDetailTest {

	private final static String EXPECTED_XML_FRAGMENT1 = "<fragment xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:ns3=\"urn:uddi-org:api_v3\">\n"
                                                       +"    <ns3:authInfo>AuthInfo String</ns3:authInfo>\n"
                                                       +"</fragment>";
	private final static String EXPECTED_XML_FRAGMENT2 = "<fragment xmlns:ns3=\"urn:uddi-org:api_v3\" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\">\n"
        +"    <ns3:authInfo>AuthInfo String</ns3:authInfo>\n"
        +"</fragment>";
	private final static String UTF8_WORD = "メインページ";

	private final static String EXPECTED_UTF8_XML_FRAGMENT1 = "<fragment xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:ns3=\"urn:uddi-org:api_v3\">\n"
        +"    <ns3:authInfo>" + UTF8_WORD + "</ns3:authInfo>\n"
        +"</fragment>";
	private final static String EXPECTED_UTF8_XML_FRAGMENT2 = "<fragment xmlns:ns3=\"urn:uddi-org:api_v3\" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\">\n"
        +"    <ns3:authInfo>" + UTF8_WORD + "</ns3:authInfo>\n"
        +"</fragment>";
	/**
	 * Testing going from object to XML using JAXB using a XML Fragment.
	 */
	@Test 
	public void marshall()
	{
		try {
			JAXBContext jaxbContext=JAXBContext.newInstance("org.apache.juddi.api_v3");
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			ObjectFactory factory = new ObjectFactory();
			GetPublisherDetail getPublisherDetail = new GetPublisherDetail();
			getPublisherDetail.authInfo = "some token";
			
			StringWriter writer = new StringWriter();
			JAXBElement<GetPublisherDetail> element = new JAXBElement<GetPublisherDetail>(new QName("","fragment"),GetPublisherDetail.class,getPublisherDetail);
			marshaller.marshal(element,writer);
			String actualXml=writer.toString();
			System.out.println(actualXml);
			
			
		} catch (JAXBException jaxbe) {
			jaxbe.printStackTrace();
			
			fail("No exception should be thrown");
		}
	}
        
        @Test
        public void marshallReplicationMessage() throws Exception{
                ReplicationConfiguration r = new ReplicationConfiguration();
                r.setCommunicationGraph(new CommunicationGraph());
                        Operator op = new Operator();
                        op.setOperatorNodeID("a node");
                        op.setSoapReplicationURL("http://localhost/services/replication");
                        
                        op.getContact().add(new Contact());
                        op.getContact().get(0).getPersonName().add(new PersonName("Unknown", null));
                        op.setOperatorStatus(OperatorStatusType.NORMAL);
                        
                        r.getOperator().add(op);
                        r.getCommunicationGraph().getNode().add("a node");
                        r.getCommunicationGraph().getControlledMessage().add("*");
                        r.setSerialNumber(0);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddkkmmZ");
                        r.setTimeOfConfigurationUpdate(sdf.format(new Date()));
                        r.setRegistryContact(new org.uddi.repl_v3.ReplicationConfiguration.RegistryContact());
                        r.getRegistryContact().setContact(new Contact());
                        r.getRegistryContact().getContact().getPersonName().add(new PersonName("Unknown", null));
                                JAXB.marshal(r, System.out);
        }
	
}
