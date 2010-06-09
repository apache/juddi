/*
 * Copyright 2001-2010 The Apache Software Foundation.
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
package org.apache.juddi.rmi;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Assert;
import org.junit.Test;
import org.mockejb.jndi.MockContextFactory;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class JNDIRegistrationTest 
{
	@Test
	public void registerToJNDI_AnonymousPort() throws ConfigurationException
	{
		try {
			MockContextFactory.setAsInitial();
			//register all jUDDI services, under an anonymous port
		    JNDIRegistration.getInstance().register(0);
		    JNDIRegistration.getInstance().unregister();
		    
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void registerToJNDI_UserDefinedPort() throws ConfigurationException
	{
		try {
			MockContextFactory.setAsInitial();
			//register all jUDDI services, under an use defined port
		    JNDIRegistration.getInstance().register(34567);
		    JNDIRegistration.getInstance().unregister();
		    
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	
}
