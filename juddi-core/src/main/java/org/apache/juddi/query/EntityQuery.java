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
import java.util.ArrayList;
import java.util.Collections;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.configuration.ConfigurationException;
import org.uddi.api_v3.ListDescription;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.Property;
import org.apache.juddi.query.util.DynamicQuery;
import org.apache.log4j.Logger;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public abstract class EntityQuery {
	private static Logger log = Logger.getLogger(EntityQuery.class);

	public static final String KEY_NAME = "entityKey";

	public static final int DEFAULT_MAXROWS = 1000;
	public static final int DEFAULT_MAXINCLAUSE = 1000;
	
	// TODO:  make this alias creator a little more unique
	public static String buildAlias(String entityName) {
		if (entityName == null || entityName.length() == 0)
			return "x";
		
		return entityName.substring(0, entityName.length() - 3) + "_";
	}
	
	/*
	 * Used to retrieve the final results of find operations.  Handles paging as specified by user.  
	 * 
	 * TODO: This query will use an IN clause, however, it is not restricted per the global parameter.  This is so the query can
	 * take advantage of sorting through SQL.  The fix would be to apply the parameter, but only if the IN list is greater than the
	 * parameter.  In this case, sorting would have to be done in java.
	 * 
	 */
	public static List<?> getPagedResult(EntityManager em, DynamicQuery dynamicQry, Integer maxRowsUser, Integer listHead, ListDescription listDesc) {
		
		int maxRows = DEFAULT_MAXROWS;
		try {
			maxRows = AppConfig.getConfiguration().getInteger(Property.JUDDI_MAX_ROWS, DEFAULT_MAXROWS);
		}
		catch(ConfigurationException ce) {
			log.error("Configuration exception occurred retrieving: " + Property.JUDDI_MAX_ROWS);
		}
		
		if (maxRowsUser != null && maxRowsUser > 0) {
			if (maxRowsUser < maxRows)
				maxRows = maxRowsUser;
		}
		
		if (listHead == null || listHead <= 0)
			listHead = 1;
		
		
		Query qry = dynamicQry.buildJPAQuery(em);
		List<?> result = qry.getResultList();
		int resultSize = result.size();

		if (listDesc != null) {
			listDesc.setActualCount(resultSize);
			listDesc.setListHead(listHead);
		}
		
		int startIndex = listHead - 1;
		if (startIndex >= resultSize) {
			if (listDesc != null)
				listDesc.setIncludeCount(0);

			return Collections.emptyList();
		}
		else {
			int endIndex = Math.min(startIndex + maxRows, resultSize);
			if (listDesc != null)
				listDesc.setIncludeCount(endIndex - startIndex);

			List<Object> subList = new ArrayList<Object>(endIndex);
			for (int i=startIndex; i< endIndex; i++) {
				subList.add(result.get(i));
			}
			return subList;
		}
	}
	
	/*
	 * Used for all the find operation sub-queries.  Restricts size of the IN clause based on global parameter
	 */
	@SuppressWarnings("unchecked")
	public static List<?> getQueryResult(EntityManager em, DynamicQuery dynamicQry, List<?> keysIn, String inListTerm) {
		
		List<Object> result = new ArrayList<Object>(0);
		// If keysIn is null, then no IN list is applied to the query - we simply need to run the query.  Otherwise, the IN list is chunked based on
		// the application property.
		if (keysIn == null) {
			log.debug(dynamicQry);
			Query qry = dynamicQry.buildJPAQuery(em);
			result = qry.getResultList();
		}
		else {
			int maxInClause = DEFAULT_MAXINCLAUSE;
			try {
				maxInClause = AppConfig.getConfiguration().getInteger(Property.JUDDI_MAX_IN_CLAUSE, DEFAULT_MAXINCLAUSE);
			}
			catch(ConfigurationException ce) {
				log.error("Configuration exception occurred retrieving: " + Property.JUDDI_MAX_IN_CLAUSE);
			}
			
			int inParamsLeft = keysIn.size();
			int startIndex = 0;
			while(inParamsLeft > 0) {
				int endIndex = startIndex + Math.min(inParamsLeft, maxInClause);
				
				List<Object> subKeysIn = new ArrayList<Object>(endIndex);
				for (int i=startIndex; i< endIndex; i++) {
					subKeysIn.add(keysIn.get(i));
				}
				dynamicQry.appendInListWithAnd(inListTerm, subKeysIn);
				log.debug(dynamicQry);
	
				Query qry = dynamicQry.buildJPAQuery(em);
				List<Object> resultChunk = qry.getResultList();
				result.addAll(resultChunk);
				
				inParamsLeft = inParamsLeft - (endIndex - startIndex);
				startIndex = endIndex;
			}
		}
		
		return result;
		

	}

	
}
