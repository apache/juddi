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


/**
 * A changeRecordDelete element indicates that an item defined in the UDDI registry is to no longer be used and expunged from the data stores in each of the nodes. The item to be deleted is indicated in the change record by the key of an appropriate entity type; this must contain the unique key of some businessEntity, businessService, bindingTemplate, or tModel that is presently defined.  The changeRecordDelete element for deleting tModels corresponds to the administrative deletion of a tModel described in Section 6.1.3 Updates and Deletions of this specification.  The changeRecordDelete for a tModel does not correspond to any API described in this specification and should only appear in the replication stream as the result of an administrative function to permanently remove a tModel.

Permanent deletions of tModel information within the node may be made administratively. In this event, a UDDI Node may insert a delete operation into the replication stream.  The publisher identifier for this operation is the account associated with the UDDI Node.  Note that a permanent deletion of tModel information from the registry must have the prior approval of the other nodes participating within the registry.

The changeRecordDelete MUST contain a modified timestamp to allow multi-node registries to calculate consistent modifiedIncludingChildren timestamps as described in Section 3.8 operationalInfo Structure.
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{urn:uddi-org:repl_v3}genericKey_type"/>
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
    "businessKey",
    "tModelKey",
    "serviceKey",
    "bindingKey",
    "modified"
})
@XmlRootElement(name = "changeRecordDelete")
public class ChangeRecordDelete implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = -7081596275330679517L;
	@XmlElement(namespace = "urn:uddi-org:repl_v3")
    protected String businessKey;
    @XmlElement(namespace = "urn:uddi-org:repl_v3")
    protected String tModelKey;
    @XmlElement(namespace = "urn:uddi-org:repl_v3")
    protected String serviceKey;
    @XmlElement(namespace = "urn:uddi-org:repl_v3")
    protected String bindingKey;
    @XmlElement(required = true)
    protected XMLGregorianCalendar modified;

    /**
     * Gets the value of the businessKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBusinessKey() {
        return businessKey;
    }

    /**
     * Sets the value of the businessKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBusinessKey(String value) {
        this.businessKey = value;
    }

    /**
     * Gets the value of the tModelKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTModelKey() {
        return tModelKey;
    }

    /**
     * Sets the value of the tModelKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTModelKey(String value) {
        this.tModelKey = value;
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