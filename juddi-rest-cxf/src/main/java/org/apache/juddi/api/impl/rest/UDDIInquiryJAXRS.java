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

import java.net.URI;
import java.net.URISyntaxException;
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
import org.apache.juddi.v3.client.mapping.ReadWSDL;
import org.apache.juddi.v3.client.mapping.URLLocalizerDefaultImpl;
import org.apache.juddi.v3.client.mapping.WSDL2UDDI;
import org.apache.juddi.v3.error.UDDIErrorHelper;
import org.uddi.api_v3.*;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * UDDI Inquiry functions via a JAX-RS REST API. It's basically a wrapper for
 * the REST fans to access UDDI from a URL pattern This class will ONLY deploy
 * using the Apache CXF WS stack for REST (JAX-RS)
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
@Path("/")
@Produces("application/xml")
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
    @Path("/JSON/businessDetail/{id}")
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
    @Path("/XML/businessDetail/{id}")
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
    @Path("/XML/tModelDetail/{id}")
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
    @Path("/JSON/tModelDetail/{id}")
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
    @Path("/XML/serviceDetail/{id}")
    @Produces("application/json")
    @org.apache.cxf.jaxrs.model.wadl.Description("Returns the details of a service entity in JSON")
    public org.uddi.api_v3.BusinessService getServiceDetailJSON(@PathParam("id") String id) throws WebApplicationException {
        return getServiceDetail(id);
    }

    /**
     * Returns the details of a service entity in XML
     *
     * @param id
     * @return
     * @throws WebApplicationException
     */
    @GET
    @Path("/XML/serviceDetail/{id}")
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
    @Path("/XML/opInfo/{id}")
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
    @Path("/XML/bindingDetail/{id}")
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
    @Path("/XML/bindingDetail/{id}")
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
    @Path("/XML/endpointsByService/{id}")
    @Produces("application/json")
    @org.apache.cxf.jaxrs.model.wadl.Description("Returns the binding details of a given entity in JSON")
    public List<URI> geEndpointsByServiceJSON(@PathParam("id") String id) throws WebApplicationException {
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
    @org.apache.cxf.jaxrs.model.wadl.Description("Returns the binding details of a given entity in XML")
    public List<URI> getEndpointsByServiceXML(@PathParam("id") String id) throws WebApplicationException {
        return getEndpointsByService(id);
    }
    
    /**
     * 6.5 HTTP GET Services for UDDI Data Structures
A node may offer an HTTP GET service for access to the XML representations of UDDI data structures. If a node offers this service, the URLs should be in a format that is predictable and uses the entity key as a URL parameter.

The RECOMMENDED syntax for the URLs for such a service is as follows:

If a UDDI node’s base URI is http://uddi.example.org/mybase, then the URI http://uddi.example.org/mybase?<entity>Key=uddiKey would retrieve the XML for the data structure whose type is <entity> and whose key is uddiKey.  For example, the XML representation of a tModel whose key is "uddi:tempuri.com:fish:interface" can be retrieved by using the URL http://uddi.example.org/mybase?tModelKey=uddi:tempuri.com:fish:interface.

In the case of businessEntities, the node MAY add these URIs to the businessEntity’s discoveryURLs structure, though this is NOT RECOMMENDED behavior as it complicates the use of digital signatures.


 
     * @param id
     * @return
     * @throws WebApplicationException 
     */
    @GET
    @Path("/base?{entity}Key={key}")
    @Produces("application/xml")
     @org.apache.cxf.jaxrs.model.wadl.Description("Returns the selected UDDI entity as XML per section 6.5 of the UDDIv3 specification. Use businessKey, tmodelKey, bindingKey or serviceKey ")
    public Object getEntityAsXML(@PathParam("entity") String entity,@PathParam("key") String key) throws WebApplicationException {
        if (entity.equalsIgnoreCase("business"))
                return getBusinessDetailXML(key);
        if (entity.equalsIgnoreCase("tmodel"))
                return getTModelDetailXML(key);
        if (entity.equalsIgnoreCase("binding"))
                return getBindingDetailXML(key);
        if (entity.equalsIgnoreCase("service"))
                return getServiceDetailXML(key);
        throw new WebApplicationException(400);
    }
    
    private List<URI> getEndpointsByService(String id) throws WebApplicationException {
        List<URI> ret = new ArrayList<URI>();
        GetServiceDetail fs = new GetServiceDetail();
    
        fs.getServiceKey().add(id);
        try {
            ServiceDetail serviceDetail = inquiry.getServiceDetail(fs);
            if (serviceDetail == null || serviceDetail.getBusinessService().isEmpty()) {
                throw new WebApplicationException(400);
            } else {
                List<URI> endpoints = GetEndpoints(serviceDetail, null);
                ret.addAll(endpoints);
            }
        } catch (DispositionReportFaultMessage ex) {
            HandleException(ex);
        }
        return ret;
    }
    
    

    private List<URI> GetEndpoints(ServiceDetail serviceDetail, String authInfo) throws DispositionReportFaultMessage {
        List<URI> items = new ArrayList<URI>();
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

    private List<URI> GetBindingInfo(String value, String cred) throws DispositionReportFaultMessage {
        List<URI> items = new ArrayList<URI>();
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

    private List<URI> FetchWSDL(String value) {
        List<URI> items = new ArrayList<URI>();

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

    private List<URI> ParseBinding(BindingTemplate get, String authInfo) throws DispositionReportFaultMessage {
        List<URI> items = new ArrayList<URI>();
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
                try {
                    //this is unexpected, usetype is a required field
                    items.add(new URI(get.getAccessPoint().getValue()));
                } catch (URISyntaxException ex) {
                    log.warn(ex);
                }
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
                try {
                    items.add(new URI(get.getAccessPoint().getValue()));
                } catch (URISyntaxException ex) {
                    log.warn(ex);
                }
            } else {
                try {
                    //treat it has an extension or whatever
                    items.add(new URI(get.getAccessPoint().getValue()));
                } catch (URISyntaxException ex) {
                    log.warn(ex);
                }
            }

        }
        return items;
    }

    private static void HandleException(DispositionReportFaultMessage ex) throws WebApplicationException {
        if (ex == null) {
            throw new WebApplicationException(500);
        }
        log.error(ex);
        if (ex.getFaultInfo() == null) {
            throw new WebApplicationException(500);
        }
        if (ex.getFaultInfo().countainsErrorCode(UDDIErrorHelper.lookupErrCode(UDDIErrorHelper.E_AUTH_TOKEN_EXPIRED))) {
            throw new WebApplicationException(ex, 401);
        }
        if (ex.getFaultInfo().countainsErrorCode(UDDIErrorHelper.lookupErrCode(UDDIErrorHelper.E_AUTH_TOKEN_REQUIRED))) {
            throw new WebApplicationException(ex, 401);
        }
        if (ex.getFaultInfo().countainsErrorCode(UDDIErrorHelper.lookupErrCode(UDDIErrorHelper.E_FATAL_ERROR))) {
            throw new WebApplicationException(ex, 500);
        }
        throw new WebApplicationException(ex, 400);
    }
}
