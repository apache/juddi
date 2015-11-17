/*
 * Copyright 2015 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.juddi.v3.client.config;

import java.util.UUID;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.configuration.ConfigurationException;
import static org.apache.juddi.v3.client.config.WebHelper.JUDDI_CLIENT_NAME;
import static org.apache.juddi.v3.client.config.WebHelper.UDDI_CLIENT_CONFIG_FILE;
import static org.apache.juddi.v3.client.config.WebHelper.UDDI_CLIENT_NAME;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import org.junit.Assert;

/**
 *
 * @author alex
 */
public class WebHelperTest {

     public WebHelperTest() {
     }

     @BeforeClass
     public static void setUpClass() {
     }

     @AfterClass
     public static void tearDownClass() {
     }

     @Before
     public void setUp() {
     }

     @After
     public void tearDown() {
     }

     /**
      * Test of getUDDIClient method, of class WebHelper.
      */
     @Test
     public void testGetUDDIClient() throws Exception {
          System.out.println("getUDDIClient");

          ServletContext req = createNiceMock(ServletContext.class);
          req.setAttribute(JUDDI_CLIENT_NAME, null);
          expect(req.getInitParameter(WebHelper.JUDDI_CLIENT_NAME)).andReturn(null).times(0, 1);
          //using default config
          //     expect(req.getInitParameter(WebHelper.UDDI_CLIENT_NAME)).andReturn(null).times(0, 2);
          //   expect(req.getInitParameter(WebHelper.UDDI_CLIENT_CONFIG_FILE)).andReturn(null).times(0, 2);
          // expect(req.getInitParameter(WebHelper.JUDDI_CLIENT_TRANSPORT)).andReturn(null).times(0, 2);
          //expect(req.getAttribute(WebHelper.JUDDI_CLIENT_NAME)).andReturn(null).times(0, 2);
          //using default config
          // expect(req.getAttribute(WebHelper.UDDI_CLIENT_NAME)).andReturn(null).times(0, 2);
          //expect(req.getAttribute(WebHelper.UDDI_CLIENT_CONFIG_FILE)).andReturn(null).times(0, 2);
          //expect(req.getAttribute(WebHelper.JUDDI_CLIENT_TRANSPORT)).andReturn(null).times(0, 2);

          replay(req);

          UDDIClient result = WebHelper.getUDDIClient(req);
          Assert.assertNotNull(result);
     }
     
     
      @Test
     public void testGetUDDIClientRandom() throws Exception {
          System.out.println("testGetUDDIClientRandom");

          String random = UUID.randomUUID().toString();
          ServletContext req = createNiceMock(ServletContext.class);
          req.setAttribute(UDDI_CLIENT_NAME, random);
          req.setAttribute(JUDDI_CLIENT_NAME, null);
          expect(req.getInitParameter(WebHelper.JUDDI_CLIENT_NAME)).andReturn(null).times(0, 1);
          expect(req.getInitParameter(WebHelper.UDDI_CLIENT_NAME)).andReturn(random).times(0, 1);
          
          
          
               
               
          //using default config
          //     expect(req.getInitParameter(WebHelper.UDDI_CLIENT_NAME)).andReturn(null).times(0, 2);
          //   expect(req.getInitParameter(WebHelper.UDDI_CLIENT_CONFIG_FILE)).andReturn(null).times(0, 2);
          // expect(req.getInitParameter(WebHelper.JUDDI_CLIENT_TRANSPORT)).andReturn(null).times(0, 2);
          //expect(req.getAttribute(WebHelper.JUDDI_CLIENT_NAME)).andReturn(null).times(0, 2);
          //using default config
          // expect(req.getAttribute(WebHelper.UDDI_CLIENT_NAME)).andReturn(null).times(0, 2);
          //expect(req.getAttribute(WebHelper.UDDI_CLIENT_CONFIG_FILE)).andReturn(null).times(0, 2);
          //expect(req.getAttribute(WebHelper.JUDDI_CLIENT_TRANSPORT)).andReturn(null).times(0, 2);

          replay(req);

          UDDIClient result = WebHelper.getUDDIClient(req);
          Assert.assertNotNull(result);
     }
     
     
       @Test(expected = ConfigurationException.class)
     public void testGetUDDIClientNoConfig() throws Exception {
          System.out.println("testGetUDDIClientNoConfig");

          
          String random = UUID.randomUUID().toString();
          ServletContext req = createNiceMock(ServletContext.class);
          
          req.setAttribute(JUDDI_CLIENT_NAME, null);
          expect(req.getInitParameter(WebHelper.JUDDI_CLIENT_NAME)).andReturn(null).times(0, 1);
          req.setAttribute(UDDI_CLIENT_CONFIG_FILE, random);
          expect(req.getInitParameter(WebHelper.UDDI_CLIENT_CONFIG_FILE)).andReturn(random).times(0, 1);
          
          
          
          
               
               
          //using default config
          //     expect(req.getInitParameter(WebHelper.UDDI_CLIENT_NAME)).andReturn(null).times(0, 2);
          //   expect(req.getInitParameter(WebHelper.UDDI_CLIENT_CONFIG_FILE)).andReturn(null).times(0, 2);
          // expect(req.getInitParameter(WebHelper.JUDDI_CLIENT_TRANSPORT)).andReturn(null).times(0, 2);
          //expect(req.getAttribute(WebHelper.JUDDI_CLIENT_NAME)).andReturn(null).times(0, 2);
          //using default config
          // expect(req.getAttribute(WebHelper.UDDI_CLIENT_NAME)).andReturn(null).times(0, 2);
          //expect(req.getAttribute(WebHelper.UDDI_CLIENT_CONFIG_FILE)).andReturn(null).times(0, 2);
          //expect(req.getAttribute(WebHelper.JUDDI_CLIENT_TRANSPORT)).andReturn(null).times(0, 2);

          replay(req);

          UDDIClient result = WebHelper.getUDDIClient(req);
          Assert.assertNotNull(result);
     }

}
