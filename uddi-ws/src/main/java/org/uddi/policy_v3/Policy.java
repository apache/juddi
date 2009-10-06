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


package org.uddi.policy_v3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * This element should be used to articulate one policy.
 * 
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:uddi-org:policy_v3}policyName"/>
 *         &lt;element ref="{urn:uddi-org:policy_v3}policyDescription" maxOccurs="unbounded"/>
 *         &lt;element ref="{urn:uddi-org:policy_v3}policyDecisionPoint"/>
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
    "policyName",
    "policyDescription",
    "policyDecisionPoint"
})
@XmlRootElement(name = "policy")
public class Policy implements Serializable{

	@XmlTransient
	private static final long serialVersionUID = -3788182208582126483L;
	@XmlElement(required = true)
    protected String policyName;
    @XmlElement(required = true)
    protected List<PolicyDescriptionType> policyDescription;
    @XmlElement(required = true)
    protected String policyDecisionPoint;

    /**
     * Gets the value of the policyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPolicyName() {
        return policyName;
    }

    /**
     * Sets the value of the policyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPolicyName(String value) {
        this.policyName = value;
    }

    /**
     * Gets the value of the policyDescription property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the policyDescription property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPolicyDescription().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PolicyDescriptionType }
     * 
     * 
     */
    public List<PolicyDescriptionType> getPolicyDescription() {
        if (policyDescription == null) {
            policyDescription = new ArrayList<PolicyDescriptionType>();
        }
        return this.policyDescription;
    }

    /**
     * Gets the value of the policyDecisionPoint property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPolicyDecisionPoint() {
        return policyDecisionPoint;
    }

    /**
     * Sets the value of the policyDecisionPoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPolicyDecisionPoint(String value) {
        this.policyDecisionPoint = value;
    }

}
