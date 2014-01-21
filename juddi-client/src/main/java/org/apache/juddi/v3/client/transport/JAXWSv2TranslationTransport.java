/*
 * Copyright 2001-2009 The Apache Software Foundation.
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
package org.apache.juddi.v3.client.transport;

import java.rmi.RemoteException;
import java.util.List;
import org.apache.juddi.v3.client.transport.*;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.DefaultEditorKit;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.UDDIServiceV2;
import org.apache.juddi.v3.client.config.Property;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.cryptor.CryptorFactory;
import org.apache.juddi.v3.client.mapping.MapUDDIv2Tov3;
import org.apache.juddi.v3.client.mapping.MapUDDIv3Tov2;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.uddi.api_v2.PublisherAssertions;
import org.uddi.api_v2.SetPublisherAssertions;
import org.uddi.api_v3.AddPublisherAssertions;
import org.uddi.api_v3.AssertionStatusItem;
import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.CompletionStatus;
import org.uddi.api_v3.DeleteBinding;
import org.uddi.api_v3.DeleteBusiness;
import org.uddi.api_v3.DeletePublisherAssertions;
import org.uddi.api_v3.DeleteService;
import org.uddi.api_v3.DeleteTModel;
import org.uddi.api_v3.FindBinding;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindRelatedBusinesses;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.GetBindingDetail;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.GetOperationalInfo;
import org.uddi.api_v3.GetRegisteredInfo;
import org.uddi.api_v3.GetServiceDetail;
import org.uddi.api_v3.GetTModelDetail;
import org.uddi.api_v3.OperationalInfos;
import org.uddi.api_v3.PublisherAssertion;
import org.uddi.api_v3.RegisteredInfo;
import org.uddi.api_v3.RelatedBusinessesList;
import org.uddi.api_v3.SaveBinding;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.SaveService;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.api_v3.ServiceList;
import org.uddi.api_v3.TModelDetail;
import org.uddi.api_v3.TModelList;
import org.uddi.v2_service.DispositionReport;
import org.uddi.v2_service.Inquire;
import org.uddi.v2_service.Publish;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDICustodyTransferPortType;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionListenerPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;

/**
 * JAXWS Transport for UDDIv2 bindings. Use this class for accessing UDDIv2
 * server using the UDDIv3 APIs
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 * @since 3.2
 */
public class JAXWSv2TranslationTransport extends JAXWSTransport implements UDDIInquiryPortType, UDDIPublicationPortType {

        private static Log logger = LogFactory.getLog(JAXWSv2TranslationTransport.class);

        String nodeName = null;
        String clientName = null;
        Inquire inquiryService = null;

        Publish publishService = null;

        public JAXWSv2TranslationTransport() {
                super();
                this.nodeName = Transport.DEFAULT_NODE_NAME;
        }

        public JAXWSv2TranslationTransport(String nodeName) {
                super();
                this.nodeName = nodeName;
        }

        public JAXWSv2TranslationTransport(String clientName, String nodeName) {
                super();
                this.clientName = clientName;
                this.nodeName = nodeName;
        }

