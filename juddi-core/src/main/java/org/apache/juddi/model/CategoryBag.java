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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 */
@Entity
@Table(name = "category_bag")
public class CategoryBag implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	private CategoryBagId id;

	public CategoryBag() {
	}

	public CategoryBag(CategoryBagId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "categoryBagKey", column = @Column(name = "category_bag_key", nullable = false, length = 41)),
			@AttributeOverride(name = "tmodelKeyRef", column = @Column(name = "tmodel_key_ref", length = 41))})

	public CategoryBagId getId() {
		return this.id;
	}

	public void setId(CategoryBagId id) {
		this.id = id;
	}

}
