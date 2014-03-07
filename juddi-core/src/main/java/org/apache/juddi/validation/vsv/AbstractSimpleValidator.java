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
import java.util.List;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.InvalidValueException;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.KeyedReferenceGroup;
import org.uddi.api_v3.PublisherAssertion;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelInstanceInfo;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * A simple base class for the validator interface that lets you define a simple set of allowed values. 
 * All other values will be rejected. Valid values apply to all UDDI elements
 * @author Alex O'Ree
 * @since 3.2.1
 * 
 */
public abstract class AbstractSimpleValidator implements ValueSetValidator {

        @Override
        public void validateValuesBindingTemplate(List<BindingTemplate> items, String xpath) throws DispositionReportFaultMessage {
                if (items == null) {
                        return;
                }
                for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).getCategoryBag() != null) {
                                validatedValuesKeyRef(items.get(i).getCategoryBag().getKeyedReference(), xpath + "bindingTemplate(" + i + ").categoryBag.");
                                validatedValuesKeyRefGrp(items.get(i).getCategoryBag().getKeyedReferenceGroup(), xpath + "bindingTemplate(" + i + ").categoryBag.");
                        }
                        if (items.get(i).getTModelInstanceDetails() != null) {

                                validateTmodelInstanceDetails(items.get(i).getTModelInstanceDetails().getTModelInstanceInfo(), xpath + "bindingTemplate(" + i + ").tModelInstanceDetails.");
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
                                validatedValuesKeyRef(items.get(i).getCategoryBag().getKeyedReference(), "businessEntity(" + i + ").categoryBag.");
                                validatedValuesKeyRefGrp(items.get(i).getCategoryBag().getKeyedReferenceGroup(), "businessEntity(" + i + ").categoryBag.");
                        }
                        if (items.get(i).getIdentifierBag() != null) {
                                validatedValuesKeyRef(items.get(i).getIdentifierBag().getKeyedReference(), "businessEntity(" + i + ").identifierBag.");
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
                                validatedValuesKeyRef(items.get(i).getCategoryBag().getKeyedReference(), xpath + "businessService(" + i + ").categoryBag.");
                                validatedValuesKeyRefGrp(items.get(i).getCategoryBag().getKeyedReferenceGroup(), xpath + "businessService(" + i + ").categoryBag.");
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
                        if (items.get(i).getKeyedReference() != null) {
                                List<KeyedReference> temp = new ArrayList<KeyedReference>();
                                temp.add(items.get(i).getKeyedReference());
                                validatedValuesKeyRef(temp, "publisherAssertion(" + i + ").");
                        }
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

        private void validatedValuesKeyRefGrp(List<KeyedReferenceGroup> items, String xpath) throws DispositionReportFaultMessage {
                if (items == null) {
                        return;
                }
                for (int i = 0; i < items.size(); i++) {
                        validatedValuesKeyRef(items.get(i).getKeyedReference(), xpath + "keyReferenceGroup(" + i + ").");
                }
        }

        @Override
        public void validateTmodelInstanceDetails(List<TModelInstanceInfo> tModelInstanceInfo, String xpath) throws DispositionReportFaultMessage {

        }

        @Override
        public abstract List<String> getValidValues();

        @Override
        public void validateValuesTModel(List<TModel> items) throws DispositionReportFaultMessage {
                if (items == null) {
                        return;
                }
                for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).getCategoryBag() != null) {
                                validatedValuesKeyRef(items.get(i).getCategoryBag().getKeyedReference(), "tModel(" + i + ").categoryBag.");
                                validatedValuesKeyRefGrp(items.get(i).getCategoryBag().getKeyedReferenceGroup(), "tModel(" + i + ").categoryBag.");
                        }
                        if (items.get(i).getIdentifierBag() != null) {
                                validatedValuesKeyRef(items.get(i).getIdentifierBag().getKeyedReference(), "tModel(" + i + ").identifierBag.");
                        }
                }
        }

        public static void validateKeyNotPresentKeyRef(List<KeyedReference> items, String key, String itemtype) throws DispositionReportFaultMessage {
                if (items == null) {
                        return;
                }
                String err = "";
                for (int i = 0; i < items.size(); i++) {
                        validateKeyNotPresentKeyRef(items.get(i), key, itemtype);
                }
                if (err.length() > 0) {

                }
        }

        public static void validateKeyNotPresentKeyRef(KeyedReference item, String key, String itemtype) throws DispositionReportFaultMessage {
                if (item == null) {
                        return;
                }
                if (key.equalsIgnoreCase(item.getTModelKey())) {
                        throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", "key " + key + " not allowed on " + itemtype));
                }
        }

        public static void validateKeyNotPresentKeyRefGrp(List<KeyedReferenceGroup> items, String key, String itemtype) throws DispositionReportFaultMessage {
                if (items == null) {
                        return;
                }
                for (int i = 0; i < items.size(); i++) {
                        if (key.equalsIgnoreCase(items.get(i).getTModelKey())) {
                                throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", "key " + key + " not allowed on " + itemtype));
                        }
                        validateKeyNotPresentKeyRef(items.get(i).getKeyedReference(), key, itemtype);
                }
        }

}
