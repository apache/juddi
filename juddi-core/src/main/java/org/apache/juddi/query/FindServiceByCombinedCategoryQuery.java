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

import org.apache.juddi.query.util.DynamicQuery;
import org.apache.juddi.query.util.FindQualifiers;
import org.uddi.api_v3.CategoryBag;

/**
 * Returns the list of service keys possessing the keyedReferences in the passed category bag.
 * Output is restricted by list of service keys passed in.  If null, all business services are searched.
 * Output is produced by building the appropriate JPA query based on input and find qualifiers.
 * 
 * From specification:
 * From specification:
 * "combineCategoryBags:  this may only be used in the find_business and find_service calls.  In the case of 
 * find_business, this qualifier makes the categoryBag entries for the full businessEntity element behave as though all 
 * categoryBag elements found at the businessEntity level and in all contained or referenced businessService elements 
 * and bindingTemplate elements were combined.  Searching for a category will yield a positive match on a registered 
 * business if any of the categoryBag elements contained within the full businessEntity element (including the 
 * categoryBag elements within contained or referenced businessService elements or bindingTemplate elements) 
 * contains the filter criteria. 
 * 
 * In the case of find_service, this qualifier makes the categoryBag entries 
 * for the full businessService element behave as though all categoryBag elements found at the businessService level 
 * and in all contained or referenced elements in the bindingTemplate elements were combined.  Searching for a category 
 * will yield a positive match on a registered service if any of the categoryBag elements contained within the 
 * full businessService element (including the categoryBag elements within contained or referenced bindingTemplate 
 * elements) contains the filter criteria. 
 * 
 * This find qualifier does not cause the keyedReferences in categoryBags 
 * to be combined with the keyedReferences in keyedReferenceGroups in categoryBags when performing the comparison.  
 * The keyedReferences are combined with each other, and the keyedReferenceGroups are combined with each other."
 * 
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 * @author <a href="mailto:kstam@apache.org">Kurt Stam</a>
 */
public class FindServiceByCombinedCategoryQuery {
	
	private static final String ENTITY_NAME_CHILD  = "ServiceCategoryBag";
	private static final String ENTITY_FIELD2      = "bindingTemplate." + BusinessServiceQuery.ENTITY_FIELD;
	private static final String ENTITY_NAME_CHILD2 = "BindingCategoryBag";
	
	private static FindEntityByCombinedCategoryQuery findQuery;
	
	static {
		findQuery = new FindEntityByCombinedCategoryQuery(BusinessServiceQuery.ENTITY_NAME, 
														  BusinessServiceQuery.ENTITY_ALIAS, 
														  BusinessServiceQuery.KEY_NAME, 
														  BusinessServiceQuery.ENTITY_FIELD, 
														  ENTITY_NAME_CHILD,
														  ENTITY_FIELD2,
														  ENTITY_NAME_CHILD2,
														  null,
														  null,
														  BusinessServiceQuery.SIGNATURE_PRESENT);
	}
	
	public static List<?> select(EntityManager em, FindQualifiers fq, CategoryBag categoryBag, String parentKey, List<?> keysIn) {
		if (parentKey != null && parentKey.length() > 0) {
			DynamicQuery.Parameter param = new DynamicQuery.Parameter(BusinessServiceQuery.ENTITY_ALIAS + "." + BusinessServiceQuery.KEY_NAME_PARENT, parentKey, DynamicQuery.PREDICATE_EQUALS); 
			return findQuery.select(em, fq, categoryBag, keysIn, param);
		}
		else
			return findQuery.select(em, fq, categoryBag, keysIn);
	}
}
