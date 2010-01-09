/*
 * Copyright 2001-2010 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.juddi.xlt.test;

import org.apache.juddi.xlt.action.SOAP.GetAuthenticationToken;
import org.apache.juddi.xlt.util.AbstractUDDIClientTestCase;
import org.junit.Test;
 
/**
 * This test case simply gets an AuthToken from the juddi node
 * 
 */
public class TGetAuthToken extends AbstractUDDIClientTestCase {

	/**
	 * Gets a AuthToken from Juddi
	 * @throws Throwable
	 */
	@Test
	public void test() throws Throwable {
		GetAuthenticationToken getAuthenticationToken = new GetAuthenticationToken();
		getAuthenticationToken.run();
	}
}
