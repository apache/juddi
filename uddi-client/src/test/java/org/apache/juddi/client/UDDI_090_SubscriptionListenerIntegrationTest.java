package org.apache.juddi.client;

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
import java.io.BufferedReader;
import java.io.FileReader;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.Registry;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.client.config.ClientConfig;
import org.uddi.api_v3.client.config.Property;
import org.uddi.api_v3.client.transport.InVMTransport;
import org.uddi.api_v3.client.transport.Transport;
import org.uddi.api_v3.tck.TckBusiness;
import org.uddi.api_v3.tck.TckBusinessService;
import org.uddi.api_v3.tck.TckPublisher;
import org.uddi.api_v3.tck.TckSecurity;
import org.uddi.api_v3.tck.TckSubscriptionListener;
import org.uddi.api_v3.tck.TckTModel;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;

/**
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
public class UDDI_090_SubscriptionListenerIntegrationTest
{
	private static Logger logger = Logger.getLogger(UDDI_090_SubscriptionListenerIntegrationTest.class);

	private static TckTModel tckTModel                    = null;
	private static TckBusiness tckBusiness                = null;
	private static TckBusinessService tckBusinessService  = null;
	private static TckSubscriptionListener tckSubscriptionListener = null;

	private static UDDIPublicationPortType publication = null;

	
	private static String authInfoJoe = null;
	private static String authInfoSam = null;

	@BeforeClass
	public static void setup() throws ConfigurationException {
		String clazz = ClientConfig.getConfiguration().getString(Property.UDDI_PROXY_TRANSPORT,Property.DEFAULT_UDDI_PROXY_TRANSPORT);
		if (InVMTransport.class.getName().equals(clazz)) {
			Registry.start();
		}
		logger.debug("Getting subscriber proxy..");
		try {
	    	 clazz = ClientConfig.getConfiguration().getString(Property.UDDI_PROXY_TRANSPORT, Property.DEFAULT_UDDI_PROXY_TRANSPORT);
	         Class<?> transportClass = Loader.loadClass(clazz);
	         if (transportClass!=null) {
	        	 Transport transport = (Transport) transportClass.newInstance();
	        	 
	        	 UDDISecurityPortType security = transport.getUDDISecurityService();
	        	 authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.JOE_PUBLISHER_ID, TckPublisher.JOE_PUBLISHER_CRED);
	        	 Assert.assertNotNull(authInfoJoe);
	        	 authInfoSam = TckSecurity.getAuthToken(security, TckPublisher.SAM_SYNDICATOR_ID, TckPublisher.SAM_SYNDICATOR_CRED);
	        	 Assert.assertNotNull(authInfoSam);
	        	 
	        	 publication = transport.getUDDIPublishService();
	        	 UDDIInquiryPortType inquiry = transport.getUDDIInquiryService();
	        	 UDDISubscriptionPortType subscription = transport.getUDDISubscriptionService();
	        	 
	        	 tckTModel  = new TckTModel(publication, inquiry);
	        	 tckBusiness = new TckBusiness(publication, inquiry);
	        	 tckBusinessService = new TckBusinessService(publication, inquiry);
	        	 tckSubscriptionListener = new TckSubscriptionListener(subscription, publication);	
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
	public void joePublisher() {
		try {
			tckTModel.saveJoePublisherTmodel(authInfoJoe);
			tckBusiness.saveJoePublisherBusiness(authInfoJoe);
			tckBusinessService.saveJoePublisherService(authInfoJoe);
			tckSubscriptionListener.saveService(authInfoJoe);
			tckSubscriptionListener.saveNotifierSubscription(authInfoJoe);
			tckSubscriptionListener.changeSubscribedObject(authInfoJoe);			

			String test = readLogAsString("./target/uddiclient.log");

			if (! test.contains("<name xml:lang=\"en\">Notifier One</name>")) {
				Assert.fail("Notification does not contain the correct service");
			}
			
		} catch (Exception e) {
			e.printStackTrace();

			Assert.fail();
		} finally {
			tckSubscriptionListener.deleteNotifierSubscription(authInfoJoe);
			tckBusinessService.deleteJoePublisherService(authInfoJoe);
			tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
			tckTModel.deleteJoePublisherTmodel(authInfoJoe);
		}
	}	
	
    private static String readLogAsString(String filePath)
    throws java.io.IOException{
        StringBuffer data = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(
                new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            data.append(buf, 0, numRead);
        }
        reader.close();
        String notify = data.toString().replace("Notification received by UDDISubscriptionListenerService : <?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", "");
        
        return notify;
    }
}
