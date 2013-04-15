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
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.config.Constants;
import org.apache.juddi.query.util.DynamicQuery;
import org.apache.juddi.query.util.FindQualifiers;
import org.apache.juddi.query.util.KeyedRefTModelComparator;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.KeyedReference;

/**
 * Returns the list of "entity" keys possessing the keyedReferences in the passed category bag.
 * Output is restricted by list of "entity" keys passed in.  If null, all entities are searched.
 * Output is produced by building the appropriate JPA query based on input and find qualifiers.
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
 */
public class FindEntityByCategoryQuery extends EntityQuery {
	
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(FindEntityByCategoryQuery.class);

	private static final String ENTITY_KEYEDREFERENCE = "KeyedReference";
	private static final String ALIAS_KEYEDREFERENCE = buildAlias(ENTITY_KEYEDREFERENCE);
	private static final String FIELD_CATEGORYBAG = "categoryBag";
	
	protected String entityName;
	protected String entityAlias;
	protected String keyName;
	protected String entityField;
	protected String entityNameChild;
	protected String entityAliasChild;
	protected String selectSQL;
	protected String signaturePresent;

	public FindEntityByCategoryQuery(String entityName, String entityAlias, String keyName,
			String entityField, String entityNameChild, String signaturePresent) {
		this.entityName = entityName;
		this.entityAlias = entityAlias;
		this.keyName = keyName;
		this.entityField = entityField;
		this.entityNameChild = entityNameChild;
		this.entityAliasChild = buildAlias(entityNameChild);
		this.signaturePresent = signaturePresent;
		
		StringBuffer sql = new StringBuffer(200);
		sql.append("select distinct " + entityAlias + "." + keyName + " from " + entityName + " " + entityAlias + " , " + entityNameChild + " " + entityAliasChild + " ");
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
	
	public List<?> select(EntityManager em, FindQualifiers fq, CategoryBag categoryBag, List<?> keysIn, DynamicQuery.Parameter... restrictions) {
		// If keysIn is not null and empty, then search is over.
		if ((keysIn != null) && (keysIn.size() == 0))
			return keysIn;
		
		if (categoryBag == null)
			return keysIn;
		
		List<KeyedReference> categories = categoryBag.getKeyedReference();
		if (categories == null || categories.size() == 0)
			return keysIn;
		
		List<KeyedReference> keyedRefs = new ArrayList<KeyedReference>(0);
		for (KeyedReference elem : categories) {
			if (elem instanceof KeyedReference)
				keyedRefs.add((KeyedReference)elem);
		}
		if (keyedRefs.size() == 0)
			return keysIn;		
		
		DynamicQuery dynamicQry = new DynamicQuery(selectSQL);
		appendConditions(dynamicQry, fq, keyedRefs);
		if (restrictions != null && restrictions.length > 0)
			dynamicQry.AND().pad().appendGroupedAnd(restrictions);

		return getQueryResult(em, dynamicQry, keysIn, entityAlias + "." + keyName);
	}
	
	
	/*
	 * Appends the conditions to the query based on the keyedReference list.  With the default or when "orAllKeys" is passed, the keyedReferences are autonomous and are
	 * all AND'd or OR'd respectively.  However, "orLikeKeys" requires special treatment.  The goal is to create the conditions in this format:
	 * 
	 * (likeKey1 = X or likeKey1 = Y) and (likeKey2 = A or likeKey2 = B or likeKey2 = C) 
	 * 
	 * ie. the "like" KeyedReferences are OR'd and the groups of "like" KeyedReferences are AND'd with each other.
	 */
	public void appendConditions(DynamicQuery qry, FindQualifiers fq, List<KeyedReference> keyedRefs) {
		
		// Append the necessary tables (two will always be added connecting the entity to its category table and then the category table to the keyed references).
		appendJoinTables(qry, fq, keyedRefs);
		qry.AND().pad().openParen().pad();
		
		String predicate = DynamicQuery.PREDICATE_EQUALS;
		if (fq.isApproximateMatch()) {
			predicate = DynamicQuery.PREDICATE_LIKE;
		}
		
		// Sorting the collection by tModel Key
		Collections.sort(keyedRefs, new KeyedRefTModelComparator());

		String prevTModelKey = null;
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

			// Either opening up (and AND'ing) a new "group" of like keys or simply appending an "or".  If this is not "orLikeKeys", then just need to increment
			// the table count.
			if (fq.isOrLikeKeys()) {
				if (count == 0) {
					qry.openParen().pad();
					tblCount++;
				}
				else {
					if (!tmodelKey.equals(prevTModelKey)) {
						qry.closeParen().pad().AND().pad().openParen().pad();
						tblCount++;
					}
					else
						qry.OR().pad();
				}
			}
			else
				tblCount++;
			
			String keyValueTerm = (fq.isOrAllKeys()?ALIAS_KEYEDREFERENCE + "0":ALIAS_KEYEDREFERENCE + tblCount) + ".keyValue";
			String keyNameTerm = (fq.isOrAllKeys()?ALIAS_KEYEDREFERENCE + "0":ALIAS_KEYEDREFERENCE + tblCount) + ".keyName";
			String tmodelKeyTerm = (fq.isOrAllKeys()?ALIAS_KEYEDREFERENCE + "0":ALIAS_KEYEDREFERENCE + tblCount) + ".tmodelKeyRef";
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
			
			if (count + 1 < keyedRefs.size()) {
				if (fq.isOrAllKeys())
					qry.OR().pad();
				else if (fq.isOrLikeKeys()) {
				}
				else
					qry.AND().pad();
			}
			
			// The "orLikeKeys" will always leave an unclosed parenthesis.  This will close it.
			if (fq.isOrLikeKeys() && (count + 1 == keyedRefs.size()))
				qry.closeParen().pad();

			prevTModelKey = tmodelKey;
			count++;
		}
		qry.closeParen().pad();
		
	}

	
	
