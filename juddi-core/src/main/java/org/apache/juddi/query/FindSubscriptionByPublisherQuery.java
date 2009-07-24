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

/**
 * Finds and returns a list of subscriptions by the authorized name.
 * 
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class FindSubscriptionByPublisherQuery extends SubscriptionQuery {
	
	
	public static List<?> select(EntityManager em, String authorizedName) {
		DynamicQuery dynamicQry = new DynamicQuery(selectSQL);
		dynamicQry.WHERE().pad();
		dynamicQry.appendGroupedAnd(new DynamicQuery.Parameter(ENTITY_ALIAS + "." + FindEntityByPublisherQuery.AUTHORIZED_NAME_FIELD, authorizedName, DynamicQuery.PREDICATE_EQUALS));		
		return getQueryResult(em, dynamicQry, null, null);
	}

}
