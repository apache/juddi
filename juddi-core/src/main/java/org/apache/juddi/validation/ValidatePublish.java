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
package org.apache.juddi.validation;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.xml.ws.Holder;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.api_v3.DeletePublisher;
import org.apache.juddi.api_v3.SavePublisher;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.Constants;
import org.apache.juddi.config.Install;
import org.apache.juddi.config.PersistenceManager;
import org.apache.juddi.config.Property;
import org.apache.juddi.keygen.KeyGenerator;
import org.apache.juddi.keygen.KeyGeneratorFactory;
import org.apache.juddi.mapping.MappingModelToApi;
import org.apache.juddi.model.Publisher;
import org.apache.juddi.model.Tmodel;
import org.apache.juddi.model.UddiEntity;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.query.FindBusinessByPublisherQuery;
import org.apache.juddi.query.FindTModelByPublisherQuery;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.error.AssertionNotFoundException;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.FatalErrorException;
import org.apache.juddi.v3.error.InvalidKeyPassedException;
import org.apache.juddi.v3.error.InvalidProjectionException;
import org.apache.juddi.v3.error.KeyUnavailableException;
import org.apache.juddi.v3.error.MaxEntitiesExceededException;
import org.apache.juddi.v3.error.UserMismatchException;
import org.apache.juddi.v3.error.ValueNotAllowedException;
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.AddPublisherAssertions;
import org.uddi.api_v3.AddressLine;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.DeleteBinding;
import org.uddi.api_v3.DeleteBusiness;
import org.uddi.api_v3.DeletePublisherAssertions;
import org.uddi.api_v3.DeleteService;
import org.uddi.api_v3.DeleteTModel;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.DiscoveryURL;
import org.uddi.api_v3.Email;
import org.uddi.api_v3.HostingRedirector;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.KeyedReferenceGroup;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.OverviewDoc;
import org.uddi.api_v3.OverviewURL;
import org.uddi.api_v3.Phone;
import org.uddi.api_v3.SaveBinding;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.SaveService;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.TModel;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * Provides validation of publish requests to Juddi
 *
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a> Modified March
 * 2013 to validate string length and ref integrity
 *
 * Advisory, be careful calling AppConfig.getConfiguration() from within
 * validation functions, it may inadvertently cause infinite loops during the
 * Install phase
 * @see Install
 */
public class ValidatePublish extends ValidateUDDIApi {

        /**
         * This flag will add additional output to stdout for debugging
         * purposes, set this to true if
         */
        private Log log = LogFactory.getLog(this.getClass());

        public ValidatePublish(UddiEntityPublisher publisher, String nodeid) {
                super(publisher,nodeid);
        }
        
         public ValidatePublish(UddiEntityPublisher publisher) {
                super(publisher);
        }

        public void validateDeleteBusiness(EntityManager em, DeleteBusiness body) throws DispositionReportFaultMessage {

                // No null input
                if (body == null) {
                        throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
                }

                // No null or empty list
                List<String> entityKeyList = body.getBusinessKey();
                if (entityKeyList == null || entityKeyList.size() == 0) {
                        throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.NoKeys"));
                }

                HashSet<String> dupCheck = new HashSet<String>();
                int i = 0;
                for (String entityKey : entityKeyList) {

                        // Per section 4.4: keys must be case-folded
                        entityKey = entityKey.toLowerCase();
                        entityKeyList.set(i, entityKey);

                        boolean inserted = dupCheck.add(entityKey);
                        if (!inserted) {
                                throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.DuplicateKey", entityKey));
                        }

                        Object obj = em.find(org.apache.juddi.model.BusinessEntity.class, entityKey);
                        if (obj == null) {
                                throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.BusinessNotFound", entityKey));
                        }

                        if (!publisher.isOwner((UddiEntity) obj)&& !((Publisher) publisher).isAdmin() ) {
                                throw new UserMismatchException(new ErrorMessage("errors.usermismatch.InvalidOwner", entityKey));
                        }

                        i++;
                }
        }

        public void validateDeleteService(EntityManager em, DeleteService body) throws DispositionReportFaultMessage {

                // No null input
                if (body == null) {
                        throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
                }

                // No null or empty list
                List<String> entityKeyList = body.getServiceKey();
                if (entityKeyList == null || entityKeyList.size() == 0) {
                        throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.NoKeys"));
                }

                HashSet<String> dupCheck = new HashSet<String>();
                int i = 0;
                for (String entityKey : entityKeyList) {

                        // Per section 4.4: keys must be case-folded
                        entityKey = entityKey.toLowerCase();
                        entityKeyList.set(i, entityKey);

                        boolean inserted = dupCheck.add(entityKey);
                        if (!inserted) {
                                throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.DuplicateKey", entityKey));
                        }

                        Object obj = em.find(org.apache.juddi.model.BusinessService.class, entityKey);
                        if (obj == null) {
                                throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.ServiceNotFound", entityKey));
                        }

                         //if you're are the owner, access granted
                        //if you are an admin && this item belongs to this node, access granted
                        //else denied
                        
                        AccessCheck(obj, entityKey);
                        i++;
                }
        }

        public void validateDeleteBinding(EntityManager em, DeleteBinding body) throws DispositionReportFaultMessage {

                // No null input
                if (body == null) {
                        throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
                }

                // No null or empty list
                List<String> entityKeyList = body.getBindingKey();
                if (entityKeyList == null || entityKeyList.size() == 0) {
                        throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.NoKeys"));
                }

                // Checking for duplicates and existence
                HashSet<String> dupCheck = new HashSet<String>();
                int i = 0;
                for (String entityKey : entityKeyList) {
                        validateKeyLength(entityKey);
                        // Per section 4.4: keys must be case-folded
                        entityKey = entityKey.toLowerCase();
                        entityKeyList.set(i, entityKey);

                        boolean inserted = dupCheck.add(entityKey);
                        if (!inserted) {
                                throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.DuplicateKey", entityKey));
                        }

                        Object obj = em.find(org.apache.juddi.model.BindingTemplate.class, entityKey);
                        if (obj == null) {
                                throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.BindingTemplateNotFound", entityKey));
                        }
                        
                        AccessCheck(obj, entityKey);

                        i++;
                }
        }

        public void validateDeleteTModel(EntityManager em, DeleteTModel body) throws DispositionReportFaultMessage {

                // No null input
                if (body == null) {
                        throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
                }

                // No null or empty list
                List<String> entityKeyList = body.getTModelKey();
                if (entityKeyList == null || entityKeyList.size() == 0) {
                        throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.NoKeys"));
                }

                HashSet<String> dupCheck = new HashSet<String>();
                int i = 0;
                for (String entityKey : entityKeyList) {
                        validateKeyLength(entityKey);
                        // Per section 4.4: keys must be case-folded
                        entityKey = entityKey.toLowerCase();
                        entityKeyList.set(i, entityKey);

                        boolean inserted = dupCheck.add(entityKey);
                        if (!inserted) {
                                throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.DuplicateKey", entityKey));
                        }

                        Object obj = em.find(org.apache.juddi.model.Tmodel.class, entityKey);
                        if (obj == null) {
                                throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.TModelNotFound", entityKey));
                        }

                        AccessCheck(obj, entityKey);

                        i++;
                }
        }
        
        private void AccessCheck(Object obj, String entityKey) throws UserMismatchException{
                        boolean accessCheck=false; //assume access denied
                        if (publisher.isOwner((UddiEntity) obj)){
                           accessCheck=true;
                                
                        }
                        if (((Publisher) publisher).isAdmin() && 
                                nodeID.equals(((UddiEntity) obj).getNodeId())){
                           accessCheck=true;
                        }
                
                        if (!accessCheck ) {
                                throw new UserMismatchException(new ErrorMessage("errors.usermismatch.InvalidOwner", entityKey));
                        }

        }

        public void validateDeletePublisherAssertions(EntityManager em, DeletePublisherAssertions body) throws DispositionReportFaultMessage {

                // No null input
                if (body == null) {
                        throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
                }

                // No null or empty list
                List<org.uddi.api_v3.PublisherAssertion> entityList = body.getPublisherAssertion();
                if (entityList == null || entityList.size() == 0) {
                        throw new AssertionNotFoundException(new ErrorMessage("errors.pubassertion.NoPubAssertions"));
                }

                for (org.uddi.api_v3.PublisherAssertion entity : entityList) {
                        validateKeyLength(entity.getFromKey());
                        validateKeyLength(entity.getToKey());
                        validatePublisherAssertion(em, entity);

                        org.apache.juddi.model.PublisherAssertionId pubAssertionId = new org.apache.juddi.model.PublisherAssertionId(entity.getFromKey(), entity.getToKey());
                        Object obj = em.find(org.apache.juddi.model.PublisherAssertion.class, pubAssertionId);
                        if (obj == null) {
                                throw new AssertionNotFoundException(new ErrorMessage("errors.pubassertion.AssertionNotFound", entity.getFromKey() + ", " + entity.getToKey()));
                        } else {
                                org.apache.juddi.model.PublisherAssertion pubAssertion = (org.apache.juddi.model.PublisherAssertion) obj;
                                org.uddi.api_v3.KeyedReference keyedRef = entity.getKeyedReference();
                                if (keyedRef == null) {
                                        throw new AssertionNotFoundException(new ErrorMessage("errors.pubassertion.AssertionNotFound", entity.getFromKey() + ", " + entity.getToKey()));
                                }

                                if (!pubAssertion.getTmodelKey().equalsIgnoreCase(keyedRef.getTModelKey())
                                        || !pubAssertion.getKeyName().equalsIgnoreCase(keyedRef.getKeyName())
                                        || !pubAssertion.getKeyValue().equalsIgnoreCase(keyedRef.getKeyValue())) {
                                        throw new AssertionNotFoundException(new ErrorMessage("errors.pubassertion.AssertionNotFound", entity.getFromKey() + ", " + entity.getToKey()));
                                }

                        }

                }
        }

