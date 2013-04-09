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
package org.apache.juddi.v3.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * 
 */
public class Release {
	
	private static final String UDDI_VERSION = "3.0";
	private static final String JAR_NAME = "juddi-client";
	private static String registryVersion = null;
   
	private Release () {
	}

	public static String getRegistryVersion() {
		if (registryVersion == null) {
			registryVersion = getVersionFromManifest(JAR_NAME);
		}
		return registryVersion;
		
	}

	public static String getUDDIVersion() {
		return UDDI_VERSION;	
	} 
	
	public static String getVersionFromManifest(String jarName) {
		Enumeration<URL> resEnum;
        try {
            resEnum = Thread.currentThread().getContextClassLoader().getResources(JarFile.MANIFEST_NAME);
            while (resEnum.hasMoreElements()) {
                try {
                    URL url = (URL) resEnum.nextElement();
                    if (url.toString().toLowerCase().contains(jarName)) {
                        InputStream is = url.openStream();
                        if (is != null) {
                            Manifest manifest = new Manifest(is);
                            Attributes mainAttribs = manifest.getMainAttributes();
                            String version = mainAttribs.getValue("Bundle-Version");
                            if (version != null) {
                                return (version);
                            }
                        }
                    }
                } catch (Exception e) {
                    // Silently ignore wrong manifests on classpath?
                }
            }
         } catch (IOException e1) {
            // Silently ignore wrong manifests on classpath?
         }
         return "unknown";
	}
}
