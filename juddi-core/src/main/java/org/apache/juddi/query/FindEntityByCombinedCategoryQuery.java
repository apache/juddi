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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.query.util.DynamicQuery;
import org.apache.juddi.query.util.FindQualifiers;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.KeyedReference;

/**
 * Returns the list of "entity" keys possessing the keyedReferences passed in or the level below.
 * For findBusiness queries, this means keyedReferences on the businessEntity and service levels,
 * and for findService queries this means keyedReferences on the service and serviceBinding level.
 * 
 * Output is restricted by list of "entity" keys passed in.  If null, all entities are searched.
 * Output is produced by building the appropriate JPA query based on input and find qualifiers.
 * 
 * From the spec : 
 * 
 *  * From specification:
 * "combineCategoryBags:  this may only be used in the find_business and find_service calls.  In the case of 
 * find_business, this qualifier makes the categoryBag entries for the full businessEntity element behave as though all 
 * categoryBag elements found at the businessEntity level and in all contained or referenced businessService elements 
 * and bindingTemplate elements were combined.  Searching for a category will yield a positive match on a registered 
 * business if any of the categoryBag elements contained within the full businessEntity element (including the 
 * categoryBag elements within contained or referenced businessService elements or bindingTemplate elements) 
 * contains the filter criteria.  In the case of find_service, this qualifier makes the categoryBag entries 
 * for the full businessService element behave as though all categoryBag elements found at the businessService level 
 * and in all contained or referenced elements in the bindingTemplate elements were combined.  Searching for a category 
 * will yield a positive match on a registered service if any of the categoryBag elements contained within the 
 * full businessService element (including the categoryBag elements within contained or referenced bindingTemplate 
 * elements) contains the filter criteria. This find qualifier does not cause the keyedReferences in categoryBags 
 * to be combined with the keyedReferences in keyedReferenceGroups in categoryBags when performing the comparison.  
 * The keyedReferences are combined with each other, and the keyedReferenceGroups are combined with each other."
 * 
 * 
 * NOTES:
 * 1) Categories are grouped with a logical AND by default.
 * 2) Concerning when the categories are AND'd together - the only way this can be done with a single query was to create a self-join for 
 *    each category.  If there are a lot of categories, the performance could suffer.
 *    TODO:  Test performance with multiple AND'd categories.  If too slow, look to process this query in multiple steps.
 * 3) The "orLikeKeys" qualifier complicates matters.  The "like" keys are OR'd together and these groups of "like" keys are AND'd together.
 *    As with "andAllKeys", self-joins are created but only one for each group of "like" keys.  If none of the keyedReferences passed are alike then this
 *    will reduce to an "andAllKeys" query.  If all are alike, then this will query will exhibit the behavior of OR'ing all keys.
 * 
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 * @author <a href="mailto:kstam@apache.org">Kurt Stam</a>
 */
public class FindEntityByCombinedCategoryQuery extends FindEntityByCategoryQuery {
	
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(FindEntityByCombinedCategoryQuery.class);
	
	protected String entityField2;
	protected String entityNameChild2;
	protected String entityAliasChild2;
	
	protected String entityField3;
	protected String entityNameChild3;
	protected String entityAliasChild3;
	
	protected String signaturePresent;

	public FindEntityByCombinedCategoryQuery(String entityName, String entityAlias, String keyName,
			String entityField, String entityNameChild, String signaturePresent) {
		super(entityName, entityAlias, keyName, entityField, entityNameChild, signaturePresent);
	}
	
	public FindEntityByCombinedCategoryQuery(String entityName, String entityAlias, String keyName, 
			String entityField, String entityNameChild,
			String entityField2, String entityNameChild2, String entityField3, String entityNameChild3,
			String signaturePresent) {
		super(entityName, entityAlias, keyName, entityField, entityNameChild, signaturePresent);
		
		this.entityNameChild2 = entityNameChild2;
		this.entityAliasChild2 = buildAlias(entityNameChild2);
		this.entityField2 = entityField2;
		if (entityNameChild3!=null) {
			this.entityField3 = entityField3;
			this.entityNameChild3 = entityNameChild3;
			this.entityAliasChild3 = buildAlias(entityNameChild3);
		}
		this.signaturePresent = signaturePresent;
		selectSQL = "";
	}
	
	public String getEntityNameChild2() {
		return entityNameChild2;
	}
	
	public String getEntityAliasChild2() {
		return entityAliasChild2;
	}
	
	public String getEntityNameChild3() {
		return entityNameChild3;
	}
	
	public String getEntityAliasChild3() {
		return entityAliasChild3;
	}
		
