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
 *
 */
package org.apache.juddi.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */


@Embeddable
public class ServiceProjectionId implements java.io.Serializable {
	
	private static final long serialVersionUID = 3146022787279005205L;
	@Column(name = "business_key", nullable = false, length = 255)
	String businessKey;
	@Column(name = "service_key", nullable = false, length = 255)
	String serviceKey;
	
	public ServiceProjectionId() {
	}
	public ServiceProjectionId(String businessKey, String serviceKey) {
		this.businessKey = businessKey;
		this.serviceKey = serviceKey;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((businessKey == null) ? 0 : businessKey.hashCode());
		result = prime * result
				+ ((serviceKey == null) ? 0 : serviceKey.hashCode());
		return result;
	}
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServiceProjectionId other = (ServiceProjectionId) obj;
		if (businessKey == null) {
			if (other.businessKey != null)
				return false;
		} else if (!businessKey.equals(other.businessKey))
			return false;
		if (serviceKey == null) {
			if (other.serviceKey != null)
				return false;
		} else if (!serviceKey.equals(other.serviceKey))
			return false;
		return true;
	}
	
}
