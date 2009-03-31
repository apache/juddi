package org.uddi.api_v3.client.i18n;

import java.util.List;

import org.uddi.api_v3.Address;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.Name;
/** 
 * Returns the entity in the proper language. If no language is specified
 * or if no language is matched return the value of the first entity in the list.
 */
public class EntityForLang {
	
	public static Name get(List<Name> entityList, String lang) {
		
		if (lang==null) return entityList.get(0);
		for (Name entity : entityList) {
			if (lang.equalsIgnoreCase(entity.getLang())) {
				return entity;
			}
		}
		return entityList.get(0);
	}
	
	public static Address get(List<Address> entityList, String lang) {
		
		if (lang==null) return entityList.get(0);
		for (Address entity : entityList) {
			if (lang.equalsIgnoreCase(entity.getLang())) {
				return entity;
			}
		}
		return entityList.get(0);
	}
	
	public static Description get(List<Description> entityList, String lang) {
		
		if (lang==null) return entityList.get(0);
		for (Description entity : entityList) {
			if (lang.equalsIgnoreCase(entity.getLang())) {
				return entity;
			}
		}
		return entityList.get(0);
	}
	
	
}
