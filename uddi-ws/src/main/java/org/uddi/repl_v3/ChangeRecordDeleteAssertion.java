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
import javax.xml.datatype.XMLGregorianCalendar;
import org.uddi.api_v3.PublisherAssertion;


/**
 * The changeRecordPublisherAssertion element describes the information that UDDI replication MUST convey in order to support the business-to-business relationship definition supported by UDDI. 

An implementation MUST be able to determine the Registry changes from the information transmitted within the replication stream.  The fromBusinessCheck and toBusinessCheck elements are Boolean values that represent which side of the business relationship is being asserted.  A changeRecordPublisherAssertion message may include one or both sides of the relationship.  For example, if the fromBusinessCheck is present and set to "true" then the assertion represents the parent-side of a parent-child relationship.
* 
* <br>
* <img src="http://www.uddi.org/pubs/uddi-v3.0.2-20041019_files/image115.gif">
* <br>
* A changeRecordPublisherAssertion element indicates that one or both sides of the business relationship are to be inserted.
<BR><BR>
a.       changeRecordPublisherAssertion with:
<BR><BR>
&lt;fromBusinessCheck>true</fromBusinessCheck> and &lt;toBusinessCheck>true</toBusinessCheck> is used to indicate that both sides of the publisherAssertion (i.e., business relationship) are to be inserted. The two businessEntity elements that are referred to within the publisherAssertion MUST be in the custody of the node that originates the changeRecord.
<BR><BR>
b.      changeRecordPublisherAssertion with:
<BR><BR>
&lt;fromBusinessCheck>true</fromBusinessCheck> and &lt;toBusinessCheck>false</toBusinessCheck> is used to indicate that the fromBusinessCheck side of the publisherAssertion (i.e., business relationship) is to be inserted. The businessEntity that is referred to in the fromBusinessCheck MUST be in the custody of the node that originates the changeRecord.
<BR><BR>
c.       changeRecordPublisherAssertion with:
<BR><BR>
&lt;fromBusinessCheck>false</fromBusinessCheck> and &lt;toBusinessCheck>true</toBusinessCheck> is used to indicate that the toBusinessCheck side of the publisherAssertion (i.e., business relationship) is to be inserted. The businessEntity that is referred to in the toBusinessCheck MUST be in the custody of the node that originates the changeRecord.
<BR><BR>
d.      changeRecordPublisherAssertion with:
<BR><BR>
&lt;fromBusinessCheck>false</fromBusinessCheck> and &lt;toBusinessCheck>false</toBusinessCheck> if this is received in the replication stream, such a changeRecord will not generate any change to the registry. The node SHOULD log any events such as this.
<BR><BR>
The operationalInfo element MUST contain a modified date corresponding to the update for the publisher assertion. This modified date should be stored by nodes supporting the subscription APIs in order to respond to subscription requests involving find_relatedBusinesses and get_assertionStatusReport filters. Since the publisherAssertions corresponding to a relationship may be originated from more than one node, the modified date stored for any relationship corresponding to the publisher should be the most recent date received from any node.
<BR><BR>
To handle signed publisherAssertion elements, it is necessary to indicate which set of signatures are being completely replaced as a result of the originating node’s change to update one or both sides of the relationship represented by the publisherAssertion. The optional signature element in the publisherAssertion must be ignored in replication and the toSignatures and fromSignatures elements must be used to replace signatures stored for the publisherAssertion. One of the elements toSignatures, fromSignatures or both must appear in the changeRecordPublisherAssertion. The presence of a toSignatures or fromSignatures element indicates that the signatures associated with the "to" or "from" side of the relationship must be deleted and completely replaced with the Signatures in the toSignatures or fromSignatures element. In the case where a single publisherAssertion represents both sides of the relationship, the node originating the corresponding changeRecordPublisherAssertion must include both a toSignatures and fromSignatures element with the identical set of Signature elements in both the toSignatures and fromSignatures. When the toSignatures element is not present, no changes are made to the signature elements associated with the "to" side of the relationship in the node. Similarily, when the fromSignatures element is not present, no changes are made to the signature elements associated with the "from" side of the relationship in the node.
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:uddi-org:api_v3}publisherAssertion"/>
 *         &lt;element name="fromBusinessCheck" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="toBusinessCheck" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="modified" type="{urn:uddi-org:api_v3}timeInstant"/>
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
    "publisherAssertion",
    "fromBusinessCheck",
    "toBusinessCheck",
    "modified"
})
@XmlRootElement(name = "changeRecordDeleteAssertion")
public class ChangeRecordDeleteAssertion implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = -4008979760033075709L;
	@XmlElement(namespace = "urn:uddi-org:api_v3", required = true)
    protected PublisherAssertion publisherAssertion;
    protected boolean fromBusinessCheck;
    protected boolean toBusinessCheck;
    @XmlElement(required = true)
    protected XMLGregorianCalendar modified;

    /**
     * Gets the value of the publisherAssertion property.
     * 
     * @return
     *     possible object is
     *     {@link PublisherAssertion }
     *     
     */
    public PublisherAssertion getPublisherAssertion() {
        return publisherAssertion;
    }

    /**
     * Sets the value of the publisherAssertion property.
     * 
     * @param value
     *     allowed object is
     *     {@link PublisherAssertion }
     *     
     */
    public void setPublisherAssertion(PublisherAssertion value) {
        this.publisherAssertion = value;
    }

    /**
     * Gets the value of the fromBusinessCheck property.
     * 
     */
    public boolean isFromBusinessCheck() {
        return fromBusinessCheck;
    }

    /**
     * Sets the value of the fromBusinessCheck property.
     * 
     */
    public void setFromBusinessCheck(boolean value) {
        this.fromBusinessCheck = value;
    }

    /**
     * Gets the value of the toBusinessCheck property.
     * 
     */
    public boolean isToBusinessCheck() {
        return toBusinessCheck;
    }

    /**
     * Sets the value of the toBusinessCheck property.
     * 
     */
    public void setToBusinessCheck(boolean value) {
        this.toBusinessCheck = value;
    }

    /**
     * Gets the value of the modified property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getModified() {
        return modified;
    }

    /**
     * Sets the value of the modified property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setModified(XMLGregorianCalendar value) {
        this.modified = value;
    }

}
