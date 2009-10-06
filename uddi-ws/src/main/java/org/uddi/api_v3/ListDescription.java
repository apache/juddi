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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for listDescription complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="listDescription">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:uddi-org:api_v3}includeCount"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}actualCount"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}listHead"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "listDescription", propOrder = {
    "includeCount",
    "actualCount",
    "listHead"
})
public class ListDescription implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = 77138243591130431L;

	protected int includeCount;
    protected int actualCount;
    protected int listHead;

    /**
     * Gets the value of the includeCount property.
     * 
     */
    public int getIncludeCount() {
        return includeCount;
    }

    /**
     * Sets the value of the includeCount property.
     * 
     */
    public void setIncludeCount(int value) {
        this.includeCount = value;
    }

    /**
     * Gets the value of the actualCount property.
     * 
     */
    public int getActualCount() {
        return actualCount;
    }

    /**
     * Sets the value of the actualCount property.
     * 
     */
    public void setActualCount(int value) {
        this.actualCount = value;
    }

    /**
     * Gets the value of the listHead property.
     * 
     */
    public int getListHead() {
        return listHead;
    }

    /**
     * Sets the value of the listHead property.
     * 
     */
    public void setListHead(int value) {
        this.listHead = value;
    }

}