        public void validateSaveBusiness(EntityManager em, SaveBusiness body, Configuration config) throws DispositionReportFaultMessage {

                if (config == null) {
                        try {
                                config = AppConfig.getConfiguration();
                        } catch (ConfigurationException ce) {
                                log.error("Could not optain config. " + ce.getMessage(), ce);
                        }
                }
                // No null input
                if (body == null) {
                        throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
                }

                // No null or empty list
                List<org.uddi.api_v3.BusinessEntity> entityList = body.getBusinessEntity();
                if (entityList == null || entityList.size() == 0) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.savebusiness.NoInput"));
                }

                for (org.uddi.api_v3.BusinessEntity entity : entityList) {
                        validateBusinessEntity(em, entity, config);
                }
        }

        public void validateSaveBusinessMax(EntityManager em) throws DispositionReportFaultMessage {

                //Obtain the maxSettings for this publisher or get the defaults
                Publisher publisher = em.find(Publisher.class, getPublisher().getAuthorizedName());
                Integer maxBusinesses = publisher.getMaxBusinesses();
                try {
                        if (maxBusinesses == null) {
                                if (AppConfig.getConfiguration().containsKey(Property.JUDDI_MAX_BUSINESSES_PER_PUBLISHER)) {
                                        maxBusinesses = AppConfig.getConfiguration().getInteger(Property.JUDDI_MAX_BUSINESSES_PER_PUBLISHER, -1);
                                } else {
                                        maxBusinesses = -1;
                                }
                        }
                } catch (ConfigurationException e) {
                        log.error(e.getMessage(), e);
                        maxBusinesses = -1; //in case the configuration is not available
                }
                //if we have the maxBusinesses set for this publisher then we need to make sure we did not exceed it.
                if (maxBusinesses > 0) {
                        //get the businesses owned by this publisher
                        List<?> businessKeysFound = FindBusinessByPublisherQuery.select(em, null, publisher, null);
                        if (businessKeysFound != null && businessKeysFound.size() > maxBusinesses) {
                                throw new MaxEntitiesExceededException(new ErrorMessage("errors.save.maxBusinessesExceeded"));
                        }
                }

        }

        public void validateSaveService(EntityManager em, SaveService body, Configuration config) throws DispositionReportFaultMessage {

                if (config == null) {
                        try {
                                config = AppConfig.getConfiguration();
                        } catch (ConfigurationException ce) {
                                log.error("Could not optain config. " + ce.getMessage(), ce);
                        }
                }
                // No null input
                if (body == null) {
                        throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
                }

                // No null or empty list
                List<org.uddi.api_v3.BusinessService> entityList = body.getBusinessService();
                if (entityList == null || entityList.size() == 0) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.saveservice.NoInput"));
                }

                for (org.uddi.api_v3.BusinessService entity : entityList) {
                        // Entity specific data validation
                        validateBusinessService(em, entity, null, config);
                }
        }

        public void validateSaveServiceMax(EntityManager em, String businessKey) throws DispositionReportFaultMessage {

                //Obtain the maxSettings for this publisher or get the defaults
                Publisher publisher = em.find(Publisher.class, getPublisher().getAuthorizedName());
                Integer maxServices = publisher.getMaxBusinesses();
                try {
                        if (maxServices == null) {
                                if (AppConfig.getConfiguration().containsKey(Property.JUDDI_MAX_SERVICES_PER_BUSINESS)) {
                                        maxServices = AppConfig.getConfiguration().getInteger(Property.JUDDI_MAX_SERVICES_PER_BUSINESS, -1);
                                } else {
                                        maxServices = -1;
                                }
                        }
                } catch (ConfigurationException e) {
                        log.error(e.getMessage(), e);
                        maxServices = -1; //incase the configuration isn't available
                }
                //if we have the maxServices set for a business then we need to make sure we did not exceed it.
                if (maxServices > 0) {
                        //get the businesses owned by this publisher
                        org.apache.juddi.model.BusinessEntity modelBusinessEntity = em.find(org.apache.juddi.model.BusinessEntity.class, businessKey);
                        if (modelBusinessEntity.getBusinessServices() != null && modelBusinessEntity.getBusinessServices().size() > maxServices) {
                                throw new MaxEntitiesExceededException(new ErrorMessage("errors.save.maxServicesExceeded"));
                        }
                }
        }

        public void validateSaveBinding(EntityManager em, SaveBinding body, Configuration config) throws DispositionReportFaultMessage {

                if (config == null) {
                        try {
                                config = AppConfig.getConfiguration();
                        } catch (ConfigurationException ce) {
                                log.error("Could not optain config. " + ce.getMessage(), ce);
                        }
                }
                // No null input
                if (body == null) {
                        throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
                }

                // No null or empty list
                List<org.uddi.api_v3.BindingTemplate> entityList = body.getBindingTemplate();
                if (entityList == null || entityList.size() == 0) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.savebinding.NoInput"));
                }

                for (org.uddi.api_v3.BindingTemplate entity : entityList) {
                        validateBindingTemplate(em, entity, null, config);
                }
        }

        public void validateSaveBindingMax(EntityManager em, String serviceKey) throws DispositionReportFaultMessage {

                //Obtain the maxSettings for this publisher or get the defaults
                Publisher publisher = em.find(Publisher.class, getPublisher().getAuthorizedName());
                Integer maxBindings = publisher.getMaxBindingsPerService();
                try {
                        if (maxBindings == null) {
                                if (AppConfig.getConfiguration().containsKey(Property.JUDDI_MAX_BINDINGS_PER_SERVICE)) {
                                        maxBindings = AppConfig.getConfiguration().getInteger(Property.JUDDI_MAX_BINDINGS_PER_SERVICE, -1);
                                } else {
                                        maxBindings = -1;
                                }
                        }
                } catch (ConfigurationException e) {
                        log.error(e.getMessage(), e);
                        maxBindings = -1; //incase the config isn't available
                }
                //if we have the maxBindings set for a service then we need to make sure we did not exceed it.
                if (maxBindings > 0) {
                        //get the bindings owned by this service
                        org.apache.juddi.model.BusinessService modelBusinessService = em.find(org.apache.juddi.model.BusinessService.class, serviceKey);
                        if (modelBusinessService.getBindingTemplates() != null && modelBusinessService.getBindingTemplates().size() > maxBindings) {
                                throw new MaxEntitiesExceededException(new ErrorMessage("errors.save.maxBindingsExceeded"));
                        }
                }
        }

        public void validateSaveTModel(EntityManager em, SaveTModel body, Configuration config) throws DispositionReportFaultMessage {

                if (config == null) {
                        try {
                                config = AppConfig.getConfiguration();
                        } catch (ConfigurationException ce) {
                                log.error("Could not optain config. " + ce.getMessage(), ce);
                        }
                }
                // No null input
                if (body == null) {
                        throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
                }

                // No null or empty list
                List<org.uddi.api_v3.TModel> entityList = body.getTModel();
                if (entityList == null || entityList.size() == 0) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.savetmodel.NoInput"));
                }

                for (org.uddi.api_v3.TModel entity : entityList) {
                        validateTModel(em, entity, config);
                }
        }

        public void validateSaveTModelMax(EntityManager em) throws DispositionReportFaultMessage {

                //Obtain the maxSettings for this publisher or get the defaults
                Publisher publisher = em.find(Publisher.class, getPublisher().getAuthorizedName());
                Integer maxTModels = publisher.getMaxTmodels();
                try {
                        if (maxTModels == null) {
                                if (AppConfig.getConfiguration().containsKey(Property.JUDDI_MAX_TMODELS_PER_PUBLISHER)) {
                                        maxTModels = AppConfig.getConfiguration().getInteger(Property.JUDDI_MAX_TMODELS_PER_PUBLISHER, -1);
                                } else {
                                        maxTModels = -1;
                                }
                        }
                } catch (ConfigurationException e) {
                        log.error(e.getMessage(), e);
                        maxTModels = -1; //incase the config isn't available
                }
                //if we have the TModels set for a publisher then we need to make sure we did not exceed it.
                if (maxTModels > 0) {
                        //get the tmodels owned by this publisher
                        List<?> tmodelKeysFound = FindTModelByPublisherQuery.select(em, null, publisher, null);
                        if (tmodelKeysFound != null && tmodelKeysFound.size() > maxTModels) {
                                throw new MaxEntitiesExceededException(new ErrorMessage("errors.save.maxTModelsExceeded"));
                        }
                }
        }

        public void validateAddPublisherAssertions(EntityManager em, AddPublisherAssertions body) throws DispositionReportFaultMessage {

                // No null input
                if (body == null) {
                        throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
                }

                // No null or empty list
                List<org.uddi.api_v3.PublisherAssertion> entityList = body.getPublisherAssertion();
                if (entityList == null || entityList.size() == 0) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.addpublisherassertions.NoInput"));
                }

                for (org.uddi.api_v3.PublisherAssertion entity : entityList) {
                        validatePublisherAssertion(em, entity);
                }
        }

        public void validateSetPublisherAssertions(EntityManager em, Holder<List<org.uddi.api_v3.PublisherAssertion>> body) throws DispositionReportFaultMessage {

                // No null input
                if (body == null) {
                        throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
                }

                // Assertion list can be null or empty - it signifies that publisher is deleting all their assertions
                List<org.uddi.api_v3.PublisherAssertion> entityList = body.value;
                if (entityList != null && entityList.size() > 0) {

                        for (org.uddi.api_v3.PublisherAssertion entity : entityList) {
                                validatePublisherAssertion(em, entity);
                        }
                }
        }

        void validateNotSigned(org.uddi.api_v3.BusinessEntity item) throws ValueNotAllowedException {
                if (item == null) {
                        return;
                }
                if (item.getBusinessKey() == null && !item.getSignature().isEmpty()) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.entity.SignedButNoKey", "businessKey"));
                }
                if (item.getBusinessServices() != null && !item.getSignature().isEmpty()) {
                        for (int i = 0; i < item.getBusinessServices().getBusinessService().size(); i++) {
                                if (item.getBusinessServices().getBusinessService().get(i).getBusinessKey() == null
                                        || item.getBusinessServices().getBusinessService().get(i).getBusinessKey().length() == 0) {
                                        throw new ValueNotAllowedException(new ErrorMessage("errors.entity.SignedButNoKey", "business/Service(" + i + ")/businessKey"));
                                }
                                if (item.getBusinessServices().getBusinessService().get(i).getServiceKey() == null
                                        || item.getBusinessServices().getBusinessService().get(i).getServiceKey().length() == 0) {
                                        throw new ValueNotAllowedException(new ErrorMessage("errors.entity.SignedButNoKey", "business/Service(" + i + ")/serviceKey"));
                                }
                                if (item.getBusinessServices().getBusinessService().get(i).getBindingTemplates() != null) {
                                        for (int k = 0; k < item.getBusinessServices().getBusinessService().get(i).getBindingTemplates().getBindingTemplate().size(); k++) {
                                                if (item.getBusinessServices().getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getBindingKey() == null
                                                        || item.getBusinessServices().getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getBindingKey().length() == 0) {
                                                        throw new ValueNotAllowedException(new ErrorMessage("errors.entity.SignedButNoKey", "business/Service(" + i + ")/bindingTemplate)" + k + ")/bindingKey"));
                                                }
                                        }
                                }
                        }
                }
        }

        void validateNotSigned(org.uddi.api_v3.BindingTemplate item) throws ValueNotAllowedException {
                if (item == null) {
                        return;
                }
                if (item.getBindingKey() == null && !item.getSignature().isEmpty()) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.entity.SignedButNoKey", "bindingKey"));
                }
                if (item.getServiceKey() == null && !item.getSignature().isEmpty()) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.entity.SignedButNoKey", "serviceKey"));
                }
        }

        void validateNotSigned(org.uddi.api_v3.TModel item) throws ValueNotAllowedException {
                if (item == null) {
                        return;
                }
                if (item.getTModelKey() == null && !item.getSignature().isEmpty()) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.entity.SignedButNoKey", "tModelKey"));
                }
        }

        void validateNotSigned(org.uddi.api_v3.BusinessService item) throws ValueNotAllowedException {
                if (item == null) {
                        return;
                }
                if (item.getBusinessKey() == null && !item.getSignature().isEmpty()) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.entity.SignedButNoKey", "businessKey"));
                }
                if (item.getServiceKey() == null && !item.getSignature().isEmpty()) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.entity.SignedButNoKey", "serviceKey"));
                }
                //if i'm signed and a key isn't defined in a bt
                if (item.getBindingTemplates() != null && !item.getSignature().isEmpty()) {
                        for (int i = 0; i < item.getBindingTemplates().getBindingTemplate().size(); i++) {
                                if (item.getBindingTemplates().getBindingTemplate().get(i).getBindingKey() == null
                                        || item.getBindingTemplates().getBindingTemplate().get(i).getBindingKey().length() == 0) {
                                        throw new ValueNotAllowedException(new ErrorMessage("errors.entity.SignedButNoKey", "businessService/bindingTemplate(" + i + ")/bindingKey"));
                                }
                        }
                }
        }

        public void validateBusinessEntity(EntityManager em, org.uddi.api_v3.BusinessEntity businessEntity, Configuration config) throws DispositionReportFaultMessage {

                // A supplied businessEntity can't be null
                if (businessEntity == null) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.businessentity.NullInput"));
                }

                boolean entityExists = false;
                validateNotSigned(businessEntity);
                String entityKey = businessEntity.getBusinessKey();
                if (entityKey == null || entityKey.length() == 0) {
                        KeyGenerator keyGen = KeyGeneratorFactory.getKeyGenerator();
                        entityKey = keyGen.generate();
                        businessEntity.setBusinessKey(entityKey);
                } else {
                        // Per section 4.4: keys must be case-folded
                        entityKey = entityKey.toLowerCase();
                        businessEntity.setBusinessKey(entityKey);
                        validateKeyLength(entityKey);
                        Object obj = em.find(org.apache.juddi.model.BusinessEntity.class, entityKey);
                        if (obj != null) {
                                entityExists = true;

                                // Make sure publisher owns this entity.
                                AccessCheck(obj, entityKey);
                                
                        } else {
                                // Inside this block, we have a key proposed by the publisher on a new entity

                                // Validate key and then check to see that the proposed key is valid for this publisher
                                ValidateUDDIKey.validateUDDIv3Key(entityKey);
                                if (!publisher.isValidPublisherKey(em, entityKey)) {
                                        throw new KeyUnavailableException(new ErrorMessage("errors.keyunavailable.BadPartition", entityKey));
                                }

                        }
                }

                if (!entityExists) {
                        // Check to make sure key isn't used by another entity.
                        if (!isUniqueKey(em, entityKey)) {
                                throw new KeyUnavailableException(new ErrorMessage("errors.keyunavailable.KeyExists", entityKey));
                        }
                }

                // was TODO: validate "checked" categories or category groups (see section 5.2.3 of spec)? optional to support
                //covered by ref integrity checks

                validateNames(businessEntity.getName());
                validateDiscoveryUrls(businessEntity.getDiscoveryURLs());
                validateContacts(businessEntity.getContacts(), config);
                validateCategoryBag(businessEntity.getCategoryBag(), config, false);
                validateIdentifierBag(businessEntity.getIdentifierBag(), config, false);
                validateDescriptions(businessEntity.getDescription());
                validateBusinessServices(em, businessEntity.getBusinessServices(), businessEntity, config);

        }

        public void validateBusinessServices(EntityManager em, org.uddi.api_v3.BusinessServices businessServices, org.uddi.api_v3.BusinessEntity parent, Configuration config)
                throws DispositionReportFaultMessage {
                // Business services is optional
                if (businessServices == null) {
                        return;
                }
                List<org.uddi.api_v3.BusinessService> businessServiceList = businessServices.getBusinessService();
                if (businessServiceList == null || businessServiceList.size() == 0) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.businessservices.NoInput"));
                }

                for (org.uddi.api_v3.BusinessService businessService : businessServiceList) {
                        validateBusinessService(em, businessService, parent, config);
                }

        }

        public void validateBusinessService(EntityManager em, org.uddi.api_v3.BusinessService businessService, org.uddi.api_v3.BusinessEntity parent, Configuration config)
                throws DispositionReportFaultMessage {

                // A supplied businessService can't be null
                if (businessService == null) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.businessservice.NullInput"));
                }

                validateNotSigned(businessService);
                // Retrieve the service's passed key
                String entityKey = businessService.getServiceKey();
                if (entityKey != null && entityKey.length() > 0) {
                        // Per section 4.4: keys must be case-folded
                        entityKey = entityKey.toLowerCase();
                        validateKeyLength(entityKey);
                        businessService.setServiceKey(entityKey);
                }

                // The parent key is either supplied or provided by the higher call to the parent entity save.  If the passed-in parent's business key differs from 
                // the (non-null) business key retrieved from the service, then we have a possible service projection.
                String parentKey = businessService.getBusinessKey();
                if (parentKey != null && parentKey.length() > 0) {
                        // Per section 4.4: keys must be case-folded
                        parentKey = parentKey.toLowerCase();
                        businessService.setBusinessKey(parentKey);
                }

                boolean isProjection = false;
                if (parent != null) {
                        if (parentKey != null && parentKey.length() > 0) {
                                if (!parentKey.equalsIgnoreCase(parent.getBusinessKey())) {
                                        // Possible projected service - if we have differing parent businesses but a service key was not provided, this is an error as it is not possible 
                                        // for the business that doesn't "own" the service to generate the key for it.
                                        if (entityKey == null || entityKey.length() == 0) {
                                                throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.ServiceKeyNotProvidedWithProjection", parentKey + ", " + parent.getBusinessKey()));
                                        }

                                        isProjection = true;
                                }
                        } else {
                                parentKey = parent.getBusinessKey();
                        }
                }

                // Projections don't require as rigorous testing as only the projected service's business key and service key are examined for validity.
                if (isProjection) {


                        Object obj = em.find(org.apache.juddi.model.BusinessService.class, entityKey);
                        // Can't project a service that doesn't exist!
                        if (obj == null) {
                                throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.ProjectedServiceNotFound", parentKey + ", " + entityKey));
                        } else {
                                // If the supplied business key doesn't match the existing service's business key, the projection is invalid.
                                org.apache.juddi.model.BusinessService bs = (org.apache.juddi.model.BusinessService) obj;
                                if (!businessService.getBusinessKey().equalsIgnoreCase(bs.getBusinessEntity().getEntityKey())) {
                                        throw new InvalidProjectionException(new ErrorMessage("errors.invalidprojection.ParentMismatch", businessService.getBusinessKey() + ", " + bs.getBusinessEntity().getEntityKey()));
                                }
                        }
                        obj = null;
                } else {
                        boolean entityExists = false;
                        if (entityKey == null || entityKey.length() == 0) {
                                KeyGenerator keyGen = KeyGeneratorFactory.getKeyGenerator();
                                entityKey = keyGen.generate();
                                businessService.setServiceKey(entityKey);
                        } else {

                                Object obj = em.find(org.apache.juddi.model.BusinessService.class, entityKey);
                                if (obj != null) {
                                        entityExists = true;

                                        org.apache.juddi.model.BusinessService bs = (org.apache.juddi.model.BusinessService) obj;

                                        // If the object exists, and the parentKey was not found to this point, then a save on an existing service with a blank
                                        // business key has occurred.  It is set here and added to the entity being saved - a necessary step for the object to be
                                        // persisted properly. (This condition makes some validation tests below unnecessary as the parent is "verified" but it's OK to
                                        // still run them).
                                        if (parentKey == null || parentKey.length() == 0) {
                                                parentKey = bs.getBusinessEntity().getEntityKey();
                                                businessService.setBusinessKey(parentKey);
                                        }

                                        
                                        // Make sure publisher owns this entity.
                                        AccessCheck(obj, entityKey);

                                        // If existing service trying to be saved has a different parent key, then we have a problem
                                        if (!parentKey.equalsIgnoreCase(bs.getBusinessEntity().getEntityKey())) {
                                                // if both businesses are owned by this publisher then we allow it.
                                                // we already check the current business is owned, lets see if the old one is too
                                                if (!publisher.isOwner(bs.getBusinessEntity())) {
                                                        throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.businessservice.ParentMismatch", parentKey + ", " + bs.getBusinessEntity().getEntityKey()));
                                                } else {
                                                        if (log.isDebugEnabled()) {
                                                                log.debug("Services moved from business " + bs.getBusinessEntity() + " to " + businessService.getBusinessKey());
                                                        }
                                                }
                                        }

                                } else {
                                        // Inside this block, we have a key proposed by the publisher on a new entity

                                        // Validate key and then check to see that the proposed key is valid for this publisher
                                        ValidateUDDIKey.validateUDDIv3Key(entityKey);
                                        if (!publisher.isValidPublisherKey(em, entityKey)) {
                                                throw new KeyUnavailableException(new ErrorMessage("errors.keyunavailable.BadPartition", entityKey));
                                        }

                                }

                        }

                        // Parent key must be passed if this is a new entity
                        if (!entityExists) {
                                if (parentKey == null || parentKey.length() == 0) {
                                        throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.ParentBusinessNotFound", parentKey));
                                }
                        }

                        // If parent key IS passed, whether new entity or not, it must be valid.  Additionally, the current publisher must be the owner of the parent.  Note that
                        // if a parent ENTITY was passed in, then we don't need to check for any of this since this is part of a higher call.
                        if (parentKey != null) {
                                if (parent == null) {
                                        Object parentTemp = em.find(org.apache.juddi.model.BusinessEntity.class, parentKey);
                                        if (parentTemp == null) {
                                                throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.ParentBusinessNotFound", parentKey));
                                        }

                                        // Make sure publisher owns this parent entity.
                                        AccessCheck(parentTemp, parentKey);
                                       // if (!publisher.isOwner((UddiEntity) parentTemp)) {
                                        //        throw new UserMismatchException(new ErrorMessage("errors.usermismatch.InvalidOwnerParent", parentKey));
                                        //}
                                }
                        }

                        if (!entityExists) {
                                // Check to make sure key isn't used by another entity.
                                if (!isUniqueKey(em, entityKey)) {
                                        throw new KeyUnavailableException(new ErrorMessage("errors.keyunavailable.KeyExists", entityKey));
                                }
                        }

                        // TODO: validate "checked" categories or category groups (see section 5.2.3 of spec)? optional to support

                        validateNames(businessService.getName());
                        validateCategoryBag(businessService.getCategoryBag(), config, false);
                        validateDescriptions(businessService.getDescription());
                        validateBindingTemplates(em, businessService.getBindingTemplates(), businessService, config);
                }

        }

        public void validateBindingTemplates(EntityManager em, org.uddi.api_v3.BindingTemplates bindingTemplates, org.uddi.api_v3.BusinessService parent, Configuration config)
                throws DispositionReportFaultMessage {
                // Binding templates is optional
                if (bindingTemplates == null) {
                        return;
                }

                List<org.uddi.api_v3.BindingTemplate> bindingTemplateList = bindingTemplates.getBindingTemplate();
                if (bindingTemplateList == null || bindingTemplateList.size() == 0) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.bindingtemplates.NoInput"));
                }

                for (org.uddi.api_v3.BindingTemplate bindingTemplate : bindingTemplateList) {
                        validateBindingTemplate(em, bindingTemplate, parent, config);
                }

        }

        public void validateBindingTemplate(EntityManager em, org.uddi.api_v3.BindingTemplate bindingTemplate,
                org.uddi.api_v3.BusinessService parent, Configuration config)
                throws DispositionReportFaultMessage {

                // A supplied bindingTemplate can't be null
                if (bindingTemplate == null) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.bindingtemplate.NullInput"));
                }

                // Retrieve the binding's passed key
                String entityKey = bindingTemplate.getBindingKey();
                if (entityKey != null && entityKey.length() > 0) {
                        // Per section 4.4: keys must be case-folded
                        entityKey = entityKey.toLowerCase();
                        bindingTemplate.setBindingKey(entityKey);
                        validateKeyLength(entityKey);
                }

                // The parent key is either supplied or provided by the higher call to the parent entity save.  If it is provided in both instances, if they differ, an 
                // error occurs.
                String parentKey = bindingTemplate.getServiceKey();
                if (parentKey != null && parentKey.length() > 0) {
                        // Per section 4.4: keys must be case-folded
                        parentKey = parentKey.toLowerCase();
                        bindingTemplate.setServiceKey(parentKey);
                }

                if (parent != null) {
                        if (parentKey != null && parentKey.length() > 0) {
                                if (!parentKey.equalsIgnoreCase(parent.getServiceKey())) {
                                        throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.bindingtemplate.ParentMismatch", parentKey + ", " + parent.getBusinessKey()));
                                }
                        } else {
                                parentKey = parent.getServiceKey();
                        }
                }

                boolean entityExists = false;
                if (entityKey == null || entityKey.length() == 0) {
                        validateNotSigned(bindingTemplate);
                        KeyGenerator keyGen = KeyGeneratorFactory.getKeyGenerator();
                        entityKey = keyGen.generate();
                        bindingTemplate.setBindingKey(entityKey);
                } else {

                        Object obj = em.find(org.apache.juddi.model.BindingTemplate.class, entityKey);
                        if (obj != null) {
                                entityExists = true;

                                org.apache.juddi.model.BindingTemplate bt = (org.apache.juddi.model.BindingTemplate) obj;

                                // If the object exists, and the parentKey was not found to this point, then a save on an existing binding with a blank
                                // service key has occurred.  It is set here and added to the entity being saved - a necessary step for the object to be
                                // persisted properly. (This condition makes some validation tests below unnecessary as the parent is "verified" but it's OK to
                                // still run them).
                                if (parentKey == null || parentKey.length() == 0) {
                                        parentKey = bt.getBusinessService().getEntityKey();
                                        bindingTemplate.setServiceKey(parentKey);
                                }

                                // If existing binding trying to be saved has a different parent key, then we have a problem
                                // TODO: moving bindings is allowed according to spec?
                                if (!parentKey.equalsIgnoreCase(bt.getBusinessService().getEntityKey())) {
                                        throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.bindingtemplate.ParentMismatch", parentKey + ", " + bt.getBusinessService().getEntityKey()));
                                }

                                // Make sure publisher owns this entity.
                                 AccessCheck(obj, entityKey);
                                //if (!publisher.isOwner((UddiEntity) obj)&& !((Publisher) publisher).isAdmin()) {
//                                        throw new UserMismatchException(new ErrorMessage("errors.usermismatch.InvalidOwner", entityKey));
  //                              }

                        } else {
                                // Inside this block, we have a key proposed by the publisher on a new entity

                                // Validate key and then check to see that the proposed key is valid for this publisher
                                ValidateUDDIKey.validateUDDIv3Key(entityKey);
                                if (!publisher.isValidPublisherKey(em, entityKey)) {
                                        throw new KeyUnavailableException(new ErrorMessage("errors.keyunavailable.BadPartition", entityKey));
                                }

                        }

                }

                // Parent key must be passed if this is a new entity
                if (!entityExists) {
                        if (parentKey == null || parentKey.length() == 0) {
                                throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.ParentServiceNotFound", parentKey));
                        }
                }

                // If parent key IS passed, whether new entity or not, it must be valid.  Additionally, the current publisher must be the owner of the parent.  Note that
                // if a parent ENTITY was passed in, then we don't need to check for any of this since this is part of a higher call.
                if (parentKey != null) {
                        if (parent == null) {
                                Object parentTemp = em.find(org.apache.juddi.model.BusinessService.class, parentKey);
                                if (parentTemp == null) {
                                        throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.ParentBusinessNotFound", parentKey));
                                }

                                // Make sure publisher owns this parent entity.
                                AccessCheck(parentTemp, parentKey);
//                                if (!publisher.isOwner((UddiEntity) parentTemp)) {
//                                        throw new UserMismatchException(new ErrorMessage("errors.usermismatch.InvalidOwnerParent", parentKey));
//                                }

                        }
                }

                if (!entityExists) {
                        // Check to make sure key isn't used by another entity.
                        if (!isUniqueKey(em, entityKey)) {
                                throw new KeyUnavailableException(new ErrorMessage("errors.keyunavailable.KeyExists", entityKey));
                        }
                }

                //was TODO validate "checked" categories or category groups (see section 5.2.3 of spec)? optional to support

                //at least one must be defined
                if (bindingTemplate.getAccessPoint() == null && bindingTemplate.getHostingRedirector() == null) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.bindingtemplate.NoAccessPoint"));
                }
                //but not both
                if (bindingTemplate.getAccessPoint() != null && bindingTemplate.getHostingRedirector() != null) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.bindingtemplate.NoAccessPoint"));
                }
                validateCategoryBag(bindingTemplate.getCategoryBag(), config, false);
                validateTModelInstanceDetails(bindingTemplate.getTModelInstanceDetails(), config, false);
                validateAccessPoint(em, bindingTemplate.getAccessPoint(), config);
                validateDescriptions(bindingTemplate.getDescription());
                validateHostingRedirector(em, bindingTemplate.getHostingRedirector(), config);

        }

        public void validateTModel(EntityManager em, org.uddi.api_v3.TModel tModel, Configuration config) throws DispositionReportFaultMessage {
                // A supplied tModel can't be null
                if (tModel == null) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.tmodel.NullInput"));
                }

                boolean entityExists = false;
                String entityKey = tModel.getTModelKey();
                if (entityKey == null || entityKey.length() == 0) {
                        KeyGenerator keyGen = KeyGeneratorFactory.getKeyGenerator();
                        entityKey = keyGen.generate();
                        validateNotSigned(tModel);
                        tModel.setTModelKey(entityKey);
                } else {
                        // Per section 4.4: keys must be case-folded
                        entityKey = entityKey.toLowerCase();
                        tModel.setTModelKey(entityKey);

                        Object obj = em.find(org.apache.juddi.model.Tmodel.class, entityKey);
                        if (obj != null) {
                                entityExists = true;

                                // Make sure publisher owns this entity.
                                AccessCheck(obj, entityKey);
                                //if (!publisher.isOwner((UddiEntity) obj)&& !((Publisher) publisher).isAdmin()) {
                                //        throw new UserMismatchException(new ErrorMessage("errors.usermismatch.InvalidOwner", entityKey));
                               // }
                        } else {
                                // Inside this block, we have a key proposed by the publisher on a new entity

                                // First test to see if this is a Key Generator tModel. The keyGenerator suffix appearing in the key is the indicator, since this is not
                                // allowed *unless* it's a key generator.
                                if (entityKey.toUpperCase().contains(KeyGenerator.KEYGENERATOR_SUFFIX.toUpperCase())) {
                                        ValidateUDDIKey.validateUDDIv3KeyGeneratorTModel(tModel);

                                        // The root publisher is only allowed one key generator.  This is published in the installation.
                                        String rootPublisherStr = "root";
                                        try {
                                                rootPublisherStr = AppConfig.getConfiguration().getString(Property.JUDDI_ROOT_PUBLISHER);
                                        } catch (ConfigurationException ce) {
                                                log.error("Could not read the root publisher setting in the configuration.");
                                        }
                                        if (publisher.getAuthorizedName().equals(rootPublisherStr)) {
                                                throw new FatalErrorException(new ErrorMessage("errors.tmodel.keygenerator.RootKeyGen"));
                                        }

                                        // It's a valid Key Generator, but is it available for this publisher?
                                        if (!publisher.isKeyGeneratorAvailable(em, entityKey)) {
                                                throw new KeyUnavailableException(new ErrorMessage("errors.keyunavailable.BadPartition", entityKey));
                                        }

                                } else {
                                        // If not a key generator, then simply validate key and then check to see that the proposed key is valid for this publisher
                                        ValidateUDDIKey.validateUDDIv3Key(entityKey);
                                        if (!publisher.isValidPublisherKey(em, entityKey)) {
                                                throw new KeyUnavailableException(new ErrorMessage("errors.keyunavailable.BadPartition", entityKey));
                                        }
                                }
                        }
                }

                if (!entityExists) {
                        // Check to make sure key isn't used by another entity.
                        if (!isUniqueKey(em, entityKey)) {
                                throw new KeyUnavailableException(new ErrorMessage("errors.keyunavailable.KeyExists", entityKey));
                        }
                }
                validateKeyLength(entityKey);


                // TODO: validate "checked" categories or category groups (see section 5.2.3 of spec)? optional to support

                if (tModel.getName() == null || tModel.getName().getValue() == null
                        || tModel.getName().getValue().equals("")) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.tmodel.NoName"));
                }

                validateCategoryBag(tModel.getCategoryBag(), config, false);
                validateIdentifierBag(tModel.getIdentifierBag(), config, false);
                validateDescriptions(tModel.getDescription());
                validateNameLength(tModel.getName().getValue());
                validateLang(tModel.getName().getLang());
                List<org.uddi.api_v3.OverviewDoc> overviewDocList = tModel.getOverviewDoc();
                if (overviewDocList != null) {
                        for (org.uddi.api_v3.OverviewDoc overviewDoc : overviewDocList) {
                                validateOverviewDoc(overviewDoc);
                        }
                }

        }

        public void validatePublisherAssertion(EntityManager em, org.uddi.api_v3.PublisherAssertion pubAssertion) throws DispositionReportFaultMessage {
                // A supplied publisher assertion can't be null
                if (pubAssertion == null) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.pubassertion.NullInput"));
                }

                // The keyedRef must not be blank and every field must contain data.
                org.uddi.api_v3.KeyedReference keyedRef = pubAssertion.getKeyedReference();
                if (keyedRef == null
                        || keyedRef.getTModelKey() == null || keyedRef.getTModelKey().length() == 0
                        || keyedRef.getKeyName() == null || keyedRef.getKeyName().length() == 0
                        || keyedRef.getKeyValue() == null || keyedRef.getKeyValue().length() == 0) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.pubassertion.BlankKeyedRef"));
                }

                String fromKey = pubAssertion.getFromKey();
                if (fromKey == null || fromKey.length() == 0) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.pubassertion.BlankFromKey"));
                }

                String toKey = pubAssertion.getToKey();
                if (toKey == null || toKey.length() == 0) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.pubassertion.BlankToKey"));
                }

                if (fromKey.equalsIgnoreCase(toKey)) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.pubassertion.SameBusinessKey"));
                }

                // Per section 4.4: keys must be case-folded
                fromKey = fromKey.toLowerCase();
                pubAssertion.setFromKey(fromKey);
                toKey = toKey.toLowerCase();
                pubAssertion.setToKey(toKey);
                validateKeyLength(toKey);
                validateKeyLength(fromKey);
                Object fromObj = em.find(org.apache.juddi.model.BusinessEntity.class, fromKey);
                if (fromObj == null) {
                        throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.BusinessNotFound", fromKey));
                }

                Object toObj = em.find(org.apache.juddi.model.BusinessEntity.class, pubAssertion.getToKey());
                if (toObj == null) {
                        throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.BusinessNotFound", toKey));
                }

                if (!publisher.isOwner((UddiEntity) fromObj) && !publisher.isOwner((UddiEntity) toObj)) {
                        throw new UserMismatchException(new ErrorMessage("errors.pubassertion.UserMismatch", fromKey + " & " + toKey));
                }

                try {
                        validateKeyedReference(pubAssertion.getKeyedReference(), AppConfig.getConfiguration(), false);
                } catch (ConfigurationException ce) {
                        log.error("Could not optain config. " + ce.getMessage(), ce);
                }
        }

        public void validateNames(List<org.uddi.api_v3.Name> names) throws DispositionReportFaultMessage {
                // At least one name is required
                if (names == null || names.size() == 0) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.names.NoInput"));
                }

                for (Name n : names) {
                        if (n.getValue() == null || n.getValue().length() == 0) {
                                throw new ValueNotAllowedException(new ErrorMessage("errors.names.NoValue"));
                        }
                        validateNameLength(n.getValue());
                        validateLang(n.getLang());
                }

        }

        public void validateContacts(org.uddi.api_v3.Contacts contacts, Configuration config) throws DispositionReportFaultMessage {
                // Contacts is optional
                if (contacts == null) {
                        return;
                }

                // If contacts do exist, at least one contact is required
                List<org.uddi.api_v3.Contact> contactList = contacts.getContact();
                if (contactList == null || contactList.size() == 0) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.contacts.NoInput"));
                }

                for (org.uddi.api_v3.Contact contact : contactList) {
                        validateContact(contact, config);
                }

        }

        public void validateContact(org.uddi.api_v3.Contact contact, Configuration config) throws DispositionReportFaultMessage {
                if (log.isDebugEnabled()) {
                        log.debug("validateContact");
                }
                // A supplied contact can't be null
                if (contact == null) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.contact.NullInput"));
                }

                // At least one personName is required
                List<org.uddi.api_v3.PersonName> pnameList = contact.getPersonName();
                if (pnameList == null || pnameList.size() == 0) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.contact.NoPersonName"));
                }
                for (org.uddi.api_v3.PersonName pn : pnameList) {
                        if (pn.getValue() == null || pn.getValue().length() == 0) {
                                throw new ValueNotAllowedException(new ErrorMessage("errors.contacts.NoPersonName"));
                        }
                        validateNameLength(pn.getValue());
                        validateLang(pn.getLang());
                }

                List<org.uddi.api_v3.Address> addressList = contact.getAddress();
                if (addressList != null) {
                        for (org.uddi.api_v3.Address address : addressList) {
                                if (address != null) {
                                        validateSortCode(address.getSortCode());
                                        validateKeyLength(address.getTModelKey());
                                        validateLang(address.getLang());
                                        validateUseType(address.getUseType());
                                        boolean checked = true;
                                        // Per section 4.4: keys must be case-folded
                                        if (address.getTModelKey() != null) {
                                                address.setTModelKey(address.getTModelKey().toLowerCase());

                                                checked = verifyTModelKeyExistsAndChecked(address.getTModelKey(), config);

                                        }
                                        if (address.getAddressLine() == null || address.getAddressLine().size() == 0) {
                                                throw new ValueNotAllowedException(new ErrorMessage("errors.contact.NoAddressLine"));
                                        }

                                        if (checked) {
                                                validateAddressLines(address.getAddressLine(), config);
                                        }
                                }
                        }
                }
                validateEmailAddress(contact.getEmail());
                validatePhone(contact.getPhone());
                validateDescriptions(contact.getDescription());
                validateUseType(contact.getUseType());
        }

        public void validateDiscoveryUrls(org.uddi.api_v3.DiscoveryURLs discUrls) throws DispositionReportFaultMessage {
                // Discovery Urls is optional
                if (discUrls == null) {
                        return;
                }

                // If discUrls does exist, it must have at least one element
                List<org.uddi.api_v3.DiscoveryURL> discUrlList = discUrls.getDiscoveryURL();
                if (discUrlList == null || discUrlList.size() == 0) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.discurls.NoInput"));
                }
                for (org.uddi.api_v3.DiscoveryURL url : discUrlList) {
                        if (url.getValue() == null || url.getValue().length() == 0) {
                                throw new ValueNotAllowedException(new ErrorMessage("errors.discurls.NoInput"));
                        }
                        validateDiscoveryUrlLength(url);
                }
        }

        public void validateCategoryBag(org.uddi.api_v3.CategoryBag categories, Configuration config, boolean isRoot) throws DispositionReportFaultMessage {

                // Category bag is optional
                if (categories == null) {
                        return;
                }

                // If category bag does exist, it must have at least one element
                List<KeyedReference> elems = categories.getKeyedReference();
                List<KeyedReferenceGroup> groups = categories.getKeyedReferenceGroup();
                if (groups.size() == 0 && elems.size() == 0) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.categorybag.NoInput"));
                }

                for (KeyedReferenceGroup group : groups) {
                        validateKeyedReferenceGroup(group, config, isRoot);
                }

                for (KeyedReference elem : elems) {
                        validateKeyedReference(elem, config, isRoot);
                }
        }

        public void validateIdentifierBag(org.uddi.api_v3.IdentifierBag identifiers, Configuration config, boolean isRoot) throws DispositionReportFaultMessage {

                // Identifier bag is optional
                if (identifiers == null) {
                        return;
                }

                // If category bag does exist, it must have at least one element
                List<org.uddi.api_v3.KeyedReference> keyedRefList = identifiers.getKeyedReference();
                if (keyedRefList == null || keyedRefList.size() == 0) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.identifierbag.NoInput"));
                }

                for (org.uddi.api_v3.KeyedReference keyedRef : keyedRefList) {
                        validateKeyedReference(keyedRef, config, isRoot);
                }
        }

        public void validateKeyedReferenceGroup(KeyedReferenceGroup krg, Configuration config, boolean isRoot) throws DispositionReportFaultMessage {
                // Keyed reference groups must contain a tModelKey
                if (log.isDebugEnabled()) {
                        log.debug("validateKeyedReferenceGroup");
                }
                if (krg.getTModelKey() == null || krg.getTModelKey().length() == 0) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.keyedreference.NoTModelKey"));
                }

                // Per section 4.4: keys must be case-folded
                String tmodelKey = krg.getTModelKey().toLowerCase();
                krg.setTModelKey(tmodelKey);
                validateKeyLength(tmodelKey);

                boolean checkRef = false;
                try {
                        checkRef = config.getBoolean(Property.JUDDI_ENFORCE_REFERENTIAL_INTEGRITY, false);
                } catch (Exception ex) {
                        log.warn("Error caught reading " + Property.JUDDI_ENFORCE_REFERENTIAL_INTEGRITY + " from config file", ex);
                }
                if (checkRef && !isRoot) {
                        this.verifyTModelKeyExists(tmodelKey);
                }

                boolean checked = verifyTModelKeyExistsAndChecked(tmodelKey, config);

                if (checked) {
                        List<KeyedReference> keyedRefs = krg.getKeyedReference();
                        // Should being empty raise an error?
                        if (keyedRefs != null && keyedRefs.size() > 0) {
                                for (KeyedReference keyedRef : keyedRefs) {
                                        validateKeyedReference(keyedRef, config, isRoot);
                                }
                        }
                }
        }

        /**
         *
         * @param kr
         * @param config
         * @param isRoot true during install time, otherwise false
         * @throws DispositionReportFaultMessage
         */
        public void validateKeyedReference(KeyedReference kr, Configuration config, boolean isRoot) throws DispositionReportFaultMessage {
                if (log.isDebugEnabled()) {
                        log.debug("validateKeyedReference");
                }
                String tmodelKey = kr.getTModelKey();

                if (tmodelKey == null || tmodelKey.length() == 0) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.keyedreference.NoTModelKey"));
                }

                // Per section 4.4: keys must be case-folded
                tmodelKey = tmodelKey.toLowerCase();
                kr.setTModelKey(tmodelKey);
                validateKeyLength(tmodelKey);

                if (kr.getKeyValue() == null || kr.getKeyValue().length() == 0) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.keyedreference.NoKeyValue"));
                }
                validateKeyValue(kr.getKeyValue());
                validateKeyName(kr.getKeyName());


                boolean checkRef = false;
                try {
                        checkRef = config.getBoolean(Property.JUDDI_ENFORCE_REFERENTIAL_INTEGRITY, false);
                } catch (Exception ex) {
                        log.warn("Error caught reading " + Property.JUDDI_ENFORCE_REFERENTIAL_INTEGRITY + " from config file", ex);
                }
                if (checkRef && !isRoot) {
                        this.verifyTModelKeyExists(tmodelKey);

                }


                String rootPublisherStr = config.getString(Property.JUDDI_ROOT_PUBLISHER);
                // Per section 6.2.2.1 of the specification, no publishers (except the root) are allowed to use the node categorization tmodelKey
                if (Constants.NODE_CATEGORY_TMODEL.equalsIgnoreCase(kr.getTModelKey())) {
                        if (!rootPublisherStr.equals(publisher.getAuthorizedName())) {
                                throw new ValueNotAllowedException(new ErrorMessage("errors.keyedreference.NodeCategoryTModel", Constants.NODE_CATEGORY_TMODEL));
                        }
                }
        }

        public void validateTModelInstanceDetails(org.uddi.api_v3.TModelInstanceDetails tmodelInstDetails, Configuration config, boolean isRoot) throws DispositionReportFaultMessage {
                if (log.isDebugEnabled()) {
                        log.debug("validateTModelInstanceDetails");
                }
                // tModel Instance Details is optional
                if (tmodelInstDetails == null) {
                        return;
                }

                // If tmodelInstDetails does exist, it must have at least one element
                List<org.uddi.api_v3.TModelInstanceInfo> tmodelInstInfoList = tmodelInstDetails.getTModelInstanceInfo();
                if (tmodelInstInfoList == null || tmodelInstInfoList.size() == 0) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.tmodelinstdetails.NoInput"));
                }

                for (org.uddi.api_v3.TModelInstanceInfo tmodelInstInfo : tmodelInstInfoList) {
                        validateTModelInstanceInfo(tmodelInstInfo, config, isRoot);
                }
        }

        public void validateTModelInstanceInfo(org.uddi.api_v3.TModelInstanceInfo tmodelInstInfo, Configuration config, boolean isRoot) throws DispositionReportFaultMessage {
                // tModel Instance Info can't be null
                if (tmodelInstInfo == null) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.tmodelinstinfo.NullInput"));
                }

                // TModel key is required
                if (tmodelInstInfo.getTModelKey() == null || tmodelInstInfo.getTModelKey().length() == 0) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.tmodelinstinfo.NoTModelKey"));
                }

                // Per section 4.4: keys must be case-folded
                tmodelInstInfo.setTModelKey((tmodelInstInfo.getTModelKey().toLowerCase()));

                boolean checkRef = false;
                try {
                        checkRef = config.getBoolean(Property.JUDDI_ENFORCE_REFERENTIAL_INTEGRITY, false);
                } catch (Exception ex) {
                        log.warn("Error caught reading " + Property.JUDDI_ENFORCE_REFERENTIAL_INTEGRITY + " from config file", ex);
                }
                if (checkRef && !isRoot) {
                        this.verifyTModelKeyExists(tmodelInstInfo.getTModelKey());
                }

                validateInstanceDetails(tmodelInstInfo.getInstanceDetails());
                if (log.isDebugEnabled()) {
                        log.debug("validateTModelInstanceInfo");
                }

                validateKeyLength(tmodelInstInfo.getTModelKey());
                validateDescriptions(tmodelInstInfo.getDescription());


        }

        public void validateInstanceDetails(org.uddi.api_v3.InstanceDetails instDetails) throws DispositionReportFaultMessage {
                // Instance Details is optional
                if (instDetails == null) {
                        return;
                }

                // At least one OverviewDoc or instanceParms must be supplied
                List<OverviewDoc> elems = instDetails.getOverviewDoc();
                if (instDetails.getInstanceParms() == null && elems.size() == 0) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.instdetails.NoOverviewOrParms"));
                }
                for (int i = 0; i < elems.size(); i++) {
                        validateDescriptions(elems.get(i).getDescription());
                        validateOverviewURL(elems.get(i).getOverviewURL());
                }
        }

        public void validateOverviewDoc(org.uddi.api_v3.OverviewDoc overviewDoc) throws DispositionReportFaultMessage {
                // OverviewDoc can't be null
                if (overviewDoc == null) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.overviewdoc.NullInput"));
                }

                // At least one description or overview URL must be supplied
                List<org.uddi.api_v3.Description> elems = overviewDoc.getDescription();
                if ((elems == null || elems.size() == 0) && overviewDoc.getOverviewURL() == null) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.overviewdoc.NoDescOrUrl"));
                }
                for (int i = 0; i < elems.size(); i++) {
                        validateLang(elems.get(i).getLang());
                        //TODO verify this is correct
                        validateURL(elems.get(i).getValue());
                }
        }

        public void validateRegisteredInfo(org.uddi.api_v3.GetRegisteredInfo body) throws DispositionReportFaultMessage {
                // No null input
                if (body == null) {
                        throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
                }

                // infoSelection is required
                if (body.getInfoSelection() == null) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.getregisteredinfo.NoInfoSelection"));
                }

        }

        /**
         * Publishing API functions are specific to jUDDI. Requires administrative privilege
         * @param em
         * @param body
         * @throws DispositionReportFaultMessage 
         */ 
        public void validateDeletePublisher(EntityManager em, DeletePublisher body) throws DispositionReportFaultMessage {

                // No null input
                if (body == null) {
                        throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
                }

                // No null or empty list
                List<String> entityKeyList = body.getPublisherId();
                if (entityKeyList == null || entityKeyList.size() == 0) {
                        throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.NoKeys"));
                }

                if (!((Publisher) publisher).isAdmin()) {
                        throw new UserMismatchException(new ErrorMessage("errors.deletepublisher.AdminReqd"));
                }

                HashSet<String> dupCheck = new HashSet<String>();
                for (String entityKey : entityKeyList) {
                        validateKeyLength(entityKey);
                        boolean inserted = dupCheck.add(entityKey);
                        if (!inserted) {
                                throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.DuplicateKey", entityKey));
                        }

                        Object obj = em.find(org.apache.juddi.model.Publisher.class, entityKey);
                        if (obj == null) {
                                throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.PublisherNotFound", entityKey));
                        }

                }
        }

        public void validateSavePublisher(EntityManager em, SavePublisher body) throws DispositionReportFaultMessage {

                // No null input
                if (body == null) {
                        throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
                }

                // No null or empty list
                List<org.apache.juddi.api_v3.Publisher> entityList = body.getPublisher();
                if (entityList == null || entityList.size() == 0) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.savepublisher.NoInput"));
                }

                if (!((Publisher) publisher).isAdmin()) {
                        throw new UserMismatchException(new ErrorMessage("errors.savepublisher.AdminReqd", publisher.getAuthorizedName()));
                }

                for (org.apache.juddi.api_v3.Publisher entity : entityList) {
                        validatePublisher(em, entity);
                }
        }

        public void validatePublisher(EntityManager em, org.apache.juddi.api_v3.Publisher publisher) throws DispositionReportFaultMessage {

                // No null input
                if (publisher == null) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.publisher.NullInput"));
                }

                String authorizedName = publisher.getAuthorizedName();
                if (authorizedName == null || authorizedName.length() == 0) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.publisher.NoAuthorizedName"));
                }

                String publisherName = publisher.getPublisherName();
                if (publisherName == null || publisherName.length() == 0) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.publisher.NoPublisherName"));
                }
