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
package org.apache.juddi.api.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jws.WebService;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.Property;
import org.apache.juddi.v3.client.mapping.MapUDDIv2Tov3;
import org.apache.juddi.v3.client.mapping.MapUDDIv3Tov2;
import org.uddi.api_v2.BindingDetail;
import org.uddi.api_v2.BusinessDetail;
import org.uddi.api_v2.BusinessDetailExt;
import org.uddi.api_v2.BusinessList;
import org.uddi.api_v2.FindBinding;
import org.uddi.api_v2.FindBusiness;
import org.uddi.api_v2.FindRelatedBusinesses;
import org.uddi.api_v2.FindService;
import org.uddi.api_v2.FindTModel;
import org.uddi.api_v2.GetBindingDetail;
import org.uddi.api_v2.GetBusinessDetail;
import org.uddi.api_v2.GetBusinessDetailExt;
import org.uddi.api_v2.GetServiceDetail;
import org.uddi.api_v2.GetTModelDetail;
import org.uddi.api_v2.RelatedBusinessesList;
import org.uddi.api_v2.ServiceDetail;
import org.uddi.api_v2.ServiceList;
import org.uddi.api_v2.TModelDetail;
import org.uddi.api_v2.TModelList;
import org.uddi.v2_service.DispositionReport;
import org.uddi.v2_service.Inquire;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * UDDI v2 Implementation for the Inquiry service. This implementation is
 * basically a wrapper and API translator that translates and forwards the
 * request to our UDDIv3 Inquiry implementation.<br><br>
 * This class is a BETA feature and is largely untested. Please report any
 * issues
 *
 * @author <a href="mailto:alexoree.apache.org">Alex O'Ree</a>
 * @since 3.2
 */
@WebService(serviceName = "Inquire", targetNamespace = "urn:uddi-org:inquiry_v2",
        endpointInterface = "org.uddi.v2_service.Inquire")
public class UDDIv2InquiryImpl implements Inquire {

        private static Log logger = LogFactory.getLog(UDDIv2InquiryImpl.class);
        static UDDIInquiryImpl inquiryService = new UDDIInquiryImpl();

        public UDDIv2InquiryImpl() {
                logger.warn("This implementation of UDDIv2 Inquire service " + UDDIv2InquiryImpl.class.getCanonicalName() + " is considered BETA. Please"
                        + " report any issues to https://issues.apache.org/jira/browse/JUDDI");
        }

        static String nodeId=null;
        private static String getNodeID(){
                 try {
                         nodeId=AppConfig.getConfiguration().getString(Property.JUDDI_NODE_ID);
                 } catch (ConfigurationException ex) {
                         logger.warn(ex.getMessage());
                         nodeId="JUDDI_v3";
                 }
                 return nodeId;
        }
        
        @Override
        public BindingDetail findBinding(FindBinding body) throws DispositionReport {
                try {
                        return MapUDDIv3Tov2.MapBindingDetail(inquiryService.findBinding(MapUDDIv2Tov3.MapFindBinding(body)), getNodeID());
                } catch (DispositionReportFaultMessage ex) {
                        throw MapUDDIv3Tov2.MapException(ex, getNodeID());
                }
        }

        @Override
        public BusinessList findBusiness(FindBusiness body) throws DispositionReport {
                try {
                        return MapUDDIv3Tov2.MapBusinessListEntity(inquiryService.findBusiness(MapUDDIv2Tov3.MapFindBusiness(body)), getNodeID());
                } catch (DispositionReportFaultMessage ex) {
                        throw MapUDDIv3Tov2.MapException(ex, getNodeID());
                }

        }

        @Override
        public RelatedBusinessesList findRelatedBusinesses(FindRelatedBusinesses body) throws DispositionReport {
                try {
                        return MapUDDIv3Tov2.MapRelatedBusinessList(inquiryService.findRelatedBusinesses(MapUDDIv2Tov3.MapFindRelatedBusiness(body)), getNodeID());
                } catch (DispositionReportFaultMessage ex) {
                        throw MapUDDIv3Tov2.MapException(ex, getNodeID());
                }
        }

        @Override
        public ServiceList findService(FindService body) throws DispositionReport {
                try {
                        return MapUDDIv3Tov2.MapServiceList(inquiryService.findService(MapUDDIv2Tov3.MapFindService(body)), getNodeID());
                } catch (DispositionReportFaultMessage ex) {
                        throw MapUDDIv3Tov2.MapException(ex, getNodeID());
                }
        }

        @Override
        public TModelList findTModel(FindTModel body) throws DispositionReport {
                try {
                        return MapUDDIv3Tov2.MapTModelListElement(inquiryService.findTModel(MapUDDIv2Tov3.MapFindTModel(body)), getNodeID());
                } catch (DispositionReportFaultMessage ex) {
                        throw MapUDDIv3Tov2.MapException(ex, getNodeID());
                }
        }

        @Override
        public BindingDetail getBindingDetail(GetBindingDetail body) throws DispositionReport {
                try {
                        return MapUDDIv3Tov2.MapBindingDetail(inquiryService.getBindingDetail(MapUDDIv2Tov3.MapGetBindingDetail(body)), getNodeID());
                } catch (DispositionReportFaultMessage ex) {
                        throw MapUDDIv3Tov2.MapException(ex, getNodeID());
                }
        }

        @Override
        public BusinessDetail getBusinessDetail(GetBusinessDetail body) throws DispositionReport {
                try {
                        BusinessDetail MapBusinessDetail = MapUDDIv3Tov2.MapBusinessDetail(inquiryService.getBusinessDetail(MapUDDIv2Tov3.MapGetBusinessDetail(body)), getNodeID());
                       // StringWriter sw = new StringWriter();
                       // JAXB.marshal(MapBusinessDetail, sw);
                      //  logger.info(sw.toString());
                        return MapBusinessDetail;
                } catch (DispositionReportFaultMessage ex) {
                        throw MapUDDIv3Tov2.MapException(ex, getNodeID());
                }
        }

        @Override
        public BusinessDetailExt getBusinessDetailExt(GetBusinessDetailExt body) throws DispositionReport {
                try {
                        return MapUDDIv3Tov2.MapBusinessDetailExt(inquiryService.getBusinessDetail(MapUDDIv2Tov3.MapGetBusinessDetailExt(body)));
                } catch (DispositionReportFaultMessage ex) {
                        throw MapUDDIv3Tov2.MapException(ex, getNodeID());
                }
        }

        @Override
        public ServiceDetail getServiceDetail(GetServiceDetail body) throws DispositionReport {
                try {
                        return MapUDDIv3Tov2.MapServiceDetail(inquiryService.getServiceDetail(MapUDDIv2Tov3.MapGetServiceDetail(body)), getNodeID());
                } catch (DispositionReportFaultMessage ex) {
                        throw MapUDDIv3Tov2.MapException(ex, getNodeID());
                }
        }

        @Override
        public TModelDetail getTModelDetail(GetTModelDetail body) throws DispositionReport {
                try {
                	//remove duplicates using a set
                	Set<String> keyList = new HashSet(body.getTModelKey());
                	body.getTModelKey().clear();
                	body.getTModelKey().addAll(keyList);
                        return MapUDDIv3Tov2.MapTModelDetail(inquiryService.getTModelDetail(MapUDDIv2Tov3.MapGetTModelDetail(body)), getNodeID());
                } catch (DispositionReportFaultMessage ex) {
                        throw MapUDDIv3Tov2.MapException(ex, getNodeID());
                }
        }

}
