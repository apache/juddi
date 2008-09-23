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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 */
@Entity
@Table(name = "contact")
public class Contact implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private ContactId id;
	private BusinessEntity businessEntity;
	private String useType;
	private String personName;
	private Set<ContactDescr> contactDescrs = new HashSet<ContactDescr>(0);
	private Set<Email> emails = new HashSet<Email>(0);
	private Set<Phone> phones = new HashSet<Phone>(0);
	private Set<Address> addresses = new HashSet<Address>(0);

	public Contact() {
	}

	public Contact(ContactId id, BusinessEntity businessEntity,
			String personName) {
		this.id = id;
		this.businessEntity = businessEntity;
		this.personName = personName;
	}
	public Contact(ContactId id, BusinessEntity businessEntity, String useType,
			String personName, Set<ContactDescr> contactDescrs,
			Set<Email> emails, Set<Phone> phones, Set<Address> addresses) {
		this.id = id;
		this.businessEntity = businessEntity;
		this.useType = useType;
		this.personName = personName;
		this.contactDescrs = contactDescrs;
		this.emails = emails;
		this.phones = phones;
		this.addresses = addresses;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "businessKey", column = @Column(name = "business_key", nullable = false, length = 255)),
			@AttributeOverride(name = "contactId", column = @Column(name = "contact_id", nullable = false))})

	public ContactId getId() {
		return this.id;
	}

	public void setId(ContactId id) {
		this.id = id;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "business_key", nullable = false, insertable = false, updatable = false)

	public BusinessEntity getBusinessEntity() {
		return this.businessEntity;
	}

	public void setBusinessEntity(BusinessEntity businessEntity) {
		this.businessEntity = businessEntity;
	}

	@Column(name = "use_type")
	public String getUseType() {
		return this.useType;
	}

	public void setUseType(String useType) {
		this.useType = useType;
	}

	@Column(name = "person_name", nullable = false)

	public String getPersonName() {
		return this.personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "contact")
	public Set<ContactDescr> getContactDescrs() {
		return this.contactDescrs;
	}

	public void setContactDescrs(Set<ContactDescr> contactDescrs) {
		this.contactDescrs = contactDescrs;
	}
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "contact")
	public Set<Email> getEmails() {
		return this.emails;
	}

	public void setEmails(Set<Email> emails) {
		this.emails = emails;
	}
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "contact")
	public Set<Phone> getPhones() {
		return this.phones;
	}

	public void setPhones(Set<Phone> phones) {
		this.phones = phones;
	}
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "contact")
	public Set<Address> getAddresses() {
		return this.addresses;
	}

	public void setAddresses(Set<Address> addresses) {
		this.addresses = addresses;
	}

}
