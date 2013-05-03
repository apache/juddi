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
package org.uddi.v3_service;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import org.uddi.custody_v3.DiscardTransferToken;
import org.uddi.custody_v3.KeyBag;
import org.uddi.custody_v3.TransferEntities;

/**
 * This portType defines all of the UDDI custody transfer operations. This
 * section defines the UDDI Custody and Ownership Transfer API Set[28]. Data
 * custody is introduced in Section 1.5.6 Data Custody. Ownership transfer is
 * introduced in Section 1.5.7 Transfer of Ownership. By virtue of having
 * created an entity, a publisher has ownership of the entity and is said to be
 * the owner of the entity. A custodial node MUST maintain a relationship of
 * ownership between an entity and its publisher by means of authorization
 * mechanisms. Every node of a multi-node registry MUST guarantee the integrity
 * of an entity's custody. As such, a node MUST not permit changes to an entity
 * unless it has custody of it.
 *
 * The Custody and Ownership Transfer API Set enables any nodes of a registry to
 * cooperatively transfer custody of one or more businessEntity or tModel
 * structures from one node to another, as well as allowing the transfer of
 * ownership of these structures from one publisher to another. Associated
 * entities of a businessEntity such as its businessService, bindingTemplate,
 * and publisherAssertion structures are transferred as part of the custody
 * transfer of the business entity. * From a custody transfer point of view, the
 * publishers are always distinct, though it may be the case that the publishers
 * are the same person. Also, the two nodes may or may not be distinct;
 * intra-node transfer between two publishers is simply a degenerate case in
 * which node custody does not change. Thus, in the case of an inter-node
 * transfer, ownership transfer is implied. In the case of an intra-node
 * transfer the behavior results in the transfer of ownership between two
 * publishers.
 *
 * For example, one UDDI registry, UDDI-1, MAY allow each node in UDDI-1
 * (composed of nodes 1A, 1B and 1C) to define its own policies for
 * registration, authentication and authorization. In this case, a "person",
 * (P1) would need to review the policies of all 3 nodes and decide upon the
 * node with which it chooses to register with. P1 may choose to register with
 * more than one node. P1 registers with node1A . Node1A also specifies how P1
 * is authenticated. If P1 successfully authenticates and publishes a business
 * entity (BE1) then P1 becomes the "owner" of BE1. Node1A is said to be the
 * "custodian" of BE1. P1 can also register at node1B. If P1 successfully
 * authenticates and publishes a business entity (BE2) then P1 becomes the
 * "owner" of BE2. Node1B is said to be the "custodian" of BE2. There is no
 * assumption that the registry UDDI-1 or its nodes (node1A and node1B) are
 * aware that P1 is the same "person". P1 is responsible for maintaining the
 * appropriate identity and authenticating correctly to each node within a
 * registry.
 *
 * Another UDDI registry, UDDI-2, MAY require each of its nodes (node2-1,
 * node2-2 and node2-3) to use the same registration, authentication and
 * authorization mechanisms. In this case, the policies are the same across all
 * nodes. The relationship of registration, publication and ownership remains
 * the same. If P1 wants to register with different nodes in UDDI-2, then it
 * needs to differentiate its registration with the different nodes, since an
 * attempt to register at node2-2 after registering at node2-1, would fail as
 * "already registered" (since by policy the nodes all share the same
 * registration, authentication and authorization).
 *
 * 5.4.1 Overview There are a number of scenarios where a publisher may choose
 * to transfer custodianship or ownership of one or more entities. These are
 * described in this section.
 *
 * 5.4.1.1 Intra-Node Ownership Transfer Intra-node ownership transfer involves
 * transferring entity ownership from one publisher to another within the same
 * UDDI node. Usage scenarios for this type of transfer include the following:
 *
 * · Businesses or organizational merges: Multiple organizations need to be
 * consolidated under the control of a single publisher.
 *
 * · Domain key generators: One use of ownership transfer is the transfer of
 * ownership of a derived key generator from one publisher to another to enable
 * her or him to publish entities using keys in that domain.
 *
 * The save_xx APIs can also be used to move entities between parent entities
 * that are owned by the same publisher. The save_service API, for example, can
 * be used to move services (and binding templates) between one business entity
 * and another as described in Section 5.2.17.3 Behavior of the save_service
 * API. Changing the parent relationship in this way causes two businessEntity
 * structures to be changed. Doing so enables the following scenarios:
 *
 * · Divestitures: An organization needs to reassign the control of a set of
 * services to two or more publishers.
 *
 * · Consolidation of registry entities: There are multiple entities for a given
 * business that are to be consolidated under a single publisher.
 *
 * 5.4.1.2 Inter-Node Custody Transfer Inter-node custody transfer involves the
 * custody transfer of a set of entities across nodes of a UDDI registry. A
 * transfer of ownership ensues as a consequence of this custody transfer. In
 * addition to the intra-node scenarios described above, inter-node custody
 * transfer may be used to address the following use cases:
 *
 * · Unsatisfactory service level: The functionality or service level provided
 * by a given node operator is insufficient, and the publisher wishes to move
 * their UDDI data to another node.
 *
 * · Change in availability for a UDDI node: A node is no longer providing UDDI
 * services, and all publishers need to be migrated to one or more nodes of the
 * registry.
 *
 * · Organizational Mergers, Divestitures or Consolidations: Changes in
 * organizational structure may result in the need to make changes to the set of
 * publishers used to manage the entities at various nodes of a registry.
 *
 * For any of these intra and inter-node scenarios, a mechanism is specified to
 * facilitate the transfer the custody of businessEntity and tModel entities
 * between nodes whether the entity is being transferred within a single node or
 * whether a custody transfer occurs between nodes of a registry.
 *
 * 5.4.2 Custody Transfer Considerations When a businessEntity is transferred,
 * all related businessService and bindingTemplate elements are transferred as
 * well. In addition, any publisherAssertion elements that reference the
 * businessEntity element’s businessKey that are owned by the publisher are also
 * transferred.
 *
 * Note that the relinquishing publisher is not required to transfer all of its
 * UDDI entities (i.e. businessEntity and/or tModel entities) in a single
 * custody transfer request, nor is it required to transfer all of its entities
 * to the same target publisher or target node. Any combination or subset of
 * UDDI registry entities may be transferred to any number of target publishers
 * or nodes.
 *
 * 5.4.3 Transfer Execution The Custody and Ownership Transfer API Set enables
 * two publishers P1 and P2 and two nodes, N1 and N2, in a registry to
 * cooperatively transfer custody of one or more existing businessEntity or
 * tModel structures, E1…En, from N1 to N2 and, and by extension to transfer
 * ownership of the entities from P1 to P2. Related businessService,
 * bindingTemplate, and publisherAssertion structures are transferred with their
 * related businessEntities. From the registry’s point of view, the publishers
 * are always distinct, though it may be the case that P1 and P2 are the same
 * party. The two nodes may or may not be distinct; intra-node transfer of
 * ownership from P1 to P2 is simply a degenerate case in which node custody
 * does not change.
 *
 * The Custody and Ownership Transfer API Set is divided into two parts, a set
 * of two client APIs and a single inter-node API. These client APIs are
 * get_transferToken and transfer_entities; in short, this constitutes the
 * Ownership Transfer API portion of this API set. The inter-node-API is
 * transfer_ custody which when combined with replication makes up the Custody
 * Transfer API portion of this API set. * The overall flow of custody and
 * ownership transfer is as follows:
 *
 * Publisher P1 invokes get_transferToken on N1, specifying the keys K1…Kn of
 * the entities E1…En that are to be transferred. If P1 is authorized to do this
 * (i.e., if P1 has ownership of E1…En), N1 returns a structure T, called a
 * transfer token, that represents authority to transfer the entities, including
 * all of the naturally contained children and publisher assertions related to
 * business entities involved in the transfer that are owned by P1. The
 * transferToken is a structure that consists of an opaque string that is
 * meaningful only to the node that issued it, an expiration time, and a node
 * identifier.
 *
 * P1 then gives T to P2 (typically by some secure means since T is valuable).
 * The publisher obtaining the custody information needs to have previously
 * obtained a publishers account on the node accepting custody of the entity
 * before he/she can complete the custody transfer. P2 then invokes
 * transfer_entities on N2, passing K1…Kn and T. If transfer_entities completes
 * successfully, the entities E1…En and their related structures
 * (businessService, bindingTemplate, and publisherAssertion) are in the custody
 * of N2 and are owned by P2. If the operation fails, nothing happens to the
 * entities. The actual transfer proceeds as follows, in the processing of
 * transfer_entities.
 *
 * If N1 and N2 are not distinct nodes, the ownership transfer from P1 to P2 is
 * an operation that is purely internal to the node – how it happens is up to
 * the implementation. If N1 and N2 are distinct, the following protocol occurs
 * while processing the transfer_entities request on N2.
 *
 * Upon receipt of a transfer_entities request, N2 checks that K1…Kn are valid
 * keys. There is the possibility that P1 might transfer more data than P2 can
 * accept due to policy-based restrictions on the limit of entities allowed to
 * be owned by P2 at N2. As is described below, replication is used to complete
 * the custody transfer process. A question that arises is at the time of
 * accepting the datum related to the transfer, could N2 throw a replication
 * error because the data being transferred exceeds the limits of user P2? Such
 * limits can not be enforced during replication because they are node-local
 * policy decisions from the perspective of enforcement. Thus, it is therefore
 * possible that as a result of a custody transfer a publisher may be caused to
 * hold more data that he/she would have been able to publish. Should this
 * situation occur, P2 MUST not be allowed to publish any additional data unless
 * P2 first reduces the number of entries it owns to an allowable limit.
 *
 * If all is well, N2 invokes the inter-node API transfer_custody on N1,
 * presenting the keys of top-level entities to be transferred, K1…Kn, P2’s
 * identity (using the publisher’s authorizedName), N2’s node identifier (as
 * known in the Replication Configuration structure, see Section 7.5.2
 * Configuration of a UDDI Node – operator element), and T. The transferToken,
 * T, implies permission to transfer the entire content of the entities it
 * identifies, including all of the contained entities and related
 * publisherAssertions, if any. N1 checks to see whether T is a valid
 * transferToken that it issued and that T represents the authority to transfer
 * E1…En. If the validation is successful, N1 prevents further changes to
 * entities E1…En. N1 then updates the authorizedName and nodeID of the
 * operationalInfo of E1…En and related entities so that they are shown to be in
 * the custody of N2 and owned by P2. Finally, N1 responds to N2 which triggers
 * N2 to respond to the transfer_entities caller. This completes the processing
 * for the transfer_entities request.
 *
 * In the case that the datum being transferred is a key generator tModel, N1
 * will disallow further generation of keys associated with this key partition
 * at its node.
 *
 * Following the issue of the empty message by N1 to the transfer_custody call,
 * N1 will submit into the replication stream a changeRecordNewData providing in
 * the operationalInfo, N2’s nodeID identifying it as the node where the datum
 * is being transferred to, and the authorizedName of P2. The
 * acknowledgmentRequested attribute of this change record MUST be set to
 * "true".
 *
 * The last modified date timestamp in the operationalInfo must change to
 * reflect the custody transfer. Figure 2 depicts the flow of a custody transfer
 * between P1 and P2. This class was generated by the JAX-WS RI. JAX-WS RI
 * 2.1.5-b03- Generated source version: 2.1
 *
 */
