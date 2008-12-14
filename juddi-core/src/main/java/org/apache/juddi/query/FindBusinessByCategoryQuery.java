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
import javax.persistence.Query;
import javax.xml.bind.JAXBElement;

import org.apache.juddi.config.Constants;
import org.apache.juddi.query.util.DynamicQuery;
import org.apache.juddi.query.util.FindQualifiers;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.KeyedReferenceGroup;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class FindBusinessByCategoryQuery {
	
	private static String selectSQL;
	
	static {
		StringBuffer sql = new StringBuffer(200);
		sql.append("select bc.id.businessKey from BusinessCategory bc ");
		selectSQL = sql.toString();
	}

	public static List<?> select(FindQualifiers fq, CategoryBag categories, List<Object> keysIn, EntityManager em) {
		// If keysIn is not null and empty, then search is over.
		if ((keysIn != null) && (keysIn.size() == 0))
			return keysIn;
		
		if (categories == null)
			return keysIn;
		
		List<JAXBElement<?>> catElems = categories.getContent();
		if (catElems == null || catElems.size() == 0)
			return keysIn;
		
		DynamicQuery dynamicQry = new DynamicQuery(selectSQL);
		appendConditions(dynamicQry, fq, catElems, keysIn);
		
		System.out.println(dynamicQry);
		
		Query qry = dynamicQry.buildJPAQuery(em);
		List<?> result = qry.getResultList();
		
		return result;
	}
	
	public static void appendConditions(DynamicQuery qry, FindQualifiers fq, List<JAXBElement<?>> catElems, List<Object> keysIn) {
		String predicate = DynamicQuery.PREDICATE_EQUALS;
		if (fq.isApproximateMatch()) {
			predicate = DynamicQuery.PREDICATE_LIKE;
		}
		
		int count = 0;
		qry.WHERE().pad().openParen().pad();
		for(JAXBElement<?> elem : catElems) {
			String tmodelKey = null;
			String keyValue = null;
			String keyName = null;
			
			if (elem != null && elem.getValue() instanceof KeyedReference) {
				KeyedReference kr = (KeyedReference)elem.getValue();
				tmodelKey = kr.getTModelKey();
				keyValue = kr.getKeyValue();
				keyName = kr.getKeyName();
			}
			else if (elem != null && elem.getValue() instanceof KeyedReferenceGroup) {
				
			}
			
			if (fq.isApproximateMatch()) {
				keyValue = keyValue.endsWith(DynamicQuery.WILDCARD)?keyValue:keyValue + DynamicQuery.WILDCARD;
				keyName = keyName.endsWith(DynamicQuery.WILDCARD)?keyName:keyName + DynamicQuery.WILDCARD;
			}

			String keyValueTerm = "bc.keyValue";
			String keyNameTerm = "bc.keyName";
			if (fq.isCaseInsensitiveMatch()) {
				keyValueTerm = "upper(bc.keyValue)";
				keyValue = keyValue.toUpperCase();
				
				keyNameTerm = "upper(bc.keyName)";
				keyName = keyName.toUpperCase();
			}
			
			if (Constants.GENERAL_KEYWORD_TMODEL.equalsIgnoreCase(tmodelKey)) {
				qry.appendGroupedAnd(new DynamicQuery.Parameter("bc.tmodelKeyRef", tmodelKey, DynamicQuery.PREDICATE_EQUALS),
									 new DynamicQuery.Parameter(keyValueTerm, keyValue, predicate),
									 new DynamicQuery.Parameter(keyNameTerm, keyName, predicate));
			}
			else {
				qry.appendGroupedAnd(new DynamicQuery.Parameter("bc.tmodelKeyRef", tmodelKey, DynamicQuery.PREDICATE_EQUALS),
									 new DynamicQuery.Parameter(keyValueTerm, keyValue, predicate));
				
			}
			
			if (count + 1 < catElems.size()) {
				if (fq.isOrAllKeys())
					qry.OR().pad();
				else
					qry.AND().pad();
			}
			
			count++;
		}
		qry.closeParen().pad();
		
		qry.appendInListWithAnd("bc.id.businessKey", keysIn);
	}
	
	public static void appendGrouping(DynamicQuery qry, FindQualifiers fq, int elemCount) {
		qry.GROUPBY().pad().append("bc.id.businessKey").pad();
		
		// By default, all the "keys" switches in find qualifiers are turned off.  The default behavior for categories is to "AND" the keyed references.
		if (fq.isOrAllKeys()) {
			// do nothing
		}
		else
			qry.HAVING().pad().append("count(bc.id.businessKey >=" + elemCount).pad();
		
	}
}
