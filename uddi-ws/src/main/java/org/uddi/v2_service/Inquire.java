/*
 * Copyright 2001-2008 The Apache Software Foundation.
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
package org.uddi.v2_service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
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
import org.uddi.api_v2.ObjectFactory;
import org.uddi.api_v2.RelatedBusinessesList;
import org.uddi.api_v2.ServiceDetail;
import org.uddi.api_v2.ServiceList;
import org.uddi.api_v2.TModelDetail;
import org.uddi.api_v2.TModelList;


/**
 * 
 * 			This portType defines all of the UDDI inquiry operations.
 * 	  
 * 
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebService(name = "Inquire", targetNamespace = "urn:uddi-org:inquiry_v2")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface Inquire {


    /**
     * 
     * @param body
     * @return
     *     returns org.uddi.api_v2.BindingDetail
     * @throws DispositionReport
     */
    @WebMethod(operationName = "find_binding", action = "find_binding")
    @WebResult(name = "bindingDetail", targetNamespace = "urn:uddi-org:api_v2", partName = "body")
    public BindingDetail findBinding(
        @WebParam(name = "find_binding", targetNamespace = "urn:uddi-org:api_v2", partName = "body")
        FindBinding body)
        throws DispositionReport
    ;

    /**
     * 
     * @param body
     * @return
     *     returns org.uddi.api_v2.BusinessList
     * @throws DispositionReport
     */
    @WebMethod(operationName = "find_business", action = "find_business")
    @WebResult(name = "businessList", targetNamespace = "urn:uddi-org:api_v2", partName = "body")
    public BusinessList findBusiness(
        @WebParam(name = "find_business", targetNamespace = "urn:uddi-org:api_v2", partName = "body")
        FindBusiness body)
        throws DispositionReport
    ;

    /**
     * 
     * @param body
     * @return
     *     returns org.uddi.api_v2.RelatedBusinessesList
     * @throws DispositionReport
     */
    @WebMethod(operationName = "find_relatedBusinesses", action = "find_relatedBusinesses")
    @WebResult(name = "relatedBusinessesList", targetNamespace = "urn:uddi-org:api_v2", partName = "body")
    public RelatedBusinessesList findRelatedBusinesses(
        @WebParam(name = "find_relatedBusinesses", targetNamespace = "urn:uddi-org:api_v2", partName = "body")
        FindRelatedBusinesses body)
        throws DispositionReport
    ;

    /**
     * 
     * @param body
     * @return
     *     returns org.uddi.api_v2.ServiceList
     * @throws DispositionReport
     */
    @WebMethod(operationName = "find_service", action = "find_service")
    @WebResult(name = "serviceList", targetNamespace = "urn:uddi-org:api_v2", partName = "body")
    public ServiceList findService(
        @WebParam(name = "find_service", targetNamespace = "urn:uddi-org:api_v2", partName = "body")
        FindService body)
        throws DispositionReport
    ;

    /**
     * 
     * @param body
     * @return
     *     returns org.uddi.api_v2.TModelList
     * @throws DispositionReport
     */
    @WebMethod(operationName = "find_tModel", action = "find_tModel")
    @WebResult(name = "tModelList", targetNamespace = "urn:uddi-org:api_v2", partName = "body")
    public TModelList findTModel(
        @WebParam(name = "find_tModel", targetNamespace = "urn:uddi-org:api_v2", partName = "body")
        FindTModel body)
        throws DispositionReport
    ;

    /**
     * 
     * @param body
     * @return
     *     returns org.uddi.api_v2.BindingDetail
     * @throws DispositionReport
     */
    @WebMethod(operationName = "get_bindingDetail", action = "get_bindingDetail")
    @WebResult(name = "bindingDetail", targetNamespace = "urn:uddi-org:api_v2", partName = "body")
    public BindingDetail getBindingDetail(
        @WebParam(name = "get_bindingDetail", targetNamespace = "urn:uddi-org:api_v2", partName = "body")
        GetBindingDetail body)
        throws DispositionReport
    ;

    /**
     * 
     * @param body
     * @return
     *     returns org.uddi.api_v2.BusinessDetail
     * @throws DispositionReport
     */
    @WebMethod(operationName = "get_businessDetail", action = "get_businessDetail")
    @WebResult(name = "businessDetail", targetNamespace = "urn:uddi-org:api_v2", partName = "body")
    public BusinessDetail getBusinessDetail(
        @WebParam(name = "get_businessDetail", targetNamespace = "urn:uddi-org:api_v2", partName = "body")
        GetBusinessDetail body)
        throws DispositionReport
    ;

    /**
     * 
     * @param body
     * @return
     *     returns org.uddi.api_v2.BusinessDetailExt
     * @throws DispositionReport
     */
    @WebMethod(operationName = "get_businessDetailExt", action = "get_businessDetailExt")
    @WebResult(name = "businessDetailExt", targetNamespace = "urn:uddi-org:api_v2", partName = "body")
    public BusinessDetailExt getBusinessDetailExt(
        @WebParam(name = "get_businessDetailExt", targetNamespace = "urn:uddi-org:api_v2", partName = "body")
        GetBusinessDetailExt body)
        throws DispositionReport
    ;

    /**
     * 
     * @param body
     * @return
     *     returns org.uddi.api_v2.ServiceDetail
     * @throws DispositionReport
     */
    @WebMethod(operationName = "get_serviceDetail", action = "get_serviceDetail")
    @WebResult(name = "serviceDetail", targetNamespace = "urn:uddi-org:api_v2", partName = "body")
    public ServiceDetail getServiceDetail(
        @WebParam(name = "get_serviceDetail", targetNamespace = "urn:uddi-org:api_v2", partName = "body")
        GetServiceDetail body)
        throws DispositionReport
    ;

    /**
     * 
     * @param body
     * @return
     *     returns org.uddi.api_v2.TModelDetail
     * @throws DispositionReport
     */
    @WebMethod(operationName = "get_tModelDetail", action = "get_tModelDetail")
    @WebResult(name = "tModelDetail", targetNamespace = "urn:uddi-org:api_v2", partName = "body")
    public TModelDetail getTModelDetail(
        @WebParam(name = "get_tModelDetail", targetNamespace = "urn:uddi-org:api_v2", partName = "body")
        GetTModelDetail body)
        throws DispositionReport
    ;

}