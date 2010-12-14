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
package org.apache.juddi.v3.annotations;

import java.util.Properties;

import org.apache.juddi.v3.client.ClassUtil;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.KeyedReference;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class AnnotationTest {
	
	 @Test
	 public void testParsingCategoryBagInput() {
		 AnnotationProcessor ap = new AnnotationProcessor();
		 String categoryBagStr="keyedReference=keyName=uddi-org:types:wsdl;keyValue=wsdlDeployment;tModelKey=uddi:uddi.org:categorization:types," +
		    "keyedReference=keyName=uddi-org:types:wsdl2;keyValue=wsdlDeployment2;tModelKey=uddi:uddi.org:categorization:types2";
		 CategoryBag categoryBag = ap.parseCategoryBag(categoryBagStr);
		 assertEquals(2,categoryBag.getKeyedReference().size());
		 KeyedReference keyedReference = categoryBag.getKeyedReference().get(0);
		 assertEquals("uddi-org:types:wsdl",keyedReference.getKeyName());
		 assertEquals("wsdlDeployment",keyedReference.getKeyValue());
		 assertEquals("uddi:uddi.org:categorization:types",keyedReference.getTModelKey());
		 KeyedReference keyedReference2 = categoryBag.getKeyedReference().get(1);
		 assertEquals("uddi-org:types:wsdl2",keyedReference2.getKeyName());
		 assertEquals("wsdlDeployment2",keyedReference2.getKeyValue());
		 assertEquals("uddi:uddi.org:categorization:types2",keyedReference2.getTModelKey());
	 }
	 
	 @Test
     public void testReadingServiceBindingAnnotation() {
	     try {
	    	Class<?> classWithAnnotations = ClassUtil.forName(HelloWorldMockup.class.getName(), this.getClass());
	    	AnnotationProcessor ap = new AnnotationProcessor();
	    	BindingTemplate bindingTemplate = ap.parseServiceBinding(classWithAnnotations, "en", null, null);
	    	assertNotNull(bindingTemplate);
	    	//expecting references to two keys
	    	assertEquals(2, bindingTemplate.getTModelInstanceDetails().getTModelInstanceInfo().size());
	    	assertEquals("tModelKey1",bindingTemplate.getTModelInstanceDetails().getTModelInstanceInfo().get(0).getTModelKey());
	    	//expecting two KeyedReferences in the CategoryBag
	    	assertEquals(2, bindingTemplate.getCategoryBag().getKeyedReference().size());
	    	KeyedReference keyedReference2 = bindingTemplate.getCategoryBag().getKeyedReference().get(1);
			 assertEquals("uddi-org:types:wsdl2",keyedReference2.getKeyName());
			 assertEquals("wsdlDeployment2",keyedReference2.getKeyValue());
			 assertEquals("uddi:uddi.org:categorization:types2",keyedReference2.getTModelKey());
	     } catch (Exception e) {
	    	 //we should not have any issues reading the annotations
	         e.printStackTrace();
	         Assert.fail();
	     } 
     }
     
     @Test
     public void testReadingServiceAnnotation() {
    	 try {
	    	 AnnotationProcessor ap = new AnnotationProcessor();
	    	 BusinessService service = ap.readServiceAnnotations(HelloWorldMockup.class.getName(),null);
	    	 assertNotNull(service);
	    	 assertEquals("HelloWorld",service.getName().get(0).getValue());
	    	 assertEquals(1,service.getBindingTemplates().getBindingTemplate().size());
	    	 assertNull(service.getCategoryBag());
    	 } catch (Exception e) {
	    	 //we should not have any issues reading the annotations
	         e.printStackTrace();
	         Assert.fail();
	     } 
     }
     
     @Test
     public void testReadingServiceAnnotation2() {
    	 try {
	    	 AnnotationProcessor ap = new AnnotationProcessor();
	    	 Properties properties = new Properties();
	    	 properties.put("serverName", "localhost");
	    	 properties.put("serverPort", "8080");
	    	 BusinessService service = ap.readServiceAnnotations(HelloWorldMockup2.class.getName(),properties);
	    	 assertNotNull(service);
	    	 assertEquals("HelloWorldMockup2",service.getName().get(0).getValue());
	    	 assertEquals(1,service.getBindingTemplates().getBindingTemplate().size());
	    	 BindingTemplate binding = service.getBindingTemplates().getBindingTemplate().get(0);
	    	 String endPoint = binding.getAccessPoint().getValue();
	    	 assertEquals("http://localhost:8080/subscription-listener/helloworld",endPoint);
	    	 String serviceKey = binding.getServiceKey();
	    	 assertEquals(service.getServiceKey(),serviceKey);
	    	 assertNull(service.getCategoryBag());
    	 } catch (Exception e) {
	    	 //we should not have any issues reading the annotations
	         e.printStackTrace();
	         Assert.fail();
	     } 
     }
     
  
     
   
	
}
