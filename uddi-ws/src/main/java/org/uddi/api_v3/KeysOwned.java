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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for keysOwned complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="keysOwned">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;sequence>
 *           &lt;element ref="{urn:uddi-org:api_v3}fromKey"/>
 *           &lt;element ref="{urn:uddi-org:api_v3}toKey" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;element ref="{urn:uddi-org:api_v3}toKey"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "keysOwned", propOrder = {
})
public class KeysOwned implements Serializable {

	@XmlTransient
	private static final long serialVersionUID = -6857337672894793291L;

    @XmlElement(required=false)
    protected String fromKey;

    @XmlElement(required=false)
    protected String toKey;
        
    public String getToKey() {
    	return toKey;
    }

    public void setToKey(String toKey) {
    	this.toKey = toKey;
    }
    
    public String getFromKey() {
    	return fromKey;
    }

    public void setFromKey(String fromKey) {
    	this.fromKey = fromKey;
    }    
}
