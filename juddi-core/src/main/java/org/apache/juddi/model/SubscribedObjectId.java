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
public class SubscribedObjectId implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int subscribedObjectKey;
	private String subscriptionKey;

	public SubscribedObjectId() {
	}

	public SubscribedObjectId(int subscribedObjectKey, String subscriptionKey) {
		this.subscribedObjectKey = subscribedObjectKey;
		this.subscriptionKey = subscriptionKey;
	}

	@Column(name = "subscribed_object_key", nullable = false)

	public int getSubscribedObjectKey() {
		return this.subscribedObjectKey;
	}

	public void setSubscribedObjectKey(int subscribedObjectKey) {
		this.subscribedObjectKey = subscribedObjectKey;
	}

	@Column(name = "subscription_key", nullable = false, length = 255)
	public String getSubscriptionKey() {
		return this.subscriptionKey;
	}

	public void setSubscriptionKey(String subscriptionKey) {
		this.subscriptionKey = subscriptionKey;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof SubscribedObjectId))
			return false;
		SubscribedObjectId castOther = (SubscribedObjectId) other;

		return (this.getSubscribedObjectKey() == castOther
				.getSubscribedObjectKey())
				&& ((this.getSubscriptionKey() == castOther
						.getSubscriptionKey()) || (this.getSubscriptionKey() != null
						&& castOther.getSubscriptionKey() != null && this
						.getSubscriptionKey().equals(
								castOther.getSubscriptionKey())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getSubscribedObjectKey();
		result = 37
				* result
				+ (getSubscriptionKey() == null ? 0 : this.getSubscriptionKey()
						.hashCode());
		return result;
	}

}
