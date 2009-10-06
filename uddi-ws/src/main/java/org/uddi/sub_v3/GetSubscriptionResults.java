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


/**
 * <p>Java class for get_subscriptionResults complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="get_subscriptionResults">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:uddi-org:api_v3}authInfo" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:sub_v3}subscriptionKey"/>
 *         &lt;element ref="{urn:uddi-org:sub_v3}coveragePeriod"/>
 *         &lt;element ref="{urn:uddi-org:sub_v3}chunkToken" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "get_subscriptionResults", propOrder = {
    "authInfo",
    "subscriptionKey",
    "coveragePeriod",
    "chunkToken"
})
public class GetSubscriptionResults implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = 1677722415954673870L;
	@XmlElement(namespace = "urn:uddi-org:api_v3")
    protected String authInfo;
    @XmlElement(required = true)
    protected String subscriptionKey;
    @XmlElement(required = true)
    protected CoveragePeriod coveragePeriod;
    protected String chunkToken;

    /**
     * Gets the value of the authInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthInfo() {
        return authInfo;
    }

    /**
     * Sets the value of the authInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthInfo(String value) {
        this.authInfo = value;
    }

    /**
     * Gets the value of the subscriptionKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubscriptionKey() {
        return subscriptionKey;
    }

    /**
     * Sets the value of the subscriptionKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubscriptionKey(String value) {
        this.subscriptionKey = value;
    }

    /**
     * Gets the value of the coveragePeriod property.
     * 
     * @return
     *     possible object is
     *     {@link CoveragePeriod }
     *     
     */
    public CoveragePeriod getCoveragePeriod() {
        return coveragePeriod;
    }

    /**
     * Sets the value of the coveragePeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link CoveragePeriod }
     *     
     */
    public void setCoveragePeriod(CoveragePeriod value) {
        this.coveragePeriod = value;
    }

    /**
     * Gets the value of the chunkToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChunkToken() {
        return chunkToken;
    }

    /**
     * Sets the value of the chunkToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChunkToken(String value) {
        this.chunkToken = value;
    }

}
