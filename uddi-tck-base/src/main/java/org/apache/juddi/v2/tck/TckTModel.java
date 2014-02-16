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
import org.uddi.api_v2.DeleteTModel;
import org.uddi.api_v2.FindTModel;
import org.uddi.api_v2.GetTModelDetail;
import org.uddi.api_v2.OverviewDoc;
import org.uddi.api_v2.SaveTModel;
import org.uddi.api_v2.TModel;
import org.uddi.api_v2.TModelDetail;
import org.uddi.api_v2.TModelList;
import org.uddi.v2_service.Inquire;
import org.uddi.v2_service.Publish;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class TckTModel {

        public static final String JOE_PUBLISHER_TMODEL_XML = "uddi_data_v2/joepublisher/tModelKeyGen.xml";
        
        
        final static String JOE_PUBLISHER_TMODEL_KEY = "uuid:c537997c-e082-461d-94e6-ec7935a3b18e";
        final static String MARY_PUBLISHER_TMODEL_XML = "uddi_data_v2/marypublisher/tModelKeyGen.xml";
        
        final static String MARY_PUBLISHER_TMODEL_KEY = "uuid:333f6466-3de7-4532-849f-7354eb842e6a";
        final static String SAM_SYNDICATOR_TMODEL_XML = "uddi_data_v2/samsyndicator/tModelKeyGen.xml";
        
        final static String SAM_SYNDICATOR_TMODEL_KEY = "uuid:c1e003d3-cb43-42f0-905f-aa8a92870bf6";
        final static String TMODEL_PUBLISHER_TMODEL_XML = "uddi_data_v2/tmodels/tModelKeyGen.xml";
        final static String TMODEL_PUBLISHER_TMODEL_KEY = "uuid:0ebf9f1d-4b81-4554-87b0-f4af21b6b569";
        
        
        final static String FIND_TMODEL_XML = "uddi_data_v2/find/findTModel1.xml";
        final static String FIND_TMODEL_XML_BY_CAT = "uddi_data_v2/find/findTModelByCategoryBag.xml";
        public final static String TMODELS_XML = "uddi_data_v2/tmodels/tmodels.xml";
        
        private Log logger = LogFactory.getLog(this.getClass());
        private Publish publication = null;
        private Inquire inquiry = null;
        private Set<String> keyscreated = new HashSet<String>();

        public TckTModel(Publish publication,
             Inquire inquiry) {
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
                        SaveTModel st = (org.uddi.api_v2.SaveTModel) EntityCreator.buildFromDoc(tModelXml, "org.uddi.api_v2");
                        st.setGeneric("2.0");

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
                gt1.setGeneric("2.0");
                gt1.getTModelKey().add(tmIn.getTModelKey());
                try {
                        TModelDetail td1 = inquiry.getTModelDetail(gt1);
                        if (td1 != null && !td1.getTModel().isEmpty()) {
                                        exists = true;
                        }
                } catch (Exception ex) {
                }

                if (!exists || force) // Add the tModel
                {
                        try {
                                SaveTModel st = new SaveTModel();
                                st.setAuthInfo(authInfo);

                                st.setGeneric("2.0");
                                st.getTModel().add(tmIn);
                                publication.saveTModel(st);

                                keyscreated.add(tmIn.getTModelKey());
                                // Now get the entity and check the values
                                GetTModelDetail gt = new GetTModelDetail();
                                gt.setGeneric("2.0");
                                gt.getTModelKey().add(tmIn.getTModelKey());
                                TModelDetail td = inquiry.getTModelDetail(gt);
                                List<org.uddi.api_v2.TModel> tmOutList = td.getTModel();
                                org.uddi.api_v2.TModel tmOut = tmOutList.get(0);

                                assertEquals(tmIn.getTModelKey().toLowerCase(), tmOut.getTModelKey());
                                assertEquals(tmIn.getName().getLang(), tmOut.getName().getLang());
                                assertEquals(tmIn.getName().getValue(), tmOut.getName().getValue());
                                TckValidator.checkDescriptions(tmIn.getDescription(), tmOut.getDescription());
                                TckValidator.checkCategories(tmIn.getCategoryBag(), tmOut.getCategoryBag());
                                
                                        TckValidator.checkOverviewDocs(tmIn.getOverviewDoc(), tmOut.getOverviewDoc());
                                
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
                org.uddi.api_v2.TModel tmIn = null;
                try {
                        tmIn = (org.uddi.api_v2.TModel) EntityCreator.buildFromDoc(tModelXml, "org.uddi.api_v2");
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
                                dt.setGeneric("2.0");
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
                        tmodelDetail.setGeneric("2.0");
                        //tmodelDetail.setAuthInfo(authInfo);
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

                        FindTModel body = (FindTModel) EntityCreator.buildFromDoc(FIND_TMODEL_XML, "org.uddi.api_v2");
                        body.setGeneric("2.0");
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

                        FindTModel body = (FindTModel) EntityCreator.buildFromDoc(FIND_TMODEL_XML_BY_CAT, "org.uddi.api_v2");
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
                                dtm.setGeneric("2.0");
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
	