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
package org.apache.juddi.validation.vsv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.apache.juddi.config.PersistenceManager;
import org.apache.juddi.model.Tmodel;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.InvalidValueException;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.PublisherAssertion;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelInstanceInfo;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * The owningBusiness tModel represents a category system that may be used to
 * locate the businessEntity associated with the publisher of a tModel.
 *
 * It is often desirable to be able to discover the business entity that
 * represents the publisher of a given tModel. When choosing among similar Web
 * service definitions, for example, it is useful to be able to determine that
 * one of them is published by a known organization. For most UDDI entities this
 * can be deduced by inspecting the containment hierarchy of the entity to its
 * root businessEntity. For tModels, the UDDI owningBusiness category system
 * fills this need by allowing tModels to point to the businessEntity of their
 * publisher.
 *
 * The value set of this value set is the set of businessKeys. The content of
 * keyValue in keyedReferences that refers to this tModel must be a businessKey.
 * The keyValue is used to specify that the businessEntity whose businessKey is
 * the keyValue in a keyedReference "owns" the tagged tModel. The entity tagged
 * must be a tModel, the referred-to businessEntity must exist, and it must have
 * been published by the same publisher.
 *
 * @author Alex O'Ree
 */
public class Uddiuddiorgcategorizationowningbusiness implements ValueSetValidator {

        public static final String key = "uddi:uddi.org:categorization:owningbusiness";

        @Override
        public void validateValuesBindingTemplate(List<BindingTemplate> items, String xpath) throws DispositionReportFaultMessage {
                if (items == null) {
                        return;
                }

                for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).getCategoryBag() != null) {
                                AbstractSimpleValidator.validateKeyNotPresentKeyRef(items.get(i).getCategoryBag().getKeyedReference(), key, "binding");
                                AbstractSimpleValidator.validateKeyNotPresentKeyRefGrp(items.get(i).getCategoryBag().getKeyedReferenceGroup(), key, "binding");
                        }
                        if (items.get(i).getTModelInstanceDetails() != null) {
                                for (int k = 0; k < items.get(i).getTModelInstanceDetails().getTModelInstanceInfo().size(); k++) {
                                        if (items.get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k) != null) 
                                        if (key.equalsIgnoreCase(items.get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k).getTModelKey())) {
                                                throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", "not allowed on binding templates"));
                                        }
                                }
                        }
                }
        }

        @Override
        public void validateValuesBusinessEntity(List<BusinessEntity> items) throws DispositionReportFaultMessage {
                if (items == null) {
                        return;
                }
                for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).getCategoryBag() != null) {
                                AbstractSimpleValidator.validateKeyNotPresentKeyRef(items.get(i).getCategoryBag().getKeyedReference(), key, "business");
                                AbstractSimpleValidator.validateKeyNotPresentKeyRefGrp(items.get(i).getCategoryBag().getKeyedReferenceGroup(), key, "business");
                        }
                        if (items.get(i).getIdentifierBag() != null) {
                                AbstractSimpleValidator.validateKeyNotPresentKeyRef(items.get(i).getCategoryBag().getKeyedReference(), key, "business");
                        }
                        if (items.get(i).getBusinessServices() != null) {
                                validateValuesBusinessService(items.get(i).getBusinessServices().getBusinessService(), "businessEntity(" + i + ").");
                        }
                }
        }

        @Override
        public void validateValuesBusinessService(List<BusinessService> items, String xpath) throws DispositionReportFaultMessage {
                if (items == null) {
                        return;
                }
                for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).getCategoryBag() != null) {
                                AbstractSimpleValidator.validateKeyNotPresentKeyRef(items.get(i).getCategoryBag().getKeyedReference(), key, "service");
                                AbstractSimpleValidator.validateKeyNotPresentKeyRefGrp(items.get(i).getCategoryBag().getKeyedReferenceGroup(), key, "service");
                        }
                        if (items.get(i).getBindingTemplates() != null) {
                                validateValuesBindingTemplate(items.get(i).getBindingTemplates().getBindingTemplate(), xpath + xpath + "businessService(" + i + ").identifierBag.");
                        }
                }
        }

        @Override
        public void validateValuesPublisherAssertion(List<PublisherAssertion> items) throws DispositionReportFaultMessage {
                if (items == null) {
                        return;
                }
                for (int i = 0; i < items.size(); i++) {
                        AbstractSimpleValidator.validateKeyNotPresentKeyRef(items.get(i).getKeyedReference(), key, "publisherAssertion");
                }
        }

        @Override
        public void validateTmodelInstanceDetails(List<TModelInstanceInfo> tModelInstanceInfo, String xpath) throws DispositionReportFaultMessage {
                if (tModelInstanceInfo == null) {
                        return;
                }
                for (int k = 0; k < tModelInstanceInfo.size(); k++) {
                        if (key.equalsIgnoreCase(tModelInstanceInfo.get(k).getTModelKey())) {
                                throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", "not allowed on tModel instance info"));
                        }
                }
        }

        @Override
        public void validateValuesTModel(List<TModel> items) throws DispositionReportFaultMessage {
                if (items == null) {
                        return;
                }
                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        for (int i = 0; i < items.size(); i++) {
                                if (items.get(i).getCategoryBag() != null) {
                                        for (int k = 0; k < items.get(i).getCategoryBag().getKeyedReference().size(); k++) {
                                                if (key.equalsIgnoreCase(items.get(i).getCategoryBag().getKeyedReference().get(k).getTModelKey())) {
                                                        //The content of keyValue in keyedReferences that refers to this tModel must be a businessKey. 
                                                        //the referred-to businessEntity must exist, and it must have been published by the same publisher.
                                                        org.apache.juddi.model.BusinessEntity find = em.find(org.apache.juddi.model.BusinessEntity.class, items.get(i).getCategoryBag().getKeyedReference().get(k).getKeyValue());

                                                        if (find == null) {
                                                                throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", "Referenced key " + items.get(i).getCategoryBag().getKeyedReference().get(k).getKeyValue() + " does not exist"));
                                                        } else {
                                                                if (items.get(i).getTModelKey() != null) {
                                                                        org.apache.juddi.model.Tmodel tm = em.find(org.apache.juddi.model.Tmodel.class, items.get(i).getTModelKey());
                                                                        if (tm == null) {
                                                                                //this is a project tModel, let access control rules take care of ownership info        
                                                                        } else if (find.getAuthorizedName().equalsIgnoreCase(tm.getAuthorizedName())) {
                                                                                throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", "Referenced key " + items.get(i).getCategoryBag().getKeyedReference().get(k).getKeyValue() + " exists but is not owned by you."));
                                                                        }
                                                                } else {
                                                                        //this is a project tModel, let access control rules take care of ownership info
                                                                }
                                                        }
                                                }
                                        }
                                }
                                if (items.get(i).getIdentifierBag() != null) {
                                        AbstractSimpleValidator.validateKeyNotPresentKeyRef(items.get(i).getIdentifierBag().getKeyedReference(), key, "tmodel identbag");
                                }
                        }
                } catch (DispositionReportFaultMessage d) {
                        throw d;
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
        }

        @Override
        public List<String> getValidValues() {
                return Collections.EMPTY_LIST;
        }
}
