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
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 */
@Entity
@Table(name = "j3_publisher_assertion")
public class PublisherAssertion implements java.io.Serializable {

	private static final long serialVersionUID = -5285434317957104272L;
	private PublisherAssertionId id;
	private BusinessEntity businessEntityByToKey;
	private BusinessEntity businessEntityByFromKey;
	private String tmodelKey;
	private String keyName;
	private String keyValue;
	private String fromCheck;
	private String toCheck;

	public PublisherAssertion() {
	}

	public PublisherAssertion(PublisherAssertionId id,
			BusinessEntity businessEntityByToKey,
			BusinessEntity businessEntityByFromKey, String tmodelKey,
			String keyName, String keyValue, String fromCheck, String toCheck) {
		this.id = id;
		this.businessEntityByToKey = businessEntityByToKey;
		this.businessEntityByFromKey = businessEntityByFromKey;
		this.tmodelKey = tmodelKey;
		this.keyName = keyName;
		this.keyValue = keyValue;
		this.fromCheck = fromCheck;
		this.toCheck = toCheck;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "fromKey", column = @Column(name = "from_key", nullable = false, length = 255)),
			@AttributeOverride(name = "toKey", column = @Column(name = "to_key", nullable = false, length = 255))})

	public PublisherAssertionId getId() {
		return this.id;
	}

	public void setId(PublisherAssertionId id) {
		this.id = id;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "to_key", nullable = false, insertable = false, updatable = false)

	public BusinessEntity getBusinessEntityByToKey() {
		return this.businessEntityByToKey;
	}

	public void setBusinessEntityByToKey(BusinessEntity businessEntityByToKey) {
		this.businessEntityByToKey = businessEntityByToKey;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "from_key", nullable = false, insertable = false, updatable = false)

	public BusinessEntity getBusinessEntityByFromKey() {
		return this.businessEntityByFromKey;
	}

	public void setBusinessEntityByFromKey(
			BusinessEntity businessEntityByFromKey) {
		this.businessEntityByFromKey = businessEntityByFromKey;
	}

	@Column(name = "tmodel_key", nullable = false, length = 255)
	public String getTmodelKey() {
		return this.tmodelKey;
	}

	public void setTmodelKey(String tmodelKey) {
		this.tmodelKey = tmodelKey;
	}

	@Column(name = "key_name", nullable = false)

	public String getKeyName() {
		return this.keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	@Column(name = "key_value", nullable = false)

	public String getKeyValue() {
		return this.keyValue;
	}

	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}

	@Column(name = "from_check", nullable = false, length = 5)
	public String getFromCheck() {
		return this.fromCheck;
	}

	public void setFromCheck(String fromCheck) {
		this.fromCheck = fromCheck;
	}

	@Column(name = "to_check", nullable = false, length = 5)
	public String getToCheck() {
		return this.toCheck;
	}

	public void setToCheck(String toCheck) {
		this.toCheck = toCheck;
	}
}
