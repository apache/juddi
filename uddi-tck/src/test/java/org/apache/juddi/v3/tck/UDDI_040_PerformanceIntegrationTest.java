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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.ws.BindingProvider;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.DeleteBusiness;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * Runs a number of tests and calculates operations/second as part of a unit
 * test
 *
 * @author alex o'ree
 */
public class UDDI_040_PerformanceIntegrationTest {

        static Map<String, Double> data = new HashMap<String, Double>();
        protected static Log logger = LogFactory.getLog(UDDI_040_BusinessServiceIntegrationTest.class);
        protected static TckTModel tckTModelSam = null;
        protected static TckBusiness tckBusinessSam = null;
        protected static TckBusinessService tckBusinessServiceSam = null;
        protected static UDDIInquiryPortType inquirySam = null;
        protected static UDDIPublicationPortType publishSam = null;

        protected static String authInfoSam = null;
        private static UDDIClient manager;

        @AfterClass
        public static void stopManager() throws Exception {
                if (!TckPublisher.isEnabled()) {
                        return;
                }

                tckTModelSam.deleteCreatedTModels(authInfoSam);
                manager.stop();
                Iterator<Map.Entry<String, Double>> iterator = data.entrySet().iterator();
                File f = new File("pref-rpt-" + System.currentTimeMillis() + ".txt");
                PrintWriter writer = new PrintWriter(f, "UTF-8");

                while (iterator.hasNext()) {
                        Map.Entry<String, Double> next = iterator.next();
                        logger.info(next.getKey() + " = " + next.getValue() + " tx/ms");
                        writer.write(next.getKey() + " = " + next.getValue() + " tx/ms" + System.getProperty("line.separator"));

                }
                writer.close();
                f = null;
                TckCommon.PrintMarker();

        }

