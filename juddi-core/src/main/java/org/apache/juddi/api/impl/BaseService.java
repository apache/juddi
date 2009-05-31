/*
 * Copyright 2001-2008 The Apache Software Foundation.
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

package org.apache.juddi.api.impl;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.Registry;
import org.apache.log4j.Logger;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public abstract class BaseService {

	private Logger log = Logger.getLogger(this.getClass());
	protected Registry registry = null;
	
	protected BaseService() {
		super();
		try {
			Registry.start();
		} catch (ConfigurationException ce) {
			log.error(ce.getMessage(),ce);
		}
	}
}
