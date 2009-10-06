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
 *         &lt;element ref="{urn:uddi-org:repl_v3}changeRecordNewData"/>
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
    "changeRecordNewData"
})
@XmlRootElement(name = "changeRecordNewDataConditional")
public class ChangeRecordNewDataConditional implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = -5631615951563463814L;
	@XmlElement(required = true)
    protected ChangeRecordNewData changeRecordNewData;

    /**
     * Gets the value of the changeRecordNewData property.
     * 
     * @return
     *     possible object is
     *     {@link ChangeRecordNewData }
     *     
     */
    public ChangeRecordNewData getChangeRecordNewData() {
        return changeRecordNewData;
    }

    /**
     * Sets the value of the changeRecordNewData property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChangeRecordNewData }
     *     
     */
    public void setChangeRecordNewData(ChangeRecordNewData value) {
        this.changeRecordNewData = value;
    }

}
