/*
 * Copyright 2001-2013 The Apache Software Foundation.
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
package org.uddi;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

public class JAXBContextUtil {

	private static Log log = LogFactory.getLog(JAXBContextUtil.class);
	private static final Map<String, JAXBContext> JAXBContexts = new HashMap<String, JAXBContext>();

	public static JAXBContext getContext(String packageName) throws JAXBException {
		if (!JAXBContexts.containsKey(packageName)) {
			log.info("Creating JAXB Context for " + packageName);
			JAXBContexts.put(packageName, JAXBContext.newInstance(packageName));
		}
		return JAXBContexts.get(packageName);
	}
	
}
