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
@Table(name = "j3_address")
public class Address implements java.io.Serializable {

	private static final long serialVersionUID = 7767275374035531912L;
	private Long id;
	private Contact contact;
	private String useType;
	private String sortCode;
	private String tmodelKey;
	private List<AddressLine> addressLines = new ArrayList<AddressLine>(0);

	public Address() {
	}

	public Address(Contact contact) {
		this.contact = contact;
	}
	public Address(Long id, Contact contact, String useType,
			String sortCode, String tmodelKey, List<AddressLine> addressLines) {
		this.contact = contact;
		this.useType = useType;
		this.sortCode = sortCode;
		this.tmodelKey = tmodelKey;
		this.addressLines = addressLines;
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
	@JoinColumn(name = "address_id", nullable = false)
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

	@Column(name = "tmodel_key", length = 255)
	public String getTmodelKey() {
		return this.tmodelKey;
	}
	public void setTmodelKey(String tmodelKey) {
		this.tmodelKey = tmodelKey;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "address")
	@OrderBy
	public List<AddressLine> getAddressLines() {
		return this.addressLines;
	}
	public void setAddressLines(List<AddressLine> addressLines) {
		this.addressLines = addressLines;
	}

}
