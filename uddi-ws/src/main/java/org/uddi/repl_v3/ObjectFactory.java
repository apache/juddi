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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.uddi.repl_v3 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _HighWaterMarks_QNAME = new QName("urn:uddi-org:repl_v3", "highWaterMarks");
    private final static QName _ChangeRecordNull_QNAME = new QName("urn:uddi-org:repl_v3", "changeRecordNull");
    private final static QName _TransferCustody_QNAME = new QName("urn:uddi-org:repl_v3", "transfer_custody");
    private final static QName _OperatorNodeID_QNAME = new QName("urn:uddi-org:repl_v3", "operatorNodeID");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.uddi.repl_v3
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ChangeRecordCorrection }
     * 
     */
    public ChangeRecordCorrection createChangeRecordCorrection() {
        return new ChangeRecordCorrection();
    }

    /**
     * Create an instance of {@link GetChangeRecords }
     * 
     */
    public GetChangeRecords createGetChangeRecords() {
        return new GetChangeRecords();
    }

    /**
     * Create an instance of {@link ChangeRecordDelete }
     * 
     */
    public ChangeRecordDelete createChangeRecordDelete() {
        return new ChangeRecordDelete();
    }

    /**
     * Create an instance of {@link TransferCustody }
     * 
     */
    public TransferCustody createTransferCustody() {
        return new TransferCustody();
    }

    /**
     * Create an instance of {@link ChangeRecords }
     * 
     */
    public ChangeRecords createChangeRecords() {
        return new ChangeRecords();
    }

    /**
     * Create an instance of {@link ReplicationConfiguration }
     * 
     */
    public ReplicationConfiguration createReplicationConfiguration() {
        return new ReplicationConfiguration();
    }

    /**
     * Create an instance of {@link ToSignatures }
     * 
     */
    public ToSignatures createToSignatures() {
        return new ToSignatures();
    }

    /**
     * Create an instance of {@link CommunicationGraph }
     * 
     */
    public CommunicationGraph createCommunicationGraph() {
        return new CommunicationGraph();
    }

    /**
     * Create an instance of {@link ChangeRecordHide }
     * 
     */
    public ChangeRecordHide createChangeRecordHide() {
        return new ChangeRecordHide();
    }

    /**
     * Create an instance of {@link ChangeRecordDeleteAssertion }
     * 
     */
    public ChangeRecordDeleteAssertion createChangeRecordDeleteAssertion() {
        return new ChangeRecordDeleteAssertion();
    }

    /**
     * Create an instance of {@link GetHighWaterMarks }
     * 
     */
    public GetHighWaterMarks createGetHighWaterMarks() {
        return new GetHighWaterMarks();
    }

    /**
     * Create an instance of {@link ChangeRecordPublisherAssertion }
     * 
     */
    public ChangeRecordPublisherAssertion createChangeRecordPublisherAssertion() {
        return new ChangeRecordPublisherAssertion();
    }

    /**
     * Create an instance of {@link DoPing }
     * 
     */
    public DoPing createDoPing() {
        return new DoPing();
    }

    /**
     * Create an instance of {@link HighWaterMarkVectorType }
     * 
     */
    public HighWaterMarkVectorType createHighWaterMarkVectorType() {
        return new HighWaterMarkVectorType();
    }

    /**
     * Create an instance of {@link NotifyChangeRecordsAvailable }
     * 
     */
    public NotifyChangeRecordsAvailable createNotifyChangeRecordsAvailable() {
        return new NotifyChangeRecordsAvailable();
    }

    /**
     * Create an instance of {@link ChangeRecord }
     * 
     */
    public ChangeRecord createChangeRecord() {
        return new ChangeRecord();
    }

    /**
     * Create an instance of {@link FromSignatures }
     * 
     */
    public FromSignatures createFromSignatures() {
        return new FromSignatures();
    }

    /**
     * Create an instance of {@link ChangeRecordIDType }
     * 
     */
    public ChangeRecordIDType createChangeRecordIDType() {
        return new ChangeRecordIDType();
    }

    /**
     * Create an instance of {@link ChangeRecordNewDataConditional }
     * 
     */
    public ChangeRecordNewDataConditional createChangeRecordNewDataConditional() {
        return new ChangeRecordNewDataConditional();
    }

    /**
     * Create an instance of {@link ChangeRecordConditionFailed }
     * 
     */
    public ChangeRecordConditionFailed createChangeRecordConditionFailed() {
        return new ChangeRecordConditionFailed();
    }

    /**
     * Create an instance of {@link ChangeRecordAcknowledgement }
     * 
     */
    public ChangeRecordAcknowledgement createChangeRecordAcknowledgement() {
        return new ChangeRecordAcknowledgement();
    }

    /**
     * Create an instance of {@link ReplicationConfiguration.RegistryContact }
     * 
     */
    public ReplicationConfiguration.RegistryContact createReplicationConfigurationRegistryContact() {
        return new ReplicationConfiguration.RegistryContact();
    }

    /**
     * Create an instance of {@link CommunicationGraph.Edge }
     * 
     */
    public CommunicationGraph.Edge createCommunicationGraphEdge() {
        return new CommunicationGraph.Edge();
    }

    /**
     * Create an instance of {@link Operator }
     * 
     */
    public Operator createOperator() {
        return new Operator();
    }

    /**
     * Create an instance of {@link ChangeRecordNewData }
     * 
     */
    public ChangeRecordNewData createChangeRecordNewData() {
        return new ChangeRecordNewData();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HighWaterMarkVectorType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:repl_v3", name = "highWaterMarks")
    public JAXBElement<HighWaterMarkVectorType> createHighWaterMarks(HighWaterMarkVectorType value) {
        return new JAXBElement<HighWaterMarkVectorType>(_HighWaterMarks_QNAME, HighWaterMarkVectorType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:repl_v3", name = "changeRecordNull")
    public JAXBElement<Object> createChangeRecordNull(Object value) {
        return new JAXBElement<Object>(_ChangeRecordNull_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransferCustody }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:repl_v3", name = "transfer_custody")
    public JAXBElement<TransferCustody> createTransferCustody(TransferCustody value) {
        return new JAXBElement<TransferCustody>(_TransferCustody_QNAME, TransferCustody.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:repl_v3", name = "operatorNodeID")
    public JAXBElement<String> createOperatorNodeID(String value) {
        return new JAXBElement<String>(_OperatorNodeID_QNAME, String.class, null, value);
    }

}
