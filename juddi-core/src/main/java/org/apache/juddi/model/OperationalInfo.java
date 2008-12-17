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
 *
 */

package org.apache.juddi.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@Entity
@Table(name = "operational_info")
public class OperationalInfo implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String entityKey;
	private Date created;
	private Date modified;
	private Date modifiedIncludingChildren;
	private String nodeId;
	private UddiEntityPublisher publisher;

	public OperationalInfo() {
		
	}

	public OperationalInfo(String entityKey, String nodeId,
			UddiEntityPublisher publisher) {
		super();
		this.entityKey = entityKey;
		this.nodeId = nodeId;
		this.publisher = publisher;
	}

	@Id
	@Column(name = "entity_key", nullable = false, length = 255)
	public String getEntityKey() {
		return entityKey;
	}
	public void setEntityKey(String entityKey) {
		this.entityKey = entityKey;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created", nullable = false, length = 29)
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	
	@Column(name = "modified", nullable = false, length = 29)
	public Date getModified() {
		return modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}

	@Column(name = "modified_including_children", length = 29)
	public Date getModifiedIncludingChildren() {
		return modifiedIncludingChildren;
	}
	public void setModifiedIncludingChildren(Date modifiedIncludingChildren) {
		this.modifiedIncludingChildren = modifiedIncludingChildren;
	}

	@Column(name = "node_id", nullable = false, length = 255)
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "authorized_name", nullable = false)
	public UddiEntityPublisher getPublisher() {
		return publisher;
	}
	public void setPublisher(UddiEntityPublisher publisher) {
		this.publisher = publisher;
	}
	
}
