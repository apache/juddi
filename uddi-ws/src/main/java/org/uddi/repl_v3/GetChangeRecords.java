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
import java.math.BigInteger;
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
 *         &lt;element name="requestingNode" type="{urn:uddi-org:repl_v3}operatorNodeID_type"/>
 *         &lt;element name="changesAlreadySeen" type="{urn:uddi-org:repl_v3}highWaterMarkVector_type" minOccurs="0"/>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="responseLimitCount" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *           &lt;element name="responseLimitVector" type="{urn:uddi-org:repl_v3}highWaterMarkVector_type"/>
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
@XmlType(name = "", propOrder = {
    "requestingNode",
    "changesAlreadySeen",
    "responseLimitCount",
    "responseLimitVector"
})
@XmlRootElement(name = "get_changeRecords")
public class GetChangeRecords implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = -4384132817965793900L;
	@XmlElement(required = true)
    protected String requestingNode;
    protected HighWaterMarkVectorType changesAlreadySeen;
    protected BigInteger responseLimitCount;
    protected HighWaterMarkVectorType responseLimitVector;

    /**
     * Gets the value of the requestingNode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestingNode() {
        return requestingNode;
    }

    /**
     * Sets the value of the requestingNode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestingNode(String value) {
        this.requestingNode = value;
    }

    /**
     * Gets the value of the changesAlreadySeen property.
     * 
     * @return
     *     possible object is
     *     {@link HighWaterMarkVectorType }
     *     
     */
    public HighWaterMarkVectorType getChangesAlreadySeen() {
        return changesAlreadySeen;
    }

    /**
     * Sets the value of the changesAlreadySeen property.
     * 
     * @param value
     *     allowed object is
     *     {@link HighWaterMarkVectorType }
     *     
     */
    public void setChangesAlreadySeen(HighWaterMarkVectorType value) {
        this.changesAlreadySeen = value;
    }

    /**
     * Gets the value of the responseLimitCount property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getResponseLimitCount() {
        return responseLimitCount;
    }

    /**
     * Sets the value of the responseLimitCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setResponseLimitCount(BigInteger value) {
        this.responseLimitCount = value;
    }

    /**
     * Gets the value of the responseLimitVector property.
     * 
     * @return
     *     possible object is
     *     {@link HighWaterMarkVectorType }
     *     
     */
    public HighWaterMarkVectorType getResponseLimitVector() {
        return responseLimitVector;
    }

    /**
     * Sets the value of the responseLimitVector property.
     * 
     * @param value
     *     allowed object is
     *     {@link HighWaterMarkVectorType }
     *     
     */
    public void setResponseLimitVector(HighWaterMarkVectorType value) {
        this.responseLimitVector = value;
    }

}
