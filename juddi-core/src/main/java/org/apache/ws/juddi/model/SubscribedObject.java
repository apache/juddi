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
@Table(name = "subscribed_object")
public class SubscribedObject implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private SubscribedObjectId id;

	public SubscribedObject() {
	}

	public SubscribedObject(SubscribedObjectId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "subscribedObjectKey", column = @Column(name = "subscribed_object_key", nullable = false)),
			@AttributeOverride(name = "subscriptionKey", column = @Column(name = "subscription_key", nullable = false, length = 41))})

	public SubscribedObjectId getId() {
		return this.id;
	}

	public void setId(SubscribedObjectId id) {
		this.id = id;
	}

}
