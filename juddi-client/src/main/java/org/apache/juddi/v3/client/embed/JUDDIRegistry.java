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
package org.apache.juddi.v3.client.embed;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.ClassUtil;

public class JUDDIRegistry implements EmbeddedRegistry {

	private Log logger = LogFactory.getLog(this.getClass());
	public void start() {
		try {
			Class<?> juddiRegistry = ClassUtil.forName("org.apache.juddi.Registry", JUDDIRegistry.class);
			Method startMethod = juddiRegistry.getDeclaredMethod("start");
			startMethod.invoke(juddiRegistry);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	public void stop() {
		try {
			Class<?> juddiRegistry = ClassUtil.forName("org.apache.juddi.Registry", JUDDIRegistry.class);
			Method stopMethod = juddiRegistry.getDeclaredMethod("stop");
			stopMethod.invoke(juddiRegistry);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

}
