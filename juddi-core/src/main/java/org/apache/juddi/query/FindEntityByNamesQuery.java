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
import org.uddi.api_v3.Name;

/**
 * Returns the list of "entity" keys possessing the Names in the passed Name list.
 * Output is restricted by list of "entity" keys passed in.  If null, all entities are searched.
 * Output is produced by building the appropriate JPA query based on input and find qualifiers.
 * 
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class FindEntityByNamesQuery extends EntityQuery {

	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(FindEntityByNamesQuery.class);

	private final String entityName;
	private final String entityAlias;
	private final String keyName;
	private final String entityField;
	private final String entityNameChild;
	private final String entityAliasChild;
	private final String selectSQL;
	private String signaturePresent;
	
	public FindEntityByNamesQuery(String entityName, String entityAlias, String keyName, String entityField, 
			String entityNameChild, String signaturePresent) {
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
	
	public List<Object> select(EntityManager em, FindQualifiers fq, List<Name> names, List<Object> keysIn, DynamicQuery.Parameter... restrictions) {
		// If keysIn is not null and empty, then search is over.
		if ((keysIn != null) && (keysIn.size() == 0))
			return keysIn;
		
		if (names == null || names.size() == 0)
			return keysIn;
		
		DynamicQuery dynamicQry = new DynamicQuery(selectSQL);
		appendConditions(dynamicQry, fq, names);
		if (restrictions != null && restrictions.length > 0)
			dynamicQry.AND().pad().appendGroupedAnd(restrictions);

		return getQueryResult(em, dynamicQry, keysIn, entityAlias + "." + keyName);
	}
	
	/*
	 * Appends the conditions to the query based on the name list.  	 
	 */
	public void appendConditions(DynamicQuery qry, FindQualifiers fq, List<Name> names) {

		// Append the necessary tables (one will always be added connecting the entity to its name table).
		appendJoinTables(qry, fq, names);
		qry.AND().pad().openParen().pad();
		
		String namePredicate = DynamicQuery.PREDICATE_EQUALS;
		if (fq.isApproximateMatch()) {
			namePredicate = DynamicQuery.PREDICATE_LIKE;
		}
		
		int count = 0;
		for(Name n : names) {
			String nameTerm = entityAliasChild + ".name";
			String nameValue = n.getValue();
			if (fq.isCaseInsensitiveMatch()) {
				nameTerm = "upper(" + entityAliasChild + ".name)";
				nameValue = n.getValue().toUpperCase();
			}
			// JUDDI-235: wildcards are provided by user (only commenting in case a new interpretation arises)
			//if (fq.isApproximateMatch())
			//	nameValue = nameValue.endsWith(DynamicQuery.WILDCARD)?nameValue:nameValue + DynamicQuery.WILDCARD;
			
			if (n.getLang() == null || n.getLang().length() == 0 ) {
				qry.appendGroupedAnd(new DynamicQuery.Parameter(nameTerm, nameValue, namePredicate));
			}
			else {
				// Per spec, the language argument is always wildcarded and case insensitive
				String langValue = n.getLang().endsWith(DynamicQuery.WILDCARD)?n.getLang().toUpperCase():n.getLang().toUpperCase() + DynamicQuery.WILDCARD;
				qry.appendGroupedAnd(new DynamicQuery.Parameter(nameTerm, nameValue, namePredicate), 
									 new DynamicQuery.Parameter("upper(" + entityAliasChild + ".langCode)", langValue, DynamicQuery.PREDICATE_LIKE));
			}
			
			if (count + 1 < names.size())
				qry.OR().pad();
			
			count++;
		}
		qry.closeParen().pad();
		
	}
	
	/*
	 * Appends the necessary join table for the child entity 
	 */
	public void appendJoinTables(DynamicQuery qry, FindQualifiers fq, List<Name> names) {
		qry.comma().pad().append(entityNameChild + " " + entityAliasChild).pad();
		qry.WHERE().pad().openParen().pad();
		qry.append(entityAlias + "." + keyName + " = " + entityAliasChild + "." + entityField + "." + keyName + " ");
		qry.closeParen().pad();
		if (fq!=null && fq.isSignaturePresent()) {
			qry.AND().pad().openParen().pad().append(getSignaturePresent()).pad().closeParen().pad();
		}
	}


	
}
