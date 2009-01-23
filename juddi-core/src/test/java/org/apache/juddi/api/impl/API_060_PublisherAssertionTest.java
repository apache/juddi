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
package org.apache.juddi.api.impl;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
import static junit.framework.Assert.assertEquals;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.AddPublisherAssertions;
import org.uddi.api_v3.DeletePublisherAssertions;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.PublisherAssertion;
import org.uddi.v3_service.DispositionReportFaultMessage;

public class API_060_PublisherAssertionTest {
	
	final static String JOE_ASSERT_XML    = "api_xml_data/joepublisher/publisherAssertion.xml";
	private static Logger logger = Logger.getLogger(API_060_PublisherAssertionTest.class);
    
	private UDDIPublicationImpl publish = new UDDIPublicationImpl();
	
	private static API_010_PublisherTest api010      = new API_010_PublisherTest();
	private static API_020_TmodelTest api020         = new API_020_TmodelTest();
	private static API_030_BusinessEntityTest api030 = new API_030_BusinessEntityTest();
	private static String authInfoJoe                = null;
	private static String authInfoSam                = null;
	
	@BeforeClass
	public static void setup() {
		logger.debug("Getting auth token..");
		try {
			api010.saveJoePublisher();
			api010.saveSamSyndicator();
			authInfoJoe = API_010_PublisherTest.authInfoJoe();
			authInfoSam = API_010_PublisherTest.authInfoSam();
		} catch (DispositionReportFaultMessage e) {
			logger.error(e.getMessage(), e);
			Assert.fail("Could not obtain authInfo token.");
		}
	}
	
	@Test
	public void testJoepublisherToSamSyndicator() {
		try {
			api020.saveJoePublisherTmodel(authInfoJoe);
			api020.saveSamSyndicatorTmodel(authInfoSam);
			api030.saveJoePublisherBusiness(authInfoJoe);
			api030.saveSamSyndicatorBusiness(authInfoSam);
			addPublisherAssertion(authInfoJoe, JOE_ASSERT_XML);
			deletePublisherAssertion(authInfoJoe, JOE_ASSERT_XML);
		} finally {
			api030.deleteJoePublisherBusiness(authInfoJoe);
			api030.deleteSamSyndicatorBusiness(authInfoSam);
			api020.deleteJoePublisherTmodel(authInfoJoe);
			api020.deleteSamSyndicatorTmodel(authInfoSam);
		}
	}
	
	private void addPublisherAssertion(String authInfo, String pubassertXML) {
		try {
			AddPublisherAssertions ap = new AddPublisherAssertions();
			ap.setAuthInfo(authInfo);

			PublisherAssertion paIn = (PublisherAssertion)UDDIApiTestHelper.buildEntityFromDoc(pubassertXML, "org.uddi.api_v3");
			ap.getPublisherAssertion().add(paIn);
			publish.addPublisherAssertions(ap);
	
			// Now get the entity and check the values
			List<PublisherAssertion> paOutList = publish.getPublisherAssertions(authInfo);
			PublisherAssertion paOut = paOutList.get(0);

			assertEquals(paIn.getFromKey(), paOut.getFromKey());
			assertEquals(paIn.getToKey(), paOut.getToKey());
			
			KeyedReference keyRefIn = paIn.getKeyedReference();
			KeyedReference keyRefOut = paOut.getKeyedReference();
			
			assertEquals(keyRefIn.getTModelKey(), keyRefOut.getTModelKey());
			assertEquals(keyRefIn.getKeyName(), keyRefOut.getKeyName());
			assertEquals(keyRefIn.getKeyValue(), keyRefOut.getKeyValue());
			
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown");
		}

	}

	private void deletePublisherAssertion(String authInfo, String pubassertXML) {
		try {
			// Delete the entity and make sure it is removed
			DeletePublisherAssertions dp = new DeletePublisherAssertions();
			dp.setAuthInfo(authInfo);
			
			PublisherAssertion paIn = (PublisherAssertion)UDDIApiTestHelper.buildEntityFromDoc(pubassertXML, "org.uddi.api_v3");
			dp.getPublisherAssertion().add(paIn);
			
			publish.deletePublisherAssertions(dp);
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown");
		}
		
	}

}
