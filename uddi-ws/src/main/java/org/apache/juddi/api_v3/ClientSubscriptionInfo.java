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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for clientSubscriptionInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="clientSubscriptionInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="subscriptionKey" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fromClerk" type="{urn:juddi-apache-org:api_v3}clerk"/>
 *         &lt;element name="toClerk" type="{urn:juddi-apache-org:api_v3}clerk"/>
 *         &lt;element name="lastModified" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="lastNotified" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "clientSubscriptionInfo", propOrder = {
    "subscriptionKey",
    "fromClerk",
    "toClerk",
    "lastModified",
    "lastNotified"
})
public class ClientSubscriptionInfo {

    @XmlElement(required = true)
    protected String subscriptionKey;
    @XmlElement(required = true)
    protected Clerk fromClerk;
    @XmlElement(required = true)
    protected Clerk toClerk;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastModified;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastNotified;

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
     * Gets the value of the fromClerk property.
     * 
     * @return
     *     possible object is
     *     {@link Clerk }
     *     
     */
    public Clerk getFromClerk() {
        return fromClerk;
    }

    /**
     * Sets the value of the fromClerk property.
     * 
     * @param value
     *     allowed object is
     *     {@link Clerk }
     *     
     */
    public void setFromClerk(Clerk value) {
        this.fromClerk = value;
    }

    /**
     * Gets the value of the toClerk property.
     * 
     * @return
     *     possible object is
     *     {@link Clerk }
     *     
     */
    public Clerk getToClerk() {
        return toClerk;
    }

    /**
     * Sets the value of the toClerk property.
     * 
     * @param value
     *     allowed object is
     *     {@link Clerk }
     *     
     */
    public void setToClerk(Clerk value) {
        this.toClerk = value;
    }

    /**
     * Gets the value of the lastModified property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastModified() {
        return lastModified;
    }

    /**
     * Sets the value of the lastModified property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastModified(XMLGregorianCalendar value) {
        this.lastModified = value;
    }

    /**
     * Gets the value of the lastNotified property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastNotified() {
        return lastNotified;
    }

    /**
     * Sets the value of the lastNotified property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastNotified(XMLGregorianCalendar value) {
        this.lastNotified = value;
    }

}
