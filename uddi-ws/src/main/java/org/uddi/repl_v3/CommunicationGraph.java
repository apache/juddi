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
import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element name="node" type="{urn:uddi-org:repl_v3}operatorNodeID_type" maxOccurs="unbounded"/>
 *         &lt;element name="controlledMessage" type="{urn:uddi-org:repl_v3}controlledMessage_type" maxOccurs="unbounded"/>
 *         &lt;element name="edge" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="message" type="{urn:uddi-org:repl_v3}message_type" maxOccurs="unbounded"/>
 *                   &lt;element name="messageSender" type="{urn:uddi-org:repl_v3}operatorNodeID_type"/>
 *                   &lt;element name="messageReceiver" type="{urn:uddi-org:repl_v3}operatorNodeID_type"/>
 *                   &lt;element name="messageReceiverAlternate" type="{urn:uddi-org:repl_v3}operatorNodeID_type" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
    "node",
    "controlledMessage",
    "edge"
})
@XmlRootElement(name = "communicationGraph")
public class CommunicationGraph implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = -2640578798126913062L;
	@XmlElement(required = true)
    protected List<String> node;
    @XmlElement(required = true)
    protected List<String> controlledMessage;
    protected List<CommunicationGraph.Edge> edge;

    /**
     * Gets the value of the node property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the node property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getNode() {
        if (node == null) {
            node = new ArrayList<String>();
        }
        return this.node;
    }

    /**
     * Gets the value of the controlledMessage property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the controlledMessage property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getControlledMessage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getControlledMessage() {
        if (controlledMessage == null) {
            controlledMessage = new ArrayList<String>();
        }
        return this.controlledMessage;
    }

    /**
     * Gets the value of the edge property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the edge property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEdge().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CommunicationGraph.Edge }
     * 
     * 
     */
    public List<CommunicationGraph.Edge> getEdge() {
        if (edge == null) {
            edge = new ArrayList<CommunicationGraph.Edge>();
        }
        return this.edge;
    }


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
     *         &lt;element name="message" type="{urn:uddi-org:repl_v3}message_type" maxOccurs="unbounded"/>
     *         &lt;element name="messageSender" type="{urn:uddi-org:repl_v3}operatorNodeID_type"/>
     *         &lt;element name="messageReceiver" type="{urn:uddi-org:repl_v3}operatorNodeID_type"/>
     *         &lt;element name="messageReceiverAlternate" type="{urn:uddi-org:repl_v3}operatorNodeID_type" maxOccurs="unbounded" minOccurs="0"/>
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
        "message",
        "messageSender",
        "messageReceiver",
        "messageReceiverAlternate"
    })
    public static class Edge {

        @XmlElement(required = true)
        protected List<String> message;
        @XmlElement(required = true)
        protected String messageSender;
        @XmlElement(required = true)
        protected String messageReceiver;
        protected List<String> messageReceiverAlternate;

        /**
         * Gets the value of the message property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the message property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getMessage().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getMessage() {
            if (message == null) {
                message = new ArrayList<String>();
            }
            return this.message;
        }

        /**
         * Gets the value of the messageSender property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMessageSender() {
            return messageSender;
        }

        /**
         * Sets the value of the messageSender property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMessageSender(String value) {
            this.messageSender = value;
        }

        /**
         * Gets the value of the messageReceiver property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMessageReceiver() {
            return messageReceiver;
        }

        /**
         * Sets the value of the messageReceiver property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMessageReceiver(String value) {
            this.messageReceiver = value;
        }

        /**
         * Gets the value of the messageReceiverAlternate property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the messageReceiverAlternate property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getMessageReceiverAlternate().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getMessageReceiverAlternate() {
            if (messageReceiverAlternate == null) {
                messageReceiverAlternate = new ArrayList<String>();
            }
            return this.messageReceiverAlternate;
        }

    }

}
