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
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
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
public class Uddiuddiorgcategorizationowningbusiness implements ValueSetValidator{

        @Override
        public List<String> getValidValues() {
                List<String> ret = new ArrayList<String>();

                return ret;
        }

        @Override
        public void validateTmodelInstanceDetails(List<TModelInstanceInfo> tModelInstanceInfo, String xpath) throws DispositionReportFaultMessage {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void validateValuesBindingTemplate(List<BindingTemplate> items, String xpath) throws DispositionReportFaultMessage {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void validateValuesBusinessEntity(List<BusinessEntity> items) throws DispositionReportFaultMessage {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void validateValuesBusinessService(List<BusinessService> items, String xpath) throws DispositionReportFaultMessage {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void validateValuesPublisherAssertion(List<PublisherAssertion> items) throws DispositionReportFaultMessage {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void validateValuesTModel(List<TModel> items) throws DispositionReportFaultMessage {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

}
