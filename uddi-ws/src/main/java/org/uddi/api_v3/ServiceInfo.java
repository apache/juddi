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
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for serviceInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="serviceInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:uddi-org:api_v3}name" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="serviceKey" use="required" type="{urn:uddi-org:api_v3}serviceKey" />
 *       &lt;attribute name="businessKey" use="required" type="{urn:uddi-org:api_v3}businessKey" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "serviceInfo", propOrder = {
    "name"
})
public class ServiceInfo implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = 7482031574974280179L;
	protected List<Name> name;
    @XmlAttribute(required = true)
    protected String serviceKey;
    @XmlAttribute(required = true)
    protected String businessKey;

    /**
     * Gets the value of the name property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the name property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Name }
     * 
     * 
     */
    public List<Name> getName() {
        if (name == null) {
            name = new ArrayList<Name>();
        }
        return this.name;
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

}
