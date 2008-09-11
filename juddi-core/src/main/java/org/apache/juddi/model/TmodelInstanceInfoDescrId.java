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
public class TmodelInstanceInfoDescrId implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String bindingKey;
	private int tmodelInstanceInfoId;
	private int tmodelInstanceInfoDescrId;

	public TmodelInstanceInfoDescrId() {
	}

	public TmodelInstanceInfoDescrId(String bindingKey,
			int tmodelInstanceInfoId, int tmodelInstanceInfoDescrId) {
		this.bindingKey = bindingKey;
		this.tmodelInstanceInfoId = tmodelInstanceInfoId;
		this.tmodelInstanceInfoDescrId = tmodelInstanceInfoDescrId;
	}

	@Column(name = "binding_key", nullable = false, length = 41)
	public String getBindingKey() {
		return this.bindingKey;
	}

	public void setBindingKey(String bindingKey) {
		this.bindingKey = bindingKey;
	}

	@Column(name = "tmodel_instance_info_id", nullable = false)

	public int getTmodelInstanceInfoId() {
		return this.tmodelInstanceInfoId;
	}

	public void setTmodelInstanceInfoId(int tmodelInstanceInfoId) {
		this.tmodelInstanceInfoId = tmodelInstanceInfoId;
	}

	@Column(name = "tmodel_instance_info_descr_id", nullable = false)

	public int getTmodelInstanceInfoDescrId() {
		return this.tmodelInstanceInfoDescrId;
	}

	public void setTmodelInstanceInfoDescrId(int tmodelInstanceInfoDescrId) {
		this.tmodelInstanceInfoDescrId = tmodelInstanceInfoDescrId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof TmodelInstanceInfoDescrId))
			return false;
		TmodelInstanceInfoDescrId castOther = (TmodelInstanceInfoDescrId) other;

		return ((this.getBindingKey() == castOther.getBindingKey()) || (this
				.getBindingKey() != null
				&& castOther.getBindingKey() != null && this.getBindingKey()
				.equals(castOther.getBindingKey())))
				&& (this.getTmodelInstanceInfoId() == castOther
						.getTmodelInstanceInfoId())
				&& (this.getTmodelInstanceInfoDescrId() == castOther
						.getTmodelInstanceInfoDescrId());
	}

	public int hashCode() {
		int result = 17;

		result = 37
				* result
				+ (getBindingKey() == null ? 0 : this.getBindingKey()
						.hashCode());
		result = 37 * result + this.getTmodelInstanceInfoId();
		result = 37 * result + this.getTmodelInstanceInfoDescrId();
		return result;
	}

}
