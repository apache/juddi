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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.uddi.custody_v3.KeyBag;
import org.uddi.custody_v3.TransferOperationalInfo;
import org.uddi.custody_v3.TransferToken;


/**
 * <p>Java class for transfer_custody complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="transfer_custody">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:uddi-org:custody_v3}transferToken"/>
 *         &lt;element ref="{urn:uddi-org:custody_v3}keyBag"/>
 *         &lt;element ref="{urn:uddi-org:custody_v3}transferOperationalInfo"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "transfer_custody", propOrder = {
    "transferToken",
    "keyBag",
    "transferOperationalInfo"
})
public class TransferCustody implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = 8770604650672969536L;
	@XmlElement(namespace = "urn:uddi-org:custody_v3", required = true)
    protected TransferToken transferToken;
    @XmlElement(namespace = "urn:uddi-org:custody_v3", required = true)
    protected KeyBag keyBag;
    @XmlElement(namespace = "urn:uddi-org:custody_v3", required = true)
    protected TransferOperationalInfo transferOperationalInfo;

    /**
     * Gets the value of the transferToken property.
     * 
     * @return
     *     possible object is
     *     {@link TransferToken }
     *     
     */
    public TransferToken getTransferToken() {
        return transferToken;
    }

    /**
     * Sets the value of the transferToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransferToken }
     *     
     */
    public void setTransferToken(TransferToken value) {
        this.transferToken = value;
    }

    /**
     * Gets the value of the keyBag property.
     * 
     * @return
     *     possible object is
     *     {@link KeyBag }
     *     
     */
    public KeyBag getKeyBag() {
        return keyBag;
    }

    /**
     * Sets the value of the keyBag property.
     * 
     * @param value
     *     allowed object is
     *     {@link KeyBag }
     *     
     */
    public void setKeyBag(KeyBag value) {
        this.keyBag = value;
    }

    /**
     * Gets the value of the transferOperationalInfo property.
     * 
     * @return
     *     possible object is
     *     {@link TransferOperationalInfo }
     *     
     */
    public TransferOperationalInfo getTransferOperationalInfo() {
        return transferOperationalInfo;
    }

    /**
     * Sets the value of the transferOperationalInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransferOperationalInfo }
     *     
     */
    public void setTransferOperationalInfo(TransferOperationalInfo value) {
        this.transferOperationalInfo = value;
    }

}
