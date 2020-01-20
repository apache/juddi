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
package org.apache.juddi.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.ClassUtil;
import org.apache.juddi.api.impl.AuthenticatedService;
import org.apache.juddi.api.impl.UDDIInquiryImpl;
import org.apache.juddi.api.impl.UDDIPublicationImpl;
import org.apache.juddi.keygen.KeyGenerator;
import org.apache.juddi.mapping.MappingApiToModel;
import org.apache.juddi.mapping.MappingModelToApi;
import org.apache.juddi.model.ReplicationConfiguration;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.replication.ReplicationNotifier;
import org.apache.juddi.v3.client.cryptor.XmlUtils;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.FatalErrorException;
import org.apache.juddi.v3.error.InvalidKeyPassedException;
import org.apache.juddi.v3.error.KeyUnavailableException;
import org.apache.juddi.v3.error.ValueNotAllowedException;
import org.apache.juddi.validation.ValidatePublish;
import org.apache.juddi.validation.ValidateReplication;
import org.apache.juddi.validation.ValidateUDDIKey;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.TModel;
import org.uddi.repl_v3.Operator;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * This class is called when jUDDI starts up
 *
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 * @see org.apache.juddi.servlets.RegistryServlet
 */
public class Install {

        public static final String FILE_BUSINESSENTITY = "_BusinessEntity.xml";
        public static final String FILE_PUBLISHER = "_Publisher.xml";
        public static final String FILE_TMODELKEYGEN = "_tModelKeyGen.xml";
        public static final String FILE_TMODELS = "_tModels.xml";

        public static final String FILE_PERSISTENCE = "persistence.xml";
        public static final String JUDDI_INSTALL_DATA_DIR = "juddi_install_data/";
        public static final String JUDDI_CUSTOM_INSTALL_DATA_DIR = "juddi_custom_install_data/";
        public static final String FILE_REPLICATION_CONFIG = "_replicationConfiguration.xml";
        public static final Log log = LogFactory.getLog(Install.class);

        protected static void install(Configuration config) throws JAXBException, DispositionReportFaultMessage, IOException, ConfigurationException, XMLStreamException {

                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();

                UddiEntityPublisher rootPublisher = null;

                try {
                        tx.begin();
                        boolean seedAlways = config.getBoolean("juddi.seed.always", false);
                        boolean alreadyInstalled = alreadyInstalled(config);
                        if (!seedAlways && alreadyInstalled) {
                                throw new FatalErrorException(new ErrorMessage("errors.install.AlreadyInstalled"));
                        }

                        String rootPublisherStr = config.getString(Property.JUDDI_ROOT_PUBLISHER);
                        String fileRootTModelKeygen = rootPublisherStr + FILE_TMODELKEYGEN;
                        TModel rootTModelKeyGen = (TModel) buildInstallEntity(fileRootTModelKeygen, "org.uddi.api_v3", config);
                        String fileRootBusinessEntity = rootPublisherStr + FILE_BUSINESSENTITY;
                        org.uddi.api_v3.BusinessEntity rootBusinessEntity = (org.uddi.api_v3.BusinessEntity) buildInstallEntity(fileRootBusinessEntity, "org.uddi.api_v3", config);

                        String rootPartition = getRootPartition(rootTModelKeyGen);
                        //JUDDI-645
                        String nodeId = config.getString(Property.JUDDI_NODE_ID, getNodeId(rootBusinessEntity.getBusinessKey(), rootPartition));
                        //getNodeId(rootBusinessEntity.getBusinessKey(), rootPartition);
                        String rootbizkey = getNodeId(rootBusinessEntity.getBusinessKey(), rootPartition);
                        String fileRootPublisher = rootPublisherStr + FILE_PUBLISHER;
                        String fileReplicationConfig = rootPublisherStr + FILE_REPLICATION_CONFIG;
                        org.uddi.repl_v3.ReplicationConfiguration replicationCfg = (org.uddi.repl_v3.ReplicationConfiguration) buildInstallEntityAlt(fileReplicationConfig, org.uddi.repl_v3.ReplicationConfiguration.class, config);
                        if (!alreadyInstalled) {
                                log.info("Loading the root Publisher from file " + fileRootPublisher);

                                rootPublisher = installPublisher(em, fileRootPublisher, config);
                                installRootPublisherKeyGen(em, rootTModelKeyGen, rootPartition, rootPublisher, nodeId);
                                rootBusinessEntity.setBusinessKey(rootbizkey);
                                installBusinessEntity(true, em, rootBusinessEntity, rootPublisher, rootPartition, config, nodeId);
                                installReplicationConfiguration(em, replicationCfg, config, nodeId);
                        } else {
                                log.debug("juddi.seed.always reapplies all seed files except for the root data.");
                        }

                        List<String> juddiPublishers = getPublishers(config);
                        for (String publisherStr : juddiPublishers) {
                                String filePublisher = publisherStr + FILE_PUBLISHER;
                                String fileTModelKeygen = publisherStr + FILE_TMODELKEYGEN;
                                TModel tModelKeyGen = (TModel) buildInstallEntity(fileTModelKeygen, "org.uddi.api_v3", config);
                                String fileBusinessEntity = publisherStr + FILE_BUSINESSENTITY;
                                org.uddi.api_v3.BusinessEntity businessEntity = (org.uddi.api_v3.BusinessEntity) buildInstallEntity(fileBusinessEntity, "org.uddi.api_v3", config);
                                UddiEntityPublisher publisher = installPublisher(em, filePublisher, config);
                                if (publisher == null) {
                                        throw new ConfigurationException("File " + filePublisher + " not found.");
                                } else {
                                        if (tModelKeyGen != null) {
                                                installPublisherKeyGen(em, tModelKeyGen, publisher, nodeId);
                                        }
                                        if (businessEntity != null) {
                                                installBusinessEntity(false, em, businessEntity, publisher, null, config, nodeId);
                                        }
                                        String fileTModels = publisherStr + FILE_TMODELS;
                                        installSaveTModel(em, fileTModels, publisher, nodeId, config);
                                }
                        }

                        tx.commit();
                } catch (DispositionReportFaultMessage dr) {
                        log.error(dr.getMessage(), dr);
                        tx.rollback();
                        throw dr;
                } catch (JAXBException je) {
                        log.error(je.getMessage(), je);
                        tx.rollback();
                        throw je;
                } catch (IOException ie) {
                        log.error(ie.getMessage(), ie);
                        tx.rollback();
                        throw ie;
                } catch (XMLStreamException ex) {
                        log.error(ex.getMessage(), ex);
                        tx.rollback();
                        throw ex;
            } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
        }

