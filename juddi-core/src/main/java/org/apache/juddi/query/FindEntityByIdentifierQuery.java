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

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.config.Constants;
import org.apache.juddi.query.util.DynamicQuery;
import org.apache.juddi.query.util.FindQualifiers;
import org.apache.juddi.query.util.KeyedRefTModelComparator;
import org.uddi.api_v3.IdentifierBag;
import org.uddi.api_v3.KeyedReference;

/**
 * Returns the list of "entity" keys possessing the keyedReferences in the passed identifier bag.
 * Output is restricted by list of "entity" keys passed in.  If null, all entities are searched.
 * Output is produced by building the appropriate JPA query based on input and find qualifiers.
 * 
 * NOTES:
 * 1) Identifiers are grouped with a logical OR by default.
 * 2) In the case that the "andAllKeys" find qualifier is used the identifiers are AND'd together.  The only way this can be done
 *    with a single query was to create a self-join for each identifier.  If there are a lot of identifiers, the performance could suffer.
 *    TODO:  Test performance with multiple AND'd identifiers.  If too slow, look to process this query in multiple steps.
 * 3) The "orLikeKeys" qualifier complicates matters.  The "like" keys are OR'd together and these groups of "like" keys are AND'd together.
 *    As with "andAllKeys", self-joins are created but only one for each group of "like" keys.  If none of the keyedReferences passed are alike then this
 *    will reduce to an "andAllKeys" query.  If all are alike, then this will query will exhibit the default behavior of OR'ing all keys.
 * 
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class FindEntityByIdentifierQuery extends EntityQuery {
	
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(FindEntityByIdentifierQuery.class);

	private String entityName;
	private String entityAlias;
	private String keyName;
	private String entityField;
	private String entityNameChild;
	private String entityAliasChild;
	private String selectSQL;
	private String signaturePresent;

	public FindEntityByIdentifierQuery(String entityName, String entityAlias, String keyName, 
			String entityField, String entityNameChild, String signaturePresent) {
		this.entityName = entityName;
		this.entityAlias = entityAlias;
		this.keyName = keyName;
		this.entityField = entityField;
		this.entityNameChild = entityNameChild;
		this.entityAliasChild = buildAlias(entityNameChild);
		this.signaturePresent = signaturePresent;
		
		StringBuffer sql = new StringBuffer(200);
		sql.append("select distinct " + entityAlias + "." + keyName + " from " + entityName + " " + entityAlias + " ");
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

	
	public List<?> select(EntityManager em, FindQualifiers fq, IdentifierBag identifiers, List<?> keysIn, DynamicQuery.Parameter... restrictions) {
		// If keysIn is not null and empty, then search is over.
		if ((keysIn != null) && (keysIn.size() == 0))
			return keysIn;
		
		if (identifiers == null)
			return keysIn;
		
		List<KeyedReference> keyedRefs = identifiers.getKeyedReference();
		if (keyedRefs == null || keyedRefs.size() == 0)
			return keysIn;
		
		DynamicQuery dynamicQry = new DynamicQuery(selectSQL);
		appendConditions(dynamicQry, fq, keyedRefs);
		if (restrictions != null && restrictions.length > 0)
			dynamicQry.AND().pad().appendGroupedAnd(restrictions);

		return getQueryResult(em, dynamicQry, keysIn, entityAlias + "." + keyName);
	}
	
	
	/*
	 * Appends the conditions to the query based on the keyedReference list.  With the default or when "andAllKeys" is passed, the keyedReferences are autonomous and are
	 * all OR'd or AND'd respectively.  However, "orLikeKeys" requires special treatment.  The goal is to create the conditions in this format:
	 * 
	 * (likeKey1 = X or likeKey1 = Y) and (likeKey2 = A or likeKey2 = B or likeKey2 = C) 
	 * 
	 * ie. the "like" KeyedReferences are OR'd and the groups of "like" KeyedReferences are AND'd with each other.
	 */
	public void appendConditions(DynamicQuery qry, FindQualifiers fq, List<KeyedReference> keyedRefs) {
		
		// Append the necessary tables (one will always be added connecting the entity to its identifier table).
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
			
			String keyValueTerm = (fq.isAndAllKeys()||fq.isOrLikeKeys()?entityAliasChild + tblCount:entityAliasChild + "0") + ".keyValue";
			String keyNameTerm = (fq.isAndAllKeys()||fq.isOrLikeKeys()?entityAliasChild + tblCount:entityAliasChild + "0") + ".keyName";
			String tmodelKeyTerm = (fq.isAndAllKeys()||fq.isOrLikeKeys()?entityAliasChild + tblCount:entityAliasChild + "0") + ".tmodelKeyRef";
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
				if (fq.isAndAllKeys())
					qry.AND().pad();
				else if (fq.isOrLikeKeys()) {
				}
				else
					qry.OR().pad();
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
					if (fq.isOrLikeKeys() && curTModelKey.equals(prevTModelKey)) {
						// Do nothing
					}
					else {
						tblCount++;
						qry.comma().pad().append(entityNameChild + " " + entityAliasChild + tblCount).pad();
						thetaJoins.append(entityAliasChild + (tblCount - 1) + "." + entityField + "." + keyName + " = " + entityAliasChild + tblCount + "." + entityField + "." + keyName + " ");
						thetaJoins.append(DynamicQuery.OPERATOR_AND + " ");
					}

				}
				else {
					qry.comma().pad().append(entityNameChild + " " + entityAliasChild + tblCount).pad();
					thetaJoins.append(entityAlias + "." + keyName + " = " + entityAliasChild + tblCount + "." + entityField + "." + keyName + " ");
					thetaJoins.append(DynamicQuery.OPERATOR_AND + " ");
				}
				prevTModelKey = curTModelKey;
				count++;
			}
			
			qry.WHERE().pad().openParen().pad();

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
