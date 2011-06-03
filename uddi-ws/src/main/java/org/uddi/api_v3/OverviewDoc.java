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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for overviewDoc complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="overviewDoc">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;sequence>
 *           &lt;element ref="{urn:uddi-org:api_v3}description" maxOccurs="unbounded"/>
 *           &lt;element ref="{urn:uddi-org:api_v3}overviewURL" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;element ref="{urn:uddi-org:api_v3}overviewURL"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "overviewDoc", propOrder = {
        "description", "overviewURL"
})
public class OverviewDoc implements Serializable {
	@XmlTransient
	private static final long serialVersionUID = 2375126956490542327L;
	@XmlElement
    protected List<Description> description;
    @XmlElement(required = false)
    protected OverviewURL overviewURL;

    
    public OverviewURL getOverviewURL() {
		return overviewURL;
	}


	public void setOverviewURL(OverviewURL overviewURL) {
		this.overviewURL = overviewURL;
	}

	/**
     * Gets the rest of the content model. 
     * 
     * <p>
     * You are getting this "catch-all" property because of the following reason: 
     * The field name "OverviewURL" is used by two different parts of a schema. See: 
     * line 392 of file:/C:/Development/Projects/jUDDI_v3/uddi-ws/src/main/resources/uddi_v3.xsd
     * line 390 of file:/C:/Development/Projects/jUDDI_v3/uddi-ws/src/main/resources/uddi_v3.xsd
     * <p>
     * To get rid of this property, apply a property customization to one 
     * of both of the following declarations to change their names: 
     * Gets the value of the content property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the content property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link Description }{@code >}
     * {@link JAXBElement }{@code <}{@link OverviewURL }{@code >}
     * 
     * 
     */
    public List<Description> getDescription() {
        if (description == null) {
        	description = new ArrayList<Description>();
        }
        return this.description;
    }
    
}
