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
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@Embeddable
public class KeyGeneratorKeyId implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String publisherId;
	private int keygeneratorId;

	public KeyGeneratorKeyId() {
	}

	public KeyGeneratorKeyId(String publisherId, int keygeneratorId) {
		this.publisherId = publisherId;
		this.keygeneratorId = keygeneratorId;
	}

	@Column(name = "publisher_id", nullable = false, length = 20)
	public String getPublisherId() {
		return this.publisherId;
	}

	public void setPublisherId(String publisherId) {
		this.publisherId = publisherId;
	}

	@Column(name = "category_id", nullable = false)
	public int getKeygeneratorId() {
		return this.keygeneratorId;
	}

	public void setKeygeneratorId(int keygeneratorId) {
		this.keygeneratorId = keygeneratorId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof KeyGeneratorKeyId))
			return false;
		KeyGeneratorKeyId castOther = (KeyGeneratorKeyId) other;

		return ((this.getPublisherId() == castOther.getPublisherId()) || (this.getPublisherId() != null
				&& castOther.getPublisherId() != null && this.getPublisherId()
				.equals(castOther.getPublisherId())))
				&& (this.getKeygeneratorId() == castOther.getKeygeneratorId());
	}

	public int hashCode() {
		int result = 17;

		result = 37
				* result
				+ (getPublisherId() == null ? 0 : this.getPublisherId()
						.hashCode());
		result = 37 * result + this.getKeygeneratorId();
		return result;
	}

}
