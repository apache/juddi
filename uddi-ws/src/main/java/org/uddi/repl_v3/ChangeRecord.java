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
import javax.xml.bind.annotation.XmlAttribute;
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
 *         &lt;element name="changeID" type="{urn:uddi-org:repl_v3}changeRecordID_type"/>
 *         &lt;group ref="{urn:uddi-org:repl_v3}changeRecordPayload_type"/>
 *       &lt;/sequence>
 *       &lt;attribute name="acknowledgementRequested" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "changeID",
    "changeRecordNull",
    "changeRecordNewData",
    "changeRecordDelete",
    "changeRecordPublisherAssertion",
    "changeRecordHide",
    "changeRecordDeleteAssertion",
    "changeRecordAcknowledgement",
    "changeRecordCorrection",
    "changeRecordNewDataConditional",
    "changeRecordConditionFailed"
})
@XmlRootElement(name = "changeRecord")
public class ChangeRecord implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = 5218553502385587391L;
	@XmlElement(required = true)
    protected ChangeRecordIDType changeID;
    protected Object changeRecordNull;
    protected ChangeRecordNewData changeRecordNewData;
    protected ChangeRecordDelete changeRecordDelete;
    protected ChangeRecordPublisherAssertion changeRecordPublisherAssertion;
    protected ChangeRecordHide changeRecordHide;
    protected ChangeRecordDeleteAssertion changeRecordDeleteAssertion;
    protected ChangeRecordAcknowledgement changeRecordAcknowledgement;
    protected ChangeRecordCorrection changeRecordCorrection;
    protected ChangeRecordNewDataConditional changeRecordNewDataConditional;
    protected ChangeRecordConditionFailed changeRecordConditionFailed;
    @XmlAttribute(required = true)
    protected boolean acknowledgementRequested;

    /**
     * Gets the value of the changeID property.
     * 
     * @return
     *     possible object is
     *     {@link ChangeRecordIDType }
     *     
     */
    public ChangeRecordIDType getChangeID() {
        return changeID;
    }

    /**
     * Sets the value of the changeID property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChangeRecordIDType }
     *     
     */
    public void setChangeID(ChangeRecordIDType value) {
        this.changeID = value;
    }

    /**
     * Gets the value of the changeRecordNull property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getChangeRecordNull() {
        return changeRecordNull;
    }

    /**
     * Sets the value of the changeRecordNull property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setChangeRecordNull(Object value) {
        this.changeRecordNull = value;
    }

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

    /**
     * Gets the value of the changeRecordDelete property.
     * 
     * @return
     *     possible object is
     *     {@link ChangeRecordDelete }
     *     
     */
    public ChangeRecordDelete getChangeRecordDelete() {
        return changeRecordDelete;
    }

    /**
     * Sets the value of the changeRecordDelete property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChangeRecordDelete }
     *     
     */
    public void setChangeRecordDelete(ChangeRecordDelete value) {
        this.changeRecordDelete = value;
    }

    /**
     * Gets the value of the changeRecordPublisherAssertion property.
     * 
     * @return
     *     possible object is
     *     {@link ChangeRecordPublisherAssertion }
     *     
     */
    public ChangeRecordPublisherAssertion getChangeRecordPublisherAssertion() {
        return changeRecordPublisherAssertion;
    }

    /**
     * Sets the value of the changeRecordPublisherAssertion property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChangeRecordPublisherAssertion }
     *     
     */
    public void setChangeRecordPublisherAssertion(ChangeRecordPublisherAssertion value) {
        this.changeRecordPublisherAssertion = value;
    }

    /**
     * Gets the value of the changeRecordHide property.
     * 
     * @return
     *     possible object is
     *     {@link ChangeRecordHide }
     *     
     */
    public ChangeRecordHide getChangeRecordHide() {
        return changeRecordHide;
    }

    /**
     * Sets the value of the changeRecordHide property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChangeRecordHide }
     *     
     */
    public void setChangeRecordHide(ChangeRecordHide value) {
        this.changeRecordHide = value;
    }

    /**
     * Gets the value of the changeRecordDeleteAssertion property.
     * 
     * @return
     *     possible object is
     *     {@link ChangeRecordDeleteAssertion }
     *     
     */
    public ChangeRecordDeleteAssertion getChangeRecordDeleteAssertion() {
        return changeRecordDeleteAssertion;
    }

    /**
     * Sets the value of the changeRecordDeleteAssertion property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChangeRecordDeleteAssertion }
     *     
     */
    public void setChangeRecordDeleteAssertion(ChangeRecordDeleteAssertion value) {
        this.changeRecordDeleteAssertion = value;
    }

    /**
     * Gets the value of the changeRecordAcknowledgement property.
     * 
     * @return
     *     possible object is
     *     {@link ChangeRecordAcknowledgement }
     *     
     */
    public ChangeRecordAcknowledgement getChangeRecordAcknowledgement() {
        return changeRecordAcknowledgement;
    }

    /**
     * Sets the value of the changeRecordAcknowledgement property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChangeRecordAcknowledgement }
     *     
     */
    public void setChangeRecordAcknowledgement(ChangeRecordAcknowledgement value) {
        this.changeRecordAcknowledgement = value;
    }

    /**
     * Gets the value of the changeRecordCorrection property.
     * 
     * @return
     *     possible object is
     *     {@link ChangeRecordCorrection }
     *     
     */
    public ChangeRecordCorrection getChangeRecordCorrection() {
        return changeRecordCorrection;
    }

    /**
     * Sets the value of the changeRecordCorrection property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChangeRecordCorrection }
     *     
     */
    public void setChangeRecordCorrection(ChangeRecordCorrection value) {
        this.changeRecordCorrection = value;
    }

    /**
     * Gets the value of the changeRecordNewDataConditional property.
     * 
     * @return
     *     possible object is
     *     {@link ChangeRecordNewDataConditional }
     *     
     */
    public ChangeRecordNewDataConditional getChangeRecordNewDataConditional() {
        return changeRecordNewDataConditional;
    }

    /**
     * Sets the value of the changeRecordNewDataConditional property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChangeRecordNewDataConditional }
     *     
     */
    public void setChangeRecordNewDataConditional(ChangeRecordNewDataConditional value) {
        this.changeRecordNewDataConditional = value;
    }

    /**
     * Gets the value of the changeRecordConditionFailed property.
     * 
     * @return
     *     possible object is
     *     {@link ChangeRecordConditionFailed }
     *     
     */
    public ChangeRecordConditionFailed getChangeRecordConditionFailed() {
        return changeRecordConditionFailed;
    }

    /**
     * Sets the value of the changeRecordConditionFailed property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChangeRecordConditionFailed }
     *     
     */
    public void setChangeRecordConditionFailed(ChangeRecordConditionFailed value) {
        this.changeRecordConditionFailed = value;
    }

    /**
     * Gets the value of the acknowledgementRequested property.
     * 
     */
    public boolean isAcknowledgementRequested() {
        return acknowledgementRequested;
    }

    /**
     * Sets the value of the acknowledgementRequested property.
     * 
     */
    public void setAcknowledgementRequested(boolean value) {
        this.acknowledgementRequested = value;
    }

}
