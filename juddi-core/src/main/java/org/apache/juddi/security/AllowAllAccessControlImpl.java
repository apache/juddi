/*
 * Copyright 2019 The Apache Software Foundation.
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
package org.apache.juddi.security;

import java.util.List;
import javax.xml.ws.WebServiceContext;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessInfo;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.OperationalInfo;
import org.uddi.api_v3.RelatedBusinessInfo;
import org.uddi.api_v3.ServiceInfo;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelInfo;

/**
 * Default implementation, performs no additional filtering of data
 * @since 3.4
 * @author Alex O'Ree
 */
public class AllowAllAccessControlImpl implements IAccessControl {

 

    @Override
    public List<BusinessService> filterServices(WebServiceContext arg0, String arg1, List<BusinessService> arg2) {
        return arg2;
    }

    @Override
    public List<BusinessEntity> filterBusinesses(WebServiceContext arg0, String arg1, List<BusinessEntity> arg2) {
        return arg2;
    }

    @Override
    public List<BusinessInfo> filterBusinessInfo(WebServiceContext arg0, String arg1, List<BusinessInfo> arg2) {
        return arg2;
    }

    @Override
    public List<TModel> filterTModels(WebServiceContext arg0, String arg1, List<TModel> arg2) {
        return arg2;
    }

    @Override
    public List<BindingTemplate> filterBindingTemplates(WebServiceContext arg0, String arg1, List<BindingTemplate> arg2) {
        return arg2;
    }

    @Override
    public List<RelatedBusinessInfo> filtedRelatedBusinessInfos(WebServiceContext arg0, String arg1, List<RelatedBusinessInfo> arg2) {
        return arg2;
    }

    @Override
    public List<ServiceInfo> filterServiceInfo(WebServiceContext arg0, String arg1, List<ServiceInfo> arg2) {
        return arg2;
    }

    @Override
    public List<TModelInfo> filterTModelInfo(WebServiceContext arg0, String arg1, List<TModelInfo> arg2) {
        return arg2;
    }

    @Override
    public List<OperationalInfo> filterOperationalInfo(WebServiceContext arg0, String arg1, List<OperationalInfo> arg2) {
        return arg2;
    }

}
