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


package org.uddi.repl_v3;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.uddi.api_v3.PublisherAssertion;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:uddi-org:api_v3}publisherAssertion"/>
 *         &lt;element name="fromBusinessCheck" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="toBusinessCheck" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="modified" type="{urn:uddi-org:api_v3}timeInstant"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "publisherAssertion",
    "fromBusinessCheck",
    "toBusinessCheck",
    "modified"
})
@XmlRootElement(name = "changeRecordDeleteAssertion")
public class ChangeRecordDeleteAssertion implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = -4008979760033075709L;
	@XmlElement(namespace = "urn:uddi-org:api_v3", required = true)
    protected PublisherAssertion publisherAssertion;
    protected boolean fromBusinessCheck;
    protected boolean toBusinessCheck;
    @XmlElement(required = true)
    protected XMLGregorianCalendar modified;

    /**
     * Gets the value of the publisherAssertion property.
     * 
     * @return
     *     possible object is
     *     {@link PublisherAssertion }
     *     
     */
    public PublisherAssertion getPublisherAssertion() {
        return publisherAssertion;
    }

    /**
     * Sets the value of the publisherAssertion property.
     * 
     * @param value
     *     allowed object is
     *     {@link PublisherAssertion }
     *     
     */
    public void setPublisherAssertion(PublisherAssertion value) {
        this.publisherAssertion = value;
    }

    /**
     * Gets the value of the fromBusinessCheck property.
     * 
     */
    public boolean isFromBusinessCheck() {
        return fromBusinessCheck;
    }

    /**
     * Sets the value of the fromBusinessCheck property.
     * 
     */
    public void setFromBusinessCheck(boolean value) {
        this.fromBusinessCheck = value;
    }

    /**
     * Gets the value of the toBusinessCheck property.
     * 
     */
    public boolean isToBusinessCheck() {
        return toBusinessCheck;
    }

    /**
     * Sets the value of the toBusinessCheck property.
     * 
     */
    public void setToBusinessCheck(boolean value) {
        this.toBusinessCheck = value;
    }

    /**
     * Gets the value of the modified property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getModified() {
        return modified;
    }

    /**
     * Sets the value of the modified property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setModified(XMLGregorianCalendar value) {
        this.modified = value;
    }

}
