/*
 * Copyright 2001-2009 The Apache Software Foundation.
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
import org.uddi.api_v3.DeleteTModel;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.apache.juddi.api_v3 package. 
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

    private final static QName _DeletePublisher_QNAME = new QName("urn:juddi-apache-org:api_v3", "delete_publisher");
    private final static QName _GetAllPublisherDetail_QNAME = new QName("urn:juddi-apache-org:api_v3", "get_allPublisherDetail");
    private final static QName _SaveClerkRequest_QNAME = new QName("urn:juddi-apache-org:api_v3", "save_ClerkRequest");
    private final static QName _DeleteClientSubscriptionInfo_QNAME = new QName("urn:juddi-apache-org:api_v3", "delete_ClientSubscriptionInfo");
    private final static QName _SaveNodeRequest_QNAME = new QName("urn:juddi-apache-org:api_v3", "save_NodeRequest");
    private final static QName _SyncSubscription_QNAME = new QName("urn:juddi-apache-org:api_v3", "sync_subscription");
    private final static QName _PublisherDetailResponse_QNAME = new QName("urn:juddi-apache-org:api_v3", "publisherDetailResponse");
    private final static QName _SaveNodeResponse_QNAME = new QName("urn:juddi-apache-org:api_v3", "save_NodeResponse");
    private final static QName _SaveClientSubscriptionInfoResponse_QNAME = new QName("urn:juddi-apache-org:api_v3", "save_ClientSubscriptionInfoResponse");
    private final static QName _SyncSubscriptionDetail_QNAME = new QName("urn:juddi-apache-org:api_v3", "sync_subscriptionDetail");
    private final static QName _PublisherDetail_QNAME = new QName("urn:juddi-apache-org:api_v3", "publisherDetail");
    private final static QName _SaveClientSubscriptionInfoRequest_QNAME = new QName("urn:juddi-apache-org:api_v3", "save_ClientSubscriptionInfoRequest");
    private final static QName _Publisher_QNAME = new QName("urn:juddi-apache-org:api_v3", "publisher");
    private final static QName _InvokeSyncSubscription_QNAME = new QName("urn:juddi-apache-org:api_v3", "invoke_SyncSubscription");
    private final static QName _AdminDeleteTmodel_QNAME = new QName("urn:juddi-apache-org:api_v3", "adminDelete_tmodel");
    private final static QName _SavePublisherResponse_QNAME = new QName("urn:juddi-apache-org:api_v3", "save_publisherResponse");
    private final static QName _SaveClerkResponse_QNAME = new QName("urn:juddi-apache-org:api_v3", "save_ClerkResponse");
    private final static QName _GetPublisherDetail_QNAME = new QName("urn:juddi-apache-org:api_v3", "get_publisherDetail");
    private final static QName _SavePublisher_QNAME = new QName("urn:juddi-apache-org:api_v3", "save_publisher");
    private final static QName _InvokeSyncSubscriptionResponse_QNAME = new QName("urn:juddi-apache-org:api_v3", "invoke_SyncSubscriptionResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.apache.juddi.api_v3
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DeleteClientSubscriptionInfo }
     * 
     */
    public DeleteClientSubscriptionInfo createDeleteClientSubscriptionInfo() {
        return new DeleteClientSubscriptionInfo();
    }

    /**
     * Create an instance of {@link SaveClerk }
     * 
     */
    public SaveClerk createSaveClerk() {
        return new SaveClerk();
    }

    /**
     * Create an instance of {@link ClientSubscriptionInfoDetail }
     * 
     */
    public ClientSubscriptionInfoDetail createClientSubscriptionInfoDetail() {
        return new ClientSubscriptionInfoDetail();
    }

    /**
     * Create an instance of {@link NodeDetail }
     * 
     */
    public NodeDetail createNodeDetail() {
        return new NodeDetail();
    }

    /**
     * Create an instance of {@link GetPublisherDetail }
     * 
     */
    public GetPublisherDetail createGetPublisherDetail() {
        return new GetPublisherDetail();
    }

    /**
     * Create an instance of {@link ClerkDetail }
     * 
     */
    public ClerkDetail createClerkDetail() {
        return new ClerkDetail();
    }

    /**
     * Create an instance of {@link GetAllPublisherDetail }
     * 
     */
    public GetAllPublisherDetail createGetAllPublisherDetail() {
        return new GetAllPublisherDetail();
    }

    /**
     * Create an instance of {@link Publisher }
     * 
     */
    public Publisher createPublisher() {
        return new Publisher();
    }

    /**
     * Create an instance of {@link SaveClientSubscriptionInfo }
     * 
     */
    public SaveClientSubscriptionInfo createSaveClientSubscriptionInfo() {
        return new SaveClientSubscriptionInfo();
    }

    /**
     * Create an instance of {@link PublisherDetail }
     * 
     */
    public PublisherDetail createPublisherDetail() {
        return new PublisherDetail();
    }

    /**
     * Create an instance of {@link SyncSubscription }
     * 
     */
    public SyncSubscription createSyncSubscription() {
        return new SyncSubscription();
    }

    /**
     * Create an instance of {@link SyncSubscriptionDetailResponse }
     * 
     */
    public SyncSubscriptionDetailResponse createSyncSubscriptionDetailResponse() {
        return new SyncSubscriptionDetailResponse();
    }

    /**
     * Create an instance of {@link SyncSubscriptionRequest }
     * 
     */
    public SyncSubscriptionRequest createSyncSubscriptionRequest() {
        return new SyncSubscriptionRequest();
    }

    /**
     * Create an instance of {@link SavePublisher }
     * 
     */
    public SavePublisher createSavePublisher() {
        return new SavePublisher();
    }

    /**
     * Create an instance of {@link DeletePublisher }
     * 
     */
    public DeletePublisher createDeletePublisher() {
        return new DeletePublisher();
    }

    /**
     * Create an instance of {@link SyncSubscriptionDetail }
     * 
     */
    public SyncSubscriptionDetail createSyncSubscriptionDetail() {
        return new SyncSubscriptionDetail();
    }

    /**
     * Create an instance of {@link SaveNode }
     * 
     */
    public SaveNode createSaveNode() {
        return new SaveNode();
    }

    /**
     * Create an instance of {@link ClientSubscriptionInfo }
     * 
     */
    public ClientSubscriptionInfo createClientSubscriptionInfo() {
        return new ClientSubscriptionInfo();
    }

    /**
     * Create an instance of {@link Node }
     * 
     */
    public Node createNode() {
        return new Node();
    }

    /**
     * Create an instance of {@link Clerk }
     * 
     */
    public Clerk createClerk() {
        return new Clerk();
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
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllPublisherDetail }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "get_allPublisherDetail")
    public JAXBElement<GetAllPublisherDetail> createGetAllPublisherDetail(GetAllPublisherDetail value) {
        return new JAXBElement<GetAllPublisherDetail>(_GetAllPublisherDetail_QNAME, GetAllPublisherDetail.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SaveClerk }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "save_ClerkRequest")
    public JAXBElement<SaveClerk> createSaveClerkRequest(SaveClerk value) {
        return new JAXBElement<SaveClerk>(_SaveClerkRequest_QNAME, SaveClerk.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteClientSubscriptionInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "delete_ClientSubscriptionInfo")
    public JAXBElement<DeleteClientSubscriptionInfo> createDeleteClientSubscriptionInfo(DeleteClientSubscriptionInfo value) {
        return new JAXBElement<DeleteClientSubscriptionInfo>(_DeleteClientSubscriptionInfo_QNAME, DeleteClientSubscriptionInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SaveNode }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "save_NodeRequest")
    public JAXBElement<SaveNode> createSaveNodeRequest(SaveNode value) {
        return new JAXBElement<SaveNode>(_SaveNodeRequest_QNAME, SaveNode.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link PublisherDetail }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "publisherDetailResponse")
    public JAXBElement<PublisherDetail> createPublisherDetailResponse(PublisherDetail value) {
        return new JAXBElement<PublisherDetail>(_PublisherDetailResponse_QNAME, PublisherDetail.class, null, value);
    }

	/**
     * Create an instance of {@link JAXBElement }{@code <}{@link NodeDetail }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "save_NodeResponse")
    public JAXBElement<NodeDetail> createSaveNodeResponse(NodeDetail value) {
        return new JAXBElement<NodeDetail>(_SaveNodeResponse_QNAME, NodeDetail.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ClientSubscriptionInfoDetail }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "save_ClientSubscriptionInfoResponse")
    public JAXBElement<ClientSubscriptionInfoDetail> createSaveClientSubscriptionInfoResponse(ClientSubscriptionInfoDetail value) {
        return new JAXBElement<ClientSubscriptionInfoDetail>(_SaveClientSubscriptionInfoResponse_QNAME, ClientSubscriptionInfoDetail.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SyncSubscriptionDetail }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "sync_subscriptionDetail")
    public JAXBElement<SyncSubscriptionDetail> createSyncSubscriptionDetail(SyncSubscriptionDetail value) {
        return new JAXBElement<SyncSubscriptionDetail>(_SyncSubscriptionDetail_QNAME, SyncSubscriptionDetail.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link SaveClientSubscriptionInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "save_ClientSubscriptionInfoRequest")
    public JAXBElement<SaveClientSubscriptionInfo> createSaveClientSubscriptionInfoRequest(SaveClientSubscriptionInfo value) {
        return new JAXBElement<SaveClientSubscriptionInfo>(_SaveClientSubscriptionInfoRequest_QNAME, SaveClientSubscriptionInfo.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link SyncSubscriptionRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "invoke_SyncSubscription")
    public JAXBElement<SyncSubscriptionRequest> createInvokeSyncSubscription(SyncSubscriptionRequest value) {
        return new JAXBElement<SyncSubscriptionRequest>(_InvokeSyncSubscription_QNAME, SyncSubscriptionRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteTModel }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "adminDelete_tmodel")
    public JAXBElement<DeleteTModel> createAdminDeleteTmodel(DeleteTModel value) {
        return new JAXBElement<DeleteTModel>(_AdminDeleteTmodel_QNAME, DeleteTModel.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PublisherDetail }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "save_publisherResponse")
    public JAXBElement<PublisherDetail> createSavePublisherResponse(PublisherDetail value) {
        return new JAXBElement<PublisherDetail>(_SavePublisherResponse_QNAME, PublisherDetail.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ClerkDetail }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "save_ClerkResponse")
    public JAXBElement<ClerkDetail> createSaveClerkResponse(ClerkDetail value) {
        return new JAXBElement<ClerkDetail>(_SaveClerkResponse_QNAME, ClerkDetail.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link SavePublisher }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "save_publisher")
    public JAXBElement<SavePublisher> createSavePublisher(SavePublisher value) {
        return new JAXBElement<SavePublisher>(_SavePublisher_QNAME, SavePublisher.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SyncSubscriptionDetailResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "invoke_SyncSubscriptionResponse")
    public JAXBElement<SyncSubscriptionDetailResponse> createInvokeSyncSubscriptionResponse(SyncSubscriptionDetailResponse value) {
        return new JAXBElement<SyncSubscriptionDetailResponse>(_InvokeSyncSubscriptionResponse_QNAME, SyncSubscriptionDetailResponse.class, null, value);
    }

}
