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
import org.apache.juddi.query.util.DynamicQuery;

/**
 * 
 * Returns the list of PublisherAssertions that contain the input businessKeys as their from or to key
 *  * 
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class DeletePublisherAssertionByBusinessQuery extends PublisherAssertionQuery {

	private static Log log = LogFactory.getLog(DeletePublisherAssertionByBusinessQuery.class);

	public static int delete(EntityManager em, List<?> businessKeys) {
		if ((businessKeys == null) || (businessKeys.size() == 0))
			return 0;
		
		DynamicQuery dynamicQry = new DynamicQuery(deleteSQL);
		appendConditions(dynamicQry, businessKeys);
		
		log.debug(dynamicQry);
		
		Query qry = dynamicQry.buildJPAQuery(em);
		int result = qry.executeUpdate();
		
		return result;
	}
	
	/*
	 * Appends the conditions to the query based on the businessKey list.  The keys can either be in the fromKey or toKey of the publisher assertion.
	 */
	public static void appendConditions(DynamicQuery qry, List<?> businessKeys) {
		
		qry.WHERE().pad().openParen().pad();
		
		qry.appendInList(ENTITY_ALIAS + "." + FROM_KEY_NAME, businessKeys);
		qry.pad().OR().pad();
		qry.appendInList(ENTITY_ALIAS + "." + TO_KEY_NAME, businessKeys);
		qry.closeParen().pad();
		
		
	}
	
	
}
