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
import java.util.ArrayList;
import java.util.List;
import javax.xml.ws.WebServiceContext;
import org.apache.juddi.api_v3.GetPermissionsMessageRequest;
import org.apache.juddi.api_v3.GetPermissionsMessageResponse;
import org.apache.juddi.api_v3.SetPermissionsMessageRequest;
import org.apache.juddi.api_v3.SetPermissionsMessageResponse;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.FatalErrorException;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessInfo;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.OperationalInfo;
import org.uddi.api_v3.RelatedBusinessInfo;
import org.uddi.api_v3.ServiceInfo;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelInfo;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * Default implementation, performs no additional filtering of data
 *
 * @since 3.4
 * @author Alex O'Ree
 */
public class AllowAllAccessControlImpl implements IAccessControl {

    @Override
    public List<BusinessService> filterServices(WebServiceContext arg0, UddiEntityPublisher user, List<BusinessService> arg2) {
        return new ArrayList<>(arg2);
    }

    @Override
    public List<BusinessEntity> filterBusinesses(WebServiceContext arg0, UddiEntityPublisher user, List<BusinessEntity> arg2) {
        return new ArrayList<>(arg2);
    }

    @Override
    public List<BusinessInfo> filterBusinessInfo(WebServiceContext arg0, UddiEntityPublisher user, List<BusinessInfo> arg2) {
        return new ArrayList<>(arg2);
    }

    @Override
    public List<TModel> filterTModels(WebServiceContext arg0, UddiEntityPublisher user, List<TModel> arg2) {
        return new ArrayList<>(arg2);
    }

    @Override
    public List<BindingTemplate> filterBindingTemplates(WebServiceContext arg0, UddiEntityPublisher user, List<BindingTemplate> arg2) {
        return new ArrayList<>(arg2);
    }

    @Override
    public List<RelatedBusinessInfo> filtedRelatedBusinessInfos(WebServiceContext arg0, UddiEntityPublisher user, List<RelatedBusinessInfo> arg2) {
        return new ArrayList<>(arg2);
    }

    @Override
    public List<ServiceInfo> filterServiceInfo(WebServiceContext arg0, UddiEntityPublisher user, List<ServiceInfo> arg2) {
        return new ArrayList<>(arg2);
    }

    @Override
    public List<TModelInfo> filterTModelInfo(WebServiceContext arg0, UddiEntityPublisher user, List<TModelInfo> arg2) {
        return new ArrayList<>(arg2);
    }

    @Override
    public List<OperationalInfo> filterOperationalInfo(WebServiceContext arg0, UddiEntityPublisher user, List<OperationalInfo> arg2) {
        return new ArrayList<>(arg2);
    }

    @Override
    public GetPermissionsMessageResponse getPermissions(GetPermissionsMessageRequest arg0) throws DispositionReportFaultMessage, RemoteException {
        GetPermissionsMessageResponse r = new GetPermissionsMessageResponse();
       
        return r;
    }

    @Override
    public SetPermissionsMessageResponse setPermissions(SetPermissionsMessageRequest arg0) throws DispositionReportFaultMessage, RemoteException {
          throw new FatalErrorException(new ErrorMessage("errors.Unsupported"));
    }

}