        protected static void uninstall() {
                // Close the open emf, open a new one with Persistence.create...(String, Map) and overwrite the property that handles the table 
                // generation. The persistence.xml file will have to be read in to determine which property
                // to overwrite.  The property will be specific to the provider.  
                // Hibernate:  <property name="hibernate.hbm2ddl.auto" value="update"/> ->use "create-drop" or just "drop"?
                // OpenJPA: openjpa.jdbc.SynchronizeMappings=buildSchema(SchemaAction='add,deleteTableContents')
                // etc...(find more)
                // Then close this emf.  Question:  is the original emf reusable or will closing it cause problems?

        }

        /**
         * Checks if there is a database with a root publisher. If it is not
         * found an
         *
         * @param config
         * @return true if it finds a database with the root publisher in it.
         * @throws ConfigurationException
         */
        protected static boolean alreadyInstalled(Configuration config) throws ConfigurationException {

                String rootPublisherStr = config.getString(Property.JUDDI_ROOT_PUBLISHER);
                log.info("Checking if jUDDI is seeded by searching for root publisher " + rootPublisherStr);
                org.apache.juddi.model.Publisher publisher = null;
                int numberOfTries = 0;
                while (numberOfTries++ < 100) {
                        EntityManager em = PersistenceManager.getEntityManager();
                        EntityTransaction tx = em.getTransaction();
                        try {
                                tx.begin();
                                publisher = em.find(org.apache.juddi.model.Publisher.class, rootPublisherStr);
                                tx.commit();
                        } finally {
                                if (tx.isActive()) {
                                        tx.rollback();
                                }
                                em.close();
                        }
                        if (publisher != null) {
                                return true;
                        }

                        if (config.getBoolean(Property.JUDDI_LOAD_INSTALL_DATA, Property.DEFAULT_LOAD_INSTALL_DATA)) {
                                log.debug("Install data not yet installed.");
                                return false;
                        } else {
                                try {
                                        log.info("Install data not yet installed.");
                                        log.info("Going to sleep and retry...");
                                        Thread.sleep(1000l);
                                } catch (InterruptedException e) {
                                        log.error(e.getMessage(), e);
                                }
                        }
                }
                throw new ConfigurationException("Could not load the Root node data. Please check for errors.");
        }

