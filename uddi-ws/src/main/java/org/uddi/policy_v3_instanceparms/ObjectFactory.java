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


package org.uddi.policy_v3_instanceparms;

import java.math.BigInteger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.uddi.policy_v3_instanceparms package. 
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

    private final static QName _AuthInfoUse_QNAME = new QName("urn:uddi-org:policy_v3_instanceParms", "authInfoUse");
    private final static QName _MaximumRequestMessageSize_QNAME = new QName("urn:uddi-org:policy_v3_instanceParms", "maximumRequestMessageSize");
    private final static QName _UDDIinstanceParmsContainer_QNAME = new QName("urn:uddi-org:policy_v3_instanceParms", "UDDIinstanceParmsContainer");
    private final static QName _FilterUsingFindAPI_QNAME = new QName("urn:uddi-org:policy_v3_instanceParms", "filterUsingFindAPI");
    private final static QName _DefaultSortOrder_QNAME = new QName("urn:uddi-org:policy_v3_instanceParms", "defaultSortOrder");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.uddi.policy_v3_instanceparms
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UDDIinstanceParmsContainerType }
     * 
     */
    public UDDIinstanceParmsContainerType createUDDIinstanceParmsContainerType() {
        return new UDDIinstanceParmsContainerType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AuthInfoUseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:policy_v3_instanceParms", name = "authInfoUse")
    public JAXBElement<AuthInfoUseType> createAuthInfoUse(AuthInfoUseType value) {
        return new JAXBElement<AuthInfoUseType>(_AuthInfoUse_QNAME, AuthInfoUseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:policy_v3_instanceParms", name = "maximumRequestMessageSize")
    public JAXBElement<BigInteger> createMaximumRequestMessageSize(BigInteger value) {
        return new JAXBElement<BigInteger>(_MaximumRequestMessageSize_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UDDIinstanceParmsContainerType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:policy_v3_instanceParms", name = "UDDIinstanceParmsContainer")
    public JAXBElement<UDDIinstanceParmsContainerType> createUDDIinstanceParmsContainer(UDDIinstanceParmsContainerType value) {
        return new JAXBElement<UDDIinstanceParmsContainerType>(_UDDIinstanceParmsContainer_QNAME, UDDIinstanceParmsContainerType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FilterUsingFindAPIType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:policy_v3_instanceParms", name = "filterUsingFindAPI")
    public JAXBElement<FilterUsingFindAPIType> createFilterUsingFindAPI(FilterUsingFindAPIType value) {
        return new JAXBElement<FilterUsingFindAPIType>(_FilterUsingFindAPI_QNAME, FilterUsingFindAPIType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:policy_v3_instanceParms", name = "defaultSortOrder")
    public JAXBElement<String> createDefaultSortOrder(String value) {
        return new JAXBElement<String>(_DefaultSortOrder_QNAME, String.class, null, value);
    }

}
