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
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@Entity
@Table(name = "j3_subscription_chunk_token")
public class SubscriptionChunkToken implements java.io.Serializable  {
	
	private static final long serialVersionUID = 3839994259992953856L;
	private String chunkToken;
	private String subscriptionKey;
	private Date startPoint;
	private Date endPoint;
	private Integer data;
	private Date expiresAfter;
	
	public SubscriptionChunkToken() {
		
	}
	
	public SubscriptionChunkToken(String chunkToken) {
		this.chunkToken = chunkToken;
	}
	
	@Id
	@Column(name = "chunk_token", nullable = false, length = 255)
	public String getChunkToken() {
		return chunkToken;
	}
	public void setChunkToken(String chunkToken) {
		this.chunkToken = chunkToken;
	}
	
	@Column(name = "subscription_key", nullable = false, length = 255)
	public String getSubscriptionKey() {
		return subscriptionKey;
	}
	public void setSubscriptionKey(String subscriptionKey) {
		this.subscriptionKey = subscriptionKey;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_point", length = 29)
	public Date getStartPoint() {
		return startPoint;
	}
	public void setStartPoint(Date startPoint) {
		this.startPoint = startPoint;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_point", length = 29)
	public Date getEndPoint() {
		return endPoint;
	}
	public void setEndPoint(Date endPoint) {
		this.endPoint = endPoint;
	}
	
	@Column(name = "data", nullable = false)
	public Integer getData() {
		return data;
	}
	public void setData(Integer data) {
		this.data = data;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "expires_after", nullable = false, length = 29)
	public Date getExpiresAfter() {
		return expiresAfter;
	}
	public void setExpiresAfter(Date expiresAfter) {
		this.expiresAfter = expiresAfter;
	}

	
}
