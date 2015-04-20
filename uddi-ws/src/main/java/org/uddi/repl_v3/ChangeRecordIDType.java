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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for changeRecordID_type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained
 * within this class.
 *
 * <pre>
 * &lt;complexType name="changeRecordID_type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="nodeID" type="{urn:uddi-org:repl_v3}operatorNodeID_type"/>
 *         &lt;element name="originatingUSN" type="{urn:uddi-org:repl_v3}USN_type" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "changeRecordID_type", propOrder = {
        "nodeID",
        "originatingUSN"
})
public class ChangeRecordIDType implements Serializable {

        @XmlTransient
        private static final long serialVersionUID = -8665882376068031545L;
        @XmlElement(required = true)
        protected String nodeID;
        protected Long originatingUSN;

        public ChangeRecordIDType() {
        }

        /**
         * 
         * @param node owning node of the item/source of request
         * @param oUSN Each node SHALL maintain a strictly increasing register known as its Originating Update Sequence Number (USN).  An originating USN is assigned to a change record at its creation by a node. The originating USN SHALL NEVER decrease in value, even in the face of system crashes and restarts. UDDI nodes MUST NOT rely on an originating USN sequence increasing monotonically by a value of "1".  Gaps in a node's originating USN sequence MUST be allowed for as they are likely to occur in the face of system crashes and restarts.

While processing changes to the Registry as a result of performing UDDI Replication, all replicated data MUST be assigned an additional unique and locally generated USN register value – a local USN. 

The originating and local USN registers MUST be sufficiently large such that register rollover is not a concern. For this purpose, UDDI nodes MUST implement a USN of exactly 63 bits in size.

Note that it is semantically meaningless to compare USNs that have been generated on different nodes; only USNs generated on the same node may be meaningfully compared to each other.

NO change record MAY have a USN equal to 0 (zero).  
         */
        public ChangeRecordIDType(String node, Long oUSN) {
                originatingUSN = oUSN;
                nodeID = node;
        }

        /**
         * Gets the value of the nodeID property.
         *
         * @return possible object is {@link String }
         *
         */
        public String getNodeID() {
                return nodeID;
        }

        /**
         * Sets the value of the nodeID property.
         *
         * @param value allowed object is {@link String }
         *
         */
        public void setNodeID(String value) {
                this.nodeID = value;
        }

        /**
         * Gets the value of the originatingUSN property.
         *
         * @return possible object is {@link Long }
         *
         */
        public Long getOriginatingUSN() {
                return originatingUSN;
        }

        /**
         * Sets the value of the originatingUSN property.
         *
         * @param value allowed object is {@link Long }
         *
         */
        public void setOriginatingUSN(Long value) {
                this.originatingUSN = value;
        }
}
