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

package org.apache.juddi.query.util;

import java.util.Comparator;
import org.uddi.api_v3.KeyedReferenceGroup;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class KeyedRefGroupTModelComparator implements Comparator<KeyedReferenceGroup> {

	public int compare(KeyedReferenceGroup krg1, KeyedReferenceGroup krg2) {
		if (krg1 == null && krg2 == null)
			return 0;
		if (krg1 == null)
			return -1;
		if (krg2 == null)
			return 1;
		
		if (krg1.getTModelKey() == null && krg2.getTModelKey() == null)
			return 0;
		if (krg1.getTModelKey() == null)
			return -1;
		if (krg2.getTModelKey() == null)
			return 1;
			
		return krg1.getTModelKey().compareTo(krg2.getTModelKey());
	}
	
}
