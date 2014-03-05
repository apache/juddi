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

import java.util.List;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.DispositionReport;
import org.uddi.api_v3.PublisherAssertion;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelInstanceInfo;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.vs_v3.ValidateValues;

/**
 *
 * @author Alex O'Ree
 */
public interface ValueSetValidator {

        public void validateTmodelInstanceDetails(List<TModelInstanceInfo> tModelInstanceInfo, String xpath) throws DispositionReportFaultMessage;

        public void validateValuesBindingTemplate(List<BindingTemplate> items, String xpath) throws DispositionReportFaultMessage;

        public void validateValuesBusinessEntity(List<BusinessEntity> items) throws DispositionReportFaultMessage;

        public void validateValuesBusinessService(List<BusinessService> items, String xpath) throws DispositionReportFaultMessage;

        public void validateValuesPublisherAssertion(List<PublisherAssertion> items) throws DispositionReportFaultMessage;

        public void validateValuesTModel(List<TModel> items) throws DispositionReportFaultMessage;
        
        public List<String> getValidValues();
}
