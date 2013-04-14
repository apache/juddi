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

package org.apache.juddi.query;

import java.util.List;
import javax.persistence.EntityManager;

import org.apache.juddi.query.util.FindQualifiers;
import org.uddi.api_v3.Name;

/**
 * Returns the list of business keys possessing the Names in the passed Name list.
 * Output is restricted by list of business keys passed in.  If null, all businesses are searched.
 * Output is produced by building the appropriate JPA query based on input and find qualifiers.
 *
 * From specification:
 * "This optional collection of string values represents one or more names potentially qualified with xml:lang attributes. 
 * Since "exactMatch" is the default behavior, the value supplied for the name argument must be an exact match.  If the "approximateMatch" 
 * findQualifier is used together with an appropriate wildcard character in the name, then any businessEntity matching this name with 
 * wildcards and the other criteria will be referenced in the results.  For more on wildcard matching, see Section 5.1.6 About Wildcards.   
 * The businessList returned contains businessInfo structures for businesses whose name matches the value(s) passed 
 * (lexical-order match i.e., leftmost in left-to-right languages).  If multiple name values are passed, the match occurs on a 
 * logical OR basis. Each name MAY be marked with an xml:lang adornment.  If a language markup is specified, the search results report a 
 * match only on those entries that match both the name value and language criteria. The match on language is a leftmost case-insensitive 
 * comparison of the characters supplied. This allows one to find all businesses whose name begins with an "A" and are expressed in any 
 * dialect of French, for example.  Values which can be passed in the language criteria adornment MUST obey the rules governing the 
 * xml:lang data type as defined in Section 3.3.2.3 name."
 *  
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class FindBusinessByNameQuery {

	public static final String ENTITY_NAME_CHILD = "BusinessName";
	
	private static FindEntityByNamesQuery findQuery;
	
	static {
		findQuery = new FindEntityByNamesQuery(BusinessEntityQuery.ENTITY_NAME, 
											   BusinessEntityQuery.ENTITY_ALIAS, 
											   BusinessEntityQuery.KEY_NAME, 
											   BusinessEntityQuery.ENTITY_FIELD, 
											   ENTITY_NAME_CHILD,
											   BusinessEntityQuery.SIGNATURE_PRESENT);
	}

	public static List<?> select(EntityManager em, FindQualifiers fq, List<Name> names, List<?> keysIn) {
		return findQuery.select(em, fq, names, keysIn);
	}
	
}