        protected static String getRootPartition(TModel rootTModelKeyGen) throws JAXBException, IOException, DispositionReportFaultMessage {
                String result = rootTModelKeyGen.getTModelKey().substring(0, rootTModelKeyGen.getTModelKey().lastIndexOf(KeyGenerator.PARTITION_SEPARATOR));

                if (result == null || result.length() == 0) {
                        throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.MalformedKey", result));
                }

                // Must validate the root partition.  The first component should be a domain key and the any following
                // tokens should be a valid KSS.
                result = result.trim();
                if (result.endsWith(KeyGenerator.PARTITION_SEPARATOR) || result.startsWith(KeyGenerator.PARTITION_SEPARATOR)) {
                        throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.MalformedKey", result));
                }

                StringTokenizer tokenizer = new StringTokenizer(result.toLowerCase(), KeyGenerator.PARTITION_SEPARATOR);
                for (int count = 0; tokenizer.hasMoreTokens(); count++) {
                        String nextToken = tokenizer.nextToken();

                        if (count == 0) {
                                if (!ValidateUDDIKey.isValidDomainKey(nextToken)) {
                                        throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.MalformedKey", result));
                                }
                        } else {
                                if (!ValidateUDDIKey.isValidKSS(nextToken)) {
                                        throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.MalformedKey", result));
                                }
                        }
                }

                return result;
        }

        protected static String getNodeId(String userNodeId, String rootPartition) throws DispositionReportFaultMessage {

                String result = userNodeId;
                if (result == null || result.length() == 0) {
                        result = rootPartition + KeyGenerator.PARTITION_SEPARATOR + UUID.randomUUID();
                } else {
                        ValidateUDDIKey.validateUDDIv3Key(result);
                        String keyPartition = result.substring(0, result.lastIndexOf(KeyGenerator.PARTITION_SEPARATOR));
                        if (!rootPartition.equalsIgnoreCase(keyPartition)) {
                                throw new KeyUnavailableException(new ErrorMessage("errors.keyunavailable.BadPartition", userNodeId));
                        }
                }
                return result;
        }

        private static String installBusinessEntity(boolean isRoot, EntityManager em, org.uddi.api_v3.BusinessEntity rootBusinessEntity,
                UddiEntityPublisher rootPublisher, String rootPartition, Configuration config, String nodeId)
                throws JAXBException, DispositionReportFaultMessage, IOException {

                if (isRoot) {
                        validateRootBusinessEntity(rootBusinessEntity, rootPublisher, rootPartition, config);
                }

                org.apache.juddi.model.BusinessEntity modelBusinessEntity = new org.apache.juddi.model.BusinessEntity();
                MappingApiToModel.mapBusinessEntity(rootBusinessEntity, modelBusinessEntity);

                modelBusinessEntity.setAuthorizedName(rootPublisher.getAuthorizedName());
                modelBusinessEntity.setNodeId(nodeId);
                Date now = new Date();
                modelBusinessEntity.setCreated(now);
                modelBusinessEntity.setModified(now);
                modelBusinessEntity.setModifiedIncludingChildren(now);
                //JUDDI-645
                //modelBusinessEntity.setNodeId(modelBusinessEntity.getEntityKey());
                modelBusinessEntity.setNodeId(config.getString(Property.JUDDI_NODE_ID, modelBusinessEntity.getEntityKey()));

                for (org.apache.juddi.model.BusinessService service : modelBusinessEntity.getBusinessServices()) {
                        service.setAuthorizedName(rootPublisher.getAuthorizedName());
                        service.setNodeId(modelBusinessEntity.getNodeId());
                        service.setCreated(now);
                        service.setModified(now);
                        service.setModifiedIncludingChildren(now);
                        //JUDDI-645
                        //service.setNodeId(config.getString(Property.JUDDI_NODE_ID,modelBusinessEntity.getEntityKey()));
                        //service.setNodeId(modelBusinessEntity.getEntityKey());

                        for (org.apache.juddi.model.BindingTemplate binding : service.getBindingTemplates()) {
                                binding.setAuthorizedName(rootPublisher.getAuthorizedName());
                                binding.setNodeId(nodeId);
                                binding.setCreated(now);
                                binding.setModified(now);
                                binding.setModifiedIncludingChildren(now);
                                //binding.setNodeId(modelBusinessEntity.getEntityKey());
                                //binding.setNodeId(config.getString(Property.JUDDI_NODE_ID,modelBusinessEntity.getEntityKey()));
                                //JUDDI-645

                        }
                }

                em.persist(modelBusinessEntity);
                SaveBusiness sb = new SaveBusiness();
                sb.getBusinessEntity().add(rootBusinessEntity);
                ReplicationNotifier.enqueue(UDDIPublicationImpl.getChangeRecord(modelBusinessEntity, rootBusinessEntity, modelBusinessEntity.getNodeId()));

                return modelBusinessEntity.getEntityKey();

        }