	public List<?> select(EntityManager em, FindQualifiers fq, CategoryBag categoryBag, 
			List<?> keysIn, DynamicQuery.Parameter... restrictions) {
	        
        // If keysIn is not null and empty, then search is over.
		if ((keysIn != null) && (keysIn.size() == 0))
			return keysIn;
		
		if (categoryBag == null)
			return keysIn;
		
		List<KeyedReference> keyRefsInCategories = categoryBag.getKeyedReference();
		if (keyRefsInCategories == null || keyRefsInCategories.size() == 0)
			return keysIn;
		
		Map<KeyedReference,Set<String>> map  = new HashMap<KeyedReference,Set<String>>();
		//1. First match at the top level (i.e. categoryBag on business)
		findEntityByCategoryQuery(map, em, fq, categoryBag, entityField, entityNameChild, keysIn, restrictions);
		//2. Now match at the second level (i.e. categoryBag on services for businesses)
		findEntityByCategoryQuery(map, em, fq, categoryBag, entityField2, entityNameChild2, keysIn, restrictions);
		//3. Now match at the third level (i.e. categoryBag on binding for businesses)
		//   This does only apply to businesses (not for services)
		if (entityNameChild3!=null) {
			findEntityByCategoryQuery(map, em, fq, categoryBag, entityField3, entityNameChild3, keysIn, restrictions);
		}
		
		//Now build the results taking into account AND/OR/LIKE
		Set<String> resultingEntityKeys = new HashSet<String>();
		if (fq.isOrAllKeys()) {
			//in this case get ALL businessKeys
			for (KeyedReference keyRef: map.keySet()) {
				resultingEntityKeys.addAll(map.get(keyRef));
			}
		} else if (fq.isOrLikeKeys()) {
			// any keyedReference filters that come from the same namespace (e.g. have the same tModelKey value) 
			// are OR’d together rather than AND’d
			// 1. OR if we have keys with similar namespaces (keyValue)
			Map<String,Set<String>> likeMap  = new HashMap<String,Set<String>>();
			for (KeyedReference keyRef: map.keySet()) {
				String keyValue = keyRef.getKeyValue();
				if (likeMap.containsKey(keyValue)) {
					likeMap.get(keyValue).addAll(map.get(keyRef));
				} else {
					likeMap.put(keyValue, map.get(keyRef));
				}
			}
			// 2. Now AND the likeMap
			boolean firstTime = true;
			for (String keyValue: likeMap.keySet()) {
				if (firstTime) {
					resultingEntityKeys = map.get(keyValue);
					firstTime = false;
				} else {
					resultingEntityKeys.retainAll(map.get(keyValue));
				}
			}
		} else {
			// AND keys by default, in this case each entity (business or service)
			// needs to have ALL keys
			boolean firstTime = true;
			for (KeyedReference keyRef: map.keySet()) {
				if (firstTime) {
					resultingEntityKeys = map.get(keyRef);
					firstTime = false;
				} else {
					resultingEntityKeys.retainAll(map.get(keyRef));
				}
			}
		}
		return new ArrayList<String>(resultingEntityKeys);
	}
	/**
	 * Finding the entities (businesses or services) that have a matching keyedReference in their
	 * categoryBag.
	 * 
	 * @param map - result map of keyedReference and matching businesses
	 * @param em
	 * @param fq
	 * @param categoryBag
	 * @param entityField
	 * @param entityNameChild
	 * @param keysIn
	 * @param restrictions
	 */
	private void findEntityByCategoryQuery(Map<KeyedReference,Set<String>> map, EntityManager em, 
			FindQualifiers fq, CategoryBag categoryBag, String entityField, String entityNameChild, 
			List<?> keysIn, DynamicQuery.Parameter... restrictions) 
	{
		FindEntityByCategoryQuery findEntityByCategoryQuery = new FindEntityByCategoryQuery(
				entityName, entityAlias, keyName, entityField, entityNameChild, signaturePresent);
		for (KeyedReference keyedReference : categoryBag.getKeyedReference()) {
			CategoryBag categoryBagWithOneKey = new CategoryBag();
			categoryBagWithOneKey.getKeyedReference().add(keyedReference);
			List<?> entityKeys =  findEntityByCategoryQuery.select(
					em, fq, categoryBagWithOneKey, keysIn, restrictions);
			@SuppressWarnings({ "unchecked", "rawtypes" })
			Set<String> keySet = new HashSet(entityKeys);
			if (map.containsKey(keyedReference)) {
				map.get(keyedReference).addAll(keySet);
			} else {
				map.put(keyedReference, keySet);
			}
		}
	}
	
}
