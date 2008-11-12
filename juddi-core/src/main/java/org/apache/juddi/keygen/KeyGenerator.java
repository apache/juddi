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

package org.apache.juddi.keygen;


/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public interface KeyGenerator {
	// TODO: This is temporary until JUDDI-155 is worked out.
	public static String ROOT_DOMAIN = "juddi.apache.org";
	
	
	public static String UDDI_SCHEME = "uddi";
	public static String PARTITION_SEPARATOR = ":";
	public static String KEYGENERATOR_SUFFIX = "keygenerator";

	/*
	 * Generates a key that is used to save a UDDI entity.
	 */
	public String generate();
}