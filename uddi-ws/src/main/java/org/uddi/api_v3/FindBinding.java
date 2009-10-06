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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for find_binding complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="find_binding">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:uddi-org:api_v3}authInfo" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}findQualifiers" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}tModelBag" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}find_tModel" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}categoryBag" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="maxRows" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="serviceKey" type="{urn:uddi-org:api_v3}serviceKey" />
 *       &lt;attribute name="listHead" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "find_binding", propOrder = {
    "authInfo",
    "findQualifiers",
    "tModelBag",
    "findTModel",
    "categoryBag"
})
public class FindBinding implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = 5788351026931169344L;
	protected String authInfo;
    protected FindQualifiers findQualifiers;
    protected TModelBag tModelBag;
    @XmlElement(name = "find_tModel")
    protected FindTModel findTModel;
    protected CategoryBag categoryBag;
    @XmlAttribute
    protected Integer maxRows;
    @XmlAttribute
    protected String serviceKey;
    @XmlAttribute
    protected Integer listHead;

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
     * Gets the value of the findQualifiers property.
     * 
     * @return
     *     possible object is
     *     {@link FindQualifiers }
     *     
     */
    public FindQualifiers getFindQualifiers() {
        return findQualifiers;
    }

    /**
     * Sets the value of the findQualifiers property.
     * 
     * @param value
     *     allowed object is
     *     {@link FindQualifiers }
     *     
     */
    public void setFindQualifiers(FindQualifiers value) {
        this.findQualifiers = value;
    }

    /**
     * Gets the value of the tModelBag property.
     * 
     * @return
     *     possible object is
     *     {@link TModelBag }
     *     
     */
    public TModelBag getTModelBag() {
        return tModelBag;
    }

    /**
     * Sets the value of the tModelBag property.
     * 
     * @param value
     *     allowed object is
     *     {@link TModelBag }
     *     
     */
    public void setTModelBag(TModelBag value) {
        this.tModelBag = value;
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
     * Gets the value of the categoryBag property.
     * 
     * @return
     *     possible object is
     *     {@link CategoryBag }
     *     
     */
    public CategoryBag getCategoryBag() {
        return categoryBag;
    }

    /**
     * Sets the value of the categoryBag property.
     * 
     * @param value
     *     allowed object is
     *     {@link CategoryBag }
     *     
     */
    public void setCategoryBag(CategoryBag value) {
        this.categoryBag = value;
    }

    /**
     * Gets the value of the maxRows property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxRows() {
        return maxRows;
    }

    /**
     * Sets the value of the maxRows property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxRows(Integer value) {
        this.maxRows = value;
    }

    /**
     * Gets the value of the serviceKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceKey() {
        return serviceKey;
    }

    /**
     * Sets the value of the serviceKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceKey(String value) {
        this.serviceKey = value;
    }

    /**
     * Gets the value of the listHead property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getListHead() {
        return listHead;
    }

    /**
     * Sets the value of the listHead property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setListHead(Integer value) {
        this.listHead = value;
    }

}
