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
package org.apache.juddi.client;

import static junit.framework.Assert.assertEquals;

import java.rmi.RemoteException;
import java.util.List;

import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.Registry;
import org.apache.juddi.api_v3.DeletePublisher;
import org.apache.juddi.api_v3.GetAllPublisherDetail;
import org.apache.juddi.api_v3.GetPublisherDetail;
import org.apache.juddi.api_v3.Publisher;
import org.apache.juddi.api_v3.PublisherDetail;
import org.apache.juddi.api_v3.SavePublisher;
import org.apache.juddi.config.Constants;
import org.apache.juddi.error.InvalidKeyPassedException;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.api_v3.client.config.ClientConfig;
import org.uddi.api_v3.client.config.Property;
import org.uddi.api_v3.client.transport.InVMTransport;
import org.uddi.api_v3.client.transport.Transport;
import org.uddi.api_v3.tck.EntityCreator;
import org.uddi.api_v3.tck.TckPublisher;
import org.uddi.api_v3.tck.TckSecurity;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class JUDDI_010_PublisherIntegrationTest {
	
	private static Logger logger                      = Logger.getLogger(JUDDI_010_PublisherIntegrationTest.class);
	
	private static UDDISecurityPortType security =null;
	private static JUDDIApiPortType publisher = null;
	private static String authInfo = null;
	
	@BeforeClass
	public static void startRegistry() throws ConfigurationException {
		String clazz = ClientConfig.getConfiguration().getString(Property.UDDI_PROXY_TRANSPORT,Property.DEFAULT_UDDI_PROXY_TRANSPORT);
		if (InVMTransport.class.getName().equals(clazz)) {
			Registry.start();
		}
		logger.debug("Getting auth tokens..");
		try {
	         Class<?> transportClass = Loader.loadClass(clazz);
	         if (transportClass!=null) {
	        	 Transport transport = (Transport) transportClass.newInstance();
	        	 security = transport.getUDDISecurityService();
	        	 UDDISecurityPortType securityService = transport.getUDDISecurityService();
	        	 GetAuthToken getAuthToken = new GetAuthToken();
	        	 getAuthToken.setUserID("root");
	        	 getAuthToken.setCred("");
	        	 authInfo = securityService.getAuthToken(getAuthToken).getAuthInfo();
	        	 publisher = transport.getJUDDIApiService();
	         } else {
	        	 Assert.fail();
	         }
	     } catch (Exception e) {
	    	 logger.error(e.getMessage(), e);
				Assert.fail("Could not obtain authInfo token.");
	     } 
	}
	
	@AfterClass
	public static void stopRegistry() throws ConfigurationException {
		String clazz = ClientConfig.getConfiguration().getString(Property.UDDI_PROXY_TRANSPORT,Property.DEFAULT_UDDI_PROXY_TRANSPORT);
		if (InVMTransport.class.getName().equals(clazz)) {
			Registry.stop();
		}
	}
	
     @Test
     public void testAuthToken() {
	     try {
	    	 String clazz = ClientConfig.getConfiguration().getString(Property.UDDI_PROXY_TRANSPORT,Property.DEFAULT_UDDI_PROXY_TRANSPORT);
	         Class<?> transportClass = Loader.loadClass(clazz);
	         if (transportClass!=null) {
	        	 Transport transport = (Transport) transportClass.newInstance();
	        	 
	        	 UDDISecurityPortType securityService = transport.getUDDISecurityService();
	        	 GetAuthToken getAuthToken = new GetAuthToken();
	        	 getAuthToken.setUserID("root");
	        	 getAuthToken.setCred("");
	        	 AuthToken authToken = securityService.getAuthToken(getAuthToken);
	        	 System.out.println(authToken.getAuthInfo());
	        	 Assert.assertNotNull(authToken);
	         } else {
	        	 Assert.fail();
	         }
	     } catch (Exception e) {
	         e.printStackTrace();
	         Assert.fail();
	     } 
     }
     
     @Test
 	public void testJoePublisher() {
 		//We can only test this if the publisher is not there already.
 		//If it already there is probably has foreign key relationships.
 		//This test should really only run on an empty database. Seed
 		//data will be added if the root publisher is missing.
 		if (!isExistPublisher(TckPublisher.JOE_PUBLISHER_ID)) {
 			saveJoePublisher();
 			deleteJoePublisher();
 		}
 	}
 	
 	@Test
 	public void testSamSyndicator() {
 		//We can only test this if the publisher is not there already.
 		if (!isExistPublisher(TckPublisher.SAM_SYNDICATOR_ID)) {
 			saveSamSyndicator();
 			deleteSamSyndicator();
 		}
 	}
 	
 	@Test
 	public void testGetAllPublishers(){
		GetAllPublisherDetail gp = new GetAllPublisherDetail();
		gp.setAuthInfo(authInfo);
		try {
			PublisherDetail publisherDetail = publisher.getAllPublisherDetail(gp);
			Assert.assertTrue(publisherDetail.getPublisher().size() > 1);
		} catch (Exception e) {
			Assert.fail();
		}
	}
 	
 	/**
 	 * Persists Joe Publisher to the database.
 	 * @return - true if the published did not exist already, 
 	 * 		   - false in all other cases.
 	 */
 	public boolean saveJoePublisher() {
 		if (!isExistPublisher(TckPublisher.JOE_PUBLISHER_ID)) {
 			savePublisher(TckPublisher.JOE_PUBLISHER_ID, TckPublisher.JOE_PUBLISHER_XML);
 			return true;
 		} else {
 			return false;
 		}
 	}
 	/**
 	 * Removes Joe Publisher from the database, this will fail if there
 	 * are child objects attached; think Services etc.
 	 */
 	public void deleteJoePublisher() {
 		deletePublisher(TckPublisher.JOE_PUBLISHER_ID);
 	}
 	/**
 	 * Persists Sam Syndicator to the database.
 	 * @return publisherId
 	 */
 	public String saveSamSyndicator() {
 		if (!isExistPublisher(TckPublisher.SAM_SYNDICATOR_ID)) {
 			savePublisher(TckPublisher.SAM_SYNDICATOR_ID, TckPublisher.SAM_SYNDICATOR_XML);
 		}
 		return TckPublisher.SAM_SYNDICATOR_ID;
 	}
 	/**
 	 * Removes Sam Syndicator from the database, this will fail if there
 	 * are child objects attached; think Services etc.
 	 */
 	public void deleteSamSyndicator() {
 		deletePublisher(TckPublisher.SAM_SYNDICATOR_ID);
 	}
 	
 	private void savePublisher(String publisherId, String publisherXML) {
		try {
			authInfo = TckSecurity.getAuthToken(security, Constants.ROOT_PUBLISHER, "");
			logger.debug("Saving new publisher: " + publisherXML);
			SavePublisher sp = new SavePublisher();
			sp.setAuthInfo(authInfo);
			Publisher pubIn = (Publisher)EntityCreator.buildFromDoc(publisherXML, "org.apache.juddi.api_v3");
			sp.getPublisher().add(pubIn);
			publisher.savePublisher(sp);
	
			// Now get the entity and check the values
			GetPublisherDetail gp = new GetPublisherDetail();
			gp.getPublisherId().add(publisherId);
			gp.setAuthInfo(authInfo);
			PublisherDetail pd = publisher.getPublisherDetail(gp);
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
			
			logger.debug("Querying for publisher: " + publisherXML);
			//Querying for this publisher to make sure it's really gone
			//We're expecting a invalid Key exception at this point.
			PublisherDetail pdBeforeDelete =null;
			try {
				pdBeforeDelete = publisher.getPublisherDetail(gp);
				Assert.assertNotNull(pdBeforeDelete);
			} catch (InvalidKeyPassedException e) {
				Assert.fail("We expected to find publisher " + publisherXML);
			}
			
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown");
		}
	}
	
	private void deletePublisher(String publisherId) {
		try {
			authInfo = TckSecurity.getAuthToken(security, Constants.ROOT_PUBLISHER, "");
			logger.debug("Delete publisher: " + publisherId);
			//Now deleting this publisher
			DeletePublisher dp = new DeletePublisher();
			dp.setAuthInfo(authInfo);
			dp.getPublisherId().add(publisherId);
			publisher.deletePublisher(dp);
			
			logger.info("Querying for publisher: " + publisherId + " after deletion.");
			//Querying for this publisher to make sure it's really gone
			//We're expecting a invalid Key exception at this point.
			GetPublisherDetail gp = new GetPublisherDetail();
			gp.getPublisherId().add(publisherId);
			gp.setAuthInfo(authInfo);
			PublisherDetail pdAfterDelete =null;
			try {
				pdAfterDelete = publisher.getPublisherDetail(gp);
				Assert.fail("We did not expect to find this publisher anymore.");
				//don't think we really want a SOAPFaulException be thrown here.
			} catch (SOAPFaultException e) {
				Assert.assertNull(pdAfterDelete);
			}
			
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown");
		}
	}
	
	private boolean isExistPublisher(String publisherId) {
		GetPublisherDetail gp = new GetPublisherDetail();
		gp.setAuthInfo(authInfo);
		gp.getPublisherId().add(publisherId);
		try {
			publisher.getPublisherDetail(gp);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	protected String authInfoJoe() throws RemoteException, DispositionReportFaultMessage {
		return TckSecurity.getAuthToken(security, TckPublisher.JOE_PUBLISHER_ID, TckPublisher.JOE_PUBLISHER_CRED);
	}
	
	protected String authInfoSam() throws RemoteException,  DispositionReportFaultMessage {
		return TckSecurity.getAuthToken(security, TckPublisher.SAM_SYNDICATOR_ID, TckPublisher.SAM_SYNDICATOR_CRED);
	}
	
}
