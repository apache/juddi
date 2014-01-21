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
package org.uddi.api_v2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for instanceDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="instanceDetails">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:uddi-org:api_v2}description" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v2}overviewDoc" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v2}instanceParms" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "instanceDetails", propOrder = {
    "description",
    "overviewDoc",
    "instanceParms"
})
public class InstanceDetails {

    protected List<Description> description;
    protected OverviewDoc overviewDoc;
    protected String instanceParms;

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
     * Gets the value of the overviewDoc property.
     * 
     * @return
     *     possible object is
     *     {@link OverviewDoc }
     *     
     */
    public OverviewDoc getOverviewDoc() {
        return overviewDoc;
    }

    /**
     * Sets the value of the overviewDoc property.
     * 
     * @param value
     *     allowed object is
     *     {@link OverviewDoc }
     *     
     */
    public void setOverviewDoc(OverviewDoc value) {
        this.overviewDoc = value;
    }

    /**
     * Gets the value of the instanceParms property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstanceParms() {
        return instanceParms;
    }

    /**
     * Sets the value of the instanceParms property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstanceParms(String value) {
        this.instanceParms = value;
    }

}
