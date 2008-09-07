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

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 */
@Embeddable
public class BindingDescrId implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String bindingKey;
	private int bindingDescrId;

	public BindingDescrId() {
	}

	public BindingDescrId(String bindingKey, int bindingDescrId) {
		this.bindingKey = bindingKey;
		this.bindingDescrId = bindingDescrId;
	}

	@Column(name = "binding_key", nullable = false, length = 41)
	public String getBindingKey() {
		return this.bindingKey;
	}

	public void setBindingKey(String bindingKey) {
		this.bindingKey = bindingKey;
	}

	@Column(name = "binding_descr_id", nullable = false)

	public int getBindingDescrId() {
		return this.bindingDescrId;
	}

	public void setBindingDescrId(int bindingDescrId) {
		this.bindingDescrId = bindingDescrId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof BindingDescrId))
			return false;
		BindingDescrId castOther = (BindingDescrId) other;

		return ((this.getBindingKey() == castOther.getBindingKey()) || (this
				.getBindingKey() != null
				&& castOther.getBindingKey() != null && this.getBindingKey()
				.equals(castOther.getBindingKey())))
				&& (this.getBindingDescrId() == castOther.getBindingDescrId());
	}

	public int hashCode() {
		int result = 17;

		result = 37
				* result
				+ (getBindingKey() == null ? 0 : this.getBindingKey()
						.hashCode());
		result = 37 * result + this.getBindingDescrId();
		return result;
	}

}
