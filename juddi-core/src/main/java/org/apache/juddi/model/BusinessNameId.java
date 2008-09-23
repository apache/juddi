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

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 */
@Embeddable
public class BusinessNameId implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String businessKey;
	private int businessNameId;

	public BusinessNameId() {
	}

	public BusinessNameId(String businessKey, int businessNameId) {
		this.businessKey = businessKey;
		this.businessNameId = businessNameId;
	}

	@Column(name = "business_key", nullable = false, length = 255)
	public String getBusinessKey() {
		return this.businessKey;
	}

	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}

	@Column(name = "business_name_id", nullable = false)

	public int getBusinessNameId() {
		return this.businessNameId;
	}

	public void setBusinessNameId(int businessNameId) {
		this.businessNameId = businessNameId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof BusinessNameId))
			return false;
		BusinessNameId castOther = (BusinessNameId) other;

		return ((this.getBusinessKey() == castOther.getBusinessKey()) || (this
				.getBusinessKey() != null
				&& castOther.getBusinessKey() != null && this.getBusinessKey()
				.equals(castOther.getBusinessKey())))
				&& (this.getBusinessNameId() == castOther.getBusinessNameId());
	}

	public int hashCode() {
		int result = 17;

		result = 37
				* result
				+ (getBusinessKey() == null ? 0 : this.getBusinessKey()
						.hashCode());
		result = 37 * result + this.getBusinessNameId();
		return result;
	}

}
