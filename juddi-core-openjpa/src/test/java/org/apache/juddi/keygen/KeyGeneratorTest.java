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
package org.apache.juddi.keygen;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.config.Property;
import org.apache.juddi.model.UddiEntityPublisher;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class KeyGeneratorTest 
{
	private Log logger = LogFactory.getLog(this.getClass());
	

	@Test() 
	public void testNonExisitingKeyGeneratorClass()
	{
		System.setProperty(Property.JUDDI_KEYGENERATOR, "org.apache.juddi.keygen.FooGenerator");
		try {
			KeyGenerator keyGenerator = KeyGeneratorFactory.forceNewKeyGenerator();
			System.out.println("Generator = " + keyGenerator.getClass());
                        if (!keyGenerator.getClass().getCanonicalName().equals(KeyGeneratorFactory.DEFAULT_IMPL))
                            Assert.fail("This should have thrown an exception because this class does not exist.");
		} catch (Exception e) {
			String message = e.getMessage();
			Assert.assertEquals("The specified Key Generator class 'org.apache.juddi.keygen.FooGenerator' was not found on classpath.", message);
		}
                System.clearProperty(Property.JUDDI_KEYGENERATOR);
	}
	
	@Test() 
	public void testGeneratorInterface()
	{
		System.setProperty(Property.JUDDI_KEYGENERATOR, "org.apache.juddi.keygen.KeyGenerator");
		try {
			KeyGenerator keyGenerator = KeyGeneratorFactory.forceNewKeyGenerator();
			System.out.println("Generator = " + keyGenerator.getClass());
                        Assert.fail("This should have thrown an exception because you cannot instantiate an interface.");
		} catch (Exception e) {
			String message = e.getMessage();
			Assert.assertEquals("The specified Key Generator class 'org.apache.juddi.keygen.KeyGenerator' cannot be instantiated.", message);
		}
                System.clearProperty(Property.JUDDI_KEYGENERATOR);
	}
	/**
	 * The DefaultKeyGenerator
	 * @throws ConfigurationException
	 */
	@Test
	public void testDefaultKeyGenerator()
	{
		System.setProperty(Property.JUDDI_KEYGENERATOR, "org.apache.juddi.keygen.DefaultKeyGenerator");
		try {
			KeyGenerator keyGenerator = KeyGeneratorFactory.forceNewKeyGenerator();
			Assert.assertEquals(org.apache.juddi.keygen.DefaultKeyGenerator.class, keyGenerator.getClass());
			String key = keyGenerator.generate(null);
			Assert.assertNotNull(key);
			Assert.assertTrue(key.startsWith("uddi:juddi.apache.org"));
			UddiEntityPublisher publisher = new UddiEntityPublisher();
			
			List<String> keyGeneratorKeys = new ArrayList<String>();
			keyGeneratorKeys.add("uddi:mydomain.org:keyGenerator");
			publisher.setKeyGeneratorKeys(keyGeneratorKeys);
			String key2 = keyGenerator.generate(publisher);
			Assert.assertNotNull(key2);
			Assert.assertTrue(key2.startsWith("uddi:mydomain.org"));
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			Assert.fail("unexpected");
		}
                System.clearProperty(Property.JUDDI_KEYGENERATOR);
	}
	
}