        // A watered down version of ValidatePublish's validateBusinessEntity, designed for the specific condition that this is run upon the initial
        // jUDDI install.
        private static void validateRootBusinessEntity(org.uddi.api_v3.BusinessEntity businessEntity, UddiEntityPublisher rootPublisher,
                String rootPartition, Configuration config)
                throws DispositionReportFaultMessage {

                // A supplied businessService can't be null
                if (businessEntity == null) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.businessentity.NullInput"));
                }

                // The business key should already be set to the previously calculated and validated nodeId.  This validation is unnecessary but kept for 
                // symmetry with the other entity validations.
                String entityKey = businessEntity.getBusinessKey();
                if (entityKey == null || entityKey.length() == 0) {
                        entityKey = rootPartition + KeyGenerator.PARTITION_SEPARATOR + UUID.randomUUID();
                        businessEntity.setBusinessKey(entityKey);
                } else {
                        // Per section 4.4: keys must be case-folded
                        entityKey = entityKey.toLowerCase();
                        businessEntity.setBusinessKey(entityKey);

                        ValidateUDDIKey.validateUDDIv3Key(entityKey);
                        String keyPartition = entityKey.substring(0, entityKey.lastIndexOf(KeyGenerator.PARTITION_SEPARATOR));
                        if (!rootPartition.equalsIgnoreCase(keyPartition)) {
                                throw new KeyUnavailableException(new ErrorMessage("errors.keyunavailable.BadPartition", entityKey));
                        }
                }

                ValidatePublish validatePublish = new ValidatePublish(rootPublisher, config.getString(Property.JUDDI_NODE_ID));

                validatePublish.validateNames(businessEntity.getName());
                validatePublish.validateDiscoveryUrls(businessEntity.getDiscoveryURLs());
                validatePublish.validateContacts(businessEntity.getContacts(), config);
                validatePublish.validateCategoryBag(businessEntity.getCategoryBag(), config, true);
                validatePublish.validateIdentifierBag(businessEntity.getIdentifierBag(), config, true);

