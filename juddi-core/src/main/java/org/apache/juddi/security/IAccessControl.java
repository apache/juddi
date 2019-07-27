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

import java.rmi.RemoteException;
import java.util.List;
import javax.xml.ws.WebServiceContext;
import org.apache.juddi.api_v3.AccessLevel;
import org.apache.juddi.api_v3.EntityType;
import org.apache.juddi.api_v3.GetPermissionsMessageRequest;
import org.apache.juddi.api_v3.GetPermissionsMessageResponse;
import org.apache.juddi.api_v3.SetPermissionsMessageRequest;
import org.apache.juddi.api_v3.SetPermissionsMessageResponse;
import org.apache.juddi.model.UddiEntityPublisher;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessInfo;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.OperationalInfo;
import org.uddi.api_v3.RelatedBusinessInfos;
import org.uddi.api_v3.ServiceInfos;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelInfos;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * Provides an interface for a pluggable Fine Grained Access Control mechanism
 * for jUDDI
 *
 * @author Alex O'Ree
 * @since 3.4
 * @see AccessLevel
 */
public interface IAccessControl {

    public List<BusinessService> filterServices(WebServiceContext ctx, UddiEntityPublisher username, List<BusinessService> services);

    public List<BusinessEntity> filterBusinesses(WebServiceContext ctx, UddiEntityPublisher username, List<BusinessEntity> business);

    public List<BusinessInfo> filterBusinessInfo(WebServiceContext ctx, UddiEntityPublisher username, List<BusinessInfo> business);

    public List<TModel> filterTModels(WebServiceContext ctx, UddiEntityPublisher username, List<TModel> tmodels);

    public List<BindingTemplate> filterBindingTemplates(WebServiceContext ctx, UddiEntityPublisher username, List<BindingTemplate> bindings);

    public RelatedBusinessInfos filtedRelatedBusinessInfos(WebServiceContext ctx, UddiEntityPublisher username,  RelatedBusinessInfos bindings);

    public ServiceInfos filterServiceInfo(WebServiceContext ctx, UddiEntityPublisher authorizedName, ServiceInfos serviceInfo);

    public TModelInfos filterTModelInfo(WebServiceContext ctx, UddiEntityPublisher authorizedName, TModelInfos tModelInfo);

    public List<OperationalInfo> filterOperationalInfo(WebServiceContext ctx, UddiEntityPublisher authorizedName, List<OperationalInfo> operationalInfo);

    public GetPermissionsMessageResponse getPermissions(GetPermissionsMessageRequest arg0) throws DispositionReportFaultMessage, RemoteException;

    public SetPermissionsMessageResponse setPermissions(SetPermissionsMessageRequest arg0) throws DispositionReportFaultMessage, RemoteException;

    public boolean hasPermission(AccessLevel level, WebServiceContext ctx, UddiEntityPublisher username, String entityId, EntityType type);
}