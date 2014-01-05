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

import java.net.BindException;
import java.net.URL;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class UDDIServiceCacheTest {
	
	@Ignore
	@Test(expected=BindException.class)
	public void testPortTakenException() throws Throwable {
		
		try {
			String urlStr = "http://localhost:8080/subscriptionlistener_uddi_client";
			URL url = new URL(urlStr);
			URL url2 = new URL(url.getProtocol(),url.getHost(),url.getPort(),url.getFile());
//			String protocol = url.getProtocol();
//			String host = url.getHost();
//			String path = url.getPath();
			
			
			
			
          
			System.out.println("");
			//UDDIServiceCache serviceCache = new UDDIServiceCache();
			
			
			//serviceCache.publishHttpCallbackEndpoint(new UDDIClientSubscriptionListenerImpl(),urlStr);
			
		} catch (Throwable e) {
			if (e.getCause() instanceof BindException) {
				throw e.getCause();
			}
		}
	}
	
	
}
