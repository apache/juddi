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
import org.junit.Test;
import org.uddi.api_v3.AddPublisherAssertions;
import org.uddi.api_v3.DeletePublisherAssertions;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.PublisherAssertion;

public class API_050_PublisherAssertionTest {
	
	final static String JOE_ASSERT_XML    = "api_xml_data/joepublisher/publisherAssertion.xml";
	private Logger logger = Logger.getLogger(this.getClass());
    
	private UDDIPublicationImpl publish = new UDDIPublicationImpl();
	
	@Test
	public void joepublisherToSamSyndicator() {
		String publisherId = API_010_PublisherTest.JOE_PUBLISHER_ID;
		String publisherId2 = API_010_PublisherTest.SAM_SYNDICATOR_ID;
		try {
			API_010_PublisherTest api010 = new API_010_PublisherTest();
			//joepublisher
			if (!api010.isExistPublisher(publisherId)) {
				//Add the Publisher
				api010.savePublisher(publisherId, API_010_PublisherTest.JOE_PUBLISHER_XML);
			}
			new API_020_TmodelTest().saveTModel(publisherId, API_020_TmodelTest.JOE_PUBLISHER_TMODEL_XML, API_020_TmodelTest.JOE_PUBLISHER_TMODEL_KEY);
			new API_030_BusinessEntityTest().saveBusiness(publisherId, API_030_BusinessEntityTest.JOE_BUSINESS_XML, API_030_BusinessEntityTest.JOE_BUSINESS_KEY);
			//samsyndicator
			if (!api010.isExistPublisher(publisherId2)) {
				//Add the Publisher
				api010.savePublisher(publisherId2, API_010_PublisherTest.SAM_SYNDICATOR_XML);
			}
			new API_020_TmodelTest().saveTModel(publisherId2, API_020_TmodelTest.SAM_SYNDICATOR_TMODEL_XML, API_020_TmodelTest.SAM_SYNDICATOR_TMODEL_KEY);
			new API_030_BusinessEntityTest().saveBusiness(publisherId2, API_030_BusinessEntityTest.SAM_BUSINESS_XML, API_030_BusinessEntityTest.SAM_BUSINESS_KEY);
			addPublisherAssertion(publisherId, JOE_ASSERT_XML);
			deletePublisherAssertion(publisherId, JOE_ASSERT_XML);
		} finally {
			new API_030_BusinessEntityTest().deleteBusiness(publisherId, API_030_BusinessEntityTest.JOE_BUSINESS_XML, API_030_BusinessEntityTest.JOE_BUSINESS_KEY);
			new API_020_TmodelTest().deleteTModel(publisherId, API_020_TmodelTest.JOE_PUBLISHER_TMODEL_XML, API_020_TmodelTest.JOE_PUBLISHER_TMODEL_KEY);
			new API_030_BusinessEntityTest().deleteBusiness(publisherId2, API_030_BusinessEntityTest.SAM_BUSINESS_XML, API_030_BusinessEntityTest.SAM_BUSINESS_KEY);
			new API_020_TmodelTest().deleteTModel(publisherId2, API_020_TmodelTest.SAM_SYNDICATOR_TMODEL_XML, API_020_TmodelTest.SAM_SYNDICATOR_TMODEL_KEY);
		}
	}
	
	public void addPublisherAssertion(String publisherId, String pubassertXML) {
		try {
			String authInfo = UDDIApiTestHelper.getAuthToken(publisherId);
			
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

	public void deletePublisherAssertion(String publisherId, String pubassertXML) {
		try {
			String authInfo = UDDIApiTestHelper.getAuthToken(publisherId);
			
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
