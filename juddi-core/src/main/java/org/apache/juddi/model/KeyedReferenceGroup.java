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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 */
@Entity
@Table(name = "j3_keyed_reference_group")
public class KeyedReferenceGroup implements java.io.Serializable {

	private static final long serialVersionUID = 837654280200149969L;
	private Long id;
	private CategoryBag categoryBag;
	private String tmodelKey;
	private List<KeyedReference> keyedReferences = new ArrayList<KeyedReference>(0);
	

	public KeyedReferenceGroup() {
	}

	public KeyedReferenceGroup(CategoryBag categoryBag, String tmodelKey) {
		this.categoryBag = categoryBag;
		this.tmodelKey = tmodelKey;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "tmodel_key", length = 255)
	public String getTmodelKey() {
		return this.tmodelKey;
	}
	public void setTmodelKey(String tmodelKey) {
		this.tmodelKey = tmodelKey;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_bag_id", nullable = false)
	public CategoryBag getCategoryBag() {
		return this.categoryBag;
	}
	public void setCategoryBag(CategoryBag categoryBag) {
		this.categoryBag = categoryBag;
	}
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "keyedReferenceGroup")
	@OrderBy
	public List<KeyedReference> getKeyedReferences() {
		return keyedReferences;
	}
	public void setKeyedReferences(List<KeyedReference> keyedReferences) {
		this.keyedReferences = keyedReferences;
	}

}
