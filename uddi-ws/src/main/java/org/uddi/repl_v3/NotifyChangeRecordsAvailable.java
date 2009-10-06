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
 *         &lt;element name="notifyingNode" type="{urn:uddi-org:repl_v3}operatorNodeID_type"/>
 *         &lt;element name="changesAvailable" type="{urn:uddi-org:repl_v3}highWaterMarkVector_type"/>
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
    "notifyingNode",
    "changesAvailable"
})
@XmlRootElement(name = "notify_changeRecordsAvailable")
public class NotifyChangeRecordsAvailable implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = -8280288001706059769L;
	@XmlElement(required = true)
    protected String notifyingNode;
    @XmlElement(required = true)
    protected HighWaterMarkVectorType changesAvailable;

    /**
     * Gets the value of the notifyingNode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotifyingNode() {
        return notifyingNode;
    }

    /**
     * Sets the value of the notifyingNode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotifyingNode(String value) {
        this.notifyingNode = value;
    }

    /**
     * Gets the value of the changesAvailable property.
     * 
     * @return
     *     possible object is
     *     {@link HighWaterMarkVectorType }
     *     
     */
    public HighWaterMarkVectorType getChangesAvailable() {
        return changesAvailable;
    }

    /**
     * Sets the value of the changesAvailable property.
     * 
     * @param value
     *     allowed object is
     *     {@link HighWaterMarkVectorType }
     *     
     */
    public void setChangesAvailable(HighWaterMarkVectorType value) {
        this.changesAvailable = value;
    }

}
