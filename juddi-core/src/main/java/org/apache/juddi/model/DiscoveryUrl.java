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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 */
@Entity
@Table(name = "discovery_url")
public class DiscoveryUrl implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private DiscoveryUrlId id;
	private BusinessEntity businessEntity;
	private String useType;
	private String url;

	public DiscoveryUrl() {
	}

	public DiscoveryUrl(DiscoveryUrlId id, BusinessEntity businessEntity,
			String useType, String url) {
		this.id = id;
		this.businessEntity = businessEntity;
		this.useType = useType;
		this.url = url;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "businessKey", column = @Column(name = "business_key", nullable = false, length = 255)),
			@AttributeOverride(name = "discoveryUrlId", column = @Column(name = "discovery_url_id", nullable = false))})

	public DiscoveryUrlId getId() {
		return this.id;
	}

	public void setId(DiscoveryUrlId id) {
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

	@Column(name = "use_type", nullable = false)

	public String getUseType() {
		return this.useType;
	}

	public void setUseType(String useType) {
		this.useType = useType;
	}

	@Column(name = "url", nullable = false)

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
