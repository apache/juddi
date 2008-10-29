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

import org.apache.juddi.query.util.DynamicQuery;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public abstract class EntityQuery {
	public static final String GENERAL_KEYWORD_TMODEL = "uddi:uddi-org:general_keywords";
	
	public static String buildAlias(String entityName) {
		if (entityName == null || entityName.length() == 0)
			return "x";
		
		return entityName.substring(0, entityName.length() - 3);
	}
	
	public static List<?> getPagedResult(EntityManager em, DynamicQuery dynamicQry, Integer maxRows, Integer listHead) {
		
		if (maxRows == null || maxRows <= 0)
			maxRows = 10; //TODO: set to system default
		
		if (listHead == null || listHead <= 0)
			listHead = 0;
		
		
		Query qry = dynamicQry.buildJPAQuery(em);
		qry.setMaxResults(maxRows);
		qry.setFirstResult(listHead);
		
		return qry.getResultList();
		

	}
}