                org.uddi.api_v3.BusinessServices businessServices = businessEntity.getBusinessServices();
                if (businessServices != null) {
                        List<org.uddi.api_v3.BusinessService> businessServiceList = businessServices.getBusinessService();
                        if (businessServiceList == null || businessServiceList.size() == 0) {
                                throw new ValueNotAllowedException(new ErrorMessage("errors.businessservices.NoInput"));
                        }

                        for (org.uddi.api_v3.BusinessService businessService : businessServiceList) {
                                validateRootBusinessService(businessService, businessEntity, rootPublisher, rootPartition, config);
                        }
                }

        }

        // A watered down version of ValidatePublish's validateBusinessService, designed for the specific condition that this is run upon the initial
        // jUDDI install.
        private static void validateRootBusinessService(org.uddi.api_v3.BusinessService businessService, org.uddi.api_v3.BusinessEntity parent,
                UddiEntityPublisher rootPublisher, String rootPartition, Configuration config)
                throws DispositionReportFaultMessage {

                // A supplied businessService can't be null
                if (businessService == null) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.businessservice.NullInput"));
                }

                // A business key doesn't have to be provided, but if it is, it should match the parent business's key
                String parentKey = businessService.getBusinessKey();
                if (parentKey != null && parentKey.length() > 0) {
                        if (!parentKey.equalsIgnoreCase(parent.getBusinessKey())) {
                                throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.ParentBusinessNotFound", parentKey + " " + businessService.getBusinessKey() + " " + businessService.getBusinessKey().length() + " " + parentKey.length()));
                        }
                }

                // Retrieve the service's passed key
                String entityKey = businessService.getServiceKey();
                if (entityKey == null || entityKey.length() == 0) {
                        entityKey = rootPartition + KeyGenerator.PARTITION_SEPARATOR + UUID.randomUUID();
                        businessService.setServiceKey(entityKey);
                } else {
                        // Per section 4.4: keys must be case-folded
                        entityKey = entityKey.toLowerCase();
                        businessService.setServiceKey(entityKey);

                        ValidateUDDIKey.validateUDDIv3Key(entityKey);
                        String keyPartition = entityKey.substring(0, entityKey.lastIndexOf(KeyGenerator.PARTITION_SEPARATOR));
                        if (!rootPartition.equalsIgnoreCase(keyPartition)) {
                                throw new KeyUnavailableException(new ErrorMessage("errors.keyunavailable.BadPartition", entityKey));
                        }
                }

                ValidatePublish validatePublish = new ValidatePublish(rootPublisher, config.getString(Property.JUDDI_NODE_ID));

                validatePublish.validateNames(businessService.getName());
                validatePublish.validateCategoryBag(businessService.getCategoryBag(), config, true);

                org.uddi.api_v3.BindingTemplates bindingTemplates = businessService.getBindingTemplates();
                if (bindingTemplates != null) {
                        List<org.uddi.api_v3.BindingTemplate> bindingTemplateList = bindingTemplates.getBindingTemplate();
                        if (bindingTemplateList == null || bindingTemplateList.size() == 0) {
                                throw new ValueNotAllowedException(new ErrorMessage("errors.bindingtemplates.NoInput"));
                        }

                        for (org.uddi.api_v3.BindingTemplate bindingTemplate : bindingTemplateList) {
                                validateRootBindingTemplate(bindingTemplate, businessService, rootPublisher, rootPartition, config);
                        }
                }
        }

        // A watered down version of ValidatePublish's validatBindingTemplate, designed for the specific condition that this is run upon the initial
        // jUDDI install.
        private static void validateRootBindingTemplate(org.uddi.api_v3.BindingTemplate bindingTemplate, org.uddi.api_v3.BusinessService parent,
                UddiEntityPublisher rootPublisher, String rootPartition, Configuration config)
                throws DispositionReportFaultMessage {

                // A supplied businessService can't be null
                if (bindingTemplate == null) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.bindingtemplate.NullInput"));
                }

                // A service key doesn't have to be provided, but if it is, it should match the parent service's key
                String parentKey = bindingTemplate.getServiceKey();
                if (parentKey != null && parentKey.length() > 0) {
                        if (!parentKey.equalsIgnoreCase(parent.getServiceKey())) {
                                throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.ParentServiceNotFound", parentKey));
                        }
                }

                // Retrieve the service's passed key
                String entityKey = bindingTemplate.getBindingKey();
                if (entityKey == null || entityKey.length() == 0) {
                        entityKey = rootPartition + KeyGenerator.PARTITION_SEPARATOR + UUID.randomUUID();
                        bindingTemplate.setBindingKey(entityKey);
                } else {
                        // Per section 4.4: keys must be case-folded
                        entityKey = entityKey.toLowerCase();
                        bindingTemplate.setBindingKey(entityKey);

                        ValidateUDDIKey.validateUDDIv3Key(entityKey);
                        String keyPartition = entityKey.substring(0, entityKey.lastIndexOf(KeyGenerator.PARTITION_SEPARATOR));
                        if (!rootPartition.equalsIgnoreCase(keyPartition)) {
                                throw new KeyUnavailableException(new ErrorMessage("errors.keyunavailable.BadPartition", entityKey));
                        }
                }

                ValidatePublish validatePublish = new ValidatePublish(rootPublisher, config.getString(Property.JUDDI_NODE_ID));

                validatePublish.validateCategoryBag(bindingTemplate.getCategoryBag(), config, true);
                validatePublish.validateTModelInstanceDetails(bindingTemplate.getTModelInstanceDetails(), config, true);

        }

        private static void installTModels(EntityManager em, List<org.uddi.api_v3.TModel> apiTModelList, UddiEntityPublisher publisher, String nodeId) throws DispositionReportFaultMessage {
                if (apiTModelList != null) {
                        for (org.uddi.api_v3.TModel apiTModel : apiTModelList) {
                                String tModelKey = apiTModel.getTModelKey();

                                if (tModelKey.toUpperCase().endsWith(KeyGenerator.KEYGENERATOR_SUFFIX.toUpperCase())) {
                                        installPublisherKeyGen(em, apiTModel, publisher, nodeId);
                                } else {
                                        org.apache.juddi.model.Tmodel modelTModel = new org.apache.juddi.model.Tmodel();
                                        apiTModel.setTModelKey(apiTModel.getTModelKey().toLowerCase());

                                        MappingApiToModel.mapTModel(apiTModel, modelTModel);

                                        modelTModel.setAuthorizedName(publisher.getAuthorizedName());

                                        Date now = new Date();
                                        modelTModel.setCreated(now);
                                        modelTModel.setModified(now);
                                        modelTModel.setModifiedIncludingChildren(now);
                                        modelTModel.setNodeId(nodeId);

                                        em.persist(modelTModel);

                                        SaveTModel stm = new SaveTModel();
                                        stm.getTModel().add(apiTModel);
                                        ReplicationNotifier.enqueue(UDDIPublicationImpl.getChangeRecord(modelTModel, apiTModel, nodeId));
                                }

                        }
                }

        }

        private static void installRootPublisherKeyGen(EntityManager em, TModel rootTModelKeyGen, String rootPartition, UddiEntityPublisher publisher, String nodeId)
                throws DispositionReportFaultMessage {

                rootTModelKeyGen.setTModelKey(rootPartition + KeyGenerator.PARTITION_SEPARATOR + KeyGenerator.KEYGENERATOR_SUFFIX);

                installPublisherKeyGen(em, rootTModelKeyGen, publisher, nodeId);
        }

        private static void installPublisherKeyGen(EntityManager em, TModel apiTModel, UddiEntityPublisher publisher, String nodeId) throws DispositionReportFaultMessage {

                org.apache.juddi.model.Tmodel modelTModel = new org.apache.juddi.model.Tmodel();
                MappingApiToModel.mapTModel(apiTModel, modelTModel);

                modelTModel.setAuthorizedName(publisher.getAuthorizedName());

                Date now = new Date();
                modelTModel.setCreated(now);
                modelTModel.setModified(now);
                modelTModel.setModifiedIncludingChildren(now);
                modelTModel.setNodeId(nodeId);

                em.persist(modelTModel);

        }

        private static List<String> getPublishers(Configuration config) throws ConfigurationException {
                List<String> publishers = new ArrayList<String>();
                String basePath = JUDDI_CUSTOM_INSTALL_DATA_DIR;
                URL url = ClassUtil.getResource(JUDDI_CUSTOM_INSTALL_DATA_DIR, Install.class);
                if (url == null) {
                        url = ClassUtil.getResource(JUDDI_INSTALL_DATA_DIR, Install.class);
                        basePath = JUDDI_INSTALL_DATA_DIR;
                }

                String path = null;

                try {
                        path = url.toURI().getSchemeSpecificPart();
                } catch (URISyntaxException e) {
                        throw new ConfigurationException(e);
                }

                File dir = new File(path);
                String rootPublisherStr = config.getString(Property.JUDDI_ROOT_PUBLISHER);
                if (dir.exists()) {
                        log.debug("Discovering the Publisher XML data files in directory: " + path);
                        File[] files = dir.listFiles(new PublisherFileFilter());
                        if (files!=null)
                                for (File f : files) {
                                        String publisher = f.getName().substring(0, f.getName().indexOf(FILE_PUBLISHER));
                                        if (!rootPublisherStr.equalsIgnoreCase(publisher)) {
                                                publishers.add(publisher);
                                        }
                                }
                } else {
                        String[] paths = {};
                        Enumeration<JarEntry> en = null;
                        JarFile jf = null;
                        try {

                                if (path.indexOf("!") > 0) {
                                        paths = path.split("!");
                                        jf = new JarFile(new File(new URI(paths[0])));
                                        en = jf.entries();
                                } else {
                                        // Handle Windows / jboss-5.1.0 case
                                        if (path.indexOf(".jar") > 0) {
                                                paths = path.split(".jar");
                                                paths[0] = paths[0] + ".jar";
                                                File temp = new File(paths[0]);
                                                if (temp.exists()) {
                                                        jf = new JarFile(temp);
                                                        en = jf.entries();
                                                }
                                        }
                                }
                                if (paths.length > 0) {
                                        log.debug("Discovering the Publisher XML data files in jar: " + paths[0]);
                                        while (en != null && en.hasMoreElements()) {
                                                String name = en.nextElement().getName();
                                                if (name.endsWith(FILE_PUBLISHER)) {
                                                        log.debug("Found publisher file=" + name);
                                                        String publisher = name.substring(basePath.length(), name.indexOf(FILE_PUBLISHER));
                                                        if (!rootPublisherStr.equalsIgnoreCase(publisher)) {
                                                                publishers.add(publisher);
                                                        }
                                                }
                                        }
                                } else {
                                        log.info("No custom configuration files where found in " + path);
                                }
                                if (jf != null) {
                                        jf.close();
                                }

                        } catch (IOException e) {
                                throw new ConfigurationException(e);
                        } catch (URISyntaxException e) {
                                throw new ConfigurationException(e);
                        }
                }
                return publishers;
        }

        private static Object buildInstallEntityAlt(final String fileName, Class outputtype, Configuration config) throws JAXBException, IOException, ConfigurationException {
                InputStream resourceStream = null;

                // First try the custom install directory
                URL url = ClassUtil.getResource(JUDDI_CUSTOM_INSTALL_DATA_DIR + fileName, Install.class);
                if (url != null) {
                        resourceStream = url.openStream();
                }

                // If the custom install directory doesn't exist, then use the standard install directory where the resource is guaranteed to exist.
                if (resourceStream == null) {
                        url = ClassUtil.getResource(JUDDI_INSTALL_DATA_DIR + fileName, Install.class);
                        if (url != null) {
                                resourceStream = url.openStream();
                        }
                        // If file still does not exist then return null;
                        if (url == null || resourceStream == null) {
                                if (fileName.endsWith(FILE_PUBLISHER)) {
                                        throw new ConfigurationException("Could not locate " + JUDDI_INSTALL_DATA_DIR + fileName);
                                } else {
                                        log.debug("Could not locate: " + url);
                                }
                                return null;
                        }
                }
                log.info("Loading the content of file: " + url);
                StringBuilder xml = new StringBuilder();
                byte[] b = new byte[4096];
                for (int n; (n = resourceStream.read(b)) != -1;) {
                        xml.append(new String(b, 0, n, AuthenticatedService.UTF8));
                }
                log.debug("inserting: " + xml.toString());
                StringReader reader = new StringReader(xml.toString());
             
               Object obj= XmlUtils.unmarshal(reader, outputtype);
               reader.close();
               return obj;
        }

        private static Object buildInstallEntity(final String fileName, String packageName, Configuration config) throws JAXBException, IOException, ConfigurationException, XMLStreamException {
                InputStream resourceStream = null;

                // First try the custom install directory
                URL url = ClassUtil.getResource(JUDDI_CUSTOM_INSTALL_DATA_DIR + fileName, Install.class);
                if (url != null) {
                        resourceStream = url.openStream();
                }

                // If the custom install directory doesn't exist, then use the standard install directory where the resource is guaranteed to exist.
                if (resourceStream == null) {
                        url = ClassUtil.getResource(JUDDI_INSTALL_DATA_DIR + fileName, Install.class);
                        if (url != null) {
                                resourceStream = url.openStream();
                        }
                        // If file still does not exist then return null;
                        if (url == null || resourceStream == null) {
                                if (fileName.endsWith(FILE_PUBLISHER)) {
                                        throw new ConfigurationException("Could not locate " + JUDDI_INSTALL_DATA_DIR + fileName);
                                } else {
                                        log.debug("Could not locate: " + url);
                                }
                                return null;
                        }
                }
                log.info("Loading the content of file: " + url);
                StringBuilder xml = new StringBuilder();
                byte[] b = new byte[4096];
                for (int n; (n = resourceStream.read(b)) != -1;) {
                        xml.append(new String(b, 0, n, AuthenticatedService.UTF8));
                }
                log.debug("inserting: " + xml.toString());
                StringReader reader = new StringReader(xml.toString());

                Object obj= XmlUtils.unmarshal(reader, packageName);
                reader.close();
                return obj;
        }

        /**
         * Public convenience method that allows one to retrieve the node
         * business entity (perhaps to display during an install process, or
         * even to initiate the install process).
         *
         * @param businessKey
         * @return BusinessEntity Object
         * @throws DispositionReportFaultMessage
         */
        public static org.uddi.api_v3.BusinessEntity getNodeBusinessEntity(String businessKey) throws DispositionReportFaultMessage {
                UDDIInquiryImpl inquiry = new UDDIInquiryImpl();

                org.uddi.api_v3.GetBusinessDetail gbd = new org.uddi.api_v3.GetBusinessDetail();
                gbd.getBusinessKey().add(businessKey);

                org.uddi.api_v3.BusinessDetail bd = inquiry.getBusinessDetail(gbd);
                if (bd != null) {
                        List<org.uddi.api_v3.BusinessEntity> beList = bd.getBusinessEntity();
                        if (beList != null && beList.size() > 0) {
                                return beList.get(0);
                        }
                }

                return new org.uddi.api_v3.BusinessEntity();
        }

        /**
         * Public convenience method that allows one to install additional
         * TModels via a SaveTModel structure.
         *
         * @param em - the entity manager to a juddi model
         * @param fileName - name of SaveTModel xml file
         * @param publisher - the publisher structure that owns the tModels
         * @param nodeId - the node id of the custodial node
         * @throws JAXBException
         * @throws DispositionReportFaultMessage
         * @throws IOException
         * @throws ConfigurationException
         */
        public static void installSaveTModel(EntityManager em, String fileName, UddiEntityPublisher publisher, String nodeId, Configuration config)
                throws JAXBException, DispositionReportFaultMessage, IOException, ConfigurationException, XMLStreamException {

                SaveTModel apiSaveTModel = (SaveTModel) buildInstallEntity(fileName, "org.uddi.api_v3", config);
                if (apiSaveTModel != null) {
                        installTModels(em, apiSaveTModel.getTModel(), publisher, nodeId);
                }
        }

        /**
         * Public convenience method that allows one to install additional
         * Publishers via a Publisher structure.
         *
         * @param em - the entity manager to the juddi model
         * @param fileName - name of Publisher xml file
         * @return UddiEntityPublisher object, can be any UDDIEntity
         * @throws JAXBException
         * @throws DispositionReportFaultMessage
         * @throws IOException
         * @throws ConfigurationException
         */
        public static UddiEntityPublisher installPublisher(EntityManager em, String fileName, Configuration config)
                throws JAXBException, DispositionReportFaultMessage, IOException, ConfigurationException, XMLStreamException {

                org.apache.juddi.api_v3.Publisher apiPub = (org.apache.juddi.api_v3.Publisher) buildInstallEntity(fileName, "org.apache.juddi.api_v3", config);
                if (apiPub == null) {
                        return null;
                }
                org.apache.juddi.model.Publisher modelPub = new org.apache.juddi.model.Publisher();
                MappingApiToModel.mapPublisher(apiPub, modelPub);
                em.persist(modelPub);
                return modelPub;
        }

        public static org.uddi.repl_v3.ReplicationConfiguration applyReplicationTokenChanges(org.uddi.repl_v3.ReplicationConfiguration replicationCfg, Configuration config, String thisnode) {
                log.info("replacing tokens on repl config");
                if (replicationCfg == null) {
                        return null;
                }
                //apply any token replacements
                for (Operator op : replicationCfg.getOperator()) {
                        op.setOperatorNodeID(op.getOperatorNodeID().replace("${juddi.nodeId}", thisnode));
                        op.setSoapReplicationURL(op.getSoapReplicationURL().replace("${juddi.server.baseurlsecure}", config.getString("juddi.server.baseurlsecure")));
                        op.setSoapReplicationURL(op.getSoapReplicationURL().replace("${juddi.server.baseurl}", config.getString("juddi.server.baseurl")));
                }
                if (replicationCfg.getCommunicationGraph() != null) {
                        for (int i = 0; i < replicationCfg.getCommunicationGraph().getNode().size(); i++) {
                                replicationCfg.getCommunicationGraph().getNode().set(i, replicationCfg.getCommunicationGraph().getNode().get(i).replace("${juddi.nodeId}", thisnode));

                        }
                        for (int i = 0; i < replicationCfg.getCommunicationGraph().getEdge().size(); i++) {
                                replicationCfg.getCommunicationGraph().getEdge().get(i).setMessageSender(replicationCfg.getCommunicationGraph().getEdge().get(i).getMessageSender().replace("${juddi.nodeId}", thisnode));
                                replicationCfg.getCommunicationGraph().getEdge().get(i).setMessageReceiver(replicationCfg.getCommunicationGraph().getEdge().get(i).getMessageReceiver().replace("${juddi.nodeId}", thisnode));
                                for (int k = 0; k < replicationCfg.getCommunicationGraph().getEdge().get(i).getMessageReceiverAlternate().size(); k++) {
                                        replicationCfg.getCommunicationGraph().getEdge().get(i).getMessageReceiverAlternate().set(k, replicationCfg.getCommunicationGraph().getEdge().get(i).getMessageReceiverAlternate().get(k).replace("${juddi.nodeId}", thisnode));
                                }
                        }
                }

                for (Operator op : replicationCfg.getOperator()) {
                        op.setOperatorNodeID(op.getOperatorNodeID().replace("${juddi.nodeId}", thisnode));
                }
                log.info("replacing tokens on repl config, done");
                return replicationCfg;
        }

        private static void installReplicationConfiguration(EntityManager em, org.uddi.repl_v3.ReplicationConfiguration replicationCfg, Configuration config, String thisnode) throws DispositionReportFaultMessage, ConfigurationException {
                replicationCfg = applyReplicationTokenChanges(replicationCfg, config, thisnode);
                new ValidateReplication(null, thisnode).validateSetReplicationNodes(replicationCfg, em, thisnode, config);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddkkmmZ");
                replicationCfg.setTimeOfConfigurationUpdate(sdf.format(new Date()));

                org.apache.juddi.model.ReplicationConfiguration model = new ReplicationConfiguration();

                MappingApiToModel.mapReplicationConfiguration(replicationCfg, model, em);

                model.setSerialNumber(System.currentTimeMillis());

                org.apache.juddi.model.ReplicationConfiguration oldstuff = null;
                // logger.info(publisher.getAuthorizedName() + " is setting the replication config from " + getRequestorsIPAddress());// + " " + sw.toString());
                try {
                        oldstuff = (ReplicationConfiguration) em.createQuery("select c FROM ReplicationConfiguration c order by c.serialNumber desc").getSingleResult();
                } catch (Exception ex) {
                }
                if (oldstuff != null) {
                        em.remove(oldstuff);
                }
                em.persist(model);
        }

}
