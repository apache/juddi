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
 * <p>Java class for tModelInstanceInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tModelInstanceInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:uddi-org:api_v3}description" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}instanceDetails" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="tModelKey" use="required" type="{urn:uddi-org:api_v3}tModelKey" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tModelInstanceInfo", propOrder = {
    "description",
    "instanceDetails"
})
public class TModelInstanceInfo implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = -1642460620530669083L;
	protected List<Description> description;
    protected InstanceDetails instanceDetails;
    @XmlAttribute(required = true)
    protected String tModelKey;

    /**
     * Gets the value of the description property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the description property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDescription().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Description }
     * 
     * 
     */
    public List<Description> getDescription() {
        if (description == null) {
            description = new ArrayList<Description>();
        }
        return this.description;
    }

    /**
     * Gets the value of the instanceDetails property.
     * 
     * @return
     *     possible object is
     *     {@link InstanceDetails }
     *     
     */
    public InstanceDetails getInstanceDetails() {
        return instanceDetails;
    }

    /**
     * Sets the value of the instanceDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link InstanceDetails }
     *     
     */
    public void setInstanceDetails(InstanceDetails value) {
        this.instanceDetails = value;
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

}
