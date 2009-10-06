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
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.uddi.api_v3.Contact;
import org.w3._2000._09.xmldsig_.KeyInfoType;


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
 *         &lt;element ref="{urn:uddi-org:repl_v3}operatorNodeID"/>
 *         &lt;element name="operatorStatus" type="{urn:uddi-org:repl_v3}operatorStatus_type"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}contact" maxOccurs="unbounded"/>
 *         &lt;element name="soapReplicationURL" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element ref="{http://www.w3.org/2000/09/xmldsig#}KeyInfo" maxOccurs="unbounded" minOccurs="0"/>
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
    "operatorNodeID",
    "operatorStatus",
    "contact",
    "soapReplicationURL",
    "keyInfo"
})
@XmlRootElement(name = "operator")
public class Operator implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = 3012475870316361941L;
	@XmlElement(required = true)
    protected String operatorNodeID;
    @XmlElement(required = true)
    protected OperatorStatusType operatorStatus;
    @XmlElement(namespace = "urn:uddi-org:api_v3", required = true)
    protected List<Contact> contact;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String soapReplicationURL;
    @XmlElement(name = "KeyInfo", namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected List<KeyInfoType> keyInfo;

    /**
     * Gets the value of the operatorNodeID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperatorNodeID() {
        return operatorNodeID;
    }

    /**
     * Sets the value of the operatorNodeID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperatorNodeID(String value) {
        this.operatorNodeID = value;
    }

    /**
     * Gets the value of the operatorStatus property.
     * 
     * @return
     *     possible object is
     *     {@link OperatorStatusType }
     *     
     */
    public OperatorStatusType getOperatorStatus() {
        return operatorStatus;
    }

    /**
     * Sets the value of the operatorStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link OperatorStatusType }
     *     
     */
    public void setOperatorStatus(OperatorStatusType value) {
        this.operatorStatus = value;
    }

    /**
     * Gets the value of the contact property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the contact property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContact().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Contact }
     * 
     * 
     */
    public List<Contact> getContact() {
        if (contact == null) {
            contact = new ArrayList<Contact>();
        }
        return this.contact;
    }

    /**
     * Gets the value of the soapReplicationURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSoapReplicationURL() {
        return soapReplicationURL;
    }

    /**
     * Sets the value of the soapReplicationURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSoapReplicationURL(String value) {
        this.soapReplicationURL = value;
    }

    /**
     * Gets the value of the keyInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the keyInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKeyInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link KeyInfoType }
     * 
     * 
     */
    public List<KeyInfoType> getKeyInfo() {
        if (keyInfo == null) {
            keyInfo = new ArrayList<KeyInfoType>();
        }
        return this.keyInfo;
    }

}
