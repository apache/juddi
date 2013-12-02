
package org.apache.juddi.v3_service;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.apache.juddi.api_v3.ClerkDetail;
import org.apache.juddi.api_v3.ClientSubscriptionInfoDetail;
import org.apache.juddi.api_v3.NodeDetail;
import org.apache.juddi.api_v3.PublisherDetail;
import org.apache.juddi.api_v3.SyncSubscriptionDetailResponse;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.apache.juddi.v3_service package. 
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

    private final static QName _SaveClientSubscriptionInfoResponse_QNAME = new QName("urn:juddi-apache-org:v3_service", "save_ClientSubscriptionInfoResponse");
    private final static QName _InvokeSyncSubscriptionResponse_QNAME = new QName("urn:juddi-apache-org:v3_service", "invoke_SyncSubscriptionResponse");
    private final static QName _SaveNodeResponse_QNAME = new QName("urn:juddi-apache-org:v3_service", "save_NodeResponse");
    private final static QName _SavePublisherResponse_QNAME = new QName("urn:juddi-apache-org:v3_service", "save_publisherResponse");
    private final static QName _SaveClerkResponse_QNAME = new QName("urn:juddi-apache-org:v3_service", "save_ClerkResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.apache.juddi.v3_service
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ClientSubscriptionInfoDetail }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:v3_service", name = "save_ClientSubscriptionInfoResponse")
    public JAXBElement<ClientSubscriptionInfoDetail> createSaveClientSubscriptionInfoResponse(ClientSubscriptionInfoDetail value) {
        return new JAXBElement<ClientSubscriptionInfoDetail>(_SaveClientSubscriptionInfoResponse_QNAME, ClientSubscriptionInfoDetail.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SyncSubscriptionDetailResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:v3_service", name = "invoke_SyncSubscriptionResponse")
    public JAXBElement<SyncSubscriptionDetailResponse> createInvokeSyncSubscriptionResponse(SyncSubscriptionDetailResponse value) {
        return new JAXBElement<SyncSubscriptionDetailResponse>(_InvokeSyncSubscriptionResponse_QNAME, SyncSubscriptionDetailResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NodeDetail }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:v3_service", name = "save_NodeResponse")
    public JAXBElement<NodeDetail> createSaveNodeResponse(NodeDetail value) {
        return new JAXBElement<NodeDetail>(_SaveNodeResponse_QNAME, NodeDetail.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PublisherDetail }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:v3_service", name = "save_publisherResponse")
    public JAXBElement<PublisherDetail> createSavePublisherResponse(PublisherDetail value) {
        return new JAXBElement<PublisherDetail>(_SavePublisherResponse_QNAME, PublisherDetail.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ClerkDetail }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:juddi-apache-org:v3_service", name = "save_ClerkResponse")
    public JAXBElement<ClerkDetail> createSaveClerkResponse(ClerkDetail value) {
        return new JAXBElement<ClerkDetail>(_SaveClerkResponse_QNAME, ClerkDetail.class, null, value);
    }

}
