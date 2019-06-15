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
 * Provides an interface for a pluggable Fine Grained Access Control mechanism
 * for jUDDI
 *
 * @author Alex O'Ree
 * @since 3.4
 * @see AccessLevel
 */
public interface IAccessControl {

    public List<BusinessService> filterServices(WebServiceContext ctx, String username, List<BusinessService> services);

    public List<BusinessEntity> filterBusinesses(WebServiceContext ctx, String username, List<BusinessEntity> business);

    public List<BusinessInfo> filterBusinessInfo(WebServiceContext ctx, String username, List<BusinessInfo> business);

    public List<TModel> filterTModels(WebServiceContext ctx, String username, List<TModel> tmodels);

    public List<BindingTemplate> filterBindingTemplates(WebServiceContext ctx, String username, List<BindingTemplate> bindings);

    public List<RelatedBusinessInfo> filtedRelatedBusinessInfos(WebServiceContext ctx, String username, List<RelatedBusinessInfo> bindings);

    public List<ServiceInfo> filterServiceInfo(WebServiceContext ctx, String authorizedName, List<ServiceInfo> serviceInfo);

    public List<TModelInfo> filterTModelInfo(WebServiceContext ctx, String authorizedName, List<TModelInfo> tModelInfo);

    public List<OperationalInfo> filterOperationalInfo(WebServiceContext ctx, String authorizedName, List<OperationalInfo> operationalInfo);
}
