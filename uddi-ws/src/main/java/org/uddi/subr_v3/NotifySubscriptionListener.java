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


package org.uddi.subr_v3;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.uddi.sub_v3.SubscriptionResultsList;


/**
 * <p>Java class for notify_subscriptionListener complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="notify_subscriptionListener">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:uddi-org:api_v3}authInfo" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:sub_v3}subscriptionResultsList"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "notify_subscriptionListener", propOrder = {
    "authInfo",
    "subscriptionResultsList"
})
@XmlRootElement(name="notify_subscriptionListener")
public class NotifySubscriptionListener implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = 4531235329051418025L;
	@XmlElement(namespace = "urn:uddi-org:api_v3")
    protected String authInfo;
    @XmlElement(namespace = "urn:uddi-org:sub_v3", required = true)
    protected SubscriptionResultsList subscriptionResultsList;

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
     * Gets the value of the subscriptionResultsList property.
     * 
     * @return
     *     possible object is
     *     {@link SubscriptionResultsList }
     *     
     */
    public SubscriptionResultsList getSubscriptionResultsList() {
        return subscriptionResultsList;
    }

    /**
     * Sets the value of the subscriptionResultsList property.
     * 
     * @param value
     *     allowed object is
     *     {@link SubscriptionResultsList }
     *     
     */
    public void setSubscriptionResultsList(SubscriptionResultsList value) {
        this.subscriptionResultsList = value;
    }

}
