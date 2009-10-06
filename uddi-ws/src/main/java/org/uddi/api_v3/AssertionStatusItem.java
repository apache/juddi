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


package org.uddi.api_v3;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for assertionStatusItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="assertionStatusItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:uddi-org:api_v3}fromKey"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}toKey"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}keyedReference"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}keysOwned"/>
 *       &lt;/sequence>
 *       &lt;attribute name="completionStatus" use="required" type="{urn:uddi-org:api_v3}completionStatus" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "assertionStatusItem", propOrder = {
    "fromKey",
    "toKey",
    "keyedReference",
    "keysOwned"
})
@XmlRootElement
public class AssertionStatusItem implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = 2275087906912814101L;
	@XmlElement(required = true)
    protected String fromKey;
    @XmlElement(required = true)
    protected String toKey;
    @XmlElement(required = true)
    protected KeyedReference keyedReference;
    @XmlElement(required = true)
    protected KeysOwned keysOwned;
    @XmlAttribute(required = true)
    protected CompletionStatus completionStatus;

    /**
     * Gets the value of the fromKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFromKey() {
        return fromKey;
    }

    /**
     * Sets the value of the fromKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromKey(String value) {
        this.fromKey = value;
    }

    /**
     * Gets the value of the toKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToKey() {
        return toKey;
    }

    /**
     * Sets the value of the toKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToKey(String value) {
        this.toKey = value;
    }

    /**
     * Gets the value of the keyedReference property.
     * 
     * @return
     *     possible object is
     *     {@link KeyedReference }
     *     
     */
    public KeyedReference getKeyedReference() {
        return keyedReference;
    }

    /**
     * Sets the value of the keyedReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link KeyedReference }
     *     
     */
    public void setKeyedReference(KeyedReference value) {
        this.keyedReference = value;
    }

    /**
     * Gets the value of the keysOwned property.
     * 
     * @return
     *     possible object is
     *     {@link KeysOwned }
     *     
     */
    public KeysOwned getKeysOwned() {
        return keysOwned;
    }

    /**
     * Sets the value of the keysOwned property.
     * 
     * @param value
     *     allowed object is
     *     {@link KeysOwned }
     *     
     */
    public void setKeysOwned(KeysOwned value) {
        this.keysOwned = value;
    }

    /**
     * Gets the value of the completionStatus property.
     * 
     * @return
     *     possible object is
     *     {@link CompletionStatus }
     *     
     */
    public CompletionStatus getCompletionStatus() {
        return completionStatus;
    }

    /**
     * Sets the value of the completionStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompletionStatus }
     *     
     */
    public void setCompletionStatus(CompletionStatus value) {
        this.completionStatus = value;
    }

}
