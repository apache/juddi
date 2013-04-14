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
 * Returns the list of tModel keys possessing the publisherId in the passed UddiEntityPublisher.
 * Output is restricted by list of tModel keys passed in.  If null, all tModels are searched.
 * Output is produced by building the appropriate JPA query based on input and find qualifiers.
 *  *  
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class FindTModelByPublisherQuery {
	
	
	private static FindEntityByPublisherQuery findQuery;
	
	static {
		findQuery = new FindEntityByPublisherQuery(
						TModelQuery.ENTITY_NAME, 
						TModelQuery.ENTITY_ALIAS, 
						TModelQuery.KEY_NAME,
						TModelQuery.SIGNATURE_PRESENT);
	}
	
	public static List<?> select(EntityManager em, FindQualifiers fq, UddiEntityPublisher publisher, List<?> keysIn, DynamicQuery.Parameter... restrictions) {
		return findQuery.select(em, fq, publisher, keysIn, restrictions);
	}
	
}
