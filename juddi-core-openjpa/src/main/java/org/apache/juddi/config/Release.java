/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

/**
 * 
 */
public class Release {
	
	private static final String UDDI_VERSION = "3.0";
	private static final String JAR_NAME = "juddi-core";
	private static String registryVersion = null;
   
	private Release () {
	}

	public static String getRegistryVersion() {
		if (registryVersion == null) {
			registryVersion = org.apache.juddi.v3.client.Release.getVersionFromManifest(JAR_NAME);
		}
		return registryVersion;
		
	}

	public static String getUDDIVersion() {
		return UDDI_VERSION;	
	} 
	
	
}
