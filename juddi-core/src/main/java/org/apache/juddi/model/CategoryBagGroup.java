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
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 */
@Entity
@Table(name = "category_bag_group")
public class CategoryBagGroup implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int bagGroupId;
	private String bindingKey;
	private String categoryBagKey;

	public CategoryBagGroup() {
	}

	public CategoryBagGroup(int bagGroupId, String bindingKey,
			String categoryBagKey) {
		this.bagGroupId = bagGroupId;
		this.bindingKey = bindingKey;
		this.categoryBagKey = categoryBagKey;
	}

	@Id
	@Column(name = "bag_group_id", unique = true, nullable = false)

	public int getBagGroupId() {
		return this.bagGroupId;
	}

	public void setBagGroupId(int bagGroupId) {
		this.bagGroupId = bagGroupId;
	}

	@Column(name = "binding_key", nullable = false, length = 41)
	public String getBindingKey() {
		return this.bindingKey;
	}

	public void setBindingKey(String bindingKey) {
		this.bindingKey = bindingKey;
	}

	@Column(name = "category_bag_key", nullable = false, length = 41)
	public String getCategoryBagKey() {
		return this.categoryBagKey;
	}

	public void setCategoryBagKey(String categoryBagKey) {
		this.categoryBagKey = categoryBagKey;
	}

}
