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
package org.apache.juddi.api.impl;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api.util.QueryStatus;
import org.apache.juddi.api.util.ValueSetValidationQuery;
import org.apache.juddi.config.PersistenceManager;
import org.apache.juddi.model.ValueSetValues;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.InvalidValueException;
import org.apache.juddi.v3.error.ValueNotAllowedException;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.DispositionReport;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.KeyedReferenceGroup;
import org.uddi.api_v3.PublisherAssertion;
import org.uddi.api_v3.Result;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelInstanceInfo;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDIValueSetValidationPortType;
import org.uddi.vs_v3.ValidateValues;

//@WebService(serviceName="UDDIValueSetValidationService", 
//			endpointInterface="org.uddi.v3_service.UDDIValueSetValidationPortType",
//			targetNamespace = "urn:uddi-org:v3_service")
public class UDDIValueSetValidationImpl extends AuthenticatedService implements
        UDDIValueSetValidationPortType {

        private UDDIServiceCounter serviceCounter;

        public UDDIValueSetValidationImpl() {
                super();
                serviceCounter = ServiceCounterLifecycleResource.getServiceCounter(this.getClass());
        }

        @Override
        public DispositionReport validateValues(ValidateValues body)
                throws DispositionReportFaultMessage {
                long startTime = System.currentTimeMillis();

                if (body == null) {
                        long procTime = System.currentTimeMillis() - startTime;
                        serviceCounter.update(ValueSetValidationQuery.VALIDATE_VALUES,
                                QueryStatus.FAILED, procTime);

                        throw new ValueNotAllowedException(new ErrorMessage("errors.valuesetvalidation.noinput"));
                }
                /*
                 * The UDDI node that is calling validate_values MUST pass one 
                 * or more businessEntity elements, one or more businessService 
                 * elements, one or more bindingTemplate elements, one or more 
                 * tModel elements, or one or more publisherAssertion elements 
                 * as the sole argument to this Web service. 
                 * */


                /* performs validation on all of the keyedReferences or keyedReferenceGroups */

                /*when the entity being saved is a businessEntity, contained 
                 * businessService and bindingTemplate entities may themselves 
                 * reference values from the authorized value sets as well. */
                validateValuesBT(body.getBindingTemplate(), "");
                validateValuesBE(body.getBusinessEntity());
                validateValuesBS(body.getBusinessService(), "");
                validateValuesPA(body.getPublisherAssertion());
                validateValuesTM(body.getTModel());

                DispositionReport r = new DispositionReport();
                r.getResult().add(new Result());
                long procTime = System.currentTimeMillis() - startTime;
                serviceCounter.update(ValueSetValidationQuery.VALIDATE_VALUES,
                        QueryStatus.SUCCESS, procTime);

                return r;
        }

        private void validateValuesBT(List<BindingTemplate> items, String xpath) throws DispositionReportFaultMessage {
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

        private void validateValuesBE(List<BusinessEntity> items) throws DispositionReportFaultMessage {
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
                                validateValuesBS(items.get(i).getBusinessServices().getBusinessService(), "businessEntity(" + i + ").");
                        }
                }
        }

        private void validateValuesBS(List<BusinessService> items, String xpath) throws DispositionReportFaultMessage {
                if (items == null) {
                        return;
                }
                for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).getCategoryBag() != null) {
                                validatedValuesKeyRef(items.get(i).getCategoryBag().getKeyedReference(), xpath + "businessService(" + i + ").categoryBag.");
                                validatedValuesKeyRefGrp(items.get(i).getCategoryBag().getKeyedReferenceGroup(), xpath + "businessService(" + i + ").categoryBag.");
                        }
                        if (items.get(i).getBindingTemplates() != null) {
                                validateValuesBT(items.get(i).getBindingTemplates().getBindingTemplate(), xpath + xpath + "businessService(" + i + ").identifierBag.");
                        }
                }
        }

        private void validateValuesPA(List<PublisherAssertion> items) throws DispositionReportFaultMessage {
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

        private void validateValuesTM(List<TModel> items) throws DispositionReportFaultMessage {
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

        /**
         * returns null if no valid values are defined (i.e. anything goes)
         *
         * @param tModelkey
         * @return
         */
        public static List<String> getValidValues(String tModelKey) {
                List<String> ret = null;
                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();
                        ValueSetValues items = em.find(ValueSetValues.class, tModelKey);
                        if (items != null && items.getValues() != null) {
                                ret = new ArrayList<String>();
                                for (int i = 0; i < items.getValues().size(); i++) {
                                        ret.add(items.getValues().get(i).getValue());
                                }
                        }
                        tx.commit();
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
                return ret;

        }

        private void validatedValuesKeyRef(List<KeyedReference> items, String xpath) throws DispositionReportFaultMessage {
                if (items == null) {
                        return;
                }
                String err = "";
                for (int i = 0; i < items.size(); i++) {
                        List<String> validValues = getValidValues(items.get(i).getTModelKey());
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

        private void validateTmodelInstanceDetails(List<TModelInstanceInfo> tModelInstanceInfo, String xpath) throws DispositionReportFaultMessage {
                /*
                 if (tModelInstanceInfo == null) {
                 return;
                 }
                 String err = "";
                 for (int i = 0; i < tModelInstanceInfo.size(); i++) {
                 List<String> validValues = getValidValues(tModelInstanceInfo.get(i).getTModelKey());
                 if (validValues != null) {
                 //compare against the instance info
                 if (tModelInstanceInfo.get(i).getInstanceDetails() == null) {
                 err += xpath + ".(" + i + ").instanceDetails=null ";
                 } else {
                 boolean ok = false;
                 for (int k = 0; k < validValues.size(); k++) {
                 if (validValues.get(k).equals(tModelInstanceInfo.get(i).getInstanceDetails().getInstanceParms())) {
                 ok = true;
                 }
                 }
                 if (!ok) {
                 err += xpath + ".(" + i + ").instanceDetails.instanceParams ";
                 }
                 }
                 }
                 }
                 if (err.length() > 0) {
                 throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", err));
                 }*/
        }
}
