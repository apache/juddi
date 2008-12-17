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
			Assert.assertEquals(2000, refreshDelay);
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
			Assert.assertEquals(3000, nonexisting);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
