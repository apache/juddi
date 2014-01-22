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
import java.util.List;
import java.util.Map;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.Holder;
import javax.xml.ws.soap.SOAPFaultException;
import org.apache.juddi.v3.client.UDDIServiceV2;
import org.apache.juddi.v3.client.mapping.MapUDDIv2Tov3;
import org.apache.juddi.v3.client.mapping.MapUDDIv3Tov2;
import org.uddi.api_v2.AssertionStatusReport;
import org.uddi.api_v2.PublisherAssertions;
import org.uddi.api_v2.SetPublisherAssertions;
import org.uddi.api_v3.AddPublisherAssertions;
import org.uddi.api_v3.AssertionStatusItem;
import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.CompletionStatus;
import org.uddi.api_v3.DeleteBinding;
import org.uddi.api_v3.DeleteBusiness;
import org.uddi.api_v3.DeletePublisherAssertions;
import org.uddi.api_v3.DeleteService;
import org.uddi.api_v3.DeleteTModel;
import org.uddi.api_v3.GetRegisteredInfo;
import org.uddi.api_v3.PublisherAssertion;
import org.uddi.api_v3.RegisteredInfo;
import org.uddi.api_v3.SaveBinding;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.SaveService;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.api_v3.TModelDetail;
import org.uddi.v2_service.DispositionReport;
import org.uddi.v2_service.Publish;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDIPublicationPortType;

/**
 *
 * @author Alex O'Ree
 */
public class Publish3to2 implements UDDIPublicationPortType, BindingProvider {

        Publish publishService=null;
        public Publish3to2(){
                
                UDDIServiceV2 service = new UDDIServiceV2();
                publishService = service.getPublish();

        }
        @Override
        public void addPublisherAssertions(AddPublisherAssertions body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        publishService.addPublisherAssertions(MapUDDIv3Tov2.MapAddPublisherAssertions(body));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                } catch (SOAPFaultException ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public void deleteBinding(DeleteBinding body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        publishService.deleteBinding(MapUDDIv3Tov2.MapDeleteBinding(body));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                } catch (SOAPFaultException ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public void deleteBusiness(DeleteBusiness body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        publishService.deleteBusiness(MapUDDIv3Tov2.MapDeleteBusiness(body));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                } catch (SOAPFaultException ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public void deletePublisherAssertions(DeletePublisherAssertions body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        publishService.deletePublisherAssertions(MapUDDIv3Tov2.MapDeletePublisherAssertions(body));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                } catch (SOAPFaultException ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public void deleteService(DeleteService body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        publishService.deleteService(MapUDDIv3Tov2.MapDeleteService(body));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                } catch (SOAPFaultException ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public void deleteTModel(DeleteTModel body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        publishService.deleteTModel(MapUDDIv3Tov2.MapDeleteTModel(body));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                } catch (SOAPFaultException ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public List<AssertionStatusItem> getAssertionStatusReport(String authInfo, CompletionStatus completionStatus) throws DispositionReportFaultMessage, RemoteException {
                try {
                        AssertionStatusReport assertionStatusReport = publishService.getAssertionStatusReport(MapUDDIv3Tov2.MapGetAssertionStatusReport(authInfo, completionStatus));
                        return MapUDDIv2Tov3.MapAssertionStatusItems(assertionStatusReport);
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                } catch (SOAPFaultException ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public List<PublisherAssertion> getPublisherAssertions(String authInfo) throws DispositionReportFaultMessage, RemoteException {
                try {
                        return MapUDDIv2Tov3.MapListPublisherAssertion(publishService.getPublisherAssertions(MapUDDIv3Tov2.MapGetPublisherAssertions(authInfo)));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                } catch (SOAPFaultException ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public RegisteredInfo getRegisteredInfo(GetRegisteredInfo body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        return MapUDDIv2Tov3.MapListRegisteredInfo(publishService.getRegisteredInfo(MapUDDIv3Tov2.MapGetRegisteredInfo(body)));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                } catch (SOAPFaultException ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }

        }

        @Override
        public BindingDetail saveBinding(SaveBinding body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        return MapUDDIv2Tov3.MapBindingDetail(publishService.saveBinding(MapUDDIv3Tov2.MapSaveBinding(body)));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
                catch (SOAPFaultException ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public BusinessDetail saveBusiness(SaveBusiness body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        return MapUDDIv2Tov3.MapBusinessDetail(publishService.saveBusiness(MapUDDIv3Tov2.MapSaveBusiness(body)));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
                 catch (SOAPFaultException ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public ServiceDetail saveService(SaveService body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        return MapUDDIv2Tov3.MapServiceDetail(publishService.saveService(MapUDDIv3Tov2.MapSaveService(body)));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
                 catch (SOAPFaultException ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public TModelDetail saveTModel(SaveTModel body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        return MapUDDIv2Tov3.MapTModelDetail(publishService.saveTModel(MapUDDIv3Tov2.MapSaveTModel(body)));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                } catch (SOAPFaultException ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
                
        }

        @Override
        public void setPublisherAssertions(String authInfo, Holder<List<PublisherAssertion>> publisherAssertion) throws DispositionReportFaultMessage, RemoteException {
                try {
                        SetPublisherAssertions req = MapUDDIv3Tov2.MapSetPublisherAssertions(publisherAssertion.value);
                        req.setAuthInfo(authInfo);
                        PublisherAssertions setPublisherAssertions = publishService.setPublisherAssertions(req);
                        publisherAssertion.value = MapUDDIv2Tov3.MapListPublisherAssertion(setPublisherAssertions);

                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                } catch (SOAPFaultException ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }


        @Override
        public Map<String, Object> getRequestContext() {
                return ((BindingProvider)publishService).getRequestContext();
        }

        @Override
        public Map<String, Object> getResponseContext() {
                return ((BindingProvider)publishService).getResponseContext();
        }

        @Override
        public Binding getBinding() {
                return ((BindingProvider)publishService).getBinding();
        }

        @Override
        public EndpointReference getEndpointReference() {
                return ((BindingProvider)publishService).getEndpointReference();
        }

        @Override
        public <T extends EndpointReference> T getEndpointReference(Class<T> clazz) {
                return ((BindingProvider)publishService).getEndpointReference(clazz);
        }

}
