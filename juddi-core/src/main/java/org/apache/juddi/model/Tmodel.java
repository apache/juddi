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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 */
@Entity
@Table(name = "tmodel")
public class Tmodel implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String tmodelKey;
	private UddiEntity uddiEntity;
	private String authorizedName;
	private String publisherId;
	private String operator;
	private String name;
	private String langCode;
	private String deleted;
	private Date lastUpdate;
	private Set<TmodelDescr> tmodelDescrs = new HashSet<TmodelDescr>(0);
	private Set<TmodelDocDescr> tmodelDocDescrs = new HashSet<TmodelDocDescr>(0);
	private Set<TmodelIdentifier> tmodelIdentifiers = new HashSet<TmodelIdentifier>(
			0);
	private Set<TmodelCategory> tmodelCategories = new HashSet<TmodelCategory>(
			0);

	public Tmodel() {
	}

	public Tmodel(String tmodelKey, UddiEntity uddiEntity,
			String authorizedName, String operator, String name, Date lastUpdate) {
		this.tmodelKey = tmodelKey;
		this.uddiEntity = uddiEntity;
		this.authorizedName = authorizedName;
		this.operator = operator;
		this.name = name;
		this.lastUpdate = lastUpdate;
	}
	public Tmodel(String tmodelKey, UddiEntity uddiEntity,
			String authorizedName, String publisherId, String operator,
			String name, String langCode, String deleted, Date lastUpdate,
			Set<TmodelDescr> tmodelDescrs, Set<TmodelDocDescr> tmodelDocDescrs,
			Set<TmodelIdentifier> tmodelIdentifiers,
			Set<TmodelCategory> tmodelCategories) {
		this.tmodelKey = tmodelKey;
		this.uddiEntity = uddiEntity;
		this.authorizedName = authorizedName;
		this.publisherId = publisherId;
		this.operator = operator;
		this.name = name;
		this.langCode = langCode;
		this.deleted = deleted;
		this.lastUpdate = lastUpdate;
		this.tmodelDescrs = tmodelDescrs;
		this.tmodelDocDescrs = tmodelDocDescrs;
		this.tmodelIdentifiers = tmodelIdentifiers;
		this.tmodelCategories = tmodelCategories;
	}

	@Id
	@Column(name = "tmodel_key", unique = true, nullable = false, length = 41)
	public String getTmodelKey() {
		return this.tmodelKey;
	}

	public void setTmodelKey(String tmodelKey) {
		this.tmodelKey = tmodelKey;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tmodel_key", unique = true, nullable = false, insertable = false, updatable = false)

	public UddiEntity getUddiEntity() {
		return this.uddiEntity;
	}

	public void setUddiEntity(UddiEntity uddiEntity) {
		this.uddiEntity = uddiEntity;
	}

	@Column(name = "authorized_name", nullable = false)

	public String getAuthorizedName() {
		return this.authorizedName;
	}

	public void setAuthorizedName(String authorizedName) {
		this.authorizedName = authorizedName;
	}

	@Column(name = "publisher_id", length = 20)
	public String getPublisherId() {
		return this.publisherId;
	}

	public void setPublisherId(String publisherId) {
		this.publisherId = publisherId;
	}

	@Column(name = "operator", nullable = false)

	public String getOperator() {
		return this.operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	@Column(name = "name", nullable = false)

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "lang_code", length = 5)
	public String getLangCode() {
		return this.langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	@Column(name = "deleted", length = 5)
	public String getDeleted() {
		return this.deleted;
	}

	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_update", nullable = false, length = 29)

	public Date getLastUpdate() {
		return this.lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tmodel")
	public Set<TmodelDescr> getTmodelDescrs() {
		return this.tmodelDescrs;
	}

	public void setTmodelDescrs(Set<TmodelDescr> tmodelDescrs) {
		this.tmodelDescrs = tmodelDescrs;
	}
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tmodel")
	public Set<TmodelDocDescr> getTmodelDocDescrs() {
		return this.tmodelDocDescrs;
	}

	public void setTmodelDocDescrs(Set<TmodelDocDescr> tmodelDocDescrs) {
		this.tmodelDocDescrs = tmodelDocDescrs;
	}
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tmodel")
	public Set<TmodelIdentifier> getTmodelIdentifiers() {
		return this.tmodelIdentifiers;
	}

	public void setTmodelIdentifiers(Set<TmodelIdentifier> tmodelIdentifiers) {
		this.tmodelIdentifiers = tmodelIdentifiers;
	}
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tmodel")
	public Set<TmodelCategory> getTmodelCategories() {
		return this.tmodelCategories;
	}

	public void setTmodelCategories(Set<TmodelCategory> tmodelCategories) {
		this.tmodelCategories = tmodelCategories;
	}

}
