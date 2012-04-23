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


package org.apache.juddi.portlets.client.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 * <p>Java class for Publisher type.  Specific to juddi.
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a> 
 * 
 */
public class Publisher implements Serializable{
	private static final long serialVersionUID = 2384133177092719820L;
	@NotNull
    @Size(min=1,max=50)
	protected String publisherName;
    protected String emailAddress;
    protected String isAdmin;
    protected String isEnabled;
    protected Integer maxBindingsPerService;
    protected Integer maxBusinesses;
    protected Integer maxServicePerBusiness;
    protected Integer maxTModels;
    @NotNull
    @Size(min=1,max=20)
    protected String authorizedName;

    /**
	 * @return the publisherName
	 */
	public String getPublisherName() {
		return publisherName;
	}

	/**
	 * @param publisherName the publisherName to set
	 */
	public void setPublisherName(String publisherName) {
		this.publisherName = publisherName;
	}

	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * @param emailAddress the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * @return the isAdmin
	 */
	public String getIsAdmin() {
		return isAdmin;
	}

	/**
	 * @param isAdmin the isAdmin to set
	 */
	public void setIsAdmin(String isAdmin) {
		this.isAdmin = isAdmin;
	}

	/**
	 * @return the isEnabled
	 */
	public String getIsEnabled() {
		return isEnabled;
	}

	/**
	 * @param isEnabled the isEnabled to set
	 */
	public void setIsEnabled(String isEnabled) {
		this.isEnabled = isEnabled;
	}

	/**
	 * @return the maxBindingsPerService
	 */
	public Integer getMaxBindingsPerService() {
		return maxBindingsPerService;
	}

	/**
	 * @param maxBindingsPerService the maxBindingsPerService to set
	 */
	public void setMaxBindingsPerService(Integer maxBindingsPerService) {
		this.maxBindingsPerService = maxBindingsPerService;
	}

	/**
	 * @return the maxBusinesses
	 */
	public Integer getMaxBusinesses() {
		return maxBusinesses;
	}

	/**
	 * @param maxBusinesses the maxBusinesses to set
	 */
	public void setMaxBusinesses(Integer maxBusinesses) {
		this.maxBusinesses = maxBusinesses;
	}

	/**
	 * @return the maxServicePerBusiness
	 */
	public Integer getMaxServicePerBusiness() {
		return maxServicePerBusiness;
	}

	/**
	 * @param maxServicePerBusiness the maxServicePerBusiness to set
	 */
	public void setMaxServicePerBusiness(Integer maxServicePerBusiness) {
		this.maxServicePerBusiness = maxServicePerBusiness;
	}

	/**
	 * @return the maxTModels
	 */
	public Integer getMaxTModels() {
		return maxTModels;
	}

	/**
	 * @param maxTModels the maxTModels to set
	 */
	public void setMaxTModels(Integer maxTModels) {
		this.maxTModels = maxTModels;
	}

	/**
	 * @return the authorizedName
	 */
	public String getAuthorizedName() {
		return authorizedName;
	}

	/**
	 * @param authorizedName the authorizedName to set
	 */
	public void setAuthorizedName(String authorizedName) {
		this.authorizedName = authorizedName;
	}

}
