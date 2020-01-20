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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.query.util.DynamicQuery;
import org.apache.juddi.query.util.FindQualifiers;
import org.uddi.api_v3.TModelBag;

/**
 * Returns the list of binding keys possessing the tModels in the passed tModelBag.
 * Output is restricted by list of binding keys passed in.  If null, all bindings are searched.
 * Output is produced by building the appropriate JPA query based on input and find qualifiers.
 * 
 * From specification:
 * "This collection of tModelKey elements represent in part or in whole the technical fingerprint of the bindingTemplate 
 * structures for which the search is being performed. At least one of either a tModelBag or a find_tModel argument SHOULD be 
 * supplied, unless a categoryBag based search is being used.  

 * If a find_tModel argument is specified (see above), it is treated as an embedded inquiry.  The tModelKeys returned as a 
 * result of this embedded find_tModel argument are used as if they had been 
 * supplied in a tModelBag argument. Changing the order of the keys in the collection or specifying the same tModelKey more than 
 * once does not change the behavior of the find.  
 * 
 * By default, only bindingTemplates that have a technical fingerprint containing 
 * all of the supplied tModelKeys match (logical AND). Specifying appropriate findQualifiers can override this behavior so that 
 * bindingTemplates with a technical fingerprint containing any of the specified tModelKeys are returned (logical OR)." 
 *  
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class FindBindingByTModelKeyQuery extends BindingTemplateQuery {
	
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(FindBindingByTModelKeyQuery.class);

	public static final String ENTITY_NAME_CHILD = "TmodelInstanceInfo";
	
	protected static final String entityAliasChild;
	
	static {
		entityAliasChild = buildAlias(ENTITY_NAME_CHILD);
	}

	public static List<Object> select(EntityManager em, FindQualifiers fq, TModelBag tModels, String parentKey, 
			List<Object> keysIn, DynamicQuery.Parameter... restrictions) {
		// If keysIn is not null and empty, then search is over.
		//if ((keysIn != null) && (keysIn.size() == 0))
		//	return keysIn;
		
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
		
		if (tmodelKeys != null && tmodelKeys.size() > 0) {

			StringBuffer thetaJoins = new StringBuffer(200);
			int tblCount = 0;
			for(int count = 0; count < tmodelKeys.size(); count++) {
				if (count != 0) {
					if (!fq.isOrAllKeys()) {
						tblCount++;
						qry.comma().pad().append(ENTITY_NAME_CHILD + " " + entityAliasChild + tblCount).pad();
						thetaJoins.append(entityAliasChild + (tblCount - 1) + "." + ENTITY_FIELD + "." + KEY_NAME + " = " + entityAliasChild + tblCount + "." + ENTITY_FIELD + "." + KEY_NAME + " ");
						thetaJoins.append(DynamicQuery.OPERATOR_AND + " ");
					}
				}
				else {
					qry.comma().pad().append(ENTITY_NAME_CHILD + " " + entityAliasChild + tblCount).pad();
					thetaJoins.append(ENTITY_ALIAS + "." + KEY_NAME + " = " + entityAliasChild + tblCount + "." + ENTITY_FIELD + "." + KEY_NAME + " ");
					thetaJoins.append(DynamicQuery.OPERATOR_AND + " ");
				}
			}
			
			qry.WHERE().pad().openParen().pad();

			String thetaJoinsStr = thetaJoins.toString();
			if (thetaJoinsStr.endsWith(DynamicQuery.OPERATOR_AND + " "))
				thetaJoinsStr = thetaJoinsStr.substring(0, thetaJoinsStr.length() - (DynamicQuery.OPERATOR_AND + " ").length());
			qry.append(thetaJoinsStr);

			qry.closeParen().pad();
			if (fq!=null && fq.isSignaturePresent()) {
				qry.AND().pad().openParen().pad().append(BindingTemplateQuery.SIGNATURE_PRESENT).pad().closeParen().pad();
			}
		}
	}
	
}
