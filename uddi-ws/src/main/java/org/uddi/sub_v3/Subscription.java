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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for subscription complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="subscription">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:uddi-org:sub_v3}subscriptionKey" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:sub_v3}subscriptionFilter" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}bindingKey" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:sub_v3}notificationInterval" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:sub_v3}maxEntities" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:sub_v3}expiresAfter" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="brief" type="{urn:uddi-org:sub_v3}brief" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "subscription", propOrder = {
    "subscriptionKey",
    "subscriptionFilter",
    "bindingKey",
    "notificationInterval",
    "maxEntities",
    "expiresAfter"
})
public class Subscription implements Serializable{

	@XmlTransient
	private static final long serialVersionUID = 1L;
    protected String subscriptionKey;
    protected SubscriptionFilter subscriptionFilter;
    @XmlElement(namespace = "urn:uddi-org:api_v3")
    protected String bindingKey;
    protected Duration notificationInterval;
    protected Integer maxEntities;
    protected XMLGregorianCalendar expiresAfter;
    @XmlAttribute
    protected Boolean brief;

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
     * Gets the value of the subscriptionFilter property.
     * 
     * @return
     *     possible object is
     *     {@link SubscriptionFilter }
     *     
     */
    public SubscriptionFilter getSubscriptionFilter() {
        return subscriptionFilter;
    }

    /**
     * Sets the value of the subscriptionFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link SubscriptionFilter }
     *     
     */
    public void setSubscriptionFilter(SubscriptionFilter value) {
        this.subscriptionFilter = value;
    }

    /**
     * Gets the value of the bindingKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBindingKey() {
        return bindingKey;
    }

    /**
     * Sets the value of the bindingKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBindingKey(String value) {
        this.bindingKey = value;
    }

    /**
     * Gets the value of the notificationInterval property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getNotificationInterval() {
        return notificationInterval;
    }

    /**
     * Sets the value of the notificationInterval property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setNotificationInterval(Duration value) {
        this.notificationInterval = value;
    }

    /**
     * Gets the value of the maxEntities property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxEntities() {
        return maxEntities;
    }

    /**
     * Sets the value of the maxEntities property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxEntities(Integer value) {
        this.maxEntities = value;
    }

    /**
     * Gets the value of the expiresAfter property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExpiresAfter() {
        return expiresAfter;
    }

    /**
     * Sets the value of the expiresAfter property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExpiresAfter(XMLGregorianCalendar value) {
        this.expiresAfter = value;
    }

    /**
     * Gets the value of the brief property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBrief() {
        return brief;
    }

    /**
     * Sets the value of the brief property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBrief(Boolean value) {
        this.brief = value;
    }

}
