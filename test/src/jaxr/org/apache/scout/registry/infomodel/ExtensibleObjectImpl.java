/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
 */
package org.apache.scout.registry.infomodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.registry.JAXRException;
import javax.xml.registry.infomodel.ExtensibleObject;
import javax.xml.registry.infomodel.Slot;

/** 
 * Implements JAXR Interface.
 * For futher details, look into the JAXR API Javadoc. 
 * @author Anil Saldhana  <anil@apache.org>
 */
public class ExtensibleObjectImpl implements ExtensibleObject {
	
	private Collection slots = new ArrayList();

	/* (non-Javadoc)
	 * @see javax.xml.registry.infomodel.ExtensibleObject#removeSlot(java.lang.String)
	 */
	public void removeSlot(String arg0) throws JAXRException {
//	  Check for a Slot with the specified String 
	  Iterator iter = slots.iterator();
	        while( iter.hasNext()){
	            Slot slot = (Slot)iter.next();
	            if( slot.getName().equals(arg0)) 
	                slots.remove(slot);
	        } 
		
	}

	/* (non-Javadoc)
	 * @see javax.xml.registry.infomodel.ExtensibleObject#getSlots()
	 */
	public Collection getSlots() throws JAXRException {
		 return slots;
	}

	/* (non-Javadoc)
	 * @see javax.xml.registry.infomodel.ExtensibleObject#addSlots(java.util.Collection)
	 */
	public void addSlots(Collection arg0) throws JAXRException {
		slots.addAll( arg0);		
	}

	/* (non-Javadoc)
	 * @see javax.xml.registry.infomodel.ExtensibleObject#removeSlots(java.util.Collection)
	 */
	public void removeSlots(Collection arg0) throws JAXRException {
		slots.removeAll( arg0);
		
	}

	/* (non-Javadoc)
	 * @see javax.xml.registry.infomodel.ExtensibleObject#addSlot(javax.xml.registry.infomodel.Slot)
	 */
	public void addSlot(Slot arg0) throws JAXRException {
		slots.add( arg0);
		
	}

	/* (non-Javadoc)
	 * @see javax.xml.registry.infomodel.ExtensibleObject#getSlot(java.lang.String)
	 */
	public Slot getSlot(String arg0) throws JAXRException {
		// Check for a Slot with the specified String 
	    Iterator iter = slots.iterator();
	        while( iter.hasNext()){
	            Slot slot = (Slot)iter.next();
	            if( slot.getName().equals(arg0)) return slot;
	        }
		return null;
	}

}
