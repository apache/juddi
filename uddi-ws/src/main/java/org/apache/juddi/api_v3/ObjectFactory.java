
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

    private final static QName _AdminDeleteSubscriptionResponse_QNAME = new QName("urn:juddi-apache-org:api_v3", "adminDelete_SubscriptionResponse");
    private final static QName _GetReplicationNodesResponse_QNAME = new QName("urn:juddi-apache-org:api_v3", "get_ReplicationNodesResponse");
    private final static QName _GetAllPublisherDetail_QNAME = new QName("urn:juddi-apache-org:api_v3", "get_allPublisherDetail");
    private final static QName _SetReplicationNodes_QNAME = new QName("urn:juddi-apache-org:api_v3", "set_ReplicationNodes");
    private final static QName _DeleteClerk_QNAME = new QName("urn:juddi-apache-org:api_v3", "delete_Clerk");
    private final static QName _SyncSubscription_QNAME = new QName("urn:juddi-apache-org:api_v3", "sync_subscription");
    private final static QName _AdminSaveTModelResponse_QNAME = new QName("urn:juddi-apache-org:api_v3", "adminSave_tModelResponse");
    private final static QName _AdminSaveTModel_QNAME = new QName("urn:juddi-apache-org:api_v3", "adminSave_tModel");
    private final static QName _GetAllNodes_QNAME = new QName("urn:juddi-apache-org:api_v3", "get_AllNodes");
    private final static QName _GetAllClientSubscriptionInfo_QNAME = new QName("urn:juddi-apache-org:api_v3", "get_allClientSubscriptionInfo");
    private final static QName _SetAllValidValues_QNAME = new QName("urn:juddi-apache-org:api_v3", "set_allValidValues");
    private final static QName _AdminSaveBusinessResponse_QNAME = new QName("urn:juddi-apache-org:api_v3", "adminSave_BusinessResponse");
    private final static QName _GetAllClientSubscriptionInfoResponse_QNAME = new QName("urn:juddi-apache-org:api_v3", "get_allClientSubscriptionInfoResponse");
    private final static QName _SyncSubscriptionDetail_QNAME = new QName("urn:juddi-apache-org:api_v3", "sync_subscriptionDetail");
    private final static QName _InvokeSyncSubscription_QNAME = new QName("urn:juddi-apache-org:api_v3", "invoke_SyncSubscription");
    private final static QName _AdminSaveSubscriptionResponse_QNAME = new QName("urn:juddi-apache-org:api_v3", "adminSave_SubscriptionResponse");
    private final static QName _DeleteNode_QNAME = new QName("urn:juddi-apache-org:api_v3", "delete_Node");
    private final static QName _GetAllClerks_QNAME = new QName("urn:juddi-apache-org:api_v3", "get_AllClerks");
    private final static QName _GetPublisherDetail_QNAME = new QName("urn:juddi-apache-org:api_v3", "get_publisherDetail");
    private final static QName _SavePublisher_QNAME = new QName("urn:juddi-apache-org:api_v3", "save_publisher");
    private final static QName _DeleteClerkResponse_QNAME = new QName("urn:juddi-apache-org:api_v3", "delete_ClerkResponse");
    private final static QName _SetAllValidValuesResponse_QNAME = new QName("urn:juddi-apache-org:api_v3", "set_allValidValuesResponse");
    private final static QName _DeletePublisher_QNAME = new QName("urn:juddi-apache-org:api_v3", "delete_publisher");
    private final static QName _AdminDeleteSubscription_QNAME = new QName("urn:juddi-apache-org:api_v3", "adminDelete_Subscription");
    private final static QName _SaveClerkRequest_QNAME = new QName("urn:juddi-apache-org:api_v3", "save_ClerkRequest");
    private final static QName _DeleteClientSubscriptionInfo_QNAME = new QName("urn:juddi-apache-org:api_v3", "delete_ClientSubscriptionInfo");
    private final static QName _SaveNodeRequest_QNAME = new QName("urn:juddi-apache-org:api_v3", "save_NodeRequest");
    private final static QName _GetAllNodesResponse_QNAME = new QName("urn:juddi-apache-org:api_v3", "get_AllNodesResponse");
    private final static QName _GetAllClerksResponse_QNAME = new QName("urn:juddi-apache-org:api_v3", "get_AllClerksResponse");
    private final static QName _AdminSaveBusiness_QNAME = new QName("urn:juddi-apache-org:api_v3", "adminSave_Business");
    private final static QName _DeleteNodeResponse_QNAME = new QName("urn:juddi-apache-org:api_v3", "delete_NodeResponse");
    private final static QName _GetReplicationNodes_QNAME = new QName("urn:juddi-apache-org:api_v3", "get_ReplicationNodes");
    private final static QName _AdminSaveSubscription_QNAME = new QName("urn:juddi-apache-org:api_v3", "adminSave_Subscription");
    private final static QName _SetReplicationNodesResponse_QNAME = new QName("urn:juddi-apache-org:api_v3", "set_ReplicationNodesResponse");
    private final static QName _PublisherDetail_QNAME = new QName("urn:juddi-apache-org:api_v3", "publisherDetail");
    private final static QName _SaveClientSubscriptionInfoRequest_QNAME = new QName("urn:juddi-apache-org:api_v3", "save_ClientSubscriptionInfoRequest");
    private final static QName _Publisher_QNAME = new QName("urn:juddi-apache-org:api_v3", "publisher");
    private final static QName _AdminDeleteTmodel_QNAME = new QName("urn:juddi-apache-org:api_v3", "adminDelete_tmodel");

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
     * Create an instance of {@link AdminDeleteSubscriptionRequest }
     * 
     */
    public AdminDeleteSubscriptionRequest createAdminDeleteSubscriptionRequest() {
        return new AdminDeleteSubscriptionRequest();
    }

    /**
     * Create an instance of {@link SaveClerkInfo }
     * 
     */
    public SaveClerkInfo createSaveClerkInfo() {
        return new SaveClerkInfo();
    }

    /**
     * Create an instance of {@link GetPublisherDetail }
     * 
     */
    public GetPublisherDetail createGetPublisherDetail() {
        return new GetPublisherDetail();
    }

    /**
     * Create an instance of {@link SetReplicationNodesResponse }
     * 
     */
    public SetReplicationNodesResponse createSetReplicationNodesResponse() {
        return new SetReplicationNodesResponse();
    }

    /**
     * Create an instance of {@link GetAllPublisherDetail }
     * 
     */
    public GetAllPublisherDetail createGetAllPublisherDetail() {
        return new GetAllPublisherDetail();
    }

    /**
     * Create an instance of {@link SetReplicationNodes }
     * 
     */
    public SetReplicationNodes createSetReplicationNodes() {
        return new SetReplicationNodes();
    }

    /**
     * Create an instance of {@link GetAllClientSubscriptionInfoResponse }
     * 
     */
    public GetAllClientSubscriptionInfoResponse createGetAllClientSubscriptionInfoResponse() {
        return new GetAllClientSubscriptionInfoResponse();
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
     * Create an instance of {@link SyncSubscription }
     * 
     */
    public SyncSubscription createSyncSubscription() {
        return new SyncSubscription();
    }

    /**
     * Create an instance of {@link PublisherDetail }
     * 
     */
    public PublisherDetail createPublisherDetail() {
        return new PublisherDetail();
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
     * Create an instance of {@link DeleteClerk }
     * 
     */
    public DeleteClerk createDeleteClerk() {
        return new DeleteClerk();
    }

    /**
     * Create an instance of {@link GetAllNodesResponse }
     * 
     */
    public GetAllNodesResponse createGetAllNodesResponse() {
        return new GetAllNodesResponse();
    }

    /**
     * Create an instance of {@link SaveNodeInfo }
     * 
     */
    public SaveNodeInfo createSaveNodeInfo() {
        return new SaveNodeInfo();
    }

    /**
     * Create an instance of {@link SyncSubscriptionDetail }
     * 
     */
    public SyncSubscriptionDetail createSyncSubscriptionDetail() {
        return new SyncSubscriptionDetail();
    }

    /**
     * Create an instance of {@link AdminSaveSubscriptionResponse }
     * 
     */
    public AdminSaveSubscriptionResponse createAdminSaveSubscriptionResponse() {
        return new AdminSaveSubscriptionResponse();
    }

    /**
     * Create an instance of {@link GetAllClientSubscriptionInfo }
     * 
     */
    public GetAllClientSubscriptionInfo createGetAllClientSubscriptionInfo() {
        return new GetAllClientSubscriptionInfo();
    }

    /**
     * Create an instance of {@link GetAllNodes }
     * 
     */
    public GetAllNodes createGetAllNodes() {
        return new GetAllNodes();
    }

    /**
     * Create an instance of {@link GetAllClerksResponse }
     * 
     */
    public GetAllClerksResponse createGetAllClerksResponse() {
        return new GetAllClerksResponse();
    }

    /**
     * Create an instance of {@link AdminSaveTModel }
     * 
     */
    public AdminSaveTModel createAdminSaveTModel() {
        return new AdminSaveTModel();
    }

    /**
     * Create an instance of {@link AdminDeleteSubscriptionResponse }
     * 
     */
    public AdminDeleteSubscriptionResponse createAdminDeleteSubscriptionResponse() {
        return new AdminDeleteSubscriptionResponse();
    }

    /**
     * Create an instance of {@link SetAllValidValuesResponse }
     * 
     */
    public SetAllValidValuesResponse createSetAllValidValuesResponse() {
        return new SetAllValidValuesResponse();
    }

    /**
     * Create an instance of {@link AdminSaveTModelResponse }
     * 
     */
    public AdminSaveTModelResponse createAdminSaveTModelResponse() {
        return new AdminSaveTModelResponse();
    }

    /**
     * Create an instance of {@link DeleteClerkResponse }
     * 
     */
    public DeleteClerkResponse createDeleteClerkResponse() {
        return new DeleteClerkResponse();
    }

    /**
     * Create an instance of {@link GetReplicationNodesResponse }
     * 
     */
    public GetReplicationNodesResponse createGetReplicationNodesResponse() {
        return new GetReplicationNodesResponse();
    }

    /**
     * Create an instance of {@link AdminSaveBusinessResponse }
     * 
     */
    public AdminSaveBusinessResponse createAdminSaveBusinessResponse() {
        return new AdminSaveBusinessResponse();
    }

    /**
     * Create an instance of {@link AdminSaveSubscriptionRequest }
     * 
     */
    public AdminSaveSubscriptionRequest createAdminSaveSubscriptionRequest() {
        return new AdminSaveSubscriptionRequest();
    }

    /**
     * Create an instance of {@link GetReplicationNodes }
     * 
     */
    public GetReplicationNodes createGetReplicationNodes() {
        return new GetReplicationNodes();
    }

    /**
     * Create an instance of {@link SetAllValidValues }
     * 
     */
    public SetAllValidValues createSetAllValidValues() {
        return new SetAllValidValues();
    }

    /**
     * Create an instance of {@link DeleteNodeResponse }
     * 
     */
    public DeleteNodeResponse createDeleteNodeResponse() {
        return new DeleteNodeResponse();
    }

    /**
     * Create an instance of {@link DeletePublisher }
     * 
     */
    public DeletePublisher createDeletePublisher() {
        return new DeletePublisher();
    }

    /**
     * Create an instance of {@link GetAllClerks }
     * 
     */
    public GetAllClerks createGetAllClerks() {
        return new GetAllClerks();
    }

    /**
     * Create an instance of {@link AdminSaveBusiness }
     * 
     */
    public AdminSaveBusiness createAdminSaveBusiness() {
        return new AdminSaveBusiness();
    }

    /**
     * Create an instance of {@link DeleteNode }
     * 
     */
    public DeleteNode createDeleteNode() {
        return new DeleteNode();
    }

    /**
     * Create an instance of {@link SyncSubscriptionDetailResponse }
     * 
     */
    public SyncSubscriptionDetailResponse createSyncSubscriptionDetailResponse() {
        return new SyncSubscriptionDetailResponse();
    }

    /**
     * Create an instance of {@link SubscriptionWrapper }
     * 
     */
    public SubscriptionWrapper createSubscriptionWrapper() {
        return new SubscriptionWrapper();
    }

    /**
     * Create an instance of {@link ClientSubscriptionInfoDetail }
     * 
     */
    public ClientSubscriptionInfoDetail createClientSubscriptionInfoDetail() {
        return new ClientSubscriptionInfoDetail();
    }

    /**
     * Create an instance of {@link ClerkDetail }
     * 
     */
    public ClerkDetail createClerkDetail() {
        return new ClerkDetail();
    }

    /**
     * Create an instance of {@link AdminSaveBusinessWrapper }
     * 
     */
    public AdminSaveBusinessWrapper createAdminSaveBusinessWrapper() {
        return new AdminSaveBusinessWrapper();
    }

    /**
     * Create an instance of {@link ValidValues }
     * 
     */
    public ValidValues createValidValues() {
        return new ValidValues();
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
     * Create an instance of {@link AdminSaveTModelWrapper }
     * 
     */
    public AdminSaveTModelWrapper createAdminSaveTModelWrapper() {
        return new AdminSaveTModelWrapper();
    }

    /**
     * Create an instance of {@link NodeDetail }
     * 
     */
    public NodeDetail createNodeDetail() {
        return new NodeDetail();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AdminDeleteSubscriptionResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "adminDelete_SubscriptionResponse")
    public JAXBElement<AdminDeleteSubscriptionResponse> createAdminDeleteSubscriptionResponse(AdminDeleteSubscriptionResponse value) {
        return new JAXBElement<AdminDeleteSubscriptionResponse>(_AdminDeleteSubscriptionResponse_QNAME, AdminDeleteSubscriptionResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetReplicationNodesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "get_ReplicationNodesResponse")
    public JAXBElement<GetReplicationNodesResponse> createGetReplicationNodesResponse(GetReplicationNodesResponse value) {
        return new JAXBElement<GetReplicationNodesResponse>(_GetReplicationNodesResponse_QNAME, GetReplicationNodesResponse.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link SetReplicationNodes }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "set_ReplicationNodes")
    public JAXBElement<SetReplicationNodes> createSetReplicationNodes(SetReplicationNodes value) {
        return new JAXBElement<SetReplicationNodes>(_SetReplicationNodes_QNAME, SetReplicationNodes.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteClerk }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "delete_Clerk")
    public JAXBElement<DeleteClerk> createDeleteClerk(DeleteClerk value) {
        return new JAXBElement<DeleteClerk>(_DeleteClerk_QNAME, DeleteClerk.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link AdminSaveTModelResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "adminSave_tModelResponse")
    public JAXBElement<AdminSaveTModelResponse> createAdminSaveTModelResponse(AdminSaveTModelResponse value) {
        return new JAXBElement<AdminSaveTModelResponse>(_AdminSaveTModelResponse_QNAME, AdminSaveTModelResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AdminSaveTModel }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "adminSave_tModel")
    public JAXBElement<AdminSaveTModel> createAdminSaveTModel(AdminSaveTModel value) {
        return new JAXBElement<AdminSaveTModel>(_AdminSaveTModel_QNAME, AdminSaveTModel.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllNodes }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "get_AllNodes")
    public JAXBElement<GetAllNodes> createGetAllNodes(GetAllNodes value) {
        return new JAXBElement<GetAllNodes>(_GetAllNodes_QNAME, GetAllNodes.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllClientSubscriptionInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "get_allClientSubscriptionInfo")
    public JAXBElement<GetAllClientSubscriptionInfo> createGetAllClientSubscriptionInfo(GetAllClientSubscriptionInfo value) {
        return new JAXBElement<GetAllClientSubscriptionInfo>(_GetAllClientSubscriptionInfo_QNAME, GetAllClientSubscriptionInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAllValidValues }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "set_allValidValues")
    public JAXBElement<SetAllValidValues> createSetAllValidValues(SetAllValidValues value) {
        return new JAXBElement<SetAllValidValues>(_SetAllValidValues_QNAME, SetAllValidValues.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AdminSaveBusinessResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "adminSave_BusinessResponse")
    public JAXBElement<AdminSaveBusinessResponse> createAdminSaveBusinessResponse(AdminSaveBusinessResponse value) {
        return new JAXBElement<AdminSaveBusinessResponse>(_AdminSaveBusinessResponse_QNAME, AdminSaveBusinessResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllClientSubscriptionInfoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "get_allClientSubscriptionInfoResponse")
    public JAXBElement<GetAllClientSubscriptionInfoResponse> createGetAllClientSubscriptionInfoResponse(GetAllClientSubscriptionInfoResponse value) {
        return new JAXBElement<GetAllClientSubscriptionInfoResponse>(_GetAllClientSubscriptionInfoResponse_QNAME, GetAllClientSubscriptionInfoResponse.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link SyncSubscriptionRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "invoke_SyncSubscription")
    public JAXBElement<SyncSubscriptionRequest> createInvokeSyncSubscription(SyncSubscriptionRequest value) {
        return new JAXBElement<SyncSubscriptionRequest>(_InvokeSyncSubscription_QNAME, SyncSubscriptionRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AdminSaveSubscriptionResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "adminSave_SubscriptionResponse")
    public JAXBElement<AdminSaveSubscriptionResponse> createAdminSaveSubscriptionResponse(AdminSaveSubscriptionResponse value) {
        return new JAXBElement<AdminSaveSubscriptionResponse>(_AdminSaveSubscriptionResponse_QNAME, AdminSaveSubscriptionResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteNode }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "delete_Node")
    public JAXBElement<DeleteNode> createDeleteNode(DeleteNode value) {
        return new JAXBElement<DeleteNode>(_DeleteNode_QNAME, DeleteNode.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllClerks }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "get_AllClerks")
    public JAXBElement<GetAllClerks> createGetAllClerks(GetAllClerks value) {
        return new JAXBElement<GetAllClerks>(_GetAllClerks_QNAME, GetAllClerks.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteClerkResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "delete_ClerkResponse")
    public JAXBElement<DeleteClerkResponse> createDeleteClerkResponse(DeleteClerkResponse value) {
        return new JAXBElement<DeleteClerkResponse>(_DeleteClerkResponse_QNAME, DeleteClerkResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAllValidValuesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "set_allValidValuesResponse")
    public JAXBElement<SetAllValidValuesResponse> createSetAllValidValuesResponse(SetAllValidValuesResponse value) {
        return new JAXBElement<SetAllValidValuesResponse>(_SetAllValidValuesResponse_QNAME, SetAllValidValuesResponse.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link AdminDeleteSubscriptionRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "adminDelete_Subscription")
    public JAXBElement<AdminDeleteSubscriptionRequest> createAdminDeleteSubscription(AdminDeleteSubscriptionRequest value) {
        return new JAXBElement<AdminDeleteSubscriptionRequest>(_AdminDeleteSubscription_QNAME, AdminDeleteSubscriptionRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SaveClerkInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "save_ClerkRequest")
    public JAXBElement<SaveClerkInfo> createSaveClerkRequest(SaveClerkInfo value) {
        return new JAXBElement<SaveClerkInfo>(_SaveClerkRequest_QNAME, SaveClerkInfo.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link SaveNodeInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "save_NodeRequest")
    public JAXBElement<SaveNodeInfo> createSaveNodeRequest(SaveNodeInfo value) {
        return new JAXBElement<SaveNodeInfo>(_SaveNodeRequest_QNAME, SaveNodeInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllNodesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "get_AllNodesResponse")
    public JAXBElement<GetAllNodesResponse> createGetAllNodesResponse(GetAllNodesResponse value) {
        return new JAXBElement<GetAllNodesResponse>(_GetAllNodesResponse_QNAME, GetAllNodesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllClerksResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "get_AllClerksResponse")
    public JAXBElement<GetAllClerksResponse> createGetAllClerksResponse(GetAllClerksResponse value) {
        return new JAXBElement<GetAllClerksResponse>(_GetAllClerksResponse_QNAME, GetAllClerksResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AdminSaveBusiness }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "adminSave_Business")
    public JAXBElement<AdminSaveBusiness> createAdminSaveBusiness(AdminSaveBusiness value) {
        return new JAXBElement<AdminSaveBusiness>(_AdminSaveBusiness_QNAME, AdminSaveBusiness.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteNodeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "delete_NodeResponse")
    public JAXBElement<DeleteNodeResponse> createDeleteNodeResponse(DeleteNodeResponse value) {
        return new JAXBElement<DeleteNodeResponse>(_DeleteNodeResponse_QNAME, DeleteNodeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetReplicationNodes }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "get_ReplicationNodes")
    public JAXBElement<GetReplicationNodes> createGetReplicationNodes(GetReplicationNodes value) {
        return new JAXBElement<GetReplicationNodes>(_GetReplicationNodes_QNAME, GetReplicationNodes.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AdminSaveSubscriptionRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "adminSave_Subscription")
    public JAXBElement<AdminSaveSubscriptionRequest> createAdminSaveSubscription(AdminSaveSubscriptionRequest value) {
        return new JAXBElement<AdminSaveSubscriptionRequest>(_AdminSaveSubscription_QNAME, AdminSaveSubscriptionRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetReplicationNodesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "set_ReplicationNodesResponse")
    public JAXBElement<SetReplicationNodesResponse> createSetReplicationNodesResponse(SetReplicationNodesResponse value) {
        return new JAXBElement<SetReplicationNodesResponse>(_SetReplicationNodesResponse_QNAME, SetReplicationNodesResponse.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteTModel }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:api_v3", name = "adminDelete_tmodel")
    public JAXBElement<DeleteTModel> createAdminDeleteTmodel(DeleteTModel value) {
        return new JAXBElement<DeleteTModel>(_AdminDeleteTmodel_QNAME, DeleteTModel.class, null, value);
    }

}