        public Inquire getUDDIInquiryv2Service(String endpointURL) throws TransportException {
                try {
                        if (inquiryService == null) {
                                if (endpointURL == null) {
                                        UDDIClient client = UDDIClientContainer.getUDDIClient(clientName);
                                        endpointURL = client.getClientConfig().getUDDINode(nodeName).getInquiryUrl();
                                }
                                UDDIServiceV2 service = new UDDIServiceV2();
                                inquiryService = service.getInquire();
                        }
                        Map<String, Object> requestContext = ((BindingProvider) inquiryService).getRequestContext();
                        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointURL);
                        setCredentials(requestContext);
                } catch (Exception e) {
                        throw new TransportException(e.getMessage(), e);
                }
                return inquiryService;
        }

        public Publish getUDDIPublishv2Service(String endpointURL) throws TransportException {
                try {
                        if (publishService == null) {

                                if (endpointURL == null) {
                                        UDDIClient client = UDDIClientContainer.getUDDIClient(clientName);
                                        endpointURL = client.getClientConfig().getUDDINode(nodeName).getPublishUrl();
                                }
                                UDDIServiceV2 service = new UDDIServiceV2();
                                publishService = service.getPublish();
                        }
                        Map<String, Object> requestContext = ((BindingProvider) publishService).getRequestContext();
                        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointURL);
                        setCredentials(requestContext);
                } catch (Exception e) {
                        throw new TransportException(e.getMessage(), e);
                }
                return publishService;
        }

        public String getNodeName() {
                return nodeName;
        }

        public void setNodeName(String nodeName) {
                this.nodeName = nodeName;
        }

        /**
         * Sets the credentials on the RequestContext if the services are
         * protected by Basic Authentication. The username and password are
         * obtained from the uddi.xml.
         *
         * @param requestContext
         * @throws ConfigurationException
         */
        private void setCredentials(Map<String, Object> requestContext) throws ConfigurationException {
                UDDIClient client = UDDIClientContainer.getUDDIClient(clientName);
                Properties properties = client.getClientConfig().getUDDINode(nodeName).getProperties();
                if (properties != null) {
                        String username = null;
                        String password = null;
                        if (properties.containsKey(Property.BASIC_AUTH_USERNAME)) {
                                username = properties.getProperty(Property.BASIC_AUTH_USERNAME);
                        }
                        if (properties.containsKey(Property.BASIC_AUTH_PASSWORD)) {
                                password = properties.getProperty(Property.BASIC_AUTH_PASSWORD);
                        }
                        String cipher = null;
                        boolean isEncrypted = false;
                        if (properties.containsKey(Property.BASIC_AUTH_PASSWORD_CP)) {
                                cipher = properties.getProperty(Property.BASIC_AUTH_PASSWORD_CP);
                        }
                        if (properties.containsKey(Property.BASIC_AUTH_PASSWORD_IS_ENC)) {
                                isEncrypted = Boolean.parseBoolean(properties.getProperty(Property.BASIC_AUTH_PASSWORD_IS_ENC));
                        }
                        if (username != null && password != null) {
                                requestContext.put(BindingProvider.USERNAME_PROPERTY, username);
                                if (isEncrypted) {
                                        try {
                                                requestContext.put(BindingProvider.PASSWORD_PROPERTY, CryptorFactory.getCryptor(cipher).decrypt(password));
                                        } catch (Exception ex) {
                                                logger.error("Unable to decrypt password!", ex);
                                        }
                                } else {
                                        requestContext.put(BindingProvider.PASSWORD_PROPERTY, password);
                                }
                        }
                }
        }

        @Override
        public UDDIInquiryPortType getUDDIInquiryService(String endpointURL) throws TransportException {
                getUDDIInquiryv2Service(endpointURL);
                return this;
        }

        @Override
        public UDDISecurityPortType getUDDISecurityService(String endpointURL) throws TransportException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public UDDIPublicationPortType getUDDIPublishService(String endpointURL) throws TransportException {
                getUDDIPublishv2Service(endpointURL);
                return this;
        }

        @Override
        public UDDISubscriptionPortType getUDDISubscriptionService(String enpointURL) throws TransportException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public UDDICustodyTransferPortType getUDDICustodyTransferService(String enpointURL) throws TransportException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public UDDISubscriptionListenerPortType getUDDISubscriptionListenerService(String enpointURL) throws TransportException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public BindingDetail findBinding(FindBinding body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        return MapUDDIv2Tov3.MapBindingDetail(inquiryService.findBinding(MapUDDIv3Tov2.MapFindBinding(body)));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public BusinessList findBusiness(FindBusiness body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        return MapUDDIv2Tov3.MapBusinessList(inquiryService.findBusiness(MapUDDIv3Tov2.MapFindBusiness(body)));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public RelatedBusinessesList findRelatedBusinesses(FindRelatedBusinesses body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        return MapUDDIv2Tov3.MapRelatedBusinessesList(inquiryService.findRelatedBusinesses(MapUDDIv3Tov2.MapFindRelatedBusiness(body)));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public ServiceList findService(FindService body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        return MapUDDIv2Tov3.MapServiceList(inquiryService.findService(MapUDDIv3Tov2.MapFindService(body)));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public TModelList findTModel(FindTModel body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        return MapUDDIv2Tov3.MapTModelList(inquiryService.findTModel(MapUDDIv3Tov2.MapFindTModel(body)));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public BindingDetail getBindingDetail(GetBindingDetail body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        return MapUDDIv2Tov3.MapBindingDetail(inquiryService.getBindingDetail(MapUDDIv3Tov2.MapGetBindingDetail(body)));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public BusinessDetail getBusinessDetail(GetBusinessDetail body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        return MapUDDIv2Tov3.MapBusinessDetail(inquiryService.getBusinessDetail(MapUDDIv3Tov2.MapGetBusinessDetail(body)));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public OperationalInfos getOperationalInfo(GetOperationalInfo body) throws DispositionReportFaultMessage, RemoteException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ServiceDetail getServiceDetail(GetServiceDetail body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        return MapUDDIv2Tov3.MapServiceDetail(inquiryService.getServiceDetail(MapUDDIv3Tov2.MapGetServiceDetail(body)));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public TModelDetail getTModelDetail(GetTModelDetail body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        return MapUDDIv2Tov3.MapTModelDetail(inquiryService.getTModelDetail(MapUDDIv3Tov2.MapGetTModelDetail(body)));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public void addPublisherAssertions(AddPublisherAssertions body) throws DispositionReportFaultMessage, RemoteException {
                 try {
                        publishService.addPublisherAssertions(MapUDDIv3Tov2.MapAddPublisherAssertions(body));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public void deleteBinding(DeleteBinding body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        publishService.deleteBinding(MapUDDIv3Tov2.MapDeleteBinding(body));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public void deleteBusiness(DeleteBusiness body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        publishService.deleteBusiness(MapUDDIv3Tov2.MapDeleteBusiness(body));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public void deletePublisherAssertions(DeletePublisherAssertions body) throws DispositionReportFaultMessage, RemoteException {
                 try {
                        publishService.deletePublisherAssertions(MapUDDIv3Tov2.MapDeletePublisherAssertions(body));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public void deleteService(DeleteService body) throws DispositionReportFaultMessage, RemoteException {
                  try {
                        publishService.deleteService(MapUDDIv3Tov2.MapDeleteService(body));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public void deleteTModel(DeleteTModel body) throws DispositionReportFaultMessage, RemoteException {
                  try {
                        publishService.deleteTModel(MapUDDIv3Tov2.MapDeleteTModel(body));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public List<AssertionStatusItem> getAssertionStatusReport(String authInfo, CompletionStatus completionStatus) throws DispositionReportFaultMessage, RemoteException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public List<PublisherAssertion> getPublisherAssertions(String authInfo) throws DispositionReportFaultMessage, RemoteException {
                try {
                        return MapUDDIv2Tov3.MapListPublisherAssertion(publishService.getPublisherAssertions(MapUDDIv3Tov2.MapGetPublisherAssertions(authInfo)));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public RegisteredInfo getRegisteredInfo(GetRegisteredInfo body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        return MapUDDIv2Tov3.MapListRegisteredInfo(publishService.getRegisteredInfo(MapUDDIv3Tov2.MapGetRegisteredInfo(body)));
                } catch (DispositionReport ex) {
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
        }

        @Override
        public BusinessDetail saveBusiness(SaveBusiness body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        return MapUDDIv2Tov3.MapBusinessDetail(publishService.saveBusiness(MapUDDIv3Tov2.MapSaveBusiness(body)));
                } catch (DispositionReport ex) {
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
        }

        @Override
        public TModelDetail saveTModel(SaveTModel body) throws DispositionReportFaultMessage, RemoteException {
                 try {
                        return MapUDDIv2Tov3.MapTModelDetail(publishService.saveTModel(MapUDDIv3Tov2.MapSaveTModel(body)));
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public void setPublisherAssertions(String authInfo, Holder<List<PublisherAssertion>> publisherAssertion) throws DispositionReportFaultMessage, RemoteException {
                  try {
                          SetPublisherAssertions req = MapUDDIv3Tov2.MapSetPublisherAssertions(publisherAssertion.value);
                          PublisherAssertions setPublisherAssertions = publishService.setPublisherAssertions(req);
                          publisherAssertion.value = MapUDDIv2Tov3.MapListPublisherAssertion(setPublisherAssertions);

                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

}
