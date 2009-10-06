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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for registeredInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="registeredInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:uddi-org:api_v3}businessInfos" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}tModelInfos" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="truncated" type="{urn:uddi-org:api_v3}truncated" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "registeredInfo", propOrder = {
    "businessInfos",
    "tModelInfos"
})
@XmlRootElement
public class RegisteredInfo implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = -7441622376996526628L;
	protected BusinessInfos businessInfos;
    protected TModelInfos tModelInfos;
    @XmlAttribute
    protected Boolean truncated;

    /**
     * Gets the value of the businessInfos property.
     * 
     * @return
     *     possible object is
     *     {@link BusinessInfos }
     *     
     */
    public BusinessInfos getBusinessInfos() {
        return businessInfos;
    }

    /**
     * Sets the value of the businessInfos property.
     * 
     * @param value
     *     allowed object is
     *     {@link BusinessInfos }
     *     
     */
    public void setBusinessInfos(BusinessInfos value) {
        this.businessInfos = value;
    }

    /**
     * Gets the value of the tModelInfos property.
     * 
     * @return
     *     possible object is
     *     {@link TModelInfos }
     *     
     */
    public TModelInfos getTModelInfos() {
        return tModelInfos;
    }

    /**
     * Sets the value of the tModelInfos property.
     * 
     * @param value
     *     allowed object is
     *     {@link TModelInfos }
     *     
     */
    public void setTModelInfos(TModelInfos value) {
        this.tModelInfos = value;
    }

    /**
     * Gets the value of the truncated property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTruncated() {
        return truncated;
    }

    /**
     * Sets the value of the truncated property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTruncated(Boolean value) {
        this.truncated = value;
    }

}
