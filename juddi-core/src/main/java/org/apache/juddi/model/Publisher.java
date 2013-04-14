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
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@Entity
@Table(name = "j3_publisher")
//@PrimaryKeyJoinColumn(name = "authorized_name")
public class Publisher extends UddiEntityPublisher implements java.io.Serializable {

	private static final long serialVersionUID = 1960575191518050887L;
	private String publisherName;
	private String emailAddress;
	private String isAdmin;
	private String isEnabled;
	private Integer maxBusinesses;
	private Integer maxServicesPerBusiness;
	private Integer maxBindingsPerService;
	private Integer maxTmodels;
    private List<Signature> signatures = new ArrayList<Signature>(0);

	public Publisher() {
		super(null);
	}

	public Publisher(String publisherId, String publisherName) {
		super(publisherId);
		this.authorizedName = publisherId;
		this.publisherName = publisherName;
	}
	public Publisher(String publisherId, String publisherName,
			String emailAddress, String isAdmin, String isEnabled,
			Integer maxBusinesses, Integer maxServicesPerBusiness,
			Integer maxBindingsPerService, Integer maxTmodels) {

		super(publisherId);
		this.authorizedName = publisherId;
		this.publisherName = publisherName;
		this.emailAddress = emailAddress;
		this.isAdmin = isAdmin;
		this.isEnabled = isEnabled;
		this.maxBusinesses = maxBusinesses;
		this.maxServicesPerBusiness = maxServicesPerBusiness;
		this.maxBindingsPerService = maxBindingsPerService;
		this.maxTmodels = maxTmodels;
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

	@Transient
	public boolean isAdmin() {
		boolean ret = false;
		if (getIsAdmin() != null) {
			if (getIsAdmin().equalsIgnoreCase("true"))
				ret = true;
		}
		return ret;
	}
	
	@Column(name = "is_enabled", length = 5)
	public String getIsEnabled() {
		return this.isEnabled;
	}
	public void setIsEnabled(String isEnabled) {
		this.isEnabled = isEnabled;
	}

	@Transient
	public boolean isEnabled() {
		boolean ret = false;
		if (getIsEnabled() != null) {
			if (getIsEnabled().equalsIgnoreCase("true"))
				ret = true;
		}
		return ret;
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

        @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "publisher")
	@OrderBy
        public List<Signature> getSignatures() {
                return signatures;
        }

        public void setSignatures(List<Signature> signatures) {
                this.signatures = signatures;
        }
}
