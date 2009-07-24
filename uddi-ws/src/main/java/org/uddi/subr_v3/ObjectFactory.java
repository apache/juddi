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


package org.uddi.subr_v3;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.uddi.subr_v3 package. 
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

    private final static QName _NotifySubscriptionListener_QNAME = new QName("urn:uddi-org:subr_v3", "notify_subscriptionListener");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.uddi.subr_v3
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link NotifySubscriptionListener }
     * 
     */
    public NotifySubscriptionListener createNotifySubscriptionListener() {
        return new NotifySubscriptionListener();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotifySubscriptionListener }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:subr_v3", name = "notify_subscriptionListener")
    public JAXBElement<NotifySubscriptionListener> createNotifySubscriptionListener(NotifySubscriptionListener value) {
        return new JAXBElement<NotifySubscriptionListener>(_NotifySubscriptionListener_QNAME, NotifySubscriptionListener.class, null, value);
    }

}
