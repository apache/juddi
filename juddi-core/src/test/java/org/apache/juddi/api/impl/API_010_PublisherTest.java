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

import static junit.framework.Assert.assertEquals;

import java.util.List;

import org.apache.juddi.api.datatype.DeletePublisher;
import org.apache.juddi.api.datatype.GetPublisherDetail;
import org.apache.juddi.api.datatype.Publisher;
import org.apache.juddi.api.datatype.PublisherDetail;
import org.apache.juddi.api.datatype.SavePublisher;
import org.apache.juddi.config.Constants;
import org.apache.juddi.error.InvalidKeyPassedException;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class API_010_PublisherTest {
	
	final static String JOE_PUBLISHER_XML  = "api_xml_data/joepublisher/publisher.xml";
    final static String JOE_PUBLISHER_ID   = "joepublisher";
	final static String SAM_SYNDICATOR_XML = "api_xml_data/samsyndicator/publisher.xml";
    final static String SAM_SYNDICATOR_ID  = "ssyndicator";
    
    private Logger logger = Logger.getLogger(this.getClass());
    
	private UDDIPublicationImpl publish = new UDDIPublicationImpl();
	private UDDIInquiryImpl inquiry = new UDDIInquiryImpl();
	
	@Test
	public void joePublisher() {
		//We can only test this if the publisher is not there already.
		if (!isExistPublisher(JOE_PUBLISHER_ID)) {
			savePublisher(JOE_PUBLISHER_ID, JOE_PUBLISHER_XML);
			deletePublisher(JOE_PUBLISHER_ID);
		}
	}
	
	@Test
	public void samSyndicator() {
		//We can only test this if the publisher is not there already.
		if (!isExistPublisher(SAM_SYNDICATOR_ID)) {
			savePublisher(SAM_SYNDICATOR_ID, SAM_SYNDICATOR_XML);
			deletePublisher(SAM_SYNDICATOR_ID);
		}
	}
	
	
	public void savePublisher(String publisherId, String publisherXML) {
		try {
			logger.info("Getting auth token..");
			String authInfo = UDDIApiTestHelper.getAuthToken(Constants.ROOT_PUBLISHER);
			
			logger.debug("Saving new publisher: " + publisherXML);
			SavePublisher sp = new SavePublisher();
			sp.setAuthInfo(authInfo);
			Publisher pubIn = (Publisher)UDDIApiTestHelper.buildEntityFromDoc(publisherXML, "org.apache.juddi.api.datatype");
			sp.getPublisher().add(pubIn);
			publish.savePublisher(sp);
	
			// Now get the entity and check the values
			GetPublisherDetail gp = new GetPublisherDetail();
			gp.getPublisherId().add(publisherId);
			PublisherDetail pd = inquiry.getPublisherDetail(gp);
			List<Publisher> pubOutList = pd.getPublisher();
			Publisher pubOut = pubOutList.get(0);

			assertEquals(pubIn.getAuthorizedName(), pubOut.getAuthorizedName());
			assertEquals(pubIn.getPublisherName(), pubOut.getPublisherName());
			assertEquals(pubIn.getEmailAddress(), pubOut.getEmailAddress());
			assertEquals(pubIn.getIsAdmin(), pubOut.getIsAdmin());
			assertEquals(pubIn.getIsEnabled(), pubOut.getIsEnabled());
			assertEquals(pubIn.getMaxBindingsPerService(), pubOut.getMaxBindingsPerService());
			assertEquals(pubIn.getMaxBusinesses(), pubOut.getMaxBusinesses());
			assertEquals(pubIn.getMaxServicePerBusiness(), pubOut.getMaxServicePerBusiness());
			assertEquals(pubIn.getMaxTModels(), pubOut.getMaxTModels());
			
			logger.info("Querying for publisher: " + publisherXML);
			//Querying for this publisher to make sure it's really gone
			//We're expecting a invalid Key exception at this point.
			PublisherDetail pdBeforeDelete =null;
			try {
				pdBeforeDelete = inquiry.getPublisherDetail(gp);
				Assert.assertNotNull(pdBeforeDelete);
			} catch (InvalidKeyPassedException e) {
				Assert.fail("We expected to find publisher " + publisherXML);
			}
			
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown");
		}
	}
	
	public void deletePublisher(String publisherId) {
		try {
			logger.info("Getting auth token..");
			String authInfo = UDDIApiTestHelper.getAuthToken(Constants.ROOT_PUBLISHER);
			
			logger.info("Delete publisher: " + publisherId);
			//Now deleting this publisher
			DeletePublisher dp = new DeletePublisher();
			dp.setAuthInfo(authInfo);
			dp.getPublisherId().add(publisherId);
			publish.deletePublisher(dp);
			
			logger.info("Querying for publisher: " + publisherId + " after deletion.");
			//Querying for this publisher to make sure it's really gone
			//We're expecting a invalid Key exception at this point.
			GetPublisherDetail gp = new GetPublisherDetail();
			gp.getPublisherId().add(publisherId);
			PublisherDetail pdAfterDelete =null;
			try {
				pdAfterDelete = inquiry.getPublisherDetail(gp);
				Assert.fail("We did not expect to find this publisher anymore.");
			} catch (InvalidKeyPassedException e) {
				Assert.assertNull(pdAfterDelete);
			}
			
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown");
		}
	}
	
	public boolean isExistPublisher(String publisherId) {
		GetPublisherDetail gp = new GetPublisherDetail();
		gp.getPublisherId().add(publisherId);
		try {
			inquiry.getPublisherDetail(gp);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
}