	/*
	 * Appends the necessary join table for the child entity and additional tables for when keys are AND'd.  When "orLikeKeys" is used, 
	 * we only need an extra table for each distinct tmodelKey.
	 */
	public void appendJoinTables(DynamicQuery qry, FindQualifiers fq, List<KeyedReference> keyedRefs) {
		
		if (keyedRefs != null && keyedRefs.size() > 0) {
			// Sorting the collection by tModel Key
			Collections.sort(keyedRefs, new KeyedRefTModelComparator());

			StringBuffer thetaJoins = new StringBuffer(200);
			int tblCount = 0;
			int count = 0;
			String curTModelKey = null;
			String prevTModelKey = null;
			for(KeyedReference kr : keyedRefs) {
				curTModelKey = kr.getTModelKey();
				if (count != 0) {
					if (!fq.isOrAllKeys()) {
						if (fq.isOrLikeKeys() && curTModelKey.equals(prevTModelKey)) {
							// Do nothing
						}
						else {
							tblCount++;
							qry.comma().pad().append(ENTITY_KEYEDREFERENCE + " " + ALIAS_KEYEDREFERENCE + tblCount).pad();
							thetaJoins.append(ALIAS_KEYEDREFERENCE + (tblCount - 1) + "." + FIELD_CATEGORYBAG + ".id = " + ALIAS_KEYEDREFERENCE + tblCount + "." + FIELD_CATEGORYBAG + ".id ");
							thetaJoins.append(DynamicQuery.OPERATOR_AND + " ");
						}
					}

				}
				else {
					qry.comma().pad().append(ENTITY_KEYEDREFERENCE + " " + ALIAS_KEYEDREFERENCE + tblCount).pad();
					thetaJoins.append(entityAliasChild + ".id = " + ALIAS_KEYEDREFERENCE + tblCount + "." + FIELD_CATEGORYBAG + ".id ");
					thetaJoins.append(DynamicQuery.OPERATOR_AND + " ");
				}
				prevTModelKey = curTModelKey;
				count++;
			}
			
			qry.WHERE().pad().openParen().pad();
			
			// Appending the middling entity-specific category table condition
			qry.append(entityAlias + "." + keyName + " = " + entityAliasChild + "." + entityField + "." + KEY_NAME).pad();
			qry.AND().pad();

			String thetaJoinsStr = thetaJoins.toString();
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
