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

import java.util.List;
import javax.jws.WebService;
import javax.xml.ws.Holder;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.Property;
import org.apache.juddi.v3.client.mapping.MapUDDIv2Tov3;
import org.apache.juddi.v3.client.mapping.MapUDDIv3Tov2;
import org.uddi.api_v2.AddPublisherAssertions;
import org.uddi.api_v2.AssertionStatusReport;
import org.uddi.api_v2.AuthToken;
import org.uddi.api_v2.BindingDetail;
import org.uddi.api_v2.BusinessDetail;
import org.uddi.api_v2.DeleteBinding;
import org.uddi.api_v2.DeleteBusiness;
import org.uddi.api_v2.DeletePublisherAssertions;
import org.uddi.api_v2.DeleteService;
import org.uddi.api_v2.DeleteTModel;
import org.uddi.api_v2.DiscardAuthToken;
import org.uddi.api_v2.DispositionReport;
import org.uddi.api_v2.GetAssertionStatusReport;
import org.uddi.api_v2.GetAuthToken;
import org.uddi.api_v2.GetPublisherAssertions;
import org.uddi.api_v2.GetRegisteredInfo;
import org.uddi.api_v2.PublisherAssertions;
import org.uddi.api_v2.RegisteredInfo;
import org.uddi.api_v2.Result;
import org.uddi.api_v2.SaveBinding;
import org.uddi.api_v2.SaveBusiness;
import org.uddi.api_v2.SaveService;
import org.uddi.api_v2.SaveTModel;
import org.uddi.api_v2.ServiceDetail;
import org.uddi.api_v2.SetPublisherAssertions;
import org.uddi.api_v2.TModelDetail;
import org.uddi.api_v2.Truncated;
import org.uddi.api_v3.CompletionStatus;
import org.uddi.api_v3.PublisherAssertion;
import org.uddi.v2_service.Publish;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * UDDI v2 Implementation for the Publish service. This implementation is
 * basically a wrapper and API translator that translates and forwards the
 * request to our UDDIv3 Publish implementation<br><br>
 * This class is a BETA feature and is largely untested. Please report any
 * issues
 *
 * @author <a href="mailto:alexoree.apache.org">Alex O'Ree</a>
 * @since 3.2
 */
@WebService(serviceName = "Publish", targetNamespace = "urn:uddi-org:inquiry_v2",
        endpointInterface = "org.uddi.v2_service.Publish")
public class UDDIv2PublishImpl implements Publish {

        public UDDIv2PublishImpl() {
                logger.warn("This implementation of UDDIv2 Publish service " + UDDIv2PublishImpl.class.getCanonicalName() + " is considered BETA. Please"
                        + " report any issues to https://issues.apache.org/jira/browse/JUDDI");
        }

        static String nodeId = null;

        private static String getNodeID() {
                try {
                        nodeId = AppConfig.getConfiguration().getString(Property.JUDDI_NODE_ID);
                } catch (ConfigurationException ex) {
                        logger.warn(ex.getMessage());
                        nodeId = "JUDDI_v3";
                }
                return nodeId;
        }
        private static Log logger = LogFactory.getLog(UDDIv2PublishImpl.class);
        static UDDIPublicationImpl publishService = new UDDIPublicationImpl();
        static UDDISecurityImpl securityService = new UDDISecurityImpl();

        @Override
        public DispositionReport addPublisherAssertions(AddPublisherAssertions body) throws org.uddi.v2_service.DispositionReport {
                try {
                        publishService.addPublisherAssertions(MapUDDIv2Tov3.MapAddPublisherAssertions(body));
                } catch (DispositionReportFaultMessage ex) {
                        throw MapUDDIv3Tov2.MapException(ex, getNodeID());
                }
                return getSuccessMessage();
        }

        @Override
        public DispositionReport deleteBinding(DeleteBinding body) throws org.uddi.v2_service.DispositionReport {
                try {
                        publishService.deleteBinding(MapUDDIv2Tov3.MapDeleteBinding(body));
                } catch (DispositionReportFaultMessage ex) {
                        throw MapUDDIv3Tov2.MapException(ex, getNodeID());
                }
                return getSuccessMessage();
        }

        @Override
        public DispositionReport deleteBusiness(DeleteBusiness body) throws org.uddi.v2_service.DispositionReport {
                try {
                        publishService.deleteBusiness(MapUDDIv2Tov3.MapDeleteBusiness(body));
                } catch (DispositionReportFaultMessage ex) {
                        throw MapUDDIv3Tov2.MapException(ex, getNodeID());
                }
                return getSuccessMessage();
        }

        @Override
        public DispositionReport deletePublisherAssertions(DeletePublisherAssertions body) throws org.uddi.v2_service.DispositionReport {
                try {
                        publishService.deletePublisherAssertions(MapUDDIv2Tov3.MapDeletePublisherAssertion(body));
                } catch (DispositionReportFaultMessage ex) {
                        throw MapUDDIv3Tov2.MapException(ex, getNodeID());
                }
                return getSuccessMessage();
        }

        @Override
        public DispositionReport deleteService(DeleteService body) throws org.uddi.v2_service.DispositionReport {
                try {
                        publishService.deleteService(MapUDDIv2Tov3.MapDeleteService(body));
                } catch (DispositionReportFaultMessage ex) {
                        throw MapUDDIv3Tov2.MapException(ex, getNodeID());
                }
                return getSuccessMessage();
        }

        @Override
        public DispositionReport deleteTModel(DeleteTModel body) throws org.uddi.v2_service.DispositionReport {
                try {
                        publishService.deleteTModel(MapUDDIv2Tov3.MapDeleteTModel(body));
                } catch (DispositionReportFaultMessage ex) {
                        throw MapUDDIv3Tov2.MapException(ex, getNodeID());
                }
                return getSuccessMessage();
        }

