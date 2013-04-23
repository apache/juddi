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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.config.Property;
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
			Assert.fail("This should have thrown an exception because this class does not exist.");
		} catch (Exception e) {
			String message = e.getMessage();
			Assert.assertEquals("The specified Key Generator class 'org.apache.juddi.keygen.FooGenerator' was not found on classpath.", message);
		}
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
			String key = keyGenerator.generate();
			Assert.assertNotNull(key);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			Assert.fail("unexpected");
		}
	}
	
}
