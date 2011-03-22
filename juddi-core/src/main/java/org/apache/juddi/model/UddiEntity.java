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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@Entity
@Table(name = "j3_uddi_entity")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class UddiEntity implements Comparable<UddiEntity>{

	protected String entityKey;
	protected Date created;
	protected Date modified;
	protected Date modifiedIncludingChildren;
	protected String nodeId;
	protected String authorizedName;
	
	@Id
	@Column(name = "entity_key", nullable = false, length = 255)
	public String getEntityKey() {
		return entityKey;
	}
	public void setEntityKey(String entityKey) {
		this.entityKey = entityKey;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created", length = 29)
	public Date getCreated() {
		if (created!=null) {
			return new Date(created.getTime());
		} else {
			return null;
		}
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modified", nullable = false, length = 29)
	public Date getModified() {
		if (modified!=null) {
			return new Date(modified.getTime());
		} else {
			return null;
		}
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modified_including_children", length = 29)
	public Date getModifiedIncludingChildren() {
		if (modifiedIncludingChildren!=null) {
			return new Date(modifiedIncludingChildren.getTime());
		} else {
			return null;
		}
	}
	public void setModifiedIncludingChildren(Date modifiedIncludingChildren) {
		this.modifiedIncludingChildren = modifiedIncludingChildren;
	}
	
	@Column(name = "node_id", length = 255)
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	
	@Column(name = "authorized_name", nullable=false, length = 255)
	public String getAuthorizedName() {
		return authorizedName;
	}
	public void setAuthorizedName(String authorizedName) {
		this.authorizedName = authorizedName;
	}
	
	public int compareTo(UddiEntity o) {
		if (o==null || o.getEntityKey()==null) return 0;
		if (o.getEntityKey().equals(getEntityKey())) return 1;
		else return 0;
	}

}
