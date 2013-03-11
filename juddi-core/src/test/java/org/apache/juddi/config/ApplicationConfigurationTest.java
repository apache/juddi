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
package org.apache.juddi.config;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class ApplicationConfigurationTest 
{
	@Test
	public void readPropertyFromFile() throws ConfigurationException
	{
		try {
			long refreshDelay = AppConfig.getConfiguration().getLong(Property.JUDDI_CONFIGURATION_RELOAD_DELAY);
			System.out.println("refreshDelay=" + refreshDelay);
			Assert.assertEquals(2000l, refreshDelay);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void readNonExistingProperty() throws ConfigurationException
	{
		long defaultValue = 3000l;
		try {
			long nonexisting = AppConfig.getConfiguration().getLong("nonexisting.property",defaultValue);
			System.out.println("nonexisting=" + nonexisting);
			Assert.assertEquals(3000l, nonexisting);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void testURLFormats() throws MalformedURLException, URISyntaxException {
		
		URI file = new URI("file:/tmp/");
		String path = file.getSchemeSpecificPart();
		Assert.assertEquals("/tmp/", path);
		
		URI fileInJar = new URI("jar:file:/tmp/my.jar!/");
		String path1 = fileInJar.getSchemeSpecificPart();
		Assert.assertEquals("file:/tmp/my.jar!/", path1);
				
		URI fileInZip = new URI("zip:D:/bea/tmp/_WL_user/JuddiEAR/nk4cwv/war/WEB-INF/lib/juddi-core-3.0.1.jar!");
		String path2 = fileInZip.getSchemeSpecificPart();
		Assert.assertEquals("D:/bea/tmp/_WL_user/JuddiEAR/nk4cwv/war/WEB-INF/lib/juddi-core-3.0.1.jar!", path2);
		
		URI fileInVfszip = new URI("vfsfile:/tmp/SOA%20Platform/jbossesb-registry.sar/juddi_custom_install_data/root_Publisher.xml");
		String path3 = fileInVfszip.getSchemeSpecificPart();
		Assert.assertEquals("/tmp/SOA Platform/jbossesb-registry.sar/juddi_custom_install_data/root_Publisher.xml", path3);
		
	}
	
}
