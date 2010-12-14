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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.model.PublisherAssertion;
import org.apache.juddi.query.util.DynamicQuery;
import org.uddi.api_v3.CompletionStatus;

/**
 * 
 * Returns the list of PublisherAssertions that contain the input businessKeys as their from or to key
 *  * 
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class FindPublisherAssertionByBusinessQuery extends PublisherAssertionQuery {

	private static Log log = LogFactory.getLog(FindPublisherAssertionByBusinessQuery.class);

	@SuppressWarnings("unchecked")
	public static List<PublisherAssertion> select(EntityManager em, List<?> businessKeys, CompletionStatus completionStatus) {
		if ((businessKeys == null) || (businessKeys.size() == 0))
			return null;
		
		DynamicQuery dynamicQry = new DynamicQuery(selectSQL);
		appendConditions(dynamicQry, businessKeys, completionStatus);
		
		log.debug(dynamicQry);
		
		Query qry = dynamicQry.buildJPAQuery(em);
		List<PublisherAssertion> result = qry.getResultList();
		
		return result;
	}
	
	/*
	 * Appends the conditions to the query based on the businessKey list.  The keys can either be in the fromKey or toKey of the publisher assertion.
	 */
	public static void appendConditions(DynamicQuery qry, List<?> businessKeys, CompletionStatus completionStatus) {
		
		qry.WHERE().pad().openParen().pad();
		
		qry.appendInList(ENTITY_ALIAS + "." + FROM_KEY_NAME, businessKeys);
		qry.pad().OR().pad();
		qry.appendInList(ENTITY_ALIAS + "." + TO_KEY_NAME, businessKeys);
		qry.closeParen().pad();
		
		if (completionStatus != null) {
			qry.AND().pad().openParen().pad();
			
			String fromCheckTerm = "UPPER(" + ENTITY_ALIAS + ".fromCheck)";
			String toCheckTerm = "UPPER(" + ENTITY_ALIAS + ".toCheck)";
			if (completionStatus == CompletionStatus.STATUS_BOTH_INCOMPLETE) {
				qry.appendGroupedAnd(new DynamicQuery.Parameter(fromCheckTerm, "TRUE", DynamicQuery.PREDICATE_NOTEQUALS));
				qry.AND().pad();
				qry.appendGroupedAnd(new DynamicQuery.Parameter(toCheckTerm, "TRUE", DynamicQuery.PREDICATE_NOTEQUALS));
			}
			else if (completionStatus == CompletionStatus.STATUS_COMPLETE) {
				qry.appendGroupedAnd(new DynamicQuery.Parameter(fromCheckTerm, "TRUE", DynamicQuery.PREDICATE_EQUALS));
				qry.AND().pad();
				qry.appendGroupedAnd(new DynamicQuery.Parameter(toCheckTerm, "TRUE", DynamicQuery.PREDICATE_EQUALS));
			}
			else if (completionStatus == CompletionStatus.STATUS_FROM_KEY_INCOMPLETE) {
				qry.appendGroupedAnd(new DynamicQuery.Parameter(fromCheckTerm, "TRUE", DynamicQuery.PREDICATE_NOTEQUALS));
				qry.AND().pad();
				qry.appendGroupedAnd(new DynamicQuery.Parameter(toCheckTerm, "TRUE", DynamicQuery.PREDICATE_EQUALS));
			}
			else if (completionStatus == CompletionStatus.STATUS_TO_KEY_INCOMPLETE) {
				qry.appendGroupedAnd(new DynamicQuery.Parameter(fromCheckTerm, "TRUE", DynamicQuery.PREDICATE_EQUALS));
				qry.AND().pad();
				qry.appendGroupedAnd(new DynamicQuery.Parameter(toCheckTerm, "TRUE", DynamicQuery.PREDICATE_NOTEQUALS));
			}

			qry.closeParen().pad();
		}

		
	}
	
	
}
