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
public class CategoryBagId implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String categoryBagKey;
	private String tmodelKeyRef;

	public CategoryBagId() {
	}

	public CategoryBagId(String categoryBagKey) {
		this.categoryBagKey = categoryBagKey;
	}
	public CategoryBagId(String categoryBagKey, String tmodelKeyRef) {
		this.categoryBagKey = categoryBagKey;
		this.tmodelKeyRef = tmodelKeyRef;
	}

	@Column(name = "category_bag_key", nullable = false, length = 255)
	public String getCategoryBagKey() {
		return this.categoryBagKey;
	}

	public void setCategoryBagKey(String categoryBagKey) {
		this.categoryBagKey = categoryBagKey;
	}

	@Column(name = "tmodel_key_ref", length = 255)
	public String getTmodelKeyRef() {
		return this.tmodelKeyRef;
	}

	public void setTmodelKeyRef(String tmodelKeyRef) {
		this.tmodelKeyRef = tmodelKeyRef;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof CategoryBagId))
			return false;
		CategoryBagId castOther = (CategoryBagId) other;

		return ((this.getCategoryBagKey() == castOther.getCategoryBagKey()) || (this
				.getCategoryBagKey() != null
				&& castOther.getCategoryBagKey() != null && this
				.getCategoryBagKey().equals(castOther.getCategoryBagKey())))
				&& ((this.getTmodelKeyRef() == castOther.getTmodelKeyRef()) || (this
						.getTmodelKeyRef() != null
						&& castOther.getTmodelKeyRef() != null && this
						.getTmodelKeyRef().equals(castOther.getTmodelKeyRef())));
	}

	public int hashCode() {
		int result = 17;

		result = 37
				* result
				+ (getCategoryBagKey() == null ? 0 : this.getCategoryBagKey()
						.hashCode());
		result = 37
				* result
				+ (getTmodelKeyRef() == null ? 0 : this.getTmodelKeyRef()
						.hashCode());
		return result;
	}

}
