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

import java.net.URISyntaxException;
import java.net.URL;

import org.apache.juddi.v3.client.ClassUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class WSDLLocatorTest {

	@Test
	public void testResolveAbsoluteURL() {
		WSDLLocatorImpl wsdlLocatorImpl = new WSDLLocatorImpl(null);
		URL url = wsdlLocatorImpl.constructImportUrl(null, "http://localhost/file.wsdl");
		Assert.assertEquals("http://localhost/file.wsdl", url.toExternalForm());
	}
	
	@Test
	public void testResolveAbsoluteURL2() {
		WSDLLocatorImpl wsdlLocatorImpl = new WSDLLocatorImpl(null);
		URL url = wsdlLocatorImpl.constructImportUrl("should get ignored", "http://localhost/file.wsdl");
		Assert.assertEquals("http://localhost/file.wsdl", url.toExternalForm());
	}
	
	@Test
	public void testResolveInCurrentDir() {
		WSDLLocatorImpl wsdlLocatorImpl = new WSDLLocatorImpl(null);
		URL wsdlFile = ClassUtil.getResource("wsdl/HelloWorld.wsdl", this.getClass());
		if (wsdlFile==null) Assert.fail("Can not find HelloWorld.wsdl file");
		String wsdlFileStr = wsdlFile.toExternalForm();
		String wsdlDir = wsdlFileStr.substring(0,wsdlFileStr.lastIndexOf("/"));
		URL url = wsdlLocatorImpl.constructImportUrl(wsdlFileStr, "child.wsdl");
		Assert.assertEquals(wsdlDir + "/child.wsdl", url.toExternalForm());
	}
	
	@Test
	public void testResolveInCurrentDir2() {
		WSDLLocatorImpl wsdlLocatorImpl = new WSDLLocatorImpl(null);
		URL wsdlFile = ClassUtil.getResource("wsdl/HelloWorld.wsdl", this.getClass());
		if (wsdlFile==null) Assert.fail("Can not find HelloWorld.wsdl file");
		String wsdlFileStr = wsdlFile.toExternalForm();
		String wsdlDir = wsdlFileStr.substring(0,wsdlFileStr.lastIndexOf("/"));
		URL url = wsdlLocatorImpl.constructImportUrl(wsdlFileStr, "./child.wsdl");
		Assert.assertEquals(wsdlDir + "/child.wsdl", url.toExternalForm());
	}
	
	@Test
	public void testResolveInParentDir() {
		WSDLLocatorImpl wsdlLocatorImpl = new WSDLLocatorImpl(null);
		URL wsdlFile = ClassUtil.getResource("wsdl/HelloWorld.wsdl", this.getClass());
		if (wsdlFile==null) Assert.fail("Can not find HelloWorld.wsdl file");
		String wsdlFileStr = wsdlFile.toExternalForm();
		String wsdlDir = wsdlFileStr.substring(0,wsdlFileStr.lastIndexOf("/"));
		wsdlDir = wsdlDir.substring(0, wsdlDir.lastIndexOf("/"));
		URL url = wsdlLocatorImpl.constructImportUrl(wsdlFileStr, "../child.wsdl");
		Assert.assertEquals(wsdlDir + "/child.wsdl", url.toExternalForm());
	}
	
	@Test
	public void testResolveInParentDir2() throws URISyntaxException {
		WSDLLocatorImpl wsdlLocatorImpl = new WSDLLocatorImpl(null);
		URL wsdlFile = ClassUtil.getResource("wsdl/HelloWorld.wsdl", this.getClass());
		if (wsdlFile==null) Assert.fail("Can not find HelloWorld.wsdl file");
		String wsdlFileStr = wsdlFile.toExternalForm();
		String wsdlDir = wsdlFileStr.substring(0,wsdlFileStr.lastIndexOf("/"));
		wsdlDir = wsdlDir.substring(0, wsdlDir.lastIndexOf("/"));
		wsdlDir = wsdlDir.substring(0, wsdlDir.lastIndexOf("/"));
		URL url = wsdlLocatorImpl.constructImportUrl(wsdlFileStr, "../../child.wsdl");
		Assert.assertEquals(wsdlDir + "/child.wsdl", url.toExternalForm());
	}
	
}
