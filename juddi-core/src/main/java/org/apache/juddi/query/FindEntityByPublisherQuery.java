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
import org.apache.juddi.model.UddiEntityPublisher;

/**
 * Returns the list of "entity" keys possessing the publisher Id in the passed in UddiEntityPublisher
 * Output is restricted by list of "entity" keys passed in.  If null, all entities are searched.
 * Output is produced by building the appropriate JPA query based on input and find qualifiers.
 * 
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class FindEntityByPublisherQuery extends EntityQuery {

	public static final String AUTHORIZED_NAME_FIELD = "authorizedName";
	
	private String entityName;
	private String entityAlias;
	private String keyName;
	private String selectSQL;
	private String signaturePresent;
	
	public FindEntityByPublisherQuery(String entityName, String entityAlias, String keyName, String signaturePresent) {
		this.entityName = entityName;
		this.entityAlias = entityAlias;
		this.keyName = keyName;
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

	public String getSelectSQL() {
		return selectSQL;
	}
	
	public String getSignaturePresent() {
		return signaturePresent;
	}

	public void setSignaturePresent(String signaturePresent) {
		this.signaturePresent = signaturePresent;
	}
		
	
	public List<?> select(EntityManager em, FindQualifiers fq, UddiEntityPublisher publisher, List<?> keysIn, DynamicQuery.Parameter... restrictions) {
		// If keysIn is not null and empty, then search is over.
		if ((keysIn != null) && (keysIn.size() == 0))
			return keysIn;
		
		if (publisher == null)
			return keysIn;
		
		DynamicQuery dynamicQry = new DynamicQuery(selectSQL);
		appendConditions(dynamicQry, fq, publisher);
		if (restrictions != null && restrictions.length > 0)
			dynamicQry.AND().pad().appendGroupedAnd(restrictions);

		return getQueryResult(em, dynamicQry, keysIn, entityAlias + "." + keyName);
	}
	
	/*
	 * Appends the conditions to the query based on the publisher id
	 */
	public void appendConditions(DynamicQuery qry, FindQualifiers fq, UddiEntityPublisher publisher) {
		qry.WHERE().pad();
		if (fq!=null && fq.isSignaturePresent()) {
			qry.pad().openParen().pad().append(getSignaturePresent()).pad().closeParen().pad().AND();
		}
		qry.appendGroupedAnd(new DynamicQuery.Parameter(entityAlias + "." + AUTHORIZED_NAME_FIELD, publisher.getAuthorizedName(), DynamicQuery.PREDICATE_EQUALS));
	}


}
