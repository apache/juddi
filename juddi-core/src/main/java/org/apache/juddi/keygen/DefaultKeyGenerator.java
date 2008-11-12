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

import org.apache.juddi.uuidgen.UUIDGenFactory;
import org.apache.juddi.uuidgen.UUIDGen;

/**
 * The default jUDDI key generator.  Generates a key like this:
 * 
 * uddiScheme : RootDomain : UUID
 * 
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class DefaultKeyGenerator implements KeyGenerator {

	public String generate() {
		UUIDGen uuidgen = UUIDGenFactory.getUUIDGen();
		return UDDI_SCHEME + PARTITION_SEPARATOR + ROOT_DOMAIN + PARTITION_SEPARATOR + uuidgen.uuidgen();
	}
}