/*
cd .. * Copyright 2001-2009 The Apache Software Foundation.
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
package org.apache.juddi.subscription.notify;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

import junit.framework.Assert;

import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.model.BindingTemplate;
import org.apache.juddi.model.TmodelInstanceInfo;
import org.junit.Test;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class NotifierTest 
{	
	@Test
	public void testHTTPNotifier() throws IllegalArgumentException, SecurityException, URISyntaxException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		BindingTemplate bindingTemplate = new BindingTemplate();
		bindingTemplate.setEntityKey("uddi:uddi.joepublisher.com:bindingnotifier");
		bindingTemplate.setAccessPointType(AccessPointType.END_POINT.toString());
		bindingTemplate.setAccessPointUrl("http://localhost:12345/tcksubscriptionlistener");
		TmodelInstanceInfo instanceInfo = new TmodelInstanceInfo();
		instanceInfo.setTmodelKey("uddi:uddi.org:transport:http");
		bindingTemplate.getTmodelInstanceInfos().add(instanceInfo);
		
		Notifier notifier = new NotifierFactory().getNotifier(bindingTemplate);
		
		Assert.assertEquals(HTTPNotifier.class, notifier.getClass());
	}
	@Test
	public void testSMTPNotifier() throws IllegalArgumentException, SecurityException, URISyntaxException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		BindingTemplate bindingTemplate = new BindingTemplate();
		bindingTemplate.setEntityKey("uddi:uddi.joepublisher.com:bindingnotifier");
		bindingTemplate.setAccessPointType(AccessPointType.END_POINT.toString());
		bindingTemplate.setAccessPointUrl("mailto:order@islandtrading.example");
		TmodelInstanceInfo instanceInfo = new TmodelInstanceInfo();
		instanceInfo.setTmodelKey("uddi:uddi.org:transport:smtp");
		bindingTemplate.getTmodelInstanceInfos().add(instanceInfo);
		
		Notifier notifier = new NotifierFactory().getNotifier(bindingTemplate);
		
		Assert.assertEquals(SMTPNotifier.class, notifier.getClass());
	}
	//Expected error because we can't connect to the registry on localhost:11099
	@Test(expected=java.lang.reflect.InvocationTargetException.class)
	public void testRMINotifier() throws IllegalArgumentException, SecurityException, URISyntaxException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		BindingTemplate bindingTemplate = new BindingTemplate();
		bindingTemplate.setEntityKey("uddi:uddi.joepublisher.com:bindingnotifier");
		bindingTemplate.setAccessPointType(AccessPointType.END_POINT.toString());
		bindingTemplate.setAccessPointUrl("rmi://localhost:11099/tcksubscriptionlistener");
		TmodelInstanceInfo instanceInfo = new TmodelInstanceInfo();
		instanceInfo.setTmodelKey("uddi:uddi.org:transport:rmi");
		bindingTemplate.getTmodelInstanceInfos().add(instanceInfo);
		
		Notifier notifier = new NotifierFactory().getNotifier(bindingTemplate);
		
		Assert.assertEquals(RMINotifier.class, notifier.getClass());
	}
	//Expected error because we did not specify a correct InitialContext
	@Test(expected=java.lang.reflect.InvocationTargetException.class)
	public void testJNDIRMINotifier() throws IllegalArgumentException, SecurityException, URISyntaxException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		BindingTemplate bindingTemplate = new BindingTemplate();
		bindingTemplate.setEntityKey("uddi:uddi.joepublisher.com:bindingnotifier");
		bindingTemplate.setAccessPointType(AccessPointType.END_POINT.toString());
		bindingTemplate.setAccessPointUrl("jndi-rmi://localhost:11099/tcksubscriptionlistener");
		TmodelInstanceInfo instanceInfo = new TmodelInstanceInfo();
		instanceInfo.setTmodelKey("uddi:uddi.org:transport:jndi-rmi");
		bindingTemplate.getTmodelInstanceInfos().add(instanceInfo);
		
		Notifier notifier = new NotifierFactory().getNotifier(bindingTemplate);
		
		Assert.assertEquals(JNDI_RMINotifier.class, notifier.getClass());
	}
		
	
	
}
