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
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 */
@Entity
@Table(name = "subscription")
public class Subscription implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String subscriptionKey;
	private String bindingKey;
	private String notificationInterval;

	public Subscription() {
	}

	public Subscription(String subscriptionKey, String bindingKey,
			String notificationInterval) {
		this.subscriptionKey = subscriptionKey;
		this.bindingKey = bindingKey;
		this.notificationInterval = notificationInterval;
	}

	@Id
	@Column(name = "subscription_key", unique = true, nullable = false, length = 41)
	public String getSubscriptionKey() {
		return this.subscriptionKey;
	}

	public void setSubscriptionKey(String subscriptionKey) {
		this.subscriptionKey = subscriptionKey;
	}

	@Column(name = "binding_key", nullable = false, length = 41)
	public String getBindingKey() {
		return this.bindingKey;
	}

	public void setBindingKey(String bindingKey) {
		this.bindingKey = bindingKey;
	}

	@Column(name = "notification_interval", nullable = false)

	public String getNotificationInterval() {
		return this.notificationInterval;
	}

	public void setNotificationInterval(String notificationInterval) {
		this.notificationInterval = notificationInterval;
	}

}
