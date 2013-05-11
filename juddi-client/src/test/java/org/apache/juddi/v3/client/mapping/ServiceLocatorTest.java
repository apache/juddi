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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
	
	
	
}
