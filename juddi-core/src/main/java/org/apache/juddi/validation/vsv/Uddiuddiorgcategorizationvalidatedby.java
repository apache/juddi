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
 * This tModel represents a category system that is used to point a tModel
 * representing a checked value set to the bindingTemplate for a value set
 * caching or value set validation Web service.
 *
 * One of the concepts that tModels can represent is a checked value set. A
 * checked value set is one whose use is monitored by a validation algorithm.
 * There are two types of validation algorithms: simple checking of referenced
 * values against a pre-defined set of allowable values, and any other kind of
 * validation. UDDI provides the Value Set API set (see Section 5.6 Value Set
 * API Set) to acquire the set of allowable values or execute an external
 * validation algorithm.
 *
 * A validation algorithm for a checked value set can be acquired by nodes
 * privately, or can be obtained through normal UDDI discovery. The validatedBy
 * category system facilitates discovery of the value set caching or value set
 * validation Web service for a checked value set tModel by pointing to the
 * bindingTemplate for the Web service.  *
 * For the Web service to be useful, it must recognize any and all checked value
 * sets that it is expected to be associated with. The recommended way for doing
 * so is to place the tModels for the checked value sets it supports in the
 * tModelInstanceDetails of the bindingTemplate for the Web service. Registry
 * policy may require that providers of the Web service recognize value sets
 * supported using this technique.
 * 
* Valid Values
 *
 * The keyValues in keyedReferences that refer to this tModel must be
 * bindingKeys. Such a keyValue SHOULD reference a bindingTemplate that
 * specifies a Web service that implements a value set caching or value set
 * validation API and which SHOULD reference the value set tModel so categorized
 * with this category system. No other contextual checks are performed.
 *
 * @author Alex O'Ree
 */
public class Uddiuddiorgcategorizationvalidatedby implements ValueSetValidator{

               public static final String key = "uddi:uddi.org:categorization:validatedby";

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

        private void validatedValuesKeyRef(List<KeyedReference> items, String xpath) throws DispositionReportFaultMessage {
                if (items == null) {
                        return;
                }
                String err = "";
                for (int i = 0; i < items.size(); i++) {
                        List<String> validValues = getValidValues();
                        if (validValues != null) {
                                //ok we have some work to do
                                boolean valid = false;
                                for (int k = 0; k < validValues.size(); k++) {
                                        if (validValues.get(i).equals(items.get(i).getKeyValue())) {
                                                valid = true;
                                        }
                                }
                                if (!valid) {
                                        err += xpath + "keyedReference(" + i + ") ";
                                }
                        }
                }
                if (err.length() > 0) {
                        throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", err));
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
                                                        //TODO look up value
                                                        Tmodel find = em.find(org.apache.juddi.model.Tmodel.class, items.get(i).getCategoryBag().getKeyedReference().get(k).getKeyValue());
                                                        if (find == null) {
                                                                throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", "Referenced key " + items.get(i).getCategoryBag().getKeyedReference().get(k).getKeyValue() + " does not exist"));
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
