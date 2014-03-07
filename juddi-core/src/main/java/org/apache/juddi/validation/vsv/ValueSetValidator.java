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
import org.apache.juddi.api.impl.UDDIValueSetCachingImpl;
import org.apache.juddi.api.impl.UDDIValueSetValidationImpl;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.PublisherAssertion;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelInstanceInfo;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * This is the value set validator interface. It enables you to define your own validation rules for tModel Keyed References.
 * To use this, define a tModel containing the following 
 * <pre>&lt;categoryBag&gt;
 * &lt;keyedReference keyName=&quot;&quot;
 * keyValue=&quot;uddi:juddi.apache.org:servicebindings-valueset-cp&quot;
 * tModelKey=&quot;uddi:uddi.org:identifier:validatedby&quot;/&gt;
 * &lt;/categoryBag&gt;
 * </pre>Where uddi:juddi.apache.org:servicebindings-valueset-cp
 * is the binding key of the service implementing the VSV API (this service).
 * <Br><BR>
 * From there, you need to create a class that either implements
 * {@link ValueSetValidator} or extends {@link AbstractSimpleValidator}. It must
 * be in the package named org.apache.juddi.validation.vsv and must by named
 * following the convention outlined in {@link #ConvertKeyToClass(java.lang.String)
 * @author Alex O'Ree
 * @since 3.2.1
 * @see AbstractSimpleValidator
 * @see UDDIValueSetValidationImpl
 * @see UDDIValueSetCachingImpl
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
