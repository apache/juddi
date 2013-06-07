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

import javax.ws.rs.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api.impl.UDDIInquiryImpl;
import org.apache.juddi.v3.error.UDDIErrorHelper;
import org.uddi.api_v3.*;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * UDDI Inquiry functions via a JAX-RS REST API. It's basically a wrapper for
 * the REST fans to access UDDI from a URL pattern
 *
 * @author Alex O'Ree
 */
@Path("/")
@Produces("application/xml")
public class UDDIInquiryJAXRS {

    private static org.apache.juddi.api.impl.UDDIInquiryImpl inquiry = new UDDIInquiryImpl();
    private static Log log = LogFactory.getLog(UDDIInquiryJAXRS.class);

    /**
     *
     * @param id
     * @return
     */
    @GET
    @Path("/JSON/businessDetail/{id}")
    @Produces("application/json")
    public org.uddi.api_v3.BusinessEntity getBusinessDetailJSON(@PathParam("id") String id) throws WebApplicationException {
        return getBusinessDetail(id);
    }

    @GET
    @Path("/XML/businessDetail/{id}")
    @Produces("application/xml")
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

    @GET
    @Path("/XML/tModelDetail/{id}")
    @Produces("application/xml")
    public org.uddi.api_v3.TModel getTModelDetailXML(@PathParam("id") String id) throws WebApplicationException {
        return getTModelDetail(id);
    }

    @GET
    @Path("/JSON/tModelDetail/{id}")
    @Produces("application/json")
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

    @GET
    @Path("/XML/serviceDetail/{id}")
    @Produces("application/json")
    public org.uddi.api_v3.BusinessService getServiceDetailJSON(@PathParam("id") String id) throws WebApplicationException {
        return getServiceDetail(id);
    }

    @GET
    @Path("/XML/serviceDetail/{id}")
    @Produces("application/xml")
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
