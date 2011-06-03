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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
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
 *         &lt;element ref="{urn:uddi-org:api_v3}description" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;sequence>
 *             &lt;element ref="{urn:uddi-org:api_v3}overviewDoc" maxOccurs="unbounded"/>
 *             &lt;element ref="{urn:uddi-org:api_v3}instanceParms" minOccurs="0"/>
 *           &lt;/sequence>
 *           &lt;element ref="{urn:uddi-org:api_v3}instanceParms"/>
 *         &lt;/choice>
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
    "description", "overviewDoc", "instanceParms"
})
public class InstanceDetails implements Serializable {
	@XmlTransient
	private static final long serialVersionUID = -8133581381978826309L;
    @XmlElement(required=false)
    protected String instanceParms;
    @XmlElement(required=false)
    protected List<Description> description;
    @XmlElement(required=false)
    protected List<OverviewDoc> overviewDoc;
    
    public void setInstanceParms(String instanceParms) {
		this.instanceParms = instanceParms;
	}
    
    public String getInstanceParms() {
    	return instanceParms;
    }
    
    public List<OverviewDoc> getOverviewDoc() {
        if (overviewDoc == null) {
            overviewDoc = new ArrayList<OverviewDoc>();
        }
        return this.overviewDoc;    	
    }
    
    public List<Description> getDescription() {
        if (description == null) {
            description = new ArrayList<Description>();
        }
        return this.description;
    }

}
