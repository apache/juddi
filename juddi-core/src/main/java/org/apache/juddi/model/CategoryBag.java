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
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 */
@Entity
@Table(name = "j3_category_bag")
@Inheritance(strategy = InheritanceType.JOINED)

public class CategoryBag implements java.io.Serializable {
	
	private static final long serialVersionUID = 972745223104626841L;
	private Long id;
	private List<KeyedReference> keyedReferences = new ArrayList<KeyedReference>(0);
	private List<KeyedReferenceGroup> keyedReferenceGroups = new ArrayList<KeyedReferenceGroup>(0);
	
	public CategoryBag() {
	}

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "categoryBag")
	@OrderBy
	public List<KeyedReference> getKeyedReferences() {
		return keyedReferences;
	}
	public void setKeyedReferences(List<KeyedReference> keyedReferences) {
		this.keyedReferences = keyedReferences;
	}
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "categoryBag")
	@OrderBy
	public List<KeyedReferenceGroup> getKeyedReferenceGroups() {
		return keyedReferenceGroups;
	}
	public void setKeyedReferenceGroups(
			List<KeyedReferenceGroup> keyedReferenceGroups) {
		this.keyedReferenceGroups = keyedReferenceGroups;
	}


}
