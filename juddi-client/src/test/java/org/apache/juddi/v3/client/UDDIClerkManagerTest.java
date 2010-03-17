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
package org.apache.juddi.v3.client;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.apache.juddi.v3.annotations.AnnotationProcessor;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClerkManager;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.junit.Assert;
import org.junit.Test;
import org.uddi.api_v3.BusinessService;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class UDDIClerkManagerTest {
	
     @Test
     public void testReadingTheConfig() {
	     try {
	    	 UDDIClerkManager manager = UDDIClientContainer.getUDDIClerkManager(null);
	    	 manager.start();
	    	 manager.getClientConfig().getUDDINode("default");
	    	 assertEquals(2,manager.getClientConfig().getUDDIClerks().size());
	    	 Thread.sleep(500);
	    	 manager.stop();
	     } catch (Exception e) {
	    	 //we should not have any issues reading the config
	         e.printStackTrace();
	         Assert.fail();
	     } 
     }
     
     @Test
     public void testMultipleClientConfigFiles() {
    	 try {
    		 UDDIClerkManager manager = new UDDIClerkManager("META-INF/uddi.xml");
    		 manager.start();
			 assertEquals("test-manager", manager.getName());
			 
    		 UDDIClerkManager manager2 = new UDDIClerkManager("META-INF/uddi2.xml");
    		 manager2.start();
			 assertEquals("second-manager", manager2.getName());
			 Thread.sleep(500);
			 manager.stop();
			 manager2.stop();
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail("No exceptions are expected");
		}
    	 
     }
     
     @Test
     public void testDefaultConfigFile() {
    	 try {
    		 UDDIClerkManager manager = new UDDIClerkManager(null);
    		 //We're expecting the manager defined in the META-INF/uddi.xml file.
    		 manager.start();
			 assertEquals("test-manager", manager.getName());
			 Thread.sleep(500);
			 manager.stop();
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail("No exceptions are expected");
		}
    	 
     }
     
     @Test
     public void testDefaultManager() {
    	 try {		
    		 //This is a special case where the manager in the META-INF/uddi.xml file is 
    		 //instantiated and started simply by getting it.
    		 //This functionality was add for backwards compatibility. 
    		 UDDIClerkManager manager = UDDIClientContainer.getUDDIClerkManager(null);
    		 manager.start();
			 assertEquals("test-manager", manager.getName());
			 assertEquals("default", manager.getClientConfig().getHomeNode().getName());
			 Thread.sleep(500);
			 manager.stop();
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail("No exceptions are expected");
		}
    	 
     }
     
     @Test
     public void testReadingAnnotations() {
    	 try {
    		 UDDIClerkManager manager = UDDIClientContainer.getUDDIClerkManager(null);
	    	 Map<String,UDDIClerk> clerks = manager.getClientConfig().getUDDIClerks();
	 		 AnnotationProcessor ap = new AnnotationProcessor();
	 		 if (clerks.containsKey("default")) {
		 		 UDDIClerk clerk = clerks.get("default");
		 		 BusinessService service = ap.readServiceAnnotations(
		 				 HelloWorldMockup.class.getName(), clerk.getUDDINode().getProperties());
		 		 assertEquals("uddi:juddi.apache.org:services-helloworld",service.getServiceKey());
	 		 } else {
	 			Assert.fail("Could not find expected clerk='default'");
	 		 }
    	 } catch (Exception e) {
	    	 //we should not have any issues reading the config
	         e.printStackTrace();
	         Assert.fail("No exceptions are expected");
	     } 
    	 
     }
     
   
	
}
