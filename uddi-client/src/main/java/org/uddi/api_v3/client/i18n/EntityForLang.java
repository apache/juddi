package org.uddi.api_v3.client.i18n;

import java.util.List;

import org.uddi.api_v3.Address;
import org.uddi.api_v3.Name;

public class EntityForLang {
	/** 
	 * Returns the name in the proper language. If no language is specified
	 * or if no language is matched return the value of the first name in the list.
	 * 
	 * @param nameList - List of names
	 * @param lang - requested name.
	 *
	 * @return - the value of the name in the language matched.
	 * 
	 */
	public static Name get(List<Name> entityList, String lang) {
		
		if (lang==null) return entityList.get(0);
		for (Name entity : entityList) {
			if (lang.equalsIgnoreCase(entity.getLang())) {
				return entity;
			}
		}
		return entityList.get(0);
	}
	/** 
	 * Returns the name in the proper language. If no language is specified
	 * or if no language is matched return the value of the first name in the list.
	 * 
	 * @param nameList - List of names
	 * @param lang - requested name.
	 *
	 * @return - the value of the name in the language matched.
	 * 
	 */
	public static Address get(List<Address> entityList, String lang) {
		
		if (lang==null) return entityList.get(0);
		for (Address entity : entityList) {
			if (lang.equalsIgnoreCase(entity.getLang())) {
				return entity;
			}
		}
		return entityList.get(0);
	}
	
	
}
