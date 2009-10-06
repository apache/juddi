/*
 * Copyright 2001-2009 The Apache Software Foundation.
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
package org.apache.juddi.v3.client.i18n;

import java.util.List;

import org.uddi.api_v3.Address;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.Name;
/** 
 * Returns the entity in the proper language. If no language is specified
 * or if no language is matched return the value of the first entity in the list.
 */
public class EntityForLang {
	
	public static Name getName(List<Name> entityList, String lang) {
		
		if (entityList.size()==0) {
			Name name = new Name();
			name.setValue("");
			name.setLang(lang);
			entityList.add(name);
		}
		if (lang==null) return entityList.get(0);
		for (Name entity : entityList) {
			if (lang.equalsIgnoreCase(entity.getLang())) {
				return entity;
			}
		}
		return entityList.get(0);
	}
	
	public static Address getAddress(List<Address> entityList, String lang) {
		
		if (entityList.size()==0) {
			Address address = new Address();
			address.setLang(lang);
			entityList.add(address);
		}
		if (lang==null) return entityList.get(0);
		for (Address entity : entityList) {
			if (lang.equalsIgnoreCase(entity.getLang())) {
				return entity;
			}
		}
		return entityList.get(0);
	}
	
	public static Description getDescription(List<Description> entityList, String lang) {
		
		if (entityList.size()==0) {
			Description description = new Description();
			description.setValue("");
			description.setLang(lang);
			entityList.add(description);
		}
		if (lang==null) return entityList.get(0);
		for (Description entity : entityList) {
			if (lang.equalsIgnoreCase(entity.getLang())) {
				return entity;
			}
		}
		return entityList.get(0);
	}
	
	
}
