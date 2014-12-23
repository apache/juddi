/*
 * Copyright 2014 The Apache Software Foundation.
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
package org.apache.juddi.api.impl;

import java.rmi.RemoteException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.Registry;
import org.apache.juddi.v3.tck.TckBusiness;
import org.apache.juddi.v3.tck.TckFindEntity;
import org.apache.juddi.v3.tck.TckPublisher;
import org.apache.juddi.v3.tck.TckSecurity;
import org.apache.juddi.v3.tck.TckTModel;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.GetOperationalInfo;
import org.uddi.api_v3.OperationalInfos;
import org.uddi.custody_v3.KeyBag;
import org.uddi.custody_v3.TransferEntities;
import org.uddi.custody_v3.TransferToken;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 *
 * @author alex
 */
public class API_170_CustodyTransferTest {
        
        private static Log logger = LogFactory.getLog(API_030_BusinessEntityTest.class);
        private static API_010_PublisherTest api010 = new API_010_PublisherTest();
        private static TckTModel tckTModel = new TckTModel(new UDDIPublicationImpl(), new UDDIInquiryImpl());
        private static TckBusiness tckBusiness = new TckBusiness(new UDDIPublicationImpl(), new UDDIInquiryImpl());
        private static TckFindEntity tckFindEntity = new TckFindEntity(new UDDIInquiryImpl());
        private static String authInfoMary = null;
        private static String authInfoSam = null;

        @BeforeClass
        public static void setup() throws ConfigurationException {
                Registry.start();
                logger.info("API_030_BusinessEntityTest");
                logger.debug("Getting auth token..");
                try {
                        api010.saveJoePublisher();
                        api010.saveSamSyndicator();
                        UDDISecurityPortType security = new UDDISecurityImpl();
                        authInfoMary = TckSecurity.getAuthToken(security, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());
                        authInfoSam = TckSecurity.getAuthToken(security, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                        String authInfoUDDI = TckSecurity.getAuthToken(security, TckPublisher.getUDDIPublisherId(), TckPublisher.getUDDIPassword());
                        tckTModel.saveUDDIPublisherTmodel(authInfoUDDI);
                        tckTModel.saveTModels(authInfoUDDI, TckTModel.TMODELS_XML);
                        tckTModel.saveMaryPublisherTmodel(authInfoMary);
                        tckTModel.saveSamSyndicatorTmodel(authInfoSam);
                } catch (RemoteException e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("Could not obtain authInfo token.");
                }
        }

        @AfterClass
        public static void stopRegistry() throws ConfigurationException {
                tckTModel.deleteCreatedTModels(authInfoMary);
                tckTModel.deleteCreatedTModels(authInfoSam);
                Registry.stop();
        }

        
        @Test
        public void testTransfer() throws Exception{
                 UDDIInquiryImpl inquire = new UDDIInquiryImpl();
                 
                 
                //save sam's business
                tckBusiness.saveSamSyndicatorBusiness(authInfoSam);
                
                //save mary's business
                tckBusiness.saveMaryPublisherBusiness(authInfoMary);
                
                GetOperationalInfo goi = new GetOperationalInfo();
                goi.getEntityKey().add(TckBusiness.SAM_BUSINESS_KEY);
                
                OperationalInfos operationalInfo = inquire.getOperationalInfo(goi);
                Assert.assertEquals(operationalInfo.getOperationalInfo().get(0).getAuthorizedName(), TckPublisher.getSamPublisherId());
                
                //sam wants to transfer to mary, get a token
                UDDICustodyTransferImpl custody = new UDDICustodyTransferImpl();
                KeyBag kb = new  KeyBag();
                kb.getKey().add(TckBusiness.SAM_BUSINESS_KEY);
                Holder<String> node = new Holder<String>();
                Holder<XMLGregorianCalendar> expires = new Holder<XMLGregorianCalendar>();
                Holder<byte[]> token = new Holder<byte[]>();
                custody.getTransferToken(authInfoSam,kb , node, expires, token);
                
                //use mary's account to accept and transfer
                TransferEntities te = new TransferEntities();
                te.setAuthInfo(authInfoMary);
                te.setKeyBag(kb);
                te.setTransferToken(new TransferToken());
                te.getTransferToken().setExpirationTime(expires.value);
                te.getTransferToken().setNodeID(node.value);
                te.getTransferToken().setOpaqueToken(token.value);
                custody.transferEntities(te);
                
               
              
                operationalInfo = inquire.getOperationalInfo(goi);
                
                Assert.assertEquals(operationalInfo.getOperationalInfo().get(0).getAuthorizedName(), TckPublisher.getMaryPublisherId());
        }
}
