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
import org.uddi.api_v3.DiscoveryURLs;
import org.uddi.api_v3.DiscoveryURL;

/**
 * 
 * Returns the list of business keys possessing the DiscoveryUrls in the passed DiscoveryUrl list.
 * Output is restricted by list of business keys passed in.  If null, all businesses are searched.
 * Output is produced by building the appropriate JPA query based on input and find qualifiers.
 * 
 * From specification:
 * "This is a list of discoveryURL structures to be matched against the discoveryURL data associated with registered businessEntity 
 * information.  To search for URL without regard to useType attribute values, omit the useType attribute or pass it as an empty 
 * attribute.  If useType values are included, the match occurs only on registered information that matches both the useType and 
 * URL value.  The returned businessList contains businessInfo structures matching any of the URL's passed (logical OR)."
 * 
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class FindBusinessByDiscoveryURLQuery extends BusinessEntityQuery {

	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(FindBusinessByDiscoveryURLQuery.class);

	private static final String ENTITY_NAME_CHILD = "DiscoveryUrl";

	private static String entityAliasChild;;
	
	static {
		entityAliasChild = buildAlias(ENTITY_NAME_CHILD);
	}

	public static List<?> select(EntityManager em, FindQualifiers fq, DiscoveryURLs discURLs, 
			List<?> keysIn, DynamicQuery.Parameter... restrictions) {
		// If keysIn is not null and empty, then search is over.
		if ((keysIn != null) && (keysIn.size() == 0))
			return keysIn;
		
		if (discURLs == null)
			return keysIn;
		
		List<DiscoveryURL> discURLlist = discURLs.getDiscoveryURL();
		if (discURLlist == null || discURLlist.size() == 0)
			return keysIn;
		
		DynamicQuery dynamicQry = new DynamicQuery(selectSQL);
		appendConditions(dynamicQry, fq, discURLlist);
		if (restrictions != null && restrictions.length > 0)
			dynamicQry.AND().pad().appendGroupedAnd(restrictions);

		return getQueryResult(em, dynamicQry, keysIn, ENTITY_ALIAS + "." + KEY_NAME);
	}
	
	/*
	 * Appends the conditions to the query based on the discovery url list.  By default, the urls are OR'd and this cannot be changed. 
	 *	 
	 */
	public static void appendConditions(DynamicQuery qry, FindQualifiers fq, List<DiscoveryURL> discURLs) {
		
		// Append the necessary tables (one will always be added connecting the entity to its discovery url table).
		appendJoinTables(qry, fq, discURLs);
		qry.AND().pad().openParen().pad();

		int count = 0;
		for(DiscoveryURL discURL : discURLs) {
			String urlTerm = entityAliasChild + ".url";
			String urlValue = discURL.getValue();
			
			if (discURL.getUseType() == null || discURL.getUseType().length() == 0 ) {
				qry.appendGroupedAnd(new DynamicQuery.Parameter(urlTerm, urlValue, DynamicQuery.PREDICATE_EQUALS));
			}
			else {
				qry.appendGroupedAnd(new DynamicQuery.Parameter(urlTerm, urlValue, DynamicQuery.PREDICATE_EQUALS), 
									 new DynamicQuery.Parameter(entityAliasChild + ".useType", discURL.getUseType(), DynamicQuery.PREDICATE_EQUALS));
			}
			
			if (count + 1 < discURLs.size())
				qry.OR().pad();
			
			count++;
		}
		qry.closeParen().pad();
		
	}
	
	/*
	 * Appends the necessary join table for the child entity 
	 */
	public static void appendJoinTables(DynamicQuery qry, FindQualifiers fq, List<DiscoveryURL> discURLs) {
		qry.comma().pad().append(ENTITY_NAME_CHILD + " " + entityAliasChild).pad();
		qry.WHERE().pad().openParen().pad();
		qry.append(ENTITY_ALIAS + "." + KEY_NAME + " = " + entityAliasChild + "." + ENTITY_FIELD + "." + KEY_NAME + " ");
		qry.closeParen().pad();
	}
	
}
