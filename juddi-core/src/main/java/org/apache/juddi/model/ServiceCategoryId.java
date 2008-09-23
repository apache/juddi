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
public class ServiceCategoryId implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String serviceKey;
	private int categoryId;

	public ServiceCategoryId() {
	}

	public ServiceCategoryId(String serviceKey, int categoryId) {
		this.serviceKey = serviceKey;
		this.categoryId = categoryId;
	}

	@Column(name = "service_key", nullable = false, length = 255)
	public String getServiceKey() {
		return this.serviceKey;
	}

	public void setServiceKey(String serviceKey) {
		this.serviceKey = serviceKey;
	}

	@Column(name = "category_id", nullable = false)

	public int getCategoryId() {
		return this.categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof ServiceCategoryId))
			return false;
		ServiceCategoryId castOther = (ServiceCategoryId) other;

		return ((this.getServiceKey() == castOther.getServiceKey()) || (this
				.getServiceKey() != null
				&& castOther.getServiceKey() != null && this.getServiceKey()
				.equals(castOther.getServiceKey())))
				&& (this.getCategoryId() == castOther.getCategoryId());
	}

	public int hashCode() {
		int result = 17;

		result = 37
				* result
				+ (getServiceKey() == null ? 0 : this.getServiceKey()
						.hashCode());
		result = 37 * result + this.getCategoryId();
		return result;
	}

}
