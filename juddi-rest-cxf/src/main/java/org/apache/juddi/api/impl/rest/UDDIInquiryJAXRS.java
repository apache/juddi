/*
 * Copyright 2013 The Apache Software Foundation.
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
package org.apache.juddi.api.impl.rest;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.ws.rs.*;
import javax.wsdl.Definition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api.impl.UDDIInquiryImpl;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.api_v3.rest.UriContainer;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.mapping.ReadWSDL;
import org.apache.juddi.v3.client.mapping.URLLocalizerDefaultImpl;
import org.apache.juddi.v3.client.mapping.WSDL2UDDI;
import org.apache.juddi.v3.error.UDDIErrorHelper;
import org.uddi.api_v3.*;
import org.uddi.sub_v3.KeyBag;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * UDDI Inquiry functions via a JAX-RS REST API. It's basically a wrapper for
 * the REST fans to access UDDI from a URL pattern This class will ONLY deploy
 * using the Apache CXF WS stack for REST (JAX-RS)
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
@Path("/")
@Produces({"application/xml", "application/json"})
@org.apache.cxf.jaxrs.model.wadl.Description("This service provides access to UDDI data via a REST interface")
public class UDDIInquiryJAXRS {

        private static org.apache.juddi.api.impl.UDDIInquiryImpl inquiry = new UDDIInquiryImpl();
        private static Log log = LogFactory.getLog(UDDIInquiryJAXRS.class);

        /**
         * Returns the details of a business entity in JSON
         *
         * @param id
         * @return
         */
        @GET
        @Path("/JSON/businessKey/{id}")
        @Produces("application/json")
        @org.apache.cxf.jaxrs.model.wadl.Description("Returns the details of a business entity in JSON")
        public org.uddi.api_v3.BusinessEntity getBusinessDetailJSON(@PathParam("id") String id) throws WebApplicationException {
                return getBusinessDetail(id);
        }

        /**
         * Returns the details of a business entity in XML
         *
         * @param id
         * @return
         * @throws WebApplicationException
         */
        @GET
        @Path("/XML/businessKey/{id}")
        @Produces("application/xml")
        @org.apache.cxf.jaxrs.model.wadl.Description("Returns the details of a business entity in XML")
        public org.uddi.api_v3.BusinessEntity getBusinessDetailXML(@PathParam("id") String id) throws WebApplicationException {
                return getBusinessDetail(id);
        }

        private org.uddi.api_v3.BusinessEntity getBusinessDetail(String id) {
                GetBusinessDetail gbd = new GetBusinessDetail();
                gbd.getBusinessKey().add(id);
                BusinessDetail businessDetail;
                try {
                        businessDetail = inquiry.getBusinessDetail(gbd);
                        return businessDetail.getBusinessEntity().get(0);
                } catch (DispositionReportFaultMessage ex) {
                        HandleException(ex);
                }
                return null;
        }

        /**
         * Returns the details of a tModel entity in XML
         *
         * @param id
         * @return
         * @throws WebApplicationException
         */
        @GET
        @Path("/XML/tModelKey/{id}")
        @Produces("application/xml")
        @org.apache.cxf.jaxrs.model.wadl.Description("Returns the details of a tModel entity in XML")
        public org.uddi.api_v3.TModel getTModelDetailXML(@PathParam("id") String id) throws WebApplicationException {
                return getTModelDetail(id);
        }

        /**
         * Returns the details of a tModel entity in JSON
         *
         * @param id
         * @return
         * @throws WebApplicationException
         */
        @GET
        @Path("/JSON/tModelKey/{id}")
        @Produces("application/json")
        @org.apache.cxf.jaxrs.model.wadl.Description("Returns the details of a tModel entity in JSON")
        public org.uddi.api_v3.TModel getTModelDetailJSON(@PathParam("id") String id) throws WebApplicationException {
                return getTModelDetail(id);
        }

        private org.uddi.api_v3.TModel getTModelDetail(String id) {
                GetTModelDetail gbd = new GetTModelDetail();
                gbd.getTModelKey().add(id);

                try {
                        TModelDetail tModelDetail = inquiry.getTModelDetail(gbd);
                        return tModelDetail.getTModel().get(0);
                } catch (DispositionReportFaultMessage ex) {
                        HandleException(ex);
                }
                return null;
        }

        /**
         * Returns the details of a service entity in JSON
         *
         * @param id
         * @return
         * @throws WebApplicationException
         */
        @GET
        @Path("/JSON/serviceKey/{id}")
        @Produces("application/json")
        @org.apache.cxf.jaxrs.model.wadl.Description("Returns the details of a service entity in JSON")
        public org.uddi.api_v3.BusinessService getServiceDetailJSON(@PathParam("id") String id) throws WebApplicationException {
                return getServiceDetail(id);
        }

        @GET
        @Path("/JSON/getDetail")
        @Produces("application/json")
        @org.apache.cxf.jaxrs.model.wadl.Description("Returns the details of a UDDI entity in JSON, use query parameters"
                + "serviceKey,businessKey,tModelKey, bindingKey")
        public Object getDetailJSON(@QueryParam("serviceKey") String serviceKey,
                @QueryParam("businessKey") String businessKey,
                @QueryParam("tModelKey") String tModelKey,
                @QueryParam("bindingKey") String bindingKey) throws WebApplicationException {
                int params = 0;
                if (businessKey != null) {
                        params++;
                }
                if (tModelKey != null) {
                        params++;
                }
                if (bindingKey != null) {
                        params++;
                }
                if (serviceKey != null) {
                        params++;
                }
                if (params != 1) {
                        throw new WebApplicationException(400);
                }
                
                if (businessKey != null) {
                        return getBusinessDetail(businessKey);
                }
                if (tModelKey != null) {
                        return getTModelDetail(tModelKey);
                }
                if (bindingKey != null) {
                        return getBindingDetail(bindingKey);
                }
                if (serviceKey != null) {
                        return getServiceDetail(serviceKey);
                }
                throw new WebApplicationException(400);
        }

        @GET
        @Path("/XML/getDetail")
        @Produces("application/json")
        @org.apache.cxf.jaxrs.model.wadl.Description("Returns the details of a UDDI entity in XML, use query parameters"
                + "serviceKey,businessKey,tModelKey, bindingKey")
        public Object getDetailXML(@QueryParam("serviceKey") String serviceKey,
                @QueryParam("businessKey") String businessKey,
                @QueryParam("tModelKey") String tModelKey,
                @QueryParam("bindingKey") String bindingKey) throws WebApplicationException {
                int params = 0;
                if (businessKey != null) {
                        params++;
                }
                if (tModelKey != null) {
                        params++;
                }
                if (bindingKey != null) {
                        params++;
                }
                if (serviceKey != null) {
                        params++;
                }
                if (params != 1) {
                        throw new WebApplicationException(400);
                }
                if (businessKey != null) {
                        return getBusinessDetail(businessKey);
                }
                if (tModelKey != null) {
                        return getTModelDetail(tModelKey);
                }
                if (bindingKey != null) {
                        return getBindingDetail(bindingKey);
                }
                if (serviceKey != null) {
                        return getServiceDetail(serviceKey);
                }
                throw new WebApplicationException(400);
        }

        /**
         * Returns the details of a service entity in XML
         *
         * @param id
         * @return
         * @throws WebApplicationException
         */
        @GET
        @Path("/XML/serviceKey/{id}")
        @Produces("application/xml")
        @org.apache.cxf.jaxrs.model.wadl.Description("Returns the details of a service entity in XML")
        public org.uddi.api_v3.BusinessService getServiceDetailXML(@PathParam("id") String id) throws WebApplicationException {
                return getServiceDetail(id);
        }

        private BusinessService getServiceDetail(String id) {
                GetServiceDetail gbd = new GetServiceDetail();
                gbd.getServiceKey().add(id);

                try {
                        ServiceDetail serviceDetail = inquiry.getServiceDetail(gbd);
                        return serviceDetail.getBusinessService().get(0);
                } catch (DispositionReportFaultMessage ex) {
                        HandleException(ex);
                }
                return null;
        }

        /**
         * Returns the operational details of a given entity in JSON
         *
         * @param id
         * @return
         * @throws WebApplicationException
         */
        @GET
        @Path("/JSON/opInfo/{id}")
        @Produces("application/json")
        @org.apache.cxf.jaxrs.model.wadl.Description("Returns the operational details of a given entity in JSON")
        public org.uddi.api_v3.OperationalInfo getOpInfoJSON(@PathParam("id") String id) throws WebApplicationException {
                return getOpInfoDetail(id);
        }

        /**
         * Returns the operational details of a given entity in XML
         *
         * @param id
         * @return
         * @throws WebApplicationException
         */
        @GET
        @Path("/XML/opInfo/{id}")
        @Produces("application/xml")
        @org.apache.cxf.jaxrs.model.wadl.Description("Returns the operational details of a given entity in XML")
        public org.uddi.api_v3.OperationalInfo getOpInfoXML(@PathParam("id") String id) throws WebApplicationException {
                return getOpInfoDetail(id);
        }

        private OperationalInfo getOpInfoDetail(String id) {
                GetOperationalInfo req = new GetOperationalInfo();
                req.getEntityKey().add(id);
                try {
                        OperationalInfos operationalInfo = inquiry.getOperationalInfo(req);
                        return operationalInfo.getOperationalInfo().get(0);
                } catch (DispositionReportFaultMessage ex) {
                        HandleException(ex);
                }
                return null;
        }

        /**
         * Returns the binding details of a given entity in JSON
         *
         * @param id
         * @return
         * @throws WebApplicationException
         */
        @GET
        @Path("/JSON/bindingKey/{id}")
        @Produces("application/json")
        @org.apache.cxf.jaxrs.model.wadl.Description("Returns the binding details of a given entity in JSON")
        public org.uddi.api_v3.BindingTemplate getBindingDetailJSON(@PathParam("id") String id) throws WebApplicationException {
                return getBindingDetail(id);
        }

        /**
         * Returns the binding details of a given entity in XML
         *
         * @param id
         * @return
         * @throws WebApplicationException
         */
        @GET
        @Path("/XML/bindingKey/{id}")
        @Produces("application/xml")
        @org.apache.cxf.jaxrs.model.wadl.Description("Returns the binding details of a given entity in XML")
        public org.uddi.api_v3.BindingTemplate getBindingDetailXML(@PathParam("id") String id) throws WebApplicationException {
                return getBindingDetail(id);
        }

        private BindingTemplate getBindingDetail(String id) {
                GetBindingDetail req = new GetBindingDetail();
                req.getBindingKey().add(id);
                try {
                        BindingDetail bindingDetail = inquiry.getBindingDetail(req);
                        return bindingDetail.getBindingTemplate().get(0);
                } catch (DispositionReportFaultMessage ex) {
                        HandleException(ex);
                }
                return null;
        }

        /**
         * Returns the binding details of a given entity in JSON
         *
         * @param id
         * @return
         * @throws WebApplicationException
         */
        @GET
        @Path("/JSON/endpointsByService/{id}")
        @Produces("application/json")
        @org.apache.cxf.jaxrs.model.wadl.Description("Returns the service access points of a given service in JSON")
        public UriContainer geEndpointsByServiceJSON(@PathParam("id") String id) throws WebApplicationException {
                return getEndpointsByService(id);
        }

        /**
         * Returns the binding details of a given entity in XML
         *
         * @param id
         * @return
         * @throws WebApplicationException
         */
        @GET
        @Path("/XML/endpointsByService/{id}")
        @Produces("application/xml")
        @org.apache.cxf.jaxrs.model.wadl.Description("Returns the service access points of a given service in XML")
        public UriContainer getEndpointsByServiceXML(@PathParam("id") String id) throws WebApplicationException {
                return getEndpointsByService(id);
        }

        @GET
        @Path("/XML/businessList")
        @Produces("application/xml")
        @org.apache.cxf.jaxrs.model.wadl.Description("Returns the business keys of the first 100 registered businesses in XML")
        public KeyBag getBusinessListXML() throws WebApplicationException {
                return getBusinessListData();
        }

        @GET
        @Path("/JSON/businessList")
        @Produces("application/json")
        @org.apache.cxf.jaxrs.model.wadl.Description("Returns the business keys of the first 100 registered businesses in JSON")
        public KeyBag getBusinessListJSON() throws WebApplicationException {
                return getBusinessListData();
        }

        @GET
        @Path("/XML/serviceList")
        @Produces("application/xml")
        @org.apache.cxf.jaxrs.model.wadl.Description("Returns the Service keys of the first 100 registered services in XML")
        public KeyBag getServiceListXML() throws WebApplicationException {
                return getServiceListData();
        }

        @GET
        @Path("/JSON/serviceList")
        @Produces("application/json")
        @org.apache.cxf.jaxrs.model.wadl.Description("Returns the Service keys of the first 100 registered services in JSON")
        public KeyBag getServiceListJSON() throws WebApplicationException {
                return getServiceListData();
        }

        @GET
        @Path("/XML/tModelList")
        @Produces("application/xml")
        @org.apache.cxf.jaxrs.model.wadl.Description("Returns the tModel keys of the first 100 registered services in XML")
        public KeyBag getTModelListXML() throws WebApplicationException {
                return getTmodelListData();
        }

        @GET
        @Path("/JSON/tModelList")
        @Produces("application/json")
        @org.apache.cxf.jaxrs.model.wadl.Description("Returns the tModel keys of the first 100 registered services in JSON")
        public KeyBag getTModelListJSON() throws WebApplicationException {
                return getTmodelListData();
        }

        private UriContainer getEndpointsByService(String id) throws WebApplicationException {
                UriContainer c = new UriContainer();
                List<String> ret = new ArrayList<String>();
                GetServiceDetail fs = new GetServiceDetail();

                fs.getServiceKey().add(id);
                try {
                        ServiceDetail serviceDetail = inquiry.getServiceDetail(fs);
                        if (serviceDetail == null || serviceDetail.getBusinessService().isEmpty()) {
                                throw new WebApplicationException(400);
                        } else {
                                List<String> endpoints = GetEndpoints(serviceDetail, null);
                                ret.addAll(endpoints);
                        }
                } catch (DispositionReportFaultMessage ex) {
                        HandleException(ex);
                }
                c.setUriList(ret);
                return c;
        }

        private List<String> GetEndpoints(ServiceDetail serviceDetail, String authInfo) throws DispositionReportFaultMessage {
                List<String> items = new ArrayList<String>();
                if (serviceDetail == null) {
                        return items;
                }
                for (int i = 0; i < serviceDetail.getBusinessService().size(); i++) {
                        if (serviceDetail.getBusinessService().get(i).getBindingTemplates() != null) {
                                for (int k = 0; k < serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().size(); k++) {
                                        items.addAll(ParseBinding(serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k), authInfo));
                                }
                        }
                }
                return items;
        }

        private List<String> GetBindingInfo(String value, String cred) throws DispositionReportFaultMessage {
                List<String> items = new ArrayList<String>();
                if (value == null) {
                        return items;
                }
                GetBindingDetail b = new GetBindingDetail();
                b.setAuthInfo(cred);
                b.getBindingKey().add(value);
                BindingDetail bindingDetail = inquiry.getBindingDetail(b);
                for (int i = 0; i < bindingDetail.getBindingTemplate().size(); i++) {
                        items.addAll(ParseBinding(bindingDetail.getBindingTemplate().get(i), cred));
                }
                return items;
        }

        private List<String> FetchWSDL(String value) {
                List<String> items = new ArrayList<String>();

                if (value.startsWith("http://") || value.startsWith("https://")) {
                        //here, we need an HTTP Get for WSDLs
                        org.apache.juddi.v3.client.mapping.ReadWSDL r = new ReadWSDL();
                        r.setIgnoreSSLErrors(true);
                        try {
                                Definition wsdlDefinition = r.readWSDL(new URL(value));
                                Properties properties = new Properties();


                                properties.put("keyDomain", "domain");
                                properties.put("businessName", "biz");
                                properties.put("serverName", "localhost");
                                properties.put("serverPort", "80");

                                WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(null, new URLLocalizerDefaultImpl(), properties);
                                BusinessServices businessServices = wsdl2UDDI.createBusinessServices(wsdlDefinition);
                                for (int i = 0; i < businessServices.getBusinessService().size(); i++) {
                                        if (businessServices.getBusinessService().get(i).getBindingTemplates() != null) {
                                                for (int k = 0; k < businessServices.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().size(); k++) {
                                                        items.addAll(ParseBinding(businessServices.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k), null));
                                                }
                                        }
                                }
                        } catch (Exception ex) {
                        }

                }
                return items;
        }

        private List<String> ParseBinding(BindingTemplate get, String authInfo) throws DispositionReportFaultMessage {
                List<String> items = new ArrayList<String>();
                if (get == null || get.getAccessPoint() == null) {
                        return items;
                }
                if (get.getHostingRedirector() != null) {
                        //hosting Redirector is the same as "reference this other binding template". It's actually deprecated so 
                        //don't expect to see this too often
                        items.addAll(GetBindingInfo(get.getHostingRedirector().getBindingKey(), authInfo));
                }
                if (get.getAccessPoint() != null) {
                        String usetype = get.getAccessPoint().getUseType();
                        if (usetype == null) {

                                //this is unexpected, usetype is a required field
                                items.add((get.getAccessPoint().getValue()));

                        } else if (usetype.equalsIgnoreCase(AccessPointType.BINDING_TEMPLATE.toString())) {
                                //referencing another binding template
                                items.addAll(GetBindingInfo(get.getAccessPoint().getValue(), authInfo));
                        } else if (usetype.equalsIgnoreCase(AccessPointType.HOSTING_REDIRECTOR.toString())) {
                                //this one is a bit strange. the value should be a binding template

                                items.addAll(GetBindingInfo(get.getAccessPoint().getValue(), authInfo));

                        } else if (usetype.equalsIgnoreCase(AccessPointType.WSDL_DEPLOYMENT.toString())) {
                                //fetch wsdl and parse
                                items.addAll(FetchWSDL(get.getAccessPoint().getValue()));
                        } else if (usetype.equalsIgnoreCase(AccessPointType.END_POINT.toString())) {

                                items.add((get.getAccessPoint().getValue()));

                        } else {

                                //treat it has an extension or whatever
                                items.add((get.getAccessPoint().getValue()));

                        }

                }
                return items;
        }

        private static void HandleException(Exception ex) throws WebApplicationException {
                if (ex == null) {
                        throw new WebApplicationException(500);
                }
                log.error(ex.getMessage());
                log.debug(ex);
                if (ex instanceof DispositionReportFaultMessage) {
                        DispositionReportFaultMessage dr = (DispositionReportFaultMessage) ex;
                        if (dr.getFaultInfo() == null) {
                                throw new WebApplicationException(500);
                        }
                        if (dr.getFaultInfo().countainsErrorCode(UDDIErrorHelper.lookupErrCode(UDDIErrorHelper.E_AUTH_TOKEN_EXPIRED))) {
                                throw new WebApplicationException(ex, 401);
                        }
                        if (dr.getFaultInfo().countainsErrorCode(UDDIErrorHelper.lookupErrCode(UDDIErrorHelper.E_AUTH_TOKEN_REQUIRED))) {
                                throw new WebApplicationException(ex, 401);
                        }
                        if (dr.getFaultInfo().countainsErrorCode(UDDIErrorHelper.lookupErrCode(UDDIErrorHelper.E_FATAL_ERROR))) {
                                throw new WebApplicationException(ex, 500);
                        }
                }
                throw new WebApplicationException(ex, 400);
        }
        private final int MAX_ROWS = 100;

        private KeyBag getBusinessListData() {
                FindBusiness fb = new FindBusiness();
                fb.getName().add(new Name(UDDIConstants.WILDCARD, null));
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                fb.setMaxRows(MAX_ROWS);
                BusinessList findBusiness = null;
                try {
                        findBusiness = inquiry.findBusiness(fb);
                } catch (Exception ex) {
                        HandleException(ex);
                }
                KeyBag kb = new KeyBag();
                if (findBusiness != null && findBusiness.getBusinessInfos() != null) {
                        for (int i = 0; i < findBusiness.getBusinessInfos().getBusinessInfo().size(); i++) {
                                kb.getBusinessKey().add(findBusiness.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey());
                        }
                }
                return kb;
        }

        private KeyBag getServiceListData() {
                FindService fb = new FindService();
                fb.getName().add(new Name(UDDIConstants.WILDCARD, null));
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                fb.setMaxRows(MAX_ROWS);
                ServiceList findBusiness = null;
                try {
                        findBusiness = inquiry.findService(fb);
                } catch (Exception ex) {
                        HandleException(ex);
                }
                KeyBag kb = new KeyBag();
                if (findBusiness != null && findBusiness.getServiceInfos() != null) {
                        for (int i = 0; i < findBusiness.getServiceInfos().getServiceInfo().size(); i++) {
                                kb.getServiceKey().add(findBusiness.getServiceInfos().getServiceInfo().get(i).getServiceKey());
                        }
                }
                return kb;
        }

        private KeyBag getTmodelListData() {
                FindTModel fb = new FindTModel();
                fb.setName(new Name(UDDIConstants.WILDCARD, null));
                fb.setFindQualifiers(new FindQualifiers());
                fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                fb.setMaxRows(MAX_ROWS);
                TModelList findBusiness = null;
                try {
                        findBusiness = inquiry.findTModel(fb);
                } catch (Exception ex) {
                        HandleException(ex);
                }
                KeyBag kb = new KeyBag();
                if (findBusiness != null && findBusiness.getTModelInfos() != null) {
                        for (int i = 0; i < findBusiness.getTModelInfos().getTModelInfo().size(); i++) {
                                kb.getTModelKey().add(findBusiness.getTModelInfos().getTModelInfo().get(i).getTModelKey());
                        }
                }
                return kb;
        }
}
