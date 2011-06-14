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

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class PublisherAssertionQuery extends EntityQuery {

	public static final String ENTITY_NAME = "PublisherAssertion";
	public static final String ENTITY_ALIAS = "pa";
	public static final String FROM_KEY_NAME = "businessEntityByFromKey." + KEY_NAME;
	public static final String TO_KEY_NAME = "businessEntityByToKey." + KEY_NAME;
	
	protected static String selectSQL;
	protected static String deleteSQL;

	static {
		StringBuffer sql = new StringBuffer(200);
		sql.append("select distinct " + ENTITY_ALIAS + " from " + ENTITY_NAME + " " + ENTITY_ALIAS + " ");
		selectSQL = sql.toString();
	}

	static {
		StringBuffer sql = new StringBuffer(200);
		sql.append("delete from " + ENTITY_NAME + " " + ENTITY_ALIAS + " ");
		deleteSQL = sql.toString();
	}
	
	public static String getSelectSQL() {
		return selectSQL;
	}
	
}
