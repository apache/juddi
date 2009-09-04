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
package org.uddi.api_v3.tck;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.Contact;
import org.uddi.api_v3.Contacts;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.DiscoveryURL;
import org.uddi.api_v3.DiscoveryURLs;
import org.uddi.api_v3.HostingRedirector;
import org.uddi.api_v3.InstanceDetails;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.OverviewDoc;
import org.uddi.api_v3.OverviewURL;
import org.uddi.api_v3.PersonName;
import org.uddi.api_v3.TModelInstanceDetails;
import org.uddi.api_v3.TModelInstanceInfo;

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
			
			checkPersonNames(contact1.getPersonName(), contact2.getPersonName());
			checkDescriptions(contact1.getDescription(), contact2.getDescription());
		}
	}
	
	public static void checkPersonNames(List<PersonName> names1, List<PersonName> names2) {
		if (names1 == null || names2 == null) {
			assertEquals(names1, names2);
			return;
		}
		assertEquals(names1.size(), names2.size());
		Iterator<PersonName> names1Itr = names1.iterator();
		Iterator<PersonName> names2Itr = names2.iterator();
		while (names1Itr.hasNext()) {
			PersonName name1 = names1Itr.next();
			PersonName name2 = names2Itr.next();
			assertEquals(name1.getLang(), name2.getLang());
			assertEquals(name1.getValue(), name2.getValue());
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
			if (elem1 instanceof org.uddi.api_v3.KeyedReference) {
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
			assertEquals(bt1.getAccessPoint().getUseType(),bt2.getAccessPoint().getUseType());
			assertEquals(bt1.getBindingKey(),bt2.getBindingKey());
			checkCategories(bt1.getCategoryBag(), bt2.getCategoryBag());
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
			assertEquals(tmI1.getTModelKey(),tmI2.getTModelKey());
		}
	}
	
	public static void checkInstanceDetails(InstanceDetails ids1, InstanceDetails ids2) {
		if (ids1 == null || ids2 == null) {
			assertEquals(ids1, ids2);
			return;
		}
		List<Description> elem1s =  ids1.getDescription();
		List<Description> elem2s =  ids2.getDescription();
		Iterator<Description> elem1 = elem1s.iterator();

		boolean isMatch=false;

		if (elem1s.size() == 0 && elem2s.size() == 0) {
			isMatch = true;
		}
		
		while (elem1.hasNext()) {
			Description desc1 = elem1.next();
			
			if (desc1 instanceof org.uddi.api_v3.Description) {
				//Descriptions
				Iterator<Description> elem2 = elem2s.iterator();
				while (elem2.hasNext()) {
					Description desc2 = elem2.next();
					if (desc2 instanceof org.uddi.api_v3.Description) {
						if (desc1.getLang().equals(desc2.getLang()) && desc1.getValue().equals(desc2.getValue())) {
							isMatch=true;
							break;
						}
					}
				}
			}
		}
		assertTrue(isMatch);

		
		List<OverviewDoc> odoc1s =  ids1.getOverviewDoc();
		List<OverviewDoc> odoc2s =  ids2.getOverviewDoc();
		Iterator<OverviewDoc> docelem1 = odoc1s.iterator();
		isMatch = false;
		if (odoc1s.size() == 0 && odoc2s.size() == 0) {
			isMatch = true;
		}
		
		while (docelem1.hasNext()) {
			OverviewDoc odoc1 = docelem1.next();
			if (odoc1 instanceof org.uddi.api_v3.OverviewDoc) {
				//OverviewDocs
				checkOverviewDocs(odoc1, odoc2s);
				isMatch=true;
			}
		}
		
		assertEquals((String)ids1.getInstanceParms(),(String)ids2.getInstanceParms());
		assertTrue(isMatch);
	}
	
	public static void checkOverviewDocs(OverviewDoc doc1, List<OverviewDoc> elem2s) {
		boolean isMatch=false;
		Iterator<OverviewDoc> elem2 = elem2s.iterator();
		//Match against any OverviewDocs in the elem2 list
		while (elem2.hasNext()) {
			OverviewDoc doc2 = elem2.next();
			if (doc2 instanceof org.uddi.api_v3.OverviewDoc) {
				//match doc1 against this doc2
				isMatch = compareOverviewDocs(doc1, doc2);
				if (isMatch) break;
			}
		}
		assertTrue(isMatch);
	}
	
	public static void checkOverviewDocs(OverviewDoc doc1, Collection<OverviewDoc> doc2s) {
		boolean isMatch=false;
		Iterator<OverviewDoc> docIter = doc2s.iterator();
		while (docIter.hasNext()) {
			OverviewDoc doc2 = docIter.next();
			//match doc1 against this doc2
			isMatch = compareOverviewDocs(doc1, doc2);
			if (isMatch) break;
		}
		assertTrue(isMatch);
	}
	
	public static boolean compareOverviewDocs(OverviewDoc doc1, OverviewDoc doc2) 
	{	
		boolean descMatch=false;
		boolean urlMatch =false;

		OverviewURL url1 = (OverviewURL) doc1.getOverviewURL();
		OverviewURL url2 = (OverviewURL) doc2.getOverviewURL();
		if (url1.getUseType().equals(url2.getUseType()) && url1.getValue().equals(url2.getValue())) {
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