//TODO identify JUDDI restrictions on publisher name
        }

        public void validateAdminDeleteTModel(EntityManager em, DeleteTModel body) throws DispositionReportFaultMessage {

                // No null input
                if (body == null) {
                        throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
                }

                // No null or empty list
                List<String> entityKeyList = body.getTModelKey();
                if (entityKeyList == null || entityKeyList.size() == 0) {
                        throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.NoKeys"));
                }

                if (!((Publisher) publisher).isAdmin()) {
                        throw new UserMismatchException(new ErrorMessage("errors.AdminReqd"));
                }

                HashSet<String> dupCheck = new HashSet<String>();
                for (String entityKey : entityKeyList) {
                        validateKeyLength(entityKey);
                        boolean inserted = dupCheck.add(entityKey);
                        if (!inserted) {
                                throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.DuplicateKey", entityKey));
                        }

                        Object obj = em.find(org.apache.juddi.model.Tmodel.class, entityKey);
                        if (obj == null) {
                                throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.TModelNotFound", entityKey));
                        }

                        //if (!publisher.isOwner((UddiEntity) obj)) {
                        //        throw new UserMismatchException(new ErrorMessage("errors.usermismatch.InvalidOwner", entityKey));
                        //}

                }
        }
        ////////////////////////////////////////////////////////////////////
        ////////////////////////// begin validation code
        ////////////////////////////////////////////////////////////////////

        private static void validateDescription(String value) throws ValueNotAllowedException {
                if (value != null && value.length() > ValidationConstants.MAX_description) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.DescriptionTooLong"));
                }
        }

        public static void validateLang(String lang) throws ValueNotAllowedException {
                if (lang != null && lang.length() > ValidationConstants.MAX_xml_lang) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.names.LangTooLong"));
                }
        }

        private static void validateUseType(String useType) throws ValueNotAllowedException {
                if (useType != null && useType.length() > ValidationConstants.MAX_useType) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.names.UseTypeTooLong"));
                }
        }

        private static void validateKeyLength(String value) throws ValueNotAllowedException {
                if (value != null && value.length() > ValidationConstants.MAX_Key) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.keys.TooLong"));
                }
        }

        private void validateAccessPoint(EntityManager em, AccessPoint value, Configuration config) throws ValueNotAllowedException {
                if (log.isDebugEnabled()) {
                        log.debug("validateAccessPoint");
                }


                if (value != null) {
                        if (value.getValue().length() > ValidationConstants.MAX_accessPoint) {
                                throw new ValueNotAllowedException(new ErrorMessage("errors.accessPoint.TooLong"));
                        }

                        validateUseType(value.getUseType());
                        if (value.getUseType() != null) {
                                if (value.getUseType().equalsIgnoreCase(AccessPointType.BINDING_TEMPLATE.toString())) {
                                        //validate that the referenced binding key exists already
                                        Object obj = em.find(org.apache.juddi.model.BindingTemplate.class, value.getValue());
                                        if (obj == null) {
                                                throw new ValueNotAllowedException(new ErrorMessage("errors.accessPoint.bindingtemplateRedirect.keynotexist"));
                                        }

                                } else if (value.getUseType().equalsIgnoreCase(AccessPointType.HOSTING_REDIRECTOR.toString())) {
                                        try {
                                                //no validation necessary other than confirm that it's a URL
                                                new URL(value.getValue());
                                        } catch (MalformedURLException ex) {
                                                throw new ValueNotAllowedException(new ErrorMessage("errors.accessPoint.hostingRedirector.notaurl"));
                                        }
                                }
                                //TODO determine if additional validation is required.
                                //potentials, if its a wsdl deployment, is the Value a valid URI
                                //if endpoint, is it a valid URI?
                        }
                }
        }

        private void validateHostingRedirector(EntityManager em, HostingRedirector hostingRedirector, Configuration config) throws ValueNotAllowedException {
                if (log.isDebugEnabled()) {
                        log.debug("validateHostingRedirector");
                }
                if (hostingRedirector == null) {
                        return;
                }

                if (hostingRedirector.getBindingKey() == null || hostingRedirector.getBindingKey().length() == 0) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.hostingredirector.noinput"));
                }
                if (hostingRedirector.getBindingKey().length() > ValidationConstants.MAX_Key) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.hostingredirector.TooLong"));
                }
                boolean checkRef = false;
                try {
                        checkRef = config.getBoolean(Property.JUDDI_ENFORCE_REFERENTIAL_INTEGRITY, false);
                } catch (Exception ex) {
                        log.warn("Error caught reading " + Property.JUDDI_ENFORCE_REFERENTIAL_INTEGRITY + " from config file", ex);
                }
                if (checkRef) {
                        //TODO check the spec to confirm this is logically correct
            /*Object obj = em.find(org.apache.juddi.model.BindingTemplate.class, hostingRedirector.getBindingKey());
                         if (obj == null) {
                         throw new ValueNotAllowedException(new ErrorMessage("errors.hostingredirector.keynotexist"));
                         }*/
                }

        }

        private void validateNameLength(String value) throws ValueNotAllowedException {
                if (value == null || value.length() == 0) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.names.NoInput"));
                }
                if (value.length() > ValidationConstants.MAX_name) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.names.TooLong"));
                }

        }

        private void validateSortCode(String value) throws ValueNotAllowedException {
                if (value != null && value.length() > ValidationConstants.MAX_sortCode) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.sortCode.TooLong"));
                }
        }

        private void validateAddressLines(List<AddressLine> addressLine, Configuration config) throws ValueNotAllowedException {
                if (log.isDebugEnabled()) {
                        log.debug("validateAddressLines");
                }
                if (addressLine != null) {
                        for (int i = 0; i < addressLine.size(); i++) {
                                validateKeyName(addressLine.get(i).getKeyName());
                                verifyTModelKeyExistsAndChecked(addressLine.get(i).getKeyName(), config);

                                validateKeyValue(addressLine.get(i).getKeyValue());
                                if (addressLine.get(i).getValue() == null || addressLine.get(i).getValue().length() == 0) {
                                        throw new ValueNotAllowedException(new ErrorMessage("errors.addressline.noinput"));
                                }
                                if (addressLine.get(i).getValue().length() > ValidationConstants.MAX_addressLine) {
                                        throw new ValueNotAllowedException(new ErrorMessage("errors.addressline.TooLong"));
                                }
                        }
                }
        }

        private void validateEmailAddress(List<Email> email) throws ValueNotAllowedException {
                if (email != null) {
                        for (int i = 0; i < email.size(); i++) {
                                validateUseType(email.get(i).getUseType());
                                if (email.get(i).getValue() == null || email.get(i).getValue().length() == 0) {
                                        throw new ValueNotAllowedException(new ErrorMessage("errors.email.noinput"));
                                }
                                if (email.get(i).getValue().length() > ValidationConstants.MAX_email) {
                                        throw new ValueNotAllowedException(new ErrorMessage("errors.email.TooLong"));
                                }
                        }
                }
        }

        private void validatePhone(List<Phone> phone) throws ValueNotAllowedException {
                if (phone != null) {
                        for (int i = 0; i < phone.size(); i++) {
                                validateUseType(phone.get(i).getUseType());
                                if (phone.get(i).getValue() == null
                                        || phone.get(i).getValue().length() == 0) {
                                        throw new ValueNotAllowedException(new ErrorMessage("errors.phone.noinput"));
                                }
                                if (phone.get(i).getValue().length() > ValidationConstants.MAX_phone) {
                                        throw new ValueNotAllowedException(new ErrorMessage("errors.phone.TooLong"));
                                }
                        }
                }
        }

        private void validateDiscoveryUrlLength(DiscoveryURL url) throws ValueNotAllowedException {
                if (url != null) {
                        validateUseType(url.getUseType());
                        validateURL(url.getValue());
                }
        }

        private void validateKeyValue(String value) throws ValueNotAllowedException {
                if (value != null && value.length() > ValidationConstants.MAX_keyValue) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.keyvalue.TooLong"));
                }
        }

        private void validateKeyName(String value) throws ValueNotAllowedException {
                if (value != null && value.length() > ValidationConstants.MAX_keyValue) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.keyname.TooLong"));
                }


        }

        private void validateDescriptions(List<Description> description) throws ValueNotAllowedException {
                for (int i = 0; i < description.size(); i++) {
                        if (description.get(i).getValue() == null) {
                                throw new ValueNotAllowedException(new ErrorMessage("errors.contact.EmptyDescription"));
                        }

                        validateLang(description.get(i).getLang());
                        validateDescription(description.get(i).getValue());
                }
        }

        private void validateOverviewURL(OverviewURL overviewURL) throws ValueNotAllowedException {
                if (overviewURL != null) {
                        validateUseType(overviewURL.getUseType());
                        validateURL(overviewURL.getValue());
                }
        }

        private void validateURL(String value) throws ValueNotAllowedException {
                if (value != null && value.length() > ValidationConstants.MAX_overviewURL) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.url.overviewTooLong"));
                }
        }

        /**
         * Validates that a tmodel key is registered Alex O'Ree
         *
         * @param tmodelKey
         * @param em
         * @throws ValueNotAllowedException
         * @see org.apache.juddi.config.Install
         * @since 3.1.5
         */
        private boolean verifyTModelKeyExistsAndChecked(String tmodelKey, Configuration config) throws ValueNotAllowedException {
                boolean checked = true;
                if (tmodelKey == null || tmodelKey.length() == 0) {
                        return false;
                }
                if (tmodelKey.equalsIgnoreCase("uddi:uddi.org:categorization:types")) {
                        return false;
                }
                if (tmodelKey.equalsIgnoreCase("uddi:uddi.org:categorization:nodes")) {
                        return false;
                }
                if (tmodelKey.equalsIgnoreCase("uddi:uddi.org:v3_inquiry")) {
                        return false;
                }
                if (tmodelKey.equalsIgnoreCase("uddi:uddi.org:v3_publication")) {
                        return false;
                }
                if (tmodelKey.equalsIgnoreCase("uddi:uddi.org:v3_security")) {
                        return false;
                }
                if (tmodelKey.equalsIgnoreCase("uddi:uddi.org:v3_ownership_transfer")) {
                        return false;
                }
                if (tmodelKey.equalsIgnoreCase("uddi:uddi.org:v3_subscription")) {
                        return false;
                }
                if (tmodelKey.equalsIgnoreCase("uddi:uddi.org:v3_subscriptionlistener")) {
                        return false;
                }

                if (config == null) {
                        log.warn(new ErrorMessage("errors.tmodel.ReferentialIntegrityNullConfig"));
                        return false;
                }
                boolean checkRef = false;
                try {
                        checkRef = config.getBoolean(Property.JUDDI_ENFORCE_REFERENTIAL_INTEGRITY, false);
                } catch (Exception ex) {
                        log.warn("Error caught reading " + Property.JUDDI_ENFORCE_REFERENTIAL_INTEGRITY + " from config file", ex);
                }
                if (checkRef) {
                        if (log.isDebugEnabled()) {
                                log.debug("verifyTModelKeyExists " + tmodelKey);
                        }
                        EntityManager em = PersistenceManager.getEntityManager();

                        if (em == null) {
                                //this is normally the Install class firing up
                                log.warn(new ErrorMessage("errors.tmodel.ReferentialIntegrityNullEM"));
                        } else {
                                //Collections.sort(buildInTmodels);
                                //if ((buildInTmodels, tmodelKey) == -1)
                                Tmodel modelTModel = null;
                                {
                                        EntityTransaction tx = em.getTransaction();
                                        try {

                                                tx.begin();
                                                modelTModel = em.find(org.apache.juddi.model.Tmodel.class, tmodelKey);

                                                if (modelTModel == null) {
                                                        checked = false;
                                                } else {
                                                        for (org.apache.juddi.model.KeyedReference ref : modelTModel.getCategoryBag().getKeyedReferences()) {
                                                                if ("uddi-org:types:unchecked".equalsIgnoreCase(ref.getKeyName())) {
                                                                        checked = false;
                                                                        break;
                                                                }
                                                        }
                                                }

                                                tx.commit();

                                        } finally {
                                                if (tx.isActive()) {
                                                        tx.rollback();
                                                }
                                                em.close();
                                        }
                                        if (modelTModel == null) {
                                                throw new ValueNotAllowedException(new ErrorMessage("errors.tmodel.ReferencedKeyDoesNotExist", tmodelKey));
                                        }
                                }
                        }
                }
                return checked;
        }

        private boolean verifyTModelKeyChecked(Tmodel modelTModel) {
                boolean checked = true;
                if (modelTModel == null) {
                        checked = false;
                } else {
                        for (org.apache.juddi.model.KeyedReference ref : modelTModel.getCategoryBag().getKeyedReferences()) {
                                if ("uddi-org:types:unchecked".equalsIgnoreCase(ref.getTmodelKeyRef())) {
                                        checked = false;
                                        break;
                                }
                        }
                }
                return checked;
        }

        /**
         * throws if it doesn't exist, returns it if it does
         *
         * @param tmodelKey
         * @return
         * @throws ValueNotAllowedException
         * @since 3.3
         */
        private TModel verifyTModelKeyExists(String tmodelKey) throws ValueNotAllowedException, DispositionReportFaultMessage {
                TModel api = null;
                EntityManager em = PersistenceManager.getEntityManager();
                boolean found = false;
                if (em == null) {
                        log.warn(new ErrorMessage("errors.tmodel.ReferentialIntegrityNullEM"));
                } else {
                        Tmodel modelTModel = null;
                        {
                                EntityTransaction tx = em.getTransaction();
                                try {

                                        tx.begin();
                                        modelTModel = em.find(org.apache.juddi.model.Tmodel.class, tmodelKey);


                                        if (modelTModel != null) {
                                                found = true;
                                                api = new TModel();
                                                MappingModelToApi.mapTModel(modelTModel, api);
                                        }
                                        tx.commit();

                                } finally {
                                        if (tx.isActive()) {
                                                tx.rollback();
                                        }
                                        em.close();
                                }

                        }
                }
                if (!found) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.tmodel.ReferencedKeyDoesNotExist", tmodelKey));
                }
                return api;
        }

        private List<String> GetBindingKeysCheckedTModelKeyedReference(Map<String, TModel> cache, BindingTemplate bt) {
                List<String> ret = new ArrayList<String>();

                if (bt == null) {
                        return ret;
                }
                if (bt.getCategoryBag() != null) {
                        for (int i = 0; i < bt.getCategoryBag().getKeyedReference().size(); i++) {
                                ret.addAll(GetBindingKeysCheckedTModelKeyedReference(cache, bt.getCategoryBag().getKeyedReference().get(i)));
                        }
                }


                return ret;
        }

        private List<String> GetBindingKeysCheckedTModelKeyedReference(Map<String, TModel> cache, BusinessService bt) {
                List<String> ret = new ArrayList<String>();

                if (bt == null) {
                        return ret;
                }
                if (bt.getCategoryBag() != null) {
                        for (int i = 0; i < bt.getCategoryBag().getKeyedReference().size(); i++) {
                                ret.addAll(GetBindingKeysCheckedTModelKeyedReference(cache, bt.getCategoryBag().getKeyedReference().get(i)));
                        }
                }
                if (bt.getBindingTemplates() != null) {
                        for (int i = 0; i < bt.getBindingTemplates().getBindingTemplate().size(); i++) {
                                ret.addAll(GetBindingKeysCheckedTModelKeyedReference(cache, bt.getBindingTemplates().getBindingTemplate().get(i)));
                        }
                }


                return ret;
        }

        private List<String> GetBindingKeysCheckedTModelKeyedReference(Map<String, TModel> cache, BusinessEntity bt) {
                List<String> ret = new ArrayList<String>();


                if (bt == null) {
                        return ret;
                }
                if (bt.getCategoryBag() != null) {
                        for (int i = 0; i < bt.getCategoryBag().getKeyedReference().size(); i++) {
                                ret.addAll(GetBindingKeysCheckedTModelKeyedReference(cache, bt.getCategoryBag().getKeyedReference().get(i)));
                        }
                }
                if (bt.getIdentifierBag() != null) {
                        for (int i = 0; i < bt.getIdentifierBag().getKeyedReference().size(); i++) {
                                ret.addAll(GetBindingKeysCheckedTModelKeyedReference(cache, bt.getIdentifierBag().getKeyedReference().get(i)));
                        }
                }

                if (bt.getBusinessServices() != null) {
                        for (int i = 0; i < bt.getBusinessServices().getBusinessService().size(); i++) {
                                ret.addAll(GetBindingKeysCheckedTModelKeyedReference(cache, bt.getBusinessServices().getBusinessService().get(i)));
                        }
                }


                return ret;
        }

        private List<String> GetBindingKeysCheckedTModelKeyedReference(Map<String, TModel> cache, TModel bt) {
                List<String> ret = new ArrayList<String>();


                if (bt == null) {
                        return ret;
                }
                if (bt.getCategoryBag() != null) {
                        for (int i = 0; i < bt.getCategoryBag().getKeyedReference().size(); i++) {
                                ret.addAll(GetBindingKeysCheckedTModelKeyedReference(cache, bt.getCategoryBag().getKeyedReference().get(i)));
                        }
                }
                if (bt.getIdentifierBag() != null) {
                        for (int i = 0; i < bt.getIdentifierBag().getKeyedReference().size(); i++) {
                                ret.addAll(GetBindingKeysCheckedTModelKeyedReference(cache, bt.getIdentifierBag().getKeyedReference().get(i)));
                        }
                }

                return ret;
        }

        private List<String> GetBindingKeysCheckedTModelKeyedReference(Map<String, TModel> cache, KeyedReference get) {
                List<String> ret = new ArrayList<String>();
                TModel ref = null;
                if (cache.containsKey(get.getTModelKey())) {
                        ref = cache.get(get.getTModelKey());
                }
                if (ref == null) {
                        try {
                                ref = verifyTModelKeyExists(get.getTModelKey());
                                cache.put(get.getTModelKey(), ref);
                        } catch (Exception ex) {
                                log.error("unexpected error loading tmodel " + get.getTModelKey(), ex);
                        }
                }
                if (ref != null) {
                        ret.addAll(TModelContains(UDDIConstants.IS_VALIDATED_BY, ref));


                }
                return ret;
        }

        private List<String> TModelContains(String IS_VALIDATED_BY, TModel ref) {

                List<String> ret = new ArrayList<String>();
                if (ref == null) {
                        return null;
                }
                if (ref.getCategoryBag() != null) {
                        for (int i = 0; i < ref.getCategoryBag().getKeyedReference().size(); i++) {
                                if (ref.getCategoryBag().getKeyedReference().get(i).getTModelKey().equalsIgnoreCase(IS_VALIDATED_BY)) {
                                        ret.add(ref.getCategoryBag().getKeyedReference().get(i).getKeyValue());
                                }
                        }
                        for (int i = 0; i < ref.getCategoryBag().getKeyedReferenceGroup().size(); i++) {
                                for (int k = 0; k < ref.getCategoryBag().getKeyedReferenceGroup().get(i).getKeyedReference().size(); k++) {
                                        if (ref.getCategoryBag().getKeyedReferenceGroup().get(i).getKeyedReference().get(k).getTModelKey().equalsIgnoreCase(IS_VALIDATED_BY)) {
                                                ret.add(ref.getCategoryBag().getKeyedReferenceGroup().get(i).getKeyedReference().get(k).getKeyValue());
                                        }
                                }
                        }
                }
                if (ref.getIdentifierBag() != null) {
                        for (int i = 0; i < ref.getIdentifierBag().getKeyedReference().size(); i++) {
                                if (ref.getIdentifierBag().getKeyedReference().get(i).getTModelKey().equalsIgnoreCase(IS_VALIDATED_BY)) {
                                        ret.add(ref.getIdentifierBag().getKeyedReference().get(i).getKeyValue());
                                }
                        }
                }
                return ret;
        }



}
