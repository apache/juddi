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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for operationalInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="operationalInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="created" type="{urn:uddi-org:api_v3}timeInstant" minOccurs="0"/>
 *         &lt;element name="modified" type="{urn:uddi-org:api_v3}timeInstant" minOccurs="0"/>
 *         &lt;element name="modifiedIncludingChildren" type="{urn:uddi-org:api_v3}timeInstant" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}nodeID" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}authorizedName" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="entityKey" use="required" type="{urn:uddi-org:api_v3}uddiKey" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "operationalInfo", propOrder = {
    "created",
    "modified",
    "modifiedIncludingChildren",
    "nodeID",
    "authorizedName"
})
public class OperationalInfo implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = -3112659463581534112L;
	protected XMLGregorianCalendar created;
    protected XMLGregorianCalendar modified;
    protected XMLGregorianCalendar modifiedIncludingChildren;
    protected String nodeID;
    protected String authorizedName;
    @XmlAttribute(required = true)
    protected String entityKey;

    /**
     * Gets the value of the created property.
     * Indicates the instant in time at which the entity with which the 
     * operationalInfo is associated first appeared in the registry.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreated() {
        return created;
    }

    /**
     * Sets the value of the created property.
     * Indicates the instant in time at which the entity with which the 
     * operationalInfo is associated first appeared in the registry.
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreated(XMLGregorianCalendar value) {
        this.created = value;
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

    /**
     * Gets the value of the modifiedIncludingChildren property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getModifiedIncludingChildren() {
        return modifiedIncludingChildren;
    }

    /**
     * Sets the value of the modifiedIncludingChildren property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setModifiedIncludingChildren(XMLGregorianCalendar value) {
        this.modifiedIncludingChildren = value;
    }

    /**
     * Gets the value of the nodeID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNodeID() {
        return nodeID;
    }

    /**
     * Sets the value of the nodeID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNodeID(String value) {
        this.nodeID = value;
    }

    /**
     * Gets the value of the authorizedName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthorizedName() {
        return authorizedName;
    }

    /**
     * Sets the value of the authorizedName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthorizedName(String value) {
        this.authorizedName = value;
    }

    /**
     * Gets the value of the entityKey property.
     * The entityKey references the UDDI entity with which the operationalInfo is associated.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEntityKey() {
        return entityKey;
    }

    /**
     * Sets the value of the entityKey property.
     * The entityKey references the UDDI entity with which the operationalInfo is associated.
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEntityKey(String value) {
        this.entityKey = value;
    }

}
