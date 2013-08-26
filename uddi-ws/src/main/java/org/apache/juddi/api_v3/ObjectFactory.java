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


package org.apache.juddi.api_v3;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 * This object was copied from the UDDI generated package 
 * and hand-edited to fit the juddi specific Publisher structures.
 * 
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@XmlRegistry
public class ObjectFactory {
	
	private final static QName _SaveClerk_QNAME = new QName("urn:juddi-apache-org:api_v3", "save_clerk");
	private final static QName _SaveNode_QNAME = new QName("urn:juddi-apache-org:api_v3", "save_node");
    private final static QName _SavePublisher_QNAME = new QName("urn:juddi-apache-org:api_v3", "save_publisher");
    private final static QName _DeletePublisher_QNAME = new QName("urn:juddi-apache-org:api_v3", "delete_publisher");
    private final static QName _SaveClientSubscriptionInfo_QNAME = new QName("urn:juddi-apache-org:api_v3", "save_clientSubscriptionInfo");
    private final static QName _DeleteClientSubscriptionInfo_QNAME = new QName("urn:juddi-apache-org:api_v3", "delete_clientSubscriptionInfo");
    
    private final static QName _Publisher_QNAME = new QName("urn:juddi-apache-org:api_v3", "publisher");
   
    private final static QName _ClerkDetail_QNAME = new QName("urn:juddi-apache-org:api_v3", "clerkDetail");
    private final static QName _NodeDetail_QNAME = new QName("urn:juddi-apache-org:api_v3", "nodeDetail");
    private final static QName _PublisherDetail_QNAME = new QName("urn:juddi-apache-org:api_v3", "publisherDetail");
    private final static QName _GetPublisherDetail_QNAME = new QName("urn:juddi-apache-org:api_v3", "get_publisherDetail");
    private final static QName _GetAllPublisherDetail_QNAME = new QName("urn:juddi-apache-org:api_v3", "get_allPublisherDetail");
    private final static QName _SyncSubscription_QNAME = new QName("urn:juddi-apache-org:api_v3", "sync_subscription");
    private final static QName _ClientSubscriptionInfoDetail_QNAME = new QName("urn:juddi-apache-org:api_v3", "clientSubscriptionInfoDetail");
    private final static QName _SyncSubscriptionDetail_QNAME = new QName("urn:juddi-apache-org:api_v3", "syncSubscriptionDetail");

//    
//    /**
//     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.apache.juddi.api_v3
//     * 
//     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SaveClerk }
     * 
     */
    public SaveClerk createSaveClerk() {
        return new SaveClerk();
    }
    
    /**
     * Create an instance of {@link SaveNode }
     * 
     */
    public SaveNode createSaveNode() {
        return new SaveNode();
    }
    
    /**
     * Create an instance of {@link SavePublisher }
     * 
     */
    public SavePublisher createSavePublisher() {
        return new SavePublisher();
    }
    
    /**
     * Create an instance of {@link SaveClientSubscriptionInfo }
     * 
     */
    public SaveClientSubscriptionInfo createSaveClientSubscriptionInfo() {
        return new SaveClientSubscriptionInfo();
    }

    /**
     * Create an instance of {@link DeletePublisher }
     * 
     */
    public DeletePublisher createDeletePublisher() {
        return new DeletePublisher();
    }
    
    /**
     * Create an instance of {@link DeleteClientSubscriptionInfo }
     * 
     */
    public DeleteClientSubscriptionInfo createDeleteClientSubscriptionInfo() {
        return new DeleteClientSubscriptionInfo();
    }

    /**
     * Create an instance of {@link Publisher }
     * 
     */
    public Publisher createPublisher() {
        return new Publisher();
    }
    
 
    /**
     * Create an instance of {@link ClerkDetail }
     * 
     */
    public ClerkDetail createClerkDetail() {
        return new ClerkDetail();
    }
    
    /**
     * Create an instance of {@link PublisherDetail }
     * 
     */
    public PublisherDetail createPublisherDetail() {
        return new PublisherDetail();
    }

    /**
     * Create an instance of {@link GetPublisherDetail }
     * 
     */
    public GetPublisherDetail createGetPublisherDetail() {
        return new GetPublisherDetail();
    }
    
    /**
     * Create an instance of {@link GetPublisherDetail }
     * 
     */
    public GetAllPublisherDetail createGetAllPublisherDetail() {
        return new GetAllPublisherDetail();
    }

    /**
     * Create an instance of {@link GetPublisherDetail }
     * 
     */
    public SyncSubscription createSyncSubscription() {
        return new SyncSubscription();
    }
    
    /**
     * Create an instance of {@link GetPublisherDetail }
     * 
     */
    public SyncSubscriptionDetail createSyncSubscriptionDetail() {
        return new SyncSubscriptionDetail();
    }
    
