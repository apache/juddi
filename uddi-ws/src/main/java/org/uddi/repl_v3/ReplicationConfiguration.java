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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.uddi.api_v3.Contact;
import org.w3._2000._09.xmldsig_.SignatureType;


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
 *         &lt;element name="serialNumber" type="{urn:uddi-org:repl_v3}USN_type"/>
 *         &lt;element name="timeOfConfigurationUpdate" type="{urn:uddi-org:repl_v3}timeOfConfigurationUpdate_type"/>
 *         &lt;element name="registryContact">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{urn:uddi-org:api_v3}contact"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element ref="{urn:uddi-org:repl_v3}operator" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:repl_v3}communicationGraph" minOccurs="0"/>
 *         &lt;element name="maximumTimeToSyncRegistry" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="maximumTimeToGetChanges" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element ref="{http://www.w3.org/2000/09/xmldsig#}Signature" maxOccurs="unbounded" minOccurs="0"/>
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
    "serialNumber",
    "timeOfConfigurationUpdate",
    "registryContact",
    "operator",
    "communicationGraph",
    "maximumTimeToSyncRegistry",
    "maximumTimeToGetChanges",
    "signature"
})
@XmlRootElement(name = "replicationConfiguration")
public class ReplicationConfiguration implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = 621260248291581845L;
	protected long serialNumber;
    @XmlElement(required = true)
    protected String timeOfConfigurationUpdate;
    @XmlElement(required = true)
    protected ReplicationConfiguration.RegistryContact registryContact;
    protected List<Operator> operator;
    protected CommunicationGraph communicationGraph;
    protected BigInteger maximumTimeToSyncRegistry;
    @XmlElement(required = true)
    protected BigInteger maximumTimeToGetChanges;
    @XmlElement(name = "Signature", namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected List<SignatureType> signature;

    /**
     * Gets the value of the serialNumber property.
     * 
     */
    public long getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the value of the serialNumber property.
     * 
     */
    public void setSerialNumber(long value) {
        this.serialNumber = value;
    }

    /**
     * Gets the value of the timeOfConfigurationUpdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimeOfConfigurationUpdate() {
        return timeOfConfigurationUpdate;
    }

    /**
     * Sets the value of the timeOfConfigurationUpdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimeOfConfigurationUpdate(String value) {
        this.timeOfConfigurationUpdate = value;
    }

    /**
     * Gets the value of the registryContact property.
     * 
     * @return
     *     possible object is
     *     {@link ReplicationConfiguration.RegistryContact }
     *     
     */
    public ReplicationConfiguration.RegistryContact getRegistryContact() {
        return registryContact;
    }

    /**
     * Sets the value of the registryContact property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReplicationConfiguration.RegistryContact }
     *     
     */
    public void setRegistryContact(ReplicationConfiguration.RegistryContact value) {
        this.registryContact = value;
    }

    /**
     * Gets the value of the operator property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the operator property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOperator().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Operator }
     * 
     * 
     */
    public List<Operator> getOperator() {
        if (operator == null) {
            operator = new ArrayList<Operator>();
        }
        return this.operator;
    }

    /**
     * Gets the value of the communicationGraph property.
     * 
     * @return
     *     possible object is
     *     {@link CommunicationGraph }
     *     
     */
    public CommunicationGraph getCommunicationGraph() {
        return communicationGraph;
    }

    /**
     * Sets the value of the communicationGraph property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommunicationGraph }
     *     
     */
    public void setCommunicationGraph(CommunicationGraph value) {
        this.communicationGraph = value;
    }

    /**
     * Gets the value of the maximumTimeToSyncRegistry property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaximumTimeToSyncRegistry() {
        return maximumTimeToSyncRegistry;
    }

    /**
     * Sets the value of the maximumTimeToSyncRegistry property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaximumTimeToSyncRegistry(BigInteger value) {
        this.maximumTimeToSyncRegistry = value;
    }

    /**
     * Gets the value of the maximumTimeToGetChanges property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaximumTimeToGetChanges() {
        return maximumTimeToGetChanges;
    }

    /**
     * Sets the value of the maximumTimeToGetChanges property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaximumTimeToGetChanges(BigInteger value) {
        this.maximumTimeToGetChanges = value;
    }

    /**
     * Gets the value of the signature property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the signature property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSignature().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SignatureType }
     * 
     * 
     */
    public List<SignatureType> getSignature() {
        if (signature == null) {
            signature = new ArrayList<SignatureType>();
        }
        return this.signature;
    }


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
     *         &lt;element ref="{urn:uddi-org:api_v3}contact"/>
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
        "contact"
    })
    public static class RegistryContact {

        @XmlElement(namespace = "urn:uddi-org:api_v3", required = true)
        protected Contact contact;

        /**
         * Gets the value of the contact property.
         * 
         * @return
         *     possible object is
         *     {@link Contact }
         *     
         */
        public Contact getContact() {
            return contact;
        }

        /**
         * Sets the value of the contact property.
         * 
         * @param value
         *     allowed object is
         *     {@link Contact }
         *     
         */
        public void setContact(Contact value) {
            this.contact = value;
        }

    }

}
