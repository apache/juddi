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


package org.uddi.custody_v3;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.uddi.custody_v3 package. 
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

    private final static QName _KeyBag_QNAME = new QName("urn:uddi-org:custody_v3", "keyBag");
    private final static QName _DiscardTransferToken_QNAME = new QName("urn:uddi-org:custody_v3", "discard_transferToken");
    private final static QName _GetTransferToken_QNAME = new QName("urn:uddi-org:custody_v3", "get_transferToken");
    private final static QName _TransferEntities_QNAME = new QName("urn:uddi-org:custody_v3", "transfer_entities");
    private final static QName _TransferOperationalInfo_QNAME = new QName("urn:uddi-org:custody_v3", "transferOperationalInfo");
    private final static QName _TransferToken_QNAME = new QName("urn:uddi-org:custody_v3", "transferToken");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.uddi.custody_v3
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TransferOperationalInfo }
     * 
     */
    public TransferOperationalInfo createTransferOperationalInfo() {
        return new TransferOperationalInfo();
    }

    /**
     * Create an instance of {@link TransferEntities }
     * 
     */
    public TransferEntities createTransferEntities() {
        return new TransferEntities();
    }

    /**
     * Create an instance of {@link DiscardTransferToken }
     * 
     */
    public DiscardTransferToken createDiscardTransferToken() {
        return new DiscardTransferToken();
    }

    /**
     * Create an instance of {@link GetTransferToken }
     * 
     */
    public GetTransferToken createGetTransferToken() {
        return new GetTransferToken();
    }

    /**
     * Create an instance of {@link KeyBag }
     * 
     */
    public KeyBag createKeyBag() {
        return new KeyBag();
    }

    /**
     * Create an instance of {@link TransferToken }
     * 
     */
    public TransferToken createTransferToken() {
        return new TransferToken();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KeyBag }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:custody_v3", name = "keyBag")
    public JAXBElement<KeyBag> createKeyBag(KeyBag value) {
        return new JAXBElement<KeyBag>(_KeyBag_QNAME, KeyBag.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DiscardTransferToken }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:custody_v3", name = "discard_transferToken")
    public JAXBElement<DiscardTransferToken> createDiscardTransferToken(DiscardTransferToken value) {
        return new JAXBElement<DiscardTransferToken>(_DiscardTransferToken_QNAME, DiscardTransferToken.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetTransferToken }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:custody_v3", name = "get_transferToken")
    public JAXBElement<GetTransferToken> createGetTransferToken(GetTransferToken value) {
        return new JAXBElement<GetTransferToken>(_GetTransferToken_QNAME, GetTransferToken.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransferEntities }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:custody_v3", name = "transfer_entities")
    public JAXBElement<TransferEntities> createTransferEntities(TransferEntities value) {
        return new JAXBElement<TransferEntities>(_TransferEntities_QNAME, TransferEntities.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransferOperationalInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:custody_v3", name = "transferOperationalInfo")
    public JAXBElement<TransferOperationalInfo> createTransferOperationalInfo(TransferOperationalInfo value) {
        return new JAXBElement<TransferOperationalInfo>(_TransferOperationalInfo_QNAME, TransferOperationalInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransferToken }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:custody_v3", name = "transferToken")
    public JAXBElement<TransferToken> createTransferToken(TransferToken value) {
        return new JAXBElement<TransferToken>(_TransferToken_QNAME, TransferToken.class, null, value);
    }

}