    /**
     * Create an instance of {@link ClientSubscriptionInfoDetail }
     * 
     */
    public ClientSubscriptionInfoDetail createClientSubscriptionInfoDetail() {
        return new ClientSubscriptionInfoDetail();
    }

    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SavePublisher }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "save_clerk")
    public JAXBElement<SaveClerk> createSaveClerk(SaveClerk value) {
        return new JAXBElement<SaveClerk>(_SaveClerk_QNAME, SaveClerk.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SavePublisher }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "save_node")
    public JAXBElement<SaveNode> createSaveNode(SaveNode value) {
        return new JAXBElement<SaveNode>(_SaveNode_QNAME, SaveNode.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SavePublisher }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "save_publisher")
    public JAXBElement<SavePublisher> createSavePublisher(SavePublisher value) {
        return new JAXBElement<SavePublisher>(_SavePublisher_QNAME, SavePublisher.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SavePublisher }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "save_clientSubscriptionInfo")
    public JAXBElement<SaveClientSubscriptionInfo> createSaveClientSubscriptionInfo(SaveClientSubscriptionInfo value) {
        return new JAXBElement<SaveClientSubscriptionInfo>(_SaveClientSubscriptionInfo_QNAME, SaveClientSubscriptionInfo.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeletePublisher }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "delete_publisher")
    public JAXBElement<DeletePublisher> createDeletePublisher(DeletePublisher value) {
        return new JAXBElement<DeletePublisher>(_DeletePublisher_QNAME, DeletePublisher.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SavePublisher }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "delete_clientSubscriptionInfo")
    public JAXBElement<DeleteClientSubscriptionInfo> createDeleteClientSubscriptionInfo(DeleteClientSubscriptionInfo value) {
        return new JAXBElement<DeleteClientSubscriptionInfo>(_DeleteClientSubscriptionInfo_QNAME, DeleteClientSubscriptionInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Publisher }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "publisher")
    public JAXBElement<Publisher> createPublisher(Publisher value) {
        return new JAXBElement<Publisher>(_Publisher_QNAME, Publisher.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PublisherDetail }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "clerkDetail")
    public JAXBElement<ClerkDetail> createClerkDetail(ClerkDetail value) {
        return new JAXBElement<ClerkDetail>(_ClerkDetail_QNAME, ClerkDetail.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PublisherDetail }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "nodeDetail")
    public JAXBElement<NodeDetail> createNodeDetail(NodeDetail value) {
        return new JAXBElement<NodeDetail>(_NodeDetail_QNAME, NodeDetail.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PublisherDetail }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "publisherDetail")
    public JAXBElement<PublisherDetail> createPublisherDetail(PublisherDetail value) {
        return new JAXBElement<PublisherDetail>(_PublisherDetail_QNAME, PublisherDetail.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PublisherDetail }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "clientSubscriptionInfoDetail")
    public JAXBElement<ClientSubscriptionInfoDetail> createClientSubscriptionInfoDetail(ClientSubscriptionInfoDetail value) {
        return new JAXBElement<ClientSubscriptionInfoDetail>(_ClientSubscriptionInfoDetail_QNAME, ClientSubscriptionInfoDetail.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPublisherDetail }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "get_publisherDetail")
    public JAXBElement<GetPublisherDetail> createGetPublisherDetail(GetPublisherDetail value) {
        return new JAXBElement<GetPublisherDetail>(_GetPublisherDetail_QNAME, GetPublisherDetail.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllPublisherDetail }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "get_allPublisherDetail")
    public JAXBElement<GetAllPublisherDetail> createGetAllPublisherDetail(GetAllPublisherDetail value) {
        return new JAXBElement<GetAllPublisherDetail>(_GetAllPublisherDetail_QNAME, GetAllPublisherDetail.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SyncSubscription }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "sync_subscription")
    public JAXBElement<SyncSubscription> createSyncSubscription(SyncSubscription value) {
        return new JAXBElement<SyncSubscription>(_SyncSubscription_QNAME, SyncSubscription.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SyncSubscription }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "sync_subscriptionDetail")
    public JAXBElement<SyncSubscriptionDetail> createSyncSubscription(SyncSubscriptionDetail value) {
        return new JAXBElement<SyncSubscriptionDetail>(_SyncSubscriptionDetail_QNAME, SyncSubscriptionDetail.class, null, value);
    }
    
//    /**
//     * Create an instance of {@link JAXBElement }{@code <}{@link SyncSubscriptionDetail }{@code >}}
//     * 
//     */
//    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "sync_subscriptionDetail")
//    public JAXBElement<SyncSubscriptionDetail> createGetAllSyncSubsriptionDetail(SyncSubscriptionDetail value) {
//        return new JAXBElement<SyncSubscriptionDetail>(_SyncSubscriptionDetail_QNAME, SyncSubscriptionDetail.class, null, value);
//    }   
}
