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

import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;

import org.apache.juddi.query.util.DynamicQuery;
import org.apache.juddi.query.util.FindQualifiers;
import org.apache.log4j.Logger;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.api_v3.ListDescription;

/**
 * The "select" method retrieves all the entities for the input key list and sorts according to the user settings.  Paging is taken into account when retrieving 
 * the results.  The result is a list of the entity objects containing all top level elements (restricted to the given page). 
 * 
 * NOTE:  Results usually need multiple one-to-many collections to be fetched.  Although this could be done in one query with eager fetching, this strategy is not
 * recommended as it will lead to a potentially large Cartesian product.  Therefore, the collections are initialized in separate queries during the mapping 
 * stage.  If the returned results are small (maxRows parameters is set appropriately), a single query is likely faster, but probably not by enough to consider 
 * the optimization under these conditions.
 * 
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class FetchTModelsQuery extends TModelQuery {

	private static Logger log = Logger.getLogger(FetchTModelsQuery.class);

	protected static String selectSQL;

	static {
		StringBuffer sql = new StringBuffer(200);
		sql.append("select distinct " + ENTITY_ALIAS + " from " + ENTITY_NAME + " " + ENTITY_ALIAS + " ");
		selectSQL = sql.toString();
	}
	
	public static List<?> select(EntityManager em, FindQualifiers fq, List<?> keysIn, Integer maxRows, Integer listHead, ListDescription listDesc, DynamicQuery.Parameter... restrictions) throws DispositionReportFaultMessage {
		
		// If keysIn is null or empty, then nothing to fetch.
		if ((keysIn == null) || (keysIn.size() == 0))
			return Collections.emptyList();

		DynamicQuery dynamicQry = new DynamicQuery(selectSQL);
		appendSortTables(dynamicQry);
		dynamicQry.appendInListWithAnd(ENTITY_ALIAS + "." + KEY_NAME, keysIn);
		if (restrictions != null && restrictions.length > 0)
			dynamicQry.AND().pad().appendGroupedAnd(restrictions);
		
		appendSortCriteria(dynamicQry, fq);

		log.debug(dynamicQry);
		
		return getPagedResult(em, dynamicQry, maxRows, listHead, listDesc);
	}
	
	private static void appendSortTables(DynamicQuery qry) {
		// TModels don't need the join table, the single name is in the main table.
		qry.WHERE().pad().append("1=1").pad();
	}
	
	/*
	 *	Default ordering is name ascending and date descending.  If a name item is set, then it will always go first.  If only a date item is set, then date will
	 *  go first, and the name sort will occur second in ascending order.
	 */
	private static void appendSortCriteria(DynamicQuery qry, FindQualifiers fq) {
		
		String nameTerm = ENTITY_ALIAS + ".name";
		if (fq.isCaseInsensitiveSort())
			nameTerm = "upper(" + nameTerm + ")";
		
		String dateTerm = ENTITY_ALIAS + ".modified";

		String orderClause = nameTerm + " " + DynamicQuery.SORT_ASC + ", " + dateTerm + " " + DynamicQuery.SORT_DESC;
		if (fq.isSortByNameAsc()) {
			if (fq.isSortByDateAsc())
				orderClause = nameTerm + " " + DynamicQuery.SORT_ASC + ", " + dateTerm + " " + DynamicQuery.SORT_ASC;
		}
		else if (fq.isSortByNameDesc()) {
			if (fq.isSortByDateAsc())
				orderClause = nameTerm + " " + DynamicQuery.SORT_DESC + ", " + dateTerm + " " + DynamicQuery.SORT_ASC;
			else
				orderClause = nameTerm + " " + DynamicQuery.SORT_DESC + ", " + dateTerm + " " + DynamicQuery.SORT_DESC;
		}
		else if (fq.isSortByDateAsc())
			orderClause =  dateTerm + " " + DynamicQuery.SORT_ASC + ", " +  nameTerm + " " + DynamicQuery.SORT_ASC;
		else if (fq.isSortByDateDesc())
			orderClause =  dateTerm + " " + DynamicQuery.SORT_DESC + ", " +  nameTerm + " " + DynamicQuery.SORT_ASC;
			
		qry.ORDERBY().pad();
		qry.append(orderClause);
		qry.pad();
	}

}
