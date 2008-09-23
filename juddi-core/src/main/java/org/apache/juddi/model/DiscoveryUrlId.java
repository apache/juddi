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
public class DiscoveryUrlId implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String businessKey;
	private int discoveryUrlId;

	public DiscoveryUrlId() {
	}

	public DiscoveryUrlId(String businessKey, int discoveryUrlId) {
		this.businessKey = businessKey;
		this.discoveryUrlId = discoveryUrlId;
	}

	@Column(name = "business_key", nullable = false, length = 255)
	public String getBusinessKey() {
		return this.businessKey;
	}

	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}

	@Column(name = "discovery_url_id", nullable = false)

	public int getDiscoveryUrlId() {
		return this.discoveryUrlId;
	}

	public void setDiscoveryUrlId(int discoveryUrlId) {
		this.discoveryUrlId = discoveryUrlId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof DiscoveryUrlId))
			return false;
		DiscoveryUrlId castOther = (DiscoveryUrlId) other;

		return ((this.getBusinessKey() == castOther.getBusinessKey()) || (this
				.getBusinessKey() != null
				&& castOther.getBusinessKey() != null && this.getBusinessKey()
				.equals(castOther.getBusinessKey())))
				&& (this.getDiscoveryUrlId() == castOther.getDiscoveryUrlId());
	}

	public int hashCode() {
		int result = 17;

		result = 37
				* result
				+ (getBusinessKey() == null ? 0 : this.getBusinessKey()
						.hashCode());
		result = 37 * result + this.getDiscoveryUrlId();
		return result;
	}

}