        @Override
        public DispositionReport discardAuthToken(DiscardAuthToken body) throws org.uddi.v2_service.DispositionReport {
                try {
                        securityService.discardAuthToken(new org.uddi.api_v3.DiscardAuthToken(body.getAuthInfo()));
                } catch (DispositionReportFaultMessage ex) {
                        throw MapUDDIv3Tov2.MapException(ex, getNodeID());
                }
                return getSuccessMessage();
        }

        @Override
        public AssertionStatusReport getAssertionStatusReport(GetAssertionStatusReport body) throws org.uddi.v2_service.DispositionReport {
                try {
                        
                       return MapUDDIv3Tov2.MapAssertionStatusReport( publishService.getAssertionStatusReport(body.getAuthInfo(), MapUDDIv2Tov3.MapCompletionStatus(body.getCompletionStatus())));
                } catch (DispositionReportFaultMessage ex) {
                        throw MapUDDIv3Tov2.MapException(ex, getNodeID());
                }
        }

        @Override
        public AuthToken getAuthToken(GetAuthToken body) throws org.uddi.v2_service.DispositionReport {
                try {
                        org.uddi.api_v3.GetAuthToken r = new org.uddi.api_v3.GetAuthToken();
                        r.setCred(body.getCred());
                        r.setUserID(body.getUserID());
                        org.uddi.api_v3.AuthToken authToken = securityService.getAuthToken(r);
                        AuthToken ret = new AuthToken();
                        ret.setAuthInfo(authToken.getAuthInfo());
                        ret.setGeneric("2.0");
                        ret.setOperator(getNodeID());
                        return ret;
                } catch (DispositionReportFaultMessage ex) {
                        throw MapUDDIv3Tov2.MapException(ex, getNodeID());
                }
        }

        @Override
        public PublisherAssertions getPublisherAssertions(GetPublisherAssertions body) throws org.uddi.v2_service.DispositionReport {
                try {
                        return MapUDDIv3Tov2.MapPublisherAssertions(publishService.getPublisherAssertions(body.getAuthInfo()), getNodeID());
                } catch (DispositionReportFaultMessage ex) {
                        throw MapUDDIv3Tov2.MapException(ex, getNodeID());
                }
        }

        @Override
        public RegisteredInfo getRegisteredInfo(GetRegisteredInfo body) throws org.uddi.v2_service.DispositionReport {
                try {
                        return MapUDDIv3Tov2.MapRegisteredInfo(publishService.getRegisteredInfo(MapUDDIv2Tov3.MapGetRegisteredInfo(body)), getNodeID());
                } catch (DispositionReportFaultMessage ex) {
                        throw MapUDDIv3Tov2.MapException(ex, getNodeID());
                }

        }

        @Override
        public BindingDetail saveBinding(SaveBinding body) throws org.uddi.v2_service.DispositionReport {
                try {
                        return MapUDDIv3Tov2.MapBindingDetail(publishService.saveBinding(MapUDDIv2Tov3.MapSaveBinding(body)), getNodeID());
                } catch (DispositionReportFaultMessage ex) {
                        throw MapUDDIv3Tov2.MapException(ex, getNodeID());
                }
        }

        @Override
        public BusinessDetail saveBusiness(SaveBusiness body) throws org.uddi.v2_service.DispositionReport {
                try {
                        return MapUDDIv3Tov2.MapBusinessDetail(publishService.saveBusiness(MapUDDIv2Tov3.MapSaveBusiness(body)), getNodeID());
                } catch (DispositionReportFaultMessage ex) {
                        throw MapUDDIv3Tov2.MapException(ex, getNodeID());
                }
        }

        @Override
        public ServiceDetail saveService(SaveService body) throws org.uddi.v2_service.DispositionReport {
                try {
                        return MapUDDIv3Tov2.MapServiceDetail(publishService.saveService(MapUDDIv2Tov3.MapSaveService(body)), getNodeID());
                } catch (DispositionReportFaultMessage ex) {
                        throw MapUDDIv3Tov2.MapException(ex, getNodeID());
                }
        }

        @Override
        public TModelDetail saveTModel(SaveTModel body) throws org.uddi.v2_service.DispositionReport {
                try {
                        return MapUDDIv3Tov2.MapTModelDetail(publishService.saveTModel(MapUDDIv2Tov3.MapSaveTModel(body)), getNodeID());
                } catch (DispositionReportFaultMessage ex) {
                        throw MapUDDIv3Tov2.MapException(ex, getNodeID());
                }
        }

        @Override
        public PublisherAssertions setPublisherAssertions(SetPublisherAssertions body) throws org.uddi.v2_service.DispositionReport {
                try {
                        Holder<List<PublisherAssertion>> req = new Holder<List<PublisherAssertion>>();
                        req.value = MapUDDIv2Tov3.MapSetPublisherAssertions(body);
                        publishService.setPublisherAssertions(body.getAuthInfo(), req);
                        return MapUDDIv3Tov2.MapPublisherAssertions(req.value, getNodeID());
                } catch (DispositionReportFaultMessage ex) {
                        throw MapUDDIv3Tov2.MapException(ex, getNodeID());
                }
        }

        private DispositionReport getSuccessMessage() {
                DispositionReport r = new DispositionReport();
                r.setGeneric("2.0");
                r.setTruncated(Truncated.FALSE);
                Result x = new Result();
                r.setOperator(getNodeID());
                r.getResult().add(x);
                return r;
        }

}
