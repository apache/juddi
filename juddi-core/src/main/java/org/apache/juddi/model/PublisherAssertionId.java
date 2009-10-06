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
public class PublisherAssertionId implements java.io.Serializable {

	private static final long serialVersionUID = -8329568181003010492L;
	private String fromKey;
	private String toKey;

	public PublisherAssertionId() {
	}

	public PublisherAssertionId(String fromKey, String toKey) {
		this.fromKey = fromKey;
		this.toKey = toKey;
	}

	@Column(name = "from_key", nullable = false, length = 255)
	public String getFromKey() {
		return this.fromKey;
	}

	public void setFromKey(String fromKey) {
		this.fromKey = fromKey;
	}

	@Column(name = "to_key", nullable = false, length = 255)
	public String getToKey() {
		return this.toKey;
	}

	public void setToKey(String toKey) {
		this.toKey = toKey;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof PublisherAssertionId))
			return false;
		PublisherAssertionId castOther = (PublisherAssertionId) other;

		return ((this.getFromKey() == castOther.getFromKey()) || (this
				.getFromKey() != null
				&& castOther.getFromKey() != null && this.getFromKey().equals(
				castOther.getFromKey())))
				&& ((this.getToKey() == castOther.getToKey()) || (this
						.getToKey() != null
						&& castOther.getToKey() != null && this.getToKey()
						.equals(castOther.getToKey())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getFromKey() == null ? 0 : this.getFromKey().hashCode());
		result = 37 * result
				+ (getToKey() == null ? 0 : this.getToKey().hashCode());
		return result;
	}

}
