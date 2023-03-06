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
package org.apache.juddi.config;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * From the 
 * 
 * @author kstam
 *
 */
public class PublisherFileFilter implements FileFilter {

	private Log log = LogFactory.getLog(this.getClass());
	
	public boolean accept(File file) {
		log.debug("file=" + file);
		if (file.getName().endsWith(Install.FILE_PUBLISHER)) {
			return true;
		}
		return false;
	}

}
