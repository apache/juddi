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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@Entity
@Table(name = "j3_contact")
public class Contact implements java.io.Serializable {

	private static final long serialVersionUID = 3350341195850056589L;
	private Long id;
	private BusinessEntity businessEntity;
	private String useType;
	private List<PersonName> personNames = new ArrayList<PersonName>(0);
	private List<ContactDescr> contactDescrs = new ArrayList<ContactDescr>(0);
	private List<Email> emails = new ArrayList<Email>(0);
	private List<Phone> phones = new ArrayList<Phone>(0);
	private List<Address> addresses = new ArrayList<Address>(0);

	public Contact() {
	}

	public Contact(BusinessEntity businessEntity) {
		this.businessEntity = businessEntity;
	}
	public Contact(BusinessEntity businessEntity, String useType,
			List<PersonName> personNames, List<ContactDescr> contactDescrs,
			List<Email> emails, List<Phone> phones, List<Address> addresses) {
		this.businessEntity = businessEntity;
		this.useType = useType;
		this.personNames = personNames;
		this.contactDescrs = contactDescrs;
		this.emails = emails;
		this.phones = phones;
		this.addresses = addresses;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "entity_key", nullable = false)
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

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "contact")
	@OrderBy
	public List<PersonName> getPersonNames() {
		return this.personNames;
	}
	public void setPersonNames(List<PersonName> personNames) {
		this.personNames = personNames;
	}
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "contact")
	@OrderBy
	public List<ContactDescr> getContactDescrs() {
		return this.contactDescrs;
	}
	public void setContactDescrs(List<ContactDescr> contactDescrs) {
		this.contactDescrs = contactDescrs;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "contact")
	@OrderBy
	public List<Email> getEmails() {
		return this.emails;
	}
	public void setEmails(List<Email> emails) {
		this.emails = emails;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "contact")
	@OrderBy
	public List<Phone> getPhones() {
		return this.phones;
	}
	public void setPhones(List<Phone> phones) {
		this.phones = phones;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "contact")
	@OrderBy
	public List<Address> getAddresses() {
		return this.addresses;
	}
	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}

}
