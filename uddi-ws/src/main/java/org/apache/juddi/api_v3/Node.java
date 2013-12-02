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
/**
 * <p>Java class for Node type.  Specific to juddi.
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a> 
 * 
 */
package org.apache.juddi.api_v3;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for node complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="node">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="clientName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="custodyTransferUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="inquiryUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="publishUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="securityUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subscriptionUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subscriptionListenerUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="replicationUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="juddiApiUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="proxyTransport" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="factoryInitial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="factoryURLPkgs" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="factoryNamingProvider" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "node", propOrder = {
    "name",
    "clientName",
    "description",
    "custodyTransferUrl",
    "inquiryUrl",
    "publishUrl",
    "securityUrl",
    "subscriptionUrl",
    "subscriptionListenerUrl",
    "replicationUrl",
    "juddiApiUrl",
    "proxyTransport",
    "factoryInitial",
    "factoryURLPkgs",
    "factoryNamingProvider"
})
public class Node implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = -4601378453000384721L;

    protected String name;
    protected String clientName;
    protected String description;
    protected String custodyTransferUrl;
    protected String inquiryUrl;
    protected String publishUrl;
    protected String securityUrl;
    protected String subscriptionUrl;
    protected String subscriptionListenerUrl;
    protected String replicationUrl;
    protected String juddiApiUrl;
    protected String proxyTransport;
    protected String factoryInitial;
    protected String factoryURLPkgs;
    protected String factoryNamingProvider;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the clientName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * Sets the value of the clientName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClientName(String value) {
        this.clientName = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the custodyTransferUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustodyTransferUrl() {
        return custodyTransferUrl;
    }

    /**
     * Sets the value of the custodyTransferUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustodyTransferUrl(String value) {
        this.custodyTransferUrl = value;
    }

    /**
     * Gets the value of the inquiryUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInquiryUrl() {
        return inquiryUrl;
    }

    /**
     * Sets the value of the inquiryUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInquiryUrl(String value) {
        this.inquiryUrl = value;
    }

    /**
     * Gets the value of the publishUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPublishUrl() {
        return publishUrl;
    }

    /**
     * Sets the value of the publishUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPublishUrl(String value) {
        this.publishUrl = value;
    }

    /**
     * Gets the value of the securityUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSecurityUrl() {
        return securityUrl;
    }

    /**
     * Sets the value of the securityUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSecurityUrl(String value) {
        this.securityUrl = value;
    }

    /**
     * Gets the value of the subscriptionUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubscriptionUrl() {
        return subscriptionUrl;
    }

    /**
     * Sets the value of the subscriptionUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubscriptionUrl(String value) {
        this.subscriptionUrl = value;
    }

    /**
     * Gets the value of the subscriptionListenerUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubscriptionListenerUrl() {
        return subscriptionListenerUrl;
    }

    /**
     * Sets the value of the subscriptionListenerUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubscriptionListenerUrl(String value) {
        this.subscriptionListenerUrl = value;
    }

    /**
     * Gets the value of the replicationUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReplicationUrl() {
        return replicationUrl;
    }

    /**
     * Sets the value of the replicationUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReplicationUrl(String value) {
        this.replicationUrl = value;
    }

    /**
     * Gets the value of the juddiApiUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJuddiApiUrl() {
        return juddiApiUrl;
    }

    /**
     * Sets the value of the juddiApiUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJuddiApiUrl(String value) {
        this.juddiApiUrl = value;
    }

    /**
     * Gets the value of the proxyTransport property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProxyTransport() {
        return proxyTransport;
    }

    /**
     * Sets the value of the proxyTransport property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProxyTransport(String value) {
        this.proxyTransport = value;
    }

    /**
     * Gets the value of the factoryInitial property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFactoryInitial() {
        return factoryInitial;
    }

    /**
     * Sets the value of the factoryInitial property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFactoryInitial(String value) {
        this.factoryInitial = value;
    }

    /**
     * Gets the value of the factoryURLPkgs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFactoryURLPkgs() {
        return factoryURLPkgs;
    }

    /**
     * Sets the value of the factoryURLPkgs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFactoryURLPkgs(String value) {
        this.factoryURLPkgs = value;
    }

    /**
     * Gets the value of the factoryNamingProvider property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFactoryNamingProvider() {
        return factoryNamingProvider;
    }

    /**
     * Sets the value of the factoryNamingProvider property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFactoryNamingProvider(String value) {
        this.factoryNamingProvider = value;
    }

}
