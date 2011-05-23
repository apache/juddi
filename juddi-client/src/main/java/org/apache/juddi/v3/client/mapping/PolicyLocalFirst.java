/*
 * Copyright 2001-2011 The Apache Software Foundation.
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
package org.apache.juddi.v3.client.mapping;

import java.util.Properties;

public class PolicyLocalFirst extends PolicyRoundRobin {

	public final static String JUDDI_CLIENT_LOCAL   = "juddi.client.local";
	public final static String DEFAULT_CLIENT_LOCAL = "localhost";
	private static String local;
	
	/**
	 * This policy prefers 'local' EPR over remote EPRs. By default 'local' means
	 * the EPR contains the String 'localhost'. This setting can be overwritten
	 * by setting the 'juddi.client.local' property. An example would be 'localhost:8080'.
	 * 
	 * @param properties
	 */
	public PolicyLocalFirst(Properties properties) {
		super(properties);
		if (properties!=null) {
			local = properties.getProperty(JUDDI_CLIENT_LOCAL, DEFAULT_CLIENT_LOCAL);
		} else {
			local = DEFAULT_CLIENT_LOCAL;
		}
	}
	
	public String select(Topology topology) {
		
		if (topology.getEprs().size()==0) return null;
		
		if ((topology.getHasLocal()==null)) {
			int pointer = 0;
			topology.setHasLocal(Boolean.FALSE);
			for (String epr : topology.getEprs()) {
				if (epr.toLowerCase().contains(local.toLowerCase())) {
					topology.setPointer(pointer);
					topology.setHasLocal(Boolean.TRUE);
					break;
				}
				pointer++;
			}
			
		}
		
		if (topology.getHasLocal()) {
			//return the localEpr
			return topology.getEprs().get(topology.getPointer());
		} else {
			//no local EPR, fall back on roundrobin
			return super.select(topology);
		}
	}

}
