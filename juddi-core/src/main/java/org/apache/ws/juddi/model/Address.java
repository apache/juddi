package org.apache.ws.juddi.model;
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
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 */
@Entity
@Table(name = "address")
public class Address implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private AddressId id;
	private Contact contact;
	private String useType;
	private String sortCode;
	private String tmodelKey;
	private Set<AddressLine> addressLines = new HashSet<AddressLine>(0);

	public Address() {
	}

	public Address(AddressId id, Contact contact) {
		this.id = id;
		this.contact = contact;
	}
	public Address(AddressId id, Contact contact, String useType,
			String sortCode, String tmodelKey, Set<AddressLine> addressLines) {
		this.id = id;
		this.contact = contact;
		this.useType = useType;
		this.sortCode = sortCode;
		this.tmodelKey = tmodelKey;
		this.addressLines = addressLines;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "businessKey", column = @Column(name = "business_key", nullable = false, length = 41)),
			@AttributeOverride(name = "contactId", column = @Column(name = "contact_id", nullable = false)),
			@AttributeOverride(name = "addressId", column = @Column(name = "address_id", nullable = false))})

	public AddressId getId() {
		return this.id;
	}

	public void setId(AddressId id) {
		this.id = id;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "business_key", referencedColumnName = "business_key", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "contact_id", referencedColumnName = "contact_id", nullable = false, insertable = false, updatable = false)})

	public Contact getContact() {
		return this.contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	@Column(name = "use_type")
	public String getUseType() {
		return this.useType;
	}

	public void setUseType(String useType) {
		this.useType = useType;
	}

	@Column(name = "sort_code", length = 10)
	public String getSortCode() {
		return this.sortCode;
	}

	public void setSortCode(String sortCode) {
		this.sortCode = sortCode;
	}

	@Column(name = "tmodel_key", length = 41)
	public String getTmodelKey() {
		return this.tmodelKey;
	}

	public void setTmodelKey(String tmodelKey) {
		this.tmodelKey = tmodelKey;
	}
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "address")
	public Set<AddressLine> getAddressLines() {
		return this.addressLines;
	}

	public void setAddressLines(Set<AddressLine> addressLines) {
		this.addressLines = addressLines;
	}

}
