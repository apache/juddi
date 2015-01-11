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
import org.apache.juddi.v3.client.UDDIService;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.BusinessServices;
import org.uddi.api_v3.Contact;
import org.uddi.api_v3.DeleteTModel;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.GetOperationalInfo;
import org.uddi.api_v3.GetTModelDetail;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.OperationalInfos;
import org.uddi.api_v3.PersonName;
import org.uddi.api_v3.SaveBusiness;
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

        private static Log logger = LogFactory.getLog(JUDDI_300_MultiNodeIntegrationTest.class);
        private static UDDIClient manager;
        private static String rootNode1Token;
        private static String rootNode2Token;
        private static JUDDIApiPortType juddiApiServiceNode1;
        private static JUDDIApiPortType juddiApiServiceNode2;

        private static String maryTokenNode1;
        private static String samTokenNode2;
        private static UDDICustodyTransferPortType custodySam;
        private static UDDICustodyTransferPortType custodyMary;
        private static UDDIPublicationPortType publishMary;
        private static UDDIPublicationPortType publishSam;
        private static UDDIInquiryPortType inquiryMary;
        private static UDDIInquiryPortType inquirySam;
        private static UDDIReplicationPortType replicationMary;
        private static UDDIReplicationPortType replicationSam;
        static TckBusiness maryBizNode1;
        static TckTModel maryTModelNode1;

        static TckBusiness samBizNode2;
        static TckTModel samTModelNode2;

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
                UDDIService uddiService = new UDDIService();

                replicationMary = uddiService.getUDDIReplicationPort();

                ((BindingProvider) replicationMary).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, manager.getClientConfig().getUDDINode(CFG_node1_MARY).getReplicationUrl());
                UDDISecurityPortType secNode1 = node1.getUDDISecurityService();
                rootNode1Token = TckSecurity.getAuthToken(secNode1, TckPublisher.getRootPublisherId(), TckPublisher.getRootPassword());
                maryTokenNode1 = TckSecurity.getAuthToken(secNode1, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());

                Transport node2 = manager.getTransport(CFG_node2_SAM);
                UDDISecurityPortType secNode2 = node2.getUDDISecurityService();
                rootNode2Token = TckSecurity.getAuthToken(secNode2, TckPublisher.getRootPublisherId(), TckPublisher.getRootPassword());
                samTokenNode2 = TckSecurity.getAuthToken(secNode2, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                replicationSam = uddiService.getUDDIReplicationPort();
                ((BindingProvider) replicationSam).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, manager.getClientConfig().getUDDINode(CFG_node2_SAM).getReplicationUrl());
                juddiApiServiceNode2 = node2.getJUDDIApiService();
                custodySam = node2.getUDDICustodyTransferService();
                inquirySam = node2.getUDDIInquiryService();
                publishSam = node2.getUDDIPublishService();

                samBizNode2 = new TckBusiness(publishSam, inquirySam);
                samTModelNode2 = new TckTModel(publishSam, inquirySam);
                maryBizNode1 = new TckBusiness(publishMary, inquiryMary);
                maryTModelNode1 = new TckTModel(publishMary, inquiryMary);

                if (!TckPublisher.isUDDIAuthMode()) {
                        TckSecurity.setCredentials((BindingProvider) juddiApiServiceNode1, TckPublisher.getRootPublisherId(), TckPublisher.getRootPassword());
                        TckSecurity.setCredentials((BindingProvider) juddiApiServiceNode2, TckPublisher.getRootPublisherId(), TckPublisher.getRootPassword());
                        TckSecurity.setCredentials((BindingProvider) custodyMary, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());
                        TckSecurity.setCredentials((BindingProvider) inquiryMary, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());
                        TckSecurity.setCredentials((BindingProvider) publishMary, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());
                        TckSecurity.setCredentials((BindingProvider) replicationMary, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());

                        TckSecurity.setCredentials((BindingProvider) custodySam, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                        TckSecurity.setCredentials((BindingProvider) inquirySam, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                        TckSecurity.setCredentials((BindingProvider) replicationSam, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                        TckSecurity.setCredentials((BindingProvider) publishSam, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());

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
                                System.out.println("Error getting replication config");
                                ex.printStackTrace();
                                Assert.fail(ex.getMessage());

                        }
                        if (replicationNode1.getCommunicationGraph() == null) {
                                replicationNode1.setCommunicationGraph(new CommunicationGraph());
                        }

                        Operator op = new Operator();
                        op.setOperatorNodeID("uddi:juddi.apache.org:node1");
                        op.setSoapReplicationURL(manager.getClientConfig().getUDDINode(CFG_node1_MARY).getReplicationUrl());
                        op.setOperatorStatus(OperatorStatusType.NORMAL);
                        op.getContact().add(new Contact());
                        op.getContact().get(0).getPersonName().add(new PersonName("bob", "en"));
                        op.getContact().get(0).setUseType("admin");
                        if (!Contains(replicationNode1.getOperator(), op)) {
                                replicationNode1.getOperator().add(op);
                        }

                        op = new Operator();
                        op.setOperatorNodeID("uddi:another.juddi.apache.org:node2");
                        op.setSoapReplicationURL(manager.getClientConfig().getUDDINode(CFG_node2_SAM).getReplicationUrl());
                        op.setOperatorStatus(OperatorStatusType.NORMAL);
                        op.getContact().add(new Contact());
                        op.getContact().get(0).getPersonName().add(new PersonName("mary", "en"));
                        op.getContact().get(0).setUseType("admin");
                        if (!Contains(replicationNode1.getOperator(), op)) {
                                replicationNode1.getOperator().add(op);
                        }
                        if (!replicationNode1.getCommunicationGraph().getNode().contains("uddi:another.juddi.apache.org:node2")) {
                                replicationNode1.getCommunicationGraph().getNode().add("uddi:another.juddi.apache.org:node2");
                        }
                        if (!replicationNode1.getCommunicationGraph().getNode().contains("uddi:juddi.apache.org:node1")) {
                                replicationNode1.getCommunicationGraph().getNode().add("uddi:juddi.apache.org:node1");
                        }
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
                        logger.info("Setting replication config on Node 1...");
                        juddiApiServiceNode1.setReplicationNodes(rootNode1Token, replicationNode1);
                        logger.info("Setting replication config on Node 2...");
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
        public void testMultiNodeBusinessCustodyTransfer() throws Exception {
                logger.info("testMultiNodeBusinessCustodyTransfer");
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

                                BusinessDetail businessDetail = inquirySam.getBusinessDetail(gbd);
                                if (businessDetail != null
                                        && !businessDetail.getBusinessEntity().isEmpty()) {
                                        logger.info("Mary's business exists on Sams node, grabbing the operational info to confirm ownership...");
                                }
                                beforeNode2 = inquirySam.getOperationalInfo(operationalInfo);
                                break;
                        } catch (Exception ex) {
                                logger.warn(ex.getMessage(), ex);
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
                                        afterNode2 = inquirySam.getOperationalInfo(operationalInfo);
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

        }

        /**
         * covers business, tmodels and publisher assertions
         *
         * @throws Exception
         */
        @Test

        public void testReplicationTModelBusinessPublisherAssertionAddDelete() throws Exception {
                Assume.assumeTrue(TckPublisher.isReplicationEnabled());
                Assume.assumeTrue(TckPublisher.isJUDDI());
                try {
                        TckCommon.PrintMarker();
                        TckCommon.PrintMarker();
                        TckCommon.PrintMarker();
                        logger.info("testReplicationTModelBusinessPublisherAssertionAddDelete");

                        restTmodels();

                        TModel saveMaryPublisherTmodel = maryTModelNode1.saveMaryPublisherTmodel(maryTokenNode1);

                        BusinessEntity saveMaryPublisherBusiness = maryBizNode1.saveMaryPublisherBusiness(maryTokenNode1);

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
                                        tModelDetail = inquirySam.getTModelDetail(findTModel);
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
                        Assert.assertTrue(tModelDetail.getTModel().get(0).getTModelKey().equals(TckTModel.MARY_PUBLISHER_TMODEL_KEY));

                        GetBusinessDetail gbd = new GetBusinessDetail();
                        gbd.setAuthInfo(samTokenNode2);
                        gbd.getBusinessKey().add(TckBusiness.MARY_BUSINESS_KEY);
                        
                        //confirm mary's biz made it too
                        timeout = TckPublisher.getSubscriptionTimeout();
                        BusinessDetail businessDetail =null;
                        while (timeout > 0) {
                                logger.info("Waiting for the update...");
                                try {
                                         businessDetail = inquirySam.getBusinessDetail(gbd);
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
                        Assert.assertTrue(businessDetail.getBusinessEntity().get(0).getBusinessKey().equals(TckBusiness.MARY_BUSINESS_KEY));

                        //setup a publisher assertion
                        
                        //clean up
                        maryBizNode1.deleteMaryPublisherBusiness(maryTokenNode1);
                        maryTModelNode1.deleteMaryPublisherTmodel(maryTokenNode1);

                        //delete both
                         timeout = TckPublisher.getSubscriptionTimeout();
                        businessDetail = null;
                        while (timeout > 0) {
                                logger.info("Waiting for the update...");
                                try {
                                        businessDetail = inquirySam.getBusinessDetail(gbd);

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

                        tModelDetail = inquirySam.getTModelDetail(findTModel);
                        Assert.assertNotNull(tModelDetail);
                        Assert.assertNotNull(tModelDetail.getTModel());
                        Assert.assertNotNull(tModelDetail.getTModel().get(0));
                        Assert.assertEquals(tModelDetail.getTModel().get(0).getTModelKey(), TckTModel.MARY_PUBLISHER_TMODEL_KEY);
                        Assert.assertEquals(tModelDetail.getTModel().get(0).isDeleted(), true);

                        TckCommon.PrintMarker();
                        TckCommon.PrintMarker();
                        TckCommon.PrintMarker();
                } finally {
                        
                        samBizNode2.deleteSamSyndicatorBusiness(samTokenNode2);
                        restTmodels();
                        
                }
                //check node2 for a "hidden" tmodel should be accessible via getDetails
        }

        @Test
        public void testReplicationPublisherAssertionSet() throws Exception {
                Assume.assumeTrue(TckPublisher.isReplicationEnabled());
                Assume.assumeTrue(TckPublisher.isJUDDI());
                logger.info("testReplicationPublisherAssertionSet");
        }

        @Test
        public void testReplicationServiceAdd() throws Exception {
                Assume.assumeTrue(TckPublisher.isReplicationEnabled());
                Assume.assumeTrue(TckPublisher.isJUDDI());
                logger.info("testReplicationServiceAdd");
        }

        @Test
        public void testReplicationServiceDelete() throws Exception {
                Assume.assumeTrue(TckPublisher.isReplicationEnabled());
                Assume.assumeTrue(TckPublisher.isJUDDI());
                logger.info("testReplicationServiceDelete");
        }

        @Test
        public void testReplicationBindingAdd() throws Exception {
                Assume.assumeTrue(TckPublisher.isReplicationEnabled());
                Assume.assumeTrue(TckPublisher.isJUDDI());
                logger.info("testReplicationBindingAdd");
        }

        @Test
        public void testReplicationBindingDelete() throws Exception {
                Assume.assumeTrue(TckPublisher.isReplicationEnabled());
                Assume.assumeTrue(TckPublisher.isJUDDI());
                logger.info("testReplicationBindingDelete");
        }

        /**
         * this function basically waits until the nodes are done synchronizing
         *
         * @throws Exception
         */
        private void getReplicationStatus() throws Exception {
                logger.info("Getting replication status....Mary's node1...");
                waitUntilSynched(replicationMary);
                logger.info("Getting replication status....Sam's node2...");
                waitUntilSynched(replicationSam);
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

        private void restTmodels() {
                TckCommon.PrintMarker();
                logger.info("resting tmodels");
                DeleteTModel dtm = new DeleteTModel();
                dtm.setAuthInfo(rootNode1Token);
                dtm.getTModelKey().add(TckTModel.MARY_PUBLISHER_TMODEL_KEY);
                try {
                        juddiApiServiceNode1.adminDeleteTModel(dtm);
                        logger.info("Node1 mary deleted");
                } catch (Exception ex) {
                        logger.info("unable to delete tmodel " + ex.getMessage());
                }

                dtm = new DeleteTModel();
                dtm.setAuthInfo(rootNode1Token);
                dtm.getTModelKey().add(TckTModel.JOE_PUBLISHER_TMODEL_KEY);
                try {
                        juddiApiServiceNode1.adminDeleteTModel(dtm);
                        logger.info("Node1 joe deleted");
                } catch (Exception ex) {
                        logger.info("unable to delete tmodel " + ex.getMessage());
                }

                dtm = new DeleteTModel();
                dtm.setAuthInfo(rootNode1Token);
                dtm.getTModelKey().add(TckTModel.SAM_SYNDICATOR_TMODEL_KEY);
                try {
                        juddiApiServiceNode1.adminDeleteTModel(dtm);
                        logger.info("Node1 sam deleted");
                } catch (Exception ex) {
                        logger.info("unable to delete tmodel " + ex.getMessage());
                }

                dtm = new DeleteTModel();
                dtm.setAuthInfo(rootNode2Token);
                dtm.getTModelKey().add(TckTModel.MARY_PUBLISHER_TMODEL_KEY);
                try {
                        juddiApiServiceNode2.adminDeleteTModel(dtm);
                        logger.info("Node2 mary deleted");
                } catch (Exception ex) {
                        logger.info("unable to delete tmodel " + ex.getMessage());
                }

                dtm = new DeleteTModel();
                dtm.setAuthInfo(rootNode2Token);
                dtm.getTModelKey().add(TckTModel.JOE_PUBLISHER_TMODEL_KEY);
                try {
                        juddiApiServiceNode2.adminDeleteTModel(dtm);
                        logger.info("Node2 joe deleted");
                } catch (Exception ex) {
                        logger.info("unable to delete tmodel " + ex.getMessage());
                }

                dtm = new DeleteTModel();
                dtm.setAuthInfo(rootNode2Token);
                dtm.getTModelKey().add(TckTModel.SAM_SYNDICATOR_TMODEL_KEY);
                try {
                        juddiApiServiceNode2.adminDeleteTModel(dtm);
                        logger.info("Node2 sam deleted");
                } catch (Exception ex) {
                        logger.info("unable to delete tmodel " + ex.getMessage());
                }
        }

}
