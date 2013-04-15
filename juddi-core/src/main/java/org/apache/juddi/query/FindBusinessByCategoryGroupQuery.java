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
import org.uddi.api_v3.CategoryBag;

/**
 * Returns the list of business keys possessing the keyedReferenceGroups in the passed category bag.
 * Output is restricted by list of business keys passed in.  If null, all business entities are searched.
 * Output is produced by building the appropriate JPA query based on input and find qualifiers.
 * 
 * From specification:
 * "This is a list of category references in the form of keyedReference elements and keyedReferenceGroup 
 * structures.  The returned businessList contains businessInfo elements matching all of the categories 
 * passed (logical AND by default).   Specifying the appropriate findQualifiers can override this behavior.
 * 
 * A given keyedReferenceGroup "X" (e.g., within a given categoryBag) matches a keyedReferenceGroup "Y" in the registry if 
 * and only if the tModelKey assigned to the keyedReferenceGroup X matches the tModelKey assigned to the keyedReferenceGroup 
 * Y and the set of keyedReferences in "X" are a subset of the set of keyedReferences in "Y."  The order of individual 
 * keyedReferences within a keyedReferenceGroup is not important. Matching rules for the individual contained keyedReference 
 * elements are the same as above."
 * 
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class FindBusinessByCategoryGroupQuery {
	
	private static final String ENTITY_NAME_CHILD = "BusinessCategoryBag";
	
	private static FindEntityByCategoryGroupQuery findQuery;
	
	static {
		findQuery = new FindEntityByCategoryGroupQuery(BusinessEntityQuery.ENTITY_NAME, 
													   BusinessEntityQuery.ENTITY_ALIAS, 
													   BusinessEntityQuery.KEY_NAME, 
													   BusinessEntityQuery.ENTITY_FIELD, 
													   ENTITY_NAME_CHILD,
													   BusinessEntityQuery.SIGNATURE_PRESENT);
	}
	
	public static List<?> select(EntityManager em, FindQualifiers fq, CategoryBag categoryBag, List<?> keysIn) {
		return findQuery.select(em, fq, categoryBag, keysIn);
	}
}
