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
import org.apache.log4j.Logger;
import org.uddi.api_v3.TModelBag;

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
public class FindBindingByTModelKeyQuery extends BindingTemplateQuery {
	
	private static Logger log = Logger.getLogger(FindBindingByTModelKeyQuery.class);

	public static final String ENTITY_NAME_CHILD = "TmodelInstanceInfo";

	protected static String entityAliasChild;;
	
	static {
		entityAliasChild = buildAlias(ENTITY_NAME_CHILD);
	}

	public static List<?> select(EntityManager em, FindQualifiers fq, TModelBag tModels, String parentKey, List<?> keysIn, DynamicQuery.Parameter... restrictions) {
		// If keysIn is not null and empty, then search is over.
		if ((keysIn != null) && (keysIn.size() == 0))
			return keysIn;
		
		if (tModels == null)
			return keysIn;
		
		List<String> tmodelKeys = tModels.getTModelKey();
		if (tmodelKeys == null || tmodelKeys.size() == 0)
			return keysIn;
		
		DynamicQuery dynamicQry = new DynamicQuery(selectSQL);
		appendConditions(dynamicQry, fq, tmodelKeys);
		if (parentKey != null && parentKey.length() > 0)
			dynamicQry.AND().pad().appendGroupedAnd(new DynamicQuery.Parameter(BindingTemplateQuery.ENTITY_ALIAS + "." + BindingTemplateQuery.KEY_NAME_PARENT, parentKey, DynamicQuery.PREDICATE_EQUALS));
		
		if (restrictions != null && restrictions.length > 0)
			dynamicQry.AND().pad().appendGroupedAnd(restrictions);
		
		return getQueryResult(em, dynamicQry, keysIn, ENTITY_ALIAS + "." + KEY_NAME);
	}
	
	
	/*
	 * Appends the conditions to the query based on the tModelKey list.  With the default or when "orAllKeys" is passed, the keyedReferences are autonomous and are
	 * all AND'd or OR'd respectively.  
	 *	 
	 */
	public static void appendConditions(DynamicQuery qry, FindQualifiers fq, List<String> tmodelKeys) {
		
		// Append the necessary tables (one will always be added connecting the entity to its instanceinfo table).
		appendJoinTables(qry, fq, tmodelKeys);
		qry.AND().pad().openParen().pad();
		
		int count = 0;
		int tblCount = -1;
		for(String tmodelKey : tmodelKeys) {
			
			tblCount++;
			String tmodelKeyTerm = (fq.isOrAllKeys()?entityAliasChild + "0":entityAliasChild + tblCount) + ".tmodelKey";
			qry.appendGroupedAnd(new DynamicQuery.Parameter(tmodelKeyTerm, tmodelKey, DynamicQuery.PREDICATE_EQUALS));
			
			if (count + 1 < tmodelKeys.size()) {
				if (fq.isOrAllKeys())
					qry.OR().pad();
				else
					qry.AND().pad();
			}
			
			count++;
		}
		qry.closeParen().pad();
		
	}
	
	/*
	 * Appends the necessary join table for the child entity and additional tables for when keys are AND'd.  This is the default behavior 
	 * so only need to add additional tables if "orAllKeys" has not been set.
	 */
	public static void appendJoinTables(DynamicQuery qry, FindQualifiers fq, List<String> tmodelKeys) {
		
		if (tmodelKeys != null & tmodelKeys.size() > 0) {

			StringBuffer thetaJoins = new StringBuffer(200);
			int tblCount = 0;
			for(int count = 0; count < tmodelKeys.size(); count++) {
				if (count != 0) {
					if (!fq.isOrAllKeys()) {
						tblCount++;
						qry.comma().pad().append(ENTITY_NAME_CHILD + " " + entityAliasChild + tblCount).pad();
						thetaJoins.append(entityAliasChild + (tblCount - 1) + ".id." + KEY_NAME + " = " + entityAliasChild + tblCount + ".id." + KEY_NAME + " ");
						thetaJoins.append(DynamicQuery.OPERATOR_AND + " ");
					}
				}
				else {
					qry.comma().pad().append(ENTITY_NAME_CHILD + " " + entityAliasChild + tblCount).pad();
					thetaJoins.append(ENTITY_ALIAS + "." + KEY_NAME + " = " + entityAliasChild + tblCount + ".id." + KEY_NAME + " ");
					thetaJoins.append(DynamicQuery.OPERATOR_AND + " ");
				}
			}
			
			qry.WHERE().pad().openParen().pad();

			String thetaJoinsStr = thetaJoins.toString();
			if (thetaJoinsStr.endsWith(DynamicQuery.OPERATOR_AND + " "))
				thetaJoinsStr = thetaJoinsStr.substring(0, thetaJoinsStr.length() - (DynamicQuery.OPERATOR_AND + " ").length());
			qry.append(thetaJoinsStr);

			qry.closeParen().pad();
		}
	}
	
}
