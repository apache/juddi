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


package org.uddi.sub_v3;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.uddi.api_v3.FindBinding;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindRelatedBusinesses;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.GetAssertionStatusReport;
import org.uddi.api_v3.GetBindingDetail;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.GetServiceDetail;
import org.uddi.api_v3.GetTModelDetail;


/**
 * <p>Java class for subscriptionFilter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="subscriptionFilter">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{urn:uddi-org:api_v3}find_binding"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}find_business"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}find_relatedBusinesses"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}find_service"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}find_tModel"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}get_bindingDetail"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}get_businessDetail"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}get_serviceDetail"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}get_tModelDetail"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}get_assertionStatusReport"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "subscriptionFilter", propOrder = {
    "findBinding",
    "findBusiness",
    "findRelatedBusinesses",
    "findService",
    "findTModel",
    "getBindingDetail",
    "getBusinessDetail",
    "getServiceDetail",
    "getTModelDetail",
    "getAssertionStatusReport"
})
public class SubscriptionFilter implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = -1832900660546271425L;
	@XmlElement(name = "find_binding", namespace = "urn:uddi-org:api_v3")
    protected FindBinding findBinding;
    @XmlElement(name = "find_business", namespace = "urn:uddi-org:api_v3")
    protected FindBusiness findBusiness;
    @XmlElement(name = "find_relatedBusinesses", namespace = "urn:uddi-org:api_v3")
    protected FindRelatedBusinesses findRelatedBusinesses;
    @XmlElement(name = "find_service", namespace = "urn:uddi-org:api_v3")
    protected FindService findService;
    @XmlElement(name = "find_tModel", namespace = "urn:uddi-org:api_v3")
    protected FindTModel findTModel;
    @XmlElement(name = "get_bindingDetail", namespace = "urn:uddi-org:api_v3")
    protected GetBindingDetail getBindingDetail;
    @XmlElement(name = "get_businessDetail", namespace = "urn:uddi-org:api_v3")
    protected GetBusinessDetail getBusinessDetail;
    @XmlElement(name = "get_serviceDetail", namespace = "urn:uddi-org:api_v3")
    protected GetServiceDetail getServiceDetail;
    @XmlElement(name = "get_tModelDetail", namespace = "urn:uddi-org:api_v3")
    protected GetTModelDetail getTModelDetail;
    @XmlElement(name = "get_assertionStatusReport", namespace = "urn:uddi-org:api_v3")
    protected GetAssertionStatusReport getAssertionStatusReport;

    /**
     * Gets the value of the findBinding property.
     * 
     * @return
     *     possible object is
     *     {@link FindBinding }
     *     
     */
    public FindBinding getFindBinding() {
        return findBinding;
    }

    /**
     * Sets the value of the findBinding property.
     * 
     * @param value
     *     allowed object is
     *     {@link FindBinding }
     *     
     */
    public void setFindBinding(FindBinding value) {
        this.findBinding = value;
    }

    /**
     * Gets the value of the findBusiness property.
     * 
     * @return
     *     possible object is
     *     {@link FindBusiness }
     *     
     */
    public FindBusiness getFindBusiness() {
        return findBusiness;
    }

    /**
     * Sets the value of the findBusiness property.
     * 
     * @param value
     *     allowed object is
     *     {@link FindBusiness }
     *     
     */
    public void setFindBusiness(FindBusiness value) {
        this.findBusiness = value;
    }

    /**
     * Gets the value of the findRelatedBusinesses property.
     * 
     * @return
     *     possible object is
     *     {@link FindRelatedBusinesses }
     *     
     */
    public FindRelatedBusinesses getFindRelatedBusinesses() {
        return findRelatedBusinesses;
    }

    /**
     * Sets the value of the findRelatedBusinesses property.
     * 
     * @param value
     *     allowed object is
     *     {@link FindRelatedBusinesses }
     *     
     */
    public void setFindRelatedBusinesses(FindRelatedBusinesses value) {
        this.findRelatedBusinesses = value;
    }

    /**
     * Gets the value of the findService property.
     * 
     * @return
     *     possible object is
     *     {@link FindService }
     *     
     */
    public FindService getFindService() {
        return findService;
    }

    /**
     * Sets the value of the findService property.
     * 
     * @param value
     *     allowed object is
     *     {@link FindService }
     *     
     */
    public void setFindService(FindService value) {
        this.findService = value;
    }

    /**
     * Gets the value of the findTModel property.
     * 
     * @return
     *     possible object is
     *     {@link FindTModel }
     *     
     */
    public FindTModel getFindTModel() {
        return findTModel;
    }

    /**
     * Sets the value of the findTModel property.
     * 
     * @param value
     *     allowed object is
     *     {@link FindTModel }
     *     
     */
    public void setFindTModel(FindTModel value) {
        this.findTModel = value;
    }

    /**
     * Gets the value of the getBindingDetail property.
     * 
     * @return
     *     possible object is
     *     {@link GetBindingDetail }
     *     
     */
    public GetBindingDetail getGetBindingDetail() {
        return getBindingDetail;
    }

    /**
     * Sets the value of the getBindingDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetBindingDetail }
     *     
     */
    public void setGetBindingDetail(GetBindingDetail value) {
        this.getBindingDetail = value;
    }

    /**
     * Gets the value of the getBusinessDetail property.
     * 
     * @return
     *     possible object is
     *     {@link GetBusinessDetail }
     *     
     */
    public GetBusinessDetail getGetBusinessDetail() {
        return getBusinessDetail;
    }

    /**
     * Sets the value of the getBusinessDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetBusinessDetail }
     *     
     */
    public void setGetBusinessDetail(GetBusinessDetail value) {
        this.getBusinessDetail = value;
    }

    /**
     * Gets the value of the getServiceDetail property.
     * 
     * @return
     *     possible object is
     *     {@link GetServiceDetail }
     *     
     */
    public GetServiceDetail getGetServiceDetail() {
        return getServiceDetail;
    }

    /**
     * Sets the value of the getServiceDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetServiceDetail }
     *     
     */
    public void setGetServiceDetail(GetServiceDetail value) {
        this.getServiceDetail = value;
    }

    /**
     * Gets the value of the getTModelDetail property.
     * 
     * @return
     *     possible object is
     *     {@link GetTModelDetail }
     *     
     */
    public GetTModelDetail getGetTModelDetail() {
        return getTModelDetail;
    }

    /**
     * Sets the value of the getTModelDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetTModelDetail }
     *     
     */
    public void setGetTModelDetail(GetTModelDetail value) {
        this.getTModelDetail = value;
    }

    /**
     * Gets the value of the getAssertionStatusReport property.
     * 
     * @return
     *     possible object is
     *     {@link GetAssertionStatusReport }
     *     
     */
    public GetAssertionStatusReport getGetAssertionStatusReport() {
        return getAssertionStatusReport;
    }

    /**
     * Sets the value of the getAssertionStatusReport property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetAssertionStatusReport }
     *     
     */
    public void setGetAssertionStatusReport(GetAssertionStatusReport value) {
        this.getAssertionStatusReport = value;
    }

}
