/*
 * Copyright 2001-2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.juddi.v2.tck;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.uddi.api_v2.*;

public class TckValidator {

	
	

	public static void checkNames(List<Name> names1, List<Name> names2) {
		if (names1 == null || names2 == null) {
			assertEquals(names1, names2);
			return;
		}
		assertEquals(names1.size(), names2.size());
		Iterator<Name> names1Itr = names1.iterator();
		Iterator<Name> names2Itr = names2.iterator();
		while (names1Itr.hasNext()) {
			Name name1 = names1Itr.next();
			Name name2 = names2Itr.next();
			assertEquals(name1.getLang(), name2.getLang());
			assertEquals(name1.getValue(), name2.getValue());
		}
	}
	
	public static void checkDescriptions(List<Description> descriptions1, List<Description> descriptions2) {
		if (descriptions1 == null || descriptions2 == null) {
			assertEquals(descriptions1, descriptions2);
			return;
		}
		assertEquals(descriptions1.size(), descriptions2.size());
		Iterator<Description> descriptions1Itr = descriptions1.iterator();
		Iterator<Description> descriptions2Itr = descriptions2.iterator();
		while (descriptions1Itr.hasNext()) {
			Description description1 = descriptions1Itr.next();
			Description description2 = descriptions2Itr.next();
			assertEquals(description1.getLang(), description2.getLang());
			assertEquals(description1.getValue(), description2.getValue());
		}
	}
	
	public static void checkDiscoveryUrls(DiscoveryURLs discs1, DiscoveryURLs discs2) {
		if (discs1 == null || discs2 == null) {
			assertEquals(discs1, discs2);
			return;
		}
		List<DiscoveryURL> discList1 = discs1.getDiscoveryURL();
		List<DiscoveryURL> discList2 = discs2.getDiscoveryURL();
		
		if (discList1 == null || discList2 == null) {
			assertEquals(discList1, discList2);
			return;
		}
		assertEquals(discList1.size(), discList2.size());
		Iterator<DiscoveryURL> discList1Itr = discList1.iterator();
		Iterator<DiscoveryURL> discList2Itr = discList2.iterator();
		while (discList1Itr.hasNext()) {
			DiscoveryURL disc1 = discList1Itr.next();
			DiscoveryURL disc2 = discList2Itr.next();
			assertEquals(disc1.getUseType(), disc2.getUseType());
			assertEquals(disc1.getValue(), disc2.getValue());
		}
	}
	
        
	public static void checkContacts(Contacts contacts1, Contacts contacts2) {
		if (contacts1 == null || contacts2 == null) {
			assertEquals(contacts1, contacts2);
			return;
		}
		List<Contact> contactList1 = contacts1.getContact();
		List<Contact> contactList2 = contacts2.getContact();
		if (contactList1 == null || contactList2 == null) {
			assertEquals(contactList1, contactList2);
			return;
		}
		assertEquals(contactList1.size(), contactList2.size());
		Iterator<Contact> contactList1Itr = contactList1.iterator();
		Iterator<Contact> contactList2Itr = contactList2.iterator();
		while (contactList1Itr.hasNext()) {
			Contact contact1 = contactList1Itr.next();
			Contact contact2 = contactList2Itr.next();
			assertEquals(contact1.getUseType(), contact2.getUseType());
			assertEquals(contact1.getPersonName(), contact2.getPersonName());
			checkDescriptions(contact1.getDescription(), contact2.getDescription());
		}
	}
	public static void checkCategories(CategoryBag cbag1, CategoryBag cbag2) {
		if (cbag1 == null || cbag2 == null) {
			assertEquals(cbag1, cbag2);
			return;
		}
		List<KeyedReference> elemList1 = cbag1.getKeyedReference();
		List<KeyedReference> elemList2 = cbag2.getKeyedReference();
		if (elemList1 == null || elemList2 == null) {
			assertEquals(elemList1, elemList2);
			return;
		}
		// In object could have KeyedReferenceGroups which are ignored.  For now, only solo KeyedReferences are checked.
		//assertEquals(elemList1.size(), elemList2.size());
		Iterator<KeyedReference> elemList1Itr = elemList1.iterator();
		Iterator<KeyedReference> elemList2Itr = elemList2.iterator();
		while (elemList1Itr.hasNext()) {
			KeyedReference elem1 = elemList1Itr.next();
			if (elem1 instanceof KeyedReference) {
				KeyedReference elem2 = elemList2Itr.next();
				assertEquals(elem1.getTModelKey(), elem2.getTModelKey());
				assertEquals(elem1.getKeyName(), elem2.getKeyName());
				assertEquals(elem1.getKeyValue(), elem2.getKeyValue());
			}
			// add comparing keyedReferenceGroup
		}
	}
	
	public static void checkBindingTemplates(BindingTemplates bts1, BindingTemplates bts2) {
		if (bts1 == null || bts2 == null) {
			assertEquals(bts1, bts2);
			return;
		}
		assertEquals(bts1.getBindingTemplate().size(), bts2.getBindingTemplate().size());
		Iterator<BindingTemplate> bt1Iter  = bts1.getBindingTemplate().iterator();
		Iterator<BindingTemplate> bt2Iter  = bts2.getBindingTemplate().iterator();
		while (bt1Iter.hasNext()) {
			BindingTemplate bt1 = bt1Iter.next();
			BindingTemplate bt2 = bt2Iter.next();
			assertEquals(bt1.getAccessPoint().getValue(),bt2.getAccessPoint().getValue());
			assertEquals(bt1.getAccessPoint().getURLType(),bt2.getAccessPoint().getURLType());
			assertEquals(bt1.getBindingKey().toLowerCase(),bt2.getBindingKey());
			checkDescriptions(bt1.getDescription(),bt2.getDescription());
			checkHostingRedirector(bt1.getHostingRedirector(),bt2.getHostingRedirector());
			//the inbound apiBindingTemplate can have a null serviceKey
			if (bt1.getServiceKey()!=null) {
				assertEquals(bt1.getServiceKey(),bt2.getServiceKey());
			}
			checkTModelInstanceDetails(bt1.getTModelInstanceDetails(),bt2.getTModelInstanceDetails());
		}
	}
	
	public static void checkTModelInstanceDetails(TModelInstanceDetails tmds1, TModelInstanceDetails tmds2) {
		if (tmds1 == null || tmds2 == null) {
			assertEquals(tmds1, tmds2);
			return;
		}
		assertEquals(tmds1.getTModelInstanceInfo().size(),tmds2.getTModelInstanceInfo().size());
		Iterator<TModelInstanceInfo> tmIter1 = tmds1.getTModelInstanceInfo().iterator();
		Iterator<TModelInstanceInfo> tmIter2 = tmds2.getTModelInstanceInfo().iterator();
		while (tmIter1.hasNext()) {
			TModelInstanceInfo tmI1 = tmIter1.next();
			TModelInstanceInfo tmI2 = tmIter2.next();
			checkDescriptions(tmI1.getDescription(), tmI2.getDescription());
			checkInstanceDetails(tmI1.getInstanceDetails(), tmI2.getInstanceDetails());
			assertEquals(tmI1.getTModelKey().toLowerCase(),tmI2.getTModelKey());
		}
	}
	
	public static void checkInstanceDetails(InstanceDetails ids1, InstanceDetails ids2) {
		if (ids1 == null || ids2 == null) {
			return;
		}
		List<Description> elem1s =  ids1.getDescription();
		List<Description> elem2s =  ids2.getDescription();
		Iterator<Description> elem1 = elem1s.iterator();
          
		checkDescriptions(elem2s, elem2s);
	
		OverviewDoc odoc1s =  ids1.getOverviewDoc();
		OverviewDoc odoc2s =  ids2.getOverviewDoc();
		
		if (odoc1s==null&& odoc2s == null) {
		}else if (odoc1s!=null && odoc2s!=null)
          {
               checkDescriptions(odoc1s.getDescription(), odoc2s.getDescription());
               assertEquals("overview doc mismatch",odoc1s.getOverviewURL(), odoc2s.getOverviewURL());
          }else
          {
               assertTrue("overview doc missing one one item", false);
          }
		assertEquals("instance params mismatch",ids1.getInstanceParms(),ids2.getInstanceParms());
	}
	
	public static void checkOverviewDocs(OverviewDoc doc1, OverviewDoc elem2s) {
		boolean isMatch=false;
		
		//Match against any OverviewDocs in the elem2 list
				isMatch = compareOverviewDocs(doc1, elem2s);
		
		
		assertTrue(isMatch);
	}
	
	
	public static boolean compareOverviewDocs(OverviewDoc doc1, OverviewDoc doc2) 
	{	
          if (doc1==null && doc2==null) return true;
          if (doc1!=null && doc2==null) return false;
          if (doc1==null && doc2!=null) return false;
		boolean descMatch=false;
		boolean urlMatch =false;

		String url1 =  doc1.getOverviewURL();
		String url2 =  doc2.getOverviewURL();
		if (url1.equals(url2) ) {
			urlMatch=true;
		}


		List<Description> descList1 = doc1.getDescription();
		Iterator<Description> descIter1 = descList1.iterator();
		if (descList1.size() == 0 && doc2.getDescription().size() == 0) {
			descMatch = true;
		}
		while (descIter1.hasNext()) {
			Description descr1 = (Description) descIter1.next();
			List<Description> descList2 = doc2.getDescription();
			Iterator<Description> descElem2 = descList2.iterator();
			while (descElem2.hasNext()) {
				Description descr2 = descElem2.next();
				if (descr1.getLang().equals(descr2.getLang()) && descr1.getValue().equals(descr2.getValue())) {
					descMatch=true;
				}
			}
		}

		if (urlMatch && descMatch) {
				return true;
		}
		return false;
	}
	
	
	
	public static void checkHostingRedirector(HostingRedirector hr1, HostingRedirector hr2) {
		if (hr1 == null || hr2 == null) {
			assertEquals(hr1, hr2);
			return;
		}
		assertEquals(hr1.getBindingKey(),hr2.getBindingKey());
	}
	
}
