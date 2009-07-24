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


package org.uddi.sub_v3;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.uddi.sub_v3 package. 
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

    private final static QName _DeleteSubscription_QNAME = new QName("urn:uddi-org:sub_v3", "delete_subscription");
    private final static QName _MaxEntities_QNAME = new QName("urn:uddi-org:sub_v3", "maxEntities");
    private final static QName _EndPoint_QNAME = new QName("urn:uddi-org:sub_v3", "endPoint");
    private final static QName _ExpiresAfter_QNAME = new QName("urn:uddi-org:sub_v3", "expiresAfter");
    private final static QName _NotificationInterval_QNAME = new QName("urn:uddi-org:sub_v3", "notificationInterval");
    private final static QName _GetSubscriptions_QNAME = new QName("urn:uddi-org:sub_v3", "get_subscriptions");
    private final static QName _StartPoint_QNAME = new QName("urn:uddi-org:sub_v3", "startPoint");
    private final static QName _SubscriptionKey_QNAME = new QName("urn:uddi-org:sub_v3", "subscriptionKey");
    private final static QName _CoveragePeriod_QNAME = new QName("urn:uddi-org:sub_v3", "coveragePeriod");
    private final static QName _Subscriptions_QNAME = new QName("urn:uddi-org:sub_v3", "subscriptions");
    private final static QName _Subscription_QNAME = new QName("urn:uddi-org:sub_v3", "subscription");
    private final static QName _SubscriptionResultsList_QNAME = new QName("urn:uddi-org:sub_v3", "subscriptionResultsList");
    private final static QName _SubscriptionFilter_QNAME = new QName("urn:uddi-org:sub_v3", "subscriptionFilter");
    private final static QName _GetSubscriptionResults_QNAME = new QName("urn:uddi-org:sub_v3", "get_subscriptionResults");
    private final static QName _SaveSubscription_QNAME = new QName("urn:uddi-org:sub_v3", "save_subscription");
    private final static QName _KeyBag_QNAME = new QName("urn:uddi-org:sub_v3", "keyBag");
    private final static QName _Deleted_QNAME = new QName("urn:uddi-org:sub_v3", "deleted");
    private final static QName _ChunkToken_QNAME = new QName("urn:uddi-org:sub_v3", "chunkToken");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.uddi.sub_v3
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetSubscriptionResults }
     * 
     */
    public GetSubscriptionResults createGetSubscriptionResults() {
        return new GetSubscriptionResults();
    }

    /**
     * Create an instance of {@link SaveSubscription }
     * 
     */
    public SaveSubscription createSaveSubscription() {
        return new SaveSubscription();
    }

    /**
     * Create an instance of {@link Subscription }
     * 
     */
    public Subscription createSubscription() {
        return new Subscription();
    }

    /**
     * Create an instance of {@link CoveragePeriod }
     * 
     */
    public CoveragePeriod createCoveragePeriod() {
        return new CoveragePeriod();
    }

    /**
     * Create an instance of {@link SubscriptionFilter }
     * 
     */
    public SubscriptionFilter createSubscriptionFilter() {
        return new SubscriptionFilter();
    }

    /**
     * Create an instance of {@link Subscriptions }
     * 
     */
    public Subscriptions createSubscriptions() {
        return new Subscriptions();
    }

    /**
     * Create an instance of {@link SubscriptionResultsList }
     * 
     */
    public SubscriptionResultsList createSubscriptionResultsList() {
        return new SubscriptionResultsList();
    }

    /**
     * Create an instance of {@link GetSubscriptions }
     * 
     */
    public GetSubscriptions createGetSubscriptions() {
        return new GetSubscriptions();
    }

    /**
     * Create an instance of {@link DeleteSubscription }
     * 
     */
    public DeleteSubscription createDeleteSubscription() {
        return new DeleteSubscription();
    }

    /**
     * Create an instance of {@link KeyBag }
     * 
     */
    public KeyBag createKeyBag() {
        return new KeyBag();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteSubscription }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:sub_v3", name = "delete_subscription")
    public JAXBElement<DeleteSubscription> createDeleteSubscription(DeleteSubscription value) {
        return new JAXBElement<DeleteSubscription>(_DeleteSubscription_QNAME, DeleteSubscription.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:sub_v3", name = "maxEntities")
    public JAXBElement<Integer> createMaxEntities(Integer value) {
        return new JAXBElement<Integer>(_MaxEntities_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:sub_v3", name = "endPoint")
    public JAXBElement<XMLGregorianCalendar> createEndPoint(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_EndPoint_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:sub_v3", name = "expiresAfter")
    public JAXBElement<XMLGregorianCalendar> createExpiresAfter(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_ExpiresAfter_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Duration }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:sub_v3", name = "notificationInterval")
    public JAXBElement<Duration> createNotificationInterval(Duration value) {
        return new JAXBElement<Duration>(_NotificationInterval_QNAME, Duration.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSubscriptions }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:sub_v3", name = "get_subscriptions")
    public JAXBElement<GetSubscriptions> createGetSubscriptions(GetSubscriptions value) {
        return new JAXBElement<GetSubscriptions>(_GetSubscriptions_QNAME, GetSubscriptions.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:sub_v3", name = "startPoint")
    public JAXBElement<XMLGregorianCalendar> createStartPoint(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_StartPoint_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:sub_v3", name = "subscriptionKey")
    public JAXBElement<String> createSubscriptionKey(String value) {
        return new JAXBElement<String>(_SubscriptionKey_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CoveragePeriod }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:sub_v3", name = "coveragePeriod")
    public JAXBElement<CoveragePeriod> createCoveragePeriod(CoveragePeriod value) {
        return new JAXBElement<CoveragePeriod>(_CoveragePeriod_QNAME, CoveragePeriod.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Subscriptions }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:sub_v3", name = "subscriptions")
    public JAXBElement<Subscriptions> createSubscriptions(Subscriptions value) {
        return new JAXBElement<Subscriptions>(_Subscriptions_QNAME, Subscriptions.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Subscription }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:sub_v3", name = "subscription")
    public JAXBElement<Subscription> createSubscription(Subscription value) {
        return new JAXBElement<Subscription>(_Subscription_QNAME, Subscription.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SubscriptionResultsList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:sub_v3", name = "subscriptionResultsList")
    public JAXBElement<SubscriptionResultsList> createSubscriptionResultsList(SubscriptionResultsList value) {
        return new JAXBElement<SubscriptionResultsList>(_SubscriptionResultsList_QNAME, SubscriptionResultsList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SubscriptionFilter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:sub_v3", name = "subscriptionFilter")
    public JAXBElement<SubscriptionFilter> createSubscriptionFilter(SubscriptionFilter value) {
        return new JAXBElement<SubscriptionFilter>(_SubscriptionFilter_QNAME, SubscriptionFilter.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSubscriptionResults }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:sub_v3", name = "get_subscriptionResults")
    public JAXBElement<GetSubscriptionResults> createGetSubscriptionResults(GetSubscriptionResults value) {
        return new JAXBElement<GetSubscriptionResults>(_GetSubscriptionResults_QNAME, GetSubscriptionResults.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SaveSubscription }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:sub_v3", name = "save_subscription")
    public JAXBElement<SaveSubscription> createSaveSubscription(SaveSubscription value) {
        return new JAXBElement<SaveSubscription>(_SaveSubscription_QNAME, SaveSubscription.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KeyBag }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:sub_v3", name = "keyBag")
    public JAXBElement<KeyBag> createKeyBag(KeyBag value) {
        return new JAXBElement<KeyBag>(_KeyBag_QNAME, KeyBag.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:sub_v3", name = "deleted")
    public JAXBElement<Boolean> createDeleted(Boolean value) {
        return new JAXBElement<Boolean>(_Deleted_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:uddi-org:sub_v3", name = "chunkToken")
    public JAXBElement<String> createChunkToken(String value) {
        return new JAXBElement<String>(_ChunkToken_QNAME, String.class, null, value);
    }

}
