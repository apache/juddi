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


package org.apache.juddi.v3_service;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.apache.juddi.api_v3.DeletePublisher;
import org.apache.juddi.api_v3.GetAllPublisherDetail;
import org.apache.juddi.api_v3.GetPublisherDetail;
import org.apache.juddi.api_v3.PublisherDetail;
import org.apache.juddi.api_v3.SavePublisher;
import org.uddi.api_v3.DeleteTModel;
import org.uddi.v3_service.DispositionReportFaultMessage;


/**
 * This portType defines all of the jUDDI publisher operations.
 * 
 */
@WebService(name = "JUDDI_Api_PortType", targetNamespace = "urn:juddi-apache-org:api_v3_portType")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    org.uddi.api_v3.ObjectFactory.class,
    org.w3._2000._09.xmldsig_.ObjectFactory.class,
    org.apache.juddi.api_v3.ObjectFactory.class
})
public interface JUDDIApiPortType extends Remote{

    /**
     * 
     * @param body
     * @throws DispositionReportFaultMessage, RemoteException
     */
    @WebMethod(operationName = "save_publisher", action = "save_publisher")
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    public PublisherDetail savePublisher(
        @WebParam(name = "save_publisher", targetNamespace = "urn:juddi-apache-org:api_v3", partName = "body")
        SavePublisher body)
        throws DispositionReportFaultMessage, RemoteException
    ;

    /**
     * 
     * @param body
     * @throws DispositionReportFaultMessage, RemoteException
     */
    @WebMethod(operationName = "delete_publisher", action = "delete_publisher")
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    public void deletePublisher(
        @WebParam(name = "delete_publisher", targetNamespace = "urn:juddi-apache-org:api_v3", partName = "body")
        DeletePublisher body)
        throws DispositionReportFaultMessage, RemoteException
    ;
    
    /**
     * 
     * @param body
     * @return
     *     returns org.apache.juddi.api_v3.PublisherDetail
     * @throws DispositionReportFaultMessage, RemoteException
     */
    @WebMethod(operationName = "get_publisherDetail", action = "get_publisherDetail")
    @WebResult(name = "publisherDetail", targetNamespace = "urn:juddi-apache-org:api_v3", partName = "body")
    public PublisherDetail getPublisherDetail(
        @WebParam(name = "get_publisherDetail", targetNamespace = "urn:juddi-apache-org:api_v3", partName = "body")
        GetPublisherDetail body)
        throws DispositionReportFaultMessage, RemoteException
    ;
    
    /**
     * 
     * @param body
     * @return
     *     returns org.apache.juddi.api_v3.PublisherDetail
     * @throws DispositionReportFaultMessage, RemoteException
     */
    @WebMethod(operationName = "get_allPublisherDetail", action = "get_allPublisherDetail")
    @WebResult(name = "publisherDetail", targetNamespace = "urn:juddi-apache-org:api_v3", partName = "body")
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    public PublisherDetail getAllPublisherDetail(
        @WebParam(name = "get_allPublisherDetail", targetNamespace = "urn:juddi-apache-org:api_v3", partName = "body")
        GetAllPublisherDetail body)
        throws DispositionReportFaultMessage, RemoteException
    ;


    /**
     * 
     * @param body
     * @throws DispositionReportFaultMessage, RemoteException
     */
    @WebMethod(operationName = "adminDelete_tmodel", action = "adminDelete_tmodel")
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    public void adminDeleteTModel(
        @WebParam(name = "adminDelete_tmodel", targetNamespace = "urn:juddi-apache-org:api_v3", partName = "body")
        DeleteTModel body)
        throws DispositionReportFaultMessage, RemoteException
    ;
    
}
