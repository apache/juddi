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
import org.uddi.api_v3.DispositionReport;


/**
 * <p>Java class for adminSave_BusinessResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="adminSave_BusinessResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dispositionReport" type="{urn:uddi-org:api_v3}dispositionReport"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "adminSave_BusinessResponse", propOrder = {
    "dispositionReport"
})
public class AdminSaveBusinessResponse {

    @XmlElement(required = true)
    protected DispositionReport dispositionReport;

    /**
     * Gets the value of the dispositionReport property.
     * 
     * @return
     *     possible object is
     *     {@link DispositionReport }
     *     
     */
    public DispositionReport getDispositionReport() {
        return dispositionReport;
    }

    /**
     * Sets the value of the dispositionReport property.
     * 
     * @param value
     *     allowed object is
     *     {@link DispositionReport }
     *     
     */
    public void setDispositionReport(DispositionReport value) {
        this.dispositionReport = value;
    }

}
