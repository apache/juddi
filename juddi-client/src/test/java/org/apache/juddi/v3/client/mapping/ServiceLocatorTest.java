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
package org.apache.juddi.v3.client.mapping;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.v3.client.config.UDDIClient;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class ServiceLocatorTest {
	
	@Test @Ignore
	public void testRoundRobin() {
		try {
			List<String> eprs = new ArrayList<String>();
			eprs.add("epr1");
			eprs.add("epr2");
			eprs.add("epr3");
			
			
			Properties properties = new Properties();
			ServiceLocator locator = new ServiceLocator(null, new URLLocalizerDefaultImpl(), properties);
			System.out.println(locator);
			locator.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
			
		}
		
	}

    @Test
    public void testJUDDI_939() throws Exception {
            UDDIClient uddiClient = new UDDIClient();
            ServiceLocator serviceLocator = uddiClient.getServiceLocator("default");
            Assert.assertNotNull(serviceLocator);
            serviceLocator.setPolicy(PolicyRoundRobin.class.getName());
            //serviceLocator.withCache(new URL("http", "0.0.0.0", 0, ""));
    }
    
     @Test(expected = ConfigurationException.class)
    public void testJUDDI_939_1() throws Exception {
            UDDIClient uddiClient = new UDDIClient();
            ServiceLocator serviceLocator = uddiClient.getServiceLocator(UUID.randomUUID().toString());
            Assert.fail();
    }
    
    
    /**
     * this test requires a running juddi node on port 8080 and is therefore disabled from this context
     * @throws Exception 
     */
    @Ignore
    @Test
    public void testJUDDI_939_2() throws Exception {
        String uddiServiceId = "uddi:juddi.apache.org:services-inquiry";
        try {
            UDDIClient uddiClient = new UDDIClient();
            ServiceLocator serviceLocator = uddiClient.getServiceLocator("default8080");
            serviceLocator.setPolicy(PolicyRoundRobin.class.getName());
            //serviceLocator.withCache(new URL("http://localhost:8080/juddiv3/services/"));
            String endpoint = serviceLocator.lookupEndpoint(uddiServiceId);
            Assert.assertNotNull(endpoint);
            
            String endpoint2 = serviceLocator.lookupEndpoint(uddiServiceId);
            Assert.assertNotNull(endpoint2);
            Assert.assertNotEquals(endpoint2, endpoint);

        } catch (Exception e) {
            throw new Exception("Could not resolve endpoint '" + uddiServiceId + "'.", e);
        }

    }


	
}
