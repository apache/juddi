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
 * UDDI provides a mechanism that may be used by publishers to categorize UDDI
 * entities according to any number of category systems. See Appendix F Using
 * Categorization for more information. This section defines a tModel used to
 * associate a tModel, frequently a category system, with some other tModel,
 * frequently the value set of some other category system, for the purpose of
 * extension or redefinition of purpose.
 *
 * Most value sets are used with some purpose in mind. To avoid ambiguity in
 * publisher and inquirer intent it is not uncommon for this purpose to be
 * explicitly associated with the value set in its tModel. The IS0 3166
 * geographic category system, for example, has the purpose service offering
 * area.
 *
 * Similarly, the UDDI API is comprised of a fixed set of programming interfaces
 * and structures. UDDI registries can extend the UDDI API through schema
 * derivation, to offer additional functionality.
 *
 * The Derived From category system exists to allow tModels to refer to the
 * tModels that they extend in some way. Value set values can be re-used by
 * referring a derived value set tModel to the values in some other value set
 * tModel. The reason for reuse can be for assigning another purpose to the set
 * of values, for extending the set of values, for associating one set of values
 * with another, or for some other kind of derivation. * Specification tModels
 * that extend some other specification tModel can similarly use this category
 * system to refer to the tModels they extend, providing end users with
 * knowledge about the full scope of the API.
 * <br><br>
 * Valid Values - The keyValue attribute in a keyedReference element that
 * references this tModel within a categoryBag MUST be some other tModelKey in
 * the UDDI registry. For value set derivations the tModel that is referred to
 * contain the root values for the derived value set. A tModel for a derived
 * value set is not automatically checked if the referred to value set is
 * checked. The derived value set must itself go through the registry's process
 * for making the derived value set checked.
 *
 * @author Alex O'Ree
 */
public class Uddiuddiorgcategorizationderivedfrom implements ValueSetValidator {

        public String getMyKey() {
                return "uddi:uddi.org:categorization:derivedfrom";
        }

        @Override
        public void validateValuesBindingTemplate(List<BindingTemplate> items, String xpath) throws DispositionReportFaultMessage {
                if (items == null) {
                        return;
                }

                for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).getCategoryBag() != null) {
                                AbstractSimpleValidator.validateKeyNotPresentKeyRef(items.get(i).getCategoryBag().getKeyedReference(), getMyKey(), "binding");
                                AbstractSimpleValidator.validateKeyNotPresentKeyRefGrp(items.get(i).getCategoryBag().getKeyedReferenceGroup(), getMyKey(), "binding");
                        }
                        if (items.get(i).getTModelInstanceDetails() != null) {
                                for (int k = 0; k < items.get(i).getTModelInstanceDetails().getTModelInstanceInfo().size(); k++) {
                                        if (items.get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k) != null) {
                                                if (getMyKey().equalsIgnoreCase(items.get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k).getTModelKey())) {
                                                        throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", "not allowed on binding templates"));
                                                }
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
                                AbstractSimpleValidator.validateKeyNotPresentKeyRef(items.get(i).getCategoryBag().getKeyedReference(), getMyKey(), "business");
                                AbstractSimpleValidator.validateKeyNotPresentKeyRefGrp(items.get(i).getCategoryBag().getKeyedReferenceGroup(), getMyKey(), "business");
                        }
                        if (items.get(i).getIdentifierBag() != null) {
                                AbstractSimpleValidator.validateKeyNotPresentKeyRef(items.get(i).getCategoryBag().getKeyedReference(), getMyKey(), "business");
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
                                AbstractSimpleValidator.validateKeyNotPresentKeyRef(items.get(i).getCategoryBag().getKeyedReference(), getMyKey(), "service");
                                AbstractSimpleValidator.validateKeyNotPresentKeyRefGrp(items.get(i).getCategoryBag().getKeyedReferenceGroup(), getMyKey(), "service");
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
                        AbstractSimpleValidator.validateKeyNotPresentKeyRef(items.get(i).getKeyedReference(), getMyKey(), "publisherAssertion");
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
                                        if (validValues.get(k).equals(items.get(i).getKeyValue())) {
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
                        if (getMyKey().equalsIgnoreCase(tModelInstanceInfo.get(k).getTModelKey())) {
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
                                                if (getMyKey().equalsIgnoreCase(items.get(i).getCategoryBag().getKeyedReference().get(k).getTModelKey())) {
                                                        //TODO look up value
                                                        Tmodel find = em.find(org.apache.juddi.model.Tmodel.class, items.get(i).getCategoryBag().getKeyedReference().get(k).getKeyValue());
                                                        if (find == null) {
                                                                throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", "Referenced key " + items.get(i).getCategoryBag().getKeyedReference().get(k).getKeyValue() + " does not exist"));
                                                        }
                                                }
                                        }
                                }
                                if (items.get(i).getIdentifierBag() != null) {
                                        AbstractSimpleValidator.validateKeyNotPresentKeyRef(items.get(i).getIdentifierBag().getKeyedReference(), getMyKey(), "tmodel identbag");
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
