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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.config.Constants;
import org.apache.juddi.query.util.DynamicQuery;
import org.apache.juddi.query.util.FindQualifiers;
import org.apache.juddi.query.util.KeyedRefGroupTModelComparator;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.KeyedReferenceGroup;

/**
 * Returns the list of "entity" keys possessing the keyedReferenceGroups in the passed category bag.
 * Output is restricted by list of "entity" keys passed in.  If null, all entities are searched.
 * Output is produced by building the appropriate JPA query based on input and find qualifiers.
 * 
 * NOTES:
 * 1) Category groups are grouped with a logical AND by default.
 * 2) Concerning when the categories are AND'd together - the only way this can be done with a single query was to create a self-join for 
 *    each category.  If there are a lot of categories, the performance could suffer.
 *    TODO:  Test performance with multiple AND'd categories.  If too slow, look to process this query in multiple steps.
 * 3) The "orLikeKeys" qualifier complicates matters.  The "like" keys are OR'd together and these groups of "like" keys are AND'd together.
 *    As with "andAllKeys", self-joins are created but only one for each group of "like" keys.  If none of the keyedReferences passed are alike then this
 *    will reduce to an "andAllKeys" query.  If all are alike, then this will query will exhibit the behavior of OR'ing all keys.
 * 
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class FindEntityByCategoryGroupQuery extends EntityQuery {
	
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(FindEntityByCategoryGroupQuery.class);

	private static final String ENTITY_KEYEDREFERENCEGROUP = "KeyedReferenceGroup";
	private static final String ALIAS_KEYEDREFERENCEGROUP = "krg";
	private static final String FIELD_CATEGORYBAG = "categoryBag";

	private static final String ENTITY_KEYEDREFERENCE = "KeyedReference";
	private static final String ALIAS_KEYEDREFERENCE = buildAlias(ENTITY_KEYEDREFERENCE);
	private static final String FIELD_KEYEDREFERENCEGROUP = "keyedReferenceGroup";
	
	private String entityName;
	private String entityAlias;
	private String keyName;
	private String entityField;
	private String entityNameChild;
	private String entityAliasChild;
	private String selectSQL;
	private String signaturePresent;

	public FindEntityByCategoryGroupQuery(String entityName, String entityAlias, String keyName, 
			String entityField, String entityNameChild, String signaturePresent) {
		this.entityName = entityName;
		this.entityAlias = entityAlias;
		this.keyName = keyName;
		this.entityField = entityField;
		this.entityNameChild = entityNameChild;
		this.entityAliasChild = buildAlias(entityNameChild);
		this.signaturePresent = signaturePresent;
		
		StringBuffer sql = new StringBuffer(200);
		sql.append("select distinct " + entityAlias + "." + keyName + " from " 
				   + entityName + " " + entityAlias + " , " 
				   + entityNameChild + " " + entityAliasChild + " , "
				   + ENTITY_KEYEDREFERENCEGROUP + " " + ALIAS_KEYEDREFERENCEGROUP + " ");
		selectSQL = sql.toString();
	}
	
	public String getEntityName() {
		return entityName;
	}

	public String getEntityAlias() {
		return entityAlias;
	}

	public String getKeyName() {
		return keyName;
	}

	public String getEntityField() {
		return entityField;
	}

	public String getEntityNameChild() {
		return entityNameChild;
	}
	
	public String getEntityAliasChild() {
		return entityAliasChild;
	}
	
	public String getSelectSQL() {
		return selectSQL;
	}
	
	public String getSignaturePresent() {
		return signaturePresent;
	}

	public void setSignaturePresent(String signaturePresent) {
		this.signaturePresent = signaturePresent;
	}
	
	@SuppressWarnings("unchecked")
	public List<?> select(EntityManager em, FindQualifiers fq, CategoryBag categoryBag, List<?> keysIn, DynamicQuery.Parameter... restrictions) {
		// If keysIn is not null and empty, then search is over.
		if ((keysIn != null) && (keysIn.size() == 0))
			return keysIn;
		
		if (categoryBag == null)
			return keysIn;
		
		List<KeyedReferenceGroup> categories = categoryBag.getKeyedReferenceGroup();
		if (categories == null || categories.size() == 0)
			return keysIn;
		
		List<KeyedReferenceGroup> keyedRefGroups = new ArrayList<KeyedReferenceGroup>(0);
		for (KeyedReferenceGroup elem : categories) {
			if (elem instanceof KeyedReferenceGroup)
				keyedRefGroups.add((KeyedReferenceGroup)elem);
		}
		if (keyedRefGroups.size() == 0)
			return keysIn;		
		
		
		Collections.sort(keyedRefGroups, new KeyedRefGroupTModelComparator());
		int count = 0;
		String prevTModelKey = null;
		Set<String> orResults = new HashSet<String>(0);
		List<?> restrictionList = keysIn;
		List<?> curResult = null;
		for (KeyedReferenceGroup keyedRefGroup : keyedRefGroups) {
			String curTModelKey = keyedRefGroup.getTModelKey();
			
			DynamicQuery dynamicQry = new DynamicQuery(selectSQL);
			appendConditions(dynamicQry, fq, keyedRefGroup);
			if (restrictions != null && restrictions.length > 0)
				dynamicQry.AND().pad().appendGroupedAnd(restrictions);
			

			if (fq.isOrLikeKeys()) {
				if (!curTModelKey.equals(prevTModelKey)) {
					if (count != 0) {
						restrictionList = new ArrayList<String>(orResults);
						orResults.clear();
					}
				}
			}
			else if (!fq.isOrAllKeys()) {
				if (count != 0)
					restrictionList = curResult;
			}

			if (restrictionList != null && restrictionList.size() == 0)
				break;
			
			curResult = getQueryResult(em, dynamicQry, restrictionList, entityAlias + "." + keyName);

			if (fq.isOrAllKeys() || fq.isOrLikeKeys()) {
				orResults.addAll((List<String>)curResult);
			}
			
			prevTModelKey = curTModelKey;
			count++;
		}
		
		List<String> result = null;
		if (fq.isOrAllKeys() || fq.isOrLikeKeys()) {
			result = new ArrayList<String>(0);
			result.addAll(orResults);
		}
		else
			result = (List<String>)curResult;
		
		return result;
		
	}

	/*
	 * Appends the conditions to the query based on the keyedReferenceGroup.  According to the specification, a keyedReference group matches if all
	 * keyedReferences within the group are a subset of the target entity's group.  Thus, 
	 */
	public void appendConditions(DynamicQuery qry, FindQualifiers fq, KeyedReferenceGroup keyedRefGroup) {
		
		// Append the necessary tables (two will always be added connecting the entity to its category bag table and then the category table to the keyed reference group).
		appendJoinTables(qry, fq, keyedRefGroup);

		// First, appending the group's tmodel key as a condition
		qry.AND().pad().appendGroupedAnd(new DynamicQuery.Parameter(ALIAS_KEYEDREFERENCEGROUP + ".tmodelKey", keyedRefGroup.getTModelKey(), DynamicQuery.PREDICATE_EQUALS));
		
		List<KeyedReference> keyedRefs = keyedRefGroup.getKeyedReference();
		if (keyedRefs != null && keyedRefs.size() > 0) {
		
			qry.AND().pad().openParen().pad();
	
			String predicate = DynamicQuery.PREDICATE_EQUALS;
			if (fq.isApproximateMatch()) {
				predicate = DynamicQuery.PREDICATE_LIKE;
			}
			
			int count = 0;
			int tblCount = -1;
			for(KeyedReference keyedRef : keyedRefs) {
				String tmodelKey = keyedRef.getTModelKey();
				String keyValue = keyedRef.getKeyValue();
				String keyName = keyedRef.getKeyName();

				if (fq.isApproximateMatch()) {
					// JUDDI-235: wildcards are provided by user (only commenting in case a new interpretation arises)
					//keyValue = keyValue.endsWith(DynamicQuery.WILDCARD)?keyValue:keyValue + DynamicQuery.WILDCARD;
					//keyName = keyName.endsWith(DynamicQuery.WILDCARD)?keyName:keyName + DynamicQuery.WILDCARD;
				}

				tblCount++;
				String keyValueTerm = ALIAS_KEYEDREFERENCE + tblCount + ".keyValue";
				String keyNameTerm = ALIAS_KEYEDREFERENCE + tblCount + ".keyName";
				String tmodelKeyTerm = ALIAS_KEYEDREFERENCE + tblCount + ".tmodelKeyRef";
				if (fq.isCaseInsensitiveMatch()) {
					keyValueTerm = "upper(" + keyValueTerm + ")";
					keyValue = keyValue.toUpperCase();
					
					keyNameTerm = "upper(" + keyNameTerm + ")";
					keyName = keyName.toUpperCase();
				}
				
				// According to specification, if the "general keyword" tmodel is used, then the keyName must be part of the query.
				if (Constants.GENERAL_KEYWORD_TMODEL.equalsIgnoreCase(tmodelKey)) {
					qry.appendGroupedAnd(new DynamicQuery.Parameter(tmodelKeyTerm, tmodelKey, DynamicQuery.PREDICATE_EQUALS),
										 new DynamicQuery.Parameter(keyValueTerm, keyValue, predicate),
										 new DynamicQuery.Parameter(keyNameTerm, keyName, predicate));
				}
				else {
					qry.appendGroupedAnd(new DynamicQuery.Parameter(tmodelKeyTerm, tmodelKey, DynamicQuery.PREDICATE_EQUALS),
										 new DynamicQuery.Parameter(keyValueTerm, keyValue, predicate));
					
				}
				
				if (count + 1 < keyedRefs.size())
					qry.AND().pad();
				
				count++;
			}
			qry.closeParen().pad();
		}
		
	}

	
	
	/*
	 * Appends the necessary join table for the child entity and additional tables for when keys are AND'd.  When "orLikeKeys" is used, 
	 * we only need an extra table for each distinct tmodelKey.
	 */
	public void appendJoinTables(DynamicQuery qry, FindQualifiers fq, KeyedReferenceGroup keyedRefGroup) {
		
		if (keyedRefGroup != null) {
			
			List<KeyedReference> keyedRefs = keyedRefGroup.getKeyedReference();
			StringBuffer thetaJoins = new StringBuffer(200);
			if (keyedRefs != null && keyedRefs.size() > 0) {
				int tblCount = 0;
				for(int count = 0; count<keyedRefs.size(); count++) {
					if (count != 0) {
						tblCount++;
						qry.comma().pad().append(ENTITY_KEYEDREFERENCE + " " + ALIAS_KEYEDREFERENCE + tblCount).pad();
						thetaJoins.append(ALIAS_KEYEDREFERENCE + (tblCount - 1) + "." + FIELD_KEYEDREFERENCEGROUP + ".id = " + ALIAS_KEYEDREFERENCE + tblCount + "." + FIELD_KEYEDREFERENCEGROUP + ".id ");
						thetaJoins.append(DynamicQuery.OPERATOR_AND + " ");
					} else {
						qry.comma().pad().append(ENTITY_KEYEDREFERENCE + " " + ALIAS_KEYEDREFERENCE + tblCount).pad();
						thetaJoins.append(ALIAS_KEYEDREFERENCEGROUP + ".id = " + ALIAS_KEYEDREFERENCE + tblCount + "." + FIELD_KEYEDREFERENCEGROUP + ".id ");
						thetaJoins.append(DynamicQuery.OPERATOR_AND + " ");
					}
				}
			}
			qry.WHERE().pad().openParen().pad();
			
			// Appending the middling entity-specific category table condition
			qry.append(entityAlias + "." + keyName + " = " + entityAliasChild + "." + entityField + "." + KEY_NAME).pad();
			qry.AND().pad();

			// Now, appending the condition that attaches the keyed reference group table
			qry.append(entityAliasChild + ".id = " + ALIAS_KEYEDREFERENCEGROUP + "." + FIELD_CATEGORYBAG + ".id").pad();

			String thetaJoinsStr = thetaJoins.toString();
			if (thetaJoinsStr != null && thetaJoinsStr.length() > 0)
				qry.AND().pad();
			
			if (thetaJoinsStr.endsWith(DynamicQuery.OPERATOR_AND + " "))
				thetaJoinsStr = thetaJoinsStr.substring(0, thetaJoinsStr.length() - (DynamicQuery.OPERATOR_AND + " ").length());
			qry.append(thetaJoinsStr);

			qry.closeParen().pad();
			if (fq!=null && fq.isSignaturePresent()) {
				qry.AND().pad().openParen().pad().append(getSignaturePresent()).pad().closeParen().pad();
			}
		}
	}

}
