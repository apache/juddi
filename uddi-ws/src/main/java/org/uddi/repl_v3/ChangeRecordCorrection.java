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
 *         &lt;element ref="{urn:uddi-org:repl_v3}changeRecord"/>
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
    "changeRecord"
})
@XmlRootElement(name = "changeRecordCorrection")
public class ChangeRecordCorrection implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = -4858102532894902587L;
	@XmlElement(required = true)
    protected ChangeRecord changeRecord;

    /**
     * Gets the value of the changeRecord property.
     * 
     * @return
     *     possible object is
     *     {@link ChangeRecord }
     *     
     */
    public ChangeRecord getChangeRecord() {
        return changeRecord;
    }

    /**
     * Sets the value of the changeRecord property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChangeRecord }
     *     
     */
    public void setChangeRecord(ChangeRecord value) {
        this.changeRecord = value;
    }

}
