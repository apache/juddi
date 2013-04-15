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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@Entity
@Table(name = "j3_business_entity")
public class BusinessEntity extends UddiEntity implements java.io.Serializable {

	private static final long serialVersionUID = -7353389848796421619L;
	private List<Contact> contacts = new ArrayList<Contact>(0);
	private List<BusinessIdentifier> businessIdentifiers = new ArrayList<BusinessIdentifier>(0);
	private List<PublisherAssertion> publisherAssertionsForFromKey = new ArrayList<PublisherAssertion>(0);
	private List<DiscoveryUrl> discoveryUrls = new ArrayList<DiscoveryUrl>(0);
	private List<BusinessName> businessNames = new ArrayList<BusinessName>(0);
	private List<PublisherAssertion> publisherAssertionsForToKey = new ArrayList<PublisherAssertion>(0);
	private BusinessCategoryBag categoryBag;
	private List<BusinessService> businessServices = new ArrayList<BusinessService>(0);
	private List<BusinessDescr> businessDescrs = new ArrayList<BusinessDescr>(0);
	private List<ServiceProjection> serviceProjections = new ArrayList<ServiceProjection>(0);
    private List<Signature> signatures = new ArrayList<Signature>(0);

	public BusinessEntity() {
	}

	public BusinessEntity(String entityKey, Date modified) {
		this.entityKey = entityKey;
		this.modified = modified;
	}
	public BusinessEntity(String entityKey, String authorizedName, 
			String operator,
			Date modified, List<Contact> contacts,
			List<BusinessIdentifier> businessIdentifiers,
			List<PublisherAssertion> publisherAssertionsForFromKey,
			List<DiscoveryUrl> discoveryUrls, List<BusinessName> businessNames,
			List<PublisherAssertion> publisherAssertionsForToKey,
			BusinessCategoryBag categoryBag,
			List<BusinessService> businessServices,
			List<BusinessDescr> businessDescrs) {
		this.entityKey = entityKey;
		this.authorizedName = authorizedName;
		this.modified = modified;
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

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "businessEntity")
	@OrderBy
	public List<Contact> getContacts() {
		return this.contacts;
	}
	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "businessEntity")
	@OrderBy
	public List<BusinessIdentifier> getBusinessIdentifiers() {
		return this.businessIdentifiers;
	}
	public void setBusinessIdentifiers(
			List<BusinessIdentifier> businessIdentifiers) {
		this.businessIdentifiers = businessIdentifiers;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "businessEntityByFromKey")
	@OrderBy
	public List<PublisherAssertion> getPublisherAssertionsForFromKey() {
		return this.publisherAssertionsForFromKey;
	}
	public void setPublisherAssertionsForFromKey(
			List<PublisherAssertion> publisherAssertionsForFromKey) {
		this.publisherAssertionsForFromKey = publisherAssertionsForFromKey;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "businessEntity")
	@OrderBy
	public List<DiscoveryUrl> getDiscoveryUrls() {
		return this.discoveryUrls;
	}
	public void setDiscoveryUrls(List<DiscoveryUrl> discoveryUrls) {
		this.discoveryUrls = discoveryUrls;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "businessEntity")
	@OrderBy
	public List<BusinessName> getBusinessNames() {
		return this.businessNames;
	}
	public void setBusinessNames(List<BusinessName> businessNames) {
		this.businessNames = businessNames;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "businessEntityByToKey")
	@OrderBy
	public List<PublisherAssertion> getPublisherAssertionsForToKey() {
		return this.publisherAssertionsForToKey;
	}
	public void setPublisherAssertionsForToKey(
			List<PublisherAssertion> publisherAssertionsForToKey) {
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
	@OrderBy
	public List<BusinessService> getBusinessServices() {
		return this.businessServices;
	}
	public void setBusinessServices(List<BusinessService> businessServices) {
		this.businessServices = businessServices;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "businessEntity")
	@OrderBy
	public List<BusinessDescr> getBusinessDescrs() {
		return this.businessDescrs;
	}
	public void setBusinessDescrs(List<BusinessDescr> businessDescrs) {
		this.businessDescrs = businessDescrs;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "businessEntity")
	public List<ServiceProjection> getServiceProjections() {
		return serviceProjections;
	}
	public void setServiceProjections(List<ServiceProjection> serviceProjections) {
		this.serviceProjections = serviceProjections;
	}

        @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "businessEntity")
	@OrderBy
        public List<Signature> getSignatures() {
                return signatures;
        }

        public void setSignatures(List<Signature> signatures) {
                this.signatures = signatures;
        }
	
	
}
