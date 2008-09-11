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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 */
@Entity
@Table(name = "publisher")
public class Publisher implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String publisherId;
	private String publisherName;
	private String emailAddress;
	private String isAdmin;
	private String isEnabled;
	private Integer maxBusinesses;
	private Integer maxServicesPerBusiness;
	private Integer maxBindingsPerService;
	private Integer maxTmodels;

	public Publisher() {
	}

	public Publisher(String publisherId, String publisherName) {
		this.publisherId = publisherId;
		this.publisherName = publisherName;
	}
	public Publisher(String publisherId, String publisherName,
			String emailAddress, String isAdmin, String isEnabled,
			Integer maxBusinesses, Integer maxServicesPerBusiness,
			Integer maxBindingsPerService, Integer maxTmodels) {
		this.publisherId = publisherId;
		this.publisherName = publisherName;
		this.emailAddress = emailAddress;
		this.isAdmin = isAdmin;
		this.isEnabled = isEnabled;
		this.maxBusinesses = maxBusinesses;
		this.maxServicesPerBusiness = maxServicesPerBusiness;
		this.maxBindingsPerService = maxBindingsPerService;
		this.maxTmodels = maxTmodels;
	}

	@Id
	@Column(name = "publisher_id", unique = true, nullable = false, length = 20)
	public String getPublisherId() {
		return this.publisherId;
	}

	public void setPublisherId(String publisherId) {
		this.publisherId = publisherId;
	}

	@Column(name = "publisher_name", nullable = false)

	public String getPublisherName() {
		return this.publisherName;
	}

	public void setPublisherName(String publisherName) {
		this.publisherName = publisherName;
	}

	@Column(name = "email_address")
	public String getEmailAddress() {
		return this.emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	@Column(name = "is_admin", length = 5)
	public String getIsAdmin() {
		return this.isAdmin;
	}

	public void setIsAdmin(String isAdmin) {
		this.isAdmin = isAdmin;
	}

	@Column(name = "is_enabled", length = 5)
	public String getIsEnabled() {
		return this.isEnabled;
	}

	public void setIsEnabled(String isEnabled) {
		this.isEnabled = isEnabled;
	}

	@Column(name = "max_businesses")
	public Integer getMaxBusinesses() {
		return this.maxBusinesses;
	}

	public void setMaxBusinesses(Integer maxBusinesses) {
		this.maxBusinesses = maxBusinesses;
	}

	@Column(name = "max_services_per_business")
	public Integer getMaxServicesPerBusiness() {
		return this.maxServicesPerBusiness;
	}

	public void setMaxServicesPerBusiness(Integer maxServicesPerBusiness) {
		this.maxServicesPerBusiness = maxServicesPerBusiness;
	}

	@Column(name = "max_bindings_per_service")
	public Integer getMaxBindingsPerService() {
		return this.maxBindingsPerService;
	}

	public void setMaxBindingsPerService(Integer maxBindingsPerService) {
		this.maxBindingsPerService = maxBindingsPerService;
	}

	@Column(name = "max_tmodels")
	public Integer getMaxTmodels() {
		return this.maxTmodels;
	}

	public void setMaxTmodels(Integer maxTmodels) {
		this.maxTmodels = maxTmodels;
	}

}
