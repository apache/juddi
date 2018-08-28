/*
 * Copyright 2015 The Apache Software Foundation.
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
 */
package org.apache.juddi.v3.tck;

import java.math.BigInteger;
import java.util.List;
import javax.xml.bind.JAXB;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import org.junit.Assert;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.UDDIService;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.cryptor.TransportSecurityHelper;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.AddPublisherAssertions;
import org.uddi.api_v3.AssertionStatusItem;
import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.BusinessServices;
import org.uddi.api_v3.Contact;
import org.uddi.api_v3.DeleteBinding;
import org.uddi.api_v3.DeleteBusiness;
import org.uddi.api_v3.DeletePublisherAssertions;
import org.uddi.api_v3.DeleteService;
import org.uddi.api_v3.DeleteTModel;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.GetBindingDetail;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.GetOperationalInfo;
import org.uddi.api_v3.GetServiceDetail;
import org.uddi.api_v3.GetTModelDetail;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.OperationalInfos;
import org.uddi.api_v3.PersonName;
import org.uddi.api_v3.PublisherAssertion;
import org.uddi.api_v3.SaveBinding;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelDetail;
import org.uddi.custody_v3.KeyBag;
import org.uddi.custody_v3.TransferEntities;
import org.uddi.custody_v3.TransferToken;
import org.uddi.repl_v3.ChangeRecordIDType;
import org.uddi.repl_v3.CommunicationGraph;
import org.uddi.repl_v3.Operator;
import org.uddi.repl_v3.OperatorStatusType;
import org.uddi.repl_v3.ReplicationConfiguration;

import org.uddi.v3_service.UDDICustodyTransferPortType;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDIReplicationPortType;

import org.uddi.v3_service.UDDISecurityPortType;