@WebService(name = "UDDI_CustodyTransfer_PortType", targetNamespace = "urn:uddi-org:v3_service")
@XmlSeeAlso({
    org.uddi.custody_v3.ObjectFactory.class,
    org.uddi.repl_v3.ObjectFactory.class,
    org.uddi.subr_v3.ObjectFactory.class,
    org.uddi.api_v3.ObjectFactory.class,
    org.uddi.vscache_v3.ObjectFactory.class,
    org.uddi.vs_v3.ObjectFactory.class,
    org.uddi.sub_v3.ObjectFactory.class,
    org.w3._2000._09.xmldsig_.ObjectFactory.class,
    org.uddi.policy_v3.ObjectFactory.class,
    org.uddi.policy_v3_instanceparms.ObjectFactory.class
})
public interface UDDICustodyTransferPortType extends Remote {

    /**
     * The discard_transferToken API is a client API used to discard a
     * transferToken obtained through the get_transferToken API at the same
     * node. This API accepts either a transferToken or a keyBag as parameters
     * to remove the permission to transfer data associated with a particular
     * transferToken. If a keyBag is provided, all tokens corresponding to the
     * keys in the keyBag will be discarded and will no longer be valid for
     * custody or ownership transfer after the discard_transferToken is
     * processed, irrespective of whether the keys match any known business or
     * tmodelKey values. In the event that the keyBag represents a subset of the
     * keyBag for one or more transferToken elements, the transferToken is
     * discarded and will no longer be valid for transferring any entity. If the
     * token passed in the transferToken argument does not match an existing
     * token known to the system, no action is taken and success is reported.
     * Keys in the keyBag argument that do not have a corresponding token are
     * ignored.
     *
     * @param body <p class="MsoBodyText"
     * style="margin-left:1.0in;text-indent:-.25in"><span
     * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
     * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * </span></span><b><i>authInfo</i></b>: This OPTIONAL argument is an
     * element that contains an authentication token.&nbsp; Authentication
     * tokens are obtained using the get_authToken API call or through some
     * other means external to this specification, and represent the identity of
     * the publisher at a UDDI node.</p>
     *
     * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
     * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
     * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * </span></span><b><i>transferToken</i></b>: This is a known transferToken
     * obtained by a publisher at the node where the get_transferToken API was
     * invoked.</p>
     *
     * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
     * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
     * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * </span></span><b><i>keyBag</i></b>: One or more uddiKeys associated
     * either with businessEntity or tModel entities owned by the publisher that
     * were to be transferred to some other publisher and/or node in the
     * registry as the result of invocation of get_transferToken.&nbsp; At least
     * one businessKey or tModelKey must be provided in a keyBag.</p>
     * @return Upon successful completion, an empty message is returned. See
     * section 4.8 Success and Error Reporting.
     *
     * No error will be reported if the transferToken provided in the call does
     * not match an existing token. No error will be reported if a token is not
     * found for a particular key in the keyBag.
     * @throws DispositionReportFaultMessage
     * @throws RemoteException <p class="MsoBodyText">If an error occurs in
     * processing this API call, a dispositionReport structure MUST be returned
     * to the caller in a SOAP Fault. See Section <a href="#_Ref8979716
     * ">4.8</a> <i>Success and Error Reporting.&nbsp; </i>In addition to the
     * errors common to all APIs, the following error information is relevant
     * here: </p>
     *
     * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
     * style="font-family:Symbol">·</span><span
     * style="font-size:7.0pt;font-family: &quot;Times New
     * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * </span><b>E_invalidKeyPassed</b>: signifies that one of the
     * <i>uddiKey</i> values passed for entities to be transferred did not match
     * with any known businessKey or tModelKey values.&nbsp; The key and element
     * or attribute that caused the problem SHOULD be clearly indicated in the
     * error text.</p>
     */
    @WebMethod(operationName = "discard_transferToken", action = "discard_transferToken")
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    public void discardTransferToken(
            @WebParam(name = "discard_transferToken", targetNamespace = "urn:uddi-org:custody_v3", partName = "body") DiscardTransferToken body)
            throws DispositionReportFaultMessage, RemoteException;

