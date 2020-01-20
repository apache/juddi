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
package org.apache.juddi.v3.client.transport.wrapper;

import java.rmi.RemoteException;
import java.util.Map;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.soap.SOAPFaultException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.UDDIServiceV2;
import org.apache.juddi.v3.client.mapping.MapUDDIv2Tov3;
import org.apache.juddi.v3.client.mapping.MapUDDIv3Tov2;
import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.FindBinding;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindRelatedBusinesses;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.GetBindingDetail;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.GetOperationalInfo;
import org.uddi.api_v3.GetServiceDetail;
import org.uddi.api_v3.GetTModelDetail;
import org.uddi.api_v3.OperationalInfo;
import org.uddi.api_v3.OperationalInfos;
import org.uddi.api_v3.RelatedBusinessesList;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.api_v3.ServiceList;
import org.uddi.api_v3.TModelDetail;
import org.uddi.api_v3.TModelList;
import org.uddi.v2_service.DispositionReport;
import org.uddi.v2_service.Inquire;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDIInquiryPortType;

/**
 * This class provides a wrapper to enable UDDIv3 clients to talk to UDDIv2 servers
 * via JAXWS Transport. It handles all translations for Inquiry service methods.
 * @author <a href="alexoree@apache.org">Alex O'Ree</a>
 * @since 3.2
 */
public class Inquiry3to2 implements UDDIInquiryPortType, BindingProvider {

        private static Log logger = LogFactory.getLog(Inquiry3to2.class);
        Inquire inquiryService = null;

        public Inquiry3to2() {

                UDDIServiceV2 service = new UDDIServiceV2();
                inquiryService = service.getInquire();

        }
        
        public Inquire getUDDIv2WebServiceClient(){
             return inquiryService;
        }