/**
 * Contains all multinode tests, replication, custody transfer. This is juddi
 * specific since most of these function depend on a juddi specific api in order
 * to configure, since the uddi spec doesn't define a mechanism to do so.
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class JUDDI_300_MultiNodeIntegrationTest {

        private static final Log logger = LogFactory.getLog(JUDDI_300_MultiNodeIntegrationTest.class);
        private static UDDIClient manager;
        private static String rootNode1Token;
        private static String rootNode2Token;
        private static String uddiNode1Token;
        private static String uddiNode2Token;
        
        private static JUDDIApiPortType juddiApiServiceNode1;
        private static JUDDIApiPortType juddiApiServiceNode2;

        private static String maryTokenNode1;
        private static String samTokenNode2;
        private static UDDICustodyTransferPortType custodySam;
        private static UDDICustodyTransferPortType custodyMary;
        private static UDDIPublicationPortType publishMary;
        private static UDDIPublicationPortType publishSamNode2;
        private static UDDIPublicationPortType publishUddiNode1;
        private static UDDIPublicationPortType publishUddiNode2;
        private static UDDIInquiryPortType inquiryMary;
        private static UDDIInquiryPortType inquirySamNode2;
        
        static TckBusiness maryBizNode1;
        static TckTModel maryTModelNode1;
        static TckTModel uddiTmodelNode1;
        
        static TckBusinessService samServiceNode2;
        static TckBusiness samBizNode2;
        static TckTModel samTModelNode2;
        static TckTModel uddiTmodelNode2;

        static final String CFG_node1_MARY = "uddiv3";
        static final String CFG_node2_SAM = "uddiv3-2";

        @BeforeClass
        public static void startRegistry() throws Exception {
                if (!TckPublisher.isEnabled()) {
                        return;
                }
                if (!TckPublisher.isJUDDI()) {
                        return;
                }

                testSetupReplicationConfig();

        }

        static synchronized void init() throws Exception {
                if (manager != null) {
                        return;
                }
                manager = new UDDIClient();
                manager.start();

                Transport node1 = manager.getTransport(CFG_node1_MARY);
                juddiApiServiceNode1 = node1.getJUDDIApiService();
                custodyMary = node1.getUDDICustodyTransferService();
                inquiryMary = node1.getUDDIInquiryService();
                publishMary = node1.getUDDIPublishService();
               // UDDIService uddiService = new UDDIService();

                //replicationMary = uddiService.getUDDIReplicationPort();
                //((BindingProvider) replicationMary).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, manager.getClientConfig().getUDDINode(CFG_node1_MARY).getReplicationUrl());
                //TransportSecurityHelper.applyTransportSecurity((BindingProvider) replicationMary);
                //logger.info("Using replication endpoint Node1...@" + ((BindingProvider)replicationMary).getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY));
                UDDISecurityPortType secNode1 = node1.getUDDISecurityService();
                rootNode1Token = TckSecurity.getAuthToken(secNode1, TckPublisher.getRootPublisherId(), TckPublisher.getRootPassword());
                maryTokenNode1 = TckSecurity.getAuthToken(secNode1, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());
                uddiNode1Token = TckSecurity.getAuthToken(secNode1, TckPublisher.getUDDIPublisherId(), TckPublisher.getUDDIPassword());

                Transport node2 = manager.getTransport(CFG_node2_SAM);
                UDDISecurityPortType secNode2 = node2.getUDDISecurityService();
                rootNode2Token = TckSecurity.getAuthToken(secNode2, TckPublisher.getRootPublisherId(), TckPublisher.getRootPassword());
                samTokenNode2 = TckSecurity.getAuthToken(secNode2, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                uddiNode2Token = TckSecurity.getAuthToken(secNode2, TckPublisher.getUDDIPublisherId(), TckPublisher.getUDDIPassword());
                //replicationSam = uddiService.getUDDIReplicationPort();
                //((BindingProvider) replicationSam).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, manager.getClientConfig().getUDDINode(CFG_node2_SAM).getReplicationUrl());
                //TransportSecurityHelper.applyTransportSecurity((BindingProvider) replicationSam);
                //logger.info("Using replication endpoint Node2...@" + ((BindingProvider)replicationSam).getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY));

                juddiApiServiceNode2 = node2.getJUDDIApiService();
                custodySam = node2.getUDDICustodyTransferService();
                inquirySamNode2 = node2.getUDDIInquiryService();
                publishSamNode2 = node2.getUDDIPublishService();
                
                Transport node2Uddi = manager.getTransport(CFG_node2_SAM);
                publishUddiNode2 = node2Uddi.getUDDIPublishService();
                
                Transport node1Uddi = manager.getTransport(CFG_node1_MARY);
                publishUddiNode1 = node1Uddi.getUDDIPublishService();
                
                
                samServiceNode2 = new TckBusinessService(publishSamNode2, inquirySamNode2);

                samBizNode2 = new TckBusiness(publishSamNode2, inquirySamNode2);
                samTModelNode2 = new TckTModel(publishSamNode2, inquirySamNode2);
                maryBizNode1 = new TckBusiness(publishMary, inquiryMary);
                maryTModelNode1 = new TckTModel(publishMary, inquiryMary);
                uddiTmodelNode1 = new TckTModel(publishUddiNode1,node1Uddi.getUDDIInquiryService() );
                uddiTmodelNode2= new TckTModel(publishUddiNode2,node2Uddi.getUDDIInquiryService() );

                if (!TckPublisher.isUDDIAuthMode()) {
                        TckSecurity.setCredentials((BindingProvider) publishUddiNode2, TckPublisher.getUDDIPublisherId(), TckPublisher.getUDDIPassword());
                        TckSecurity.setCredentials((BindingProvider) publishUddiNode1, TckPublisher.getUDDIPublisherId(), TckPublisher.getUDDIPassword());
                        
                        TckSecurity.setCredentials((BindingProvider) juddiApiServiceNode1, TckPublisher.getRootPublisherId(), TckPublisher.getRootPassword());
                        TckSecurity.setCredentials((BindingProvider) juddiApiServiceNode2, TckPublisher.getRootPublisherId(), TckPublisher.getRootPassword());
                        TckSecurity.setCredentials((BindingProvider) custodyMary, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());
                        TckSecurity.setCredentials((BindingProvider) inquiryMary, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());
                        TckSecurity.setCredentials((BindingProvider) publishMary, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());
                        //TckSecurity.setCredentials((BindingProvider) replicationMary, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());

                        TckSecurity.setCredentials((BindingProvider) custodySam, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                        TckSecurity.setCredentials((BindingProvider) inquirySamNode2, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                        //TckSecurity.setCredentials((BindingProvider) replicationSam, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                        TckSecurity.setCredentials((BindingProvider) publishSamNode2, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());

                }
        }

        static void refreshTokens() throws Exception {
                manager.start();
                Transport node1 = manager.getTransport(CFG_node1_MARY);

                UDDISecurityPortType secNode1 = node1.getUDDISecurityService();
                rootNode1Token = TckSecurity.getAuthToken(secNode1, TckPublisher.getRootPublisherId(), TckPublisher.getRootPassword());
                maryTokenNode1 = TckSecurity.getAuthToken(secNode1, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());

                Transport node2 = manager.getTransport(CFG_node2_SAM);
                UDDISecurityPortType secNode2 = node2.getUDDISecurityService();
                rootNode2Token = TckSecurity.getAuthToken(secNode2, TckPublisher.getRootPublisherId(), TckPublisher.getRootPassword());
                samTokenNode2 = TckSecurity.getAuthToken(secNode2, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());

        }

        @AfterClass
        public static void stopRegistry() throws ConfigurationException {
                if (!TckPublisher.isEnabled()) {
                        return;
                }
                if (!TckPublisher.isJUDDI()) {
                        return;
                }
                if (manager != null) {
                        manager.stop();
                }
        }

        private static boolean Contains(List<Operator> operator, Operator op) {
                for (Operator o : operator) {
                        if (o.getOperatorNodeID().equals(op.getOperatorNodeID())) {
                                return true;
                        }
                }
                return false;
        }

        @Test
        public void santityTest() {

        }

        public static void testSetupReplicationConfig() {
                //this only runs if it is JUDDI and Replication is enabled
                if (!TckPublisher.isReplicationEnabled() || !TckPublisher.isJUDDI()) {
                        logger.info("TCK says that replication is disabled...skipping replication config...");
                        return;
                }
                try {
                        init();
                        refreshTokens();
                        logger.info("fetching current replication config...");

                        ReplicationConfiguration replicationNode1 = null;
                        try {
                                replicationNode1 = juddiApiServiceNode1.getReplicationNodes(rootNode1Token);
                        } catch (Exception ex) {
                                logger.error("Error getting replication config",ex);
                                Assert.fail(ex.getMessage());

                        }
                        //if (replicationNode1.getCommunicationGraph() == null) {
                        replicationNode1.setCommunicationGraph(new CommunicationGraph());
                        //}

                        replicationNode1.getOperator().clear();
                        replicationNode1.getSignature().clear();

                        Operator op = new Operator();
                        op.setOperatorNodeID("uddi:juddi.apache.org:node1");
                        op.setSoapReplicationURL(manager.getClientConfig().getUDDINode(CFG_node1_MARY).getReplicationUrl());
                        op.setOperatorStatus(OperatorStatusType.NORMAL);
                        op.getContact().add(new Contact());
                        op.getContact().get(0).getPersonName().add(new PersonName("bob", "en"));
                        op.getContact().get(0).setUseType("admin");
                        //if (!Contains(replicationNode1.getOperator(), op)) {
                                replicationNode1.getOperator().add(op);
                        //}

                        op = new Operator();
                        op.setOperatorNodeID("uddi:another.juddi.apache.org:node2");
                        op.setSoapReplicationURL(manager.getClientConfig().getUDDINode(CFG_node2_SAM).getReplicationUrl());
                        op.setOperatorStatus(OperatorStatusType.NORMAL);
                        op.getContact().add(new Contact());
                        op.getContact().get(0).getPersonName().add(new PersonName("mary", "en"));
                        op.getContact().get(0).setUseType("admin");
                        //if (!Contains(replicationNode1.getOperator(), op)) 
                        {
                                replicationNode1.getOperator().add(op);
                        }
                        //if (!replicationNode1.getCommunicationGraph().getNode().contains("uddi:another.juddi.apache.org:node2")) {
                                replicationNode1.getCommunicationGraph().getNode().add("uddi:another.juddi.apache.org:node2");
                        //}
                        //if (!replicationNode1.getCommunicationGraph().getNode().contains("uddi:juddi.apache.org:node1")) {
                                replicationNode1.getCommunicationGraph().getNode().add("uddi:juddi.apache.org:node1");
                        //}
                        replicationNode1.setSerialNumber(0L);
                        replicationNode1.setTimeOfConfigurationUpdate("");
                        replicationNode1.setMaximumTimeToGetChanges(BigInteger.ONE);
                        replicationNode1.setMaximumTimeToSyncRegistry(BigInteger.ONE);

                        if (replicationNode1.getRegistryContact().getContact() == null) {
                                replicationNode1.getRegistryContact().setContact(new Contact());
                                replicationNode1.getRegistryContact().getContact().getPersonName().add(new PersonName("unknown", null));
                        }

                        if (TckCommon.isDebug()) {
                                JAXB.marshal(replicationNode1, System.out);
                        }

                        logger.info(manager.getClientConfig().getConfigurationFile() + " Setting replication config on Node 1...@" + ((BindingProvider) juddiApiServiceNode1).getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY));
                        logger.info("Setting replication url on Node 1...@" + manager.getClientConfig().getUDDINode(CFG_node1_MARY).getReplicationUrl());
                        juddiApiServiceNode1.setReplicationNodes(rootNode1Token, replicationNode1);
                        logger.info("Setting replication config on Node 2...@" + ((BindingProvider) juddiApiServiceNode2).getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY));
                        logger.info("Setting replication url on Node 2...@" + manager.getClientConfig().getUDDINode(CFG_node2_SAM).getReplicationUrl());
                        juddiApiServiceNode2.setReplicationNodes(rootNode2Token, replicationNode1);

                } catch (Exception ex) {
                        TckCommon.PrintMarker();
                        ex.printStackTrace();
                        TckCommon.PrintMarker();
                }
        }

        /**
         * transfer business from mary/node1 to sam/node2, then delete
         *
         * @throws Exception
         */
        @Test
        //@Ignore
        public void testMultiNodeBusinessCustodyTransfer() throws Exception {
                logger.info("testMultiNodeBusinessCustodyTransfer");
                try {
                        Assume.assumeTrue(TckPublisher.isReplicationEnabled() && TckPublisher.isCustodyTransferEnabled());
                        Assume.assumeTrue(TckPublisher.isJUDDI());
                        refreshTokens();
                        testSetupReplicationConfig();

                        getReplicationStatus();

                        //create mary's business, node1
                        BusinessEntity mary = new BusinessEntity();
                        mary.getName().add(new Name("Mary's biz on " + CFG_node1_MARY, null));
                        mary.setBusinessServices(new BusinessServices());
                        BusinessService bs = new BusinessService();
                        bs.getName().add(new Name("Mary's service", null));
                        bs.setBindingTemplates(new BindingTemplates());
                        BindingTemplate bt = new BindingTemplate();
                        bt.setAccessPoint(new AccessPoint("http://localhost/mary", "wsdlDeployment"));
                        bs.getBindingTemplates().getBindingTemplate().add(bt);
                        mary.getBusinessServices().getBusinessService().add(bs);

                        SaveBusiness sb = new SaveBusiness();
                        sb.setAuthInfo(maryTokenNode1);
                        sb.getBusinessEntity().add(mary);
                        BusinessDetail saveBusiness = publishMary.saveBusiness(sb);

                        Thread.sleep(5000); //sleep a bit and wait for replication to do it's thing

                        GetOperationalInfo operationalInfo = new GetOperationalInfo();
                        operationalInfo.setAuthInfo(maryTokenNode1);
                        operationalInfo.getEntityKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        OperationalInfos beforeNode1 = inquiryMary.getOperationalInfo(operationalInfo);

                        operationalInfo.setAuthInfo(samTokenNode2);
                        OperationalInfos beforeNode2 = null;
                        int timeout = TckPublisher.getSubscriptionTimeout();
                        while (timeout > 0) {
                                logger.info("Waiting for the update...");
                                try {
                                        GetBusinessDetail gbd = new GetBusinessDetail();
                                        gbd.setAuthInfo(samTokenNode2);
                                        gbd.getBusinessKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());

                                        BusinessDetail businessDetail = inquirySamNode2.getBusinessDetail(gbd);
                                        if (businessDetail != null
                                                && !businessDetail.getBusinessEntity().isEmpty()) {
                                                logger.info("Mary's business exists on Sams node, grabbing the operational info to confirm ownership...");
                                        }
                                        beforeNode2 = inquirySamNode2.getOperationalInfo(operationalInfo);
                                        break;
                                } catch (Exception ex) {
                                        logger.warn(ex.getMessage());
                                }
                                timeout--;
                                Thread.sleep(1000);

                        }
                        Assert.assertNotNull(beforeNode1);
                        Assert.assertNotNull(beforeNode2);

                        JAXB.marshal(beforeNode1, System.out);
                        JAXB.marshal(beforeNode2, System.out);
                        //confirm we're replicated correctly
                        Assert.assertEquals(beforeNode1.getOperationalInfo().get(0).getAuthorizedName(), beforeNode2.getOperationalInfo().get(0).getAuthorizedName());
                        Assert.assertEquals(beforeNode1.getOperationalInfo().get(0).getEntityKey(), beforeNode2.getOperationalInfo().get(0).getEntityKey());
                        Assert.assertEquals(beforeNode1.getOperationalInfo().get(0).getEntityKey(), saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        Assert.assertEquals(beforeNode1.getOperationalInfo().get(0).getNodeID(), beforeNode2.getOperationalInfo().get(0).getNodeID());

                        //get a transfer token, node1
                        KeyBag kb = new KeyBag();
                        kb.getKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        Holder<String> sourceNode = new Holder<String>();
                        Holder<XMLGregorianCalendar> expiration = new Holder<XMLGregorianCalendar>();
                        Holder<byte[]> token = new Holder<byte[]>();
                        custodyMary.getTransferToken(maryTokenNode1, kb, sourceNode, expiration, token);

                        //sam accepts the transfer token, node 2
                        TransferEntities transferEntities = new TransferEntities();
                        transferEntities.setAuthInfo(samTokenNode2);
                        transferEntities.setKeyBag(kb);
                        transferEntities.setTransferToken(new TransferToken());
                        transferEntities.getTransferToken().setExpirationTime(expiration.value);
                        transferEntities.getTransferToken().setNodeID(sourceNode.value);
                        transferEntities.getTransferToken().setOpaqueToken(token.value);
                        custodySam.transferEntities(transferEntities);
                        //stuff happens
                        //Thread.sleep(5000);
                        //confirm the transfer
                        timeout = TckPublisher.getSubscriptionTimeout();
                        OperationalInfos afterNode1 = null;
                        OperationalInfos afterNode2 = null;
                        while (timeout > 0) {
                                logger.info("Waiting for the update...");
                                try {
                                        operationalInfo.setAuthInfo(maryTokenNode1);
                                        //operationalInfo.getEntityKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                                        afterNode1 = inquiryMary.getOperationalInfo(operationalInfo);
                                        if (afterNode1.getOperationalInfo().get(0).getAuthorizedName().equals(TckPublisher.getSamPublisherId())) {
                                                logger.info("Mary's biz on node 1 is now owned by Sam");
                                                //node 1 is up to date
                                                operationalInfo.setAuthInfo(samTokenNode2);
                                                afterNode2 = inquirySamNode2.getOperationalInfo(operationalInfo);
                                                if (afterNode2.getOperationalInfo().get(0).getAuthorizedName().equals(TckPublisher.getSamPublisherId())) {
                                                        //node 2 is up to date
                                                        logger.info("Mary's biz on node 2 is now owned by Sam");
                                                        break;
                                                } else {
                                                        logger.info("Mary's biz on node 2 is still owned by Mary");
                                                }
                                        } else {
                                                logger.info("Mary's biz on node 1 is still owned by Mary");
                                        }
                                } catch (Exception ex) {
                                        logger.warn(ex.getMessage());
                                }
                                timeout--;
                                Thread.sleep(1000);

                        }
                        //operationalInfo.setAuthInfo(maryTokenNode1);
                        // operationalInfo.getEntityKey().add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());

                        Assert.assertNotNull(afterNode1);
                        Assert.assertNotNull(afterNode2);
                        if (TckCommon.isDebug()) {
                                JAXB.marshal(afterNode1, System.out);
                                JAXB.marshal(afterNode2, System.out);

                        }
                        //confirm we're replicated correctly
                        Assert.assertEquals(afterNode1.getOperationalInfo().get(0).getAuthorizedName(), afterNode2.getOperationalInfo().get(0).getAuthorizedName());
                        Assert.assertEquals(afterNode1.getOperationalInfo().get(0).getEntityKey(), afterNode2.getOperationalInfo().get(0).getEntityKey());
                        Assert.assertEquals(afterNode1.getOperationalInfo().get(0).getEntityKey(), saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        Assert.assertEquals(afterNode1.getOperationalInfo().get(0).getNodeID(), afterNode2.getOperationalInfo().get(0).getNodeID());
                        //confirm that the entity now belongs to sam

                        Assert.assertEquals(afterNode1.getOperationalInfo().get(0).getAuthorizedName(), TckPublisher.getSamPublisherId());
                        Assert.assertNotEquals(beforeNode1.getOperationalInfo().get(0).getNodeID(), afterNode1.getOperationalInfo().get(0).getNodeID());

                } finally {
                        resetTmodels();
                        resetBusinesses();
                }
        }

        /**
         * covers business, tmodels and publisher assertions
         *
         * @throws Exception
         */
        @Test
        //@Ignore
        public void testReplicationTModelBusinessPublisherAssertionAddDelete() throws Exception {
                Assume.assumeTrue(TckPublisher.isReplicationEnabled());
                Assume.assumeTrue(TckPublisher.isJUDDI());
                try {
                        //TckCommon.PrintMarker();
                        logger.info("testReplicationTModelBusinessPublisherAssertionAddDelete");

                        resetTmodels();
                        resetBusinesses();

                        TModel saveMaryPublisherTmodel = maryTModelNode1.saveMaryPublisherTmodel(maryTokenNode1);
                        samTModelNode2.saveSamSyndicatorTmodel(samTokenNode2);

                        BusinessEntity saveMaryPublisherBusiness = maryBizNode1.saveMaryPublisherBusiness(maryTokenNode1);

                        uddiTmodelNode2.saveTModel(uddiNode2Token, TckTModel.TMODEL_PUBLISHER_TMODEL_XML_PARENT, TckTModel.TMODEL_PUBLISHER_TMODEL_KEY_ROOT);
                        try{
                                uddiTmodelNode2.saveUDDIPublisherTmodel(uddiNode2Token);
                                uddiTmodelNode2.saveTmodels(uddiNode2Token);
                        }catch (Throwable t) {
                            //already exists
                        }
                       
                        // TModel saveSamSyndicatorTmodel = samTModelNode2.saveSamSyndicatorTmodel(samTokenNode2);
                        BusinessEntity saveSamSyndicatorBusiness = samBizNode2.saveSamSyndicatorBusiness(samTokenNode2);

                        // getReplicationStatus();//block until synched
                        //confirm mary's tmodel is on the other node
                        GetTModelDetail findTModel = new GetTModelDetail();
                        findTModel.setAuthInfo(samTokenNode2);
                        findTModel.getTModelKey().add(TckTModel.MARY_PUBLISHER_TMODEL_KEY);
                        TModelDetail tModelDetail = null;

                        int timeout = TckPublisher.getSubscriptionTimeout();

                        while (timeout > 0) {
                                logger.info("Waiting for the update...");
                                try {
                                        tModelDetail = inquirySamNode2.getTModelDetail(findTModel);
                                        break;
                                } catch (Exception ex) {
                                        logger.warn(ex.getMessage());
                                        tModelDetail = null;

                                }
                                timeout--;
                                Thread.sleep(1000);

                        }

                        if (tModelDetail==null)
                            TckCommon.PrintMarker();
                        Assert.assertNotNull("node 2 never got mary's tmodel key generator", tModelDetail);
                        Assert.assertNotNull(tModelDetail.getTModel());
                        Assert.assertTrue(tModelDetail.getTModel().size() == 1);
                        Assert.assertTrue(tModelDetail.getTModel().get(0).getTModelKey().equals(TckTModel.MARY_PUBLISHER_TMODEL_KEY));

                        GetBusinessDetail gbd = new GetBusinessDetail();
                        gbd.setAuthInfo(samTokenNode2);
                        gbd.getBusinessKey().add(TckBusiness.MARY_BUSINESS_KEY);

                        //confirm mary's biz made it too
                        timeout = TckPublisher.getSubscriptionTimeout();
                        BusinessDetail businessDetail = null;
                        while (timeout > 0) {
                                logger.info("Waiting for the update...");
                                try {
                                        businessDetail = inquirySamNode2.getBusinessDetail(gbd);
                                        break;
                                } catch (Exception ex) {
                                        logger.warn(ex.getMessage());
                                        businessDetail = null;

                                }
                                timeout--;
                                Thread.sleep(1000);

                        }
                        logger.info("Business replicated");
                        Assert.assertNotNull(businessDetail);
                        Assert.assertNotNull(businessDetail.getBusinessEntity());
                        Assert.assertTrue(businessDetail.getBusinessEntity().get(0).getBusinessKey().equals(TckBusiness.MARY_BUSINESS_KEY));

                        logger.info(">>> Saving a new publisher assertion on node 2 (sam)");
                        //setup a publisher assertion
                        PublisherAssertion pa = new PublisherAssertion();
                        pa.setFromKey(TckBusiness.SAM_BUSINESS_KEY);
                        pa.setToKey(TckBusiness.MARY_BUSINESS_KEY);
                        pa.setKeyedReference(new KeyedReference(UDDIConstants.RELATIONSHIPS, "parent-child", "child"));
                        AddPublisherAssertions apa = new AddPublisherAssertions();
                        apa.setAuthInfo(samTokenNode2);
                        apa.getPublisherAssertion().add(pa);
                        publishSamNode2.addPublisherAssertions(apa);

                        logger.info("Confirming that the assertion is saved on node2 (sam, origin)");
                        List<AssertionStatusItem> assertionStatusReport = null;
                        boolean found = false;
                        assertionStatusReport = publishSamNode2.getAssertionStatusReport(samTokenNode2, null);
                        logger.info("Publisher assertions returned: " + assertionStatusReport.size());
                        for (int x = 0; x < assertionStatusReport.size(); x++) {
                                JAXB.marshal(assertionStatusReport.get(x), System.out);
                                if (assertionStatusReport.get(x).getFromKey().equalsIgnoreCase(TckBusiness.SAM_BUSINESS_KEY)
                                        && assertionStatusReport.get(x).getToKey().equalsIgnoreCase(TckBusiness.MARY_BUSINESS_KEY)
                                        && assertionStatusReport.get(x).getKeyedReference().getTModelKey().equalsIgnoreCase(UDDIConstants.RELATIONSHIPS)
                                        && assertionStatusReport.get(x).getKeyedReference().getKeyName().equalsIgnoreCase("parent-child")
                                        && assertionStatusReport.get(x).getKeyedReference().getKeyValue().equalsIgnoreCase("child")) {
                                        found = true;
                                        break;
                                }

                        }
                        Assert.assertTrue("Assertion not found on Sam's node (2)!!", found);
                        logger.info("Ok it's saved.");
                        //wait for synch
                        timeout = TckPublisher.getSubscriptionTimeout();
                        logger.info("confirming that the assertion made it to node 1");
                        found = false;
                        while (timeout > 0) {
                                logger.info("Waiting for the update...");
                                try {
                                        assertionStatusReport = publishMary.getAssertionStatusReport(maryTokenNode1, null);
                                        logger.info("Publisher assertions returned: " + assertionStatusReport.size());
                                        for (int x = 0; x < assertionStatusReport.size(); x++) {
                                                JAXB.marshal(assertionStatusReport.get(x), System.out);
                                                if (assertionStatusReport.get(x).getFromKey().equalsIgnoreCase(TckBusiness.SAM_BUSINESS_KEY)
                                                        && assertionStatusReport.get(x).getToKey().equalsIgnoreCase(TckBusiness.MARY_BUSINESS_KEY)
                                                        && assertionStatusReport.get(x).getKeyedReference().getTModelKey().equalsIgnoreCase(UDDIConstants.RELATIONSHIPS)
                                                        && assertionStatusReport.get(x).getKeyedReference().getKeyName().equalsIgnoreCase("parent-child")
                                                        && assertionStatusReport.get(x).getKeyedReference().getKeyValue().equalsIgnoreCase("child")) {
                                                        found = true;
                                                        break;
                                                }

                                        }
                                        if (found) {
                                                break;
                                        }
                                } catch (Exception ex) {
                                        logger.warn(ex.getMessage());
                                        Assert.fail("Unexpected failure " + ex.getMessage());

                                }
                                timeout--;
                                Thread.sleep(1000);

                        }
                        Assert.assertTrue("Assertion wasn't replicated", found);

                        logger.info("Publisher Assertion replicated...");
                        logger.info("confirming the pa is still on node 2 origin (sam)");
                        found = false;
                        assertionStatusReport = publishSamNode2.getAssertionStatusReport(samTokenNode2, null);
                        logger.info("Publisher assertions returned: " + assertionStatusReport.size());
                        for (int x = 0; x < assertionStatusReport.size(); x++) {
                                JAXB.marshal(assertionStatusReport.get(x), System.out);
                                if (assertionStatusReport.get(x).getFromKey().equalsIgnoreCase(TckBusiness.SAM_BUSINESS_KEY)
                                        && assertionStatusReport.get(x).getToKey().equalsIgnoreCase(TckBusiness.MARY_BUSINESS_KEY)
                                        && assertionStatusReport.get(x).getKeyedReference().getTModelKey().equalsIgnoreCase(UDDIConstants.RELATIONSHIPS)
                                        && assertionStatusReport.get(x).getKeyedReference().getKeyName().equalsIgnoreCase("parent-child")
                                        && assertionStatusReport.get(x).getKeyedReference().getKeyValue().equalsIgnoreCase("child")) {
                                        found = true;
                                        break;
                                }

                        }
                        Assert.assertTrue("The PA is not found on node 2(origin), very strange", found);

                        //delete the pa
                        DeletePublisherAssertions dpa = new DeletePublisherAssertions();
                        dpa.setAuthInfo(samTokenNode2);
                        dpa.getPublisherAssertion().add(pa);
                        String sam = TckCommon.DumpAllBusinesses(samTokenNode2, inquirySamNode2);
                        String mary = TckCommon.DumpAllBusinesses(maryTokenNode1, inquiryMary);
                        logger.info("Publisher Assertion deletion...");
                        try {
                                publishSamNode2.deletePublisherAssertions(dpa);
                        } catch (Exception ex) {
                                ex.printStackTrace();
                                logger.info("Sam's businesses " + sam);
                                logger.info("Mary's businesses " + mary);
                                Assert.fail("unable to delete the assertion on sam's node!");
                        }
                        //wait for synch
                        timeout = TckPublisher.getSubscriptionTimeout();

                        found = false;
                        while (timeout > 0) {
                                logger.info("Waiting for the update...");
                                try {
                                        assertionStatusReport = publishMary.getAssertionStatusReport(maryTokenNode1, null);
                                        found = false;
                                        for (int x = 0; x < assertionStatusReport.size(); x++) {
                                                JAXB.marshal(assertionStatusReport.get(x), System.out);
                                                if (assertionStatusReport.get(x).getFromKey().equalsIgnoreCase(TckBusiness.SAM_BUSINESS_KEY)
                                                        && assertionStatusReport.get(x).getToKey().equalsIgnoreCase(TckBusiness.MARY_BUSINESS_KEY)
                                                        && assertionStatusReport.get(x).getKeyedReference().getTModelKey().equalsIgnoreCase(UDDIConstants.RELATIONSHIPS)
                                                        && assertionStatusReport.get(x).getKeyedReference().getKeyName().equalsIgnoreCase("parent-child")
                                                        && assertionStatusReport.get(x).getKeyedReference().getKeyValue().equalsIgnoreCase("child")) //still there
                                                {
                                                        found = true;
                                                }

                                        }
                                        if (!found) {
                                                break;
                                        }
                                } catch (Exception ex) {
                                        logger.warn(ex.getMessage());
                                        Assert.fail("Unexpected failure " + ex.getMessage());

                                }
                                timeout--;
                                Thread.sleep(1000);

                        }
                        Assert.assertFalse("Assertion deletion wasn't replicated", found);
                        logger.info("Publisher assertion deletion replicated");
                        //clean up
                        maryBizNode1.deleteMaryPublisherBusiness(maryTokenNode1);
                        maryTModelNode1.deleteMaryPublisherTmodel(maryTokenNode1);

                        //delete both
                        timeout = TckPublisher.getSubscriptionTimeout();
                        businessDetail = null;
                        while (timeout > 0) {
                                logger.info("Waiting for the update...");
                                try {
                                        businessDetail = inquirySamNode2.getBusinessDetail(gbd);

                                } catch (Exception ex) {
                                        logger.warn(ex.getMessage());
                                        businessDetail = null;
                                        break;
                                }
                                timeout--;
                                Thread.sleep(1000);

                        }
                        //check node2 for delete biz, should be gone

                        if (businessDetail != null) {
                                Assert.fail(TckBusiness.MARY_BUSINESS_KEY + " wasn't deleted on node 2");
                        }
                        logger.info("Mary's business deletion was replicated");

                        tModelDetail = inquirySamNode2.getTModelDetail(findTModel);
                        while (timeout > 0) {
                                logger.info("Waiting for the update...");
                                try {

                                        tModelDetail = inquirySamNode2.getTModelDetail(findTModel);
                                        Assert.assertNotNull(tModelDetail);
                                        Assert.assertNotNull(tModelDetail.getTModel());
                                        Assert.assertNotNull(tModelDetail.getTModel().get(0));
                                        Assert.assertEquals(tModelDetail.getTModel().get(0).getTModelKey(), TckTModel.MARY_PUBLISHER_TMODEL_KEY);
                                        if (tModelDetail.getTModel().get(0).isDeleted()) {
                                                break;
                                        }
                                } catch (Exception ex) {
                                        logger.warn(ex.getMessage());
                                        tModelDetail = null;
                                        break;
                                }
                                timeout--;
                                Thread.sleep(1000);

                        }
                        Assert.assertNotNull(tModelDetail);
                        Assert.assertNotNull(tModelDetail.getTModel());
                        Assert.assertNotNull(tModelDetail.getTModel().get(0));
                        Assert.assertEquals(tModelDetail.getTModel().get(0).getTModelKey(), TckTModel.MARY_PUBLISHER_TMODEL_KEY);
                        Assert.assertEquals(tModelDetail.getTModel().get(0).isDeleted(), true);
                        logger.info("Mary's tModel was deleted(hidden) replicated");
                        // TckCommon.PrintMarker();

                } finally {

                        try{
                                samBizNode2.deleteSamSyndicatorBusiness(samTokenNode2);
                        } catch (Throwable t) {
                            
                        }
                        resetTmodels();
                        resetBusinesses();

                }
                //check node2 for a "hidden" tmodel should be accessible via getDetails
        }

        @Test
        public void testReplicationPublisherAssertionSet() throws Exception {
                Assume.assumeTrue(TckPublisher.isReplicationEnabled());
                Assume.assumeTrue(TckPublisher.isJUDDI());
                logger.info("testReplicationPublisherAssertionSet");
        }

        /**
         * covers adding and removing a service and a binding
         *
         * @throws Exception
         */
        @Test
        //@Ignore
        public void testReplicationServiceBindingAddRemove() throws Exception {
                Assume.assumeTrue(TckPublisher.isReplicationEnabled());
                Assume.assumeTrue(TckPublisher.isJUDDI());
                logger.info("testReplicationServiceBindingAddRemove");

                try {
                        //TckCommon.PrintMarker();

                        resetBusinesses();
                        resetTmodels();
                       
                        uddiTmodelNode2.saveUDDIPublisherTmodel(uddiNode2Token);
                        uddiTmodelNode2.saveTmodels(uddiNode2Token);
                        //samTModelNode2.saveTmodels(rootNode2Token); //should be owned by the uddi account?
                        
                        samTModelNode2.saveSamSyndicatorTmodel(samTokenNode2);
                        samBizNode2.saveSamSyndicatorBusiness(samTokenNode2);

                        // getReplicationStatus();//block until synched
                        //confirm sam's tmodel is on the other node
                        GetTModelDetail findTModel = new GetTModelDetail();
                        findTModel.setAuthInfo(maryTokenNode1);
                        findTModel.getTModelKey().add(TckTModel.SAM_SYNDICATOR_TMODEL_KEY);
                        TModelDetail tModelDetail = null;

                        int timeout = TckPublisher.getSubscriptionTimeout();

                        while (timeout > 0) {
                                logger.info("Waiting for the update...");
                                try {
                                        tModelDetail = inquiryMary.getTModelDetail(findTModel);
                                        break;
                                } catch (Exception ex) {
                                        logger.warn(ex.getMessage());
                                        tModelDetail = null;

                                }
                                timeout--;
                                Thread.sleep(1000);

                        }

                        Assert.assertNotNull(tModelDetail);
                        Assert.assertNotNull(tModelDetail.getTModel());
                        Assert.assertTrue(tModelDetail.getTModel().size() == 1);
                        Assert.assertTrue(tModelDetail.getTModel().get(0).getTModelKey().equals(TckTModel.SAM_SYNDICATOR_TMODEL_KEY));

                        GetBusinessDetail gbd = new GetBusinessDetail();
                        gbd.setAuthInfo(maryTokenNode1);
                        gbd.getBusinessKey().add(TckBusiness.SAM_BUSINESS_KEY);

                        //confirm mary's biz made it too
                        timeout = TckPublisher.getSubscriptionTimeout();
                        BusinessDetail businessDetail = null;
                        while (timeout > 0) {
                                logger.info("Waiting for the update...");
                                try {
                                        businessDetail = inquiryMary.getBusinessDetail(gbd);
                                        break;
                                } catch (Exception ex) {
                                        logger.warn(ex.getMessage());
                                        businessDetail = null;

                                }
                                timeout--;
                                Thread.sleep(1000);

                        }

                        Assert.assertNotNull(businessDetail);
                        Assert.assertNotNull(businessDetail.getBusinessEntity());
                        Assert.assertTrue(businessDetail.getBusinessEntity().get(0).getBusinessKey().equals(TckBusiness.SAM_BUSINESS_KEY));
                        logger.info("Business replicated");

                        //save a service
                        samServiceNode2.saveSamSyndicatorService(samTokenNode2);

                        GetServiceDetail gsd = new GetServiceDetail();
                        gsd.setAuthInfo(maryTokenNode1);
                        gsd.getServiceKey().add(TckBusinessService.SAM_SERVICE_KEY);

                        //confirm sam's service made it too
                        timeout = TckPublisher.getSubscriptionTimeout();
                        ServiceDetail sd = null;
                        while (timeout > 0) {
                                logger.info("Waiting for the update...");
                                try {
                                        sd = inquiryMary.getServiceDetail(gsd);
                                        break;
                                } catch (Exception ex) {
                                        logger.warn(ex.getMessage());
                                        sd = null;

                                }
                                timeout--;
                                Thread.sleep(1000);

                        }

                        Assert.assertNotNull(sd);
                        Assert.assertNotNull(sd.getBusinessService());
                        Assert.assertTrue(sd.getBusinessService().get(0).getBusinessKey().equals(TckBusiness.SAM_BUSINESS_KEY));
                        Assert.assertTrue(sd.getBusinessService().get(0).getServiceKey().equals(TckBusinessService.SAM_SERVICE_KEY));
                        logger.info("Service replicated");

                        BindingTemplate newbt = new BindingTemplate();
                        newbt.setServiceKey(TckBusinessService.SAM_SERVICE_KEY);
                        newbt.setAccessPoint(new AccessPoint("http://localhost", "wsdlDeployment"));
                        newbt.getDescription().add(new Description("a new accesspoint that's replicated!", "en"));
                        SaveBinding saveBind = new SaveBinding();
                        saveBind.setAuthInfo(samTokenNode2);
                        saveBind.getBindingTemplate().add(newbt);
                        logger.info("Binding saved");
                        BindingDetail saveBinding = publishSamNode2.saveBinding(saveBind);

                        GetBindingDetail bindingDetail = new GetBindingDetail();
                        bindingDetail.setAuthInfo(maryTokenNode1);
                        bindingDetail.getBindingKey().add(saveBinding.getBindingTemplate().get(0).getBindingKey());

                        //confirm sam's binding made it too
                        timeout = TckPublisher.getSubscriptionTimeout();
                        BindingDetail savedMary = null;
                        while (timeout > 0) {
                                logger.info("Waiting for the update...");
                                try {
                                        savedMary = inquiryMary.getBindingDetail(bindingDetail);
                                        break;
                                } catch (Exception ex) {
                                        logger.warn(ex.getMessage());
                                        savedMary = null;

                                }
                                timeout--;
                                Thread.sleep(1000);

                        }
                        Assert.assertNotNull(savedMary);
                        Assert.assertNotNull(savedMary.getBindingTemplate());
                        Assert.assertNotNull(savedMary.getBindingTemplate().get(0));
                        Assert.assertEquals(savedMary.getBindingTemplate().get(0).getBindingKey(), saveBinding.getBindingTemplate().get(0).getBindingKey());
                        Assert.assertEquals(savedMary.getBindingTemplate().get(0).getServiceKey(), saveBinding.getBindingTemplate().get(0).getServiceKey());
                        logger.info("Binding was replicated");

                        //delete the binding
                        DeleteBinding db = new DeleteBinding();
                        db.setAuthInfo(samTokenNode2);
                        db.getBindingKey().add(saveBinding.getBindingTemplate().get(0).getBindingKey());
                        publishSamNode2.deleteBinding(db);
                        logger.info("binding deleted");

                        //confirm deletion
                        timeout = TckPublisher.getSubscriptionTimeout();
                        BindingDetail gbindd = null;
                        GetBindingDetail getBindingDetail = new GetBindingDetail();
                        getBindingDetail.setAuthInfo(maryTokenNode1);
                        getBindingDetail.getBindingKey().add(saveBinding.getBindingTemplate().get(0).getBindingKey());
                        while (timeout > 0) {
                                logger.info("Waiting for the update...");
                                try {
                                        gbindd = inquiryMary.getBindingDetail(getBindingDetail);

                                } catch (Exception ex) {
                                        logger.warn(ex.getMessage());
                                        gbindd = null;
                                        break;
                                }
                                timeout--;
                                Thread.sleep(1000);

                        }
                        //check node2 for delete biz, should be gone

                        if (gbindd != null) {
                                Assert.fail(saveBinding.getBindingTemplate().get(0).getBindingKey() + " wasn't deleted on node 1");
                        }
                        logger.info("binding deletion was replicated");

                        //delete the service
                        DeleteService delsvc = new DeleteService();
                        delsvc.setAuthInfo(samTokenNode2);
                        delsvc.getServiceKey().add(TckBusinessService.SAM_SERVICE_KEY);
                        publishSamNode2.deleteService(delsvc);
                        logger.info("service deleted");

                        ServiceDetail sdd = null;
                        //confirm deletion
                        GetServiceDetail getServiceDetail = new GetServiceDetail();
                        getServiceDetail.setAuthInfo(maryTokenNode1);
                        getServiceDetail.getServiceKey().add(TckBusinessService.SAM_SERVICE_KEY);
                        timeout = TckPublisher.getSubscriptionTimeout();
                        while (timeout > 0) {
                                logger.info("Waiting for the update...");
                                try {
                                        sdd = inquiryMary.getServiceDetail(getServiceDetail);

                                } catch (Exception ex) {
                                        logger.warn(ex.getMessage());
                                        sdd = null;
                                        break;
                                }
                                timeout--;
                                Thread.sleep(1000);

                        }
                        //check node2 for delete biz, should be gone

                        if (sdd != null) {
                                Assert.fail(TckBusinessService.SAM_SERVICE_KEY + " wasn't deleted on node 1");
                        }
                        logger.info("service deletion was replicated");

                        //clean up
                        samBizNode2.deleteSamSyndicatorBusiness(samTokenNode2);
                        samTModelNode2.deleteSamSyndicatorTmodel(samTokenNode2);

                        //delete both
                        timeout = TckPublisher.getSubscriptionTimeout();
                        businessDetail = null;

                        while (timeout > 0) {
                                logger.info("Waiting for the update...");
                                try {
                                        businessDetail = inquiryMary.getBusinessDetail(gbd);

                                } catch (Exception ex) {
                                        logger.warn(ex.getMessage());
                                        businessDetail = null;
                                        break;
                                }
                                timeout--;
                                Thread.sleep(1000);

                        }
                        //check node2 for delete biz, should be gone

                        if (businessDetail != null) {
                                Assert.fail(TckBusiness.SAM_BUSINESS_KEY + " wasn't deleted on node 1");
                        }
                        logger.info("sam's business deletion was replicated");

                        tModelDetail = inquiryMary.getTModelDetail(findTModel);
                        Assert.assertNotNull(tModelDetail);
                        Assert.assertNotNull(tModelDetail.getTModel());
                        Assert.assertNotNull(tModelDetail.getTModel().get(0));
                        Assert.assertEquals(tModelDetail.getTModel().get(0).getTModelKey(), TckTModel.SAM_SYNDICATOR_TMODEL_KEY);
                        Assert.assertEquals(tModelDetail.getTModel().get(0).isDeleted(), true);
                        logger.info("sam's tModel was deleted(hidden) replicated");
                        // TckCommon.PrintMarker();
                        logger.info("Test passed");

                } finally {
                        //TckCommon.PrintMarker();
                        //logger.fatal("The test failed, attempting to clean up the business and tModels");
                        try {
                                DeleteBusiness db = new DeleteBusiness();
                                db.setAuthInfo(samTokenNode2);
                                db.getBusinessKey().add(TckBusiness.SAM_BUSINESS_KEY);
                                try {
                                        publishSamNode2.deleteBusiness(db);
                                } catch (Exception ex) {
                                }
                                int timeout = TckPublisher.getSubscriptionTimeout();
                                GetBusinessDetail findTModel = new GetBusinessDetail();
                                findTModel.setAuthInfo(maryTokenNode1);
                                findTModel.getBusinessKey().add(TckBusiness.SAM_BUSINESS_KEY);
                                BusinessDetail tModelDetail = null;
                                while (timeout > 0) {
                                        logger.info("Waiting for the update...");
                                        try {
                                                tModelDetail = inquiryMary.getBusinessDetail(findTModel);
                                        } catch (Exception ex) {
                                                logger.warn(ex.getMessage());
                                                tModelDetail = null;
                                                break;

                                        }
                                        timeout--;
                                        try {
                                                Thread.sleep(1000);
                                        } catch (InterruptedException ex) {

                                        }

                                }
                                Assert.assertNull(tModelDetail);

                        } catch (Exception ex) {
                                ex.printStackTrace();
                        }
                        resetTmodels();

                }

        }

        /**
         * this function basically waits until the nodes are done synchronizing
         *
         * @throws Exception
         */
        private void getReplicationStatus() throws Exception {
              //  logger.info("Getting replication status....Mary's node1...");
                //  waitUntilSynched(replicationMary);
                //  logger.info("Getting replication status....Sam's node2...");
                //  waitUntilSynched(replicationSam);
        }

        private void waitUntilSynched(UDDIReplicationPortType repl) throws Exception {
                List<ChangeRecordIDType> mary = repl.getHighWaterMarks();
                JAXB.marshal(mary.get(0), System.out);

                Long highmary = mary.get(0).getOriginatingUSN();
                Thread.sleep(1000);
                Long highmary2 = mary.get(0).getOriginatingUSN();
                int counter = 0;
                while (highmary2 > highmary && counter < 90) { //indicates that there are still changes being processed
                        highmary = highmary2;
                        //= mary.get(0).getOriginatingUSN();
                        Thread.sleep(1000);
                        mary = repl.getHighWaterMarks();
                        highmary2 = mary.get(0).getOriginatingUSN();
                        logger.info("Changes are still being processesed...." + highmary2 + ">" + highmary);
                        counter++;
                }
                if (counter == 90) {
                        TckCommon.PrintMarker();
                        logger.info("Changes are still being processed after a " + counter + "sec wait!!");
                }

        }

        private void resetTmodels() {

                logger.info("resetting tmodels");
                DeleteTModel dtm = new DeleteTModel();
                dtm.setAuthInfo(rootNode1Token);
                dtm.getTModelKey().add(TckTModel.MARY_PUBLISHER_TMODEL_KEY);
                try {
                        juddiApiServiceNode1.adminDeleteTModel(dtm);
                        logger.info("Node1 mary deleted");
                        waitForTModelAdminDeletion(dtm.getTModelKey().get(0), inquirySamNode2, samTokenNode2);
                } catch (Exception ex) {
                        logger.info("unable to delete tmodel " + ex.getMessage());
                }

                dtm = new DeleteTModel();
                dtm.setAuthInfo(rootNode1Token);
                dtm.getTModelKey().add(TckTModel.JOE_PUBLISHER_TMODEL_KEY);
                try {
                        juddiApiServiceNode1.adminDeleteTModel(dtm);
                        logger.info("Node1 joe deleted");
                        waitForTModelAdminDeletion(dtm.getTModelKey().get(0), inquirySamNode2, samTokenNode2);
                } catch (Exception ex) {
                        logger.info("unable to delete tmodel " + ex.getMessage());
                }

                dtm = new DeleteTModel();
                dtm.setAuthInfo(rootNode1Token);
                dtm.getTModelKey().add(TckTModel.SAM_SYNDICATOR_TMODEL_KEY);
                try {
                        juddiApiServiceNode1.adminDeleteTModel(dtm);
                        logger.info("Node1 sam deleted");
                        waitForTModelAdminDeletion(dtm.getTModelKey().get(0), inquirySamNode2, samTokenNode2);
                } catch (Exception ex) {
                        logger.info("unable to delete tmodel " + ex.getMessage());
                }

                dtm = new DeleteTModel();
                dtm.setAuthInfo(rootNode2Token);
                dtm.getTModelKey().add(TckTModel.MARY_PUBLISHER_TMODEL_KEY);
                try {
                        juddiApiServiceNode2.adminDeleteTModel(dtm);
                        logger.info("Node2 mary deleted");
                        waitForTModelAdminDeletion(dtm.getTModelKey().get(0), inquiryMary, maryTokenNode1);
                } catch (Exception ex) {
                        logger.info("unable to delete tmodel " + ex.getMessage());
                }

                dtm = new DeleteTModel();
                dtm.setAuthInfo(rootNode2Token);
                dtm.getTModelKey().add(TckTModel.JOE_PUBLISHER_TMODEL_KEY);
                try {
                        juddiApiServiceNode2.adminDeleteTModel(dtm);
                        logger.info("Node2 joe deleted");
                        waitForTModelAdminDeletion(dtm.getTModelKey().get(0), inquiryMary, maryTokenNode1);
                } catch (Exception ex) {
                        logger.info("unable to delete tmodel " + ex.getMessage());
                }

                dtm = new DeleteTModel();
                dtm.setAuthInfo(rootNode2Token);
                dtm.getTModelKey().add(TckTModel.SAM_SYNDICATOR_TMODEL_KEY);
                try {
                        juddiApiServiceNode2.adminDeleteTModel(dtm);
                        logger.info("Node2 sam deleted");
                        waitForTModelAdminDeletion(dtm.getTModelKey().get(0), inquiryMary, maryTokenNode1);
                } catch (Exception ex) {
                        logger.info("unable to delete tmodel " + ex.getMessage());
                }
        }

        private void resetBusinesses() {
                logger.info("resetting businesses");
                DeleteBusiness dtm = new DeleteBusiness();
                dtm.setAuthInfo(rootNode1Token);
                dtm.getBusinessKey().add(TckBusiness.MARY_BUSINESS_KEY);
                try {
                        publishMary.deleteBusiness(dtm);
                        logger.info("Node1 mary deleted");
                } catch (Exception ex) {
                        logger.info("unable to delete business " + ex.getMessage());
                }
                dtm.setAuthInfo(rootNode2Token);
                try {
                        publishSamNode2.deleteBusiness(dtm);
                        logger.info("Node2 mary deleted");
                } catch (Exception ex) {
                        logger.info("unable to delete business " + ex.getMessage());
                }

                dtm = new DeleteBusiness();
                dtm.setAuthInfo(rootNode1Token);
                dtm.getBusinessKey().add(TckBusiness.JOE_BUSINESS_KEY);
                try {
                        publishMary.deleteBusiness(dtm);
                        logger.info("Node1 joe deleted");
                } catch (Exception ex) {
                        logger.info("unable to delete business " + ex.getMessage());
                }
                dtm.setAuthInfo(rootNode2Token);
                try {
                        publishSamNode2.deleteBusiness(dtm);
                        logger.info("Node2 joe deleted");
                } catch (Exception ex) {
                        logger.info("unable to delete business " + ex.getMessage());
                }

                dtm = new DeleteBusiness();
                dtm.setAuthInfo(rootNode1Token);
                dtm.getBusinessKey().add(TckBusiness.SAM_BUSINESS_KEY);
                try {
                        publishMary.deleteBusiness(dtm);
                        logger.info("Node1 sam deleted");
                } catch (Exception ex) {
                        logger.info("unable to delete business " + ex.getMessage());
                }
                dtm.setAuthInfo(rootNode2Token);
                try {
                        publishSamNode2.deleteBusiness(dtm);
                        logger.info("Node2 sam deleted");
                } catch (Exception ex) {
                        logger.info("unable to delete business " + ex.getMessage());
                }

        }

        private void waitForTModelAdminDeletion(String tModelKey, UDDIInquiryPortType inquiry, String token) {

                int timeout = TckPublisher.getSubscriptionTimeout();
                GetTModelDetail findTModel = new GetTModelDetail();
                findTModel.setAuthInfo(token);
                findTModel.getTModelKey().add(tModelKey);
                TModel tModelDetail = null;
                while (timeout > 0) {
                        logger.info("Waiting for the update...");
                        try {
                            TModelDetail tModelDetail1 = inquiry.getTModelDetail(findTModel);
                            if (tModelDetail1==null || tModelDetail1.getTModel().isEmpty()) {
                                tModelDetail = null;
                                break;
                            } else {
                                tModelDetail = tModelDetail1.getTModel().get(0);
                            }
                        } catch (Exception ex) {
                                logger.warn(ex.getMessage());
                                tModelDetail = null;
                                break;
                        }
                        timeout--;
                        try {
                                Thread.sleep(1000);
                        } catch (InterruptedException ex) {

                        }

                }
                Assert.assertNull("the tModel with the key " + tModelKey + " wasn't deleted", tModelDetail);
                logger.info("******************** " + tModelKey + " confired removed at all nodes");

        }

        /**
         * If the same key is created at two nodes before the replication change
         * record has been distributed, it's possible for the same record to be
         * owned by two different nodes. After a short period of time, both
         * records should be rejected
         *
         * <a href="http://www.uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908178">7.3.9</a>
         * for more info on collision detection
         *
         * requires support for conditional new data inserts and concurrence
         * from nodes.
         *
         * @throws Exception
         */
        @Test
        @Ignore
        public void replicationConflictMaintainOwership() throws Exception {
                try {
                        resetTmodels();

                        maryTModelNode1.saveMaryPublisherTmodel(maryTokenNode1);
                        samTModelNode2.saveMaryPublisherTmodel(samTokenNode2);

                        //TODO assert both records removed
                } finally {
                        resetTmodels();
                }

        }

}
