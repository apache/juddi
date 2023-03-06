/*
 * Copyright 2001-2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.juddi.api.impl;

import java.io.File;
import java.rmi.RemoteException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.Registry;
import org.apache.juddi.config.AppConfig;
import static org.apache.juddi.config.AppConfig.JUDDI_CONFIGURATION_FILE_SYSTEM_PROPERTY;
import org.apache.juddi.config.Property;
import org.apache.juddi.query.util.FindQualifiers;
import org.apache.juddi.v3.tck.TckBusiness;
import org.apache.juddi.v3.tck.TckBusinessService;
import org.apache.juddi.v3.tck.TckPublisher;
import org.apache.juddi.v3.tck.TckSecurity;
import org.apache.juddi.v3.tck.TckTModel;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.ServiceList;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class API_040_BusinessServiceLoadTest {

        private static Log logger = LogFactory.getLog(API_040_BusinessServiceTest.class);

        private static API_010_PublisherTest api010 = new API_010_PublisherTest();
        protected static TckTModel tckTModel = new TckTModel(new UDDIPublicationImpl(), new UDDIInquiryImpl());
        protected static TckBusiness tckBusiness = new TckBusiness(new UDDIPublicationImpl(), new UDDIInquiryImpl());
        protected static TckBusinessService tckBusinessService = new TckBusinessService(new UDDIPublicationImpl(), new UDDIInquiryImpl());
        private static UDDIInquiryImpl inquiry = new UDDIInquiryImpl();

        int numberOfBusinesses = 100;
        int numberOfServices = 100;

        protected static String authInfoJoe = null;
        protected static String authInfoSam = null;

        @BeforeClass
        public static void setup() throws ConfigurationException {
                File f = new File(".");
                System.out.println("Current working dir is " + f.getAbsolutePath());
                System.setProperty(JUDDI_CONFIGURATION_FILE_SYSTEM_PROPERTY, f.getAbsolutePath()+"/src/test/resources/juddiv3DisabledTModelKeybag.xml");
                Registry.start();
                logger.info("API_040_BusinessServiceTestPerformance");
                logger.debug("Getting auth tokens..");
                try {
                        api010.saveJoePublisher();
                        UDDISecurityPortType security = new UDDISecurityImpl();
                        authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        String authInfoUDDI = TckSecurity.getAuthToken(security, TckPublisher.getUDDIPublisherId(), TckPublisher.getUDDIPassword());
                        tckTModel.saveUDDIPublisherTmodel(authInfoUDDI);
                        tckTModel.saveTModels(authInfoUDDI, TckTModel.TMODELS_XML);
                } catch (RemoteException e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("Could not obtain authInfo token.");
                }
        }

        @AfterClass
        public static void shutdown() throws ConfigurationException {
                tckTModel.deleteCreatedTModels(authInfoJoe);
                Registry.stop();
                System.clearProperty(JUDDI_CONFIGURATION_FILE_SYSTEM_PROPERTY);
        }

        /**
         * loads the database with 100x100 services, runs a basic query, then
         * deletes the records
         *
         * @throws DispositionReportFaultMessage
         * @throws ConfigurationException
         */
        @Test
        //@Ignore
        public void find20Businesses() throws DispositionReportFaultMessage, ConfigurationException {
                //disable TModelBag filtering
                Assume.assumeTrue(TckPublisher.isLoadTest());
                try {
                        //logger.info("Disabling findBusiness tModelBag filtering....");
                        //AppConfig.getConfiguration().setProperty(Property.JUDDI_ENABLE_FIND_BUSINESS_TMODEL_BAG_FILTERING, false);
                        //logger.info("findBusiness tModelBag filtering is enabled: "
                        //        + AppConfig.getConfiguration().getProperty(Property.JUDDI_ENABLE_FIND_BUSINESS_TMODEL_BAG_FILTERING));
                        System.setProperty(JUDDI_CONFIGURATION_FILE_SYSTEM_PROPERTY, "/src/test/resources/juddiv3DisabledTModelKeybag.xml");
                        tckTModel.saveJoePublisherTmodel(authInfoJoe);
                        long startSave = System.currentTimeMillis();
                        //loading up 100 businesses, with a 100 services each
                        tckBusiness.saveJoePublisherBusinesses(authInfoJoe, numberOfBusinesses);
                        for (int i = 0; i < numberOfBusinesses; i++) {
                                tckBusinessService.saveJoePublisherServices(authInfoJoe, i, numberOfServices);
                        }
                        long saveDuration = System.currentTimeMillis() - startSave;
                        System.out.println("Saved " + numberOfBusinesses + " businesses with each " + numberOfServices + " services in " + saveDuration + "ms");
                        System.out.println("Tiggering findBusiness query...");
                        //find 20 businesses by name
                        FindBusiness fb = new FindBusiness();
                        org.uddi.api_v3.FindQualifiers apiFq = new org.uddi.api_v3.FindQualifiers();
                        apiFq.getFindQualifier().add(FindQualifiers.APPROXIMATE_MATCH);
                        apiFq.getFindQualifier().add(FindQualifiers.CASE_INSENSITIVE_MATCH);
                        fb.setFindQualifiers(apiFq);
                        Name name = new Name();
                        name.setValue("John%");
                        fb.getName().add(name);
                        fb.setMaxRows(20);
                        long startFind = System.currentTimeMillis();
                        BusinessList result = inquiry.findBusiness(fb);
                        long findDuration = System.currentTimeMillis() - startFind;
                        System.out.println("Find 20 businesses took " + findDuration + "ms. Size=" + result.getBusinessInfos().getBusinessInfo().size());
                        // it takes less then 1 second, make sure it stays faster then 5 seconds
                        if (findDuration > 20000) {
                                Assert.fail("This operation took too long to process");
                        }
                        //Assert.assertTrue(findDuration < 5000);
                        System.out.println("Tiggering findService query...");
                        FindService fs = new FindService();
                        fs.setFindQualifiers(apiFq);
                        name.setValue("Service One%");
                        fs.getName().add(name);
                        startFind = System.currentTimeMillis();
                        //this will match ALL services (100 * 100 =) 10,000 services
                        int all = numberOfBusinesses * numberOfServices;
                        System.out.println("Matching " + all + " services");
                        ServiceList serviceList = inquiry.findService(fs);
                        findDuration = System.currentTimeMillis() - startFind;
                        System.out.println("Find " + all + " services took " + findDuration + "ms. Size=" + serviceList.getServiceInfos().getServiceInfo().size());
                        if (findDuration > 20000) {
                                Assert.fail("This operation took too long to process");
                        }

                } finally {
                        System.out.println("Tiggering deletion...");
                        long startDelete = System.currentTimeMillis();
                        for (int i = 0; i < numberOfBusinesses; i++) {
                                tckBusinessService.deleteJoePublisherServices(authInfoJoe, i, numberOfServices);
                        }
                        long deleteDuration = System.currentTimeMillis() - startDelete;
                        System.out.println("Delete all business and services in " + deleteDuration + "ms");
                        tckBusiness.deleteJoePublisherBusinesses(authInfoJoe, numberOfBusinesses);
                        tckTModel.deleteJoePublisherTmodel(authInfoJoe);
                }

        }

}
