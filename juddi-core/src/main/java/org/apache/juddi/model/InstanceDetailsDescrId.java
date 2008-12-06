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
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@Embeddable
public class InstanceDetailsDescrId extends TmodelInstanceInfoId implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int instanceDetailsDescrId;

	public InstanceDetailsDescrId() {
	}

	public InstanceDetailsDescrId(String entityKey, int tmodelInstanceInfoId,
			int instanceDetailsDescrId) {
		this.entityKey = entityKey;
		this.tmodelInstanceInfoId = tmodelInstanceInfoId;
		this.instanceDetailsDescrId = instanceDetailsDescrId;
	}

	@Column(name = "descr_id", nullable = false)
	public int getInstanceDetailsDescrId() {
		return this.instanceDetailsDescrId;
	}
	public void setInstanceDetailsDescrId(int instanceDetailsDescrId) {
		this.instanceDetailsDescrId = instanceDetailsDescrId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof InstanceDetailsDescrId))
			return false;
		InstanceDetailsDescrId castOther = (InstanceDetailsDescrId) other;

		return ((this.getEntityKey() == castOther.getEntityKey()) || (this
				.getEntityKey() != null
				&& castOther.getEntityKey() != null && this.getEntityKey()
				.equals(castOther.getEntityKey())))
				&& (this.getTmodelInstanceInfoId() == castOther
						.getTmodelInstanceInfoId())
				&& (this.getInstanceDetailsDescrId() == castOther
						.getInstanceDetailsDescrId());
	}

	public int hashCode() {
		int result = 17;

		result = 37
				* result
				+ (getEntityKey() == null ? 0 : this.getEntityKey()
						.hashCode());
		result = 37 * result + this.getTmodelInstanceInfoId();
		result = 37 * result + this.getInstanceDetailsDescrId();
		return result;
	}

}
