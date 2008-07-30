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


package org.uddi.vscache_v3;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.uddi.vscache_v3 package. 
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

    private final static QName _GetAllValidValues_QNAME = new QName("urn:uddi-org:vscache_v3", "get_allValidValues");
    private final static QName _KeyValue_QNAME = new QName("urn:uddi-org:vscache_v3", "keyValue");
    private final static QName _ValidValue_QNAME = new QName("urn:uddi-org:vscache_v3", "validValue");
    private final static QName _ValidValuesList_QNAME = new QName("urn:uddi-org:vscache_v3", "validValuesList");
    private final static QName _ChunkToken_QNAME = new QName("urn:uddi-org:vscache_v3", "chunkToken");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.uddi.vscache_v3
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ValidValuesList }
     * 
     */
    public ValidValuesList createValidValuesList() {
        return new ValidValuesList();
    }

    /**
     * Create an instance of {@link ValidValue }
     * 
     */
    public ValidValue createValidValue() {
        return new ValidValue();
    }

    /**
     * Create an instance of {@link GetAllValidValues }
     * 
     */
    public GetAllValidValues createGetAllValidValues() {
        return new GetAllValidValues();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllValidValues }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:vscache_v3", name = "get_allValidValues")
    public JAXBElement<GetAllValidValues> createGetAllValidValues(GetAllValidValues value) {
        return new JAXBElement<GetAllValidValues>(_GetAllValidValues_QNAME, GetAllValidValues.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:vscache_v3", name = "keyValue")
    public JAXBElement<String> createKeyValue(String value) {
        return new JAXBElement<String>(_KeyValue_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValidValue }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:vscache_v3", name = "validValue")
    public JAXBElement<ValidValue> createValidValue(ValidValue value) {
        return new JAXBElement<ValidValue>(_ValidValue_QNAME, ValidValue.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValidValuesList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:vscache_v3", name = "validValuesList")
    public JAXBElement<ValidValuesList> createValidValuesList(ValidValuesList value) {
        return new JAXBElement<ValidValuesList>(_ValidValuesList_QNAME, ValidValuesList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:vscache_v3", name = "chunkToken")
    public JAXBElement<String> createChunkToken(String value) {
        return new JAXBElement<String>(_ChunkToken_QNAME, String.class, null, value);
    }

}