        @Override
        public BindingDetail findBinding(FindBinding body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        return MapUDDIv2Tov3.MapBindingDetail(inquiryService.findBinding(MapUDDIv3Tov2.MapFindBinding(body)));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                } catch (SOAPFaultException ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public BusinessList findBusiness(FindBusiness body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        org.uddi.api_v2.FindBusiness MapFindBusiness = MapUDDIv3Tov2.MapFindBusiness(body);
                        org.uddi.api_v2.BusinessList s = inquiryService.findBusiness(MapFindBusiness);
                        return MapUDDIv2Tov3.MapBusinessList(s);
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                } catch (SOAPFaultException ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public RelatedBusinessesList findRelatedBusinesses(FindRelatedBusinesses body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        return MapUDDIv2Tov3.MapRelatedBusinessesList(inquiryService.findRelatedBusinesses(MapUDDIv3Tov2.MapFindRelatedBusiness(body)));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                } catch (SOAPFaultException ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public ServiceList findService(FindService body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        return MapUDDIv2Tov3.MapServiceList(inquiryService.findService(MapUDDIv3Tov2.MapFindService(body)));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                } catch (SOAPFaultException ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public TModelList findTModel(FindTModel body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        return MapUDDIv2Tov3.MapTModelList(inquiryService.findTModel(MapUDDIv3Tov2.MapFindTModel(body)));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                } catch (SOAPFaultException ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public BindingDetail getBindingDetail(GetBindingDetail body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        return MapUDDIv2Tov3.MapBindingDetail(inquiryService.getBindingDetail(MapUDDIv3Tov2.MapGetBindingDetail(body)));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                } catch (SOAPFaultException ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public BusinessDetail getBusinessDetail(GetBusinessDetail body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        return MapUDDIv2Tov3.MapBusinessDetail(inquiryService.getBusinessDetail(MapUDDIv3Tov2.MapGetBusinessDetail(body)));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                } catch (SOAPFaultException ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        public static final String VERSION = "2.0";

        @Override
        public OperationalInfos getOperationalInfo(GetOperationalInfo body) throws DispositionReportFaultMessage, RemoteException {
                OperationalInfos ret = new OperationalInfos();

                for (int i = 0; i < body.getEntityKey().size(); i++) {
                        OperationalInfo oi = new OperationalInfo();
                        oi.setEntityKey(body.getEntityKey().get(i));
                        try {
                                org.uddi.api_v2.GetBusinessDetail businessDetail = new org.uddi.api_v2.GetBusinessDetail();
                                businessDetail.setGeneric(VERSION);
                                businessDetail.getBusinessKey().add(body.getEntityKey().get(i));
                                org.uddi.api_v2.BusinessDetail z = inquiryService.getBusinessDetail(businessDetail);
                                oi.setNodeID(z.getOperator());
                                oi.setAuthorizedName(z.getBusinessEntity().get(0).getAuthorizedName());
                        } catch (Exception ex) {
                                logger.warn(ex.getMessage(), ex);
                        }
                        if (oi.getAuthorizedName() != null) {
                                continue;
                        }
                        try {
                                org.uddi.api_v2.GetTModelDetail tModelDetail = new org.uddi.api_v2.GetTModelDetail();
                                tModelDetail.setGeneric(VERSION);
                                tModelDetail.getTModelKey().add(body.getEntityKey().get(i));
                                org.uddi.api_v2.TModelDetail z = inquiryService.getTModelDetail(tModelDetail);
                                oi.setNodeID(z.getOperator());
                                oi.setAuthorizedName(z.getTModel().get(0).getAuthorizedName());
                        } catch (Exception ex) {
                                logger.warn(ex.getMessage(), ex);
                        }
                        if (oi.getAuthorizedName() != null) {
                                continue;
                        }
                        try {
                                //get the service
                                org.uddi.api_v2.GetServiceDetail serviceDetail = new org.uddi.api_v2.GetServiceDetail();
                                serviceDetail.setGeneric(VERSION);
                                serviceDetail.getServiceKey().add(body.getEntityKey().get(i));
                                org.uddi.api_v2.ServiceDetail z = inquiryService.getServiceDetail(serviceDetail);
                                oi.setNodeID(z.getOperator());

                                org.uddi.api_v2.GetBusinessDetail businessDetail = new org.uddi.api_v2.GetBusinessDetail();
                                businessDetail.setGeneric(VERSION);
                                //its owning business
                                businessDetail.getBusinessKey().add(z.getBusinessService().get(0).getBusinessKey());
                                org.uddi.api_v2.BusinessDetail z2 = inquiryService.getBusinessDetail(businessDetail);
                                oi.setNodeID(z.getOperator());
                                oi.setAuthorizedName(z2.getBusinessEntity().get(0).getAuthorizedName());
                        } catch (Exception ex) {
                                logger.warn(ex.getMessage(), ex);
                        }
                        ret.getOperationalInfo().add(oi);
                        ret.setTruncated(false);
                }
                return ret;
        }

        @Override
        public ServiceDetail getServiceDetail(GetServiceDetail body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        return MapUDDIv2Tov3.MapServiceDetail(inquiryService.getServiceDetail(MapUDDIv3Tov2.MapGetServiceDetail(body)));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                } catch (SOAPFaultException ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public TModelDetail getTModelDetail(GetTModelDetail body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        return MapUDDIv2Tov3.MapTModelDetail(inquiryService.getTModelDetail(MapUDDIv3Tov2.MapGetTModelDetail(body)));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                } catch (SOAPFaultException ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public Map<String, Object> getRequestContext() {
                return ((BindingProvider) inquiryService).getRequestContext();
        }

        @Override
        public Map<String, Object> getResponseContext() {
                return ((BindingProvider) inquiryService).getResponseContext();
        }

        @Override
        public Binding getBinding() {
                return ((BindingProvider) inquiryService).getBinding();
        }

        @Override
        public EndpointReference getEndpointReference() {
                return ((BindingProvider) inquiryService).getEndpointReference();
        }

        @Override
        public <T extends EndpointReference> T getEndpointReference(Class<T> clazz) {
                return ((BindingProvider) inquiryService).getEndpointReference(clazz);
        }

}