    /**
     * The get_transferToken API is a client API used to initiate the transfer
     * of custody of one or more businessEntity or tModel entities from one node
     * to another. As previously stated, the two nodes may or may not be
     * distinct; intra-node transfer between two publishers is simply a
     * degenerate case in which node custody does not change. No actual transfer
     * takes place with the invocation of this API. Instead, this API obtains
     * permission from the custodial node, in the form of a transferToken, to
     * perform the transfer. The publisher who will be recipient of the
     * transferToken returned by this API must invoke the transfer_entities API
     * on the target custodial node to actually transfer the entities.
     *
     * @param authInfo · authInfo: This OPTIONAL argument is an element that
     * contains an authentication token. Authentication tokens are obtained
     * using the get_authToken API call or through some other means external to
     * this specification and represent the identity of the publisher at a UDDI
     * node.
     * @param keyBag keyBag: One or more key (of type uddi:uddiKey) associated
     * either with businessEntity or tModel entities owned by the publisher that
     * are to be transferred to some other publisher and/or node in the
     * registry. At least one businessKey or tModelKey must be provided.
     * @param nodeID this is a return value. The transfer token consists of a
     * nodeID, an expirationTime and an opaqueToken. The nodeID is used during
     * the transfer_entities API by the recipient node to confirm with the
     * relinquishing custodial node that the custody transfer is authorized and
     * still valid. The nodeID of the transferToken is the value of the nodeID
     * element of the Replication Configuration Structure. Refer to Section
     * 7.5.2 Configuration of a UDDI Node – operator Element.
     * @param expirationTime this is a return value. The expirationTime, defined
     * as xsd:dateTime, represents the time at which the transfer token is no
     * longer valid.
     * @param opaqueToken this is a return value. The opaqueToken is only
     * meaningful to the node that issues it. The opaqueToken is defined as
     * xsd:base64Binary to allow for a RECOMMENDED encryption of the token under
     * the relinquishing custody node’s own encryption key.
     * @throws DispositionReportFaultMessage
     * @throws RemoteException <p class="MsoBodyText">If an error occurs in
     * processing this API call, a dispositionReport structure MUST be returned
     * to the caller in a SOAP Fault. See section <a href="#_Ref8979716
     * ">4.8</a> <i>Success and Error Reporting.&nbsp; </i>In addition to the
     * errors common to all APIs, the following error information is relevant
     * here: </p>
     *
     * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
     * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
     * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * </span></span><b>E_invalidKeyPassed</b>: signifies that one of the
     * <i>uddiKey</i> values passed for entities to be transferred did not match
     * with any known businessKey or tModelKey values.&nbsp; The key and element
     * or attribute that caused the problem SHOULD be clearly indicated in the
     * error text.</p>
     *
     * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
     * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
     * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * </span></span><b>E_tokenAlreadyExists</b>: signifies that one or more of
     * the businessKey or tModelKey elements that identify entities to be
     * transferred are associated with a transferToken that is still valid and
     * has not been discarded, used or expired.&nbsp; The error text SHOULD
     * clearly indicate which entity keys caused the error.</p>
     *
     * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
     * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
     * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * </span></span><b>E_userMismatch</b>: signifies that one or more of the
     * businessKey or tModelKey elements that identify entities to be
     * transferred are not owned by the publisher identified by the
     * <i>authInfo</i> element.&nbsp; The error text SHOULD clearly indicate
     * which entity keys caused the error</p>
     */
    @WebMethod(operationName = "get_transferToken", action = "get_transferToken")
    @RequestWrapper(localName = "get_transferToken", targetNamespace = "urn:uddi-org:custody_v3", className = "org.uddi.custody_v3.GetTransferToken")
    @ResponseWrapper(localName = "transferToken", targetNamespace = "urn:uddi-org:custody_v3", className = "org.uddi.custody_v3.TransferToken")
    public void getTransferToken(
            @WebParam(name = "authInfo", targetNamespace = "urn:uddi-org:api_v3") String authInfo,
            @WebParam(name = "keyBag", targetNamespace = "urn:uddi-org:custody_v3") KeyBag keyBag,
            @WebParam(name = "nodeID", targetNamespace = "urn:uddi-org:api_v3", mode = WebParam.Mode.OUT) Holder<String> nodeID,
            @WebParam(name = "expirationTime", targetNamespace = "urn:uddi-org:custody_v3", mode = WebParam.Mode.OUT) Holder<XMLGregorianCalendar> expirationTime,
            @WebParam(name = "opaqueToken", targetNamespace = "urn:uddi-org:custody_v3", mode = WebParam.Mode.OUT) Holder<byte[]> opaqueToken)
            throws DispositionReportFaultMessage, RemoteException;