        @BeforeClass
        public static void startManager() throws ConfigurationException {
                if (!TckPublisher.isEnabled()) {
                        return;
                }
                manager = new UDDIClient();
                manager.start();

                logger.debug("Getting auth tokens..");
                try {
                        Transport transport = manager.getTransport("uddiv3");
                        UDDISecurityPortType security = transport.getUDDISecurityService();
                        authInfoSam = TckSecurity.getAuthToken(security, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());

                        UDDIPublicationPortType publication = null;
                        UDDIInquiryPortType inquiry = null;

                        transport = manager.getTransport("uddiv3");
                        publication = transport.getUDDIPublishService();
                        inquiry = transport.getUDDIInquiryService();
                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) publication, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                                TckSecurity.setCredentials((BindingProvider) inquiry, TckPublisher.getSamPublisherId(), TckPublisher.getSamPassword());
                        }
                        tckTModelSam = new TckTModel(publication, inquiry);
                        tckBusinessSam = new TckBusiness(publication, inquiry);
                        tckBusinessServiceSam = new TckBusinessService(publication, inquiry);
                        inquirySam = inquiry;
                        publishSam = publication;

                } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("Could not obtain authInfo token.");
                }
        }
        long counter = 30;

        @Test
        public void testInsertFlatBusiness() throws Exception {
                logger.info("Performace test, inserting " + counter + " flat businesses");
                BusinessEntity be = new BusinessEntity();
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoSam);
                List<String> biz = new ArrayList<String>();
                long start = System.currentTimeMillis();
                for (int i = 0; i < counter; i++) {
                        System.out.print(".");
                        be.getName().clear();
                        be.getName().add(new Name("Name " + i, null));
                        sb.getBusinessEntity().clear();
                        sb.getBusinessEntity().add(be);
                        BusinessDetail saveBusiness = publishSam.saveBusiness(sb);
                        biz.add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                }
                start = System.currentTimeMillis() - start;
                data.put("Insert Flat Businesses 1x", (double)counter/(double)start );
                logger.info("Insert flat businesses took 1x" + start);

                start = System.currentTimeMillis();
                for (int i = 0; i < counter; i++) {
                        System.out.print(".");
                        DeleteBusiness deleteBusiness = new DeleteBusiness();
                        deleteBusiness.setAuthInfo(authInfoSam);
                        deleteBusiness.getBusinessKey().add(biz.get(i));
                        publishSam.deleteBusiness(deleteBusiness);

                }
                start = System.currentTimeMillis() - start;
                data.put("Delete Flat Businesses 1x", (double)counter/(double)start );
                logger.info("Insert flat businesses took 1x" + start);

        }

        @Test
        public void testInsertFlatBusinessBulk() throws Exception {
                logger.info("Performace test, inserting " + counter + " flat businesses bulk");
                BusinessEntity be = new BusinessEntity();
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoSam);
                List<String> biz = new ArrayList<String>();

                for (int i = 0; i < counter; i++) {
                        System.out.print(".");
                        be.getName().clear();
                        be.getName().add(new Name("Name " + i, null));
                        sb.getBusinessEntity().clear();
                        sb.getBusinessEntity().add(be);

                }

                long start = System.currentTimeMillis();
                BusinessDetail saveBusiness = publishSam.saveBusiness(sb);

                start = System.currentTimeMillis() - start;
                data.put("Insert Flat Businesses Bulk", (double)counter/(double)start );
                logger.info("Insert flat businesses took Bulk" + start);

                DeleteBusiness deleteBusiness = new DeleteBusiness();
                deleteBusiness.setAuthInfo(authInfoSam);

                for (int i = 0; i < saveBusiness.getBusinessEntity().size(); i++) {
                        deleteBusiness.getBusinessKey().add(saveBusiness.getBusinessEntity().get(i).getBusinessKey());
                }
                start = System.currentTimeMillis();
                publishSam.deleteBusiness(deleteBusiness);
                start = System.currentTimeMillis() - start;
                data.put("Delete Flat Businesses Bulk", (double)counter/(double)start );
                logger.info("Insert flat businesses took Bulk" + start);

        }

        @Test
        public void testInsertFlatBusinessManyNames() throws Exception {
                logger.info("Performace test, inserting " + counter + " flat businesses ManyNames");
                BusinessEntity be = new BusinessEntity();
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoSam);
                List<String> biz = new ArrayList<String>();
                long start = System.currentTimeMillis();
                for (int i = 0; i < counter; i++) {
                        System.out.print(".");
                        be.getName().clear();
                        for (int k = 0; k < counter; k++) {
                                be.getName().add(new Name("Name " + i + "-" + k, null));
                        }
                        sb.getBusinessEntity().clear();
                        sb.getBusinessEntity().add(be);
                        BusinessDetail saveBusiness = publishSam.saveBusiness(sb);
                        biz.add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                }
                start = System.currentTimeMillis() - start;
                data.put("Insert Flat Businesses 1x ManyNames", (double)counter/(double)start );
                logger.info("Insert flat businesses took 1x ManyNames" + start);

                start = System.currentTimeMillis();
                for (int i = 0; i < biz.size(); i++) {
                        System.out.print(".");
                        DeleteBusiness deleteBusiness = new DeleteBusiness();
                        deleteBusiness.setAuthInfo(authInfoSam);
                        deleteBusiness.getBusinessKey().add(biz.get(i));
                        publishSam.deleteBusiness(deleteBusiness);

                }
                start = System.currentTimeMillis() - start;
                data.put("Delete Flat Businesses 1x ManyNames", (double)counter/(double)start );
                logger.info("Insert flat businesses took 1x ManyNames" + start);

        }

        @Test
        public void testInsertFlatBusinessBulkManyNames() throws Exception {
                logger.info("Performace test, inserting " + counter + " flat businesses bulk ManyNames");
                BusinessEntity be = new BusinessEntity();
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoSam);
                List<String> biz = new ArrayList<String>();

                for (int i = 0; i < counter; i++) {
                        System.out.print(".");
                        be.getName().clear();
                        for (int k = 0; k < counter; k++) {
                                be.getName().add(new Name("Name " + i + "-" + k, null));
                        }
                        sb.getBusinessEntity().clear();
                        sb.getBusinessEntity().add(be);

                }

                long start = System.currentTimeMillis();
                BusinessDetail saveBusiness = publishSam.saveBusiness(sb);

                start = System.currentTimeMillis() - start;
                data.put("Insert Flat Businesses Bulk ManyNames", (double)counter/(double)start );
                logger.info("Insert flat businesses took Bulk ManyNames" + start);

                DeleteBusiness deleteBusiness = new DeleteBusiness();
                deleteBusiness.setAuthInfo(authInfoSam);

                for (int i = 0; i < saveBusiness.getBusinessEntity().size(); i++) {
                        deleteBusiness.getBusinessKey().add(saveBusiness.getBusinessEntity().get(i).getBusinessKey());
                }
                start = System.currentTimeMillis();
                publishSam.deleteBusiness(deleteBusiness);
                start = System.currentTimeMillis() - start;
                data.put("Delete Flat Businesses Bulk ManyNames", (double)counter/(double)start );
                logger.info("Insert flat businesses took Bulk ManyNames" + start);

        }
        
        
        
        @Test
        public void testInsertFlatBusinessManyCategories() throws Exception {
                logger.info("Performace test, inserting " + counter + " flat businesses ManyCategories");
                BusinessEntity be = new BusinessEntity();
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoSam);
                List<String> biz = new ArrayList<String>();
                long start = System.currentTimeMillis();
                for (int i = 0; i < counter; i++) {
                        System.out.print(".");
                        be.getName().clear();
                         be.getName().add(new Name("Name " + i, null));
                         be.setCategoryBag(new CategoryBag());
                        for (int k = 0; k < counter; k++) {
                               be.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.General_Keywords, "somename", "somevalue"+k));
                        }
                        sb.getBusinessEntity().clear();
                        sb.getBusinessEntity().add(be);
                        BusinessDetail saveBusiness = publishSam.saveBusiness(sb);
                        biz.add(saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                }
                start = System.currentTimeMillis() - start;
                data.put("Insert Flat Businesses 1x ManyCategories", (double)counter/(double)start );
                logger.info("Insert flat businesses took 1x ManyCategories" + start);

                start = System.currentTimeMillis();
                for (int i = 0; i < biz.size(); i++) {
                        System.out.print(".");
                        DeleteBusiness deleteBusiness = new DeleteBusiness();
                        deleteBusiness.setAuthInfo(authInfoSam);
                        deleteBusiness.getBusinessKey().add(biz.get(i));
                        publishSam.deleteBusiness(deleteBusiness);

                }
                start = System.currentTimeMillis() - start;
                data.put("Delete Flat Businesses 1x ManyCategories", (double)counter/(double)start );
                logger.info("Insert flat businesses took 1x ManyCategories" + start);

        }

        @Test
        public void testInsertFlatBusinessBulkManyCategories() throws Exception {
                logger.info("Performace test, inserting " + counter + " flat businesses bulk ManyCategories");
                BusinessEntity be = new BusinessEntity();
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfoSam);
                List<String> biz = new ArrayList<String>();

                for (int i = 0; i < counter; i++) {
                        System.out.print(".");
                        be.getName().clear();
                         be.getName().add(new Name("Name " + i, null));
                         be.setCategoryBag(new CategoryBag());
                        for (int k = 0; k < counter; k++) {
                               be.getCategoryBag().getKeyedReference().add(new KeyedReference(UDDIConstants.General_Keywords, "somename", "somevalue"+k));
                        }
                        sb.getBusinessEntity().clear();
                        sb.getBusinessEntity().add(be);

                }

                long start = System.currentTimeMillis();
                BusinessDetail saveBusiness = publishSam.saveBusiness(sb);

                start = System.currentTimeMillis() - start;
                data.put("Insert Flat Businesses Bulk ManyCategories", (double)counter/(double)start );
                logger.info("Insert flat businesses took Bulk ManyCategories" + start);

                DeleteBusiness deleteBusiness = new DeleteBusiness();
                deleteBusiness.setAuthInfo(authInfoSam);

                for (int i = 0; i < saveBusiness.getBusinessEntity().size(); i++) {
                        deleteBusiness.getBusinessKey().add(saveBusiness.getBusinessEntity().get(i).getBusinessKey());
                }
                start = System.currentTimeMillis();
                publishSam.deleteBusiness(deleteBusiness);
                start = System.currentTimeMillis() - start;
                data.put("Delete Flat Businesses Bulk ManyCategories", (double)counter/(double)start );
                logger.info("Insert flat businesses took Bulk ManyCategories" + start);

        }
}
