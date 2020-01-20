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
public class BindingTemplateQuery extends EntityQuery {

	public static final String ENTITY_NAME = "BindingTemplate";
	public static final String ENTITY_ALIAS = "bt";
	public static final String ENTITY_FIELD = "bindingTemplate";
	public static final String KEY_NAME_PARENT = BusinessServiceQuery.ENTITY_FIELD + "." + BusinessServiceQuery.KEY_NAME;
	public static final String SIGNATURE_PRESENT = 
			ENTITY_ALIAS + "." + EntityQuery.SIGNATURE_FIELD + " IS NOT EMPTY OR " +
			ENTITY_ALIAS + ".businessService." + EntityQuery.SIGNATURE_FIELD + " IS NOT EMPTY OR " +
			ENTITY_ALIAS + ".businessService.businessEntity." + EntityQuery.SIGNATURE_FIELD + " IS NOT EMPTY ";
	
	protected final static String selectSQL;

	static {
		StringBuilder sql = new StringBuilder(200);
		sql.append("select distinct "+ ENTITY_ALIAS + "." + KEY_NAME + " from " + ENTITY_NAME + " " + ENTITY_ALIAS + " ");
		selectSQL = sql.toString();
	}

	public static String getSelectSQL() {
		return selectSQL;
	}
	
}