    /**
     * The transfer_entities API is used by publishers to whom custody is being
     * transferred to actually perform the transfer. The recipient publisher
     * must have an unexpired transferToken that was issued by the custodial
     * node for the entities being transferred.
     *
     * @param body authInfo: This OPTIONAL argument is an element that contains
     * an authentication token. Authentication tokens are obtained using the
     * get_authToken API call or through some other means external to this
     * specification, and represent the identity of the publisher at a UDDI
     * node, in this case, the new owner of the entities being transferred.
     *
     * · transferToken: Required argument obtained from the custodial node via a
     * call to get_transferToken by the publisher requesting a transfer of
     * custody. The transferToken contains an opaque token, an expiration date,
     * and the identity of the custodial node. The transferToken represents
     * permission to transfer the entities that have been identified via a prior
     * call to the get_transferToken API.
     *
     * · keyBag: One or more uddiKeys associated with businessEntity or tModel
     * entities that are to be transferred to this publisher at the target node
     * in the registry. The set of keys must be the same as the set of keys in
     * the keyBag of the get_transferToken API call from which the given
     * transferToken was once obtained.
     * @return <p class="MsoBodyText">The target node responds to this API by
     * performing the transfer operation.&nbsp; This operation is comprised of
     * four steps:</p>
     *
     * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
     * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
     * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * </span></span>Verification that the entity keys are valid.</p>
     *
     * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
     * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
     * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * </span></span>Verification that ownership of the entities by the
     * recipient publisher is allowed and would not violate any policies at the
     * target node related to publisher limits.</p>
     *
     * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
     * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
     * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * </span></span>Verification with the custodial node that the transfer of
     * the designated entities is allowed.&nbsp; This is accomplished by
     * invoking transfer_custody on the custodial node that is identified by the
     * nodeID element in the transferToken.&nbsp; Any errors returned by the
     * custodial node cause this API to fail and are propagated to the
     * caller.</p>
     *
     * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
     * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
     * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * </span></span>Changing custody and ownership of the designated entities
     * and entering these changes into the replication stream.</p>
     *
     * <p class="MsoBodyText">Upon successful completion, an empty message is
     * returned indicating the success of the transfer operation. In the case of
     * an inter-node custody transfer, while the transfer is in process, the
     * entities being transferred are not available for modification. To
     * determine the state of the data, UDDI clients can use the
     * get_operationalInfo API to determine when custody and ownership transfer
     * has taken place. A change in the nodeID of the operationalInfo provides
     * such an indication.</p>
     * @throws DispositionReportFaultMessage, RemoteException <p
     * class="MsoBodyText">If an error occurs in processing this API call, a
     * dispositionReport structure MUST be returned to the caller in a SOAP
     * Fault. See Section <a href="#_Ref8979732 ">4.8</a> <i>Success and Error
     * Reporting.&nbsp; </i>In addition to the errors common to all APIs, the
     * following error information is relevant here:</p>
     *
     * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
     * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
     * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * </span></span><b>E_accountLimitExceeded</b>: signifies that the target
     * node has determined that the transfer of custody of the identified
     * entities would result in the target publisher exceeding policy limits for
     * the number of owned entities.&nbsp; The error text SHOULD clearly
     * indicate which entities cause the publishers limits to be exceeded. It is
     * possible for a publisher to come into possession of more data than the
     * target node’s policy allows. The condition and node behavior under these
     * circumstances are described in Section <a href="#_Ref11680087
     * ">5.4.3</a><i>Transfer Execution.</i></p>
     *
     * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
     * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
     * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * </span></span><b>E_invalidKeyPassed</b>: signifies that one of the
     * <i>uddiKey</i> values passed for entities to be transferred did not match
     * with any known businessKey or tModelKey values.&nbsp; The key and element
     * or attribute that caused the problem SHOULD be clearly indicated in the
     * error text.</p>
     *
     * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
     * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
     * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * </span></span><b>E_transferNotAllowed</b>: signifies that the transfer of
     * one or more entities has been rejected by the target node or the
     * custodial node.&nbsp; Reasons for rejection include expiration of the
     * transferToken, use of an invalid transferToken, and attempts to transfer
     * a set of entities that does not match the one represented by the
     * transferToken. The reason for rejecting the custody transfer SHOULD be
     * clearly indicated in the error text.</p>
     *
     * <span
     * style="font-size:10.0pt;font-family:Arial;letter-spacing:-.25pt"></span>
     */
    @WebMethod(operationName = "transfer_entities", action = "transfer_entities")
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    public void transferEntities(
            @WebParam(name = "transfer_entities", targetNamespace = "urn:uddi-org:custody_v3", partName = "body") TransferEntities body)
            throws DispositionReportFaultMessage, RemoteException;
}
