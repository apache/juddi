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
package org.apache.juddi.v2.tck;

import org.apache.juddi.v2.tck.*;
import java.io.File;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import java.util.List;
import javax.xml.bind.JAXB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.jaxb.EntityCreator;
import org.junit.Assert;
import org.uddi.api_v2.BusinessDetail;
import org.uddi.api_v2.BusinessEntity;
import org.uddi.api_v2.DeleteBusiness;
import org.uddi.api_v2.Description;
import org.uddi.api_v2.GetBusinessDetail;
import org.uddi.api_v2.SaveBusiness;
import org.uddi.v2_service.Inquire;
import org.uddi.v2_service.Publish;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class TckBusiness {

        public final static String JOE_BUSINESS_XML = "uddi_data_v2/joepublisher/businessEntity.xml";
        public final static String JOE_BUSINESS_KEY = "920465ac-07dd-4756-8212-5c4c300faf36";
        public final static String JOE_BUSINESS3_XML = "uddi_data_v2/joepublisher/businessEntity3.xml";
        
        public final static String JOE_BUSINESS3_KEY = "d04d1a31-bce1-46dc-8da9-8c7c16c46932";
        public final static String JOE_BUSINESS_MOVE_XML = "uddi_data_v2/joepublisher/moveBusinessService1to3.xml";
        public final static String MARY_BUSINESS_XML = "uddi_data_v2/marypublisher/businessEntity.xml";
        
        public final static String MARY_BUSINESS_KEY = "e509bb52-daeb-46b1-abb7-d488b10baf19";
        public final static String SAM_BUSINESS_XML = "uddi_data_v2/samsyndicator/businessEntity.xml";
        public final static String SAM_BUSINESS_WITHPROJECTION_XML = "uddi_data_v2/samsyndicator/businessEntity_withProjection.xml";
        
        public final static String SAM_BUSINESS_KEY = "49417f41-d2b5-4749-b459-55bd785a00d2";
        public final static String COMBINE_CATBAGS_BIZ_XML = "uddi_data_v2/joepublisher/combineCatBagsBusinessServices.xml";
        
        public final static String COMBINE_CATBAGS_BIZ_KEY = "26dcd995-b5be-4050-8cdc-453d62c7fcaa";
        
        private Log logger = LogFactory.getLog(this.getClass());
        private Publish publication = null;
        private Inquire inquiry = null;

        public TckBusiness(Publish publication,
             Inquire inquiry) {
                super();
                this.publication = publication;
                this.inquiry = inquiry;
        }

        public BusinessEntity saveSamSyndicatorBusiness(String authInfoSam) {
                return saveBusiness(authInfoSam, SAM_BUSINESS_XML, SAM_BUSINESS_KEY);
        }

        public void saveSamSyndicatorBusinesses(String authInfoSam, int numberOfCopies) {
                saveBusinesses(authInfoSam, SAM_BUSINESS_XML, SAM_BUSINESS_KEY, numberOfCopies);
        }

        public BusinessEntity saveSamSyndicatorBusinessWithProjection(String authInfoSam) {
                return saveBusiness(authInfoSam, SAM_BUSINESS_WITHPROJECTION_XML, SAM_BUSINESS_KEY);
        }

        public void deleteSamSyndicatorBusiness(String authInfoSam) {
                deleteBusiness(authInfoSam, SAM_BUSINESS_XML, SAM_BUSINESS_KEY);
        }

        public void deleteSamSyndicatorBusinesses(String authInfoSam, int numberOfCopies) {
                deleteBusinesses(authInfoSam, SAM_BUSINESS_XML, SAM_BUSINESS_KEY, numberOfCopies);
        }


        public void saveJoePublisherBusiness(String authInfoJoe) {
                saveBusiness(authInfoJoe, JOE_BUSINESS_XML, JOE_BUSINESS_KEY, TckCommon.isDebug());
        }

        public BusinessEntity saveCombineCatBagsPublisherBusiness(String authInfoJoe) {
                return saveBusiness(authInfoJoe, COMBINE_CATBAGS_BIZ_XML, COMBINE_CATBAGS_BIZ_KEY);
        }

        public BusinessEntity saveJoePublisherBusiness3(String authInfoJoe) {
                return saveBusiness(authInfoJoe, JOE_BUSINESS3_XML, JOE_BUSINESS3_KEY);
        }

        public BusinessEntity saveJoePublisherBusiness1to3(String authInfoJoe) {
                return saveBusiness(authInfoJoe, JOE_BUSINESS_MOVE_XML, JOE_BUSINESS3_KEY);
        }

        public BusinessEntity saveMaryPublisherBusiness(String authInfoMary) {
                return saveBusiness(authInfoMary, MARY_BUSINESS_XML, MARY_BUSINESS_KEY);
        }

        public void updateJoePublisherBusiness(String authInfoJoe) {
                updateBusiness(authInfoJoe, JOE_BUSINESS_XML, JOE_BUSINESS_KEY);
        }

        public void saveJoePublisherBusinesses(String authInfoJoe, int numberOfCopies) {
                saveBusinesses(authInfoJoe, JOE_BUSINESS_XML, JOE_BUSINESS_KEY, numberOfCopies);
        }

        public void deleteJoePublisherBusiness(String authInfoJoe) {
                deleteBusiness(authInfoJoe, JOE_BUSINESS_XML, JOE_BUSINESS_KEY);
        }

        public void deleteJoePublisherBusiness3(String authInfoJoe) {
                deleteBusiness(authInfoJoe, JOE_BUSINESS3_XML, JOE_BUSINESS3_KEY);
        }

        public void deleteMaryPublisherBusiness(String authInfoMary) {
                deleteBusiness(authInfoMary, MARY_BUSINESS_XML, MARY_BUSINESS_KEY);
        }

        public void deleteJoePublisherBusinesses(String authInfoJoe, int numberOfCopies) {
                deleteBusinesses(authInfoJoe, JOE_BUSINESS_XML, JOE_BUSINESS_KEY, numberOfCopies);
        }

        public void checkServicesBusinessOne(int expectedNumberOfServices) {
                checkNumberOfServices(JOE_BUSINESS_KEY, expectedNumberOfServices);
        }

        public void checkServicesBusinessThree(int expectedNumberOfServices) {
                checkNumberOfServices(JOE_BUSINESS3_KEY, expectedNumberOfServices);
        }

        public void saveBusinesses(String authInfo, String businessXML, String businessKey, int numberOfCopies) {
                try {
                        BusinessEntity beIn = (BusinessEntity) EntityCreator.buildFromDoc(businessXML, "org.uddi.api_v2");
                        String businessName = beIn.getName().get(0).getValue();
                        for (int i = 0; i < numberOfCopies; i++) {
                                SaveBusiness sb = new SaveBusiness();
                                sb.setGeneric("2.0");
                                sb.setAuthInfo(authInfo);
                                beIn.getName().get(0).setValue(businessName + "-" + i);
                                beIn.setBusinessKey(businessKey + "-" + i);
                                sb.getBusinessEntity().add(beIn);
                                publication.saveBusiness(sb);
                                logger.info("Saved business with key " + businessName + "-" + i);
                        }

                } catch (Throwable e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("No exception should be thrown");
                }
        }

        public void checkNumberOfServices(String businessKey, int expectedServices) {

                try {
                        GetBusinessDetail gb = new GetBusinessDetail();
                        gb.setGeneric("2.0");
                        gb.getBusinessKey().add(businessKey);
                        BusinessDetail bd;
                        bd = inquiry.getBusinessDetail(gb);
                        List<BusinessEntity> beOutList = bd.getBusinessEntity();
                        BusinessEntity beOut = beOutList.get(0);
                        if (expectedServices > 0) {
                                assertEquals(expectedServices, beOut.getBusinessServices().getBusinessService().size());
                        } else {
                                Assert.assertNull(beOut.getBusinessServices());
                        }
                } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("No exception should be thrown");
                }
        }
        public BusinessEntity saveBusiness(String authInfo, String businessXML, String businessKey) {
                return saveBusiness(authInfo, businessXML, businessKey, false);
        }

        public BusinessEntity saveBusiness(String authInfo, String businessXML, String businessKey, boolean serialize) {
                logger.info("attempting to save business " + businessKey + " from " + businessXML);
                try {
                        SaveBusiness sb = new SaveBusiness();
                        sb.setGeneric("2.0");
                        sb.setAuthInfo(authInfo);

                        BusinessEntity beIn = (BusinessEntity) EntityCreator.buildFromDoc(businessXML, "org.uddi.api_v2");
                        if (beIn == null) {
                                throw new Exception("Unload to load source xml document from " + businessXML);
                        }
                        sb.getBusinessEntity().add(beIn);
                        sb.setGeneric("2.0");
                        BusinessDetail saveBusiness = publication.saveBusiness(sb);
                        logger.info("Business saved with key " + saveBusiness.getBusinessEntity().get(0).getBusinessKey());
                        if (serialize) {
                                JAXB.marshal(saveBusiness, System.out);
                        }

                        // Now get the entity and check the values
                        GetBusinessDetail gb = new GetBusinessDetail();
                        gb.setGeneric("2.0");
                        gb.getBusinessKey().add(businessKey);
                        BusinessDetail bd = inquiry.getBusinessDetail(gb);
                        List<BusinessEntity> beOutList = bd.getBusinessEntity();
                        BusinessEntity beOut = beOutList.get(0);

                        if (serialize) {
                                JAXB.marshal(beOut, new File("target/aftersave.xml"));
                        }

                        assertEquals(beIn.getBusinessKey(), beOut.getBusinessKey());

                        TckValidator.checkNames(beIn.getName(), beOut.getName());
                        TckValidator.checkDescriptions(beIn.getDescription(), beOut.getDescription());
                        TckValidator.checkDiscoveryUrls(beIn.getDiscoveryURLs(), beOut.getDiscoveryURLs());
                        TckValidator.checkContacts(beIn.getContacts(), beOut.getContacts());
                        TckValidator.checkCategories(beIn.getCategoryBag(), beOut.getCategoryBag());

                        return beOut;
                } catch (Throwable e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("No exception should be thrown");
                }
                return null;

        }

        public void updateBusiness(String authInfo, String businessXML, String businessKey) {
                try {

                        // Now get the entity and check the values
                        GetBusinessDetail gb = new GetBusinessDetail();
                        gb.setGeneric("2.0");
                        gb.getBusinessKey().add(businessKey);
                        BusinessDetail bd = inquiry.getBusinessDetail(gb);
                        List<BusinessEntity> beOutList = bd.getBusinessEntity();
                        BusinessEntity beOut = beOutList.get(0);
                        //We are expecting 2 services
                        assertEquals(2, beOut.getBusinessServices().getBusinessService().size());

                        //Now updating the business by adding another description
                        SaveBusiness sb = new SaveBusiness();
                        sb.setGeneric("2.0");
                        sb.setAuthInfo(authInfo);
                        BusinessEntity beIn = beOut;
                        Description desc2 = new Description();
                        desc2.setLang("nl");
                        desc2.setValue("Omschrijving");
                        beIn.getDescription().add(desc2);
                        sb.getBusinessEntity().add(beIn);
                        sb.setGeneric("2.0");
                        publication.saveBusiness(sb);

                        // Now get the entity and check the values
                        BusinessDetail bdnew = inquiry.getBusinessDetail(gb);
                        List<BusinessEntity> beOutListNew = bdnew.getBusinessEntity();
                        BusinessEntity beOutNew = beOutListNew.get(0);

                        assertEquals(beIn.getBusinessKey(), beOutNew.getBusinessKey());
                        // After the update we still are supposed to see two services.
                        assertNotNull(beOutNew.getBusinessServices());
                        assertEquals(2, beOutNew.getBusinessServices().getBusinessService().size());

                } catch (Throwable e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("No exception should be thrown");
                }

        }

        public void deleteBusinesses(String authInfo, String businessXML, String businessKey, int numberOfCopies) {
                try {
                        for (int i = 0; i < numberOfCopies; i++) {
                                // Delete the entity and make sure it is removed
                                String key = businessKey + "-" + i;
                                DeleteBusiness db = new DeleteBusiness();
                                db.setGeneric("2.0");
                                db.setAuthInfo(authInfo);
                                db.getBusinessKey().add(key);
                                publication.deleteBusiness(db);
                                logger.debug("Deleted business with key " + key);
                        }

                } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("No exception should be thrown");
                }
        }

        public void deleteBusiness(String authInfo, String businessXML, String businessKey) {
                try {
                        // Delete the entity and make sure it is removed
                        DeleteBusiness db = new DeleteBusiness();
                        db.setGeneric("2.0");
                        db.setAuthInfo(authInfo);
                        db.getBusinessKey().add(businessKey);
                        publication.deleteBusiness(db);

                } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("No exception should be thrown");
                }
        }
}
