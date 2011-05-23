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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class SelectionTest {

	
	@Test
	public void testRoundRobin() {
		List<String> eprs = new ArrayList<String>();
		eprs.add("epr1");
		eprs.add("epr2");
		eprs.add("epr3");
		Topology topology = new Topology(eprs);
		SelectionPolicy selection = new PolicyRoundRobin(null);
		
		Assert.assertEquals("epr2",selection.select(topology));
		Assert.assertEquals("epr3",selection.select(topology));
		Assert.assertEquals("epr1",selection.select(topology));
		Assert.assertEquals("epr2",selection.select(topology));
		Assert.assertEquals("epr3",selection.select(topology));
		Assert.assertEquals("epr1",selection.select(topology));
		Assert.assertEquals("epr2",selection.select(topology));
		
	}
	
	@Test
	public void testLocalFirst() {
		List<String> eprs = new ArrayList<String>();
		eprs.add("localhost:epr1");
		eprs.add("remotehost:epr2");
		eprs.add("remotehost:epr3");
		Topology topology = new Topology(eprs);
		
		SelectionPolicy selection = new PolicyLocalFirst(null);
		
		Assert.assertEquals("localhost:epr1",selection.select(topology));
		Assert.assertEquals("localhost:epr1",selection.select(topology));
		Assert.assertEquals("localhost:epr1",selection.select(topology));
		
		
	}
	
	@Test
	public void testLocalFirst2() {
		List<String> eprs = new ArrayList<String>();
		eprs.add("host1:epr1");
		eprs.add("host2:epr2");
		eprs.add("host3:epr3");
		Topology topology = new Topology(eprs);
		
		//If the epr contains the String 'host2:' it should get picked 
		Properties properties = new Properties();
		properties.put(PolicyLocalFirst.JUDDI_CLIENT_LOCAL, "host2:");
		SelectionPolicy selection = new PolicyLocalFirst(properties);
		
		Assert.assertEquals("host2:epr2",selection.select(topology));
		Assert.assertEquals("host2:epr2",selection.select(topology));
		Assert.assertEquals("host2:epr2",selection.select(topology));
		
	}
	
}
