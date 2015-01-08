/*
 * Copyright 2001-2009 The Apache Software Foundation.
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
package org.apache.juddi.api_v3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.uddi.repl_v3.ChangeRecords;


/**
 * <p>Java class for getEntityHistoryMessageResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getEntityHistoryMessageResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:uddi-org:repl_v3}changeRecords"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getEntityHistoryMessageResponse", propOrder = {
    "changeRecords"
})
public class GetEntityHistoryMessageResponse {

    @XmlElement(namespace = "urn:uddi-org:repl_v3", required = true)
    protected ChangeRecords changeRecords;

    /**
     * Gets the value of the changeRecords property.
     * 
     * @return
     *     possible object is
     *     {@link ChangeRecords }
     *     
     */
    public ChangeRecords getChangeRecords() {
        return changeRecords;
    }

    /**
     * Sets the value of the changeRecords property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChangeRecords }
     *     
     */
    public void setChangeRecords(ChangeRecords value) {
        this.changeRecords = value;
    }

}
