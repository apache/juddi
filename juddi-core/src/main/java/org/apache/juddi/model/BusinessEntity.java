package org.apache.juddi.model;
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
 */

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.OrderBy;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@Entity
@Table(name = "business_entity")
public class BusinessEntity extends UddiEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private UddiEntityPublisher publisher;
	private Set<Contact> contacts = new HashSet<Contact>(0);
	private Set<BusinessIdentifier> businessIdentifiers = new HashSet<BusinessIdentifier>(0);
	private Set<PublisherAssertion> publisherAssertionsForFromKey = new HashSet<PublisherAssertion>(0);
	private Set<DiscoveryUrl> discoveryUrls = new HashSet<DiscoveryUrl>(0);
	private Set<BusinessName> businessNames = new HashSet<BusinessName>(0);
	private Set<PublisherAssertion> publisherAssertionsForToKey = new HashSet<PublisherAssertion>(0);
	private BusinessCategoryBag categoryBag = new BusinessCategoryBag();
	private Set<BusinessService> businessServices = new HashSet<BusinessService>(0);
	private Set<BusinessDescr> businessDescrs = new HashSet<BusinessDescr>(0);

	public BusinessEntity() {
	}

	public BusinessEntity(String entityKey, Date lastUpdate) {
		this.entityKey = entityKey;
		this.lastUpdate = lastUpdate;
	}
	public BusinessEntity(String entityKey, String authorizedName, 
			UddiEntityPublisher publisher, String operator,
			Date lastUpdate, Set<Contact> contacts,
			Set<BusinessIdentifier> businessIdentifiers,
			Set<PublisherAssertion> publisherAssertionsForFromKey,
			Set<DiscoveryUrl> discoveryUrls, Set<BusinessName> businessNames,
			Set<PublisherAssertion> publisherAssertionsForToKey,
			BusinessCategoryBag categoryBag,
			Set<BusinessService> businessServices,
			Set<BusinessDescr> businessDescrs) {
		this.entityKey = entityKey;
		this.publisher = publisher;
		this.lastUpdate = lastUpdate;
		this.contacts = contacts;
		this.businessIdentifiers = businessIdentifiers;
		this.publisherAssertionsForFromKey = publisherAssertionsForFromKey;
		this.discoveryUrls = discoveryUrls;
		this.businessNames = businessNames;
		this.publisherAssertionsForToKey = publisherAssertionsForToKey;
		this.categoryBag = categoryBag;
		this.businessServices = businessServices;
		this.businessDescrs = businessDescrs;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "authorized_name", nullable = false)
	public UddiEntityPublisher getPublisher() {
		return this.publisher;
	}
	public void setPublisher(UddiEntityPublisher publisher) {
		this.publisher = publisher;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "businessEntity")
	@OrderBy
	public Set<Contact> getContacts() {
		return this.contacts;
	}
	public void setContacts(Set<Contact> contacts) {
		this.contacts = contacts;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "businessEntity")
	@OrderBy
	public Set<BusinessIdentifier> getBusinessIdentifiers() {
		return this.businessIdentifiers;
	}
	public void setBusinessIdentifiers(
			Set<BusinessIdentifier> businessIdentifiers) {
		this.businessIdentifiers = businessIdentifiers;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "businessEntityByFromKey")
	public Set<PublisherAssertion> getPublisherAssertionsForFromKey() {
		return this.publisherAssertionsForFromKey;
	}
	public void setPublisherAssertionsForFromKey(
			Set<PublisherAssertion> publisherAssertionsForFromKey) {
		this.publisherAssertionsForFromKey = publisherAssertionsForFromKey;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "businessEntity")
	@OrderBy
	public Set<DiscoveryUrl> getDiscoveryUrls() {
		return this.discoveryUrls;
	}
	public void setDiscoveryUrls(Set<DiscoveryUrl> discoveryUrls) {
		this.discoveryUrls = discoveryUrls;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "businessEntity")
	@OrderBy
	public Set<BusinessName> getBusinessNames() {
		return this.businessNames;
	}
	public void setBusinessNames(Set<BusinessName> businessNames) {
		this.businessNames = businessNames;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "businessEntityByToKey")
	public Set<PublisherAssertion> getPublisherAssertionsForToKey() {
		return this.publisherAssertionsForToKey;
	}
	public void setPublisherAssertionsForToKey(
			Set<PublisherAssertion> publisherAssertionsForToKey) {
		this.publisherAssertionsForToKey = publisherAssertionsForToKey;
	}

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "businessEntity")
	public BusinessCategoryBag getCategoryBag() {
		return this.categoryBag;
	}
	public void setCategoryBag(BusinessCategoryBag categoryBag) {
		this.categoryBag = categoryBag;
	}
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "businessEntity")
	public Set<BusinessService> getBusinessServices() {
		return this.businessServices;
	}
	public void setBusinessServices(Set<BusinessService> businessServices) {
		this.businessServices = businessServices;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "businessEntity")
	@OrderBy
	public Set<BusinessDescr> getBusinessDescrs() {
		return this.businessDescrs;
	}
	public void setBusinessDescrs(Set<BusinessDescr> businessDescrs) {
		this.businessDescrs = businessDescrs;
	}

	public String retrieveAuthorizedName() {
		return getPublisher().getAuthorizedName();
	}
	public void assignAuthorizedName(String authName) {
		if (authName != null) {
			Publisher pub = new Publisher();
			pub.setAuthorizedName(authName);
			this.setPublisher(pub);
		}
	}
	
}
