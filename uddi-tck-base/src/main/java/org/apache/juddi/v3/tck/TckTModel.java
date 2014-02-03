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
package org.apache.juddi.v3.tck;

import java.util.HashSet;
import java.util.Iterator;
import static junit.framework.Assert.assertEquals;

import java.util.List;
import java.util.Set;
import javax.xml.bind.JAXB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.jaxb.EntityCreator;
import org.junit.Assert;
import org.uddi.api_v3.DeleteTModel;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.GetTModelDetail;
import org.uddi.api_v3.OverviewDoc;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelDetail;
import org.uddi.api_v3.TModelList;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class TckTModel {

        public static final String JOE_PUBLISHER_TMODEL_XML = "uddi_data/joepublisher/tModelKeyGen.xml";
        public static final String JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3 = "uddi_data/joepublisher/FindTmodelTest.xml";
        public static final String JOE_PUBLISHER_TMODEL_SUBSCRIPTION3_TMODEL_KEY = "uddi:uddi.joepublisher.com:tmodelone";
        /**
         * "uddi:uddi.joepublisher.com:"
         */
        public final static String JOE_PUBLISHER_KEY_PREFIX = "uddi:uddi.joepublisher.com:";
        final static String JOE_PUBLISHER_TMODEL_KEY = "uddi:uddi.joepublisher.com:keygenerator";
        final static String MARY_PUBLISHER_TMODEL_XML = "uddi_data/marypublisher/tModelKeyGen.xml";
        final static String MARY_PUBLISHER_TMODEL_KEY = "uddi:uddi.marypublisher.com:keygenerator";
        final static String SAM_SYNDICATOR_TMODEL_XML = "uddi_data/samsyndicator/tModelKeyGen.xml";
        final static String SAM_SYNDICATOR_TMODEL_KEY = "uddi:www.samco.com:keygenerator";
        final static String TMODEL_PUBLISHER_TMODEL_XML = "uddi_data/tmodels/tModelKeyGen.xml";
        final static String TMODEL_PUBLISHER_TMODEL_KEY = "uddi:tmodelkey:categories:keygenerator";
        final static String FIND_TMODEL_XML = "uddi_data/find/findTModel1.xml";
        final static String FIND_TMODEL_XML_BY_CAT = "uddi_data/find/findTModelByCategoryBag.xml";
        public final static String TMODELS_XML = "uddi_data/tmodels/tmodels.xml";
        public final static String RIFTSAW_PUBLISHER_TMODEL_XML = "uddi_data/bpel/riftsaw/tModelKeyGen.xml";
        public final static String RIFTSAW_PUBLISHER_TMODEL_KEY = "uddi:riftsaw.jboss.org:keygenerator";
        public final static String RIFTSAW_CUST_PORTTYPE_TMODEL_XML = "uddi_data/bpel/riftsaw/tModelCustomerPortType.xml";
        public final static String RIFTSAW_CUST_PORTTYPE_TMODEL_KEY = "uddi:riftsaw.jboss.org:CustomerInterface_portType";
        public final static String RIFTSAW_AGENT_PORTTYPE_TMODEL_XML = "uddi_data/bpel/riftsaw/tModelAgentPortType.xml";
        public final static String RIFTSAW_AGENT_PORTTYPE_TMODEL_KEY = "uddi:riftsaw.jboss.org:TravelAgentInterface_portType";
        public final static String RIFTSAW_PROCESS_TMODEL_XML = "uddi_data/bpel/riftsaw/tModelProcess.xml";
        public final static String RIFTSAW_PROCESS_TMODEL_KEY = "uddi:riftsaw.jboss.org:ReservationAndBookingTicketsProcess";
        private Log logger = LogFactory.getLog(this.getClass());
        private UDDIPublicationPortType publication = null;
        private UDDIInquiryPortType inquiry = null;
        private Set<String> keyscreated = new HashSet<String>();

        public TckTModel(UDDIPublicationPortType publication,
             UDDIInquiryPortType inquiry) {
                super();
                this.publication = publication;
                this.inquiry = inquiry;
        }

        /**
         * saves a tmodel using the tModelXml parameter as a file path
         *
         * @param authInfo
         * @param tModelXml this is a relative file path
         */
        public void saveTModels(String authInfo, String tModelXml) {

                // Add tModels
                try {
                        SaveTModel st = (org.uddi.api_v3.SaveTModel) EntityCreator.buildFromDoc(tModelXml, "org.uddi.api_v3");

                        for (int i = 0; i < st.getTModel().size(); i++) {
                                 saveTModel(authInfo, st.getTModel().get(i), false);
                        }
                        //st.setAuthInfo(authInfo);
                        //publication.saveTModel(st);

                } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("No exception should be thrown");
                }
        }

        private TModel saveTModel(String authInfo, TModel tmIn, boolean force) {
                boolean exists = false;
                GetTModelDetail gt1 = new GetTModelDetail();
                gt1.getTModelKey().add(tmIn.getTModelKey());
                try {
                        TModelDetail td1 = inquiry.getTModelDetail(gt1);
                        if (td1 != null && !td1.getTModel().isEmpty()) {
                                if (!td1.getTModel().get(0).isDeleted()) {
                                        exists = true;
                                } else {
                                        logger.info("The tModel with key " + tmIn.getTModelKey() + " exists already, but is flagged as deleted. Overwritting");
                                }
                        }
                } catch (Exception ex) {
                }

                if (!exists || force) // Add the tModel
                {
                        try {
                                SaveTModel st = new SaveTModel();
                                st.setAuthInfo(authInfo);

                                st.getTModel().add(tmIn);
                                publication.saveTModel(st);

                                keyscreated.add(tmIn.getTModelKey());
                                // Now get the entity and check the values
                                GetTModelDetail gt = new GetTModelDetail();
                                gt.getTModelKey().add(tmIn.getTModelKey());
                                TModelDetail td = inquiry.getTModelDetail(gt);
                                List<org.uddi.api_v3.TModel> tmOutList = td.getTModel();
                                org.uddi.api_v3.TModel tmOut = tmOutList.get(0);

                                assertEquals(tmIn.getTModelKey().toLowerCase(), tmOut.getTModelKey());
                                assertEquals(tmIn.getName().getLang(), tmOut.getName().getLang());
                                assertEquals(tmIn.getName().getValue(), tmOut.getName().getValue());
                                TckValidator.checkDescriptions(tmIn.getDescription(), tmOut.getDescription());
                                TckValidator.checkCategories(tmIn.getCategoryBag(), tmOut.getCategoryBag());
                                for (OverviewDoc overviewDoc : tmIn.getOverviewDoc()) {
                                        TckValidator.checkOverviewDocs(overviewDoc, tmOut.getOverviewDoc());
                                }
                                logger.info("The TModel " + tmIn.getTModelKey() + " saved");

                                if (TckCommon.isDebug()) {
                                        JAXB.marshal(tmOut, System.out);
                                }
                                return tmOut;

                        } catch (Exception e) {
                                logger.error(e.getMessage(), e);
                                Assert.fail("No exception should be thrown");
                        }

                } else {
                        logger.info("The TModel " + tmIn.getTModelKey() + " exists already, skipping");
                }
                return tmIn;
        }

        public TModel saveTModel(String authInfo, String tModelXml, String tModelKey) {
                return saveTModel(authInfo, tModelXml, tModelKey, false);
        }

        public TModel saveTModel(String authInfo, String tModelXml, String tModelKey, boolean force) {
                logger.info("Loading tModel from " + tModelXml);
                org.uddi.api_v3.TModel tmIn = null;
                try {
                        tmIn = (org.uddi.api_v3.TModel) EntityCreator.buildFromDoc(tModelXml, "org.uddi.api_v3");
                } catch (Exception ex) {
                        Assert.fail("unable to load tmodel from file!");
                }
                if (tmIn == null) {
                        Assert.fail("unable to load tmodel from file!");
                }
                return saveTModel(authInfo, tmIn, force);
        }

        public synchronized void deleteTModel(String authInfo, String tModelXml, String tModelKey, boolean force) {
                if (keyscreated.contains(tModelKey) || force) {
                        try {
                                keyscreated.remove(tModelKey);
                                //Now deleting the TModel
                                // Delete the entity and make sure it is removed
                                DeleteTModel dt = new DeleteTModel();
                                dt.setAuthInfo(authInfo);

                                logger.info("deleting tmodel " + tModelKey);
                                dt.getTModelKey().add(tModelKey);
                                publication.deleteTModel(dt);

                        } catch (Exception e) {
                                logger.error(e.getMessage(), e);
                                Assert.fail("No exception should be thrown");
                        }
                } else {
                        logger.info("skipping the deletion of tmodel " + tModelKey + " since it wasn't created by the tck");
                }
        }

        public synchronized void deleteTModel(String authInfo, String tModelXml, String tModelKey) {

                deleteTModel(authInfo, tModelXml, tModelKey, false);
        }

        public TModelDetail getTModelDetail(String authInfo, String tModelXml, String tModelKey) {
                try {
                        //Try to get the TModel
                        GetTModelDetail tmodelDetail = new GetTModelDetail();
                        tmodelDetail.setAuthInfo(authInfo);
                        tmodelDetail.getTModelKey().add(tModelKey);

                        return inquiry.getTModelDetail(tmodelDetail);

                } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("No exception should be thrown");
                }
                Assert.fail("We should already have returned");
                return null;
        }

        public TModelList findJoeTModelDetail() {
                try {

                        FindTModel body = (FindTModel) EntityCreator.buildFromDoc(FIND_TMODEL_XML, "org.uddi.api_v3");
                        TModelList result = inquiry.findTModel(body);

                        return result;

                } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("No exception should be thrown");
                }
                Assert.fail("We should already have returned");
                return null;
        }

        public TModelList findJoeTModelDetailByCategoryBag() {
                try {

                        FindTModel body = (FindTModel) EntityCreator.buildFromDoc(FIND_TMODEL_XML_BY_CAT, "org.uddi.api_v3");
                        TModelList result = inquiry.findTModel(body);

                        return result;

                } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("No exception should be thrown");
                }
                Assert.fail("We should already have returned");
                return null;
        }

        public TModel saveJoePublisherTmodel(String authInfoJoe) {
                return saveTModel(authInfoJoe, JOE_PUBLISHER_TMODEL_XML, JOE_PUBLISHER_TMODEL_KEY, false);
        }

        public TModel saveJoePublisherTmodel(String authInfoJoe, boolean force) {
                return saveTModel(authInfoJoe, JOE_PUBLISHER_TMODEL_XML, JOE_PUBLISHER_TMODEL_KEY, force);
        }

        public void saveUDDIPublisherTmodel(String authInfoTM) {
                saveTModel(authInfoTM, TMODEL_PUBLISHER_TMODEL_XML, TMODEL_PUBLISHER_TMODEL_KEY, false);
        }

        public void saveTmodels(String authInfoJoe) {
                saveTModels(authInfoJoe, TMODELS_XML);
        }

        public void deleteJoePublisherTmodel(String authInfoJoe) {
                deleteTModel(authInfoJoe, JOE_PUBLISHER_TMODEL_XML, JOE_PUBLISHER_TMODEL_KEY);
        }

        public TModelDetail getJoePublisherTmodel(String authInfoJoe) {
                return getTModelDetail(authInfoJoe, JOE_PUBLISHER_TMODEL_XML, JOE_PUBLISHER_TMODEL_KEY);
        }

        public TModelList findJoePublisherTmodel(String authInfoJoe) {
                return findJoeTModelDetail();
        }

        public TModel saveMaryPublisherTmodel(String authInfoMary) {
                return saveTModel(authInfoMary, MARY_PUBLISHER_TMODEL_XML, MARY_PUBLISHER_TMODEL_KEY, false);
        }

        public void deleteMaryPublisherTmodel(String authInfoMary) {
                deleteTModel(authInfoMary, MARY_PUBLISHER_TMODEL_XML, MARY_PUBLISHER_TMODEL_KEY);
        }

        public TModel saveSamSyndicatorTmodel(String authInfoSam) {
                return saveTModel(authInfoSam, SAM_SYNDICATOR_TMODEL_XML, SAM_SYNDICATOR_TMODEL_KEY, false);
        }

        public void deleteSamSyndicatorTmodel(String authInfoSam) {
                deleteTModel(authInfoSam, SAM_SYNDICATOR_TMODEL_XML, SAM_SYNDICATOR_TMODEL_KEY);
        }

        /**
         * deletes at tmodels created usign the tck tool, tmodels that were
         * previously present (before running) are not deleted no exception is
         * thrown if an error occurs, but it will be logged
         *
         * @param authinfo
         */
        public void deleteCreatedTModels(String authinfo) {
                if (this.keyscreated != null) {
                        Iterator<String> iterator = keyscreated.iterator();
                        while (iterator.hasNext()) {
                                DeleteTModel dtm = new DeleteTModel();
                                dtm.setAuthInfo(authinfo);
                                String s = iterator.next();
                                logger.info("cleanup tModel " + s);
                                dtm.getTModelKey().add(s);
                                try {
                                        publication.deleteTModel(dtm);
                                } catch (Exception ex) {
                                        logger.warn("failed to delete tmodel " + s + " " + ex.getMessage());
                                        logger.debug("failed to delete tmodel " + s + " " + ex.getMessage(), ex);
                                }
                        }
                }

        }
}
	