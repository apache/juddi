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
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.OperationalInfo;
import org.uddi.api_v3.TModel;


/**
 * <img src="http://www.uddi.org/pubs/uddi-v3.0.2-20041019_files/image112.gif"><Br>
 * A changeRecordNewData MUST not be empty; it must contain a valid semantic piece of new data. Change records of this type provide new or updated business or modeling information that is to be incorporated. Partial updates to a datum are not provided for; rather, the entire new contents of the datum and its operationalInfo are to be provided, and these replace any existing definition of the datum and its operationalInfo with the recipient of the change record. The hidden state (i.e. the deleted attribute) must be persisted through replication to allow for a custody transfer of hidden tModels between nodes via the replication protocol.

The operationalInfo element MUST contain the operational information associated with the indicated new data. No validation other than schema assessment and presence requirements are performed by the consuming node. Specifically, the creation date may change; the creation date need not be earlier than the modification date; the modification date need not be earlier than the modified including children date.

A changeRecordNewData is considered "successfully processed" once a node has received it, assigned a local USN to it, validated it, durably stored it in its change record journal, and then successfully incorporated it into the node’s data store.
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{urn:uddi-org:api_v3}businessEntity"/>
 *           &lt;element ref="{urn:uddi-org:api_v3}businessService"/>
 *           &lt;element ref="{urn:uddi-org:api_v3}bindingTemplate"/>
 *           &lt;element ref="{urn:uddi-org:api_v3}tModel"/>
 *         &lt;/choice>
 *         &lt;element ref="{urn:uddi-org:api_v3}operationalInfo"/>
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
    "businessEntity",
    "businessService",
    "bindingTemplate",
    "tModel",
    "operationalInfo"
})
@XmlRootElement(name = "changeRecordNewData")
public class ChangeRecordNewData implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = 3254341966186893497L;
	@XmlElement(namespace = "urn:uddi-org:api_v3")
    protected BusinessEntity businessEntity;
    @XmlElement(namespace = "urn:uddi-org:api_v3")
    protected BusinessService businessService;
    @XmlElement(namespace = "urn:uddi-org:api_v3")
    protected BindingTemplate bindingTemplate;
    @XmlElement(namespace = "urn:uddi-org:api_v3")
    protected TModel tModel;
    @XmlElement(namespace = "urn:uddi-org:api_v3", required = true)
    protected OperationalInfo operationalInfo;

    /**
     * Gets the value of the businessEntity property.
     * 
     * @return
     *     possible object is
     *     {@link BusinessEntity }
     *     
     */
    public BusinessEntity getBusinessEntity() {
        return businessEntity;
    }

    /**
     * Sets the value of the businessEntity property.
     * 
     * @param value
     *     allowed object is
     *     {@link BusinessEntity }
     *     
     */
    public void setBusinessEntity(BusinessEntity value) {
        this.businessEntity = value;
    }

    /**
     * Gets the value of the businessService property.
     * 
     * @return
     *     possible object is
     *     {@link BusinessService }
     *     
     */
    public BusinessService getBusinessService() {
        return businessService;
    }

    /**
     * Sets the value of the businessService property.
     * 
     * @param value
     *     allowed object is
     *     {@link BusinessService }
     *     
     */
    public void setBusinessService(BusinessService value) {
        this.businessService = value;
    }

    /**
     * Gets the value of the bindingTemplate property.
     * 
     * @return
     *     possible object is
     *     {@link BindingTemplate }
     *     
     */
    public BindingTemplate getBindingTemplate() {
        return bindingTemplate;
    }

    /**
     * Sets the value of the bindingTemplate property.
     * 
     * @param value
     *     allowed object is
     *     {@link BindingTemplate }
     *     
     */
    public void setBindingTemplate(BindingTemplate value) {
        this.bindingTemplate = value;
    }

    /**
     * Gets the value of the tModel property.
     * 
     * @return
     *     possible object is
     *     {@link TModel }
     *     
     */
    public TModel getTModel() {
        return tModel;
    }

    /**
     * Sets the value of the tModel property.
     * 
     * @param value
     *     allowed object is
     *     {@link TModel }
     *     
     */
    public void setTModel(TModel value) {
        this.tModel = value;
    }

    /**
     * Gets the value of the operationalInfo property.
     * 
     * @return
     *     possible object is
     *     {@link OperationalInfo }
     *     
     */
    public OperationalInfo getOperationalInfo() {
        return operationalInfo;
    }

    /**
     * Sets the value of the operationalInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link OperationalInfo }
     *     
     */
    public void setOperationalInfo(OperationalInfo value) {
        this.operationalInfo = value;
    }

}
