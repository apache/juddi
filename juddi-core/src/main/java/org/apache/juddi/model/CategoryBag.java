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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 */
@Entity
@Table(name = "category_bag")
@Inheritance(strategy = InheritanceType.JOINED)

public class CategoryBag implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	private Long id;
	private Set<KeyedReference> keyedReferences = new HashSet<KeyedReference>(0);
	private Set<KeyedReferenceGroup> keyedReferenceGroups = new HashSet<KeyedReferenceGroup>(0);
	
	public CategoryBag() {
	}

	public CategoryBag(Long id) {
		this.id = id;
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
	public Set<KeyedReference> getKeyedReferences() {
		return keyedReferences;
	}
	public void setKeyedReferences(Set<KeyedReference> keyedReferences) {
		this.keyedReferences = keyedReferences;
	}
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "categoryBag")
	public Set<KeyedReferenceGroup> getKeyedReferenceGroups() {
		return keyedReferenceGroups;
	}
	public void setKeyedReferenceGroups(
			Set<KeyedReferenceGroup> keyedReferenceGroups) {
		this.keyedReferenceGroups = keyedReferenceGroups;
	}


}
