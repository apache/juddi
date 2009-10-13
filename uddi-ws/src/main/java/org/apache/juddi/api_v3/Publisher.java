/*
 * Copyright 2001-2009 The Apache Software Foundation.
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
package org.apache.juddi.api_v3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.w3._2000._09.xmldsig_.SignatureType;


/**
 * <p>Java class for Publisher type.  Specific to juddi.
 * 
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a> 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "publisher", propOrder = {
    "publisherName",
    "emailAddress",
    "isAdmin",
    "isEnabled",
    "maxBindingsPerService",
    "maxBusinesses",
    "maxServicePerBusiness",
    "maxTModels",
    "signature",
    "authorizedName"
})
public class Publisher implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = 9145476126076932380L;
	@XmlElement(required=true)
	protected String publisherName;
    protected String emailAddress;
    protected String isAdmin;
    protected String isEnabled;
    protected Integer maxBindingsPerService;
    protected Integer maxBusinesses;
    protected Integer maxServicePerBusiness;
    protected Integer maxTModels;
    @XmlElement(name="Signature", namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected List<SignatureType> signature;
    @XmlAttribute(required=true)
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

	/**
     * Gets the value of the signature property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the signature property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSignature().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SignatureType }
     * 
     * 
     */
    public List<SignatureType> getSignature() {
        if (signature == null) {
            signature = new ArrayList<SignatureType>();
        }
        return this.signature;
    }


}
